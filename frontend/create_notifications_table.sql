-- Create notifications table for lost pet reports
-- This table stores notifications for users about lost pet reports, matches, and sightings

CREATE TABLE IF NOT EXISTS `notifications` (
  `notification_id` INT(11) NOT NULL AUTO_INCREMENT,
  `user_id` INT(11) NOT NULL COMMENT 'User who will receive this notification',
  `lost_pet_id` INT(11) DEFAULT NULL COMMENT 'Reference to lost_pet_reports table if applicable',
  `type` VARCHAR(50) NOT NULL COMMENT 'Type: lost_pet_alert, match, sighting, etc.',
  `title` VARCHAR(255) NOT NULL COMMENT 'Notification title',
  `description` TEXT NOT NULL COMMENT 'Notification description/message',
  `pet_name` VARCHAR(100) DEFAULT NULL COMMENT 'Name of the lost pet',
  `pet_type` VARCHAR(50) DEFAULT NULL COMMENT 'Dog, Cat, Other',
  `breed` VARCHAR(100) DEFAULT NULL COMMENT 'Breed of the pet',
  `location` VARCHAR(255) DEFAULT NULL COMMENT 'Location where pet was last seen',
  `lost_date` DATE DEFAULT NULL COMMENT 'Date when pet was lost',
  `owner_name` VARCHAR(100) DEFAULT NULL COMMENT 'Name of the pet owner',
  `timestamp` DATETIME NOT NULL COMMENT 'When the event occurred (e.g., when pet was lost)',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When notification was created',
  `is_unread` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '0 = read, 1 = unread',
  PRIMARY KEY (`notification_id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_lost_pet_id` (`lost_pet_id`),
  INDEX `idx_type` (`type`),
  INDEX `idx_is_unread` (`is_unread`),
  INDEX `idx_created_at` (`created_at`),
  INDEX `idx_user_unread` (`user_id`, `is_unread`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Notifications for lost pet reports, matches, and sightings';

-- Optional: Add foreign key constraint if lost_pet_reports table exists
-- ALTER TABLE `notifications` 
-- ADD CONSTRAINT `fk_notifications_lost_pet` 
-- FOREIGN KEY (`lost_pet_id`) REFERENCES `lost_pet_reports` (`lost_id`) 
-- ON DELETE SET NULL ON UPDATE CASCADE;

-- Optional: Add foreign key constraint if users table exists
-- ALTER TABLE `notifications` 
-- ADD CONSTRAINT `fk_notifications_user` 
-- FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) 
-- ON DELETE CASCADE ON UPDATE CASCADE;

