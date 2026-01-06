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

    $user_id      = $_POST['user_id'] ?? '';
    $pet_id       = $_POST['pet_id'] ?? '';
    $pet_name     = $_POST['pet_name'] ?? '';
    $vaccine_name = $_POST['vaccine_name'] ?? '';
    $last_date    = $_POST['last_date'] ?? '';
    $next_date    = $_POST['next_date'] ?? '';

    if (empty($user_id) || empty($pet_id) || empty($pet_name) || empty($vaccine_name) || empty($last_date) || empty($next_date)) {
        echo json_encode([
            "status" => false,
            "message" => "All fields are required"
        ]);
        exit;
    }

    // Auto status calculation
    $today = date("Y-m-d");
    if ($next_date < $today) {
        $status = "OVERDUE";
    } elseif ($next_date <= date('Y-m-d', strtotime('+30 days'))) {
        $status = "DUE_SOON";
    } else {
        $status = "CURRENT";
    }

    $query = "INSERT INTO vaccinations
              (user_id, pet_id, pet_name, vaccine_name, last_date, next_date, status)
              VALUES (?, ?, ?, ?, ?, ?, ?)";

    $stmt = mysqli_prepare($conn, $query);
    mysqli_stmt_bind_param(
        $stmt,
        "iisssss",
        $user_id,
        $pet_id,
        $pet_name,
        $vaccine_name,
        $last_date,
        $next_date,
        $status
    );

    if (mysqli_stmt_execute($stmt)) {
        echo json_encode([
            "status" => true,
            "message" => "Vaccination added successfully"
        ]);
    } else {
        echo json_encode([
            "status" => false,
            "message" => "Failed to add vaccination"
        ]);
    }

    mysqli_close($conn);
}
?>
