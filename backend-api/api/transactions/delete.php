<?php

require_once __DIR__ . '/../../middleware/cors.php';
require_once __DIR__ . '/../../middleware/auth.php';
require_once __DIR__ . '/../../config/database.php';
require_once __DIR__ . '/../../helpers/response.php';

if ($_SERVER['REQUEST_METHOD'] !== 'DELETE') {
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
    errorResponse('ID giao dịch không hợp lệ.', 400);
}

$database = new Database();
$db = $database->connect();

if (!$db) {
    errorResponse('Không thể kết nối cơ sở dữ liệu.', 500);
}

try {
    $stmt = $db->prepare("
        SELECT id
        FROM transactions
        WHERE id = :id
          AND user_id = :user_id
        LIMIT 1
    ");

    $stmt->execute([
        'id' => $id,
        'user_id' => $currentUserId
    ]);

    if (!$stmt->fetch()) {
        errorResponse('Không tìm thấy giao dịch.', 404);
    }

    $stmt = $db->prepare("
        DELETE FROM transactions
        WHERE id = :id
          AND user_id = :user_id
    ");

    $stmt->execute([
        'id' => $id,
        'user_id' => $currentUserId
    ]);

    successResponse(
        'Xóa giao dịch thành công.',
        [
            'id' => $id
        ]
    );

} catch (PDOException $e) {
    errorResponse('Không thể xóa giao dịch.', 500);
}