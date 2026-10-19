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

$body = json_decode(file_get_contents('php://input'), true);

if (!is_array($body)) {
    errorResponse('Dữ liệu JSON không hợp lệ.', 400);
}

$id = (int) ($body['id'] ?? 0);
$amount = $body['amount'] ?? null;
$warningLevel = (int) ($body['warning_level'] ?? 80);

if ($id <= 0) {
    errorResponse('ID ngân sách không hợp lệ.', 400);
}

if (!is_numeric($amount) || $amount <= 0) {
    errorResponse('Ngân sách phải lớn hơn 0.', 400);
}

if ($warningLevel < 1 || $warningLevel > 100) {
    errorResponse('Mức cảnh báo phải từ 1 đến 100.', 400);
}

$database = new Database();
$db = $database->connect();

if (!$db) {
    errorResponse('Không thể kết nối cơ sở dữ liệu.', 500);
}

try {
    $stmt = $db->prepare("
        SELECT id
        FROM budgets
        WHERE id = :id
          AND user_id = :user_id
        LIMIT 1
    ");

    $stmt->execute([
        'id' => $id,
        'user_id' => $currentUserId
    ]);

    if (!$stmt->fetch()) {
        errorResponse('Không tìm thấy ngân sách.', 404);
    }

    $stmt = $db->prepare("
        UPDATE budgets
        SET
            amount = :amount,
            warning_level = :warning_level
        WHERE id = :id
          AND user_id = :user_id
    ");

    $stmt->execute([
        'amount' => $amount,
        'warning_level' => $warningLevel,
        'id' => $id,
        'user_id' => $currentUserId
    ]);

    successResponse(
        'Cập nhật ngân sách thành công.',
        [
            'id' => $id,
            'amount' => (float) $amount,
            'warning_level' => $warningLevel
        ]
    );

} catch (PDOException $e) {
    errorResponse('Không thể cập nhật ngân sách.', 500);
}