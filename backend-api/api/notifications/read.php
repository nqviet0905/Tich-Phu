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

$body = json_decode(
    file_get_contents('php://input'),
    true
);

if (!is_array($body)) {
    errorResponse(
        'Dữ liệu JSON không hợp lệ.',
        400
    );
}

$id = (int) ($body['id'] ?? 0);

if ($id <= 0) {
    errorResponse(
        'ID thông báo không hợp lệ.',
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

    $stmt = $db->prepare("
        SELECT id
        FROM notifications
        WHERE id = :id
          AND user_id = :user_id
        LIMIT 1
    ");

    $stmt->execute([
        'id' => $id,
        'user_id' => $currentUserId
    ]);

    if (!$stmt->fetch()) {
        errorResponse(
            'Không tìm thấy thông báo.',
            404
        );
    }

    $stmt = $db->prepare("
        UPDATE notifications
        SET is_read = TRUE
        WHERE id = :id
          AND user_id = :user_id
    ");

    $stmt->execute([
        'id' => $id,
        'user_id' => $currentUserId
    ]);

    successResponse(
        'Đã đánh dấu thông báo là đã đọc.',
        [
            'id' => $id,
            'is_read' => true
        ]
    );

} catch (PDOException $e) {

    errorResponse(
        'Không thể cập nhật thông báo.',
        500
    );
}