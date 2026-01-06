<?php
$host = "localhost";
$username = "root";
$password = "";
$database = "petbuddy_db";

$conn = new mysqli($host, $username, $password, $database);

if ($conn->connect_error) {
    echo json_encode([
        "status" => "error",
        "message" => "Database connection failed: " . $conn->connect_error
    ]);
    exit();
}
?>

