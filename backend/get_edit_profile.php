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

if (empty($user_id)) {
    echo json_encode([
        "status" => false,
        "message" => "user_id is required"
    ]);
    exit;
}

// Check if phone column exists
$columnCheck = $conn->query("SHOW COLUMNS FROM users LIKE 'phone'");
$hasPhoneColumn = $columnCheck->num_rows > 0;

if ($hasPhoneColumn) {
    $query = "SELECT name, email, phone FROM users WHERE user_id = ?";
} else {
    $query = "SELECT name, email FROM users WHERE user_id = ?";
}

$stmt = $conn->prepare($query);
$stmt->bind_param("i", $user_id);
$stmt->execute();

$result = $stmt->get_result();
$user = $result->fetch_assoc();

if (!$user) {
    echo json_encode([
        "status" => false,
        "message" => "User not found"
    ]);
    exit;
}

echo json_encode([
    "status" => true,
    "profile" => $user
]);

$conn->close();
?>
