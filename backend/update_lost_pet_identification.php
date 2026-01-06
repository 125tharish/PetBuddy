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

/* ---------- GET POST DATA ---------- */
$lost_id = $_POST['lost_id'] ?? '';

$has_microchip = $_POST['has_microchip'] ?? 0;
$microchip_number = $_POST['microchip_number'] ?? null;

$has_collar = $_POST['has_collar'] ?? 0;
$collar_description = $_POST['collar_description'] ?? null;

$has_id_tag = $_POST['has_id_tag'] ?? 0;
$id_tag_text = $_POST['id_tag_text'] ?? null;

/* ---------- VALIDATION ---------- */
if (empty($lost_id)) {
    echo json_encode([
        "status" => false,
        "message" => "lost_id is required"
    ]);
    exit;
}

/* ---------- UPDATE QUERY ---------- */
$sql = "UPDATE lost_pets SET
            has_microchip = ?,
            microchip_number = ?,
            has_collar = ?,
            collar_description = ?,
            has_id_tag = ?,
            id_tag_text = ?
        WHERE lost_id = ?";

$stmt = $conn->prepare($sql);

$stmt->bind_param(
    "isissii",
    $has_microchip,
    $microchip_number,
    $has_collar,
    $collar_description,
    $has_id_tag,
    $id_tag_text,
    $lost_id
);

/* ---------- EXECUTE ---------- */
if ($stmt->execute()) {
    if ($stmt->affected_rows > 0) {
        echo json_encode([
            "status" => true,
            "message" => "Identification updated successfully"
        ]);
    } else {
        echo json_encode([
            "status" => false,
            "message" => "No changes made or invalid lost_id"
        ]);
    }
} else {
    echo json_encode([
        "status" => false,
        "message" => "Update failed"
    ]);
}

$stmt->close();
$conn->close();
?>
