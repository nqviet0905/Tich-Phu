<?php

require_once __DIR__ . '/../../middleware/cors.php';
require_once __DIR__ . '/../../middleware/auth.php';
require_once __DIR__ . '/../../config/database.php';
require_once __DIR__ . '/../../helpers/response.php';


/*
|--------------------------------------------------------------------------
| KIỂM TRA METHOD
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
| XÁC THỰC NGƯỜI DÙNG
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


$currentUserId =
    (int) $user['id'];


/*
|--------------------------------------------------------------------------
| LẤY THÁNG / NĂM
|--------------------------------------------------------------------------
*/

$month =
    isset($_GET['month'])
        ? (int) $_GET['month']
        : (int) date('n');


$year =
    isset($_GET['year'])
        ? (int) $_GET['year']
        : (int) date('Y');


/*
|--------------------------------------------------------------------------
| VALIDATE THÁNG
|--------------------------------------------------------------------------
*/

if (
    $month < 1 ||
    $month > 12
) {

    errorResponse(
        'Tháng không hợp lệ.',
        400
    );
}


/*
|--------------------------------------------------------------------------
| VALIDATE NĂM
|--------------------------------------------------------------------------
*/

if (
    $year < 2000 ||
    $year > 2100
) {

    errorResponse(
        'Năm không hợp lệ.',
        400
    );
}


/*
|--------------------------------------------------------------------------
| KẾT NỐI DATABASE
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
    | 1. TỔNG THU
    |--------------------------------------------------------------------------
    */

    $stmt =
        $db->prepare("
            SELECT
                COALESCE(
                    SUM(amount),
                    0
                )
            FROM transactions
            WHERE user_id = :user_id
              AND type = 'income'
              AND MONTH(transaction_date) = :month
              AND YEAR(transaction_date) = :year
        ");


    $stmt->execute([
        'user_id' =>
            $currentUserId,

        'month' =>
            $month,

        'year' =>
            $year
    ]);


    $totalIncome =
        (float) $stmt->fetchColumn();


    /*
    |--------------------------------------------------------------------------
    | 2. TỔNG CHI
    |--------------------------------------------------------------------------
    */

    $stmt =
        $db->prepare("
            SELECT
                COALESCE(
                    SUM(amount),
                    0
                )
            FROM transactions
            WHERE user_id = :user_id
              AND type = 'expense'
              AND MONTH(transaction_date) = :month
              AND YEAR(transaction_date) = :year
        ");


    $stmt->execute([
        'user_id' =>
            $currentUserId,

        'month' =>
            $month,

        'year' =>
            $year
    ]);


    $totalExpense =
        (float) $stmt->fetchColumn();


    /*
    |--------------------------------------------------------------------------
    | 3. SỐ DƯ
    |--------------------------------------------------------------------------
    */

    $balance =
        $totalIncome -
        $totalExpense;


    /*
    |--------------------------------------------------------------------------
    | 4. TỶ LỆ TIẾT KIỆM
    |--------------------------------------------------------------------------
    */

    if ($totalIncome > 0) {

        $savingRate =
            (
                (
                    $totalIncome -
                    $totalExpense
                )
                /
                $totalIncome
            )
            * 100;

    } else {

        $savingRate = 0;
    }


    /*
    |--------------------------------------------------------------------------
    | 5. SỐ GIAO DỊCH TRONG THÁNG
    |--------------------------------------------------------------------------
    */

    $stmt =
        $db->prepare("
            SELECT
                COUNT(*)
            FROM transactions
            WHERE user_id = :user_id
              AND MONTH(transaction_date) = :month
              AND YEAR(transaction_date) = :year
        ");


    $stmt->execute([
        'user_id' =>
            $currentUserId,

        'month' =>
            $month,

        'year' =>
            $year
    ]);


    $transactionCount =
        (int) $stmt->fetchColumn();


    /*
    |--------------------------------------------------------------------------
    | 6. TỔNG NGÂN SÁCH THÁNG
    |--------------------------------------------------------------------------
    */

    $stmt =
        $db->prepare("
            SELECT
                COALESCE(
                    SUM(amount),
                    0
                )
            FROM budgets
            WHERE user_id = :user_id
              AND month = :month
              AND year = :year
        ");


    $stmt->execute([
        'user_id' =>
            $currentUserId,

        'month' =>
            $month,

        'year' =>
            $year
    ]);


    $totalBudget =
        (float) $stmt->fetchColumn();


    /*
    |--------------------------------------------------------------------------
    | 7. TỶ LỆ SỬ DỤNG NGÂN SÁCH
    |--------------------------------------------------------------------------
    */

    if ($totalBudget > 0) {

        $budgetUsage =
            (
                $totalExpense /
                $totalBudget
            )
            * 100;

    } else {

        $budgetUsage = 0;
    }


    /*
    |--------------------------------------------------------------------------
    | 8. SỐ MỤC TIÊU ĐANG HOẠT ĐỘNG
    |--------------------------------------------------------------------------
    */

    $stmt =
        $db->prepare("
            SELECT
                COUNT(*)
            FROM goals
            WHERE user_id = :user_id
              AND status = 'active'
        ");


    $stmt->execute([
        'user_id' =>
            $currentUserId
    ]);


    $activeGoals =
        (int) $stmt->fetchColumn();


    /*
    |--------------------------------------------------------------------------
    | 9. MỤC TIÊU NỔI BẬT
    |--------------------------------------------------------------------------
    |
    | Lấy mục tiêu đang hoạt động có deadline gần nhất.
    |
    */

    $stmt =
        $db->prepare("
            SELECT
                id,
                name,
                current_amount,
                target_amount,
                deadline
            FROM goals
            WHERE user_id = :user_id
              AND status = 'active'
            ORDER BY
                deadline ASC,
                id ASC
            LIMIT 1
        ");


    $stmt->execute([
        'user_id' =>
            $currentUserId
    ]);


    $goal =
        $stmt->fetch(
            PDO::FETCH_ASSOC
        );


    /*
    |--------------------------------------------------------------------------
    | 10. XỬ LÝ DỮ LIỆU MỤC TIÊU
    |--------------------------------------------------------------------------
    */

    $goalName =
        null;

    $goalCurrentAmount =
        0.0;

    $goalTargetAmount =
        0.0;

    $goalProgress =
        0.0;


    if ($goal) {

        $goalName =
            $goal['name'];


        $goalCurrentAmount =
            (float) $goal['current_amount'];


        $goalTargetAmount =
            (float) $goal['target_amount'];


        if ($goalTargetAmount > 0) {

            $goalProgress =
                (
                    $goalCurrentAmount /
                    $goalTargetAmount
                )
                * 100;
        }


        /*
        |--------------------------------------------------------------------------
        | KHÔNG CHO TIẾN ĐỘ VƯỢT 100%
        |--------------------------------------------------------------------------
        */

        $goalProgress =
            min(
                $goalProgress,
                100
            );


        /*
        |--------------------------------------------------------------------------
        | KHÔNG CHO TIẾN ĐỘ NHỎ HƠN 0
        |--------------------------------------------------------------------------
        */

        $goalProgress =
            max(
                $goalProgress,
                0
            );
    }


    /*
    |--------------------------------------------------------------------------
    | 11. SỐ THÔNG BÁO CHƯA ĐỌC
    |--------------------------------------------------------------------------
    */

    $stmt =
        $db->prepare("
            SELECT
                COUNT(*)
            FROM notifications
            WHERE user_id = :user_id
              AND is_read = 0
        ");


    $stmt->execute([
        'user_id' =>
            $currentUserId
    ]);


    $unreadNotificationCount =
        (int) $stmt->fetchColumn();


    /*
    |--------------------------------------------------------------------------
    | 12. RESPONSE
    |--------------------------------------------------------------------------
    */

    successResponse(
        'Lấy thống kê tổng quan thành công.',
        [

            /*
            |--------------------------------------------------------------------------
            | THỜI GIAN
            |--------------------------------------------------------------------------
            */

            'month' =>
                $month,

            'year' =>
                $year,


            /*
            |--------------------------------------------------------------------------
            | THU CHI
            |--------------------------------------------------------------------------
            */

            'total_income' =>
                $totalIncome,

            'total_expense' =>
                $totalExpense,

            'balance' =>
                $balance,

            'saving_rate' =>
                round(
                    $savingRate,
                    2
                ),

            'transaction_count' =>
                $transactionCount,


            /*
            |--------------------------------------------------------------------------
            | NGÂN SÁCH
            |--------------------------------------------------------------------------
            */

            'total_budget' =>
                $totalBudget,

            'budget_usage_percentage' =>
                round(
                    $budgetUsage,
                    2
                ),


            /*
            |--------------------------------------------------------------------------
            | MỤC TIÊU TÀI CHÍNH
            |--------------------------------------------------------------------------
            */

            'active_goals' =>
                $activeGoals,

            'goal_name' =>
                $goalName,

            'goal_current_amount' =>
                $goalCurrentAmount,

            'goal_target_amount' =>
                $goalTargetAmount,

            'goal_progress' =>
                round(
                    $goalProgress,
                    2
                ),


            /*
            |--------------------------------------------------------------------------
            | THÔNG BÁO
            |--------------------------------------------------------------------------
            */

            'unread_notification_count' =>
                $unreadNotificationCount
        ]
    );


} catch (PDOException $e) {

    errorResponse(
        'Không thể lấy thống kê tổng quan.',
        500
    );
}