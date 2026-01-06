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
    // Get pet_id
    if (isset($_POST['pet_id'])) {
        $pet_id = intval($_POST['pet_id']);
    } elseif (isset($input['pet_id'])) {
        $pet_id = intval($input['pet_id']);
    } else {
        ob_end_clean();
        echo json_encode([
            "status" => "error",
            "message" => "pet_id is required"
        ]);
        $conn->close();
        exit();
    }
    
    // Get nutrition data
    $daily_cups = $_POST['daily_cups'] ?? $input['daily_cups'] ?? '';
    $breakfast = $_POST['breakfast'] ?? $input['breakfast'] ?? '';
    $dinner = $_POST['dinner'] ?? $input['dinner'] ?? '';
    $current_food = $_POST['current_food'] ?? $input['current_food'] ?? '';
    
    if (empty($daily_cups)) {
        ob_end_clean();
        echo json_encode([
            "status" => "error",
            "message" => "daily_cups is required"
        ]);
        $conn->close();
        exit();
    }
    
    // Step 1: Get pet_type from pet_id
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
    
    // Step 2: Check if nutrition record exists for this pet_type
    $check_stmt = $conn->prepare("SELECT pet_type FROM pet_nutrition WHERE pet_type = ?");
    if (!$check_stmt) {
        ob_end_clean();
        echo json_encode([
            "status" => "error",
            "message" => "Database query preparation failed"
        ]);
        $conn->close();
        exit();
    }
    
    $check_stmt->bind_param("s", $pet_type);
    $check_stmt->execute();
    $check_result = $check_stmt->get_result();
    $check_stmt->close();
    
    if ($check_result->num_rows > 0) {
        // Update existing record
        $update_stmt = $conn->prepare("
            UPDATE pet_nutrition 
            SET daily_cups = ?, breakfast = ?, dinner = ?, current_food = ?
            WHERE pet_type = ?
        ");
        if (!$update_stmt) {
            ob_end_clean();
            echo json_encode([
                "status" => "error",
                "message" => "Database query preparation failed"
            ]);
            $conn->close();
            exit();
        }
        
        $update_stmt->bind_param("sssss", $daily_cups, $breakfast, $dinner, $current_food, $pet_type);
        
        if ($update_stmt->execute()) {
            ob_end_clean();
            echo json_encode([
                "status" => "success",
                "message" => "Nutrition plan updated successfully"
            ]);
        } else {
            ob_end_clean();
            echo json_encode([
                "status" => "error",
                "message" => "Failed to update nutrition plan"
            ]);
        }
        $update_stmt->close();
    } else {
        // Insert new record
        $insert_stmt = $conn->prepare("
            INSERT INTO pet_nutrition (pet_type, daily_cups, breakfast, dinner, current_food)
            VALUES (?, ?, ?, ?, ?)
        ");
        if (!$insert_stmt) {
            ob_end_clean();
            echo json_encode([
                "status" => "error",
                "message" => "Database query preparation failed"
            ]);
            $conn->close();
            exit();
        }
        
        $insert_stmt->bind_param("sssss", $pet_type, $daily_cups, $breakfast, $dinner, $current_food);
        
        if ($insert_stmt->execute()) {
            ob_end_clean();
            echo json_encode([
                "status" => "success",
                "message" => "Nutrition plan added successfully"
            ]);
        } else {
            ob_end_clean();
            echo json_encode([
                "status" => "error",
                "message" => "Failed to add nutrition plan"
            ]);
        }
        $insert_stmt->close();
    }
    
    $conn->close();
} else {
    ob_end_clean();
    echo json_encode([
        "status" => "error",
        "message" => "Invalid request method"
    ]);
    $conn->close();
    exit();
}
?>

