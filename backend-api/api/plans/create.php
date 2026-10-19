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

$authData = authenticateUser();

$user = $authData['user'];

if (!$user) {

    errorResponse(
        'Tài khoản chưa được đồng bộ với hệ thống.',
        403
    );
}

$currentUserId = (int) $user['id'];


/*
|--------------------------------------------------------------------------
| BODY
|--------------------------------------------------------------------------
*/

$body = json_decode(
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
| INPUT
|--------------------------------------------------------------------------
*/

$planName =
    trim(
        $body['plan_name'] ?? ''
    );

$month =
    (int) (
        $body['month'] ?? 0
    );

$year =
    (int) (
        $body['year'] ?? 0
    );

$monthlyIncome =
    $body['monthly_income'] ?? null;

$fixedExpense =
    $body['fixed_expense'] ?? null;

$savingTarget =
    $body['saving_target'] ?? null;


/*
|--------------------------------------------------------------------------
| VALIDATE MONTH / YEAR
|--------------------------------------------------------------------------
*/

if ($month < 1 || $month > 12) {

    errorResponse(
        'Tháng không hợp lệ.',
        400
    );
}

if ($year < 2000 || $year > 2100) {

    errorResponse(
        'Năm không hợp lệ.',
        400
    );
}


/*
|--------------------------------------------------------------------------
| VALIDATE MONEY
|--------------------------------------------------------------------------
*/

if (
    !is_numeric($monthlyIncome) ||
    $monthlyIncome < 0
) {

    errorResponse(
        'Thu nhập dự kiến không hợp lệ.',
        400
    );
}

if (
    !is_numeric($fixedExpense) ||
    $fixedExpense < 0
) {

    errorResponse(
        'Chi phí cố định không hợp lệ.',
        400
    );
}

if (
    !is_numeric($savingTarget) ||
    $savingTarget < 0
) {

    errorResponse(
        'Mục tiêu tiết kiệm không hợp lệ.',
        400
    );
}


/*
|--------------------------------------------------------------------------
| CAST
|--------------------------------------------------------------------------
*/

$monthlyIncome =
    (float) $monthlyIncome;

$fixedExpense =
    (float) $fixedExpense;

$savingTarget =
    (float) $savingTarget;


/*
|--------------------------------------------------------------------------
| FLEXIBLE BUDGET
|--------------------------------------------------------------------------
*/

$flexibleBudget =
    $monthlyIncome -
    $fixedExpense -
    $savingTarget;

if ($flexibleBudget < 0) {

    errorResponse(
        'Kế hoạch không khả thi vì chi phí cố định và mục tiêu tiết kiệm vượt quá thu nhập.',
        422
    );
}


/*
|--------------------------------------------------------------------------
| AUTO STATUS
|--------------------------------------------------------------------------
|
| Tháng cũ      -> completed
| Tháng hiện tại -> active
| Tháng tương lai -> draft
|--------------------------------------------------------------------------
*/

$currentMonth =
    (int) date('n');

$currentYear =
    (int) date('Y');

$currentPeriod =
    ($currentYear * 100) +
    $currentMonth;

$planPeriod =
    ($year * 100) +
    $month;


if ($planPeriod < $currentPeriod) {

    $status = 'completed';

} elseif ($planPeriod > $currentPeriod) {

    $status = 'draft';

} else {

    $status = 'active';
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
| CREATE
|--------------------------------------------------------------------------
*/

try {

    /*
    |--------------------------------------------------------------------------
    | CHECK DUPLICATE
    |--------------------------------------------------------------------------
    */

    $stmt = $db->prepare("
        SELECT id
        FROM financial_plans
        WHERE user_id = :user_id
          AND month = :month
          AND year = :year
          AND status != 'cancelled'
        LIMIT 1
    ");

    $stmt->execute([
        'user_id' =>
            $currentUserId,

        'month' =>
            $month,

        'year' =>
            $year
    ]);


    if ($stmt->fetch()) {

        errorResponse(
            'Bạn đã có kế hoạch tài chính cho tháng này.',
            409
        );
    }


    /*
    |--------------------------------------------------------------------------
    | INSERT
    |--------------------------------------------------------------------------
    */

    $stmt = $db->prepare("
        INSERT INTO financial_plans (
            user_id,
            plan_name,
            monthly_income,
            fixed_expense,
            saving_target,
            flexible_budget,
            month,
            year,
            status
        )
        VALUES (
            :user_id,
            :plan_name,
            :monthly_income,
            :fixed_expense,
            :saving_target,
            :flexible_budget,
            :month,
            :year,
            :status
        )
    ");


    $stmt->execute([

        'user_id' =>
            $currentUserId,

        'plan_name' =>
            $planName !== ''
                ? $planName
                : null,

        'monthly_income' =>
            $monthlyIncome,

        'fixed_expense' =>
            $fixedExpense,

        'saving_target' =>
            $savingTarget,

        'flexible_budget' =>
            $flexibleBudget,

        'month' =>
            $month,

        'year' =>
            $year,

        'status' =>
            $status
    ]);


    /*
    |--------------------------------------------------------------------------
    | GET CREATED PLAN
    |--------------------------------------------------------------------------
    */

    $planId =
        (int) $db->lastInsertId();


    $stmt = $db->prepare("
        SELECT
            id,
            plan_name,
            monthly_income,
            fixed_expense,
            saving_target,
            flexible_budget,
            month,
            year,
            status,
            created_at,
            updated_at
        FROM financial_plans
        WHERE id = :id
          AND user_id = :user_id
        LIMIT 1
    ");


    $stmt->execute([

        'id' =>
            $planId,

        'user_id' =>
            $currentUserId
    ]);


    $plan =
        $stmt->fetch();


    /*
    |--------------------------------------------------------------------------
    | RESPONSE
    |--------------------------------------------------------------------------
    */

    successResponse(
        'Tạo kế hoạch tài chính thành công.',
        $plan,
        201
    );


} catch (PDOException $e) {

    errorResponse(
        'Không thể tạo kế hoạch tài chính.',
        500
    );
}