<?php

require_once __DIR__ . '/../../middleware/cors.php';
require_once __DIR__ . '/../../middleware/auth.php';
require_once __DIR__ . '/../../config/database.php';
require_once __DIR__ . '/../../helpers/response.php';

if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
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
        SELECT
            id,
            title,
            message,
            type,
            reference_id,
            is_read,
            created_at
        FROM notifications
        WHERE user_id = :user_id
        ORDER BY created_at DESC
    ");

    $stmt->execute([
        'user_id' => $currentUserId
    ]);

    successResponse(
        'Lấy danh sách thông báo thành công.',
        $stmt->fetchAll(PDO::FETCH_ASSOC)
    );

} catch (PDOException $e) {
    errorResponse(
        'Không thể lấy danh sách thông báo.',
        500
    );
}