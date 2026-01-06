-- Add phone column to users table if it doesn't exist
-- Run this SQL in phpMyAdmin under petbuddy_db database

ALTER TABLE `users` 
ADD COLUMN IF NOT EXISTS `phone` VARCHAR(20) NULL AFTER `email`;

