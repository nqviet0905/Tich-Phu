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

$id = isset($_GET['id'])
    ? (int) $_GET['id']
    : 0;

if ($id <= 0) {
    errorResponse(
        'ID danh mục không hợp lệ.',
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
        SELECT
            id,
            user_id,
            is_default
        FROM categories
        WHERE id = :id
        LIMIT 1
    ");

    $stmt->execute([
        'id' => $id
    ]);

    $category = $stmt->fetch();

    if (!$category) {
        errorResponse(
            'Không tìm thấy danh mục.',
            404
        );
    }

    if ((int) $category['is_default'] === 1) {
        errorResponse(
            'Không thể xóa danh mục mặc định.',
            403
        );
    }

    if ((int) $category['user_id'] !== $currentUserId) {
        errorResponse(
            'Bạn không có quyền xóa danh mục này.',
            403
        );
    }

    $stmt = $db->prepare("
        UPDATE categories
        SET is_active = FALSE
        WHERE id = :id
          AND user_id = :user_id
    ");

    $stmt->execute([
        'id' => $id,
        'user_id' => $currentUserId
    ]);

    successResponse(
        'Xóa danh mục thành công.',
        [
            'id' => $id
        ]
    );

} catch (PDOException $e) {
    errorResponse(
        'Không thể xóa danh mục.',
        500
    );
}