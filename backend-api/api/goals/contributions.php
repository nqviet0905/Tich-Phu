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

$goalId = isset($_GET['goal_id'])
    ? (int) $_GET['goal_id']
    : 0;

if ($goalId <= 0) {
    errorResponse(
        'ID mục tiêu không hợp lệ.',
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
        FROM goals
        WHERE id = :goal_id
          AND user_id = :user_id
        LIMIT 1
    ");

    $stmt->execute([
        'goal_id' => $goalId,
        'user_id' => $currentUserId
    ]);

    if (!$stmt->fetch()) {
        errorResponse(
            'Không tìm thấy mục tiêu.',
            404
        );
    }

    $stmt = $db->prepare("
        SELECT
            id,
            goal_id,
            amount,
            contribution_date,
            note,
            created_at
        FROM goal_transactions
        WHERE goal_id = :goal_id
        ORDER BY contribution_date DESC, id DESC
    ");

    $stmt->execute([
        'goal_id' => $goalId
    ]);

    successResponse(
        'Lấy lịch sử đóng góp thành công.',
        $stmt->fetchAll()
    );

} catch (PDOException $e) {
    errorResponse(
        'Không thể lấy lịch sử đóng góp.',
        500
    );
}