<?php
header("Content-Type: application/json");
include "db.php";

if ($_SERVER['REQUEST_METHOD'] === "POST") {

    // REQUIRED: user-specific data
    $user_id        = $_POST['user_id'] ?? '';
    $pet_type       = $_POST['pet_type'] ?? '';
    $pet_name       = $_POST['pet_name'] ?? '';
    $breed          = $_POST['breed'] ?? '';

    // OPTIONAL fields
    $age            = $_POST['age'] ?? '';
    $weight         = $_POST['weight'] ?? '';
    $primary_color  = $_POST['primary_color'] ?? '';
    $description    = $_POST['description'] ?? '';

    // Validation
    if (empty($user_id) || empty($pet_type) || empty($pet_name) || empty($breed)) {
        echo json_encode([
            "status" => false,
            "message" => "user_id, pet_type, pet_name, and breed are required"
        ]);
        exit;
    }

    $query = "INSERT INTO lost_pets
              (user_id, pet_type, pet_name, breed, age, weight, primary_color, description)
              VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

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
        // Get the inserted lost_id
        $lost_id = mysqli_insert_id($conn);
        
        echo json_encode([
            "status" => true,
            "message" => "Lost pet reported successfully for this user",
            "lost_id" => $lost_id
        ]);
    } else {
        echo json_encode([
            "status" => false,
            "message" => "Failed to report lost pet"
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
