<?php
/**
 * get_lost_pet_notifications.php (mysqli version)
 * 
 * Fetches all notifications for a user from the notifications table.
 * Uses mysqli to match your existing backend style.
 * 
 * POST Parameters:
 *   - user_id: ID of the user to fetch notifications for
 * 
 * Returns:
 *   JSON response with status, message, and notifications array
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
    $user_id = $_POST['user_id'] ?? null;

    if (empty($user_id)) {
        echo json_encode([
            "status" => "error",
            "message" => "User ID is required"
        ]);
        exit;
    }

    // Fetch notifications for the user, ordered by most recent first
    $query = "
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
    ";

    $stmt = mysqli_prepare($conn, $query);
    mysqli_stmt_bind_param($stmt, "i", $user_id);
    mysqli_stmt_execute($stmt);
    $result = mysqli_stmt_get_result($stmt);
    $notifications = [];
    
    while ($row = mysqli_fetch_assoc($result)) {
        // Convert is_unread from TINYINT to boolean
        $row['is_unread'] = (bool)$row['is_unread'];
        // Ensure all fields are present (set null for missing fields)
        $row['lost_pet_id'] = $row['lost_pet_id'] ?? null;
        $row['pet_name'] = $row['pet_name'] ?? null;
        $row['pet_type'] = $row['pet_type'] ?? null;
        $row['breed'] = $row['breed'] ?? null;
        $row['location'] = $row['location'] ?? null;
        $row['lost_date'] = $row['lost_date'] ?? null;
        $row['owner_name'] = $row['owner_name'] ?? null;
        $notifications[] = $row;
    }
    
    mysqli_stmt_close($stmt);

    echo json_encode([
        "status" => "success",
        "message" => "Notifications fetched successfully",
        "notifications" => $notifications
    ], JSON_UNESCAPED_UNICODE);

} else {
    echo json_encode([
        "status" => "error",
        "message" => "Invalid request method"
    ]);
}
?>

