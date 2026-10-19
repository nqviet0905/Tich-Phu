<?php

use Kreait\Firebase\Messaging\CloudMessage;
use Kreait\Firebase\Messaging\Notification;

class NotificationService
{
    private PDO $db;
    private $messaging;

    public function __construct(
        PDO $db,
        $messaging
    ) {
        $this->db = $db;
        $this->messaging = $messaging;
    }

    public function createBudgetNotification(
        int $userId,
        array $budgetData
    ): void {

        $status =
            $budgetData['status'];

        if ($status === 'normal') {
            return;
        }

        $categoryName =
            $budgetData['category_name'];

        $usagePercentage =
            $budgetData['usage_percentage'];

        $budgetId =
            $budgetData['budget_id'];

        if ($status === 'attention') {

            $title =
                'Ngân sách cần chú ý';

            $message =
                "Bạn đã sử dụng {$usagePercentage}% ngân sách {$categoryName}.";

        } elseif ($status === 'warning') {

            $title =
                'Cảnh báo ngân sách';

            $message =
                "Bạn đã sử dụng {$usagePercentage}% ngân sách {$categoryName}.";

        } else {

            $title =
                'Vượt ngân sách';

            $message =
                "Bạn đã sử dụng {$usagePercentage}% ngân sách {$categoryName} và đã vượt hạn mức.";
        }

        /*
         * =====================================================
         * 1. TRÁNH TẠO THÔNG BÁO TRÙNG TRONG CÙNG NGÀY
         * =====================================================
         */
        $stmt =
            $this->db->prepare("
                SELECT id
                FROM notifications
                WHERE user_id = :user_id
                  AND type = 'budget'
                  AND reference_id = :reference_id
                  AND title = :title
                  AND DATE(created_at) = CURDATE()
                LIMIT 1
            ");

        $stmt->execute([
            'user_id' =>
                $userId,

            'reference_id' =>
                $budgetId,

            'title' =>
                $title
        ]);

        if ($stmt->fetch()) {
            return;
        }

        /*
         * =====================================================
         * 2. LƯU THÔNG BÁO VÀO MYSQL
         * =====================================================
         */
        $stmt =
            $this->db->prepare("
                INSERT INTO notifications (
                    user_id,
                    title,
                    message,
                    type,
                    reference_id,
                    is_read
                )
                VALUES (
                    :user_id,
                    :title,
                    :message,
                    'budget',
                    :reference_id,
                    FALSE
                )
            ");

        $success =
            $stmt->execute([
                'user_id' =>
                    $userId,

                'title' =>
                    $title,

                'message' =>
                    $message,

                'reference_id' =>
                    $budgetId
            ]);

        if (!$success) {
            return;
        }

        /*
         * =====================================================
         * 3. GỬI PUSH NOTIFICATION QUA FIREBASE
         * =====================================================
         */
        $this->sendPushToUser(
            userId: $userId,
            title: $title,
            message: $message,
            type: 'budget',
            referenceId: $budgetId
        );
    }

    /*
     * =========================================================
     * GỬI FCM CHO TẤT CẢ THIẾT BỊ CỦA USER
     * =========================================================
     */
    private function sendPushToUser(
        int $userId,
        string $title,
        string $message,
        string $type,
        int $referenceId
    ): void {

        /*
         * Lấy FCM token của user
         */
        $stmt =
            $this->db->prepare("
                SELECT token
                FROM device_tokens
                WHERE user_id = :user_id
            ");

        $stmt->execute([
            'user_id' =>
                $userId
        ]);

        $tokens =
            $stmt->fetchAll(
                PDO::FETCH_COLUMN
            );

        if (empty($tokens)) {
            return;
        }

        /*
         * Gửi cho từng thiết bị
         */
        foreach ($tokens as $token) {

            try {

                $notification =
                    Notification::create(
                        $title,
                        $message
                    );

                $cloudMessage =
                    CloudMessage::withTarget(
                        'token',
                        $token
                    )
                    ->withNotification(
                        $notification
                    )
                    ->withData([
                        'title' =>
                            $title,

                        'message' =>
                            $message,

                        'type' =>
                            $type,

                        'reference_id' =>
                            (string)$referenceId
                    ]);

                $this->messaging
                    ->send(
                        $cloudMessage
                    );

            } catch (\Throwable $e) {

                /*
                 * Nếu FCM lỗi thì vẫn giữ
                 * notification trong database.
                 */
                error_log(
                    'FCM error: ' .
                    $e->getMessage()
                );
            }
        }
    }
}