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
            vaccine_name,
            last_date,
            next_date,
            status
          FROM vaccinations
          WHERE user_id = ?
            AND pet_id = ?
            AND pet_name = ?
          ORDER BY next_date ASC";

$stmt = mysqli_prepare($conn, $query);
mysqli_stmt_bind_param($stmt, "iis", $user_id, $pet_id, $pet_name);
mysqli_stmt_execute($stmt);

$result = mysqli_stmt_get_result($stmt);
$data = [];

while ($row = mysqli_fetch_assoc($result)) {
    $data[] = [
        "vaccine_name" => $row['vaccine_name'],
        "last_date" => date("M d, Y", strtotime($row['last_date'])),
        "next_date" => date("M d, Y", strtotime($row['next_date'])),
        "status" => $row['status']
    ];
}

echo json_encode([
    "status" => true,
    "vaccinations" => $data
]);

mysqli_close($conn);
?>
