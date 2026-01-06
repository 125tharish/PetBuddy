<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST, OPTIONS");
header("Access-Control-Allow-Headers: Content-Type");
include "db.php";

// Handle OPTIONS request for CORS
if ($_SERVER["REQUEST_METHOD"] === "OPTIONS") {
    http_response_code(200);
    exit();
}

if ($_SERVER['REQUEST_METHOD'] === "POST") {

    $user_id          = $_POST['user_id'] ?? '';
    $pet_id           = $_POST['pet_id'] ?? '';
    $pet_name         = $_POST['pet_name'] ?? '';
    $medication_name  = $_POST['medication_name'] ?? '';
    $dosage_time      = $_POST['dosage_time'] ?? '';
    $frequency        = $_POST['frequency'] ?? '';

    if (empty($user_id) || empty($pet_id) || empty($pet_name) ||
        empty($medication_name) || empty($dosage_time) || empty($frequency)) {
        echo json_encode([
            "status" => false,
            "message" => "All fields are required"
        ]);
        exit;
    }

    $query = "INSERT INTO medications
              (user_id, pet_id, pet_name, medication_name, dosage_time, frequency)
              VALUES (?, ?, ?, ?, ?, ?)";

    $stmt = mysqli_prepare($conn, $query);
    mysqli_stmt_bind_param(
        $stmt,
        "iissss",
        $user_id,
        $pet_id,
        $pet_name,
        $medication_name,
        $dosage_time,
        $frequency
    );

    if (mysqli_stmt_execute($stmt)) {
        echo json_encode([
            "status" => true,
            "message" => "Medication added successfully"
        ]);
    } else {
        echo json_encode([
            "status" => false,
            "message" => "Failed to add medication"
        ]);
    }

    mysqli_stmt_close($stmt);
    mysqli_close($conn);
} else {
    echo json_encode([
        "status" => false,
        "message" => "Invalid request method"
    ]);
}
?>
