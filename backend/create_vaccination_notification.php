<?php
/**
 * create_vaccination_notification.php
 * 
 * Creates a notification for the user when they successfully add a vaccination.
 * 
 * POST Parameters:
 *   - user_id: ID of the user who added the vaccination
 *   - pet_id: ID of the pet
 *   - pet_name: Name of the pet
 *   - vaccine_name: Name of the vaccine
 *   - next_date: Next vaccination date (optional)
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
    $vaccine_name = isset($_POST['vaccine_name']) ? trim($_POST['vaccine_name']) : '';
    $next_date = isset($_POST['next_date']) ? trim($_POST['next_date']) : '';
    
    // Validate required parameters
    if (!$user_id || !$pet_id || empty($pet_name) || empty($vaccine_name)) {
        http_response_code(400);
        echo json_encode([
            'status' => 'error',
            'message' => 'Missing required parameters: user_id, pet_id, pet_name, and vaccine_name are required.'
        ], JSON_UNESCAPED_UNICODE);
        exit();
    }
    
    // Get user name from users table
    $stmt = $pdo->prepare("SELECT name FROM users WHERE user_id = ?");
    $stmt->execute([$user_id]);
    $user = $stmt->fetch(PDO::FETCH_ASSOC);
    $user_name = $user ? $user['name'] : 'User';
    
    // Get pet type and breed from pets table
    $stmt = $pdo->prepare("SELECT pet_type, breed FROM pets WHERE pet_id = ?");
    $stmt->execute([$pet_id]);
    $pet = $stmt->fetch(PDO::FETCH_ASSOC);
    $pet_type = $pet ? $pet['pet_type'] : '';
    $breed = $pet ? $pet['breed'] : '';
    
    // Prepare notification data
    $notification_type = 'vaccination_added';
    $title = "Vaccination Added Successfully! 💉";
    $description = "You've added $vaccine_name for $pet_name";
    if ($next_date) {
        $description .= ". Next dose due on $next_date";
    }
    $description .= ".";
    
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
            null, // lost_pet_id (not applicable for vaccination)
            $notification_type,
            $title,
            $description,
            $pet_name,
            $pet_type,
            $breed,
            null, // location
            null, // lost_date
            $user_name // owner_name (the user who added the vaccination)
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

