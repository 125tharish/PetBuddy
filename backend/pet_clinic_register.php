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
$full_name = trim($_POST['full_name'] ?? '');
$email = trim($_POST['email'] ?? '');
$password = trim($_POST['password'] ?? '');
$confirm_password = trim($_POST['confirm_password'] ?? '');

/* ---------- VALIDATION ---------- */
if (empty($full_name) || empty($email) || empty($password) || empty($confirm_password)) {
    echo json_encode([
        "status" => false,
        "message" => "All fields are required"
    ]);
    exit;
}

if ($password !== $confirm_password) {
    echo json_encode([
        "status" => false,
        "message" => "Passwords do not match"
    ]);
    exit;
}

/* ---------- CHECK EMAIL ---------- */
$check = $conn->prepare(
    "SELECT clinic_user_id FROM pet_clinic_users WHERE email = ?"
);
$check->bind_param("s", $email);
$check->execute();
$check->store_result();

if ($check->num_rows > 0) {
    echo json_encode([
        "status" => false,
        "message" => "Email already registered"
    ]);
    exit;
}

/* ---------- INSERT (NO HASH) ---------- */
$sql = "INSERT INTO pet_clinic_users (full_name, email, password)
        VALUES (?, ?, ?)";

$stmt = $conn->prepare($sql);
$stmt->bind_param("sss", $full_name, $email, $password);

if ($stmt->execute()) {
    echo json_encode([
        "status" => true,
        "message" => "Pet clinic account created successfully",
        "clinic_user_id" => $stmt->insert_id
    ]);
} else {
    echo json_encode([
        "status" => false,
        "message" => "Registration failed"
    ]);
}

$stmt->close();
$conn->close();
?>
