<?php
header("Content-Type: application/json");
include "db.php";

$post_id = $_POST['post_id'] ?? '';
$user_id = $_POST['user_id'] ?? '';
$comment = $_POST['comment'] ?? '';

if (empty($post_id) || empty($user_id) || empty($comment)) {
    echo json_encode([
        "status" => false,
        "message" => "post_id, user_id and comment are required"
    ]);
    exit;
}

// Start transaction
mysqli_begin_transaction($conn);

try {

    // 1️⃣ Insert comment
    $insertQuery = "INSERT INTO post_comments (post_id, user_id, comment)
                    VALUES (?, ?, ?)";

    $stmt = mysqli_prepare($conn, $insertQuery);
    mysqli_stmt_bind_param($stmt, "iis", $post_id, $user_id, $comment);
    mysqli_stmt_execute($stmt);

    if (mysqli_stmt_affected_rows($stmt) <= 0) {
        throw new Exception("Failed to insert comment");
    }

    // 2️⃣ Increase comment count
    $updateQuery = "UPDATE community_posts
                    SET comments_count = comments_count + 1
                    WHERE post_id = ?";

    $stmt = mysqli_prepare($conn, $updateQuery);
    mysqli_stmt_bind_param($stmt, "i", $post_id);
    mysqli_stmt_execute($stmt);

    mysqli_commit($conn);

    echo json_encode([
        "status" => true,
        "message" => "Comment added successfully"
    ]);

} catch (Exception $e) {

    mysqli_rollback($conn);

    echo json_encode([
        "status" => false,
        "message" => "Failed to add comment"
    ]);
}

mysqli_close($conn);
?>
 