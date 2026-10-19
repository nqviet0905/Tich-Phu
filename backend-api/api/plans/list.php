<?php

require_once __DIR__ . '/../../middleware/cors.php';
require_once __DIR__ . '/../../middleware/auth.php';
require_once __DIR__ . '/../../config/database.php';
require_once __DIR__ . '/../../helpers/response.php';


/*
|--------------------------------------------------------------------------
| TIMEZONE
|--------------------------------------------------------------------------
*/

date_default_timezone_set('Asia/Ho_Chi_Minh');


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
| FILTER
|--------------------------------------------------------------------------
*/

$year =
    isset($_GET['year'])
        ? (int) $_GET['year']
        : null;

$status =
    isset($_GET['status'])
        ? trim($_GET['status'])
        : null;


/*
|--------------------------------------------------------------------------
| VALIDATE FILTER
|--------------------------------------------------------------------------
*/

if (
    $year !== null &&
    (
        $year < 2000 ||
        $year > 2100
    )
) {

    errorResponse(
        'Năm không hợp lệ.',
        400
    );
}


if (
    $status !== null &&
    !in_array(
        $status,
        [
            'draft',
            'active',
            'completed',
            'cancelled'
        ],
        true
    )
) {

    errorResponse(
        'Trạng thái kế hoạch không hợp lệ.',
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
| CURRENT PERIOD
|--------------------------------------------------------------------------
*/

$currentMonth =
    (int) date('n');

$currentYear =
    (int) date('Y');

$currentPeriod =
    ($currentYear * 100) +
    $currentMonth;


/*
|--------------------------------------------------------------------------
| LOAD
|--------------------------------------------------------------------------
*/

try {

    /*
    |--------------------------------------------------------------------------
    | AUTO UPDATE STATUS
    |--------------------------------------------------------------------------
    |
    | Ví dụ:
    |
    | 202608 -> completed
    | 202609 -> active
    | 202610 -> draft
    |
    | cancelled giữ nguyên.
    |--------------------------------------------------------------------------
    */

    $stmt = $db->prepare("
        UPDATE financial_plans

        SET status =
            CASE

                WHEN (year * 100 + month) < :current_period_1
                    THEN 'completed'

                WHEN (year * 100 + month) = :current_period_2
                    THEN 'active'

                ELSE 'draft'

            END

        WHERE user_id = :user_id
          AND status != 'cancelled'
    ");


    $stmt->execute([

        'current_period_1' =>
            $currentPeriod,

        'current_period_2' =>
            $currentPeriod,

        'user_id' =>
            $currentUserId
    ]);


    /*
    |--------------------------------------------------------------------------
    | SELECT
    |--------------------------------------------------------------------------
    |
    | allocation_count:
    | Đếm số danh mục đã được phân bổ trong plan_details.
    |--------------------------------------------------------------------------
    */

    $sql = "
        SELECT

            fp.id,
            fp.plan_name,
            fp.monthly_income,
            fp.fixed_expense,
            fp.saving_target,
            fp.flexible_budget,
            fp.month,
            fp.year,
            fp.status,
            fp.created_at,
            fp.updated_at,

            (
                SELECT COUNT(DISTINCT pd.category_id)

                FROM plan_details pd

                WHERE pd.plan_id = fp.id
            ) AS allocation_count

        FROM financial_plans fp

        WHERE fp.user_id = :user_id
    ";


    $params = [

        'user_id' =>
            $currentUserId
    ];


    /*
    |--------------------------------------------------------------------------
    | YEAR FILTER
    |--------------------------------------------------------------------------
    */

    if ($year !== null) {

        $sql .= "
            AND fp.year = :year
        ";

        $params['year'] =
            $year;
    }


    /*
    |--------------------------------------------------------------------------
    | STATUS FILTER
    |--------------------------------------------------------------------------
    */

    if ($status !== null) {

        $sql .= "
            AND fp.status = :status
        ";

        $params['status'] =
            $status;
    }


    /*
    |--------------------------------------------------------------------------
    | SORT
    |--------------------------------------------------------------------------
    */

    $sql .= "
        ORDER BY
            fp.year DESC,
            fp.month DESC,
            fp.id DESC
    ";


    /*
    |--------------------------------------------------------------------------
    | EXECUTE
    |--------------------------------------------------------------------------
    */

    $stmt =
        $db->prepare(
            $sql
        );

    $stmt->execute(
        $params
    );


    $plans =
        $stmt->fetchAll();


    /*
    |--------------------------------------------------------------------------
    | RESPONSE
    |--------------------------------------------------------------------------
    */

    successResponse(
        'Lấy danh sách kế hoạch thành công.',
        $plans
    );


} catch (PDOException $e) {

    errorResponse(
        'Không thể lấy danh sách kế hoạch.',
        500
    );
}