<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST, OPTIONS");
header("Access-Control-Allow-Headers: Content-Type");
include "db.php";

// Handle OPTIONS request for CORS
if ($_SERVER["REQUEST_METHOD"] === "OPTIONS") {
    http_response_code(200);
    exit;
}

// Allow only POST requests
if ($_SERVER["REQUEST_METHOD"] !== "POST") {
    echo json_encode([
        "status" => false,
        "message" => "Invalid request method"
    ]);
    exit;
}

// Get inputs
$email = $_POST['email'] ?? '';
$password = $_POST['password'] ?? '';

// Validate inputs
if (empty($email) || empty($password)) {
    echo json_encode([
        "status" => false,
        "message" => "Email and password required"
    ]);
    exit;
}

// Fetch user by email
$sql = "SELECT user_id, name, email, password, role FROM users WHERE email = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("s", $email);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows === 1) {

    $user = $result->fetch_assoc();

    // ✅ PLAIN PASSWORD COMPARISON
    if ($password === $user['password']) {

        echo json_encode([
            "status" => true,
            "message" => "Login successful",
            "user_id" => $user['user_id'],
            "name" => $user['name'],
            "email" => $user['email'],
            "role" => $user['role']
        ]);

    } else {
        echo json_encode([
            "status" => false,
            "message" => "Invalid password"
        ]);
    }

} else {
    echo json_encode([
        "status" => false,
        "message" => "User not found"
    ]);
}

$stmt->close();
$conn->close();
