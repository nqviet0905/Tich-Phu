<?php

class BudgetService
{
    private PDO $db;

    public function __construct(PDO $db)
    {
        $this->db = $db;
    }

    public function checkBudgetAfterExpense(
        int $userId,
        int $categoryId,
        string $transactionDate
    ): ?array {
        $month = (int) date('n', strtotime($transactionDate));
        $year = (int) date('Y', strtotime($transactionDate));

        // Lấy budget tương ứng
        $stmt = $this->db->prepare("
            SELECT
                b.id,
                b.amount,
                b.warning_level,
                c.name AS category_name
            FROM budgets b
            INNER JOIN categories c
                ON c.id = b.category_id
            WHERE b.user_id = :user_id
              AND b.category_id = :category_id
              AND b.month = :month
              AND b.year = :year
            LIMIT 1
        ");

        $stmt->execute([
            'user_id' => $userId,
            'category_id' => $categoryId,
            'month' => $month,
            'year' => $year
        ]);

        $budget = $stmt->fetch();

        if (!$budget) {
            return null;
        }

        // Tính tổng chi thực tế
        $stmt = $this->db->prepare("
            SELECT COALESCE(SUM(amount), 0)
            FROM transactions
            WHERE user_id = :user_id
              AND category_id = :category_id
              AND type = 'expense'
              AND MONTH(transaction_date) = :month
              AND YEAR(transaction_date) = :year
        ");

        $stmt->execute([
            'user_id' => $userId,
            'category_id' => $categoryId,
            'month' => $month,
            'year' => $year
        ]);

        $spentAmount = (float) $stmt->fetchColumn();
        $budgetAmount = (float) $budget['amount'];

        if ($budgetAmount <= 0) {
            return null;
        }

        $usagePercentage = ($spentAmount / $budgetAmount) * 100;

        $status = 'normal';

        if ($usagePercentage >= 100) {
            $status = 'exceeded';
        } elseif ($usagePercentage >= 90) {
            $status = 'warning';
        } elseif ($usagePercentage >= 70) {
            $status = 'attention';
        }

        return [
            'budget_id' => (int) $budget['id'],
            'category_name' => $budget['category_name'],
            'budget_amount' => $budgetAmount,
            'spent_amount' => $spentAmount,
            'usage_percentage' => round($usagePercentage, 2),
            'status' => $status,
            'month' => $month,
            'year' => $year
        ];
    }
}