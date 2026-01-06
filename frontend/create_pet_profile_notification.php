<?php
/**
 * create_pet_profile_notification.php
 * 
 * Creates a notification for the user when they successfully create a pet profile.
 * 
 * POST Parameters:
 *   - user_id: ID of the user who created the pet profile
 *   - pet_id: ID of the newly created pet (optional)
 *   - pet_name: Name of the pet
 *   - pet_type: Type of pet (Dog, Cat, Other)
 *   - breed: Breed of the pet (optional)
 * 
 * Returns:
 *   JSON response with status and message
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
        'status' => 'error',
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
    
    // Get POST parameters
    $user_id = isset($_POST['user_id']) ? intval($_POST['user_id']) : null;
    $pet_id = isset($_POST['pet_id']) ? intval($_POST['pet_id']) : null;
    $pet_name = isset($_POST['pet_name']) ? trim($_POST['pet_name']) : '';
    $pet_type = isset($_POST['pet_type']) ? trim($_POST['pet_type']) : '';
    $breed = isset($_POST['breed']) ? trim($_POST['breed']) : '';
    
    // Validate required parameters
    if (!$user_id || empty($pet_name) || empty($pet_type)) {
        http_response_code(400);
        echo json_encode([
            'status' => 'error',
            'message' => 'Missing required parameters: user_id, pet_name, and pet_type are required.'
        ]);
        exit();
    }
    
    // Get user name from users table
    $stmt = $pdo->prepare("SELECT name FROM users WHERE user_id = ?");
    $stmt->execute([$user_id]);
    $user = $stmt->fetch(PDO::FETCH_ASSOC);
    $user_name = $user ? $user['name'] : 'User';
    
    // Prepare notification data
    $notification_type = 'pet_profile_created';
    $title = "Pet Profile Created Successfully! 🎉";
    $description = "Congratulations! You've successfully created a profile for $pet_name";
    if ($breed) {
        $description .= ", your $breed $pet_type";
    } else {
        $description .= ", your $pet_type";
    }
    $description .= ". You can now track their health, vaccinations, and more!";
    
    $timestamp = date('Y-m-d H:i:s');
    $created_at = date('Y-m-d H:i:s');
    
    // Insert notification for the user
    $insertStmt = $pdo->prepare("
        INSERT INTO notifications (
            user_id, 
            lost_pet_id, 
            type, 
            title, 
            description, 
            pet_name, 
            pet_type, 
            breed, 
            location, 
            lost_date, 
            owner_name, 
            timestamp, 
            created_at, 
            is_unread
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 1)
    ");
    
    try {
        $insertStmt->execute([
            $user_id,
            null, // lost_pet_id (not applicable for pet profile creation)
            $notification_type,
            $title,
            $description,
            $pet_name,
            $pet_type,
            $breed,
            null, // location
            null, // lost_date
            $user_name, // owner_name (the user who created the profile)
            $timestamp,
            $created_at
        ]);
        
        echo json_encode([
            'status' => 'success',
            'message' => 'Notification created successfully',
            'notification_created' => true
        ]);
        
    } catch (PDOException $e) {
        http_response_code(500);
        echo json_encode([
            'status' => 'error',
            'message' => 'Failed to create notification: ' . $e->getMessage()
        ]);
    }
    
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Database error: ' . $e->getMessage()
    ]);
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Server error: ' . $e->getMessage()
    ]);
}
?>

