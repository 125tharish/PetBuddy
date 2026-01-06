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

$sql = "
SELECT 
    clinic_user_id,
    full_name,
    clinic_name,
    email,
    phone,
    address
FROM pet_clinic_users
WHERE clinic_user_id = ?
";

$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $clinic_user_id);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows === 1) {
    $profile = $result->fetch_assoc();
    
    echo json_encode([
        "status" => "success",
        "profile" => [
            "clinic_user_id" => $profile['clinic_user_id'],
            "full_name" => $profile['full_name'] ?? '',
            "clinic_name" => $profile['clinic_name'] ?? '',
            "email" => $profile['email'] ?? '',
            "phone" => $profile['phone'] ?? '',
            "address" => $profile['address'] ?? ''
        ]
    ]);
} else {
    echo json_encode([
        "status" => "error",
        "message" => "Profile not found"
    ]);
}

$stmt->close();
$conn->close();
?>

