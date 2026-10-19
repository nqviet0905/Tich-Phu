<?php

require_once __DIR__ . '/../../middleware/cors.php';
require_once __DIR__ . '/../../middleware/auth.php';
require_once __DIR__ . '/../../config/database.php';
require_once __DIR__ . '/../../helpers/response.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
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

$name = trim($body['name'] ?? '');
$type = trim($body['type'] ?? '');
$icon = trim($body['icon'] ?? '');

if ($name === '') {
    errorResponse(
        'Tên danh mục không được để trống.',
        400
    );
}

if (mb_strlen($name) > 100) {
    errorResponse(
        'Tên danh mục không được vượt quá 100 ký tự.',
        400
    );
}

if (!in_array($type, ['income', 'expense'], true)) {
    errorResponse(
        'Loại danh mục không hợp lệ.',
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
        FROM categories
        WHERE user_id = :user_id
          AND name = :name
          AND type = :type
          AND is_active = TRUE
        LIMIT 1
    ");

    $stmt->execute([
        'user_id' => $currentUserId,
        'name' => $name,
        'type' => $type
    ]);

    if ($stmt->fetch()) {
        errorResponse(
            'Danh mục này đã tồn tại.',
            409
        );
    }

    $stmt = $db->prepare("
        INSERT INTO categories (
            user_id,
            name,
            type,
            icon,
            is_default,
            is_active
        )
        VALUES (
            :user_id,
            :name,
            :type,
            :icon,
            FALSE,
            TRUE
        )
    ");

    $stmt->execute([
        'user_id' => $currentUserId,
        'name' => $name,
        'type' => $type,
        'icon' => $icon !== '' ? $icon : null
    ]);

    $categoryId = (int) $db->lastInsertId();

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
        'id' => $categoryId
    ]);

    $category = $stmt->fetch();

    successResponse(
        'Tạo danh mục thành công.',
        $category,
        201
    );

} catch (PDOException $e) {
    errorResponse(
        'Không thể tạo danh mục.',
        500
    );
}