<?php

require_once __DIR__ . '/../../middleware/cors.php';
require_once __DIR__ . '/../../middleware/auth.php';
require_once __DIR__ . '/../../config/database.php';
require_once __DIR__ . '/../../helpers/response.php';

if ($_SERVER['REQUEST_METHOD'] !== 'DELETE') {
    errorResponse(
        'Phương thức không được hỗ trợ.',
        405
    );
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

$fcmToken = trim($body['fcm_token'] ?? '');

if ($fcmToken === '') {
    errorResponse(
        'FCM token không được để trống.',
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
        FROM device_tokens
        WHERE user_id = :user_id
          AND fcm_token = :fcm_token
        LIMIT 1
    ");

    $stmt->execute([
        'user_id' => $currentUserId,
        'fcm_token' => $fcmToken
    ]);

    $token = $stmt->fetch();

    if (!$token) {
        errorResponse(
            'Không tìm thấy FCM token.',
            404
        );
    }

    // Soft delete token
    $stmt = $db->prepare("
        UPDATE device_tokens
        SET
            is_active = FALSE,
            updated_at = CURRENT_TIMESTAMP
        WHERE user_id = :user_id
          AND fcm_token = :fcm_token
    ");

    $stmt->execute([
        'user_id' => $currentUserId,
        'fcm_token' => $fcmToken
    ]);

    successResponse(
        'Đã vô hiệu hóa FCM token thành công.',
        [
            'token_id' => (int) $token['id'],
            'is_active' => false
        ]
    );

} catch (PDOException $e) {

    errorResponse(
        'Không thể vô hiệu hóa FCM token.',
        500
    );
}