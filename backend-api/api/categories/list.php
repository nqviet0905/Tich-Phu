<?php

require_once __DIR__ . '/../../middleware/cors.php';
require_once __DIR__ . '/../../middleware/auth.php';
require_once __DIR__ . '/../../config/database.php';
require_once __DIR__ . '/../../helpers/response.php';

$authData = authenticateUser();

$user = $authData['user'];

if (!$user) {
    errorResponse(
        'Tài khoản chưa được đồng bộ với hệ thống.',
        403
    );
}

$currentUserId = (int) $user['id'];

$type = $_GET['type'] ?? null;

if ($type !== null && !in_array($type, ['income', 'expense'], true)) {
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
    $sql = "
        SELECT
            id,
            user_id,
            name,
            type,
            icon,
            is_default,
            is_active
        FROM categories
        WHERE is_active = TRUE
          AND (
              user_id IS NULL
              OR user_id = :user_id
          )
    ";

    $params = [
        'user_id' => $currentUserId
    ];

    if ($type !== null) {
        $sql .= " AND type = :type";
        $params['type'] = $type;
    }

    $sql .= "
        ORDER BY
            is_default DESC,
            id ASC
    ";

    $stmt = $db->prepare($sql);
    $stmt->execute($params);

    $categories = $stmt->fetchAll();

    successResponse(
        'Lấy danh sách danh mục thành công.',
        $categories
    );

} catch (PDOException $e) {
    errorResponse(
        'Đã xảy ra lỗi khi lấy danh mục.',
        500
    );
}