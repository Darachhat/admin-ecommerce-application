# Firebase Login Quick Reference

## Error Messages & Solutions

### 🔴 "Admin account not configured. Your UID: XXX"

**What it means**: You're authenticated, but not registered as admin in the database.

**Quick Fix**:
```
1. Copy the UID from the error message
2. Go to Firebase Console → Realtime Database
3. Navigate to: Admins/
4. Click + to add child
5. Paste UID as the key
6. Add these fields:
   - isAdmin: true (toggle to boolean)
   - email: "your-email@example.com"
   - displayName: "Your Name"
```

---

### 🔴 "No account found with this email"

**What it means**: User doesn't exist in Firebase Authentication.

**Quick Fix**:
```
1. Go to Firebase Console → Authentication → Users
2. Click "Add User"
3. Enter email and password (min 6 chars)
4. Click "Add User"
5. Copy the UID
6. Add to database (see above)
```

---

### 🔴 "Database permission denied"

**What it means**: Database rules don't allow reading your admin entry.

**Quick Fix**:
```
1. Check database.rules.json has:
   "Admins": {
     "$uid": {
       ".read": "auth != null && auth.uid == $uid"
     }
   }

2. Deploy rules:
   firebase deploy --only database

OR temporarily set test rules:
   ".read": true  (DON'T USE IN PRODUCTION)
```

---

### 🔴 "Database timeout" or "Database unavailable"

**What it means**: Can't connect to Firebase Realtime Database.

**Quick Fix**:
```
1. Check internet connection
2. Go to Firebase Console → Realtime Database
3. If "Create Database" button shows, click it
4. Choose location (us-central1)
5. Start in test mode
6. Try login again
```

---

### 🔴 "Incorrect password"

**What it means**: Password is wrong.

**Quick Fix**:
```
Option 1: Use Forgot Password
Option 2: Reset in Firebase Console:
   Authentication → Users → Find user → 
   Click ⋮ → Reset Password
```

---

### 🔴 "Network error"

**What it means**: Can't reach Firebase servers.

**Quick Fix**:
```
1. Check internet connection
2. Try turning WiFi off/on
3. Check Firebase status: status.firebase.google.com
4. Verify google-services.json is correct
```

---

## Database Structure Quick Setup

Copy this to Firebase Console → Realtime Database:

```json
{
  "Admins": {
    "PASTE_YOUR_UID_HERE": {
      "email": "your-email@example.com",
      "displayName": "Admin User",
      "isAdmin": true,
      "createdAt": 1734134400000
    }
  }
}
```

**Important**: 
- Replace `PASTE_YOUR_UID_HERE` with actual UID from Authentication
- `isAdmin` must be **boolean** `true`, not string `"true"`
- Get UID from: Authentication → Users → Copy from list

---

## Get Your UID

**Method 1**: From error message
- Try to login
- Error will show: "Your UID: XXX"

**Method 2**: From Firebase Console
- Go to Authentication → Users
- Find your email
- Copy the UID column

**Method 3**: From logs
- Login attempt → Check Android Studio Logcat
- Search for "User UID:"

---

## Verify Setup

Run this checklist:

### Authentication ✓
- [ ] Go to Firebase Console → Authentication
- [ ] See your email in Users list
- [ ] Note the UID

### Database ✓
- [ ] Go to Firebase Console → Realtime Database
- [ ] See `Admins` node
- [ ] See your UID under `Admins`
- [ ] `isAdmin` shows `true` (blue, not "true" in quotes)
- [ ] `email` matches authentication email

### App ✓
- [ ] `google-services.json` exists in `app/` folder
- [ ] Build succeeds
- [ ] Internet is connected

---

## Import Script

To use `import-admin.bat`:

1. Edit `admin-data.json`:
```json
{
  "Admins": {
    "YOUR_UID_FROM_FIREBASE": {
      "email": "your-email@example.com",
      "displayName": "Your Name",
      "isAdmin": true,
      "createdAt": 1734134400000
    }
  }
}
```

2. Run: `import-admin.bat`

**Requires**:
- Firebase CLI installed: `npm install -g firebase-tools`
- Logged in: `firebase login`
- Project set: `firebase use ecommerce-app-ba8ed`

---

## Test Credentials (Example)

**For Development/Testing Only**:

Create in Firebase Console:
- Email: `admin@test.com`
- Password: `test123` (or stronger)

Add to database:
```
Admins/{UID_FROM_FIREBASE}/
  ├─ email: "admin@test.com"
  ├─ isAdmin: true
  └─ displayName: "Test Admin"
```

**⚠️ Delete test accounts before production!**

---

## Still Having Issues?

1. **Check Logcat** (Android Studio):
   - Filter: `LoginActivity`
   - Look for error details

2. **Verify Firebase Project**:
   - Project ID: `ecommerce-app-ba8ed`
   - Database URL: `https://ecommerce-app-ba8ed-default-rtdb.firebaseio.com/`

3. **Clear App Data**:
   - Settings → Apps → AdminApp → Storage → Clear Data
   - Try login again

4. **Rebuild App**:
   ```bash
   ./gradlew clean
   ./gradlew assembleDebug
   ```

---

## Emergency Access

If completely locked out:

1. **Firebase Console → Authentication**:
   - Delete old users
   - Create new admin user
   - Note the UID

2. **Firebase Console → Realtime Database**:
   - Delete old Admins entries
   - Add new admin with new UID
   - Set isAdmin: true

3. **Rebuild and reinstall app**

---

## Contact Info

For help, check:
- `LOGIN_FIXED.md` - Full documentation
- `QUICK_FIX_GUIDE.md` - Step-by-step guide
- Logcat output - Detailed error logs
