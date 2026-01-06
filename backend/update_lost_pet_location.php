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
$lost_id = $_POST['lost_id'] ?? '';
$last_seen_location = $_POST['last_seen_location'] ?? '';
$lost_date = $_POST['lost_date'] ?? '';
$lost_time = $_POST['lost_time'] ?? '';
$additional_notes = $_POST['additional_notes'] ?? '';

/* ---------- VALIDATION ---------- */
if (empty($lost_id) || empty($last_seen_location) || empty($lost_date)) {
    echo json_encode([
        "status" => false,
        "message" => "lost_id, location and date are required"
    ]);
    exit;
}

/* ---------- UPDATE QUERY ---------- */
$sql = "UPDATE lost_pets SET
            last_seen_location = ?,
            lost_date = ?,
            lost_time = ?,
            additional_notes = ?
        WHERE lost_id = ?";

$stmt = $conn->prepare($sql);
$stmt->bind_param(
    "ssssi",
    $last_seen_location,
    $lost_date,
    $lost_time,
    $additional_notes,
    $lost_id
);

/* ---------- EXECUTE ---------- */
if ($stmt->execute()) {
    echo json_encode([
        "status" => true,
        "message" => "Location details updated successfully"
    ]);
} else {
    echo json_encode([
        "status" => false,
        "message" => "Update failed"
    ]);
}

$stmt->close();
$conn->close();
?>
