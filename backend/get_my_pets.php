<?php
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

    if (empty($user_id)) {
        echo json_encode([
            "status" => false,
            "message" => "User ID is required"
        ]);
        exit;
    }

    /* ✅ Include pet_image */
    $query = "
        SELECT 
            pet_id,
            pet_name,
            pet_type,
            breed,
            age,
            color,
            microchip_id,
            pet_image
        FROM pets
        WHERE user_id = ?
        ORDER BY pet_id DESC
    ";

    $stmt = mysqli_prepare($conn, $query);
    mysqli_stmt_bind_param($stmt, "i", $user_id);
    mysqli_stmt_execute($stmt);

    $result = mysqli_stmt_get_result($stmt);

    $pets = [];

    while ($row = mysqli_fetch_assoc($result)) {
        $pets[] = $row;
    }

    echo json_encode([
        "status" => true,
        "pets" => $pets
    ]);

    mysqli_stmt_close($stmt);
    mysqli_close($conn);

} else {
    echo json_encode([
        "status" => false,
        "message" => "Invalid request method"
    ]);
}
?>