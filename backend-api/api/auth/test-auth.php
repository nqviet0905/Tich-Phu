<?php

require_once __DIR__ . '/../../middleware/cors.php';
require_once __DIR__ . '/../../middleware/auth.php';

$authData = authenticateUser();

successResponse(
    'Firebase token hợp lệ.',
    [
        'firebase_uid' => $authData['firebase_uid'],
        'user' => $authData['user']
    ]
);