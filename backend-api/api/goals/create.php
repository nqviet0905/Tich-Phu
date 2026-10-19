<?php

require_once __DIR__ . '/../../middleware/cors.php';
require_once __DIR__ . '/../../middleware/auth.php';
require_once __DIR__ . '/../../config/database.php';
require_once __DIR__ . '/../../helpers/response.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    errorResponse('Phương thức không được hỗ trợ.', 405);
}

$authData = authenticateUser();
$user = $authData['user'];

if (!$user) {
    errorResponse('Tài khoản chưa được đồng bộ với hệ thống.', 403);
}

$currentUserId = (int) $user['id'];

$body = json_decode(
    file_get_contents('php://input'),
    true
);

if (!is_array($body)) {
    errorResponse('Dữ liệu JSON không hợp lệ.', 400);
}

$name = trim($body['name'] ?? '');
$description = trim($body['description'] ?? '');
$targetAmount = $body['target_amount'] ?? null;
$currentAmount = $body['current_amount'] ?? 0;
$startDate = trim($body['start_date'] ?? '');
$deadline = trim($body['deadline'] ?? '');

if ($name === '') {
    errorResponse('Tên mục tiêu không được để trống.', 400);
}

if (!is_numeric($targetAmount) || $targetAmount <= 0) {
    errorResponse('Số tiền mục tiêu phải lớn hơn 0.', 400);
}

if (!is_numeric($currentAmount) || $currentAmount < 0) {
    errorResponse('Số tiền hiện có không hợp lệ.', 400);
}

$start = DateTime::createFromFormat('Y-m-d', $startDate);
$end = DateTime::createFromFormat('Y-m-d', $deadline);

if (!$start || $start->format('Y-m-d') !== $startDate) {
    errorResponse('Ngày bắt đầu không hợp lệ.', 400);
}

if (!$end || $end->format('Y-m-d') !== $deadline) {
    errorResponse('Ngày kết thúc không hợp lệ.', 400);
}

if ($deadline < $startDate) {
    errorResponse('Ngày hoàn thành phải sau hoặc bằng ngày bắt đầu.', 422);
}

$targetAmount = (float) $targetAmount;
$currentAmount = (float) $currentAmount;

$status = $currentAmount >= $targetAmount
    ? 'completed'
    : 'active';

$database = new Database();
$db = $database->connect();

if (!$db) {
    errorResponse('Không thể kết nối cơ sở dữ liệu.', 500);
}

try {
    $stmt = $db->prepare("
        INSERT INTO goals (
            user_id,
            name,
            description,
            target_amount,
            current_amount,
            start_date,
            deadline,
            status
        )
        VALUES (
            :user_id,
            :name,
            :description,
            :target_amount,
            :current_amount,
            :start_date,
            :deadline,
            :status
        )
    ");

    $stmt->execute([
        'user_id' => $currentUserId,
        'name' => $name,
        'description' => $description !== '' ? $description : null,
        'target_amount' => $targetAmount,
        'current_amount' => $currentAmount,
        'start_date' => $startDate,
        'deadline' => $deadline,
        'status' => $status
    ]);

    $goalId = (int) $db->lastInsertId();

    $remainingAmount = max(
        0,
        $targetAmount - $currentAmount
    );

    $today = new DateTime();
    $deadlineDate = new DateTime($deadline);

    $monthsRemaining = 0;

    if ($deadlineDate >= $today) {
        $interval = $today->diff($deadlineDate);

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

    $progressPercentage = $targetAmount > 0
        ? ($currentAmount / $targetAmount) * 100
        : 0;

    successResponse(
        'Tạo mục tiêu tiết kiệm thành công.',
        [
            'id' => $goalId,
            'name' => $name,
            'target_amount' => $targetAmount,
            'current_amount' => $currentAmount,
            'remaining_amount' => $remainingAmount,
            'progress_percentage' => round($progressPercentage, 2),
            'months_remaining' => $monthsRemaining,
            'required_per_month' => round($requiredPerMonth, 2),
            'start_date' => $startDate,
            'deadline' => $deadline,
            'status' => $status
        ],
        201
    );

} catch (PDOException $e) {
    errorResponse(
        'Không thể tạo mục tiêu tiết kiệm.',
        500
    );
}