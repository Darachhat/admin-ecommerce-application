# Admin User Setup Guide

Since the AdminApp connects directly to Firebase, you need to create admin users manually through Firebase Console.

## Option 1: Using Firebase Console (Recommended)

### Step 1: Create User in Firebase Authentication

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Select your project: **ecommerce-app-ba8ed**
3. Click **Authentication** in the left menu
4. Click **Users** tab
5. Click **Add User** button
6. Enter:
   - **Email**: `admin@ecommerce.com`
   - **Password**: `admin123456` (or any password)
7. Click **Add User**

### Step 2: Set Admin Privileges in Database

1. Click **Realtime Database** in the left menu
2. Navigate to the root of your database
3. Click the **+** button to add a new child
4. Create the following structure:

```json
{
  "Admins": {
    "USER_UID_HERE": {
      "email": "admin@ecommerce.com",
      "displayName": "Admin User",
      "isAdmin": true,
      "createdAt": 1234567890000
    }
  }
}
```

**Important**: Replace `USER_UID_HERE` with the actual UID of the user you created in Step 1.

To get the UID:

- Go back to **Authentication** → **Users**
- Copy the **User UID** of the user you just created
- Use that UID as the key under `Admins` node

### Step 3: Test Login

1. Open AdminApp on your emulator/device
2. Enter the email and password you created
3. Click **Sign In**

---

## Option 2: Using Firebase CLI (Advanced)

If you have Firebase CLI installed and configured:

### 1. Create Authentication User

```bash
firebase auth:import users.json --project ecommerce-app-ba8ed
```

Where `users.json` contains:

```json
{
  "users": [
    {
      "localId": "admin-user-1",
      "email": "admin@ecommerce.com",
      "passwordHash": "...",
      "emailVerified": true
    }
  ]
}
```

### 2. Set Database Admin Entry

```bash
firebase database:set /Admins/admin-user-1 '{
  "email": "admin@ecommerce.com",
  "displayName": "Admin User",
  "isAdmin": true
}' --project ecommerce-app-ba8ed
```

---

## Option 3: Quick Test Setup

For testing purposes, you can use Firebase Console with these test credentials:

### Test Admin 1

- **Email**: `admin@ecommerce.com`
- **Password**: `admin123456`
- **Display Name**: Admin User

### Test Admin 2

- **Email**: `test@admin.com`
- **Password**: `test123456`
- **Display Name**: Test Admin

---

## Database Structure for Admins

```
ecommerce-app-ba8ed
└── Admins/
    ├── [user-uid-1]/
    │   ├── email: "admin@ecommerce.com"
    │   ├── displayName: "Admin User"
    │   ├── isAdmin: true
    │   └── createdAt: 1234567890000
    └── [user-uid-2]/
        ├── email: "test@admin.com"
        ├── displayName: "Test Admin"
        ├── isAdmin: true
        └── createdAt: 1234567890000
```

---

## Security Rules (Important!)

Make sure your Firebase Realtime Database rules allow authenticated users to read their admin status:

```json
{
  "rules": {
    "Admins": {
      "$uid": {
        ".read": "auth != null && auth.uid == $uid",
        ".write": false
      }
    },
    "Items": {
      ".read": true,
      ".write": "auth != null && root.child('Admins').child(auth.uid).child('isAdmin').val() == true"
    },
    "Category": {
      ".read": true,
      ".write": "auth != null && root.child('Admins').child(auth.uid).child('isAdmin').val() == true"
    },
    "Banner": {
      ".read": true,
      ".write": "auth != null && root.child('Admins').child(auth.uid).child('isAdmin').val() == true"
    }
  }
}
```

---

## Troubleshooting

### "You don't have admin privileges" error

1. **Check Authentication**: Verify the user exists in Firebase Authentication
2. **Check Database Entry**: Verify the `Admins/[uid]/isAdmin` is set to `true`
3. **Check UID Match**: Make sure the UID in Authentication matches the key in Admins node
4. **Re-login**: After setting admin privileges, user must logout and login again

### "Authentication failed" error

1. **Check Email Format**: Ensure email is valid
2. **Check Password**: Password must be at least 6 characters
3. **Check Firebase Connection**: Verify internet connection
4. **Check google-services.json**: Ensure it's properly configured

### Can't create user in Firebase Console

1. **Check Firebase Plan**: Free Spark plan has limitations
2. **Check Email Provider**: Ensure Email/Password provider is enabled
3. Go to Authentication → Sign-in method → Enable Email/Password

---

## Next Steps

After successfully logging in:

1. **Products Management**: ✅ Working - View, Edit, Delete products
2. **Add/Edit Products**: 🚧 Form needs implementation
3. **Categories**: 🚧 Needs implementation
4. **Orders**: 🚧 Needs implementation
5. **Banners**: 🚧 Needs implementation

---

**Last Updated**: December 13, 2024  
**Firebase Project**: ecommerce-app-ba8ed
