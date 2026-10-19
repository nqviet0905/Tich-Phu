<?php

require_once __DIR__ . '/../../middleware/cors.php';
require_once __DIR__ . '/../../middleware/auth.php';
require_once __DIR__ . '/../../config/database.php';
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

$authData = authenticateUser();

$user = $authData['user'] ?? null;

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
| TRANSACTION ID
|--------------------------------------------------------------------------
*/

$id =
    isset($_GET['id'])
        ? (int) $_GET['id']
        : 0;

if ($id <= 0) {

    errorResponse(
        'ID giao dịch không hợp lệ.',
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
| GET DETAIL
|--------------------------------------------------------------------------
*/

try {

    $stmt =
        $db->prepare("
            SELECT

                t.id,

                t.category_id,

                c.name
                    AS category_name,

                c.icon
                    AS category_icon,

                t.amount,

                t.type,

                t.transaction_date,

                t.transaction_time,

                t.merchant_name,

                t.payment_method,

                t.bank_account_id,

                t.reference_code,

                t.note,

                t.created_at,

                t.updated_at,


                /*
                |--------------------------------------------------------------------------
                | BANK ACCOUNT
                |--------------------------------------------------------------------------
                */

                ba.bank_code
                    AS bank_code,

                ba.bank_name
                    AS bank_name,

                ba.account_name
                    AS bank_account_name,

                CASE

                    WHEN ba.account_number IS NULL
                    THEN NULL

                    WHEN CHAR_LENGTH(ba.account_number) <= 4
                    THEN ba.account_number

                    ELSE CONCAT(
                        '•••• ',
                        RIGHT(
                            ba.account_number,
                            4
                        )
                    )

                END
                    AS masked_account_number


            FROM transactions t


            /*
            |--------------------------------------------------------------------------
            | CATEGORY
            |--------------------------------------------------------------------------
            */

            INNER JOIN categories c
                ON c.id = t.category_id


            /*
            |--------------------------------------------------------------------------
            | BANK ACCOUNT
            |--------------------------------------------------------------------------
            |
            | LEFT JOIN vì giao dịch nhập tay bình thường
            | không có bank_account_id.
            |--------------------------------------------------------------------------
            */

            LEFT JOIN bank_accounts ba

                ON ba.id =
                    t.bank_account_id

                AND ba.user_id =
                    t.user_id


            WHERE t.id = :id

              AND t.user_id = :user_id

            LIMIT 1
        ");


    $stmt->execute([
        ':id' =>
            $id,

        ':user_id' =>
            $currentUserId
    ]);


    $transaction =
        $stmt->fetch(
            PDO::FETCH_ASSOC
        );


    /*
    |--------------------------------------------------------------------------
    | NOT FOUND
    |--------------------------------------------------------------------------
    */

    if (!$transaction) {

        errorResponse(
            'Không tìm thấy giao dịch.',
            404
        );
    }


    /*
    |--------------------------------------------------------------------------
    | NORMALIZE TYPES
    |--------------------------------------------------------------------------
    */

    $transaction['id'] =
        (int) $transaction['id'];


    $transaction['category_id'] =
        (int) $transaction['category_id'];


    $transaction['amount'] =
        (float) $transaction['amount'];


    $transaction['bank_account_id'] =
        $transaction['bank_account_id'] !== null
            ? (int) $transaction['bank_account_id']
            : null;


    /*
    |--------------------------------------------------------------------------
    | RESPONSE
    |--------------------------------------------------------------------------
    */

    successResponse(
        'Lấy chi tiết giao dịch thành công.',
        $transaction
    );


} catch (PDOException $e) {

    error_log(
        'TRANSACTION DETAIL ERROR: '
        . $e->getMessage()
    );


    errorResponse(
        'Không thể lấy giao dịch.',
        500
    );
}