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

// Suppress any output before headers
ob_start();

// Try to include db.php first, otherwise use inline connection
$pdo = null;
$conn = null;

if (file_exists(__DIR__ . '/db.php')) {
    require_once __DIR__ . '/db.php';
    // Check if db.php set $pdo or $conn (mysqli)
    if (!isset($pdo) && isset($conn) && $conn instanceof mysqli) {
        // If db.php uses mysqli, we need to use PDO instead
        // We'll create our own PDO connection
        $pdo = null; // Will be set below
    }
}

// If $pdo is still not set, create connection inline
if (!isset($pdo) || $pdo === null) {
    $host = 'localhost';
    $dbname = 'petbuddy_db';
    $username = 'root';
    $password = '';
    
    try {
        $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8mb4", $username, $password);
        $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    } catch (PDOException $e) {
        ob_end_clean();
        http_response_code(500);
        echo json_encode([
            'status' => 'error',
            'message' => 'Database connection failed: ' . $e->getMessage()
        ], JSON_UNESCAPED_UNICODE);
        exit();
    }
}

// Clear any output buffer
ob_end_clean();

try {
    // Ensure $pdo is available
    if (!isset($pdo) || $pdo === null || !($pdo instanceof PDO)) {
        throw new Exception("Database connection not available");
    }
    
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
        ], JSON_UNESCAPED_UNICODE);
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
    
    // Use MySQL NOW() for accurate server timestamp
    $currentTimestamp = date('Y-m-d H:i:s');
    
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
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW(), 1)
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
            $user_name // owner_name (the user who created the profile)
        ]);
        
        echo json_encode([
            'status' => 'success',
            'message' => 'Notification created successfully',
            'notification_created' => true
        ], JSON_UNESCAPED_UNICODE);
        
    } catch (PDOException $e) {
        http_response_code(500);
        echo json_encode([
            'status' => 'error',
            'message' => 'Failed to create notification: ' . $e->getMessage()
        ], JSON_UNESCAPED_UNICODE);
    }
    
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Database error: ' . $e->getMessage()
    ], JSON_UNESCAPED_UNICODE);
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Server error: ' . $e->getMessage()
    ], JSON_UNESCAPED_UNICODE);
}
?>

