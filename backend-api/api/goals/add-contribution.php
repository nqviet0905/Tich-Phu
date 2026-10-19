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

$goalId = (int) ($body['goal_id'] ?? 0);
$amount = $body['amount'] ?? null;
$contributionDate = trim($body['contribution_date'] ?? '');
$note = trim($body['note'] ?? '');

if ($goalId <= 0) {
    errorResponse('ID mục tiêu không hợp lệ.', 400);
}

if (!is_numeric($amount) || $amount <= 0) {
    errorResponse('Số tiền đóng góp phải lớn hơn 0.', 400);
}

$date = DateTime::createFromFormat(
    'Y-m-d',
    $contributionDate
);

if (
    !$date ||
    $date->format('Y-m-d') !== $contributionDate
) {
    errorResponse(
        'Ngày đóng góp không hợp lệ.',
        400
    );
}

if (mb_strlen($note) > 500) {
    errorResponse(
        'Ghi chú không được vượt quá 500 ký tự.',
        400
    );
}

$amount = (float) $amount;

$database = new Database();
$db = $database->connect();

if (!$db) {
    errorResponse('Không thể kết nối cơ sở dữ liệu.', 500);
}

try {
    $stmt = $db->prepare("
        SELECT
            id,
            target_amount,
            current_amount,
            status
        FROM goals
        WHERE id = :id
          AND user_id = :user_id
        LIMIT 1
    ");

    $stmt->execute([
        'id' => $goalId,
        'user_id' => $currentUserId
    ]);

    $goal = $stmt->fetch();

    if (!$goal) {
        errorResponse('Không tìm thấy mục tiêu.', 404);
    }

    if ($goal['status'] === 'cancelled') {
        errorResponse(
            'Không thể đóng góp vào mục tiêu đã hủy.',
            422
        );
    }

    if ($goal['status'] === 'completed') {
        errorResponse(
            'Mục tiêu đã hoàn thành.',
            422
        );
    }

    $targetAmount = (float) $goal['target_amount'];
    $currentAmount = (float) $goal['current_amount'];

    $newAmount = $currentAmount + $amount;

    $newStatus = $newAmount >= $targetAmount
        ? 'completed'
        : 'active';

    $db->beginTransaction();

    $stmt = $db->prepare("
        INSERT INTO goal_transactions (
            goal_id,
            amount,
            contribution_date,
            note
        )
        VALUES (
            :goal_id,
            :amount,
            :contribution_date,
            :note
        )
    ");

    $stmt->execute([
        'goal_id' => $goalId,
        'amount' => $amount,
        'contribution_date' => $contributionDate,
        'note' => $note !== '' ? $note : null
    ]);

    $contributionId = (int) $db->lastInsertId();

    $stmt = $db->prepare("
        UPDATE goals
        SET
            current_amount = :current_amount,
            status = :status
        WHERE id = :id
          AND user_id = :user_id
    ");

    $stmt->execute([
        'current_amount' => $newAmount,
        'status' => $newStatus,
        'id' => $goalId,
        'user_id' => $currentUserId
    ]);

    $db->commit();

    $remainingAmount = max(
        0,
        $targetAmount - $newAmount
    );

    $progressPercentage = $targetAmount > 0
        ? ($newAmount / $targetAmount) * 100
        : 0;

    successResponse(
        'Thêm tiền vào mục tiêu thành công.',
        [
            'contribution_id' => $contributionId,
            'goal_id' => $goalId,
            'added_amount' => $amount,
            'current_amount' => $newAmount,
            'target_amount' => $targetAmount,
            'remaining_amount' => $remainingAmount,
            'progress_percentage' =>
                round($progressPercentage, 2),
            'status' => $newStatus
        ],
        201
    );

} catch (PDOException $e) {

    if ($db->inTransaction()) {
        $db->rollBack();
    }

    errorResponse(
        'Không thể thêm tiền vào mục tiêu.',
        500
    );
}