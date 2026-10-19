
<?php

class Database
{
    private string $host;
    private string $db_name;
    private string $username;
    private string $password;

    public ?PDO $conn = null;

    public function __construct()
    {
        $this->host = getenv('DB_HOST') ?: 'localhost';
        $this->db_name = getenv('DB_NAME') ?: 'personal_finance';
        $this->username = getenv('DB_USERNAME') ?: 'root';
        $this->password = getenv('DB_PASSWORD') ?: '';
    }

    public function connect(): ?PDO
    {
        try {
            $this->conn = new PDO(
                "mysql:host={$this->host};dbname={$this->db_name};charset=utf8mb4",
                $this->username,
                $this->password
            );

            $this->conn->setAttribute(
                PDO::ATTR_ERRMODE,
                PDO::ERRMODE_EXCEPTION
            );

            $this->conn->setAttribute(
                PDO::ATTR_DEFAULT_FETCH_MODE,
                PDO::FETCH_ASSOC
            );

            return $this->conn;
        } catch (PDOException $e) {
            error_log('Database connection failed: ' . $e->getMessage());
            return null;
        }
    }
}
