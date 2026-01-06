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

/* ---------- INPUT FROM BODY ---------- */
$type = $_POST['type'] ?? 'today'; // today | upcoming | past | all
$today = date("Y-m-d");

/* ---------- FILTER CONDITION ---------- */
$where = "";

if ($type === "today") {
    $where = "WHERE appointment_date = ?";
} elseif ($type === "upcoming") {
    $where = "WHERE appointment_date > ?";
} elseif ($type === "past") {
    $where = "WHERE appointment_date < ?";
} else {
    $where = ""; // all
}

/* ---------- QUERY ---------- */
if ($type === "all") {
    $sql = "SELECT
                appointment_id,
                pet_id,
                service_name,
                appointment_date,
                appointment_time,
                status
            FROM appointments
            ORDER BY appointment_date, appointment_time";
    $stmt = $conn->prepare($sql);
} else {
    $sql = "SELECT
                appointment_id,
                pet_id,
                service_name,
                appointment_date,
                appointment_time,
                status
            FROM appointments
            $where
            ORDER BY appointment_date, appointment_time";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("s", $today);
}

$stmt->execute();
$result = $stmt->get_result();

$appointments = [];
while ($row = $result->fetch_assoc()) {
    $appointments[] = $row;
}

echo json_encode([
    "status" => true,
    "appointments" => $appointments
]);

$stmt->close();
$conn->close();
?>
