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
|
| receiver = số tài khoản người nhận.
| Ví dụ Android có thể gửi:
|
| 102400000002
|
| hoặc:
|
| 1024 0000 0002
|
| Backend tự loại bỏ khoảng trắng.
|
*/

$receiver =
    preg_replace(
        '/\D/',
        '',
        $body['receiver']
        ?? ''
    );

$amount =
    $body['amount']
    ?? null;

$note =
    trim(
        $body['note']
        ?? ''
    );


/*
|--------------------------------------------------------------------------
| VALIDATE ACCOUNT NUMBER
|--------------------------------------------------------------------------
*/

if ($receiver === '') {

    errorResponse(
        'Vui lòng nhập số tài khoản người nhận.',
        400
    );
}

if (
    strlen($receiver) < 8
    || strlen($receiver) > 20
) {

    errorResponse(
        'Số tài khoản người nhận không hợp lệ.',
        400
    );
}


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
        'Số tiền chuyển phải lớn hơn 0.',
        400
    );
}

$amount =
    (float) $amount;


/*
|--------------------------------------------------------------------------
| VALIDATE NOTE
|--------------------------------------------------------------------------
*/

if (
    mb_strlen($note) > 500
) {

    errorResponse(
        'Lời nhắn không được vượt quá 500 ký tự.',
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
| TRANSFER
|--------------------------------------------------------------------------
*/

try {

    $db->beginTransaction();


    /*
    |--------------------------------------------------------------------------
    | LOCK NGƯỜI GỬI
    |--------------------------------------------------------------------------
    |
    | Khóa row người gửi trong lúc kiểm tra số dư và tạo giao dịch.
    |
    */

    $stmt =
        $db->prepare("
            SELECT
                id,
                name,
                email,
                account_number
            FROM users
            WHERE id = :id
            LIMIT 1
            FOR UPDATE
        ");

    $stmt->execute([
        'id' =>
            $currentUserId
    ]);

    $sender =
        $stmt->fetch();


    if (!$sender) {

        $db->rollBack();

        errorResponse(
            'Không tìm thấy tài khoản người gửi.',
            404
        );
    }


    /*
    |--------------------------------------------------------------------------
    | KIỂM TRA NGƯỜI GỬI CÓ SỐ TÀI KHOẢN
    |--------------------------------------------------------------------------
    */

    if (
        empty(
            $sender['account_number']
        )
    ) {

        $db->rollBack();

        errorResponse(
            'Tài khoản của bạn chưa có số tài khoản Tích Phú.',
            422
        );
    }


    /*
    |--------------------------------------------------------------------------
    | TÌM NGƯỜI NHẬN BẰNG SỐ TÀI KHOẢN
    |--------------------------------------------------------------------------
    */

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
            $receiver
    ]);

    $receiverUser =
        $stmt->fetch();


    if (!$receiverUser) {

        $db->rollBack();

        errorResponse(
            'Không tìm thấy số tài khoản người nhận.',
            404
        );
    }


    $receiverUserId =
        (int) $receiverUser['id'];


    /*
    |--------------------------------------------------------------------------
    | KHÔNG CHUYỂN CHO CHÍNH MÌNH
    |--------------------------------------------------------------------------
    */

    if (
        $receiverUserId ===
        $currentUserId
    ) {

        $db->rollBack();

        errorResponse(
            'Bạn không thể chuyển tiền cho chính mình.',
            400
        );
    }


    /*
    |--------------------------------------------------------------------------
    | TÍNH SỐ DƯ NGƯỜI GỬI
    |--------------------------------------------------------------------------
    |
    | Số dư =
    | tổng income - tổng expense
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
                            WHEN type = 'expense'
                                THEN -amount
                            ELSE 0
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


    $currentBalance =
        (float) (
            $balanceData['balance']
            ?? 0
        );


    /*
    |--------------------------------------------------------------------------
    | KIỂM TRA SỐ DƯ
    |--------------------------------------------------------------------------
    */

    if (
        $currentBalance <= 0
        || $amount >
            $currentBalance
    ) {

        $db->rollBack();

        errorResponse(
            'Số dư không đủ để thực hiện giao dịch.',
            400
        );
    }


    /*
    |--------------------------------------------------------------------------
    | CATEGORY "CHI KHÁC"
    |--------------------------------------------------------------------------
    */

    $stmt =
        $db->prepare("
            SELECT
                id
            FROM categories
            WHERE name = :name
              AND type = 'expense'
              AND user_id IS NULL
              AND is_active = TRUE
            LIMIT 1
        ");

    $stmt->execute([
        'name' =>
            'Chi khác'
    ]);

    $expenseCategory =
        $stmt->fetch();


    if (!$expenseCategory) {

        $db->rollBack();

        errorResponse(
            'Không tìm thấy danh mục Chi khác.',
            500
        );
    }


    /*
    |--------------------------------------------------------------------------
    | CATEGORY "THU KHÁC"
    |--------------------------------------------------------------------------
    */

    $stmt =
        $db->prepare("
            SELECT
                id
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

    $incomeCategory =
        $stmt->fetch();


    if (!$incomeCategory) {

        $db->rollBack();

        errorResponse(
            'Không tìm thấy danh mục Thu khác.',
            500
        );
    }


    $expenseCategoryId =
        (int) $expenseCategory['id'];

    $incomeCategoryId =
        (int) $incomeCategory['id'];


    /*
    |--------------------------------------------------------------------------
    | GHI CHÚ NGƯỜI GỬI
    |--------------------------------------------------------------------------
    */

    $receiverName =
        trim(
            $receiverUser['name']
            ?? ''
        );

    if ($receiverName === '') {

        $receiverName =
            'Người dùng Tích Phú';
    }


    $senderNote =
        'Chuyển tiền tới '
        . $receiverName
        . ' - '
        . $receiverUser['account_number'];


    if ($note !== '') {

        $senderNote .=
            ' - '
            . $note;
    }


    /*
    |--------------------------------------------------------------------------
    | GHI CHÚ NGƯỜI NHẬN
    |--------------------------------------------------------------------------
    */

    $senderName =
        trim(
            $sender['name']
            ?? ''
        );

    if ($senderName === '') {

        $senderName =
            'Người dùng Tích Phú';
    }


    $receiverNote =
        'Nhận tiền từ '
        . $senderName
        . ' - '
        . $sender['account_number'];


    if ($note !== '') {

        $receiverNote .=
            ' - '
            . $note;
    }


    /*
    |--------------------------------------------------------------------------
    | TẠO EXPENSE CHO NGƯỜI GỬI
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
                'expense',
                CURDATE(),
                :note
            )
        ");

    $stmt->execute([
        'user_id' =>
            $currentUserId,

        'category_id' =>
            $expenseCategoryId,

        'amount' =>
            $amount,

        'note' =>
            $senderNote
    ]);


    $senderTransactionId =
        (int) $db->lastInsertId();


    /*
    |--------------------------------------------------------------------------
    | TẠO INCOME CHO NGƯỜI NHẬN
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
            $receiverUserId,

        'category_id' =>
            $incomeCategoryId,

        'amount' =>
            $amount,

        'note' =>
            $receiverNote
    ]);


    $receiverTransactionId =
        (int) $db->lastInsertId();


    /*
    |--------------------------------------------------------------------------
    | TÍNH SỐ DƯ MỚI NGƯỜI GỬI
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
                            WHEN type = 'expense'
                                THEN -amount
                            ELSE 0
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

    $newBalanceData =
        $stmt->fetch();


    $newBalance =
        (float) (
            $newBalanceData['balance']
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
        'Chuyển tiền thành công.',
        [
            'transfer_id' =>
                $senderTransactionId,

            'receiver_transaction_id' =>
                $receiverTransactionId,

            'amount' =>
                $amount,

            'balance' =>
                $newBalance,

            'receiver_name' =>
                $receiverUser['name']
                ?? null,

            'receiver_account_number' =>
                $receiverUser['account_number']
                ?? null
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
        'Wallet transfer error: '
        . $e->getMessage()
    );


    errorResponse(
        'Không thể thực hiện chuyển tiền.',
        500
    );
}