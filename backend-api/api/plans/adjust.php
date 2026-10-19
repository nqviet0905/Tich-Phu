<?php

require_once __DIR__ . '/../../middleware/cors.php';
require_once __DIR__ . '/../../middleware/auth.php';
require_once __DIR__ . '/../../config/database.php';
require_once __DIR__ . '/../../helpers/response.php';

if ($_SERVER['REQUEST_METHOD'] !== 'PUT') {
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

$planId = (int) ($body['plan_id'] ?? 0);
$allocations = $body['allocations'] ?? [];

if ($planId <= 0) {
    errorResponse('ID kế hoạch không hợp lệ.', 400);
}

if (!is_array($allocations) || count($allocations) === 0) {
    errorResponse('Danh sách phân bổ không được để trống.', 400);
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
            flexible_budget
        FROM financial_plans
        WHERE id = :id
          AND user_id = :user_id
          AND status != 'cancelled'
        LIMIT 1
    ");

    $stmt->execute([
        'id' => $planId,
        'user_id' => $currentUserId
    ]);

    $plan = $stmt->fetch();

    if (!$plan) {
        errorResponse('Không tìm thấy kế hoạch.', 404);
    }

    $totalAllocated = 0;

    foreach ($allocations as $allocation) {
        $categoryId = (int) ($allocation['category_id'] ?? 0);
        $plannedAmount = $allocation['planned_amount'] ?? null;

        if ($categoryId <= 0) {
            errorResponse('Danh mục phân bổ không hợp lệ.', 400);
        }

        if (!is_numeric($plannedAmount) || $plannedAmount < 0) {
            errorResponse('Số tiền phân bổ không hợp lệ.', 400);
        }

        $stmt = $db->prepare("
            SELECT id
            FROM categories
            WHERE id = :category_id
              AND type = 'expense'
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

        if (!$stmt->fetch()) {
            errorResponse(
                'Có danh mục không hợp lệ hoặc không thuộc quyền sử dụng của bạn.',
                422
            );
        }

        $totalAllocated += (float) $plannedAmount;
    }

    if ($totalAllocated > (float) $plan['flexible_budget']) {
        errorResponse(
            'Tổng số tiền phân bổ vượt quá ngân sách linh hoạt.',
            422
        );
    }

    $db->beginTransaction();

    $stmt = $db->prepare("
        DELETE FROM plan_details
        WHERE plan_id = :plan_id
    ");

    $stmt->execute([
        'plan_id' => $planId
    ]);

    $insertStmt = $db->prepare("
        INSERT INTO plan_details (
            plan_id,
            category_id,
            planned_amount,
            note
        )
        VALUES (
            :plan_id,
            :category_id,
            :planned_amount,
            :note
        )
    ");

    foreach ($allocations as $allocation) {
        $insertStmt->execute([
            'plan_id' => $planId,
            'category_id' => (int) $allocation['category_id'],
            'planned_amount' => (float) $allocation['planned_amount'],
            'note' => isset($allocation['note'])
                ? trim($allocation['note'])
                : null
        ]);
    }

    $db->commit();

    successResponse(
        'Cập nhật phân bổ kế hoạch thành công.',
        [
            'plan_id' => $planId,
            'total_allocated' => $totalAllocated,
            'flexible_budget' => (float) $plan['flexible_budget'],
            'remaining_unallocated' =>
                (float) $plan['flexible_budget'] - $totalAllocated
        ]
    );

} catch (PDOException $e) {
    if ($db->inTransaction()) {
        $db->rollBack();
    }

    errorResponse(
        'Không thể cập nhật phân bổ kế hoạch.',
        500
    );
}