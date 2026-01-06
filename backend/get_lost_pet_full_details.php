<?php
header("Content-Type: application/json");
include "db.php";

$user_id  = $_POST['user_id'] ?? '';
$pet_name = $_POST['pet_name'] ?? '';

if (empty($user_id) || empty($pet_name)) {
    echo json_encode([
        "status" => false,
        "message" => "user_id and pet_name are required"
    ]);
    exit;
}

$query = "
    SELECT
        p.pet_id,
        p.pet_name,
        p.breed,
        p.age,
        p.color,
        p.microchip_id,

        lp.last_seen_location,
        lp.lost_date,
        lp.description,
        lp.weight,
        lp.primary_color,
        lp.status

    FROM pets p
    INNER JOIN lost_pets lp 
        ON p.user_id = lp.user_id
        AND p.pet_name = lp.pet_name

    WHERE p.user_id = ?
      AND p.pet_name = ?
    LIMIT 1
";

$stmt = mysqli_prepare($conn, $query);
mysqli_stmt_bind_param($stmt, "is", $user_id, $pet_name);
mysqli_stmt_execute($stmt);

$result = mysqli_stmt_get_result($stmt);
$data = mysqli_fetch_assoc($result);

if (!$data) {
    echo json_encode([
        "status" => false,
        "message" => "Pet details not found"
    ]);
    exit;
}

/* Format lost date */
$lost_time = date("h:i A", strtotime($data['lost_date']));
$lost_day  = date("F j, Y", strtotime($data['lost_date']));

echo json_encode([
    "status" => true,
    "pet" => [
        "pet_name" => $data['pet_name'],
        "breed"    => $data['breed'],

        "age"      => $data['age'] . " years",
        "weight"   => $data['weight'] . " lbs",
        "color"    => !empty($data['color']) ? $data['color'] : $data['primary_color'],

        "last_seen_location" => $data['last_seen_location'],
        "last_seen_time"     => "Today at " . $lost_time,

        "description" => $data['description'],

        "microchip_id" => $data['microchip_id'],
        "status"       => $data['status']
    ]
]);

mysqli_close($conn);
?>
