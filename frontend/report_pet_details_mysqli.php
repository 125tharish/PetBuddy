<?php
/**
 * report_pet_details.php (mysqli version)
 * 
 * This file handles the creation of lost pet reports using mysqli.
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

    $user_id = $_POST['user_id'] ?? '';
    $pet_type = $_POST['pet_type'] ?? '';
    $pet_name = $_POST['pet_name'] ?? '';
    $breed = $_POST['breed'] ?? '';
    $age = $_POST['age'] ?? null;
    $weight = $_POST['weight'] ?? null;
    $primary_color = $_POST['primary_color'] ?? null;
    $description = $_POST['description'] ?? null;

    if (empty($user_id) || empty($pet_type) || empty($pet_name) || empty($breed)) {
        echo json_encode([
            "status" => false,
            "message" => "Missing required parameters: user_id, pet_type, pet_name, and breed are required."
        ]);
        exit;
    }

    // Include the notification helper function
    require_once __DIR__ . '/notification_helper_mysqli.php';

    // Insert lost pet report
    $query = "
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
    ";

    $stmt = mysqli_prepare($conn, $query);
    mysqli_stmt_bind_param(
        $stmt,
        "isssssss",
        $user_id,
        $pet_type,
        $pet_name,
        $breed,
        $age,
        $weight,
        $primary_color,
        $description
    );

    if (mysqli_stmt_execute($stmt)) {
        // Get the lost_id of the newly created report
        $lost_id = mysqli_insert_id($conn);
        
        // Create notifications for all users
        $notificationResult = createLostPetNotificationsMysqli(
            $conn,
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
            "status" => true,
            "message" => "Pet details reported successfully",
            "lost_id" => $lost_id
        ];
        
        // Optionally include notification creation info (for debugging)
        if (isset($notificationResult['notifications_created'])) {
            $response['notifications_sent'] = $notificationResult['notifications_created'];
        }
        
        echo json_encode($response);
    } else {
        echo json_encode([
            "status" => false,
            "message" => "Failed to create pet report: " . mysqli_error($conn)
        ]);
    }

    mysqli_stmt_close($stmt);
    mysqli_close($conn);

} else {
    echo json_encode([
        "status" => false,
        "message" => "Invalid request method"
    ]);
}
?>

