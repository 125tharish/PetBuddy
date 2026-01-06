-- Create pets table for PetBuddy application
-- Run this SQL in phpMyAdmin under petbuddy_db database

CREATE TABLE IF NOT EXISTS `pets` (
  `pet_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `pet_name` varchar(255) NOT NULL,
  `pet_type` varchar(50) NOT NULL,
  `breed` varchar(255) DEFAULT NULL,
  `color` varchar(100) DEFAULT NULL,
  `age` varchar(50) DEFAULT NULL,
  `microchip_id` varchar(100) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`pet_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `pets_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

