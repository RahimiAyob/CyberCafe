# Admin Dashboard Setup Guide

## Overview
This guide explains how to set up admins so they can automatically access the admin dashboard after login without manually typing the URL.

## Changes Made

### 1. **Admin Role Support**
The system now supports an `IS_ADMIN` flag on user accounts. When an admin logs in, they are automatically redirected to the admin dashboard instead of the member portal.

### 2. **Admin Login Page**
- New page: `admin_login.jsp` - A dedicated login page for admins that bypasses QR code scanning
- Added "Admin Access" links to:
  - `portal_selection.jsp` (after QR scan)
  - `scan_qr.jsp` (before QR scan)

### 3. **Automatic Redirect**
When an admin user logs in:
1. `LoginServlet` checks if the user has `IS_ADMIN = TRUE`
2. If true, automatically redirects to the Admin Dashboard (`/AdminDashboard`)
3. Otherwise, proceeds with normal member/guest flow

## Database Setup

### Add IS_ADMIN Column to USERS Table

Run this SQL command to add admin support to your existing database:

```sql
ALTER TABLE USERS ADD COLUMN IS_ADMIN BOOLEAN DEFAULT FALSE;
```

### Make a User an Admin

To promote a user to admin status:

```sql
UPDATE USERS SET IS_ADMIN = TRUE WHERE USERNAME = 'your_admin_username';
```

### Create a New Admin User

To create a completely new admin account directly:

```sql
INSERT INTO USERS (USERNAME, PASSWORD, BANKED_MINUTES, IS_ADMIN) 
VALUES ('admin_username', 'admin_password', 0, TRUE);
```

## How Admins Access the Dashboard

### Option 1: From QR Code Scanner (Original Flow)
1. Start at the main page (scan QR code as usual)
2. View "Admin Access" link at the bottom
3. Click "Admin Access"
4. Enter login credentials
5. Automatically redirected to admin dashboard

### Option 2: Direct Admin Login
1. Navigate directly to: `http://localhost:8080/CyberCafe/admin_login.jsp`
2. Enter admin credentials
3. Automatically redirected to admin dashboard

### Option 3: After QR Scan (Portal Selection)
1. Scan QR code at desk
2. View portal selection screen
3. Click "Admin Access" at the bottom
4. Enter admin credentials
5. Automatically redirected to admin dashboard

## Backwards Compatibility

The system gracefully handles the case where the `IS_ADMIN` column doesn't exist yet:
- **LoginServlet**: Catches the exception and defaults all users to non-admin
- **RegisterServlet**: Attempts to insert with IS_ADMIN, falls back to inserting without the column if needed

## File Changes Summary

### Modified Files:
- `src/main/java/com/project/cybercafe/LoginServlet.java`
  - Added IS_ADMIN check
  - Redirects admins directly to AdminDashboard

- `src/main/java/com/project/cybercafe/RegisterServlet.java`  
  - Updated to include IS_ADMIN field in new registrations
  - Added fallback for systems without IS_ADMIN column yet

- `src/main/webapp/portal_selection.jsp`
  - Added "Admin Access" link

- `src/main/webapp/scan_qr.jsp`
  - Added "Admin Access" link

### New Files:
- `src/main/webapp/admin_login.jsp`
  - New dedicated admin login page

## Testing

1. Run the application
2. Create/promote an admin user in the database
3. Navigate to `admin_login.jsp` 
4. Enter admin credentials
5. Should be automatically redirected to the admin dashboard at `/AdminDashboard`

## Troubleshooting

**Issue**: Admin login redirects to member page instead of admin dashboard
- **Solution**: Verify the `IS_ADMIN` column exists in the USERS table and is set to TRUE for the admin user

**Issue**: Getting "column not found" error
- **Solution**: Run the ALTER TABLE command above to add the IS_ADMIN column

**Issue**: New user registration fails
- **Solution**: The fallback mechanism should handle this, but verify the USERS table structure

