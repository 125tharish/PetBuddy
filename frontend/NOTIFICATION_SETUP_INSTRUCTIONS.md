# Notification Setup Instructions

## Problem
After reporting a lost pet, notifications are not showing in the notifications screen.

## Solution
You need to ensure that:
1. The `notifications` table exists in your database
2. Your `report_pet_details.php` creates notifications after reporting
3. Your `get_lost_pet_notifications.php` correctly fetches notifications

## Step 1: Check Database Table

Run this SQL to create the notifications table (if it doesn't exist):

```sql
CREATE TABLE IF NOT EXISTS notifications (
    notification_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    lost_pet_id INT NULL,
    pet_id INT NULL,
    type VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    pet_name VARCHAR(100),
    pet_type VARCHAR(50),
    breed VARCHAR(100),
    location VARCHAR(255),
    lost_date DATE,
    owner_name VARCHAR(100),
    timestamp DATETIME,
    created_at DATETIME NOT NULL,
    is_unread TINYINT(1) DEFAULT 1,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (lost_pet_id) REFERENCES lost_pet_reports(lost_id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at)
);
```

## Step 2: Update Your Backend Files

### Option A: If you use mysqli (recommended if your other files use mysqli)

1. **Copy `notification_helper_mysqli.php`** to `C:\xampp\htdocs\pet_buddy\`
2. **Copy `report_pet_details_mysqli.php`** to `C:\xampp\htdocs\pet_buddy\` and rename it to `report_pet_details.php` (backup your old one first!)
3. **Copy `get_lost_pet_notifications_mysqli.php`** to `C:\xampp\htdocs\pet_buddy\` and rename it to `get_lost_pet_notifications.php` (backup your old one first!)

### Option B: If you use PDO

1. **Copy `notification_helper.php`** to `C:\xampp\htdocs\pet_buddy\`
2. **Use the existing `report_pet_details.php`** (it should already have notification creation)
3. **Use the existing `get_lost_pet_notifications.php`**

## Step 3: Verify Your db.php File

Make sure your `db.php` file creates a `$conn` variable (for mysqli) or `$pdo` variable (for PDO).

**For mysqli:**
```php
<?php
$host = 'localhost';
$dbname = 'petbuddy_db';
$username = 'root';
$password = '';

$conn = mysqli_connect($host, $username, $password, $dbname);
if (!$conn) {
    die("Connection failed: " . mysqli_connect_error());
}
?>
```

**For PDO:**
```php
<?php
$host = 'localhost';
$dbname = 'petbuddy_db';
$username = 'root';
$password = '';

try {
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8mb4", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
} catch (PDOException $e) {
    die("Connection failed: " . $e->getMessage());
}
?>
```

## Step 4: Test the Flow

1. **Report a lost pet** from the app
2. **Check the database** to see if notifications were created:
   ```sql
   SELECT * FROM notifications ORDER BY created_at DESC LIMIT 10;
   ```
3. **Open the notifications screen** in the app
4. **Refresh** the notifications screen

## Step 5: Debugging

If notifications still don't show:

1. **Check server logs** in XAMPP for PHP errors
2. **Test the PHP endpoint directly** using Postman or curl:
   ```bash
   curl -X POST http://10.129.3.54/pet_buddy/get_lost_pet_notifications.php \
     -d "user_id=1"
   ```
3. **Check the Android logcat** for API errors
4. **Verify the user_id** matches between the report and notification fetch

## Important Notes

- The notification is created **after** the lost pet report is successfully saved
- Notifications are sent to **all users except the one who reported** the lost pet
- The notification type is `lost_pet_alert`
- Make sure your `lost_pet_reports` table exists and has the correct structure

