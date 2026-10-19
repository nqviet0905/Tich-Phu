<?php

require_once __DIR__ . '/../../middleware/cors.php';
require_once __DIR__ . '/../../config/firebase.php';
require_once __DIR__ . '/../../helpers/response.php';

use PHPMailer\PHPMailer\PHPMailer;


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
| JSON BODY
|--------------------------------------------------------------------------
*/

$rawBody =
    file_get_contents(
        'php://input'
    );

$data =
    json_decode(
        $rawBody,
        true
    );

if (!is_array($data)) {

    errorResponse(
        'Dữ liệu JSON không hợp lệ.',
        400
    );
}


/*
|--------------------------------------------------------------------------
| EMAIL
|--------------------------------------------------------------------------
*/

$email =
    strtolower(
        trim(
            $data['email']
            ?? ''
        )
    );

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
| GENERIC SUCCESS MESSAGE
|--------------------------------------------------------------------------
*/

$successMessage =
    'Nếu email đã được đăng ký, liên kết đặt lại mật khẩu sẽ được gửi đến hộp thư của bạn.';


try {

    /*
    |--------------------------------------------------------------------------
    | KIỂM TRA USER FIREBASE
    |--------------------------------------------------------------------------
    |
    | Nếu email không tồn tại vẫn trả success chung,
    | không tiết lộ tài khoản có tồn tại hay không.
    |
    */

    try {

        $firebaseAuth
            ->getUserByEmail(
                $email
            );

    } catch (Throwable $e) {

        /*
        |--------------------------------------------------------------------------
        | EMAIL KHÔNG TỒN TẠI
        |--------------------------------------------------------------------------
        */

        successResponse(
            $successMessage,
            null
        );

        exit;
    }


    /*
    |--------------------------------------------------------------------------
    | TẠO LINK RESET PASSWORD
    |--------------------------------------------------------------------------
    */

    $resetLink =
        $firebaseAuth
            ->getPasswordResetLink(
                $email
            );


    if (
        !$resetLink
        ||
        trim(
            $resetLink
        ) === ''
    ) {

        throw new Exception(
            'Không thể tạo liên kết đặt lại mật khẩu.'
        );
    }


    /*
    |--------------------------------------------------------------------------
    | MAIL CONFIG
    |--------------------------------------------------------------------------
    */

    $mailConfig =
        require __DIR__
        . '/../../config/mail.php';


    /*
    |--------------------------------------------------------------------------
    | PHPMailer
    |--------------------------------------------------------------------------
    */

    $mail =
        new PHPMailer(
            true
        );

    $mail->isSMTP();

    $mail->Host =
        $mailConfig['host'];

    $mail->SMTPAuth =
        true;

    $mail->Username =
        $mailConfig['username'];

    $mail->Password =
        str_replace(
            ' ',
            '',
            $mailConfig['password']
        );

    $mail->SMTPSecure =
        PHPMailer::ENCRYPTION_STARTTLS;

    $mail->Port =
        (int) $mailConfig['port'];

    $mail->CharSet =
        'UTF-8';


    /*
    |--------------------------------------------------------------------------
    | FROM
    |--------------------------------------------------------------------------
    */

    $mail->setFrom(
        $mailConfig['from_email'],
        $mailConfig['from_name']
    );


    /*
    |--------------------------------------------------------------------------
    | TO
    |--------------------------------------------------------------------------
    */

    $mail->addAddress(
        $email
    );


    /*
    |--------------------------------------------------------------------------
    | CONTENT
    |--------------------------------------------------------------------------
    */

    $safeResetLink =
        htmlspecialchars(
            $resetLink,
            ENT_QUOTES,
            'UTF-8'
        );

    $mail->isHTML(
        true
    );

    $mail->Subject =
        'Đặt lại mật khẩu Tích Phú';


    $mail->Body =
        '
        <!DOCTYPE html>
        <html lang="vi">

        <head>
            <meta charset="UTF-8">
        </head>

        <body
            style="
                margin:0;
                padding:0;
                background:#f6f4ff;
                font-family:Arial,sans-serif;
            "
        >

            <div
                style="
                    max-width:600px;
                    margin:30px auto;
                    background:#ffffff;
                    border-radius:20px;
                    overflow:hidden;
                    box-shadow:0 4px 20px rgba(0,0,0,0.08);
                "
            >

                <div
                    style="
                        background:#673AB7;
                        padding:28px;
                        text-align:center;
                        color:#ffffff;
                    "
                >

                    <h1
                        style="
                            margin:0;
                            font-size:28px;
                        "
                    >
                        Tích Phú
                    </h1>

                    <p
                        style="
                            margin:8px 0 0 0;
                        "
                    >
                        Quản lý tài chính cá nhân
                    </p>

                </div>


                <div
                    style="
                        padding:32px;
                        color:#28233f;
                    "
                >

                    <h2>
                        Đặt lại mật khẩu
                    </h2>

                    <p>
                        Chúng tôi nhận được yêu cầu đặt lại mật khẩu
                        cho tài khoản Tích Phú của bạn.
                    </p>

                    <p>
                        Nhấn vào nút bên dưới để tạo mật khẩu mới.
                    </p>


                    <div
                        style="
                            text-align:center;
                            margin:32px 0;
                        "
                    >

                        <a
                            href="'
                            . $safeResetLink .
                            '"
                            style="
                                display:inline-block;
                                padding:15px 28px;
                                background:#6C3BFF;
                                color:#ffffff;
                                text-decoration:none;
                                border-radius:12px;
                                font-weight:bold;
                            "
                        >
                            Đặt lại mật khẩu
                        </a>

                    </div>


                    <p
                        style="
                            font-size:14px;
                            color:#777;
                        "
                    >
                        Nếu bạn không yêu cầu đặt lại mật khẩu,
                        bạn có thể bỏ qua email này.
                    </p>

                    <p
                        style="
                            font-size:14px;
                            color:#777;
                        "
                    >
                        Không chia sẻ liên kết này cho người khác.
                    </p>

                </div>


                <div
                    style="
                        background:#f1edff;
                        padding:18px;
                        text-align:center;
                        color:#6b6480;
                        font-size:13px;
                    "
                >
                    © Tích Phú
                </div>

            </div>

        </body>

        </html>
        ';


    /*
    |--------------------------------------------------------------------------
    | TEXT FALLBACK
    |--------------------------------------------------------------------------
    */

    $mail->AltBody =
        "Đặt lại mật khẩu Tích Phú\n\n"
        .
        "Mở liên kết sau để đặt lại mật khẩu:\n"
        .
        $resetLink;


    /*
    |--------------------------------------------------------------------------
    | SEND MAIL
    |--------------------------------------------------------------------------
    */

    $mail->send();


    /*
    |--------------------------------------------------------------------------
    | RESPONSE
    |--------------------------------------------------------------------------
    */

    successResponse(
        'Đã gửi liên kết đặt lại mật khẩu đến email của bạn.',
        null
    );


} catch (Throwable $e) {

    /*
    |--------------------------------------------------------------------------
    | LOG ERROR
    |--------------------------------------------------------------------------
    */

    error_log(
        'FORGOT PASSWORD ERROR: '
        . $e->getMessage()
    );


    $errorMessage =
        $e->getMessage();


    /*
    |--------------------------------------------------------------------------
    | FIREBASE RATE LIMIT
    |--------------------------------------------------------------------------
    */

    if (
        str_contains(
            $errorMessage,
            'RESET_PASSWORD_EXCEED_LIMIT'
        )
    ) {

        errorResponse(
            'Bạn đã yêu cầu đặt lại mật khẩu quá nhiều lần. Vui lòng thử lại sau.',
            429
        );
    }


    /*
    |--------------------------------------------------------------------------
    | OTHER ERRORS
    |--------------------------------------------------------------------------
    */

    errorResponse(
        'Không thể gửi email đặt lại mật khẩu. Vui lòng thử lại sau.',
        500
    );
}