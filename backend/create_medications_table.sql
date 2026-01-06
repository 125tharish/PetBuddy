-- Create medications table for PetBuddy application
-- Run this SQL in phpMyAdmin under petbuddy_db database

CREATE TABLE IF NOT EXISTS `medications` (
  `medication_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `pet_id` int(11) NOT NULL,
  `pet_name` varchar(255) NOT NULL,
  `medication_name` varchar(255) NOT NULL,
  `dosage_time` varchar(255) NOT NULL,
  `frequency` varchar(50) NOT NULL,
  `reminder_enabled` tinyint(1) NOT NULL DEFAULT 1,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`medication_id`),
  KEY `user_id` (`user_id`),
  KEY `pet_id` (`pet_id`),
  CONSTRAINT `medications_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `medications_ibfk_2` FOREIGN KEY (`pet_id`) REFERENCES `pets` (`pet_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

