# Firebase Configuration Checklist

## Your Firebase Project Details (from google-services.json):

- **Project ID**: `ecommerce-app-ba8ed`
- **Project Number**: `1037450078096`
- **Package Name**: `com.ecommerce.adminapp`

---

## CRITICAL STEPS - CHECK EACH ONE:

### ✅ Step 1: Verify You're in the CORRECT Project

1. Go to: https://console.firebase.google.com/
2. **MAKE SURE** you see: **"ecommerce-app-ba8ed"** in the project selector
3. If you see a different project name, **SWITCH TO ecommerce-app-ba8ed**

### ✅ Step 2: Enable Email/Password Authentication

1. In project **ecommerce-app-ba8ed**
2. Click **"Build"** → **"Authentication"** in left sidebar
3. Click **"Sign-in method"** tab (TOP of the page)
4. Look for **"Email/Password"** in the list
5. **IT MUST SAY "Enabled"** - if it says "Disabled", click it and enable it!
6. Screenshot this page and verify "Email/Password" shows **Enabled**

### ✅ Step 3: Create User Account

1. Still in **Authentication**
2. Click **"Users"** tab
3. Click **"Add user"**
4. Email: `darachhat012@gmail.com`
5. Password: `darachhat012` (min 6 characters)
6. Click **"Add user"**
7. **VERIFY** the user appears in the list
8. **COPY THE UID** shown next to the user (it should be: `NjolISO9DTOw1zZOBgItlEdnB1A3`)

### ✅ Step 4: Create/Import Database

1. Click **"Build"** → **"Realtime Database"**
2. If no database exists:
   - Click **"Create Database"**
   - Choose location (e.g., us-central1)
   - Start in **"Test mode"**
   - Click **"Enable"**
3. Once database is created, import admin data:
   - Click **⋮** (three dots at top right)
   - Click **"Import JSON"**
   - Browse to: `C:\Users\Admin\Desktop\FULL-ECOMMERCE-APp\AdminApp\admin-data.json`
   - Click **"Import"**

### ✅ Step 5: Verify Database Structure

After import, your database should show:

```
root/
  └─ Admins/
      └─ NjolISO9DTOw1zZOBgItlEdnB1A3/
          ├─ email: "darachhat012@gmail.com"
          ├─ displayName: "Admin User"
          ├─ isAdmin: true
          └─ createdAt: 1702425600000
```

---

## If Still Timing Out:

### Check Emulator Internet Connection:

The emulator might not have internet access. Verify by:

1. Open Chrome browser in the emulator
2. Try visiting google.com
3. If it doesn't load, the emulator has no internet

### Check Firebase Console Project Match:

In Firebase Console, go to:

- **Project Settings** (gear icon)
- Scroll to **"Your apps"**
- Verify you see an Android app with package: `com.ecommerce.adminapp`
- If not, the google-services.json is from a different project!

---

## Quick Test:

After completing ALL steps above, login with:

- Email: `darachhat012@gmail.com`
- Password: `darachhat012`

If it STILL times out, the issue is either:

1. Email/Password is NOT actually enabled (go back and double-check)
2. You're looking at the wrong Firebase project
3. Emulator has no internet connection
