<?php

require_once __DIR__ . '/../config/firebase.php';
require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../helpers/response.php';

function getAuthorizationHeader(): ?string
{
    /*
    |--------------------------------------------------------------------------
    | APACHE / PHP
    |--------------------------------------------------------------------------
    */

    if (!empty($_SERVER['HTTP_AUTHORIZATION'])) {

        return trim(
            $_SERVER['HTTP_AUTHORIZATION']
        );
    }

    /*
    |--------------------------------------------------------------------------
    | REDIRECT_HTTP_AUTHORIZATION
    |--------------------------------------------------------------------------
    */

    if (!empty($_SERVER['REDIRECT_HTTP_AUTHORIZATION'])) {

        return trim(
            $_SERVER['REDIRECT_HTTP_AUTHORIZATION']
        );
    }

    /*
    |--------------------------------------------------------------------------
    | GETALLHEADERS
    |--------------------------------------------------------------------------
    */

    if (function_exists('getallheaders')) {

        $headers =
            getallheaders();

        foreach (
            $headers as
            $key => $value
        ) {

            if (
                strtolower($key)
                ===
                'authorization'
            ) {

                return trim(
                    $value
                );
            }
        }
    }

    return null;
}


function authenticateUser(): array
{
    global $firebaseAuth;

    /*
    |--------------------------------------------------------------------------
    | AUTHORIZATION HEADER
    |--------------------------------------------------------------------------
    */

    $header =
        getAuthorizationHeader();

    if (!$header) {

        errorResponse(
            'Thiếu Authorization header.',
            401
        );
    }

    /*
    |--------------------------------------------------------------------------
    | BEARER TOKEN
    |--------------------------------------------------------------------------
    */

    if (
        !preg_match(
            '/^Bearer\s+(.+)$/i',
            $header,
            $matches
        )
    ) {

        errorResponse(
            'Authorization header không hợp lệ.',
            401
        );
    }

    $idToken =
        trim(
            $matches[1]
        );

    if ($idToken === '') {

        errorResponse(
            'Firebase ID Token trống.',
            401
        );
    }

    /*
    |--------------------------------------------------------------------------
    | VERIFY FIREBASE
    |--------------------------------------------------------------------------
    */

    try {

        $verifiedIdToken =
            $firebaseAuth
                ->verifyIdToken(
                    $idToken
                );

        $firebaseUid =
            $verifiedIdToken
                ->claims()
                ->get('sub');

    } catch (Throwable $e) {

        error_log(
            'FIREBASE AUTH ERROR: '
            . $e->getMessage()
        );

        errorResponse(
            'Phiên đăng nhập không hợp lệ hoặc đã hết hạn.',
            401
        );
    }

    if (!$firebaseUid) {

        errorResponse(
            'Không lấy được Firebase UID.',
            401
        );
    }

    /*
    |--------------------------------------------------------------------------
    | MYSQL USER
    |--------------------------------------------------------------------------
    */

    $user = null;

    try {

        $database =
            new Database();

        $db =
            $database
                ->connect();

        if ($db) {

            $stmt =
                $db->prepare("
                    SELECT
                        id,
                        firebase_uid,
                        name,
                        email,
                        avatar_url,
                        currency
                    FROM users
                    WHERE firebase_uid = :firebase_uid
                    LIMIT 1
                ");

            $stmt->execute([
                'firebase_uid' =>
                    $firebaseUid
            ]);

            $user =
                $stmt->fetch(
                    PDO::FETCH_ASSOC
                );

            if (!$user) {
                $user = null;
            }
        }

    } catch (Throwable $e) {

        /*
         * Firebase đã xác thực thành công.
         *
         * Không biến lỗi lấy user MySQL
         * thành lỗi Firebase 401.
         */

        error_log(
            'AUTH MYSQL ERROR: '
            . $e->getMessage()
        );

        $user = null;
    }

    return [
        'firebase_uid' =>
            $firebaseUid,

        'user' =>
            $user
    ];
}