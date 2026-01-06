<?php
header("Content-Type: application/json");
include "db.php";

$post_id = $_POST['post_id'] ?? '';
$user_id = $_POST['user_id'] ?? '';

if (empty($post_id) || empty($user_id)) {
    echo json_encode([
        "status" => false,
        "message" => "post_id and user_id are required"
    ]);
    exit;
}

// Start transaction (VERY IMPORTANT)
mysqli_begin_transaction($conn);

/* Check if user already liked */
$checkQuery = "SELECT 1 
               FROM post_likes 
               WHERE post_id = ? AND user_id = ?";

$stmt = mysqli_prepare($conn, $checkQuery);
mysqli_stmt_bind_param($stmt, "ii", $post_id, $user_id);
mysqli_stmt_execute($stmt);
$result = mysqli_stmt_get_result($stmt);

if (mysqli_fetch_assoc($result)) {

    // 🔴 UNLIKE
    $deleteQuery = "DELETE FROM post_likes 
                    WHERE post_id = ? AND user_id = ?";
    $stmt = mysqli_prepare($conn, $deleteQuery);
    mysqli_stmt_bind_param($stmt, "ii", $post_id, $user_id);
    mysqli_stmt_execute($stmt);

    if (mysqli_stmt_affected_rows($stmt) > 0) {
        $updateQuery = "UPDATE community_posts
                        SET likes_count = IF(likes_count > 0, likes_count - 1, 0)
                        WHERE post_id = ?";
        $stmt = mysqli_prepare($conn, $updateQuery);
        mysqli_stmt_bind_param($stmt, "i", $post_id);
        mysqli_stmt_execute($stmt);
    }

    mysqli_commit($conn);

    echo json_encode([
        "status" => true,
        "action" => "unliked"
    ]);

} else {

    // 🟢 LIKE
    $insertQuery = "INSERT INTO post_likes (post_id, user_id)
                    VALUES (?, ?)";
    $stmt = mysqli_prepare($conn, $insertQuery);
    mysqli_stmt_bind_param($stmt, "ii", $post_id, $user_id);

    if (mysqli_stmt_execute($stmt)) {
        $updateQuery = "UPDATE community_posts
                        SET likes_count = likes_count + 1
                        WHERE post_id = ?";
        $stmt = mysqli_prepare($conn, $updateQuery);
        mysqli_stmt_bind_param($stmt, "i", $post_id);
        mysqli_stmt_execute($stmt);
    }

    mysqli_commit($conn);

    echo json_encode([
        "status" => true,
        "action" => "liked"
    ]);
}

mysqli_close($conn);
?>
