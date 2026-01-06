<?php
header("Content-Type: application/json");
include "db.php";

$appointment_id = $_POST['appointment_id'] ?? '';
$user_id        = $_POST['user_id'] ?? '';
$payment_method = $_POST['payment_method'] ?? 'CARD';
$card_last4     = $_POST['card_last4'] ?? '';

if (empty($appointment_id) || empty($user_id) || empty($card_last4)) {
    echo json_encode([
        "status" => false,
        "message" => "appointment_id, user_id, and card_last4 are required"
    ]);
    exit;
}

$query = "UPDATE appointments
          SET payment_status = 'PAID',
              payment_method = ?,
              card_last4 = ?,
              paid_at = NOW()
          WHERE appointment_id = ?
            AND user_id = ?";

$stmt = mysqli_prepare($conn, $query);
mysqli_stmt_bind_param(
    $stmt,
    "ssii",
    $payment_method,
    $card_last4,
    $appointment_id,
    $user_id
);

if (mysqli_stmt_execute($stmt) && mysqli_stmt_affected_rows($stmt) > 0) {
    echo json_encode([
        "status" => true,
        "message" => "Payment successful"
    ]);
} else {
    echo json_encode([
        "status" => false,
        "message" => "Payment failed or unauthorized"
    ]);
}

mysqli_close($conn);
?>
