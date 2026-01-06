-- Create vaccinations table for PetBuddy application
-- Run this SQL in phpMyAdmin under petbuddy_db database

CREATE TABLE IF NOT EXISTS `vaccinations` (
  `vaccination_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `pet_id` int(11) NOT NULL,
  `pet_name` varchar(255) NOT NULL,
  `vaccine_name` varchar(255) NOT NULL,
  `last_date` date NOT NULL,
  `next_date` date NOT NULL,
  `status` varchar(50) NOT NULL DEFAULT 'CURRENT',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`vaccination_id`),
  KEY `user_id` (`user_id`),
  KEY `pet_id` (`pet_id`),
  CONSTRAINT `vaccinations_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `vaccinations_ibfk_2` FOREIGN KEY (`pet_id`) REFERENCES `pets` (`pet_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

