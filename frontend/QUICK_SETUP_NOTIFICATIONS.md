# Quick Setup: Lost Pet Notifications

## Problem
After reporting a lost pet, notifications are not being created for other users.

## Solution
Two approaches have been implemented:

### Approach 1: Backend Integration (Recommended)
The backend automatically creates notifications when a lost pet is reported.

**Steps:**
1. Copy these files to `C:\xampp\htdocs\pet_buddy\`:
   - `notification_helper.php`
   - `report_pet_details.php` (replace your existing file)

2. Make sure the `notifications` table exists:
   - Run the SQL script: `create_notifications_table.sql`

3. Test:
   - Report a lost pet from the app
   - Check the `notifications` table in your database
   - All users (except the reporter) should have a new notification

### Approach 2: Android App Integration (Backup)
The Android app also calls the notification endpoint after successfully reporting.

**Files already updated:**
- `ApiService.kt` - Added `createLostPetNotifications` endpoint
- `LostPetRepository.kt` - Added `createLostPetNotifications` function
- `PetDetailsScreen.kt` - Calls notification creation after successful report

**Backend file needed:**
- `create_notification_on_lost_pet_report.php` (already created)

## Files to Copy to Server

Copy these files to `C:\xampp\htdocs\pet_buddy\`:

1. **notification_helper.php** - Helper function for creating notifications
2. **report_pet_details.php** - Complete file with notification integration (replace existing)
3. **create_notification_on_lost_pet_report.php** - Standalone endpoint (for Android app approach)

## Database Setup

Make sure you have run:
```sql
-- See create_notifications_table.sql
```

## Testing

1. **Report a lost pet:**
   - Open the app
   - Go to Home → Report Lost Pet
   - Fill in pet details and submit

2. **Check notifications:**
   - Open the app with a different user account
   - Go to Notifications
   - You should see a "Lost Pet Alert" notification

3. **Check database:**
   ```sql
   SELECT * FROM notifications WHERE type = 'lost_pet_alert' ORDER BY created_at DESC;
   ```

## Troubleshooting

### No notifications created:
1. Check if `notification_helper.php` is in the same directory as `report_pet_details.php`
2. Check PHP error logs: `C:\xampp\php\logs\php_error_log`
3. Verify database connection in PHP files
4. Make sure the `notifications` table exists

### Notifications not showing in app:
1. Check if `get_lost_pet_notifications.php` is working
2. Verify the user_id is correct when fetching notifications
3. Check the notification response format

### Error: "Call to undefined function createLostPetNotifications":
- Make sure `notification_helper.php` is included in `report_pet_details.php`
- Check file paths are correct

## What Happens Now

When a user reports a lost pet:
1. The lost pet report is created in the database
2. Notifications are automatically created for ALL other users
3. Each notification includes:
   - Pet name, type, breed
   - Owner name
   - Alert message
4. Users see the notification in their Notifications screen

## Next Steps

1. Copy the PHP files to your server
2. Replace your existing `report_pet_details.php` with the new one
3. Test by reporting a lost pet
4. Verify notifications appear for other users

