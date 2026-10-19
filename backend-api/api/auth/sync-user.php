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

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {

    errorResponse(
        'Phương thức không được hỗ trợ.',
        405
    );
}


/*
|--------------------------------------------------------------------------
| FIREBASE AUTH
|--------------------------------------------------------------------------
*/

$authData =
    authenticateUser();

$firebaseUid =
    trim(
        $authData['firebase_uid']
        ?? ''
    );

if ($firebaseUid === '') {

    errorResponse(
        'Không xác định được Firebase UID.',
        401
    );
}


/*
|--------------------------------------------------------------------------
| JSON BODY
|--------------------------------------------------------------------------
*/

$rawBody =
    file_get_contents(
        'php://input'
    );

$body =
    json_decode(
        $rawBody,
        true
    );

if (!is_array($body)) {

    errorResponse(
        'Dữ liệu JSON không hợp lệ.',
        400
    );
}


$name =
    trim(
        $body['name']
        ?? ''
    );

$email =
    trim(
        $body['email']
        ?? ''
    );


/*
|--------------------------------------------------------------------------
| VALIDATE
|--------------------------------------------------------------------------
*/

if ($name === '') {

    errorResponse(
        'Họ tên không được để trống.',
        400
    );
}

if (
    $email === ''
    ||
    !filter_var(
        $email,
        FILTER_VALIDATE_EMAIL
    )
) {

    errorResponse(
        'Email không hợp lệ.',
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
| GENERATE ACCOUNT NUMBER
|--------------------------------------------------------------------------
|
| Format:
|
| 1024 + 8 chữ số
|
| Ví dụ:
| user id 1 -> 102400000001
| user id 2 -> 102400000002
|
*/

function generateAccountNumber(
    PDO $db,
    int $userId
): string {

    /*
    |--------------------------------------------------------------------------
    | ƯU TIÊN DÙNG USER ID
    |--------------------------------------------------------------------------
    */

    $candidate =
        '1024'
        .
        str_pad(
            (string) $userId,
            8,
            '0',
            STR_PAD_LEFT
        );


    /*
    |--------------------------------------------------------------------------
    | KIỂM TRA TRÙNG
    |--------------------------------------------------------------------------
    */

    $stmt =
        $db->prepare("
            SELECT id
            FROM users
            WHERE account_number = :account_number
              AND id <> :user_id
            LIMIT 1
        ");

    $stmt->execute([
        'account_number' =>
            $candidate,

        'user_id' =>
            $userId
    ]);

    $exists =
        $stmt->fetch(
            PDO::FETCH_ASSOC
        );


    /*
    |--------------------------------------------------------------------------
    | KHÔNG TRÙNG
    |--------------------------------------------------------------------------
    */

    if (!$exists) {

        return $candidate;
    }


    /*
    |--------------------------------------------------------------------------
    | NẾU BỊ TRÙNG DO DỮ LIỆU CŨ
    |--------------------------------------------------------------------------
    |
    | Tìm số tiếp theo chưa được sử dụng.
    |
    */

    $stmt =
        $db->prepare("
            SELECT account_number
            FROM users
            WHERE account_number IS NOT NULL
              AND account_number LIKE '1024%'
            ORDER BY account_number DESC
            LIMIT 1
        ");

    $stmt->execute();

    $lastAccount =
        $stmt->fetchColumn();


    if (
        $lastAccount
        &&
        strlen($lastAccount) === 12
    ) {

        $lastSequence =
            (int) substr(
                $lastAccount,
                4
            );

    } else {

        $lastSequence =
            0;
    }


    /*
    |--------------------------------------------------------------------------
    | LOOP TÌM SỐ TRỐNG
    |--------------------------------------------------------------------------
    */

    for (
        $sequence =
            $lastSequence + 1;
        $sequence <= 99999999;
        $sequence++
    ) {

        $candidate =
            '1024'
            .
            str_pad(
                (string) $sequence,
                8,
                '0',
                STR_PAD_LEFT
            );

        $stmt =
            $db->prepare("
                SELECT id
                FROM users
                WHERE account_number = :account_number
                LIMIT 1
            ");

        $stmt->execute([
            'account_number' =>
                $candidate
        ]);

        if (
            !$stmt->fetch(
                PDO::FETCH_ASSOC
            )
        ) {

            return $candidate;
        }
    }


    throw new Exception(
        'Không thể tạo số tài khoản Tích Phú.'
    );
}


/*
|--------------------------------------------------------------------------
| SYNC USER
|--------------------------------------------------------------------------
*/

try {

    $db->beginTransaction();


    /*
    |--------------------------------------------------------------------------
    | TÌM THEO FIREBASE UID
    |--------------------------------------------------------------------------
    */

    $stmt =
        $db->prepare("
            SELECT
                id,
                account_number
            FROM users
            WHERE firebase_uid = :firebase_uid
            LIMIT 1
            FOR UPDATE
        ");

    $stmt->execute([
        'firebase_uid' =>
            $firebaseUid
    ]);

    $userByUid =
        $stmt->fetch(
            PDO::FETCH_ASSOC
        );


    /*
    |--------------------------------------------------------------------------
    | USER ĐÃ TỒN TẠI
    |--------------------------------------------------------------------------
    */

    if ($userByUid) {

        $userId =
            (int) $userByUid['id'];

        $stmt =
            $db->prepare("
                UPDATE users
                SET
                    name = :name,
                    email = :email
                WHERE id = :id
            ");

        $stmt->execute([
            'name' =>
                $name,

            'email' =>
                $email,

            'id' =>
                $userId
        ]);

        $message =
            'Cập nhật tài khoản thành công.';


    } else {

        /*
        |--------------------------------------------------------------------------
        | UID CHƯA CÓ -> KIỂM TRA EMAIL
        |--------------------------------------------------------------------------
        */

        $stmt =
            $db->prepare("
                SELECT
                    id,
                    account_number
                FROM users
                WHERE email = :email
                LIMIT 1
                FOR UPDATE
            ");

        $stmt->execute([
            'email' =>
                $email
        ]);

        $userByEmail =
            $stmt->fetch(
                PDO::FETCH_ASSOC
            );


        /*
        |--------------------------------------------------------------------------
        | EMAIL ĐÃ CÓ
        |--------------------------------------------------------------------------
        */

        if ($userByEmail) {

            $userId =
                (int) $userByEmail['id'];

            $stmt =
                $db->prepare("
                    UPDATE users
                    SET
                        firebase_uid = :firebase_uid,
                        name = :name,
                        email = :email
                    WHERE id = :id
                ");

            $stmt->execute([
                'firebase_uid' =>
                    $firebaseUid,

                'name' =>
                    $name,

                'email' =>
                    $email,

                'id' =>
                    $userId
            ]);

            $message =
                'Liên kết tài khoản thành công.';


        } else {

            /*
            |--------------------------------------------------------------------------
            | USER MỚI
            |--------------------------------------------------------------------------
            */

            $stmt =
                $db->prepare("
                    INSERT INTO users (
                        firebase_uid,
                        name,
                        email
                    )
                    VALUES (
                        :firebase_uid,
                        :name,
                        :email
                    )
                ");

            $stmt->execute([
                'firebase_uid' =>
                    $firebaseUid,

                'name' =>
                    $name,

                'email' =>
                    $email
            ]);

            $userId =
                (int) $db->lastInsertId();

            $message =
                'Tạo tài khoản thành công.';
        }
    }


    /*
    |--------------------------------------------------------------------------
    | KIỂM TRA ACCOUNT NUMBER
    |--------------------------------------------------------------------------
    |
    | Áp dụng cho:
    |
    | - User mới
    | - User cũ account_number = NULL
    | - User liên kết từ email cũ
    |
    */

    $stmt =
        $db->prepare("
            SELECT
                account_number
            FROM users
            WHERE id = :id
            LIMIT 1
            FOR UPDATE
        ");

    $stmt->execute([
        'id' =>
            $userId
    ]);

    $accountNumber =
        $stmt->fetchColumn();


    /*
    |--------------------------------------------------------------------------
    | TỰ TẠO NẾU CHƯA CÓ
    |--------------------------------------------------------------------------
    */

    if (
        $accountNumber === false
        ||
        $accountNumber === null
        ||
        trim(
            (string) $accountNumber
        ) === ''
    ) {

        $accountNumber =
            generateAccountNumber(
                $db,
                $userId
            );

        $stmt =
            $db->prepare("
                UPDATE users
                SET
                    account_number = :account_number
                WHERE id = :id
            ");

        $stmt->execute([
            'account_number' =>
                $accountNumber,

            'id' =>
                $userId
        ]);
    }


    /*
    |--------------------------------------------------------------------------
    | LẤY USER SAU KHI SYNC
    |--------------------------------------------------------------------------
    */

    $stmt =
        $db->prepare("
            SELECT
                id,
                firebase_uid,
                name,
                email,
                account_number,
                avatar_url,
                currency
            FROM users
            WHERE id = :id
            LIMIT 1
        ");

    $stmt->execute([
        'id' =>
            $userId
    ]);

    $user =
        $stmt->fetch(
            PDO::FETCH_ASSOC
        );


    if (!$user) {

        throw new Exception(
            'Không tìm thấy tài khoản sau khi đồng bộ.'
        );
    }


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
        $message,
        $user
    );


} catch (Throwable $e) {

    if (
        $db->inTransaction()
    ) {

        $db->rollBack();
    }


    error_log(
        'SYNC USER ERROR: '
        . $e->getMessage()
    );


    errorResponse(
    'Không thể đồng bộ tài khoản.',
    500
);
}