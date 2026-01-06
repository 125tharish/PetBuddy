<?php
// Start output buffering to prevent any output before JSON
ob_start();

header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST, OPTIONS");
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

/* ✅ Read JSON body */
$input = json_decode(file_get_contents("php://input"), true);

/* ✅ Fallback to form-data */
if ($_SERVER['REQUEST_METHOD'] == "POST") {
    if (isset($_POST['pet_id'])) {
        $pet_id = intval($_POST['pet_id']);
    } elseif (isset($input['pet_id'])) {
        $pet_id = intval($input['pet_id']);
    } else {
        ob_end_clean();
        echo json_encode([
            "status" => "error",
            "message" => "pet_id is required in request body"
        ]);
        $conn->close();
        exit();
    }
} else {
    ob_end_clean();
    echo json_encode([
        "status" => "error",
        "message" => "Invalid request method"
    ]);
    $conn->close();
    exit();
}

/* Step 1: Get pet_type */
$stmt = $conn->prepare("SELECT pet_type FROM pets WHERE pet_id = ?");
if (!$stmt) {
    ob_end_clean();
    echo json_encode([
        "status" => "error",
        "message" => "Database query preparation failed"
    ]);
    $conn->close();
    exit();
}

$stmt->bind_param("i", $pet_id);
$stmt->execute();
$result = $stmt->get_result();

if (!$row = $result->fetch_assoc()) {
    ob_end_clean();
    echo json_encode([
        "status" => "error",
        "message" => "Pet not found"
    ]);
    $stmt->close();
    $conn->close();
    exit();
}

$pet_type = $row['pet_type'];
$stmt->close();

/* Step 2: Get nutrition */
$stmt2 = $conn->prepare("
    SELECT daily_cups, breakfast, dinner, current_food
    FROM pet_nutrition
    WHERE pet_type = ?
");

if (!$stmt2) {
    ob_end_clean();
    echo json_encode([
        "status" => "error",
        "message" => "Database query preparation failed"
    ]);
    $conn->close();
    exit();
}

$stmt2->bind_param("s", $pet_type);
$stmt2->execute();
$result2 = $stmt2->get_result();

if ($nutrition = $result2->fetch_assoc()) {
    ob_end_clean();
    echo json_encode([
        "status" => "success",
        "pet_type" => $pet_type,
        "data" => $nutrition
    ]);
} else {
    ob_end_clean();
    echo json_encode([
        "status" => "error",
        "message" => "Nutrition not found for pet type"
    ]);
}

$stmt2->close();
$conn->close();
?>
