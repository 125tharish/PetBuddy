<?php
header("Content-Type: application/json");
include "db.php";

$post_id = $_POST['post_id'] ?? '';

$query = "SELECT
            pc.comment_id,
            pc.comment,
            pc.created_at,
            u.name
          FROM post_comments pc
          INNER JOIN users u ON pc.user_id = u.user_id
          WHERE pc.post_id = ?
          ORDER BY pc.created_at ASC";

$stmt = mysqli_prepare($conn, $query);
mysqli_stmt_bind_param($stmt, "i", $post_id);
mysqli_stmt_execute($stmt);

$result = mysqli_stmt_get_result($stmt);
$comments = [];

while ($row = mysqli_fetch_assoc($result)) {

    $comments[] = [
        "comment_id" => $row['comment_id'],
        "user_name" => $row['name'],
        "comment" => $row['comment'],
        "time" => date("h:i A", strtotime($row['created_at']))
    ];
}

echo json_encode([
    "status" => true,
    "comments" => $comments
]);

mysqli_close($conn);
?>
