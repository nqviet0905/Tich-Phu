<?php

require_once __DIR__ . '/../../middleware/cors.php';
require_once __DIR__ . '/../../middleware/auth.php';
require_once __DIR__ . '/../../config/database.php';
require_once __DIR__ . '/../../helpers/response.php';

if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
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

$month = isset($_GET['month'])
    ? (int) $_GET['month']
    : (int) date('n');

$year = isset($_GET['year'])
    ? (int) $_GET['year']
    : (int) date('Y');

$type = $_GET['type'] ?? 'expense';

if ($month < 1 || $month > 12) {
    errorResponse('Tháng không hợp lệ.', 400);
}

if ($year < 2000 || $year > 2100) {
    errorResponse('Năm không hợp lệ.', 400);
}

if (!in_array($type, ['income', 'expense'], true)) {
    errorResponse('Loại thống kê không hợp lệ.', 400);
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
            c.id AS category_id,
            c.name AS category_name,
            c.icon AS category_icon,
            COUNT(t.id) AS transaction_count,
            COALESCE(SUM(t.amount), 0) AS total_amount
        FROM transactions t
        INNER JOIN categories c
            ON c.id = t.category_id
        WHERE t.user_id = :user_id
          AND t.type = :type
          AND MONTH(t.transaction_date) = :month
          AND YEAR(t.transaction_date) = :year
        GROUP BY
            c.id,
            c.name,
            c.icon
        ORDER BY total_amount DESC
    ");

    $stmt->execute([
        'user_id' => $currentUserId,
        'type' => $type,
        'month' => $month,
        'year' => $year
    ]);

    $categories = $stmt->fetchAll();

    $totalAmount = 0;

    foreach ($categories as $item) {
        $totalAmount += (float) $item['total_amount'];
    }

    $result = [];

    foreach ($categories as $item) {

        $amount = (float) $item['total_amount'];

        $percentage = $totalAmount > 0
            ? ($amount / $totalAmount) * 100
            : 0;

        $result[] = [
            'category_id' =>
                (int) $item['category_id'],

            'category_name' =>
                $item['category_name'],

            'category_icon' =>
                $item['category_icon'],

            'transaction_count' =>
                (int) $item['transaction_count'],

            'total_amount' =>
                $amount,

            'percentage' =>
                round($percentage, 2)
        ];
    }

    successResponse(
        'Lấy thống kê theo danh mục thành công.',
        [
            'month' => $month,
            'year' => $year,
            'type' => $type,
            'total_amount' => $totalAmount,
            'categories' => $result
        ]
    );

} catch (PDOException $e) {

    errorResponse(
        'Không thể lấy thống kê theo danh mục.',
        500
    );
}