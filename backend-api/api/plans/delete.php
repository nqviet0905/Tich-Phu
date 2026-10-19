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
    errorResponse('ID kế hoạch không hợp lệ.', 400);
}

$database = new Database();
$db = $database->connect();

if (!$db) {
    errorResponse('Không thể kết nối cơ sở dữ liệu.', 500);
}

try {
    $stmt = $db->prepare("
        UPDATE financial_plans
        SET status = 'cancelled'
        WHERE id = :id
          AND user_id = :user_id
          AND status != 'cancelled'
    ");

    $stmt->execute([
        'id' => $id,
        'user_id' => $currentUserId
    ]);

    if ($stmt->rowCount() === 0) {
        errorResponse(
            'Không tìm thấy kế hoạch hoặc kế hoạch đã được hủy.',
            404
        );
    }

    successResponse(
        'Hủy kế hoạch tài chính thành công.',
        [
            'id' => $id,
            'status' => 'cancelled'
        ]
    );

} catch (PDOException $e) {
    errorResponse('Không thể hủy kế hoạch.', 500);
}