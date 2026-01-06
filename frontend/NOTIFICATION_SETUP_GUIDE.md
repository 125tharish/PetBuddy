# Lost Pet Notification Setup Guide

This guide explains how to set up automatic notifications for all users when a lost pet is reported.

## Overview

When a user reports a lost pet, all other users in the system should receive a notification. This can be implemented in two ways:

1. **Backend Integration (Recommended)**: The backend automatically creates notifications when a lost pet report is created.
2. **Android App Integration**: The Android app calls a notification endpoint after successfully creating a lost pet report.

## Option 1: Backend Integration (Recommended)

### Step 1: Copy Files to Server

Copy these files to your `C:\xampp\htdocs\pet_buddy\` directory:
- `notification_helper.php` - Helper function for creating notifications
- `create_notification_on_lost_pet_report.php` - Standalone endpoint (optional)

### Step 2: Update report_pet_details.php

Open your existing `report_pet_details.php` file and add the following code:

**At the top of the file (after database connection):**
```php
require_once 'notification_helper.php';
```

**After successfully inserting the lost pet report (after getting `$lost_id`):**
```php
// Create notifications for all users
$notificationResult = createLostPetNotifications(
    $pdo,
    $lost_id,
    $user_id,
    $pet_name,
    $pet_type,
    $breed,
    null, // location (can be updated later)
    null, // lost_date (can be updated later)
    null  // owner_name (will be fetched from users table)
);

// Optional: Log the result
error_log("Notifications created: " . $notificationResult['notifications_created']);
```

### Step 3: Test

1. Report a lost pet from the Android app
2. Check the `notifications` table in your database
3. Verify that all users (except the reporter) received a notification

## Option 2: Android App Integration

If you prefer to handle notifications from the Android app:

### Step 1: Add API Endpoint to ApiService

The Android app can call `create_notification_on_lost_pet_report.php` after successfully creating a lost pet report.

### Step 2: Update PetDetailsScreen.kt

After a successful report, call the notification creation endpoint.

## Database Requirements

Make sure the `notifications` table exists. Run the SQL script:
```sql
-- See create_notifications_table.sql
```

## Notification Details

When a lost pet is reported, notifications are created with:
- **Type**: `lost_pet_alert`
- **Title**: "Lost Pet Alert: [Pet Name]"
- **Description**: Includes pet name, breed, type, location (if available), lost date (if available), and owner name
- **Recipients**: All users except the one who reported the lost pet
- **Status**: Unread by default

## Troubleshooting

1. **No notifications created**: 
   - Check if the `notifications` table exists
   - Verify database connection in PHP files
   - Check PHP error logs

2. **Notifications not showing in app**:
   - Verify the `get_lost_pet_notifications.php` endpoint is working
   - Check that notifications are being fetched correctly

3. **Missing owner name**:
   - The function will automatically fetch the owner name from the `users` table
   - Make sure the `users` table has a `name` column

## Files Created

1. **notification_helper.php**: Helper function that can be included in existing PHP files
2. **create_notification_on_lost_pet_report.php**: Standalone endpoint for creating notifications
3. **example_report_pet_details_with_notifications.php**: Example showing how to integrate into existing code

## Next Steps

1. Choose Option 1 (Backend) or Option 2 (Android App)
2. Copy the necessary files to your server
3. Update your existing `report_pet_details.php` file
4. Test by reporting a lost pet
5. Verify notifications appear for all users

