<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST, OPTIONS");
header("Access-Control-Allow-Headers: Content-Type");
include "db.php";

// Handle OPTIONS request for CORS
if ($_SERVER["REQUEST_METHOD"] === "OPTIONS") {
    http_response_code(200);
    exit();
}

/* ---------- FUNCTION TO GET IPV4 ---------- */
function getServerIPv4() {
    if (!empty($_SERVER['HTTP_HOST']) && $_SERVER['HTTP_HOST'] !== 'localhost') {
        return $_SERVER['HTTP_HOST'];
    }

    if (!empty($_SERVER['SERVER_ADDR']) && $_SERVER['SERVER_ADDR'] !== '::1') {
        return $_SERVER['SERVER_ADDR'];
    }

    return gethostbyname(gethostname());
}

if ($_SERVER['REQUEST_METHOD'] === "POST") {
    /* ---------- INPUT ---------- */
    $user_id  = $_POST['user_id'] ?? '';
    $pet_id   = $_POST['pet_id'] ?? '';
    $title    = $_POST['title'] ?? '';
    $file_url = $_POST['file_url'] ?? '';

    if (empty($user_id) || empty($pet_id) || empty($title) || empty($file_url)) {
        echo json_encode([
            "status" => false,
            "message" => "user_id, pet_id, title and file_url are required"
        ]);
        exit;
    }

/* ---------- UPLOAD DIRECTORY ---------- */
$uploadDir = $_SERVER['DOCUMENT_ROOT'] . "/Pet Buddy/Medical_rec/";
if (!is_dir($uploadDir)) {
    mkdir($uploadDir, 0777, true);
}

/* ---------- DOWNLOAD FILE ---------- */
$fileContent = @file_get_contents($file_url);
if ($fileContent === false) {
    echo json_encode([
        "status" => false,
        "message" => "Failed to download file from link"
    ]);
    exit;
}

/* ---------- SAVE FILE ---------- */
$originalName = basename(parse_url($file_url, PHP_URL_PATH));
$fileName = time() . "_" . $originalName;
$targetPath = $uploadDir . $fileName;

file_put_contents($targetPath, $fileContent);

/* ---------- GENERATE IPV4 URL ---------- */
$ip = getServerIPv4();
$localUrl = "http://" . $ip . "/Pet%20Buddy/Medical_rec/" . $fileName;

/* ---------- INSERT INTO DB ---------- */
$query = "INSERT INTO medical_records (user_id, pet_id, title, file_url)
          VALUES (?, ?, ?, ?)";

$stmt = mysqli_prepare($conn, $query);
mysqli_stmt_bind_param($stmt, "iiss", $user_id, $pet_id, $title, $localUrl);

    if (mysqli_stmt_execute($stmt)) {
        echo json_encode([
            "status" => true,
            "message" => "Medical record saved successfully",
            "file_url" => $localUrl
        ]);
    } else {
        echo json_encode([
            "status" => false,
            "message" => "Database insert failed"
        ]);
    }

    mysqli_stmt_close($stmt);
    mysqli_close($conn);
} else {
    echo json_encode([
        "status" => false,
        "message" => "Invalid request method"
    ]);
}
?>
