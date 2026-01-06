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
$new_password = trim($_POST['password'] ?? '');

/* ---------- VALIDATION ---------- */
if (empty($email) || empty($new_password)) {
    echo json_encode([
        "status" => false,
        "message" => "Email and new password are required"
    ]);
    exit;
}

/* ---------- CHECK IF CODE WAS VERIFIED ---------- */
$checkVerified = $conn->prepare(
    "SELECT id FROM clinic_verification_codes 
     WHERE email = ? AND used = 1 
     AND created_at > DATE_SUB(NOW(), INTERVAL 30 MINUTE)
     ORDER BY created_at DESC LIMIT 1"
);
$checkVerified->bind_param("s", $email);
$checkVerified->execute();
$verifiedResult = $checkVerified->get_result();

if ($verifiedResult->num_rows === 0) {
    echo json_encode([
        "status" => false,
        "message" => "Please verify your email first"
    ]);
    exit;
}

/* ---------- CHECK EMAIL EXISTS ---------- */
$check = $conn->prepare(
    "SELECT clinic_user_id FROM pet_clinic_users WHERE email = ?"
);
$check->bind_param("s", $email);
$check->execute();
$check->store_result();

if ($check->num_rows === 0) {
    echo json_encode([
        "status" => false,
        "message" => "Email not registered"
    ]);
    exit;
}

/* ---------- UPDATE PASSWORD (NO HASH) ---------- */
$update = $conn->prepare(
    "UPDATE pet_clinic_users SET password = ? WHERE email = ?"
);
$update->bind_param("ss", $new_password, $email);

if ($update->execute()) {
    // Delete used verification codes for this email
    $deleteCodes = $conn->prepare("DELETE FROM clinic_verification_codes WHERE email = ?");
    $deleteCodes->bind_param("s", $email);
    $deleteCodes->execute();
    
    echo json_encode([
        "status" => true,
        "message" => "Password updated successfully"
    ]);
} else {
    echo json_encode([
        "status" => false,
        "message" => "Failed to update password"
    ]);
}

$check->close();
$checkVerified->close();
$update->close();
$conn->close();
?>
