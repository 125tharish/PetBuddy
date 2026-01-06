<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST, OPTIONS");
header("Access-Control-Allow-Headers: Content-Type");
include "db.php";

// Include PHPMailer
require_once 'mail/PHPMailer.php';
require_once 'mail/SMTP.php';
require_once 'mail/Exception.php';

use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\SMTP;
use PHPMailer\PHPMailer\Exception;

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

/* ---------- VALIDATION ---------- */
if (empty($email)) {
    echo json_encode([
        "status" => false,
        "message" => "Email is required"
    ]);
    exit;
}

/* ---------- CHECK EMAIL EXISTS ---------- */
$check = $conn->prepare(
    "SELECT user_id, name FROM users WHERE email = ?"
);
$check->bind_param("s", $email);
$check->execute();
$result = $check->get_result();

if ($result->num_rows === 0) {
    echo json_encode([
        "status" => false,
        "message" => "Email not registered"
    ]);
    exit;
}

$user = $result->fetch_assoc();
$userName = $user['name'];

/* ---------- GENERATE 6-DIGIT CODE ---------- */
$verificationCode = str_pad(rand(0, 999999), 6, '0', STR_PAD_LEFT);

/* ---------- STORE CODE IN DATABASE (with expiration - 10 minutes) ---------- */
// Create table if not exists
$createTable = "CREATE TABLE IF NOT EXISTS verification_codes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    code VARCHAR(6) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP DEFAULT (DATE_ADD(NOW(), INTERVAL 10 MINUTE)),
    used TINYINT(1) DEFAULT 0,
    INDEX idx_email (email),
    INDEX idx_code (code)
)";
$conn->query($createTable);

// Delete old codes for this email
$deleteOld = $conn->prepare("DELETE FROM verification_codes WHERE email = ? OR expires_at < NOW()");
$deleteOld->bind_param("s", $email);
$deleteOld->execute();

// Insert new code
$insertCode = $conn->prepare(
    "INSERT INTO verification_codes (email, code) VALUES (?, ?)"
);
$insertCode->bind_param("ss", $email, $verificationCode);
$insertCode->execute();

/* ---------- SEND EMAIL ---------- */
$mail = new PHPMailer(true);

try {
    // Server settings
    $mail->isSMTP();
    $mail->Host       = 'smtp.gmail.com';
    $mail->SMTPAuth   = true;
    $mail->Username   = 'siddamtharishreddy@gmail.com';
    $mail->Password   = 'isrjozwnbkbynlsl';
    $mail->SMTPSecure = PHPMailer::ENCRYPTION_STARTTLS;
    $mail->Port       = 587;
    $mail->CharSet    = 'UTF-8';

    // Recipients
    $mail->setFrom('siddamtharishreddy@gmail.com', 'PetBuddy');
    $mail->addAddress($email, $userName);

    // Content
    $mail->isHTML(true);
    $mail->Subject = 'Password Reset Verification Code';
    $mail->Body    = "
        <html>
        <head>
            <style>
                body { font-family: Arial, sans-serif; }
                .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                .header { background-color: #FF8A50; color: white; padding: 20px; text-align: center; }
                .content { padding: 30px; background-color: #f9f9f9; }
                .code { font-size: 32px; font-weight: bold; color: #FF8A50; text-align: center; padding: 20px; background-color: white; border-radius: 8px; margin: 20px 0; }
                .footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }
            </style>
        </head>
        <body>
            <div class='container'>
                <div class='header'>
                    <h2>Password Reset Verification</h2>
                </div>
                <div class='content'>
                    <p>Hello $userName,</p>
                    <p>You requested to reset your password for your PetBuddy account.</p>
                    <p>Please use the following verification code to reset your password:</p>
                    <div class='code'>$verificationCode</div>
                    <p>This code will expire in 10 minutes.</p>
                    <p>If you didn't request this password reset, please ignore this email.</p>
                </div>
                <div class='footer'>
                    <p>© 2024 PetBuddy. All rights reserved.</p>
                </div>
            </div>
        </body>
        </html>
    ";
    $mail->AltBody = "Hello $userName,\n\nYour password reset verification code is: $verificationCode\n\nThis code will expire in 10 minutes.\n\nIf you didn't request this password reset, please ignore this email.";

    $mail->send();
    
    echo json_encode([
        "status" => true,
        "message" => "Verification code sent to your email"
    ]);
} catch (Exception $e) {
    echo json_encode([
        "status" => false,
        "message" => "Failed to send email. Error: " . $mail->ErrorInfo
    ]);
}

$check->close();
$insertCode->close();
$conn->close();
?>

