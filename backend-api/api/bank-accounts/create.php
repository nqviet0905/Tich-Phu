<?php

require_once __DIR__ . '/../../middleware/cors.php';
require_once __DIR__ . '/../../config/database.php';
require_once __DIR__ . '/../../middleware/auth.php';
require_once __DIR__ . '/../../helpers/response.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    errorResponse('Phương thức không được hỗ trợ.', 405);
}

$authData = authenticateUser();
$user = $authData['user'] ?? null;

if (!$user) {
    errorResponse('Không tìm thấy người dùng.', 403);
}

$currentUserId = (int) $user['id'];

$database = new Database();
$db = $database->connect();

if (!$db) {
    errorResponse('Không thể kết nối cơ sở dữ liệu.', 500);
}

$data = json_decode(
    file_get_contents('php://input'),
    true
);

$bankCode = strtoupper(
    trim($data['bank_code'] ?? '')
);

$bankName = trim(
    $data['bank_name'] ?? ''
);

$accountNumber = preg_replace(
    '/\s+/',
    '',
    trim($data['account_number'] ?? '')
);

$accountName = trim(
    $data['account_name'] ?? ''
);

$setDefault = !empty(
    $data['is_default']
);


/*
|--------------------------------------------------------------------------
| VALIDATE
|--------------------------------------------------------------------------
*/

if ($bankCode === '') {
    errorResponse(
        'Vui lòng chọn ngân hàng.',
        422
    );
}

if ($bankName === '') {
    errorResponse(
        'Tên ngân hàng không hợp lệ.',
        422
    );
}

if ($accountNumber === '') {
    errorResponse(
        'Vui lòng nhập số tài khoản.',
        422
    );
}

if (!preg_match('/^[0-9]{6,30}$/', $accountNumber)) {
    errorResponse(
        'Số tài khoản không hợp lệ.',
        422
    );
}

if ($accountName === '') {
    errorResponse(
        'Vui lòng nhập tên chủ tài khoản.',
        422
    );
}


/*
|--------------------------------------------------------------------------
| CHECK DUPLICATE
|--------------------------------------------------------------------------
*/

$checkStmt = $db->prepare("
    SELECT id
    FROM bank_accounts
    WHERE user_id = :user_id
      AND bank_code = :bank_code
      AND account_number = :account_number
    LIMIT 1
");

$checkStmt->execute([
    ':user_id' => $currentUserId,
    ':bank_code' => $bankCode,
    ':account_number' => $accountNumber
]);

if ($checkStmt->fetch()) {
    errorResponse(
        'Tài khoản ngân hàng này đã được liên kết.',
        409
    );
}


/*
|--------------------------------------------------------------------------
| KIỂM TRA TÀI KHOẢN ĐẦU TIÊN
|--------------------------------------------------------------------------
*/

$countStmt = $db->prepare("
    SELECT COUNT(*)
    FROM bank_accounts
    WHERE user_id = :user_id
      AND is_active = 1
");

$countStmt->execute([
    ':user_id' => $currentUserId
]);

$currentCount = (int) $countStmt->fetchColumn();

/*
Nếu đây là tài khoản ngân hàng đầu tiên
thì tự đặt làm mặc định.
*/
$isDefault =
    $currentCount === 0 || $setDefault
        ? 1
        : 0;


try {

    $db->beginTransaction();


    /*
    |--------------------------------------------------------------------------
    | NẾU CHỌN MẶC ĐỊNH
    |--------------------------------------------------------------------------
    */

    if ($isDefault === 1) {

        $resetStmt = $db->prepare("
            UPDATE bank_accounts
            SET is_default = 0
            WHERE user_id = :user_id
        ");

        $resetStmt->execute([
            ':user_id' => $currentUserId
        ]);
    }


    /*
    |--------------------------------------------------------------------------
    | INSERT
    |--------------------------------------------------------------------------
    */

    $insertStmt = $db->prepare("
        INSERT INTO bank_accounts (
            user_id,
            bank_code,
            bank_name,
            account_number,
            account_name,
            is_default,
            is_active
        )
        VALUES (
            :user_id,
            :bank_code,
            :bank_name,
            :account_number,
            :account_name,
            :is_default,
            1
        )
    ");

    $insertStmt->execute([
        ':user_id' => $currentUserId,
        ':bank_code' => $bankCode,
        ':bank_name' => $bankName,
        ':account_number' => $accountNumber,
        ':account_name' => $accountName,
        ':is_default' => $isDefault
    ]);

    $bankAccountId =
        (int) $db->lastInsertId();

    $db->commit();


    successResponse(
        'Liên kết tài khoản ngân hàng thành công.',
        [
            'id' => $bankAccountId,
            'bank_code' => $bankCode,
            'bank_name' => $bankName,
            'account_number' => $accountNumber,
            'account_name' => $accountName,
            'is_default' => $isDefault === 1
        ],
        201
    );

} catch (Throwable $e) {

    if ($db->inTransaction()) {
        $db->rollBack();
    }

    errorResponse(
        'Không thể liên kết tài khoản ngân hàng.',
        500
    );
}