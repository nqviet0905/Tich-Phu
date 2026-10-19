<?php

require_once __DIR__ . '/../../middleware/cors.php';
require_once __DIR__ . '/../../config/database.php';
require_once __DIR__ . '/../../middleware/auth.php';
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
        'Không tìm thấy người dùng.',
        403
    );
}

$currentUserId = (int) $user['id'];


/*
|--------------------------------------------------------------------------
| DATABASE
|--------------------------------------------------------------------------
*/

$database = new Database();

$db = $database->connect();

if (!$db) {

    errorResponse(
        'Không thể kết nối cơ sở dữ liệu.',
        500
    );
}


/*
|--------------------------------------------------------------------------
| MERCHANT NAME
|--------------------------------------------------------------------------
*/

$merchantName = trim(
    $_GET['merchant_name'] ?? ''
);

if ($merchantName === '') {

    errorResponse(
        'Tên cửa hàng không hợp lệ.',
        422
    );
}


/*
|--------------------------------------------------------------------------
| FIND CATEGORY RULE
|--------------------------------------------------------------------------
*/

$stmt = $db->prepare("
    SELECT
        mcr.category_id,
        c.name AS category_name

    FROM merchant_category_rules mcr

    INNER JOIN categories c
        ON c.id = mcr.category_id

    WHERE mcr.user_id = :user_id
      AND mcr.merchant_name = :merchant_name
      AND c.type = 'expense'
      AND c.is_active = 1

    LIMIT 1
");


$stmt->execute([
    ':user_id' => $currentUserId,
    ':merchant_name' => $merchantName
]);


$rule = $stmt->fetch();


/*
|--------------------------------------------------------------------------
| CHƯA CÓ GỢI Ý
|--------------------------------------------------------------------------
*/

if (!$rule) {

    successResponse(
        'Chưa có danh mục gợi ý cho cửa hàng này.',
        [
            'merchant_name' => $merchantName,
            'category_id' => null,
            'category_name' => null
        ],
        200
    );
}


/*
|--------------------------------------------------------------------------
| CÓ GỢI Ý
|--------------------------------------------------------------------------
*/

successResponse(
    'Đã tìm thấy danh mục gợi ý.',
    [
        'merchant_name' => $merchantName,
        'category_id' => (int) $rule['category_id'],
        'category_name' => $rule['category_name']
    ],
    200
);