<?php

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
| WALLET INFO
|--------------------------------------------------------------------------
*/

try {

    /*
    |--------------------------------------------------------------------------
    | USER + ACCOUNT NUMBER
    |--------------------------------------------------------------------------
    */

    $stmt =
        $db->prepare("
            SELECT
                id,
                account_number
            FROM users
            WHERE id = :user_id
            LIMIT 1
        ");

    $stmt->execute([
        'user_id' =>
            $currentUserId
    ]);

    $walletUser =
        $stmt->fetch(
            PDO::FETCH_ASSOC
        );

    if (!$walletUser) {

        errorResponse(
            'Không tìm thấy tài khoản người dùng.',
            404
        );
    }


    /*
    |--------------------------------------------------------------------------
    | CALCULATE BALANCE
    |--------------------------------------------------------------------------
    |
    | balance = income - expense
    |
    */

    $stmt =
        $db->prepare("
            SELECT

                COALESCE(
                    SUM(
                        CASE
                            WHEN type = 'income'
                                THEN amount
                            ELSE 0
                        END
                    ),
                    0
                ) AS total_income,

                COALESCE(
                    SUM(
                        CASE
                            WHEN type = 'expense'
                                THEN amount
                            ELSE 0
                        END
                    ),
                    0
                ) AS total_expense

            FROM transactions

            WHERE user_id = :user_id
        ");

    $stmt->execute([
        'user_id' =>
            $currentUserId
    ]);

    $result =
        $stmt->fetch(
            PDO::FETCH_ASSOC
        );


    /*
    |--------------------------------------------------------------------------
    | VALUES
    |--------------------------------------------------------------------------
    */

    $totalIncome =
        (float) (
            $result['total_income']
            ?? 0
        );

    $totalExpense =
        (float) (
            $result['total_expense']
            ?? 0
        );

    $balance =
        $totalIncome
        -
        $totalExpense;


    /*
    |--------------------------------------------------------------------------
    | RESPONSE
    |--------------------------------------------------------------------------
    */

    successResponse(
        'Lấy số dư thành công.',
        [
            'balance' =>
                $balance,

            'account_number' =>
                $walletUser['account_number']
                ?? null
        ]
    );


} catch (PDOException $e) {

    error_log(
        'Wallet balance DB error: '
        . $e->getMessage()
    );

    errorResponse(
        'Không thể lấy số dư.',
        500
    );
}