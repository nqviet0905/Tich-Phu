<?php

require_once __DIR__ . '/../../middleware/cors.php';
require_once __DIR__ . '/../../config/database.php';
require_once __DIR__ . '/../../config/firebase.php';
require_once __DIR__ . '/../../middleware/auth.php';
require_once __DIR__ . '/../../helpers/response.php';


/*
|--------------------------------------------------------------------------
| METHOD
|--------------------------------------------------------------------------
*/

if ($_SERVER['REQUEST_METHOD'] !== 'GET') {

    errorResponse(
        'Phương thức không được hỗ trợ.',
        405
    );
}


/*
|--------------------------------------------------------------------------
| AUTH
|--------------------------------------------------------------------------
*/

$authData =
    authenticateUser();

$user =
    $authData['user']
    ?? null;

if (!$user) {

    errorResponse(
        'Tài khoản chưa được đồng bộ với hệ thống.',
        403
    );
}

$currentUserId =
    (int) $user['id'];


/*
|--------------------------------------------------------------------------
| ACCOUNT NUMBER
|--------------------------------------------------------------------------
*/

$accountNumber =
    preg_replace(
        '/\D/',
        '',
        $_GET['account_number']
        ?? ''
    );


/*
|--------------------------------------------------------------------------
| VALIDATE
|--------------------------------------------------------------------------
*/

if ($accountNumber === '') {

    errorResponse(
        'Vui lòng nhập số tài khoản người nhận.',
        400
    );
}


if (
    strlen($accountNumber) < 8
    ||
    strlen($accountNumber) > 20
) {

    errorResponse(
        'Số tài khoản không hợp lệ.',
        400
    );
}


/*
|--------------------------------------------------------------------------
| DATABASE
|--------------------------------------------------------------------------
*/

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


/*
|--------------------------------------------------------------------------
| FIND RECIPIENT
|--------------------------------------------------------------------------
*/

try {

    $stmt =
        $db->prepare("
            SELECT
                id,
                name,
                email,
                account_number

            FROM users

            WHERE account_number = :account_number

            LIMIT 1
        ");

    $stmt->execute([
        'account_number' =>
            $accountNumber
    ]);

    $recipient =
        $stmt->fetch(
            PDO::FETCH_ASSOC
        );


    /*
    |--------------------------------------------------------------------------
    | NOT FOUND
    |--------------------------------------------------------------------------
    */

    if (!$recipient) {

        errorResponse(
            'Không tìm thấy tài khoản người nhận.',
            404
        );
    }


    /*
    |--------------------------------------------------------------------------
    | CANNOT TRANSFER TO YOURSELF
    |--------------------------------------------------------------------------
    */

    if (
        (int) $recipient['id']
        ===
        $currentUserId
    ) {

        errorResponse(
            'Bạn không thể chuyển tiền cho chính mình.',
            400
        );
    }


    /*
    |--------------------------------------------------------------------------
    | RESPONSE
    |--------------------------------------------------------------------------
    */

    successResponse(
        'Tìm thấy người nhận.',
        [
            'id' =>
                (int) $recipient['id'],

            'name' =>
                $recipient['name']
                ?? null,

            'email' =>
                $recipient['email']
                ?? null,

            'account_number' =>
                $recipient['account_number']
        ]
    );


} catch (PDOException $e) {

    error_log(
        'Wallet recipient error: '
        . $e->getMessage()
    );

    errorResponse(
        'Không thể kiểm tra tài khoản người nhận.',
        500
    );
}