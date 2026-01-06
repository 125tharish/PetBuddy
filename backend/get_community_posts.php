<?php
header("Content-Type: application/json");
include "db.php";

$query = "
    SELECT
        cp.post_id,
        cp.content,
        cp.image_url,
        cp.created_at,
        cp.likes_count,
        cp.comments_count,
        u.name

    FROM community_posts cp
    INNER JOIN users u ON cp.user_id = u.user_id
    ORDER BY cp.created_at DESC
";

$result = mysqli_query($conn, $query);
$posts = [];

while ($row = mysqli_fetch_assoc($result)) {

    // Time ago calculation
    $minutes = floor((time() - strtotime($row['created_at'])) / 60);

    if ($minutes < 60) {
        $time_ago = $minutes . " min ago";
    } else {
        $time_ago = floor($minutes / 60) . " hours ago";
    }

    // User initial
    $initial = strtoupper(substr($row['name'], 0, 1));

    $posts[] = [
        "post_id" => $row['post_id'],
        "user_name" => $row['name'],
        "user_initial" => $initial,
        "time_ago" => $time_ago,
        "content" => $row['content'],
        "likes" => (int)$row['likes_count'],
        "comments" => (int)$row['comments_count'],
        "image_url" => $row['image_url'] // optional (can be null)
    ];
}

echo json_encode([
    "status" => true,
    "community_posts" => $posts
]);

mysqli_close($conn);
?>
