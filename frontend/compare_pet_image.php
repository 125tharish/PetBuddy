<?php
/**
 * AI Pet Image Comparison - NO API KEY REQUIRED
 * Uses PHP GD library (built into XAMPP) for image comparison
 */

header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight OPTIONS request
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

// Try to include db.php (for mysqli) or use inline database connection
$conn = null;
$pdo = null;

// Try to include db.php first
if (file_exists(__DIR__ . '/db.php')) {
    require_once __DIR__ . '/db.php';
} else {
    // If db.php doesn't exist, create connection inline
    $host = 'localhost';
    $dbname = 'petbuddy_db';
    $username = 'root';
    $password = '';
    
    // Try mysqli first
    $conn = new mysqli($host, $username, $password, $dbname);
    if ($conn->connect_error) {
        // If mysqli fails, try PDO
        try {
            $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8mb4", $username, $password);
            $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
        } catch (PDOException $e) {
            echo json_encode([
                'status' => 'error',
                'message' => 'Database connection failed: ' . $e->getMessage(),
                'matches' => []
            ]);
            exit;
        }
    }
}

$imageBase64 = $_POST['image_base64'] ?? null;
$userId = isset($_POST['user_id']) ? intval($_POST['user_id']) : null;

if (!$imageBase64) {
    echo json_encode([
        'status' => 'error',
        'message' => 'Image data is required',
        'matches' => []
    ]);
    exit;
}

try {
    // Decode base64 image
    $imageData = base64_decode($imageBase64);
    $uploadedImage = imagecreatefromstring($imageData);
    
    if (!$uploadedImage) {
        throw new Exception('Invalid image data');
    }
    
    // Get image dimensions
    $uploadedWidth = imagesx($uploadedImage);
    $uploadedHeight = imagesy($uploadedImage);
    
    // Get all lost pet reports with images
    $query = "SELECT 
        lr.lost_id,
        lr.pet_name,
        lr.pet_type,
        lr.breed,
        lr.location,
        lr.created_at,
        u.name as owner_name,
        lr.image_url
    FROM lost_pet_reports lr
    LEFT JOIN users u ON lr.user_id = u.user_id
    WHERE lr.image_url IS NOT NULL AND lr.image_url != ''
    ORDER BY lr.created_at DESC
    LIMIT 100";
    
    $matches = [];
    
    // Handle both mysqli and PDO
    if ($conn && $conn instanceof mysqli) {
        // Using mysqli
        $result = $conn->query($query);
        if (!$result) {
            throw new Exception('Database query failed: ' . $conn->error);
        }
        
        while ($row = $result->fetch_assoc()) {
        if (empty($row['image_url'])) continue;
        
        // Load comparison image from server
        $image2Path = $_SERVER['DOCUMENT_ROOT'] . '/pet_buddy/' . $row['image_url'];
        
        if (!file_exists($image2Path)) continue;
        
        $image2Data = file_get_contents($image2Path);
        $databaseImage = imagecreatefromstring($image2Data);
        
        if (!$databaseImage) continue;
        
        // Calculate similarity using multiple methods
        $similarity = calculateImageSimilarity($uploadedImage, $databaseImage);
        
        // Only include matches above 50% similarity
        if ($similarity >= 0.5) {
            $matches[] = [
                'lost_pet_id' => (int)$row['lost_id'],
                'pet_name' => $row['pet_name'],
                'pet_type' => $row['pet_type'],
                'breed' => $row['breed'],
                'similarity' => round($similarity, 2),
                'image_url' => $row['image_url'],
                'owner_name' => $row['owner_name'],
                'location' => $row['location']
            ];
        }
        
            imagedestroy($databaseImage);
        }
    } else if ($pdo && $pdo instanceof PDO) {
        // Using PDO
        $stmt = $pdo->prepare($query);
        $stmt->execute();
        $rows = $stmt->fetchAll(PDO::FETCH_ASSOC);
        
        foreach ($rows as $row) {
            if (empty($row['image_url'])) continue;
            
            // Load comparison image from server
            $image2Path = $_SERVER['DOCUMENT_ROOT'] . '/pet_buddy/' . $row['image_url'];
            
            if (!file_exists($image2Path)) continue;
            
            $image2Data = file_get_contents($image2Path);
            $databaseImage = imagecreatefromstring($image2Data);
            
            if (!$databaseImage) continue;
            
            // Calculate similarity using multiple methods
            $similarity = calculateImageSimilarity($uploadedImage, $databaseImage);
            
            // Only include matches above 50% similarity
            if ($similarity >= 0.5) {
                $matches[] = [
                    'lost_pet_id' => (int)$row['lost_id'],
                    'pet_name' => $row['pet_name'],
                    'pet_type' => $row['pet_type'],
                    'breed' => $row['breed'],
                    'similarity' => round($similarity, 2),
                    'image_url' => $row['image_url'],
                    'owner_name' => $row['owner_name'],
                    'location' => $row['location']
                ];
            }
            
            imagedestroy($databaseImage);
        }
    } else {
        throw new Exception('No database connection available');
    }
    
    imagedestroy($uploadedImage);
    
    // Sort by similarity (highest first)
    usort($matches, function($a, $b) {
        return $b['similarity'] <=> $a['similarity'];
    });
    
    // Return top 10 matches
    $matches = array_slice($matches, 0, 10);
    
    $topSimilarity = !empty($matches) ? $matches[0]['similarity'] : 0;
    $confidence = !empty($matches) ? min(1.0, $topSimilarity * 1.1) : 0;
    
    echo json_encode([
        'status' => 'success',
        'similarity_score' => $topSimilarity,
        'confidence' => $confidence,
        'matches' => $matches,
        'message' => count($matches) . ' potential match(es) found'
    ], JSON_UNESCAPED_UNICODE);
    
} catch (Exception $e) {
    echo json_encode([
        'status' => 'error',
        'message' => $e->getMessage(),
        'matches' => []
    ], JSON_UNESCAPED_UNICODE);
}

/**
 * Calculate image similarity using histogram and feature comparison
 * NO API KEY REQUIRED - Uses PHP GD library only
 */
function calculateImageSimilarity($image1, $image2) {
    // Resize both images to same size for comparison
    $width = 128;
    $height = 128;
    
    $resized1 = imagecreatetruecolor($width, $height);
    $resized2 = imagecreatetruecolor($width, $height);
    
    imagecopyresampled($resized1, $image1, 0, 0, 0, 0, $width, $height, imagesx($image1), imagesy($image1));
    imagecopyresampled($resized2, $image2, 0, 0, 0, 0, $width, $height, imagesx($image2), imagesy($image2));
    
    // Method 1: Color Histogram Comparison
    $hist1 = calculateColorHistogram($resized1);
    $hist2 = calculateColorHistogram($resized2);
    $histogramSimilarity = compareHistograms($hist1, $hist2);
    
    // Method 2: Edge Detection Comparison
    $edges1 = detectEdges($resized1);
    $edges2 = detectEdges($resized2);
    $edgeSimilarity = compareEdges($edges1, $edges2);
    
    // Method 3: Average Color Comparison
    $avgColor1 = getAverageColor($resized1);
    $avgColor2 = getAverageColor($resized2);
    $colorSimilarity = compareColors($avgColor1, $avgColor2);
    
    // Combine all methods (weighted average)
    $finalSimilarity = (
        $histogramSimilarity * 0.5 +  // 50% weight
        $edgeSimilarity * 0.3 +        // 30% weight
        $colorSimilarity * 0.2          // 20% weight
    );
    
    imagedestroy($resized1);
    imagedestroy($resized2);
    
    return max(0, min(1, $finalSimilarity));
}

/**
 * Calculate color histogram
 */
function calculateColorHistogram($image) {
    $histogram = [];
    $width = imagesx($image);
    $height = imagesy($image);
    
    // Initialize bins (16 bins for each RGB channel = 48 total)
    for ($i = 0; $i < 48; $i++) {
        $histogram[$i] = 0;
    }
    
    for ($x = 0; $x < $width; $x++) {
        for ($y = 0; $y < $height; $y++) {
            $rgb = imagecolorat($image, $x, $y);
            $r = ($rgb >> 16) & 0xFF;
            $g = ($rgb >> 8) & 0xFF;
            $b = $rgb & 0xFF;
            
            // Quantize to 16 bins
            $rBin = floor($r / 16);
            $gBin = floor($g / 16) + 16;
            $bBin = floor($b / 16) + 32;
            
            $histogram[$rBin]++;
            $histogram[$gBin]++;
            $histogram[$bBin]++;
        }
    }
    
    // Normalize
    $total = $width * $height * 3;
    foreach ($histogram as $key => $value) {
        $histogram[$key] = $value / $total;
    }
    
    return $histogram;
}

/**
 * Compare two histograms using correlation
 */
function compareHistograms($hist1, $hist2) {
    $sum1 = 0;
    $sum2 = 0;
    $sumSq1 = 0;
    $sumSq2 = 0;
    $sumProd = 0;
    $n = count($hist1);
    
    for ($i = 0; $i < $n; $i++) {
        $val1 = $hist1[$i] ?? 0;
        $val2 = $hist2[$i] ?? 0;
        
        $sum1 += $val1;
        $sum2 += $val2;
        $sumSq1 += $val1 * $val1;
        $sumSq2 += $val2 * $val2;
        $sumProd += $val1 * $val2;
    }
    
    $num = ($n * $sumProd) - ($sum1 * $sum2);
    $den = sqrt((($n * $sumSq1) - ($sum1 * $sum1)) * (($n * $sumSq2) - ($sum2 * $sum2)));
    
    if ($den == 0) return 0;
    
    $correlation = $num / $den;
    return ($correlation + 1) / 2; // Normalize to 0-1
}

/**
 * Simple edge detection using Sobel operator
 */
function detectEdges($image) {
    $width = imagesx($image);
    $height = imagesy($image);
    $edges = [];
    
    // Convert to grayscale first
    $gray = imagecreatetruecolor($width, $height);
    for ($x = 0; $x < $width; $x++) {
        for ($y = 0; $y < $height; $y++) {
            $rgb = imagecolorat($image, $x, $y);
            $r = ($rgb >> 16) & 0xFF;
            $g = ($rgb >> 8) & 0xFF;
            $b = $rgb & 0xFF;
            $grayVal = (int)(0.299 * $r + 0.587 * $g + 0.114 * $b);
            imagesetpixel($gray, $x, $y, imagecolorallocate($gray, $grayVal, $grayVal, $grayVal));
        }
    }
    
    // Simple edge detection (simplified Sobel)
    for ($x = 1; $x < $width - 1; $x++) {
        for ($y = 1; $y < $height - 1; $y++) {
            $p1 = imagecolorat($gray, $x - 1, $y) & 0xFF;
            $p2 = imagecolorat($gray, $x + 1, $y) & 0xFF;
            $p3 = imagecolorat($gray, $x, $y - 1) & 0xFF;
            $p4 = imagecolorat($gray, $x, $y + 1) & 0xFF;
            
            $edge = abs($p1 - $p2) + abs($p3 - $p4);
            $edges[$x][$y] = $edge > 30 ? 1 : 0; // Threshold
        }
    }
    
    imagedestroy($gray);
    return $edges;
}

/**
 * Compare edge patterns
 */
function compareEdges($edges1, $edges2) {
    $matches = 0;
    $total = 0;
    
    foreach ($edges1 as $x => $row) {
        foreach ($row as $y => $val) {
            $total++;
            if (isset($edges2[$x][$y]) && $edges2[$x][$y] == $val) {
                $matches++;
            }
        }
    }
    
    return $total > 0 ? $matches / $total : 0;
}

/**
 * Get average color of image
 */
function getAverageColor($image) {
    $width = imagesx($image);
    $height = imagesy($image);
    $totalR = 0;
    $totalG = 0;
    $totalB = 0;
    $pixelCount = $width * $height;
    
    for ($x = 0; $x < $width; $x++) {
        for ($y = 0; $y < $height; $y++) {
            $rgb = imagecolorat($image, $x, $y);
            $totalR += ($rgb >> 16) & 0xFF;
            $totalG += ($rgb >> 8) & 0xFF;
            $totalB += $rgb & 0xFF;
        }
    }
    
    return [
        'r' => $totalR / $pixelCount,
        'g' => $totalG / $pixelCount,
        'b' => $totalB / $pixelCount
    ];
}

/**
 * Compare average colors
 */
function compareColors($color1, $color2) {
    $diffR = abs($color1['r'] - $color2['r']);
    $diffG = abs($color1['g'] - $color2['g']);
    $diffB = abs($color1['b'] - $color2['b']);
    
    $maxDiff = 255 * 3;
    $totalDiff = $diffR + $diffG + $diffB;
    
    return 1 - ($totalDiff / $maxDiff);
}

// Close database connection
if ($conn && $conn instanceof mysqli) {
    $conn->close();
}
?>

