<?php
/**
 * example_report_pet_details_with_notifications.php
 * 
 * This is an EXAMPLE file showing how to integrate notification creation
 * into your existing report_pet_details.php file.
 * 
 * DO NOT use this file directly - instead, update your existing report_pet_details.php
 * to include the notification creation code.
 */

header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight OPTIONS request
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

// Database configuration
$host = 'localhost';
$dbname = 'petbuddy_db';
$username = 'root';
$password = '';

try {
    // Connect to database
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8mb4", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // Include the notification helper function
    require_once 'notification_helper.php';
    
    // Get POST parameters (your existing code)
    $user_id = isset($_POST['user_id']) ? intval($_POST['user_id']) : null;
    $pet_type = isset($_POST['pet_type']) ? trim($_POST['pet_type']) : '';
    $pet_name = isset($_POST['pet_name']) ? trim($_POST['pet_name']) : '';
    $breed = isset($_POST['breed']) ? trim($_POST['breed']) : '';
    $age = isset($_POST['age']) ? trim($_POST['age']) : null;
    $weight = isset($_POST['weight']) ? trim($_POST['weight']) : null;
    $primary_color = isset($_POST['primary_color']) ? trim($_POST['primary_color']) : null;
    $description = isset($_POST['description']) ? trim($_POST['description']) : null;
    
    // Validate required parameters (your existing validation)
    if (!$user_id || empty($pet_type) || empty($pet_name) || empty($breed)) {
        http_response_code(400);
        echo json_encode([
            'status' => false,
            'message' => 'Missing required parameters'
        ]);
        exit();
    }
    
    // Insert lost pet report (your existing code)
    $stmt = $pdo->prepare("
        INSERT INTO lost_pet_reports (
            user_id, 
            pet_type, 
            pet_name, 
            breed, 
            age, 
            weight, 
            primary_color, 
            description, 
            created_at
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())
    ");
    
    $stmt->execute([
        $user_id,
        $pet_type,
        $pet_name,
        $breed,
        $age,
        $weight,
        $primary_color,
        $description
    ]);
    
    // Get the lost_id of the newly created report
    $lost_id = $pdo->lastInsertId();
    
    // ============================================
    // ADD THIS CODE: Create notifications for all users
    // ============================================
    $notificationResult = createLostPetNotifications(
        $pdo,
        $lost_id,
        $user_id,
        $pet_name,
        $pet_type,
        $breed,
        null, // location (can be added later when location is saved)
        null, // lost_date (can be added later when date is saved)
        null  // owner_name (will be fetched from users table)
    );
    
    // You can optionally log the notification result
    // error_log("Notifications created: " . $notificationResult['notifications_created']);
    // ============================================
    
    // Return success response (your existing response)
    echo json_encode([
        'status' => true,
        'message' => 'Pet details reported successfully',
        'lost_id' => $lost_id,
        'notifications_sent' => $notificationResult['notifications_created'] ?? 0
    ]);
    
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode([
        'status' => false,
        'message' => 'Database error: ' . $e->getMessage()
    ]);
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => false,
        'message' => 'Server error: ' . $e->getMessage()
    ]);
}
?>

