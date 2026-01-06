<?php
header("Content-Type: application/json");
include "db.php";

$query = "SELECT 
            lost_id,
            pet_name,
            breed,
            status,
            lost_date
          FROM lost_pets
          ORDER BY lost_date DESC";

$result = mysqli_query($conn, $query);

$pets = [];

while ($row = mysqli_fetch_assoc($result)) {

    // Calculate "time ago" from lost_date
    $minutes_ago = floor((time() - strtotime($row['lost_date'])) / 60);

    if ($minutes_ago < 60) {
        $time_ago = $minutes_ago . " min ago";
    } else {
        $time_ago = floor($minutes_ago / 60) . " hour ago";
    }

    $pets[] = [
        "lost_id"  => $row['lost_id'],
        "pet_name" => $row['pet_name'],
        "breed"    => $row['breed'],
        "status"   => $row['status'],   // LOST or FOUND
        "time_ago" => $time_ago,
        "distance" => rand(1, 20) / 10 . " mi away" // demo distance
    ];
}

echo json_encode([
    "status" => true,
    "pets" => $pets
]);

mysqli_close($conn);
?>
