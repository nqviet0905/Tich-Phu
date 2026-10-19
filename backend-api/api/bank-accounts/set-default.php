<?php

require_once __DIR__ . '/../../middleware/cors.php';
require_once __DIR__ . '/../../config/database.php';
require_once __DIR__ . '/../../middleware/auth.php';
require_once __DIR__ . '/../../helpers/response.php';

if ($_SERVER['REQUEST_METHOD'] !== 'PUT'
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
| CHECK OWNERSHIP
|--------------------------------------------------------------------------
*/

$checkStmt = $db->prepare("
    SELECT id
    FROM bank_accounts
    WHERE id = :id
      AND user_id = :user_id
      AND is_active = 1
    LIMIT 1
");

$checkStmt->execute([
    ':id' => $bankAccountId,
    ':user_id' => $currentUserId
]);

if (!$checkStmt->fetch()) {

    errorResponse(
        'Không tìm thấy tài khoản ngân hàng.',
        404
    );
}


try {

    $db->beginTransaction();


    /*
    |--------------------------------------------------------------------------
    | CLEAR DEFAULT
    |--------------------------------------------------------------------------
    */

    $clearStmt = $db->prepare("
        UPDATE bank_accounts
        SET is_default = 0
        WHERE user_id = :user_id
    ");

    $clearStmt->execute([
        ':user_id' => $currentUserId
    ]);


    /*
    |--------------------------------------------------------------------------
    | SET DEFAULT
    |--------------------------------------------------------------------------
    */

    $defaultStmt = $db->prepare("
        UPDATE bank_accounts
        SET is_default = 1
        WHERE id = :id
          AND user_id = :user_id
    ");

    $defaultStmt->execute([
        ':id' => $bankAccountId,
        ':user_id' => $currentUserId
    ]);


    $db->commit();


    successResponse(
        'Đã đặt tài khoản ngân hàng mặc định.',
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
        'Không thể đặt tài khoản mặc định.',
        500
    );
}