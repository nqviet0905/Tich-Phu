<?php

require_once __DIR__ . '/../../middleware/cors.php';
require_once __DIR__ . '/../../middleware/auth.php';
require_once __DIR__ . '/../../config/database.php';
require_once __DIR__ . '/../../helpers/response.php';

if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    errorResponse('Phương thức không được hỗ trợ.', 405);
}

$authData = authenticateUser();
$user = $authData['user'];

if (!$user) {
    errorResponse(
        'Tài khoản chưa được đồng bộ với hệ thống.',
        403
    );
}

$currentUserId = (int) $user['id'];

$status = $_GET['status'] ?? null;

if (
    $status !== null &&
    !in_array($status, ['active', 'completed', 'cancelled'], true)
) {
    errorResponse(
        'Trạng thái mục tiêu không hợp lệ.',
        400
    );
}

$database = new Database();
$db = $database->connect();

if (!$db) {
    errorResponse(
        'Không thể kết nối cơ sở dữ liệu.',
        500
    );
}

try {
    $sql = "
        SELECT
            id,
            name,
            description,
            target_amount,
            current_amount,
            start_date,
            deadline,
            status,
            created_at,
            updated_at
        FROM goals
        WHERE user_id = :user_id
    ";

    $params = [
        'user_id' => $currentUserId
    ];

    if ($status !== null) {
        $sql .= " AND status = :status";
        $params['status'] = $status;
    }

    $sql .= "
        ORDER BY
            CASE
                WHEN status = 'active' THEN 1
                WHEN status = 'completed' THEN 2
                ELSE 3
            END,
            deadline ASC,
            id DESC
    ";

    $stmt = $db->prepare($sql);
    $stmt->execute($params);

    $goals = $stmt->fetchAll();

    $items = [];

    foreach ($goals as $goal) {
        $targetAmount = (float) $goal['target_amount'];
        $currentAmount = (float) $goal['current_amount'];

        $remainingAmount = max(
            0,
            $targetAmount - $currentAmount
        );

        $progressPercentage = $targetAmount > 0
            ? ($currentAmount / $targetAmount) * 100
            : 0;

        $today = new DateTime();
        $deadline = new DateTime($goal['deadline']);

        $monthsRemaining = 0;

        if ($deadline >= $today) {
            $interval = $today->diff($deadline);

            $monthsRemaining =
                ($interval->y * 12)
                + $interval->m;

            if ($interval->d > 0) {
                $monthsRemaining++;
            }
        }

        $requiredPerMonth = $monthsRemaining > 0
            ? $remainingAmount / $monthsRemaining
            : $remainingAmount;

        $items[] = [
            'id' => (int) $goal['id'],
            'name' => $goal['name'],
            'description' => $goal['description'],

            'target_amount' => $targetAmount,
            'current_amount' => $currentAmount,
            'remaining_amount' => $remainingAmount,

            'progress_percentage' =>
                round($progressPercentage, 2),

            'months_remaining' => $monthsRemaining,

            'required_per_month' =>
                round($requiredPerMonth, 2),

            'start_date' => $goal['start_date'],
            'deadline' => $goal['deadline'],
            'status' => $goal['status'],

            'created_at' => $goal['created_at'],
            'updated_at' => $goal['updated_at']
        ];
    }

    successResponse(
        'Lấy danh sách mục tiêu thành công.',
        $items
    );

} catch (PDOException $e) {
    errorResponse(
        'Không thể lấy danh sách mục tiêu.',
        500
    );
}