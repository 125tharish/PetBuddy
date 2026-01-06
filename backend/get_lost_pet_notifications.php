<?php
/**
 * get_lost_pet_notifications.php
 * 
 * Fetches all notifications for a user from the notifications table.
 * 
 * POST Parameters:
 *   - user_id: ID of the user to fetch notifications for
 * 
 * Returns:
 *   JSON response with status, message, and notifications array
 */

// Suppress any output before headers
ob_start();

header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Clear any output buffer
ob_end_clean();

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

// Try to include db.php first, otherwise use inline connection
$pdo = null;
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
        http_response_code(500);
        echo json_encode([
            'status' => 'error',
            'message' => 'Database connection failed: ' . $e->getMessage()
        ], JSON_UNESCAPED_UNICODE);
        exit();
    }
}

try {
    // Ensure $pdo is available
    if (!isset($pdo) || $pdo === null || !($pdo instanceof PDO)) {
        throw new Exception("Database connection not available");
    }
    
    // Get POST parameters
    $user_id = isset($_POST['user_id']) ? intval($_POST['user_id']) : null;
    
    // Validate required parameters
    if (!$user_id || $user_id <= 0) {
        http_response_code(400);
        echo json_encode([
            'status' => 'error',
            'message' => 'Missing or invalid parameter: user_id is required.'
        ], JSON_UNESCAPED_UNICODE);
        exit();
    }
    
    // Fetch notifications for the user, ordered by most recent first
    $stmt = $pdo->prepare("
        SELECT 
            notification_id,
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
        FROM notifications
        WHERE user_id = ?
        ORDER BY created_at DESC
    ");
    
    $stmt->execute([$user_id]);
    $notifications = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Convert boolean values (MySQL returns 0/1, we need true/false for JSON)
    foreach ($notifications as &$notification) {
        $notification['is_unread'] = (bool)$notification['is_unread'];
        // Ensure all fields are present (set null for missing fields)
        $notification['lost_pet_id'] = $notification['lost_pet_id'] ?? null;
        $notification['pet_name'] = $notification['pet_name'] ?? null;
        $notification['pet_type'] = $notification['pet_type'] ?? null;
        $notification['breed'] = $notification['breed'] ?? null;
        $notification['location'] = $notification['location'] ?? null;
        $notification['lost_date'] = $notification['lost_date'] ?? null;
        $notification['owner_name'] = $notification['owner_name'] ?? null;
    }
    unset($notification); // Break reference
    
    // Return success response
    echo json_encode([
        'status' => 'success',
        'message' => 'Notifications fetched successfully',
        'notifications' => $notifications
    ], JSON_UNESCAPED_UNICODE);
    
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
