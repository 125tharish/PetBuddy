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

if ($_SERVER['REQUEST_METHOD'] == "POST") {
    $user_id = $_POST['user_id'] ?? '';
    $pet_id  = $_POST['pet_id'] ?? '';

    if (empty($user_id) || empty($pet_id)) {
        echo json_encode([
            "status" => false,
            "message" => "user_id and pet_id are required"
        ]);
        exit;
    }

$query = "SELECT record_id, title, file_url
          FROM medical_records
          WHERE user_id = ? AND pet_id = ?
          ORDER BY created_at DESC";

$stmt = mysqli_prepare($conn, $query);
mysqli_stmt_bind_param($stmt, "ii", $user_id, $pet_id);
mysqli_stmt_execute($stmt);

$result = mysqli_stmt_get_result($stmt);
$records = [];

while ($row = mysqli_fetch_assoc($result)) {
    $records[] = [
        "record_id" => $row['record_id'],
        "title" => $row['title'],
        "download_url" => $row['file_url']
    ];
}

    echo json_encode([
        "status" => true,
        "records" => $records
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
