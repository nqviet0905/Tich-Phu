<?php

require_once __DIR__ . '/../../config/database.php';
require_once __DIR__ . '/../../config/firebase.php';
require_once __DIR__ . '/../../middleware/auth.php';
require_once __DIR__ . '/../../helpers/response.php';

require_once __DIR__ . '/../../services/BudgetService.php';
require_once __DIR__ . '/../../services/NotificationService.php';

use Kreait\Firebase\Factory;


/*
|--------------------------------------------------------------------------
| KIỂM TRA METHOD
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
| XÁC THỰC USER
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
| ĐỌC JSON BODY
|--------------------------------------------------------------------------
*/

$body =
    json_decode(
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
| LẤY DỮ LIỆU
|--------------------------------------------------------------------------
*/

$categoryId =
    (int) (
        $body['category_id']
        ?? 0
    );

$type =
    trim(
        $body['type']
        ?? ''
    );

$amount =
    $body['amount']
    ?? null;

$transactionDate =
    trim(
        $body['transaction_date']
        ?? ''
    );

$note =
    trim(
        $body['note']
        ?? ''
    );


/*
|--------------------------------------------------------------------------
| VALIDATE
|--------------------------------------------------------------------------
*/

if ($categoryId <= 0) {

    errorResponse(
        'Danh mục không hợp lệ.',
        400
    );
}


if (
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
    !is_numeric($amount)
    || $amount <= 0
) {

    errorResponse(
        'Số tiền phải lớn hơn 0.',
        400
    );
}


$date =
    DateTime::createFromFormat(
        'Y-m-d',
        $transactionDate
    );

if (
    !$date
    || $date->format('Y-m-d')
        !== $transactionDate
) {

    errorResponse(
        'Ngày giao dịch không hợp lệ. Định dạng đúng là YYYY-MM-DD.',
        400
    );
}


if (
    mb_strlen($note) > 500
) {

    errorResponse(
        'Ghi chú không được vượt quá 500 ký tự.',
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
| FIREBASE MESSAGING
|--------------------------------------------------------------------------
|
| Tạo Firebase Messaging để NotificationService
| có thể gửi FCM push xuống Android.
|
*/


try {
    $credentialsPath = getenv('FIREBASE_CREDENTIALS');

    if (!$credentialsPath || !is_file($credentialsPath)) {
        throw new RuntimeException(
            'Firebase credentials are not configured.'
        );
    }

    $firebaseFactory = (new Factory)
        ->withServiceAccount($credentialsPath);

    $messaging = $firebaseFactory->createMessaging();

} catch (Throwable $e) {
    /*
     * Không dừng API tạo giao dịch nếu
     * Firebase gặp vấn đề.
     * Ghi lỗi vào Apache/PHP log.
     */
    error_log(
        'Firebase Messaging init error: '
        . $e->getMessage()
    );

    $messaging = null;
}


/*
|--------------------------------------------------------------------------
| TẠO GIAO DỊCH
|--------------------------------------------------------------------------
*/

try {

    /*
    |--------------------------------------------------------------------------
    | KIỂM TRA CATEGORY
    |--------------------------------------------------------------------------
    */

    $stmt =
        $db->prepare("
            SELECT
                id,
                user_id,
                type,
                is_active
            FROM categories
            WHERE id = :category_id
              AND is_active = TRUE
              AND (
                    user_id IS NULL
                    OR user_id = :user_id
              )
            LIMIT 1
        ");

    $stmt->execute([
        'category_id' =>
            $categoryId,

        'user_id' =>
            $currentUserId
    ]);

    $category =
        $stmt->fetch();


    if (!$category) {

        errorResponse(
            'Không tìm thấy danh mục hoặc bạn không có quyền sử dụng danh mục này.',
            404
        );
    }


    if (
        $category['type']
        !== $type
    ) {

        errorResponse(
            'Loại giao dịch không khớp với loại danh mục.',
            422
        );
    }


    /*
    |--------------------------------------------------------------------------
    | INSERT TRANSACTION
    |--------------------------------------------------------------------------
    */

    $stmt =
        $db->prepare("
            INSERT INTO transactions (
                user_id,
                category_id,
                amount,
                type,
                transaction_date,
                note
            )
            VALUES (
                :user_id,
                :category_id,
                :amount,
                :type,
                :transaction_date,
                :note
            )
        ");


    $stmt->execute([
        'user_id' =>
            $currentUserId,

        'category_id' =>
            $categoryId,

        'amount' =>
            $amount,

        'type' =>
            $type,

        'transaction_date' =>
            $transactionDate,

        'note' =>
            $note !== ''
                ? $note
                : null
    ]);


    $transactionId =
        (int) $db->lastInsertId();


    /*
    |--------------------------------------------------------------------------
    | KIỂM TRA NGÂN SÁCH
    |--------------------------------------------------------------------------
    */

    $budgetWarning =
        null;


    if ($type === 'expense') {

        $budgetService =
            new BudgetService(
                $db
            );


        $budgetWarning =
            $budgetService
                ->checkBudgetAfterExpense(
                    $currentUserId,
                    $categoryId,
                    $transactionDate
                );


        /*
        |--------------------------------------------------------------------------
        | TẠO NOTIFICATION
        |--------------------------------------------------------------------------
        */

        if (
            $budgetWarning !== null
            && $budgetWarning['status']
                !== 'normal'
        ) {

            try {

                /*
                 * Chỉ tạo service khi
                 * Firebase Messaging khởi tạo được.
                 */

                if ($messaging !== null) {

                    $notificationService =
                        new NotificationService(
                            $db,
                            $messaging
                        );


                    $notificationService
                        ->createBudgetNotification(
                            $currentUserId,
                            $budgetWarning
                        );
                }

            } catch (Throwable $e) {

                /*
                 * Notification lỗi
                 * không làm giao dịch thất bại.
                 */

                error_log(
                    'Notification error: '
                    . $e->getMessage()
                );
            }
        }
    }


    /*
    |--------------------------------------------------------------------------
    | LẤY GIAO DỊCH VỪA TẠO
    |--------------------------------------------------------------------------
    */

    $stmt =
        $db->prepare("
            SELECT
                t.id,
                t.category_id,
                c.name
                    AS category_name,
                t.amount,
                t.type,
                t.transaction_date,
                t.note,
                t.created_at,
                t.updated_at
            FROM transactions t
            INNER JOIN categories c
                ON c.id = t.category_id
            WHERE t.id = :id
              AND t.user_id = :user_id
            LIMIT 1
        ");


    $stmt->execute([
        'id' =>
            $transactionId,

        'user_id' =>
            $currentUserId
    ]);


    $transaction =
        $stmt->fetch();


    /*
    |--------------------------------------------------------------------------
    | RESPONSE
    |--------------------------------------------------------------------------
    */

    successResponse(
        'Tạo giao dịch thành công.',
        [
            'transaction' =>
                $transaction,

            'budget_warning' =>
                $budgetWarning
        ],
        201
    );


} catch (PDOException $e) {

    error_log(
        'Create transaction DB error: '
        . $e->getMessage()
    );

    errorResponse(
        'Không thể tạo giao dịch.',
        500
    );
}