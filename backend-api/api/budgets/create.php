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
    errorResponse('Tài khoản chưa được đồng bộ với hệ thống.', 403);
}

$currentUserId = (int) $user['id'];

$body = json_decode(
    file_get_contents('php://input'),
    true
);

if (!is_array($body)) {
    errorResponse('Dữ liệu JSON không hợp lệ.', 400);
}

$categoryId = (int) ($body['category_id'] ?? 0);
$amount = $body['amount'] ?? null;
$month = (int) ($body['month'] ?? 0);
$year = (int) ($body['year'] ?? 0);
$warningLevel = (int) ($body['warning_level'] ?? 80);

if ($categoryId <= 0) {
    errorResponse('Danh mục không hợp lệ.', 400);
}

if (!is_numeric($amount) || $amount <= 0) {
    errorResponse('Ngân sách phải lớn hơn 0.', 400);
}

if ($month < 1 || $month > 12) {
    errorResponse('Tháng không hợp lệ.', 400);
}

if ($year < 2000 || $year > 2100) {
    errorResponse('Năm không hợp lệ.', 400);
}

if ($warningLevel < 1 || $warningLevel > 100) {
    errorResponse('Mức cảnh báo phải từ 1 đến 100.', 400);
}

$database = new Database();
$db = $database->connect();

if (!$db) {
    errorResponse('Không thể kết nối cơ sở dữ liệu.', 500);
}

try {
    // Chỉ cho phép đặt ngân sách cho category expense
    $stmt = $db->prepare("
        SELECT
            id,
            type
        FROM categories
        WHERE id = :category_id
          AND is_active = TRUE
          AND (
              user_id IS NULL
              OR user_id = :user_id
          )
        LIMIT 1
    ");

    $stmt->execute([
        'category_id' => $categoryId,
        'user_id' => $currentUserId
    ]);

    $category = $stmt->fetch();

    if (!$category) {
        errorResponse(
            'Không tìm thấy danh mục hoặc bạn không có quyền sử dụng danh mục này.',
            404
        );
    }

    if ($category['type'] !== 'expense') {
        errorResponse(
            'Chỉ được tạo ngân sách cho danh mục chi.',
            422
        );
    }

    // Kiểm tra budget đã tồn tại
    $stmt = $db->prepare("
        SELECT id
        FROM budgets
        WHERE user_id = :user_id
          AND category_id = :category_id
          AND month = :month
          AND year = :year
        LIMIT 1
    ");

    $stmt->execute([
        'user_id' => $currentUserId,
        'category_id' => $categoryId,
        'month' => $month,
        'year' => $year
    ]);

    if ($stmt->fetch()) {
        errorResponse(
            'Ngân sách cho danh mục này đã tồn tại trong tháng.',
            409
        );
    }

    $stmt = $db->prepare("
        INSERT INTO budgets (
            user_id,
            category_id,
            amount,
            month,
            year,
            warning_level
        )
        VALUES (
            :user_id,
            :category_id,
            :amount,
            :month,
            :year,
            :warning_level
        )
    ");

    $stmt->execute([
        'user_id' => $currentUserId,
        'category_id' => $categoryId,
        'amount' => $amount,
        'month' => $month,
        'year' => $year,
        'warning_level' => $warningLevel
    ]);

    $budgetId = (int) $db->lastInsertId();

    $stmt = $db->prepare("
        SELECT
            b.id,
            b.category_id,
            c.name AS category_name,
            b.amount,
            b.month,
            b.year,
            b.warning_level,
            b.created_at,
            b.updated_at
        FROM budgets b
        INNER JOIN categories c
            ON c.id = b.category_id
        WHERE b.id = :id
          AND b.user_id = :user_id
        LIMIT 1
    ");

    $stmt->execute([
        'id' => $budgetId,
        'user_id' => $currentUserId
    ]);

    successResponse(
        'Tạo ngân sách thành công.',
        $stmt->fetch(),
        201
    );

} catch (PDOException $e) {
    errorResponse(
        'Không thể tạo ngân sách.',
        500
    );
}