<?php

require_once __DIR__ . '/../../middleware/cors.php';
require_once __DIR__ . '/../../middleware/auth.php';
require_once __DIR__ . '/../../config/database.php';
require_once __DIR__ . '/../../helpers/response.php';

if ($_SERVER['REQUEST_METHOD'] !== 'PUT') {
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

$id = (int) ($body['id'] ?? 0);
$name = trim($body['name'] ?? '');
$description = trim($body['description'] ?? '');
$targetAmount = $body['target_amount'] ?? null;
$startDate = trim($body['start_date'] ?? '');
$deadline = trim($body['deadline'] ?? '');
$status = trim($body['status'] ?? 'active');

if ($id <= 0) {
    errorResponse('ID mục tiêu không hợp lệ.', 400);
}

if ($name === '') {
    errorResponse('Tên mục tiêu không được để trống.', 400);
}

if (!is_numeric($targetAmount) || $targetAmount <= 0) {
    errorResponse('Số tiền mục tiêu phải lớn hơn 0.', 400);
}

if (!in_array($status, ['active', 'completed', 'cancelled'], true)) {
    errorResponse('Trạng thái mục tiêu không hợp lệ.', 400);
}

$start = DateTime::createFromFormat('Y-m-d', $startDate);
$end = DateTime::createFromFormat('Y-m-d', $deadline);

if (!$start || $start->format('Y-m-d') !== $startDate) {
    errorResponse('Ngày bắt đầu không hợp lệ.', 400);
}

if (!$end || $end->format('Y-m-d') !== $deadline) {
    errorResponse('Ngày hoàn thành không hợp lệ.', 400);
}

if ($deadline < $startDate) {
    errorResponse(
        'Ngày hoàn thành phải sau hoặc bằng ngày bắt đầu.',
        422
    );
}

$targetAmount = (float) $targetAmount;

$database = new Database();
$db = $database->connect();

if (!$db) {
    errorResponse('Không thể kết nối cơ sở dữ liệu.', 500);
}

try {

    $stmt = $db->prepare("
        SELECT
            id,
            current_amount,
            status
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

    $currentAmount = (float) $goal['current_amount'];

    // Nếu đã đạt mục tiêu thì tự completed
    if ($currentAmount >= $targetAmount) {
        $status = 'completed';
    }

    $stmt = $db->prepare("
        UPDATE goals
        SET
            name = :name,
            description = :description,
            target_amount = :target_amount,
            start_date = :start_date,
            deadline = :deadline,
            status = :status
        WHERE id = :id
          AND user_id = :user_id
    ");

    $stmt->execute([
        'name' => $name,
        'description' => $description !== '' ? $description : null,
        'target_amount' => $targetAmount,
        'start_date' => $startDate,
        'deadline' => $deadline,
        'status' => $status,
        'id' => $id,
        'user_id' => $currentUserId
    ]);

    $remainingAmount = max(
        0,
        $targetAmount - $currentAmount
    );

    $progressPercentage = $targetAmount > 0
        ? ($currentAmount / $targetAmount) * 100
        : 0;

    successResponse(
        'Cập nhật mục tiêu thành công.',
        [
            'id' => $id,
            'name' => $name,
            'target_amount' => $targetAmount,
            'current_amount' => $currentAmount,
            'remaining_amount' => $remainingAmount,
            'progress_percentage' => round($progressPercentage, 2),
            'status' => $status
        ]
    );

} catch (PDOException $e) {
    errorResponse(
        'Không thể cập nhật mục tiêu.',
        500
    );
}