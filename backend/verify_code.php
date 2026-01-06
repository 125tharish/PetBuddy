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

if ($_SERVER["REQUEST_METHOD"] !== "POST") {
    echo json_encode([
        "status" => false,
        "message" => "Invalid request method"
    ]);
    exit;
}

/* ---------- INPUTS ---------- */
$email = trim($_POST['email'] ?? '');
$code = trim($_POST['code'] ?? '');

/* ---------- VALIDATION ---------- */
if (empty($email) || empty($code)) {
    echo json_encode([
        "status" => false,
        "message" => "Email and verification code are required"
    ]);
    exit;
}

/* ---------- VERIFY CODE ---------- */
$verify = $conn->prepare(
    "SELECT id FROM verification_codes 
     WHERE email = ? AND code = ? AND used = 0 AND expires_at > NOW()"
);
$verify->bind_param("ss", $email, $code);
$verify->execute();
$result = $verify->get_result();

if ($result->num_rows === 0) {
    echo json_encode([
        "status" => false,
        "message" => "Invalid or expired verification code"
    ]);
    exit;
}

/* ---------- MARK CODE AS USED ---------- */
$markUsed = $conn->prepare(
    "UPDATE verification_codes SET used = 1 WHERE email = ? AND code = ?"
);
$markUsed->bind_param("ss", $email, $code);
$markUsed->execute();

echo json_encode([
    "status" => true,
    "message" => "Verification code verified successfully"
]);

$verify->close();
$markUsed->close();
$conn->close();
?>

