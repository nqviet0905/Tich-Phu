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
    errorResponse('Tài khoản chưa được đồng bộ với hệ thống.', 403);
}

$currentUserId = (int) $user['id'];

$month = isset($_GET['month']) ? (int) $_GET['month'] : (int) date('n');
$year = isset($_GET['year']) ? (int) $_GET['year'] : (int) date('Y');

if ($month < 1 || $month > 12) {
    errorResponse('Tháng không hợp lệ.', 400);
}

if ($year < 2000 || $year > 2100) {
    errorResponse('Năm không hợp lệ.', 400);
}

$database = new Database();
$db = $database->connect();

if (!$db) {
    errorResponse('Không thể kết nối cơ sở dữ liệu.', 500);
}

try {
    $sql = "
        SELECT
            b.id,
            b.category_id,
            c.name AS category_name,
            c.icon AS category_icon,
            b.amount AS budget_amount,
            b.month,
            b.year,
            b.warning_level,

            COALESCE(SUM(t.amount), 0) AS spent_amount

        FROM budgets b

        INNER JOIN categories c
            ON c.id = b.category_id

        LEFT JOIN transactions t
            ON t.user_id = b.user_id
            AND t.category_id = b.category_id
            AND t.type = 'expense'
            AND MONTH(t.transaction_date) = b.month
            AND YEAR(t.transaction_date) = b.year

        WHERE b.user_id = :user_id
          AND b.month = :month
          AND b.year = :year

        GROUP BY
            b.id,
            b.category_id,
            c.name,
            c.icon,
            b.amount,
            b.month,
            b.year,
            b.warning_level

        ORDER BY c.name ASC
    ";

    $stmt = $db->prepare($sql);

    $stmt->execute([
        'user_id' => $currentUserId,
        'month' => $month,
        'year' => $year
    ]);

    $budgets = $stmt->fetchAll();

    $items = [];

    $totalBudget = 0;
    $totalSpent = 0;

    foreach ($budgets as $budget) {

        $budgetAmount = (float) $budget['budget_amount'];
        $spentAmount = (float) $budget['spent_amount'];

        $remainingAmount = $budgetAmount - $spentAmount;

        $usagePercentage = $budgetAmount > 0
            ? ($spentAmount / $budgetAmount) * 100
            : 0;

        if ($usagePercentage < 70) {
            $status = 'normal';
        } elseif ($usagePercentage < 90) {
            $status = 'attention';
        } elseif ($usagePercentage <= 100) {
            $status = 'warning';
        } else {
            $status = 'exceeded';
        }

        $items[] = [
            'id' => (int) $budget['id'],
            'category_id' => (int) $budget['category_id'],
            'category_name' => $budget['category_name'],
            'category_icon' => $budget['category_icon'],

            'budget_amount' => $budgetAmount,
            'spent_amount' => $spentAmount,
            'remaining_amount' => $remainingAmount,

            'usage_percentage' => round($usagePercentage, 2),
            'status' => $status,

            'warning_level' => (int) $budget['warning_level'],
            'month' => (int) $budget['month'],
            'year' => (int) $budget['year']
        ];

        $totalBudget += $budgetAmount;
        $totalSpent += $spentAmount;
    }

    $totalRemaining = $totalBudget - $totalSpent;

    $totalUsagePercentage = $totalBudget > 0
        ? ($totalSpent / $totalBudget) * 100
        : 0;

    successResponse(
        'Lấy danh sách ngân sách thành công.',
        [
            'month' => $month,
            'year' => $year,

            'summary' => [
                'total_budget' => $totalBudget,
                'total_spent' => $totalSpent,
                'total_remaining' => $totalRemaining,
                'usage_percentage' => round($totalUsagePercentage, 2)
            ],

            'items' => $items
        ]
    );

} catch (PDOException $e) {
    errorResponse(
        'Không thể lấy danh sách ngân sách.',
        500
    );
}