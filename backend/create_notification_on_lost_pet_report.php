<?php
/**
 * create_notification_on_lost_pet_report.php
 * 
 * Creates notifications for all users when a lost pet is reported.
 * Also creates a notification for the user who reported the lost pet.
 * 
 * POST Parameters:
 *   - lost_id: ID of the lost pet report
 *   - user_id: ID of the user who reported the lost pet (owner)
 *   - pet_name: Name of the lost pet
 *   - pet_type: Type of pet (Dog, Cat, Other)
 *   - breed: Breed of the pet
 *   - location: Location where pet was last seen (optional)
 *   - lost_date: Date when pet was lost (optional)
 *   - owner_name: Name of the pet owner (optional)
 * 
 * Returns:
 *   JSON response with status and message
 */

// Suppress any output before headers
ob_start();

header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST, OPTIONS");
header("Access-Control-Allow-Headers: Content-Type");

// Clear any output buffer
ob_end_clean();

/* Handle OPTIONS request for CORS */
if ($_SERVER["REQUEST_METHOD"] === "OPTIONS") {
    http_response_code(200);
    exit();
}

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
        http_response_code(500);
        echo json_encode([
            'status' => false,
            'message' => 'Database connection failed: ' . $e->getMessage()
        ], JSON_UNESCAPED_UNICODE);
        exit();
    }
}

if ($_SERVER['REQUEST_METHOD'] === "POST") {
    $lost_id = isset($_POST['lost_id']) ? intval($_POST['lost_id']) : null;
    $user_id = isset($_POST['user_id']) ? intval($_POST['user_id']) : null;
    $pet_name = isset($_POST['pet_name']) ? trim($_POST['pet_name']) : '';
    $pet_type = isset($_POST['pet_type']) ? trim($_POST['pet_type']) : '';
    $breed = isset($_POST['breed']) ? trim($_POST['breed']) : '';
    $location = isset($_POST['location']) ? trim($_POST['location']) : null;
    $lost_date = isset($_POST['lost_date']) ? trim($_POST['lost_date']) : null;
    $owner_name = isset($_POST['owner_name']) ? trim($_POST['owner_name']) : null;

    if (empty($lost_id) || empty($user_id) || empty($pet_name) || empty($pet_type) || empty($breed)) {
        http_response_code(400);
        echo json_encode([
            "status" => false,
            "message" => "Missing required parameters: lost_id, user_id, pet_name, pet_type, and breed are required."
        ], JSON_UNESCAPED_UNICODE);
        exit;
    }

    try {
        // Ensure $pdo is available
        if (!isset($pdo) || $pdo === null || !($pdo instanceof PDO)) {
            throw new Exception("Database connection not available");
        }

        // Get owner name from users table if not provided
        if (empty($owner_name)) {
            $stmt = $pdo->prepare("SELECT name FROM users WHERE user_id = ?");
            $stmt->execute([$user_id]);
            $owner = $stmt->fetch(PDO::FETCH_ASSOC);
            
            if ($owner) {
                $owner_name = $owner['name'];
            } else {
                $owner_name = 'Unknown Owner';
            }
        }
        
        // Prepare notification data
        $notification_type = 'lost_pet_alert';
        $title = "Lost Pet Alert: $pet_name";
        $description = "$pet_name, a $breed $pet_type, has been reported lost. ";
        if ($location) {
            $description .= "Last seen at: $location. ";
        }
        if ($lost_date) {
            $description .= "Lost on: $lost_date. ";
        }
        $description .= "Please keep an eye out and help reunite $pet_name with their owner, $owner_name.";
        
        // Use MySQL NOW() for accurate server timestamp
        $currentTimestamp = date('Y-m-d H:i:s');
        
        // Get all users (including the reporter, so they also get a notification)
        $stmt = $pdo->prepare("SELECT user_id FROM users");
        $stmt->execute();
        $users = $stmt->fetchAll(PDO::FETCH_ASSOC);
        
        if (empty($users)) {
            echo json_encode([
                "status" => true,
                "message" => "No users to notify.",
                "notifications_created" => 0
            ], JSON_UNESCAPED_UNICODE);
            exit;
        }
        
        // Insert notifications for all users
        $notifications_created = 0;
        $errors = [];
        
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
        
        foreach ($users as $user) {
            try {
                $insertStmt->execute([
                    $user['user_id'],
                    $lost_id,
                    $notification_type,
                    $title,
                    $description,
                    $pet_name,
                    $pet_type,
                    $breed,
                    $location,
                    $lost_date,
                    $owner_name
                ]);
                $notifications_created++;
            } catch (PDOException $e) {
                $errors[] = "Failed to create notification for user_id {$user['user_id']}: " . $e->getMessage();
            }
        }
        
        echo json_encode([
            "status" => true,
            "message" => "Notifications created successfully",
            "notifications_created" => $notifications_created,
            "total_users_notified" => count($users),
            "errors" => $errors
        ], JSON_UNESCAPED_UNICODE);
        
    } catch (PDOException $e) {
        http_response_code(500);
        echo json_encode([
            "status" => false,
            "message" => "Database error: " . $e->getMessage()
        ], JSON_UNESCAPED_UNICODE);
    } catch (Exception $e) {
        http_response_code(500);
        echo json_encode([
            "status" => false,
            "message" => "Error: " . $e->getMessage()
        ], JSON_UNESCAPED_UNICODE);
    }
} else {
    http_response_code(405);
    echo json_encode([
        "status" => false,
        "message" => "Invalid request method. Use POST."
    ], JSON_UNESCAPED_UNICODE);
}
?>

