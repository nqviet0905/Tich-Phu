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
$deviceName = trim($body['device_name'] ?? '');

if ($fcmToken === '') {
    errorResponse(
        'FCM token không được để trống.',
        400
    );
}

if (mb_strlen($fcmToken) > 500) {
    errorResponse(
        'FCM token không hợp lệ.',
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

    // Kiểm tra token đã tồn tại chưa
    $stmt = $db->prepare("
        SELECT id
        FROM device_tokens
        WHERE fcm_token = :fcm_token
        LIMIT 1
    ");

    $stmt->execute([
        'fcm_token' => $fcmToken
    ]);

    $existing = $stmt->fetch();

    if ($existing) {

        $stmt = $db->prepare("
            UPDATE device_tokens
            SET
                user_id = :user_id,
                device_name = :device_name,
                is_active = TRUE,
                updated_at = CURRENT_TIMESTAMP
            WHERE fcm_token = :fcm_token
        ");

        $stmt->execute([
            'user_id' => $currentUserId,
            'device_name' => $deviceName !== ''
                ? $deviceName
                : null,
            'fcm_token' => $fcmToken
        ]);

        successResponse(
            'Cập nhật FCM token thành công.',
            [
                'token_id' => (int) $existing['id'],
                'fcm_token' => $fcmToken,
                'device_name' => $deviceName,
                'is_active' => true
            ]
        );
    }

    $stmt = $db->prepare("
        INSERT INTO device_tokens (
            user_id,
            fcm_token,
            device_name,
            is_active
        )
        VALUES (
            :user_id,
            :fcm_token,
            :device_name,
            TRUE
        )
    ");

    $stmt->execute([
        'user_id' => $currentUserId,
        'fcm_token' => $fcmToken,
        'device_name' => $deviceName !== ''
            ? $deviceName
            : null
    ]);

    $tokenId = (int) $db->lastInsertId();

    successResponse(
        'Đăng ký FCM token thành công.',
        [
            'token_id' => $tokenId,
            'fcm_token' => $fcmToken,
            'device_name' => $deviceName,
            'is_active' => true
        ],
        201
    );

} catch (PDOException $e) {

    errorResponse(
        'Không thể đăng ký FCM token.',
        500
    );
}