<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST, OPTIONS");
header("Access-Control-Allow-Headers: Content-Type");

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

include "db.php";

$user_id = $_POST['user_id'] ?? '';

if (empty($user_id)) {
    echo json_encode([
        "status" => false,
        "message" => "user_id is required"
    ]);
    exit;
}

// 1️⃣ Pets count
$petsQuery = "SELECT COUNT(*) AS total_pets 
              FROM pets 
              WHERE user_id = ?";
$stmt = $conn->prepare($petsQuery);
$stmt->bind_param("i", $user_id);
$stmt->execute();
$petsResult = $stmt->get_result();
$petsCount = $petsResult->fetch_assoc()['total_pets'] ?? 0;

// 2️⃣ Posts count
$postsQuery = "SELECT COUNT(*) AS total_posts 
               FROM community_posts 
               WHERE user_id = ?";
$stmt = $conn->prepare($postsQuery);
$stmt->bind_param("i", $user_id);
$stmt->execute();
$postsResult = $stmt->get_result();
$postsCount = $postsResult->fetch_assoc()['total_posts'] ?? 0;

// 3️⃣ Helped count (total likes received)
$helpedQuery = "SELECT IFNULL(SUM(likes_count), 0) AS total_helped 
                FROM community_posts 
                WHERE user_id = ?";
$stmt = $conn->prepare($helpedQuery);
$stmt->bind_param("i", $user_id);
$stmt->execute();
$helpedResult = $stmt->get_result();
$helpedCount = $helpedResult->fetch_assoc()['total_helped'] ?? 0;

echo json_encode([
    "status" => true,
    "pets" => (int)$petsCount,
    "posts" => (int)$postsCount,
    "helped" => (int)$helpedCount
]);

$conn->close();
?>
