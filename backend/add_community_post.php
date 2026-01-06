<?php
header("Content-Type: application/json");
include "db.php";

/* ---------- FUNCTION TO GET IPV4 ---------- */
function getServerIPv4() {
    // If accessed via IP, use it
    if (!empty($_SERVER['HTTP_HOST']) && $_SERVER['HTTP_HOST'] !== 'localhost') {
        return $_SERVER['HTTP_HOST'];
    }

    // Fallback to SERVER_ADDR (ignore IPv6)
    if (!empty($_SERVER['SERVER_ADDR']) && $_SERVER['SERVER_ADDR'] !== '::1') {
        return $_SERVER['SERVER_ADDR'];
    }

    // Last fallback
    return gethostbyname(gethostname());
}

if ($_SERVER['REQUEST_METHOD'] === "POST") {

    $user_id = $_POST['user_id'] ?? '';
    $content = $_POST['content'] ?? '';

    if (empty($user_id) || empty($content)) {
        echo json_encode([
            "status" => false,
            "message" => "user_id and content are required"
        ]);
        exit;
    }

    $imageUrl = null;

    /* ---------- IMAGE UPLOAD ---------- */
    if (!empty($_FILES['photo']['name'])) {

        // 📁 Physical upload path
        $uploadDir = $_SERVER['DOCUMENT_ROOT'] . "/Pet Buddy/community_posts/";
        if (!is_dir($uploadDir)) {
            mkdir($uploadDir, 0777, true);
        }

        // 🔐 Unique file name
        $fileName = time() . "_" . basename($_FILES['photo']['name']);
        $targetPath = $uploadDir . $fileName;

        if (move_uploaded_file($_FILES['photo']['tmp_name'], $targetPath)) {

            // 🌐 Get correct IPv4
            $ip = getServerIPv4();

            // 🔗 Build public image URL
            $imageUrl = "http://" . $ip . "/Pet%20Buddy/community_posts/" . $fileName;
        }
    }

    /* ---------- INSERT POST ---------- */
    $query = "INSERT INTO community_posts (user_id, content, image_url)
              VALUES (?, ?, ?)";

    $stmt = mysqli_prepare($conn, $query);
    mysqli_stmt_bind_param($stmt, "iss", $user_id, $content, $imageUrl);

    if (mysqli_stmt_execute($stmt)) {
        echo json_encode([
            "status" => true,
            "message" => "Post shared successfully",
            "image_url" => $imageUrl
        ]);
    } else {
        echo json_encode([
            "status" => false,
            "message" => "Failed to post"
        ]);
    }

    mysqli_close($conn);
}
?>
