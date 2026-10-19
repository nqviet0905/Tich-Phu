<?php

require_once __DIR__ . '/../../middleware/cors.php';
require_once __DIR__ . '/../../config/database.php';
require_once __DIR__ . '/../../middleware/auth.php';
require_once __DIR__ . '/../../helpers/response.php';

if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    errorResponse(
        'Phương thức không được hỗ trợ.',
        405
    );
}

$authData = authenticateUser();
$user = $authData['user'] ?? null;

if (!$user) {
    errorResponse(
        'Không tìm thấy người dùng.',
        403
    );
}

$currentUserId =
    (int) $user['id'];

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
| LOAD BANK ACCOUNTS
|--------------------------------------------------------------------------
*/

$stmt = $db->prepare("
    SELECT
        id,
        bank_code,
        bank_name,
        account_number,
        account_name,
        is_default,
        is_active,
        created_at
    FROM bank_accounts
    WHERE user_id = :user_id
      AND is_active = 1
    ORDER BY
        is_default DESC,
        created_at DESC
");

$stmt->execute([
    ':user_id' => $currentUserId
]);

$accounts =
    $stmt->fetchAll();


/*
|--------------------------------------------------------------------------
| ADD MASKED ACCOUNT NUMBER
|--------------------------------------------------------------------------
*/

$result = [];

foreach ($accounts as $account) {

    $number =
        (string) $account['account_number'];

    $lastFour =
        substr($number, -4);

    $account['masked_account_number'] =
        '•••• ' . $lastFour;

    $account['id'] =
        (int) $account['id'];

    $account['is_default'] =
        (bool) $account['is_default'];

    $account['is_active'] =
        (bool) $account['is_active'];

    $result[] =
        $account;
}


successResponse(
    'Lấy danh sách tài khoản ngân hàng thành công.',
    $result,
    200
);