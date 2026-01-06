<?php
header("Content-Type: application/json");
include "db.php";

$user_id          = $_POST['user_id'] ?? '';
$pet_id           = $_POST['pet_id'] ?? '';
$service_id       = $_POST['service_id'] ?? '';
$appointment_date = $_POST['appointment_date'] ?? '';
$appointment_time = $_POST['appointment_time'] ?? '';

if (empty($user_id) || empty($pet_id) || empty($service_id)
    || empty($appointment_date) || empty($appointment_time)) {

    echo json_encode([
        "status" => false,
        "message" => "All fields are required"
    ]);
    exit;
}

$query = "INSERT INTO appointments
          (user_id, pet_id, service_id, appointment_date, appointment_time)
          VALUES (?, ?, ?, ?, ?)";

$stmt = mysqli_prepare($conn, $query);
mysqli_stmt_bind_param(
    $stmt,
    "iiiss",
    $user_id,
    $pet_id,
    $service_id,
    $appointment_date,
    $appointment_time
);

if (mysqli_stmt_execute($stmt)) {
    echo json_encode([
        "status" => true,
        "message" => "Appointment booked successfully"
    ]);
} else {
    echo json_encode([
        "status" => false,
        "message" => "Failed to book appointment"
    ]);
}

mysqli_close($conn);
?>
