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
        'Tài khoản chưa được đồng bộ với hệ thống.',
        403
    );
}

$currentUserId = (int) $user['id'];


/*
|--------------------------------------------------------------------------
| QUERY PARAMS
|--------------------------------------------------------------------------
*/

$type =
    $_GET['type'] ?? null;


$categoryId =
    isset($_GET['category_id'])
        ? (int) $_GET['category_id']
        : null;


$month =
    isset($_GET['month'])
        ? (int) $_GET['month']
        : null;


$year =
    isset($_GET['year'])
        ? (int) $_GET['year']
        : null;


$page =
    isset($_GET['page'])
        ? max(
            1,
            (int) $_GET['page']
        )
        : 1;


$limit =
    isset($_GET['limit'])
        ? (int) $_GET['limit']
        : 20;


/*
|--------------------------------------------------------------------------
| VALIDATION
|--------------------------------------------------------------------------
*/

if (
    $limit < 1 ||
    $limit > 100
) {

    $limit = 20;
}


if (
    $type !== null &&
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


if (
    $month !== null &&
    (
        $month < 1 ||
        $month > 12
    )
) {

    errorResponse(
        'Tháng không hợp lệ.',
        400
    );
}


if (
    $year !== null &&
    $year < 2000
) {

    errorResponse(
        'Năm không hợp lệ.',
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
| GET TRANSACTIONS
|--------------------------------------------------------------------------
*/

try {

    /*
    |--------------------------------------------------------------------------
    | WHERE
    |--------------------------------------------------------------------------
    */

    $where = [
        't.user_id = :user_id'
    ];


    $params = [
        'user_id' => $currentUserId
    ];


    /*
    |--------------------------------------------------------------------------
    | TYPE
    |--------------------------------------------------------------------------
    */

    if ($type !== null) {

        $where[] =
            't.type = :type';

        $params['type'] =
            $type;
    }


    /*
    |--------------------------------------------------------------------------
    | CATEGORY
    |--------------------------------------------------------------------------
    */

    if (
        $categoryId !== null &&
        $categoryId > 0
    ) {

        $where[] =
            't.category_id = :category_id';

        $params['category_id'] =
            $categoryId;
    }


    /*
    |--------------------------------------------------------------------------
    | MONTH
    |--------------------------------------------------------------------------
    */

    if ($month !== null) {

        $where[] =
            'MONTH(t.transaction_date) = :month';

        $params['month'] =
            $month;
    }


    /*
    |--------------------------------------------------------------------------
    | YEAR
    |--------------------------------------------------------------------------
    */

    if ($year !== null) {

        $where[] =
            'YEAR(t.transaction_date) = :year';

        $params['year'] =
            $year;
    }


    $whereSql =
        implode(
            ' AND ',
            $where
        );


    /*
    |--------------------------------------------------------------------------
    | COUNT
    |--------------------------------------------------------------------------
    */

    $countSql = "
        SELECT
            COUNT(*)

        FROM transactions t

        WHERE $whereSql
    ";


    $countStmt =
        $db->prepare(
            $countSql
        );


    $countStmt->execute(
        $params
    );


    $total =
        (int) $countStmt
            ->fetchColumn();


    /*
    |--------------------------------------------------------------------------
    | PAGINATION
    |--------------------------------------------------------------------------
    */

    $offset =
        ($page - 1)
        * $limit;


    /*
    |--------------------------------------------------------------------------
    | TRANSACTION QUERY
    |--------------------------------------------------------------------------
    |
    | Có thêm:
    |
    | transaction_time
    | merchant_name
    | payment_method
    | bank_account_id
    | reference_code
    |
    |--------------------------------------------------------------------------
    */

    $sql = "
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

        WHERE $whereSql

        ORDER BY

            t.transaction_date DESC,

            CASE
                WHEN t.transaction_time IS NULL
                THEN 1
                ELSE 0
            END,

            t.transaction_time DESC,

            t.id DESC

        LIMIT :limit
        OFFSET :offset
    ";


    $stmt =
        $db->prepare(
            $sql
        );


    /*
    |--------------------------------------------------------------------------
    | BIND FILTER PARAMS
    |--------------------------------------------------------------------------
    */

    foreach (
        $params as $key => $value
    ) {

        if (
            in_array(
                $key,
                [
                    'user_id',
                    'category_id',
                    'month',
                    'year'
                ],
                true
            )
        ) {

            $stmt->bindValue(
                ':' . $key,
                (int) $value,
                PDO::PARAM_INT
            );

        } else {

            $stmt->bindValue(
                ':' . $key,
                $value,
                PDO::PARAM_STR
            );
        }
    }


    /*
    |--------------------------------------------------------------------------
    | LIMIT / OFFSET
    |--------------------------------------------------------------------------
    */

    $stmt->bindValue(
        ':limit',
        $limit,
        PDO::PARAM_INT
    );


    $stmt->bindValue(
        ':offset',
        $offset,
        PDO::PARAM_INT
    );


    /*
    |--------------------------------------------------------------------------
    | EXECUTE
    |--------------------------------------------------------------------------
    */

    $stmt->execute();


    $items =
        $stmt->fetchAll(
            PDO::FETCH_ASSOC
        );


    /*
    |--------------------------------------------------------------------------
    | NORMALIZE DATA
    |--------------------------------------------------------------------------
    */

    foreach (
        $items as &$item
    ) {

        $item['id'] =
            (int) $item['id'];


        $item['category_id'] =
            (int) $item['category_id'];


        $item['amount'] =
            (float) $item['amount'];


        $item['bank_account_id'] =
            $item['bank_account_id'] !== null
                ? (int) $item['bank_account_id']
                : null;
    }

    unset($item);


    /*
    |--------------------------------------------------------------------------
    | TOTAL PAGES
    |--------------------------------------------------------------------------
    */

    $totalPages =
        $total > 0
            ? (int) ceil(
                $total / $limit
            )
            : 0;


    /*
    |--------------------------------------------------------------------------
    | RESPONSE
    |--------------------------------------------------------------------------
    */

    successResponse(
        'Lấy danh sách giao dịch thành công.',
        [
            'items' =>
                $items,

            'pagination' => [

                'page' =>
                    $page,

                'limit' =>
                    $limit,

                'total' =>
                    $total,

                'total_pages' =>
                    $totalPages
            ]
        ]
    );

} catch (PDOException $e) {

    /*
    |--------------------------------------------------------------------------
    | DEV LOG
    |--------------------------------------------------------------------------
    */

    error_log(
        'TRANSACTION LIST ERROR: '
        . $e->getMessage()
    );


    errorResponse(
        'Không thể lấy danh sách giao dịch.',
        500
    );
}