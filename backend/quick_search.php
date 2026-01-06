<?php
header("Content-Type: application/json");
include "db.php";

/* ---------- INPUT ---------- */
$search = trim($_GET['search'] ?? '');
$status = trim($_GET['status'] ?? '');

/* ---------- QUERY ---------- */
$query = "SELECT 
            pet_name,
            breed,
            status,
            lost_date
          FROM lost_pets
          WHERE 1=1";

/* Search filter */
if (!empty($search)) {
    $search = mysqli_real_escape_string($conn, $search);
    $query .= " AND (pet_name LIKE '%$search%' OR breed LIKE '%$search%')";
}

/* Status filter */
if (!empty($status)) {
    $status = mysqli_real_escape_string($conn, $status);
    $query .= " AND status = '$status'";
}

$query .= " ORDER BY lost_date DESC";

/* ---------- FETCH ---------- */
$result = mysqli_query($conn, $query);
$pets = [];

while ($row = mysqli_fetch_assoc($result)) {

    $minutes = floor((time() - strtotime($row['lost_date'])) / 60);

    $timeAgo = ($minutes < 60)
        ? $minutes . " min ago"
        : floor($minutes / 60) . " hour ago";

    $pets[] = [
        "title"     => $row['breed'],      // e.g. Golden Retriever
        "pet_name"  => $row['pet_name'],
        "status"    => $row['status'],
        "time_ago"  => $timeAgo,
        "distance"  => rand(1, 30) / 10 . " mi away"
    ];
}

/* ---------- RESPONSE ---------- */
echo json_encode([
    "status" => true,
    "pets"   => $pets
]);

mysqli_close($conn);
?>
