# Quick Fix: Lost Pet Notifications Not Showing

## Problem
After reporting a lost pet, notifications are not appearing in the notifications screen.

## Quick Solution Checklist

### ✅ Step 1: Copy PHP Files to Server

Copy these files to `C:\xampp\htdocs\pet_buddy\`:

1. **`create_notification_on_lost_pet_report.php`** - Standalone notification creation endpoint
2. **`get_lost_pet_notifications_mysqli.php`** - Rename to `get_lost_pet_notifications.php` (if you use mysqli)
   OR keep `get_lost_pet_notifications.php` (if you use PDO)

### ✅ Step 2: Verify Database Table

Run this SQL in phpMyAdmin:

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
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at)
);
```

### ✅ Step 3: Test

1. **Report a lost pet** from the app
2. **Check database**:
   ```sql
   SELECT * FROM notifications ORDER BY created_at DESC LIMIT 5;
   ```
3. **Open notifications screen** in the app
4. **Pull to refresh** the notifications

## How It Works

1. When you report a lost pet, the Android app calls `createLostPetNotifications()` API
2. This creates notifications for all users (except the reporter)
3. The notifications screen fetches notifications using `get_lost_pet_notifications.php`
4. Notifications are displayed with filtering options

## Troubleshooting

**If notifications still don't show:**

1. **Check if notifications were created in database:**
   ```sql
   SELECT COUNT(*) FROM notifications WHERE type = 'lost_pet_alert';
   ```

2. **Check your db.php file** - Make sure it creates `$conn` (mysqli) or `$pdo` (PDO)

3. **Check Android logcat** for API errors

4. **Test PHP endpoint directly:**
   - Open browser: `http://10.129.3.54/pet_buddy/get_lost_pet_notifications.php`
   - Use Postman to POST with `user_id=1`

5. **Verify user_id matches** - The user_id used to report must match the user_id used to fetch notifications

## Files Created

- ✅ `create_notification_on_lost_pet_report.php` - Standalone endpoint
- ✅ `get_lost_pet_notifications_mysqli.php` - mysqli version
- ✅ `notification_helper_mysqli.php` - Helper function (mysqli)
- ✅ `report_pet_details_mysqli.php` - Example integration (mysqli)

## Next Steps

After copying files, test the flow and check the database to verify notifications are being created.

