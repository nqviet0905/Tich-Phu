<?php

require_once __DIR__ . '/middleware/cors.php';
require_once __DIR__ . '/config/database.php';
require_once __DIR__ . '/helpers/response.php';

$database = new Database();
$db = $database->connect();

if (!$db) {
    errorResponse(
        'Không thể kết nối MySQL',
        500
    );
}

successResponse(
    'Personal Finance API đang hoạt động',
    [
        'database' => 'personal_finance',
        'version' => '1.0.0'
    ]
);