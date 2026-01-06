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

if ($_SERVER["REQUEST_METHOD"] !== "POST") {
    echo json_encode([
        "status" => false,
        "message" => "Invalid request method"
    ]);
    exit;
}

/* ---------- INPUT FROM BODY ---------- */
$type = $_POST['type'] ?? 'all'; // today | upcoming | past | all
$clinic_user_id = $_POST['clinic_user_id'] ?? '';

$today = date("Y-m-d");

/* ---------- FILTER CONDITION ---------- */
$where = "";

if ($type === "today") {
    $where = "WHERE a.appointment_date = ?";
} elseif ($type === "upcoming") {
    $where = "WHERE a.appointment_date > ?";
} elseif ($type === "past") {
    $where = "WHERE a.appointment_date < ?";
} else {
    $where = ""; // all
}

/* ---------- QUERY ---------- */
if ($type === "all") {
    $sql = "SELECT
                a.appointment_id,
                a.pet_id,
                a.service_name,
                a.appointment_date,
                a.appointment_time,
                a.status,
                p.pet_name,
                p.breed as pet_breed,
                u.name as owner_name,
                u.email as owner_email
            FROM appointments a
            LEFT JOIN pets p ON a.pet_id = p.pet_id
            LEFT JOIN users u ON a.user_id = u.user_id
            ORDER BY a.appointment_date ASC, a.appointment_time ASC";
    $stmt = $conn->prepare($sql);
} else {
    $sql = "SELECT
                a.appointment_id,
                a.pet_id,
                a.service_name,
                a.appointment_date,
                a.appointment_time,
                a.status,
                p.pet_name,
                p.breed as pet_breed,
                u.name as owner_name,
                u.email as owner_email
            FROM appointments a
            LEFT JOIN pets p ON a.pet_id = p.pet_id
            LEFT JOIN users u ON a.user_id = u.user_id
            $where
            ORDER BY a.appointment_date ASC, a.appointment_time ASC";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("s", $today);
}

$stmt->execute();
$result = $stmt->get_result();

$appointments = [];
while ($row = $result->fetch_assoc()) {
    // Format date
    $formattedDate = date("M d, Y", strtotime($row['appointment_date']));
    // Format time
    $formattedTime = date("h:i A", strtotime($row['appointment_time']));
    
    // Determine status color
    $statusColor = "#50C878"; // Green for Confirmed
    if ($row['status'] === "Pending") {
        $statusColor = "#FF8A50"; // Orange for Pending
    } elseif ($row['status'] === "Cancelled") {
        $statusColor = "#FF5252"; // Red for Cancelled
    }
    
    $appointments[] = [
        "appointment_id" => (int)$row['appointment_id'],
        "pet_id" => (int)$row['pet_id'],
        "pet_name" => $row['pet_name'] ?? "Unknown Pet",
        "pet_breed" => $row['pet_breed'] ?? "Unknown Breed",
        "owner_name" => $row['owner_name'] ?? "Unknown Owner",
        "owner_email" => $row['owner_email'] ?? "",
        "service_name" => $row['service_name'] ?? "General Service",
        "appointment_date" => $formattedDate,
        "appointment_time" => $formattedTime,
        "status" => $row['status'] ?? "Pending",
        "status_color" => $statusColor
    ];
}

echo json_encode([
    "status" => true,
    "appointments" => $appointments
]);

$stmt->close();
$conn->close();
?>

