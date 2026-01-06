<?php
// Start output buffering to prevent any output before JSON
ob_start();

header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: GET, OPTIONS");
header("Access-Control-Allow-Headers: Content-Type");

// Handle OPTIONS request for CORS
if ($_SERVER["REQUEST_METHOD"] === "OPTIONS") {
    ob_end_clean();
    http_response_code(200);
    exit();
}

// Suppress any warnings/errors that might output before JSON
error_reporting(0);
ini_set('display_errors', 0);

// Database connection
$host = "localhost";
$username = "root";
$password = "";
$database = "petbuddy_db";

$conn = new mysqli($host, $username, $password, $database);

if ($conn->connect_error) {
    ob_end_clean();
    echo json_encode([
        "status" => "error",
        "message" => "Database connection failed"
    ]);
    exit();
}

/* Fetch Grooming Services */
$sql = "
SELECT
    service_id,
    service_name,
    location,
    hours,
    contact,
    rating,
    total_reviews,
    min_price,
    max_price
FROM services
WHERE service_type = 'grooming'
";

$result = $conn->query($sql);

if (!$result) {
    ob_end_clean();
    echo json_encode([
        "status" => "error",
        "message" => "Database query failed"
    ]);
    $conn->close();
    exit();
}

$services = [];

while ($row = $result->fetch_assoc()) {
    $services[] = [
        "service_id"   => intval($row['service_id']),
        "service_name" => $row['service_name'],
        "location"     => $row['location'] ?? "",
        "hours"        => $row['hours'] ?? "",
        "contact"      => $row['contact'] ?? "",
        "rating"       => floatval($row['rating']),
        "total_reviews" => intval($row['total_reviews'] ?? 0),
        "min_price"    => $row['min_price'],
        "max_price"    => $row['max_price']
    ];
}

ob_end_clean();
echo json_encode([
    "status" => "success",
    "data" => $services
]);

$conn->close();
?>
