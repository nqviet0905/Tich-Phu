
<?php

require_once __DIR__ . '/../vendor/autoload.php';

use Kreait\Firebase\Factory;

$credentialsPath = getenv('FIREBASE_CREDENTIALS');

if (!$credentialsPath || !is_file($credentialsPath)) {
    throw new RuntimeException(
        'Firebase credentials are not configured.'
    );
}

$firebaseFactory = (new Factory)
    ->withServiceAccount($credentialsPath);

$firebaseAuth = $firebaseFactory->createAuth();
