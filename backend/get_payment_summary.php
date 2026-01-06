<?php
header("Content-Type: application/json");
require_once __DIR__ . "/db.php";

/* Read JSON or form-data */
$input = json_decode(file_get_contents("php://input"), true);

if (isset($_POST['appointment_id'])) {
    $appointment_id = intval($_POST['appointment_id']);
} elseif (isset($input['appointment_id'])) {
    $appointment_id = intval($input['appointment_id']);
} else {
    echo json_encode([
        "status" => "error",
        "message" => "appointment_id is required"
    ]);
    exit;
}

/* Fetch ONLY required fields for UI */
$sql = "
SELECT
    service_name,
    appointment_date,
    appointment_time,
    total_amount,
    card_last4
FROM appointments
WHERE appointment_id = ?
";

$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $appointment_id);
$stmt->execute();
$result = $stmt->get_result();

if ($row = $result->fetch_assoc()) {
    echo json_encode([
        "status" => "success",
        "data" => $row
    ]);
} else {
    echo json_encode([
        "status" => "error",
        "message" => "Appointment not found"
    ]);
}
?>
