<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST, GET, OPTIONS");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");
include "db.php";

// Handle OPTIONS request for CORS preflight
if ($_SERVER["REQUEST_METHOD"] === "OPTIONS") {
    http_response_code(200);
    exit();
}

if ($_SERVER['REQUEST_METHOD'] == "POST") {

    $user_id  = $_POST['user_id'] ?? '';
    $pet_id   = $_POST['pet_id'] ?? '';
    $pet_name = $_POST['pet_name'] ?? '';

    if (empty($user_id) || empty($pet_id) || empty($pet_name)) {
        echo json_encode([
            "status" => false,
            "message" => "user_id, pet_id, and pet_name are required"
        ]);
        exit;
    }

$query = "SELECT
            medication_name,
            dosage_time,
            frequency,
            reminder_enabled
          FROM medications
          WHERE user_id = ?
            AND pet_id = ?
            AND pet_name = ?
          ORDER BY medication_id DESC";

$stmt = mysqli_prepare($conn, $query);
mysqli_stmt_bind_param($stmt, "iis", $user_id, $pet_id, $pet_name);
mysqli_stmt_execute($stmt);

$result = mysqli_stmt_get_result($stmt);
$data = [];

while ($row = mysqli_fetch_assoc($result)) {
    $data[] = [
        "medication_name" => $row['medication_name'],
        "dosage_time" => $row['dosage_time'],
        "frequency" => $row['frequency'],
        "reminder_enabled" => (bool)$row['reminder_enabled']
    ];
}

    echo json_encode([
        "status" => true,
        "medications" => $data
    ]);

    mysqli_stmt_close($stmt);
    mysqli_close($conn);
} else {
    echo json_encode([
        "status" => false,
        "message" => "Invalid request method"
    ]);
}
?>
