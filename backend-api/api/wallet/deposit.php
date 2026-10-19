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

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {

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
    $authData['user'];

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
| JSON BODY
|--------------------------------------------------------------------------
*/

$body =
    json_decode(
        file_get_contents('php://input'),
        true
    );

if (!is_array($body)) {

    errorResponse(
        'Dữ liệu JSON không hợp lệ.',
        400
    );
}


/*
|--------------------------------------------------------------------------
| DATA
|--------------------------------------------------------------------------
*/

$amount =
    $body['amount']
    ?? null;

$method =
    trim(
        $body['method']
        ?? ''
    );

$note =
    trim(
        $body['note']
        ?? ''
    );


/*
|--------------------------------------------------------------------------
| VALIDATE AMOUNT
|--------------------------------------------------------------------------
*/

if (
    !is_numeric($amount)
    || (float) $amount <= 0
) {

    errorResponse(
        'Số tiền nạp phải lớn hơn 0.',
        400
    );
}

$amount =
    (float) $amount;


/*
|--------------------------------------------------------------------------
| VALIDATE METHOD
|--------------------------------------------------------------------------
*/

$allowedMethods =
    [
        'bank',
        'card',
        'wallet'
    ];

if (
    !in_array(
        $method,
        $allowedMethods,
        true
    )
) {

    errorResponse(
        'Phương thức nạp tiền không hợp lệ.',
        400
    );
}


if (
    mb_strlen($note) > 500
) {

    errorResponse(
        'Ghi chú không được vượt quá 500 ký tự.',
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
| DEPOSIT
|--------------------------------------------------------------------------
*/

try {

    $db->beginTransaction();


    /*
    |--------------------------------------------------------------------------
    | CATEGORY "THU KHÁC"
    |--------------------------------------------------------------------------
    */

    $stmt =
        $db->prepare("
            SELECT id
            FROM categories
            WHERE name = :name
              AND type = 'income'
              AND user_id IS NULL
              AND is_active = TRUE
            LIMIT 1
        ");

    $stmt->execute([
        'name' =>
            'Thu khác'
    ]);

    $category =
        $stmt->fetch();

    if (!$category) {

        $db->rollBack();

        errorResponse(
            'Không tìm thấy danh mục Thu khác.',
            500
        );
    }

    $categoryId =
        (int) $category['id'];


    /*
    |--------------------------------------------------------------------------
    | METHOD TEXT
    |--------------------------------------------------------------------------
    */

    $methodNames =
        [
            'bank' =>
                'Tài khoản ngân hàng',

            'card' =>
                'Thẻ ATM / Thẻ ghi nợ',

            'wallet' =>
                'Ví điện tử'
        ];

    $methodText =
        $methodNames[$method]
        ?? $method;


    /*
    |--------------------------------------------------------------------------
    | NOTE
    |--------------------------------------------------------------------------
    */

    $transactionNote =
        'Nạp tiền qua '
        . $methodText;

    if ($note !== '') {

        $transactionNote .=
            ' - '
            . $note;
    }


    /*
    |--------------------------------------------------------------------------
    | INSERT TRANSACTION
    |--------------------------------------------------------------------------
    */

    $stmt =
        $db->prepare("
            INSERT INTO transactions (
                user_id,
                category_id,
                amount,
                type,
                transaction_date,
                note
            )
            VALUES (
                :user_id,
                :category_id,
                :amount,
                'income',
                CURDATE(),
                :note
            )
        ");

    $stmt->execute([
        'user_id' =>
            $currentUserId,

        'category_id' =>
            $categoryId,

        'amount' =>
            $amount,

        'note' =>
            $transactionNote
    ]);

    $transactionId =
        (int) $db->lastInsertId();


    /*
    |--------------------------------------------------------------------------
    | BALANCE AFTER DEPOSIT
    |--------------------------------------------------------------------------
    */

    $stmt =
        $db->prepare("
            SELECT
                COALESCE(
                    SUM(
                        CASE
                            WHEN type = 'income'
                            THEN amount
                            ELSE -amount
                        END
                    ),
                    0
                ) AS balance

            FROM transactions

            WHERE user_id = :user_id
        ");

    $stmt->execute([
        'user_id' =>
            $currentUserId
    ]);

    $balanceData =
        $stmt->fetch();

    $balance =
        (float) (
            $balanceData['balance']
            ?? 0
        );


    /*
    |--------------------------------------------------------------------------
    | COMMIT
    |--------------------------------------------------------------------------
    */

    $db->commit();


    /*
    |--------------------------------------------------------------------------
    | RESPONSE
    |--------------------------------------------------------------------------
    */

    successResponse(
        'Nạp tiền thành công.',
        [
            'transaction_id' =>
                $transactionId,

            'amount' =>
                $amount,

            'balance' =>
                $balance
        ],
        201
    );


} catch (Throwable $e) {

    if (
        $db->inTransaction()
    ) {

        $db->rollBack();
    }

    error_log(
        'Wallet deposit error: '
        . $e->getMessage()
    );

    errorResponse(
        'Không thể thực hiện nạp tiền.',
        500
    );
}