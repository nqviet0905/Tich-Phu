<?php

require_once __DIR__ . '/../../middleware/cors.php';
require_once __DIR__ . '/../../middleware/auth.php';
require_once __DIR__ . '/../../config/database.php';
require_once __DIR__ . '/../../helpers/response.php';

if ($_SERVER['REQUEST_METHOD'] !== 'PUT') {
    errorResponse('Phương thức không được hỗ trợ.', 405);
}

$authData = authenticateUser();
$user = $authData['user'] ?? null;

if (!$user) {
    errorResponse(
        'Tài khoản chưa được đồng bộ với hệ thống.',
        403
    );
}

$currentUserId = (int) $user['id'];

$database = new Database();
$db = $database->connect();

if (!$db) {
    errorResponse(
        'Không thể kết nối cơ sở dữ liệu.',
        500
    );
}

try {

    $stmt = $db->prepare("
        UPDATE notifications
        SET is_read = TRUE
        WHERE user_id = :user_id
          AND is_read = FALSE
    ");

    $stmt->execute([
        'user_id' => $currentUserId
    ]);

    successResponse(
        'Đã đánh dấu tất cả thông báo là đã đọc.',
        [
            'updated_count' => $stmt->rowCount()
        ]
    );

} catch (PDOException $e) {

    errorResponse(
        'Không thể cập nhật thông báo.',
        500
    );
}