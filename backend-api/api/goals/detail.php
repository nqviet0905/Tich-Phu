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
    errorResponse('Tài khoản chưa được đồng bộ với hệ thống.', 403);
}

$currentUserId = (int) $user['id'];
$id = isset($_GET['id']) ? (int) $_GET['id'] : 0;

if ($id <= 0) {
    errorResponse('ID mục tiêu không hợp lệ.', 400);
}

$database = new Database();
$db = $database->connect();

if (!$db) {
    errorResponse('Không thể kết nối cơ sở dữ liệu.', 500);
}

try {
    $stmt = $db->prepare("
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
        WHERE id = :id
          AND user_id = :user_id
        LIMIT 1
    ");

    $stmt->execute([
        'id' => $id,
        'user_id' => $currentUserId
    ]);

    $goal = $stmt->fetch();

    if (!$goal) {
        errorResponse('Không tìm thấy mục tiêu.', 404);
    }

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

    $goal['target_amount'] = $targetAmount;
    $goal['current_amount'] = $currentAmount;
    $goal['remaining_amount'] = $remainingAmount;
    $goal['progress_percentage'] = round($progressPercentage, 2);
    $goal['months_remaining'] = $monthsRemaining;
    $goal['required_per_month'] = round($requiredPerMonth, 2);

    successResponse(
        'Lấy chi tiết mục tiêu thành công.',
        $goal
    );

} catch (PDOException $e) {
    errorResponse(
        'Không thể lấy chi tiết mục tiêu.',
        500
    );
}