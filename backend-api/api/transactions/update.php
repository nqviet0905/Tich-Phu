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

if ($_SERVER['REQUEST_METHOD'] !== 'PUT') {

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
        'Tài khoản chưa được đồng bộ với hệ thống.',
        403
    );
}

$currentUserId =
    (int) $user['id'];


/*
|--------------------------------------------------------------------------
| BODY
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
| COMMON INPUT
|--------------------------------------------------------------------------
*/

$id =
    (int) (
        $body['id'] ?? 0
    );

$categoryId =
    (int) (
        $body['category_id'] ?? 0
    );

$note =
    isset($body['note'])
        ? trim((string) $body['note'])
        : '';


if ($id <= 0) {

    errorResponse(
        'ID giao dịch không hợp lệ.',
        400
    );
}


if ($categoryId <= 0) {

    errorResponse(
        'Danh mục không hợp lệ.',
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


try {

    /*
    |--------------------------------------------------------------------------
    | GET CURRENT TRANSACTION
    |--------------------------------------------------------------------------
    |
    | Phải lấy dữ liệu hiện tại trước khi quyết định:
    |
    | - giao dịch thường -> được sửa đầy đủ
    | - qr_payment      -> chỉ được sửa category + note
    |--------------------------------------------------------------------------
    */

    $stmt =
        $db->prepare("
            SELECT
                id,
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

            FROM transactions

            WHERE id = :id
              AND user_id = :user_id

            LIMIT 1
        ");


    $stmt->execute([
        ':id' =>
            $id,

        ':user_id' =>
            $currentUserId
    ]);


    $currentTransaction =
        $stmt->fetch(
            PDO::FETCH_ASSOC
        );


    if (!$currentTransaction) {

        errorResponse(
            'Không tìm thấy giao dịch.',
            404
        );
    }


    /*
    |--------------------------------------------------------------------------
    | CHECK QR PAYMENT
    |--------------------------------------------------------------------------
    */

    $isQrPayment =
        $currentTransaction['payment_method']
            === 'qr_payment';


    /*
    |--------------------------------------------------------------------------
    | DETERMINE TYPE
    |--------------------------------------------------------------------------
    |
    | QR:
    | luôn giữ nguyên expense.
    |
    | NORMAL:
    | lấy type client gửi lên.
    |--------------------------------------------------------------------------
    */

    if ($isQrPayment) {

        $effectiveType =
            'expense';

    } else {

        $type =
            trim(
                (string) (
                    $body['type'] ?? ''
                )
            );


        if (
            !in_array(
                $type,
                [
                    'income',
                    'expense'
                ],
                true
            )
        ) {

            errorResponse(
                'Loại giao dịch không hợp lệ.',
                400
            );
        }


        $effectiveType =
            $type;
    }


    /*
    |--------------------------------------------------------------------------
    | CHECK CATEGORY
    |--------------------------------------------------------------------------
    |
    | Danh mục phải:
    |
    | - đang hoạt động
    | - là danh mục mặc định hoặc của user
    | - đúng loại giao dịch
    |
    | QR bắt buộc category.type = expense
    |--------------------------------------------------------------------------
    */

    $categoryStmt =
        $db->prepare("
            SELECT
                id,
                name,
                type

            FROM categories

            WHERE id = :category_id
              AND type = :type
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

        ':type' =>
            $effectiveType,

        ':user_id' =>
            $currentUserId
    ]);


    $category =
        $categoryStmt->fetch(
            PDO::FETCH_ASSOC
        );


    if (!$category) {

        if ($isQrPayment) {

            errorResponse(
                'Giao dịch QR chỉ có thể sử dụng danh mục chi tiêu.',
                422
            );

        } else {

            errorResponse(
                'Danh mục không tồn tại hoặc không phù hợp với loại giao dịch.',
                422
            );
        }
    }


    /*
    |--------------------------------------------------------------------------
    | BEGIN TRANSACTION
    |--------------------------------------------------------------------------
    */

    $db->beginTransaction();


    /*
    |--------------------------------------------------------------------------
    | QR PAYMENT
    |--------------------------------------------------------------------------
    |
    | KHÔNG CHO SỬA:
    |
    | amount
    | type
    | transaction_date
    | transaction_time
    | merchant_name
    | payment_method
    | bank_account_id
    | reference_code
    |
    | CHỈ CHO SỬA:
    |
    | category_id
    | note
    |--------------------------------------------------------------------------
    */

    if ($isQrPayment) {

        $updateStmt =
            $db->prepare("
                UPDATE transactions

                SET
                    category_id = :category_id,
                    note = :note

                WHERE id = :id
                  AND user_id = :user_id
            ");


        $updateStmt->execute([
            ':category_id' =>
                $categoryId,

            ':note' =>
                $note !== ''
                    ? $note
                    : null,

            ':id' =>
                $id,

            ':user_id' =>
                $currentUserId
        ]);


        /*
        |--------------------------------------------------------------------------
        | UPDATE MERCHANT CATEGORY RULE
        |--------------------------------------------------------------------------
        |
        | Ví dụ:
        |
        | trước:
        | Highlands Coffee -> Ăn uống
        |
        | user sửa thành:
        | Highlands Coffee -> Chi khác
        |
        | lần quét tiếp theo app sẽ gợi ý Chi khác.
        |--------------------------------------------------------------------------
        */

        $merchantName =
            trim(
                (string) (
                    $currentTransaction['merchant_name']
                    ?? ''
                )
            );


        if ($merchantName !== '') {

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
                        category_id = VALUES(category_id),
                        updated_at = CURRENT_TIMESTAMP
                ");


            $ruleStmt->execute([
                ':user_id' =>
                    $currentUserId,

                ':merchant_name' =>
                    $merchantName,

                ':category_id' =>
                    $categoryId
            ]);
        }

    } else {


        /*
        |--------------------------------------------------------------------------
        | NORMAL TRANSACTION INPUT
        |--------------------------------------------------------------------------
        */

        $amount =
            $body['amount'] ?? null;


        $transactionDate =
            trim(
                (string) (
                    $body['transaction_date']
                    ?? ''
                )
            );


        /*
        |--------------------------------------------------------------------------
        | VALIDATE AMOUNT
        |--------------------------------------------------------------------------
        */

        if (
            !is_numeric($amount) ||
            (float) $amount <= 0
        ) {

            if ($db->inTransaction()) {
                $db->rollBack();
            }

            errorResponse(
                'Số tiền phải lớn hơn 0.',
                400
            );
        }


        $amount =
            (float) $amount;


        /*
        |--------------------------------------------------------------------------
        | VALIDATE DATE
        |--------------------------------------------------------------------------
        */

        $date =
            DateTime::createFromFormat(
                'Y-m-d',
                $transactionDate
            );


        if (
            !$date ||
            $date->format('Y-m-d')
                !== $transactionDate
        ) {

            if ($db->inTransaction()) {
                $db->rollBack();
            }

            errorResponse(
                'Ngày giao dịch không hợp lệ. Định dạng đúng là YYYY-MM-DD.',
                400
            );
        }


        /*
        |--------------------------------------------------------------------------
        | UPDATE NORMAL TRANSACTION
        |--------------------------------------------------------------------------
        */

        $updateStmt =
            $db->prepare("
                UPDATE transactions

                SET
                    category_id = :category_id,
                    amount = :amount,
                    type = :type,
                    transaction_date = :transaction_date,
                    note = :note

                WHERE id = :id
                  AND user_id = :user_id
            ");


        $updateStmt->execute([
            ':category_id' =>
                $categoryId,

            ':amount' =>
                $amount,

            ':type' =>
                $effectiveType,

            ':transaction_date' =>
                $transactionDate,

            ':note' =>
                $note !== ''
                    ? $note
                    : null,

            ':id' =>
                $id,

            ':user_id' =>
                $currentUserId
        ]);
    }


    /*
    |--------------------------------------------------------------------------
    | COMMIT
    |--------------------------------------------------------------------------
    */

    $db->commit();


    /*
    |--------------------------------------------------------------------------
    | GET UPDATED TRANSACTION
    |--------------------------------------------------------------------------
    */

    $resultStmt =
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

                t.updated_at

            FROM transactions t

            INNER JOIN categories c
                ON c.id = t.category_id

            WHERE t.id = :id
              AND t.user_id = :user_id

            LIMIT 1
        ");


    $resultStmt->execute([
        ':id' =>
            $id,

        ':user_id' =>
            $currentUserId
    ]);


    $updatedTransaction =
        $resultStmt->fetch(
            PDO::FETCH_ASSOC
        );


    /*
    |--------------------------------------------------------------------------
    | NORMALIZE TYPES
    |--------------------------------------------------------------------------
    */

    if ($updatedTransaction) {

        $updatedTransaction['id'] =
            (int) $updatedTransaction['id'];

        $updatedTransaction['category_id'] =
            (int) $updatedTransaction['category_id'];

        $updatedTransaction['amount'] =
            (float) $updatedTransaction['amount'];

        $updatedTransaction['bank_account_id'] =
            $updatedTransaction['bank_account_id'] !== null
                ? (int) $updatedTransaction['bank_account_id']
                : null;
    }


    /*
    |--------------------------------------------------------------------------
    | RESPONSE
    |--------------------------------------------------------------------------
    */

    successResponse(

        $isQrPayment
            ? 'Đã cập nhật danh mục và ghi chú giao dịch QR.'
            : 'Cập nhật giao dịch thành công.',

        $updatedTransaction
    );


} catch (PDOException $e) {

    /*
    |--------------------------------------------------------------------------
    | ROLLBACK
    |--------------------------------------------------------------------------
    */

    if (
        isset($db) &&
        $db->inTransaction()
    ) {

        $db->rollBack();
    }


    /*
    |--------------------------------------------------------------------------
    | LOG
    |--------------------------------------------------------------------------
    */

    error_log(
        'UPDATE TRANSACTION ERROR: '
        . $e->getMessage()
    );


    errorResponse(
        'Không thể cập nhật giao dịch.',
        500
    );

} catch (Throwable $e) {

    if (
        isset($db) &&
        $db->inTransaction()
    ) {

        $db->rollBack();
    }


    error_log(
        'UPDATE TRANSACTION ERROR: '
        . $e->getMessage()
    );


    errorResponse(
        'Không thể cập nhật giao dịch.',
        500
    );
}