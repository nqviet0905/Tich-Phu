<?php

require_once __DIR__ . '/../../middleware/cors.php';
require_once __DIR__ . '/../../middleware/auth.php';
require_once __DIR__ . '/../../config/database.php';
require_once __DIR__ . '/../../helpers/response.php';

if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    errorResponse('Phương thức không được hỗ trợ.', 405);
}

$authData = authenticateUser();
$user = $authData['user'];

if (!$user) {
    errorResponse(
        'Tài khoản chưa được đồng bộ với hệ thống.',
        403
    );
}

$currentUserId = (int) $user['id'];

$year = isset($_GET['year'])
    ? (int) $_GET['year']
    : (int) date('Y');

if ($year < 2000 || $year > 2100) {
    errorResponse('Năm không hợp lệ.', 400);
}

$database = new Database();
$db = $database->connect();

if (!$db) {
    errorResponse(
        'Không thể kết nối cơ sở dữ liệu.',
        500
    );
}

try {

    $stmt = $db->prepare("
        SELECT
            MONTH(transaction_date) AS month,

            COALESCE(SUM(
                CASE
                    WHEN type = 'income'
                    THEN amount
                    ELSE 0
                END
            ), 0) AS total_income,

            COALESCE(SUM(
                CASE
                    WHEN type = 'expense'
                    THEN amount
                    ELSE 0
                END
            ), 0) AS total_expense

        FROM transactions

        WHERE user_id = :user_id
          AND YEAR(transaction_date) = :year

        GROUP BY MONTH(transaction_date)

        ORDER BY MONTH(transaction_date) ASC
    ");

    $stmt->execute([
        'user_id' => $currentUserId,
        'year' => $year
    ]);

    $rows = $stmt->fetchAll();

    // Chuyển dữ liệu database thành dạng dễ tra cứu
    $monthlyData = [];

    foreach ($rows as $row) {
        $month = (int) $row['month'];

        $monthlyData[$month] = [
            'income' => (float) $row['total_income'],
            'expense' => (float) $row['total_expense']
        ];
    }

    $result = [];

    $yearTotalIncome = 0;
    $yearTotalExpense = 0;

    // Luôn trả đủ 12 tháng
    for ($month = 1; $month <= 12; $month++) {

        $income = isset($monthlyData[$month])
            ? $monthlyData[$month]['income']
            : 0;

        $expense = isset($monthlyData[$month])
            ? $monthlyData[$month]['expense']
            : 0;

        $balance = $income - $expense;

        $savingRate = $income > 0
            ? (($income - $expense) / $income) * 100
            : 0;

        $result[] = [
            'month' => $month,
            'total_income' => $income,
            'total_expense' => $expense,
            'balance' => $balance,
            'saving_rate' => round($savingRate, 2)
        ];

        $yearTotalIncome += $income;
        $yearTotalExpense += $expense;
    }

    $yearBalance =
        $yearTotalIncome - $yearTotalExpense;

    $yearSavingRate = $yearTotalIncome > 0
        ? ($yearBalance / $yearTotalIncome) * 100
        : 0;

    successResponse(
        'Lấy thống kê theo tháng thành công.',
        [
            'year' => $year,

            'summary' => [
                'total_income' =>
                    $yearTotalIncome,

                'total_expense' =>
                    $yearTotalExpense,

                'balance' =>
                    $yearBalance,

                'saving_rate' =>
                    round($yearSavingRate, 2)
            ],

            'months' => $result
        ]
    );

} catch (PDOException $e) {

    errorResponse(
        'Không thể lấy thống kê theo tháng.',
        500
    );
}