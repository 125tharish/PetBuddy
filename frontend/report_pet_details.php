<?php
/**
 * report_pet_details.php
 * 
 * This file handles the creation of lost pet reports.
 * After successfully creating a lost pet report, it automatically creates
 * notifications for all users in the system.
 * 
 * POST Parameters:
 *   - user_id: ID of the user reporting the lost pet
 *   - pet_type: Type of pet (Dog, Cat, Other)
 *   - pet_name: Name of the pet
 *   - breed: Breed of the pet
 *   - age: Age of the pet (optional)
 *   - weight: Weight of the pet (optional)
 *   - primary_color: Primary color of the pet (optional)
 *   - description: Description of the pet (optional)
 * 
 * Returns:
 *   JSON response with status, message, and lost_id
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

// Only allow POST requests
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode([
        'status' => false,
        'message' => 'Method not allowed. Use POST.'
    ]);
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
    
    // Get POST parameters
    $user_id = isset($_POST['user_id']) ? intval($_POST['user_id']) : null;
    $pet_type = isset($_POST['pet_type']) ? trim($_POST['pet_type']) : '';
    $pet_name = isset($_POST['pet_name']) ? trim($_POST['pet_name']) : '';
    $breed = isset($_POST['breed']) ? trim($_POST['breed']) : '';
    $age = isset($_POST['age']) ? trim($_POST['age']) : null;
    $weight = isset($_POST['weight']) ? trim($_POST['weight']) : null;
    $primary_color = isset($_POST['primary_color']) ? trim($_POST['primary_color']) : null;
    $description = isset($_POST['description']) ? trim($_POST['description']) : null;
    
    // Validate required parameters
    if (!$user_id || empty($pet_type) || empty($pet_name) || empty($breed)) {
        http_response_code(400);
        echo json_encode([
            'status' => false,
            'message' => 'Missing required parameters: user_id, pet_type, pet_name, and breed are required.'
        ]);
        exit();
    }
    
    // Insert lost pet report
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
    
    // Create notifications for all users
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
    
    // Return success response
    $response = [
        'status' => true,
        'message' => 'Pet details reported successfully',
        'lost_id' => $lost_id
    ];
    
    // Optionally include notification creation info (for debugging)
    if (isset($notificationResult['notifications_created'])) {
        $response['notifications_sent'] = $notificationResult['notifications_created'];
    }
    
    echo json_encode($response);
    
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

