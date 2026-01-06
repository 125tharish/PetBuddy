<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST, OPTIONS");
header("Access-Control-Allow-Headers: Content-Type");

require_once __DIR__ . "/db.php";

/* ---------- Handle CORS ---------- */
if ($_SERVER["REQUEST_METHOD"] === "OPTIONS") {
    http_response_code(200);
    exit();
}

if ($_SERVER["REQUEST_METHOD"] !== "POST") {
    echo json_encode([
        "status" => false,
        "message" => "Invalid request method"
    ]);
    exit;
}

/* ---------- FUNCTION: GET SYSTEM IPV4 (WIFI) ---------- */
function getSystemIPv4() {

    // First try (works in most cases)
    $ip = gethostbyname(gethostname());

    // If still localhost, try Windows ipconfig
    if ($ip === "127.0.0.1") {
        $output = shell_exec("ipconfig");
        if ($output) {
            preg_match('/IPv4 Address[.\s]*:\s*([\d\.]+)/', $output, $matches);
            if (!empty($matches[1])) {
                return $matches[1];
            }
        }
    }

    return $ip;
}

/* ---------- READ FORM DATA ---------- */
$user_id      = $_POST['user_id'] ?? '';
$pet_name     = $_POST['pet_name'] ?? '';
$pet_type     = $_POST['pet_type'] ?? '';
$breed        = $_POST['breed'] ?? '';
$color        = $_POST['color'] ?? '';
$age          = $_POST['age'] ?? '';
$microchip_id = $_POST['microchip_id'] ?? '';

if (empty($user_id) || empty($pet_name) || empty($pet_type)) {
    echo json_encode([
        "status" => false,
        "message" => "Required fields are missing"
    ]);
    exit;
}

/* ---------- IMAGE UPLOAD ---------- */
$pet_image_url = null;

if (isset($_FILES['pet_image']) && $_FILES['pet_image']['error'] === 0) {

    $upload_dir = "pets_images/";

    if (!is_dir($upload_dir)) {
        mkdir($upload_dir, 0777, true);
    }

    $extension = strtolower(pathinfo($_FILES['pet_image']['name'], PATHINFO_EXTENSION));
    $allowed = ['jpg', 'jpeg', 'png'];

    if (!in_array($extension, $allowed)) {
        echo json_encode([
            "status" => false,
            "message" => "Only JPG, JPEG, PNG images allowed"
        ]);
        exit;
    }

    $file_name = time() . "_" . uniqid() . "." . $extension;
    $target_path = $upload_dir . $file_name;

    if (!move_uploaded_file($_FILES['pet_image']['tmp_name'], $target_path)) {
        echo json_encode([
            "status" => false,
            "message" => "Image upload failed"
        ]);
        exit;
    }

    /* ---------- BUILD FULL IMAGE URL ---------- */
    $protocol = (!empty($_SERVER['HTTPS']) && $_SERVER['HTTPS'] !== 'off')
        ? "https://"
        : "http://";

    $server_ip = getSystemIPv4();
    $pet_image_url = $protocol . $server_ip . "/pet_buddy/" . $target_path;
}

/* ---------- INSERT INTO DATABASE ---------- */
$query = "
INSERT INTO pets
(user_id, pet_name, pet_type, breed, color, age, microchip_id, pet_image)
VALUES (?, ?, ?, ?, ?, ?, ?, ?)
";

$stmt = mysqli_prepare($conn, $query);
mysqli_stmt_bind_param(
    $stmt,
    "isssssss",
    $user_id,
    $pet_name,
    $pet_type,
    $breed,
    $color,
    $age,
    $microchip_id,
    $pet_image_url
);

if (mysqli_stmt_execute($stmt)) {
    echo json_encode([
        "status" => true,
        "message" => "Pet profile created successfully",
        "pet_image_url" => $pet_image_url
    ]);
} else {
    echo json_encode([
        "status" => false,
        "message" => "Failed to create pet profile"
    ]);
}

mysqli_stmt_close($stmt);
mysqli_close($conn);
?>