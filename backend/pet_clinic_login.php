<?php
header("Content-Type: application/json");
include "db.php";

if ($_SERVER["REQUEST_METHOD"] !== "POST") {
    echo json_encode([
        "status" => false,
        "message" => "Invalid request method"
    ]);
    exit;
}

/* ---------- INPUTS ---------- */
$email = trim($_POST['email'] ?? '');
$password = trim($_POST['password'] ?? '');

/* ---------- VALIDATION ---------- */
if (empty($email) || empty($password)) {
    echo json_encode([
        "status" => false,
        "message" => "Email and password are required"
    ]);
    exit;
}

/* ---------- CHECK LOGIN ---------- */
$sql = "SELECT clinic_user_id, full_name, email, password
        FROM pet_clinic_users
        WHERE email = ? AND password = ?";

$stmt = $conn->prepare($sql);
$stmt->bind_param("ss", $email, $password);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows === 1) {
    $user = $result->fetch_assoc();

    echo json_encode([
        "status" => true,
        "message" => "Login successful",
        "clinic_user_id" => $user['clinic_user_id'],
        "full_name" => $user['full_name'],
        "email" => $user['email']
    ]);
} else {
    echo json_encode([
        "status" => false,
        "message" => "Invalid email or password"
    ]);
}

$stmt->close();
$conn->close();
?>
