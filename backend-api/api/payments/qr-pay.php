<?php

require_once __DIR__ . '/../../middleware/cors.php';
require_once __DIR__ . '/../../config/database.php';
require_once __DIR__ . '/../../middleware/auth.php';
require_once __DIR__ . '/../../helpers/response.php';


date_default_timezone_set(
    'Asia/Ho_Chi_Minh'
);


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
    $authData['user'] ?? null;


if (!$user) {

    errorResponse(
        'Không tìm thấy người dùng.',
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
| BODY
|--------------------------------------------------------------------------
*/

$data =
    json_decode(
        file_get_contents(
            'php://input'
        ),
        true
    );


if (!is_array($data)) {

    errorResponse(
        'Dữ liệu gửi lên không hợp lệ.',
        400
    );
}


/*
|--------------------------------------------------------------------------
| INPUT
|--------------------------------------------------------------------------
*/

$bankAccountId =
    (int) (
        $data['bank_account_id']
        ?? 0
    );


$merchantName =
    trim(
        (string) (
            $data['merchant_name']
            ?? ''
        )
    );


$merchantBankCode =
    strtoupper(
        trim(
            (string) (
                $data['merchant_bank_code']
                ?? ''
            )
        )
    );


$merchantAccountNumber =
    preg_replace(
        '/\s+/',
        '',
        trim(
            (string) (
                $data['merchant_account_number']
                ?? ''
            )
        )
    );


$categoryId =
    (int) (
        $data['category_id']
        ?? 0
    );


$amount =
    (float) (
        $data['amount']
        ?? 0
    );


$note =
    isset(
        $data['note']
    )
        ? trim(
            (string) $data['note']
        )
        : null;


/*
|--------------------------------------------------------------------------
| VALIDATE BANK ACCOUNT ID
|--------------------------------------------------------------------------
*/

if ($bankAccountId <= 0) {

    errorResponse(
        'Vui lòng chọn tài khoản thanh toán.',
        422
    );
}


/*
|--------------------------------------------------------------------------
| VALIDATE MERCHANT
|--------------------------------------------------------------------------
*/

if ($merchantName === '') {

    errorResponse(
        'Không xác định được cửa hàng.',
        422
    );
}


if (
    mb_strlen(
        $merchantName
    ) > 150
) {

    errorResponse(
        'Tên cửa hàng quá dài.',
        422
    );
}


/*
|--------------------------------------------------------------------------
| VALIDATE MERCHANT BANK
|--------------------------------------------------------------------------
*/

if ($merchantBankCode === '') {

    errorResponse(
        'Không xác định được ngân hàng người nhận.',
        422
    );
}


if (
    strlen(
        $merchantBankCode
    ) > 20
) {

    errorResponse(
        'Mã ngân hàng người nhận không hợp lệ.',
        422
    );
}


/*
|--------------------------------------------------------------------------
| VALIDATE RECEIVER ACCOUNT
|--------------------------------------------------------------------------
*/

if ($merchantAccountNumber === '') {

    errorResponse(
        'Không xác định được tài khoản người nhận.',
        422
    );
}


if (
    strlen(
        $merchantAccountNumber
    ) > 30
) {

    errorResponse(
        'Số tài khoản người nhận không hợp lệ.',
        422
    );
}


/*
|--------------------------------------------------------------------------
| VALIDATE CATEGORY
|--------------------------------------------------------------------------
*/

if ($categoryId <= 0) {

    errorResponse(
        'Vui lòng chọn mục đích thanh toán.',
        422
    );
}


/*
|--------------------------------------------------------------------------
| VALIDATE AMOUNT
|--------------------------------------------------------------------------
*/

if (
    !is_finite(
        $amount
    )
    ||
    $amount <= 0
) {

    errorResponse(
        'Số tiền thanh toán phải lớn hơn 0.',
        422
    );
}


/*
|--------------------------------------------------------------------------
| GIỚI HẠN SỐ TIỀN
|--------------------------------------------------------------------------
|
| DECIMAL(15,2) có giới hạn rất lớn.
| Ở mức ứng dụng cá nhân, giới hạn này giúp tránh request bất thường.
|
*/

if ($amount > 999999999999.99) {

    errorResponse(
        'Số tiền thanh toán vượt quá giới hạn cho phép.',
        422
    );
}


/*
|--------------------------------------------------------------------------
| LÀM TRÒN 2 CHỮ SỐ THẬP PHÂN
|--------------------------------------------------------------------------
*/

$amount =
    round(
        $amount,
        2
    );


/*
|--------------------------------------------------------------------------
| VALIDATE NOTE
|--------------------------------------------------------------------------
*/

if (
    $note !== null
    &&
    mb_strlen(
        $note
    ) > 255
) {

    errorResponse(
        'Ghi chú tối đa 255 ký tự.',
        422
    );
}


/*
|--------------------------------------------------------------------------
| CHECK BANK ACCOUNT
|--------------------------------------------------------------------------
|
| Tài khoản thanh toán phải:
|
| - tồn tại
| - thuộc user hiện tại
| - vẫn đang hoạt động
|
*/

$bankStmt =
    $db->prepare("
        SELECT
            id,
            bank_code,
            bank_name,
            account_number,
            account_name,
            is_default

        FROM bank_accounts

        WHERE id = :bank_account_id
          AND user_id = :user_id
          AND is_active = 1

        LIMIT 1
    ");


$bankStmt->execute([
    ':bank_account_id' =>
        $bankAccountId,

    ':user_id' =>
        $currentUserId
]);


$bankAccount =
    $bankStmt->fetch(
        PDO::FETCH_ASSOC
    );


if (!$bankAccount) {

    errorResponse(
        'Tài khoản ngân hàng nguồn không tồn tại hoặc đã bị hủy liên kết.',
        404
    );
}


/*
|--------------------------------------------------------------------------
| CHECK CATEGORY
|--------------------------------------------------------------------------
|
| Cho phép:
|
| - danh mục mặc định: user_id IS NULL
| - danh mục riêng của user hiện tại
|
| Và bắt buộc là danh mục expense.
|
*/

$categoryStmt =
    $db->prepare("
        SELECT
            id,
            name,
            type

        FROM categories

        WHERE id = :category_id
          AND type = 'expense'
          AND is_active = 1
          AND (
                user_id IS NULL
                OR user_id = :user_id
              )

        LIMIT 1
    ");


$categoryStmt->execute([
    ':category_id' =>
        $categoryId,

    ':user_id' =>
        $currentUserId
]);


$category =
    $categoryStmt->fetch(
        PDO::FETCH_ASSOC
    );


if (!$category) {

    errorResponse(
        'Danh mục chi tiêu không hợp lệ.',
        422
    );
}


/*
|--------------------------------------------------------------------------
| DATE + TIME
|--------------------------------------------------------------------------
*/

$transactionDate =
    date(
        'Y-m-d'
    );


$transactionTime =
    date(
        'H:i:s'
    );


/*
|--------------------------------------------------------------------------
| REFERENCE CODE
|--------------------------------------------------------------------------
|
| Ví dụ:
|
| TPQR202609151705301234
|
*/

$referenceCode =
    'TPQR'
    . date(
        'YmdHis'
    )
    . random_int(
        1000,
        9999
    );


try {

    /*
    |--------------------------------------------------------------------------
    | BEGIN TRANSACTION
    |--------------------------------------------------------------------------
    */

    $db->beginTransaction();


    /*
    |--------------------------------------------------------------------------
    | 1. CREATE TRANSACTION
    |--------------------------------------------------------------------------
    */

    $transactionStmt =
        $db->prepare("
            INSERT INTO transactions (

                user_id,
                category_id,
                amount,
                type,

                transaction_date,
                transaction_time,

                merchant_name,
                payment_method,

                bank_account_id,
                reference_code,

                note
            )

            VALUES (

                :user_id,
                :category_id,
                :amount,
                'expense',

                :transaction_date,
                :transaction_time,

                :merchant_name,
                'qr_payment',

                :bank_account_id,
                :reference_code,

                :note
            )
        ");


    $transactionStmt->execute([

        ':user_id' =>
            $currentUserId,

        ':category_id' =>
            $categoryId,

        ':amount' =>
            $amount,

        ':transaction_date' =>
            $transactionDate,

        ':transaction_time' =>
            $transactionTime,

        ':merchant_name' =>
            $merchantName,

        ':bank_account_id' =>
            $bankAccountId,

        ':reference_code' =>
            $referenceCode,

        ':note' =>
            (
                $note !== null
                &&
                $note !== ''
            )
                ? $note
                : null
    ]);


    $transactionId =
        (int) $db->lastInsertId();


    /*
    |--------------------------------------------------------------------------
    | KIỂM TRA TRANSACTION ID
    |--------------------------------------------------------------------------
    */

    if ($transactionId <= 0) {

        throw new Exception(
            'Không thể tạo giao dịch.'
        );
    }


    /*
    |--------------------------------------------------------------------------
    | 2. GHI NHỚ DANH MỤC THEO CỬA HÀNG
    |--------------------------------------------------------------------------
    |
    | Ví dụ:
    |
    | Highlands Coffee -> Ăn uống
    |
    | Lần quét sau endpoint suggestion có thể tự gợi ý.
    |
    */

    $ruleStmt =
        $db->prepare("
            INSERT INTO merchant_category_rules (

                user_id,
                merchant_name,
                category_id
            )

            VALUES (

                :user_id,
                :merchant_name,
                :category_id
            )

            ON DUPLICATE KEY UPDATE

                category_id =
                    VALUES(category_id),

                updated_at =
                    CURRENT_TIMESTAMP
        ");


    $ruleStmt->execute([

        ':user_id' =>
            $currentUserId,

        ':merchant_name' =>
            $merchantName,

        ':category_id' =>
            $categoryId
    ]);


    /*
    |--------------------------------------------------------------------------
    | 3. TẠO THÔNG BÁO
    |--------------------------------------------------------------------------
    |
    | Notification này sẽ:
    |
    | - xuất hiện trong màn hình Thông báo
    | - làm unread_notification_count tăng
    | - làm badge chuông trên Dashboard xuất hiện
    |
    */

    $formattedAmount =
        number_format(
            $amount,
            0,
            ',',
            '.'
        );


    $notificationTitle =
        'Thanh toán QR thành công';


    $notificationMessage =
        'Bạn đã thanh toán '
        . $formattedAmount
        . ' đ tại '
        . $merchantName
        . '.';


    $notificationStmt =
        $db->prepare("
            INSERT INTO notifications (

                user_id,
                title,
                message,
                type,
                reference_id,
                is_read,
                created_at
            )

            VALUES (

                :user_id,
                :title,
                :message,
                'system',
                :reference_id,
                0,
                CURRENT_TIMESTAMP
            )
        ");


    $notificationStmt->execute([

        ':user_id' =>
            $currentUserId,

        ':title' =>
            $notificationTitle,

        ':message' =>
            $notificationMessage,

        ':reference_id' =>
            $transactionId
    ]);


    /*
    |--------------------------------------------------------------------------
    | 4. COMMIT
    |--------------------------------------------------------------------------
    |
    | Chỉ commit khi:
    |
    | - transaction được tạo
    | - merchant rule được lưu
    | - notification được tạo
    |
    */

    $db->commit();


    /*
    |--------------------------------------------------------------------------
    | RESPONSE
    |--------------------------------------------------------------------------
    */

    successResponse(
        'Thanh toán QR thành công.',
        [

            'transaction_id' =>
                $transactionId,

            'reference_code' =>
                $referenceCode,

            'merchant_name' =>
                $merchantName,

            'category_id' =>
                $categoryId,

            'category_name' =>
                $category['name'],

            'amount' =>
                $amount,

            'payment_method' =>
                'qr_payment',

            'transaction_date' =>
                $transactionDate,

            'transaction_time' =>
                $transactionTime
        ],
        201
    );


} catch (Throwable $e) {

    /*
    |--------------------------------------------------------------------------
    | ROLLBACK
    |--------------------------------------------------------------------------
    */

    if (
        $db->inTransaction()
    ) {

        $db->rollBack();
    }


    /*
    |--------------------------------------------------------------------------
    | SERVER LOG
    |--------------------------------------------------------------------------
    */

    error_log(
        'QR PAYMENT ERROR: '
        . $e->getMessage()
    );


    /*
    |--------------------------------------------------------------------------
    | RESPONSE
    |--------------------------------------------------------------------------
    */

    errorResponse(
        'Không thể thực hiện thanh toán QR.',
        500
    );
}