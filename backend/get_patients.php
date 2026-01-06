<?php
header("Content-Type: application/json");
include "db.php";

if ($_SERVER["REQUEST_METHOD"] !== "POST") {
    echo json_encode([
        "status" => false,
        "message" => "Invalid request method"
    ]);
    exit;
}

/* ---------- INPUT ---------- */
$search = trim($_POST['search'] ?? '');

/* ---------- QUERY ---------- */
$sql = "
SELECT
    p.pet_id,
    p.pet_name,
    p.pet_type,
    p.breed,
    p.age,

    u.name AS owner_name,

    COUNT(a.appointment_id) AS total_visits,
    MAX(a.appointment_date) AS last_visit,

    (
        SELECT appointment_date
        FROM appointments
        WHERE pet_id = p.pet_id
          AND appointment_date >= CURDATE()
        ORDER BY appointment_date ASC
        LIMIT 1
    ) AS next_visit

FROM pets p
JOIN users u ON u.user_id = p.user_id
LEFT JOIN appointments a ON a.pet_id = p.pet_id
WHERE
    p.pet_name LIKE ? OR
    u.name LIKE ? OR
    p.breed LIKE ?
GROUP BY p.pet_id
ORDER BY p.pet_name ASC
";


$stmt = $conn->prepare($sql);

$like = "%$search%";
$stmt->bind_param("sss", $like, $like, $like);

$stmt->execute();
$result = $stmt->get_result();

$patients = [];
while ($row = $result->fetch_assoc()) {
    $patients[] = $row;
}

echo json_encode([
    "status" => true,
    "total_patients" => count($patients),
    "patients" => $patients
]);

$stmt->close();
$conn->close();
?>
