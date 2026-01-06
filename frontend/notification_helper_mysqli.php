<?php
/**
 * notification_helper_mysqli.php
 * 
 * Helper function to create notifications for all users when a lost pet is reported.
 * Uses mysqli (matches your existing backend style).
 * 
 * Usage:
 *   require_once 'notification_helper_mysqli.php';
 *   createLostPetNotificationsMysqli($conn, $lost_id, $user_id, $pet_name, $pet_type, $breed, $location, $lost_date, $owner_name);
 * 
 * @param mysqli $conn Database connection
 * @param int $lost_id The ID of the lost pet report
 * @param int $user_id The ID of the user who reported the lost pet (owner)
 * @param string $pet_name Name of the lost pet
 * @param string $pet_type Type of pet (Dog, Cat, Other)
 * @param string $breed Breed of the pet
 * @param string|null $location Location where pet was last seen (optional)
 * @param string|null $lost_date Date when pet was lost (optional)
 * @param string|null $owner_name Name of the pet owner (optional, will be fetched from DB if not provided)
 * @return array Result array with 'success' boolean, 'notifications_created' count, and 'message'
 */
function createLostPetNotificationsMysqli($conn, $lost_id, $user_id, $pet_name, $pet_type, $breed, $location = null, $lost_date = null, $owner_name = null) {
    try {
        // Get owner name from users table if not provided
        if (empty($owner_name)) {
            $stmt = mysqli_prepare($conn, "SELECT name FROM users WHERE user_id = ?");
            mysqli_stmt_bind_param($stmt, "i", $user_id);
            mysqli_stmt_execute($stmt);
            $result = mysqli_stmt_get_result($stmt);
            $owner = mysqli_fetch_assoc($result);
            if ($owner) {
                $owner_name = $owner['name'];
            } else {
                $owner_name = 'Unknown Owner';
            }
            mysqli_stmt_close($stmt);
        }
        
        // Get all users except the one who reported the lost pet
        $stmt = mysqli_prepare($conn, "SELECT user_id FROM users WHERE user_id != ?");
        mysqli_stmt_bind_param($stmt, "i", $user_id);
        mysqli_stmt_execute($stmt);
        $result = mysqli_stmt_get_result($stmt);
        $users = [];
        while ($row = mysqli_fetch_assoc($result)) {
            $users[] = $row;
        }
        mysqli_stmt_close($stmt);
        
        if (empty($users)) {
            // No other users to notify
            return [
                'success' => true,
                'notifications_created' => 0,
                'message' => 'No other users to notify.'
            ];
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
        
        $notifications_created = 0;
        $errors = [];
        
        foreach ($users as $user) {
            try {
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
            } catch (Exception $e) {
                $errors[] = "Failed to create notification for user_id {$user['user_id']}: " . $e->getMessage();
            }
        }
        
        mysqli_stmt_close($insertStmt);
        
        return [
            'success' => true,
            'notifications_created' => $notifications_created,
            'total_users_notified' => count($users),
            'errors' => $errors,
            'message' => "Notifications sent to $notifications_created users."
        ];
        
    } catch (Exception $e) {
        return [
            'success' => false,
            'notifications_created' => 0,
            'message' => 'Error: ' . $e->getMessage()
        ];
    }
}
?>

