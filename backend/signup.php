<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST, OPTIONS");
header("Access-Control-Allow-Headers: Content-Type");
include "db.php";

// Handle OPTIONS request for CORS
if ($_SERVER["REQUEST_METHOD"] === "OPTIONS") {
    http_response_code(200);
    exit();
}

// Allow only POST
if ($_SERVER["REQUEST_METHOD"] !== "POST") {
    echo json_encode([
        "status" => "error",
        "message" => "Invalid request method"
    ]);
    exit();
}

// Get inputs
$name = trim($_POST['name'] ?? '');
$email = trim($_POST['email'] ?? '');
$password = trim($_POST['password'] ?? '');
$confirmPassword = trim($_POST['confirm_password'] ?? '');

// Validation
if (empty($name) || empty($email) || empty($password) || empty($confirmPassword)) {
    echo json_encode([
        "status" => "error",
        "message" => "All fields are required"
    ]);
    exit();
}

if ($password !== $confirmPassword) {
    echo json_encode([
        "status" => "error",
        "message" => "Passwords do not match"
    ]);
    exit();
}

// Check if email already exists
$checkSql = "SELECT user_id FROM users WHERE email = ?";
$checkStmt = $conn->prepare($checkSql);
$checkStmt->bind_param("s", $email);
$checkStmt->execute();
$checkResult = $checkStmt->get_result();

if ($checkResult->num_rows > 0) {
    echo json_encode([
        "status" => "error",
        "message" => "Email already exists"
    ]);
    exit();
}

// Insert user (PLAIN password)
$sql = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, 'owner')";
$stmt = $conn->prepare($sql);
$stmt->bind_param("sss", $name, $email, $password);

if ($stmt->execute()) {
    echo json_encode([
        "status" => "success",
        "message" => "Signup successful"
    ]);
} else {
    echo json_encode([
        "status" => "error",
        "message" => "Signup failed"
    ]);
}

$stmt->close();
$conn->close();
?>
