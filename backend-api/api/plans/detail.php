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
        SELECT
            id,
            plan_name,
            monthly_income,
            fixed_expense,
            saving_target,
            flexible_budget,
            month,
            year,
            status,
            created_at,
            updated_at
        FROM financial_plans
        WHERE id = :id
          AND user_id = :user_id
        LIMIT 1
    ");

    $stmt->execute([
        'id' => $id,
        'user_id' => $currentUserId
    ]);

    $plan = $stmt->fetch();

    if (!$plan) {
        errorResponse('Không tìm thấy kế hoạch tài chính.', 404);
    }

    $stmt = $db->prepare("
        SELECT
            pd.id,
            pd.category_id,
            c.name AS category_name,
            c.icon AS category_icon,
            pd.planned_amount,
            pd.note
        FROM plan_details pd
        INNER JOIN categories c
            ON c.id = pd.category_id
        WHERE pd.plan_id = :plan_id
        ORDER BY c.name ASC
    ");

    $stmt->execute([
        'plan_id' => $id
    ]);

    $allocations = $stmt->fetchAll();

    $plan['allocations'] = $allocations;

    successResponse(
        'Lấy chi tiết kế hoạch thành công.',
        $plan
    );

} catch (PDOException $e) {
    errorResponse(
        'Không thể lấy chi tiết kế hoạch.',
        500
    );
}