<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST, OPTIONS");
header("Access-Control-Allow-Headers: Content-Type");

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

require_once __DIR__ . "/db.php";

/* Accept form-data */
if (!isset($_POST['clinic_user_id'])) {
    echo json_encode([
        "status" => "error",
        "message" => "clinic_user_id is required"
    ]);
    exit;
}

$clinic_user_id = intval($_POST['clinic_user_id']);
$full_name   = $_POST['full_name'] ?? '';
$clinic_name = $_POST['clinic_name'] ?? '';
$email       = $_POST['email'] ?? '';
$phone       = $_POST['phone'] ?? '';
$address     = $_POST['address'] ?? '';

$sql = "
UPDATE pet_clinic_users
SET
    full_name = ?,
    clinic_name = ?,
    email = ?,
    phone = ?,
    address = ?
WHERE clinic_user_id = ?
";

$stmt = $conn->prepare($sql);
$stmt->bind_param(
    "sssssi",
    $full_name,
    $clinic_name,
    $email,
    $phone,
    $address,
    $clinic_user_id
);

if ($stmt->execute()) {
    echo json_encode([
        "status" => "success",
        "message" => "Profile updated successfully"
    ]);
} else {
    echo json_encode([
        "status" => "error",
        "message" => "Update failed"
    ]);
}
?>
