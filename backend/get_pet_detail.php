<?php
header("Content-Type: application/json");
require_once __DIR__ . "/db.php";

if ($_SERVER['REQUEST_METHOD'] === "POST") {

    $pet_id = $_POST['pet_id'] ?? '';

    if (empty($pet_id)) {
        echo json_encode([
            "status" => false,
            "message" => "Pet ID is required"
        ]);
        exit;
    }

    /* ✅ Added pet_image */
    $query = "
        SELECT 
            pet_id,
            pet_name,
            pet_type,
            breed,
            color,
            age,
            microchip_id,
            pet_image
        FROM pets
        WHERE pet_id = ?
    ";

    $stmt = mysqli_prepare($conn, $query);
    mysqli_stmt_bind_param($stmt, "i", $pet_id);
    mysqli_stmt_execute($stmt);

    $result = mysqli_stmt_get_result($stmt);
    $pet = mysqli_fetch_assoc($result);

    if ($pet) {
        echo json_encode([
            "status" => true,
            "pet" => $pet
        ]);
    } else {
        echo json_encode([
            "status" => false,
            "message" => "Pet not found"
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