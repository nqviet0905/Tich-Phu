<?php

require_once __DIR__ . '/../../middleware/cors.php';
require_once __DIR__ . '/../../config/database.php';
require_once __DIR__ . '/../../middleware/auth.php';
require_once __DIR__ . '/../../helpers/response.php';

if ($_SERVER['REQUEST_METHOD'] !== 'DELETE'
    && $_SERVER['REQUEST_METHOD'] !== 'POST') {

    errorResponse(
        'Phương thức không được hỗ trợ.',
        405
    );
}

$authData =
    authenticateUser();

$user =
    $authData['user'] ?? null;

if (!$user) {

    errorResponse(
        'Không tìm thấy người dùng.',
        403
    );
}

$currentUserId =
    (int) $user['id'];

$database =
    new Database();

$db =
    $database->connect();

if (!$db) {

    errorResponse(
        'Không thể kết nối cơ sở dữ liệu.',
        500
    );
}

$data = json_decode(
    file_get_contents('php://input'),
    true
);

$bankAccountId =
    (int) ($data['bank_account_id'] ?? 0);

if ($bankAccountId <= 0) {

    errorResponse(
        'Tài khoản ngân hàng không hợp lệ.',
        422
    );
}


/*
|--------------------------------------------------------------------------
| FIND ACCOUNT
|--------------------------------------------------------------------------
*/

$stmt = $db->prepare("
    SELECT
        id,
        is_default
    FROM bank_accounts
    WHERE id = :id
      AND user_id = :user_id
      AND is_active = 1
    LIMIT 1
");

$stmt->execute([
    ':id' => $bankAccountId,
    ':user_id' => $currentUserId
]);

$account =
    $stmt->fetch();

if (!$account) {

    errorResponse(
        'Không tìm thấy tài khoản ngân hàng.',
        404
    );
}


try {

    $db->beginTransaction();


    /*
    |--------------------------------------------------------------------------
    | SOFT DELETE
    |--------------------------------------------------------------------------
    */

    $deleteStmt = $db->prepare("
        UPDATE bank_accounts
        SET
            is_active = 0,
            is_default = 0
        WHERE id = :id
          AND user_id = :user_id
    ");

    $deleteStmt->execute([
        ':id' => $bankAccountId,
        ':user_id' => $currentUserId
    ]);


    /*
    |--------------------------------------------------------------------------
    | NẾU XÓA TÀI KHOẢN MẶC ĐỊNH
    | → chọn tài khoản còn lại làm mặc định
    |--------------------------------------------------------------------------
    */

    if ((int) $account['is_default'] === 1) {

        $nextStmt = $db->prepare("
            SELECT id
            FROM bank_accounts
            WHERE user_id = :user_id
              AND is_active = 1
            ORDER BY created_at DESC
            LIMIT 1
        ");

        $nextStmt->execute([
            ':user_id' => $currentUserId
        ]);

        $nextAccount =
            $nextStmt->fetch();

        if ($nextAccount) {

            $setStmt = $db->prepare("
                UPDATE bank_accounts
                SET is_default = 1
                WHERE id = :id
                  AND user_id = :user_id
            ");

            $setStmt->execute([
                ':id' =>
                    (int) $nextAccount['id'],

                ':user_id' =>
                    $currentUserId
            ]);
        }
    }


    $db->commit();


    successResponse(
        'Đã hủy liên kết tài khoản ngân hàng.',
        [
            'bank_account_id' =>
                $bankAccountId
        ],
        200
    );

} catch (Throwable $e) {

    if ($db->inTransaction()) {
        $db->rollBack();
    }

    errorResponse(
        'Không thể hủy liên kết tài khoản.',
        500
    );
}