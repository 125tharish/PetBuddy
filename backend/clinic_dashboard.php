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

$today = date("Y-m-d");

/* ---------------- TODAY'S APPOINTMENTS ---------------- */
$sqlToday = "
SELECT
    a.appointment_id,
    a.appointment_time,
    a.status,

    p.pet_name,
    p.breed,

    u.name AS owner_name

FROM appointments a
JOIN pets p ON p.pet_id = a.pet_id
JOIN users u ON u.user_id = p.user_id
WHERE a.appointment_date = ?
ORDER BY a.appointment_time ASC
";

$stmtToday = $conn->prepare($sqlToday);
$stmtToday->bind_param("s", $today);
$stmtToday->execute();
$resToday = $stmtToday->get_result();

$todaysAppointments = [];
while ($row = $resToday->fetch_assoc()) {
    $todaysAppointments[] = $row;
}

/* ---------------- RECENT ACTIVITY ---------------- */
$sqlRecent = "
SELECT
    p.pet_name,
    a.service_name,
    a.appointment_date,
    a.appointment_time
FROM appointments a
JOIN pets p ON p.pet_id = a.pet_id
WHERE a.status = 'completed'
ORDER BY a.paid_at DESC
LIMIT 1
";

$resRecent = $conn->query($sqlRecent);
$recentActivity = $resRecent ? $resRecent->fetch_assoc() : null;

/* ---------------- RESPONSE ---------------- */
echo json_encode([
    "status" => true,
    "date" => $today,
    "todays_appointments" => $todaysAppointments,
    "recent_activity" => $recentActivity
]);

$stmtToday->close();
$conn->close();
?>
