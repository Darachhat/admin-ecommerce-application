# Quick Fix Guide - Cannot Login

## The Problem

You can't login because you need to:

1. Create an admin user in Firebase Authentication
2. Add that user's UID to Firebase Database under "Admins" node

## Solution - Follow These Steps

### Step 1: Create Admin User in Firebase Authentication

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Select your project: **ecommerce-app-ba8ed** (or your project name)
3. Click **Authentication** → **Users** tab
4. Click **Add User**
5. Enter:
   - Email: `admin@ecommerce.com`
   - Password: `admin123` (or your choice)
6. Click **Add User**
7. **COPY THE USER UID** - you'll need it in the next step!

### Step 2: Add Admin Entry to Database

1. Click **Realtime Database** in left menu
2. Click on the **Data** tab
3. Click the **+** button next to your database URL
4. Add these fields:
   - **Name**: `Admins`
   - **Value**: (leave empty for now)
5. Click **Add**

6. Click **+** next to "Admins"
7. Add:
   - **Name**: Paste the **USER UID** from Step 1
   - **Value**: (leave empty)
8. Click **Add**

9. Click **+** next to your UID
10. Add these three entries:
    - **Name**: `email` | **Value**: `admin@ecommerce.com`
    - **Name**: `isAdmin` | **Value**: `true` (toggle to boolean)
    - **Name**: `displayName` | **Value**: `Admin User`

Your structure should look like:

```
Admins/
  └─ HIBqqlGl1ieUrWScq2aQALpTZ3K3/  (your actual UID)
      ├─ email: "admin@ecommerce.com"
      ├─ isAdmin: true
      └─ displayName: "Admin User"
```

### Step 3: Test Login

1. Open your AdminApp
2. Login with:
   - Email: `admin@ecommerce.com`
   - Password: `admin123` (or what you set)

## If You Still See Errors

The new version will show you the **exact UID** that needs to be added to the database.

Look for the error message that says:

> "Your UID: XXXXX"

Copy that UID and use it in Step 2 above!

## Common Issues

### "No account found with this email"

→ You didn't create the user in Firebase Authentication (Step 1)

### "Admin account not configured. Your UID: XXX"

→ You didn't add the admin entry to the database (Step 2)
→ The UID in the database doesn't match your user UID

### "Connection timeout"

→ Check your internet connection
→ Check if Firebase Database is enabled

## Database Rules Check

Make sure your Firebase Database Rules allow reading admin data:

```json
{
  "rules": {
    "Admins": {
      "$uid": {
        ".read": "auth != null && auth.uid == $uid",
        ".write": false
      }
    }
  }
}
```

This is already set up in `database.rules.json` - just deploy it:

```bash
firebase deploy --only database
```
