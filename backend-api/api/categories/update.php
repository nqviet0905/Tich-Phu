<?php

require_once __DIR__ . '/../../middleware/cors.php';
require_once __DIR__ . '/../../middleware/auth.php';
require_once __DIR__ . '/../../config/database.php';
require_once __DIR__ . '/../../helpers/response.php';

if ($_SERVER['REQUEST_METHOD'] !== 'PUT') {
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

$id = (int) ($body['id'] ?? 0);
$name = trim($body['name'] ?? '');
$icon = trim($body['icon'] ?? '');

if ($id <= 0) {
    errorResponse(
        'ID danh mục không hợp lệ.',
        400
    );
}

if ($name === '') {
    errorResponse(
        'Tên danh mục không được để trống.',
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
            'Không thể sửa danh mục mặc định.',
            403
        );
    }

    if ((int) $category['user_id'] !== $currentUserId) {
        errorResponse(
            'Bạn không có quyền sửa danh mục này.',
            403
        );
    }

    $stmt = $db->prepare("
        UPDATE categories
        SET
            name = :name,
            icon = :icon
        WHERE id = :id
          AND user_id = :user_id
    ");

    $stmt->execute([
        'name' => $name,
        'icon' => $icon !== '' ? $icon : null,
        'id' => $id,
        'user_id' => $currentUserId
    ]);

    $stmt = $db->prepare("
        SELECT
            id,
            user_id,
            name,
            type,
            icon,
            is_default,
            is_active
        FROM categories
        WHERE id = :id
        LIMIT 1
    ");

    $stmt->execute([
        'id' => $id
    ]);

    successResponse(
        'Cập nhật danh mục thành công.',
        $stmt->fetch()
    );

} catch (PDOException $e) {
    errorResponse(
        'Không thể cập nhật danh mục.',
        500
    );
}