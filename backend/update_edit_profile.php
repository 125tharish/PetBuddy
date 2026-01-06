<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST, OPTIONS");
header("Access-Control-Allow-Headers: Content-Type");

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

include "db.php";

$user_id = $_POST['user_id'] ?? '';
$name = $_POST['name'] ?? '';
$email = $_POST['email'] ?? '';
$phone = $_POST['phone'] ?? '';

if (empty($user_id)) {
    echo json_encode([
        "status" => false,
        "message" => "user_id is required"
    ]);
    exit;
}

if (empty($name) || empty($email)) {
    echo json_encode([
        "status" => false,
        "message" => "Name and email are required"
    ]);
    exit;
}

// Check if email is already taken by another user
$checkQuery = "SELECT user_id FROM users WHERE email = ? AND user_id != ?";
$checkStmt = $conn->prepare($checkQuery);
$checkStmt->bind_param("si", $email, $user_id);
$checkStmt->execute();
$checkResult = $checkStmt->get_result();

if ($checkResult->num_rows > 0) {
    echo json_encode([
        "status" => false,
        "message" => "Email is already taken by another user"
    ]);
    $conn->close();
    exit;
}

// Update user profile
$query = "UPDATE users SET name = ?, email = ?";
$params = [$name, $email];
$types = "ss";

// Add phone if provided (check if column exists)
if (!empty($phone)) {
    // Check if phone column exists
    $columnCheck = $conn->query("SHOW COLUMNS FROM users LIKE 'phone'");
    if ($columnCheck->num_rows > 0) {
        $query .= ", phone = ?";
        $params[] = $phone;
        $types .= "s";
    }
}

$query .= " WHERE user_id = ?";
$params[] = $user_id;
$types .= "i";

$stmt = $conn->prepare($query);
$stmt->bind_param($types, ...$params);
$stmt->execute();

if ($stmt->affected_rows > 0) {
    echo json_encode([
        "status" => true,
        "message" => "Profile updated successfully"
    ]);
} else {
    echo json_encode([
        "status" => false,
        "message" => "Failed to update profile"
    ]);
}

$conn->close();
?>
