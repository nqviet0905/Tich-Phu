
<?php

return [
    'host' => getenv('MAIL_HOST') ?: 'smtp.gmail.com',
    'port' => (int) (getenv('MAIL_PORT') ?: 587),
    'username' => getenv('MAIL_USERNAME') ?: '',
    'password' => getenv('MAIL_PASSWORD') ?: '',
    'from_email' => getenv('MAIL_FROM_EMAIL') ?: '',
    'from_name' => getenv('MAIL_FROM_NAME') ?: 'Tích Phú',
];
