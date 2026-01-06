<?php
header("Content-Type: application/json");
include "db.php";

$user_id = $_POST['user_id'] ?? '';

if (empty($user_id)) {
    echo json_encode([
        "status" => false,
        "message" => "user_id is required"
    ]);
    exit;
}

$query = "
    SELECT
        a.appointment_id,
        a.appointment_date,
        a.appointment_time,
        a.status,

        s.service_id,
        s.service_name,
        s.rating

    FROM appointments a
    INNER JOIN services s ON a.service_id = s.service_id
    WHERE a.user_id = ?
    ORDER BY a.appointment_date ASC, a.appointment_time ASC
";

$stmt = mysqli_prepare($conn, $query);
mysqli_stmt_bind_param($stmt, "i", $user_id);
mysqli_stmt_execute($stmt);

$result = mysqli_stmt_get_result($stmt);
$appointments = [];

while ($row = mysqli_fetch_assoc($result)) {

    $appointments[] = [
        "appointment_id" => $row['appointment_id'],
        "service_name"   => $row['service_name'],
        "rating"         => $row['rating'],
        "appointment_date" => date("M d, Y", strtotime($row['appointment_date'])),
        "appointment_time" => date("h:i A", strtotime($row['appointment_time'])),
        "status"         => $row['status']
    ];
}

echo json_encode([
    "status" => true,
    "appointments" => $appointments
]);

mysqli_close($conn);
?>
