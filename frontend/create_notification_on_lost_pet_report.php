<?php
/**
 * create_notification_on_lost_pet_report.php
 * 
 * Standalone endpoint to create notifications for all users when a lost pet is reported.
 * This can be called from the Android app after reporting a lost pet.
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

header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST, OPTIONS");
header("Access-Control-Allow-Headers: Content-Type");

require_once __DIR__ . "/db.php";

/* Handle OPTIONS request for CORS */
if ($_SERVER["REQUEST_METHOD"] === "OPTIONS") {
    http_response_code(200);
    exit();
}

if ($_SERVER['REQUEST_METHOD'] === "POST") {
    $lost_id = $_POST['lost_id'] ?? null;
    $user_id = $_POST['user_id'] ?? null;
    $pet_name = $_POST['pet_name'] ?? '';
    $pet_type = $_POST['pet_type'] ?? '';
    $breed = $_POST['breed'] ?? '';
    $location = $_POST['location'] ?? null;
    $lost_date = $_POST['lost_date'] ?? null;
    $owner_name = $_POST['owner_name'] ?? null;

    if (empty($lost_id) || empty($user_id) || empty($pet_name) || empty($pet_type) || empty($breed)) {
        echo json_encode([
            "status" => false,
            "message" => "Missing required parameters: lost_id, user_id, pet_name, pet_type, and breed are required."
        ]);
        exit;
    }

    try {
        // Check if using mysqli or PDO
        $isMysqli = isset($conn) && $conn instanceof mysqli;
        $isPdo = isset($pdo) && $pdo instanceof PDO;

        if (!$isMysqli && !$isPdo) {
            echo json_encode([
                "status" => false,
                "message" => "Database connection not found. Please check db.php"
            ]);
            exit;
        }

        // Get owner name from users table if not provided
        if (empty($owner_name)) {
            if ($isMysqli) {
                $stmt = mysqli_prepare($conn, "SELECT name FROM users WHERE user_id = ?");
                mysqli_stmt_bind_param($stmt, "i", $user_id);
                mysqli_stmt_execute($stmt);
                $result = mysqli_stmt_get_result($stmt);
                $owner = mysqli_fetch_assoc($result);
                mysqli_stmt_close($stmt);
            } else {
                $stmt = $pdo->prepare("SELECT name FROM users WHERE user_id = ?");
                $stmt->execute([$user_id]);
                $owner = $stmt->fetch(PDO::FETCH_ASSOC);
            }
            
            if ($owner) {
                $owner_name = $owner['name'];
            } else {
                $owner_name = 'Unknown Owner';
            }
        }
        
        // Get all users except the one who reported the lost pet
        if ($isMysqli) {
            $stmt = mysqli_prepare($conn, "SELECT user_id FROM users WHERE user_id != ?");
            mysqli_stmt_bind_param($stmt, "i", $user_id);
            mysqli_stmt_execute($stmt);
            $result = mysqli_stmt_get_result($stmt);
            $users = [];
            while ($row = mysqli_fetch_assoc($result)) {
                $users[] = $row;
            }
            mysqli_stmt_close($stmt);
        } else {
            $stmt = $pdo->prepare("SELECT user_id FROM users WHERE user_id != ?");
            $stmt->execute([$user_id]);
            $users = $stmt->fetchAll(PDO::FETCH_ASSOC);
        }
        
        if (empty($users)) {
            echo json_encode([
                "status" => true,
                "message" => "No other users to notify.",
                "notifications_created" => 0
            ]);
            exit;
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
        
        $timestamp = date('Y-m-d H:i:s');
        $created_at = date('Y-m-d H:i:s');
        
        // Insert notifications for all users
        $notifications_created = 0;
        $errors = [];
        
        if ($isMysqli) {
            $insertStmt = mysqli_prepare($conn, "
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
            
            foreach ($users as $user) {
                mysqli_stmt_bind_param(
                    $insertStmt,
                    "iisssssssssssi",
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
                    $owner_name,
                    $timestamp,
                    $created_at,
                    1
                );
                
                if (mysqli_stmt_execute($insertStmt)) {
                    $notifications_created++;
                } else {
                    $errors[] = "Failed to create notification for user_id {$user['user_id']}: " . mysqli_error($conn);
                }
            }
            mysqli_stmt_close($insertStmt);
        } else {
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
                        $owner_name,
                        $timestamp,
                        $created_at
                    ]);
                    $notifications_created++;
                } catch (PDOException $e) {
                    $errors[] = "Failed to create notification for user_id {$user['user_id']}: " . $e->getMessage();
                }
            }
        }
        
        echo json_encode([
            "status" => true,
            "message" => "Notifications created successfully",
            "notifications_created" => $notifications_created,
            "total_users_notified" => count($users),
            "errors" => $errors
        ]);
        
    } catch (Exception $e) {
        echo json_encode([
            "status" => false,
            "message" => "Error: " . $e->getMessage()
        ]);
    }
} else {
    echo json_encode([
        "status" => false,
        "message" => "Invalid request method"
    ]);
}
?>
