# PetBuddy API Setup Instructions

## Database Setup

1. **Open phpMyAdmin**: Navigate to `http://localhost/phpmyadmin`

2. **Select Database**: Click on `petbuddy_db` database (or create it if it doesn't exist)

3. **Create Users Table**: 
   - Go to the SQL tab
   - Copy and paste the SQL from `create_users_table.sql`
   - Click "Go" to execute

   OR manually create the table with these columns:
   - `user_id` (INT, AUTO_INCREMENT, PRIMARY KEY)
   - `name` (VARCHAR(255), NOT NULL)
   - `email` (VARCHAR(255), NOT NULL, UNIQUE)
   - `password` (VARCHAR(255), NOT NULL)
   - `role` (VARCHAR(50), DEFAULT 'owner')
   - `created_at` (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)

## XAMPP Configuration

1. **Start XAMPP Services**:
   - Open XAMPP Control Panel
   - Start **Apache** (required for PHP files)
   - Start **MySQL** (required for database)

2. **Verify API Files Location**:
   - All PHP files should be in: `C:\xampp\htdocs\api\`
   - Files include: `login.php`, `signup.php`, `db.php`

3. **Test API Endpoints**:
   - Login: `http://localhost/api/login.php`
   - Signup: `http://localhost/api/signup.php`

## Android App Configuration

1. **IP Address Configuration**:
   - The IP address is set in: `RetrofitClient.kt`
   - Current IP: `10.44.110.54` (update if your computer's IP changes)
   - For emulator: Use `10.0.2.2`
   - For physical device: Use your computer's local IP address

2. **Network Security**:
   - The IP address `10.44.110.54` is already added to `network_security_config.xml`
   - This allows HTTP (cleartext) traffic to your XAMPP server

## Testing

1. **Test Signup**:
   - Open the app
   - Go to Sign Up
   - Enter: Name, Email, Password, Confirm Password
   - Submit
   - Check phpMyAdmin to verify the user was created in the `users` table

2. **Test Login**:
   - Use the email and password from signup
   - Should successfully login and navigate to home screen

## Troubleshooting

- **Network Security Policy Error**: Make sure the IP address in `network_security_config.xml` matches your computer's IP
- **Connection Refused**: Ensure XAMPP Apache is running
- **404 Not Found**: Check that PHP files are in `C:\xampp\htdocs\api\`
- **Database Error**: Verify MySQL is running and `petbuddy_db` database exists
- **Table Not Found**: Run the SQL from `create_users_table.sql` in phpMyAdmin

