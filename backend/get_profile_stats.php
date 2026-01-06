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

$clinic_user_id = $_POST['clinic_user_id'] ?? '';
$today = date("Y-m-d");

if (empty($clinic_user_id)) {
    echo json_encode([
        "status" => false,
        "message" => "clinic_user_id is required"
    ]);
    exit;
}

/* ---------- CLINIC DETAILS (BY clinic_user_id) ---------- */
$sqlProfile = "
SELECT
    clinic_user_id,
    full_name,
    email,
    IFNULL(phone,'') AS phone,
    IFNULL(address,'') AS address
FROM pet_clinic_users
WHERE clinic_user_id = ?
";

$stmtProfile = $conn->prepare($sqlProfile);
$stmtProfile->bind_param("i", $clinic_user_id);
$stmtProfile->execute();
$profile = $stmtProfile->get_result()->fetch_assoc();

if (!$profile) {
    echo json_encode([
        "status" => false,
        "message" => "Clinic not found"
    ]);
    exit;
}

/* ---------- GLOBAL STATS (NO clinic_user_id) ---------- */

/* Total patients (distinct pets) */
$sqlPatients = "SELECT COUNT(DISTINCT pet_id) AS total FROM appointments";
$totalPatients = $conn->query($sqlPatients)->fetch_assoc()['total'];

/* Today appointments */
$sqlToday = "
SELECT COUNT(*) AS total
FROM appointments
WHERE appointment_date = ?
";
$stmtToday = $conn->prepare($sqlToday);
$stmtToday->bind_param("s", $today);
$stmtToday->execute();
$todayCount = $stmtToday->get_result()->fetch_assoc()['total'];

/* Revenue (paid only) */
$sqlRevenue = "
SELECT IFNULL(SUM(total_amount),0) AS revenue
FROM appointments
WHERE payment_status = 'paid'
";
$revenue = $conn->query($sqlRevenue)->fetch_assoc()['revenue'];

/* ---------- RESPONSE ---------- */
echo json_encode([
    "status" => true,
    "profile" => $profile,
    "stats" => [
        "patients" => (int)$totalPatients,
        "today" => (int)$todayCount,
        "revenue" => (float)$revenue
    ]
]);

$stmtProfile->close();
$stmtToday->close();
$conn->close();
?>
