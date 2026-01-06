<?php
header("Content-Type: application/json");
include "db.php";

$service_id = $_POST['service_id'] ?? '';

if (empty($service_id)) {
    echo json_encode([
        "status" => false,
        "message" => "service_id is required"
    ]);
    exit;
}

$query = "SELECT
            service_id,
            service_name,
            location,
            hours,
            contact,
            rating,
            total_reviews
          FROM services
          WHERE service_id = ?";

$stmt = mysqli_prepare($conn, $query);
mysqli_stmt_bind_param($stmt, "i", $service_id);
mysqli_stmt_execute($stmt);

$result = mysqli_stmt_get_result($stmt);
$service = mysqli_fetch_assoc($result);

if (!$service) {
    echo json_encode([
        "status" => false,
        "message" => "Service not found"
    ]);
    exit;
}

echo json_encode([
    "status" => true,
    "service" => $service
]);

mysqli_close($conn);
?>
