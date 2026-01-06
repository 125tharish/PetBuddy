<?php
header("Content-Type: application/json");
include "db.php";

$comment_id = $_POST['comment_id'] ?? '';
$post_id    = $_POST['post_id'] ?? '';
$user_id    = $_POST['user_id'] ?? '';

if (empty($comment_id) || empty($post_id) || empty($user_id)) {
    echo json_encode([
        "status" => false,
        "message" => "comment_id, post_id and user_id are required"
    ]);
    exit;
}

// Start transaction
mysqli_begin_transaction($conn);

// Check ownership
$checkQuery = "SELECT 1 FROM post_comments
               WHERE comment_id = ? AND user_id = ?";
$stmt = mysqli_prepare($conn, $checkQuery);
mysqli_stmt_bind_param($stmt, "ii", $comment_id, $user_id);
mysqli_stmt_execute($stmt);
$result = mysqli_stmt_get_result($stmt);

if (!mysqli_fetch_assoc($result)) {
    echo json_encode([
        "status" => false,
        "message" => "Unauthorized or comment not found"
    ]);
    exit;
}

// Delete comment
$deleteQuery = "DELETE FROM post_comments WHERE comment_id = ?";
$stmt = mysqli_prepare($conn, $deleteQuery);
mysqli_stmt_bind_param($stmt, "i", $comment_id);
mysqli_stmt_execute($stmt);

// Decrease count safely
$updateQuery = "UPDATE community_posts
                SET comments_count = IF(comments_count > 0, comments_count - 1, 0)
                WHERE post_id = ?";
$stmt = mysqli_prepare($conn, $updateQuery);
mysqli_stmt_bind_param($stmt, "i", $post_id);
mysqli_stmt_execute($stmt);

mysqli_commit($conn);

echo json_encode([
    "status" => true,
    "message" => "Comment deleted successfully"
]);

mysqli_close($conn);
?>
