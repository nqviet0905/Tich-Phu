<?php

function jsonResponse(
    bool $success,
    string $message,
    mixed $data = null,
    int $statusCode = 200
): void {
    http_response_code($statusCode);

    header('Content-Type: application/json; charset=UTF-8');

    echo json_encode([
        'success' => $success,
        'message' => $message,
        'data' => $data
    ], JSON_UNESCAPED_UNICODE);

    exit;
}

function successResponse(
    string $message = 'Success',
    mixed $data = null,
    int $statusCode = 200
): void {
    jsonResponse(
        true,
        $message,
        $data,
        $statusCode
    );
}

function errorResponse(
    string $message,
    int $statusCode = 400,
    mixed $data = null
): void {
    jsonResponse(
        false,
        $message,
        $data,
        $statusCode
    );
}