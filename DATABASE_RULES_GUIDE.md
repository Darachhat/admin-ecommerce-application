# Firebase Database Security Rules Setup

## 📋 Overview

This guide explains how to set up secure Firebase Realtime Database rules for the AdminApp.

---

## 🔐 Security Rules Explained

### **users** Node

```json
"users": {
  "$uid": {
    ".read": "$uid === auth.uid",
    ".write": "$uid === auth.uid"
  }
}
```

- ✅ Users can only read/write their own data
- ✅ Prevents unauthorized access to other users' data

### **Admins** Node

```json
"Admins": {
  "$uid": {
    ".read": "auth != null && auth.uid == $uid",
    ".write": false
  }
}
```

- ✅ Admins can only read their own admin status
- ✅ Write access disabled (set via Firebase Console only)
- ✅ Prevents privilege escalation

### **Items** (Products) Node

```json
"Items": {
  ".read": true,
  "$itemId": {
    ".write": "auth != null && root.child('Admins').child(auth.uid).child('isAdmin').val() == true"
  }
}
```

- ✅ Anyone can read products (public access)
- ✅ Only authenticated admins can create/update/delete
- ✅ Validates admin status from Admins node

### **Category** Node

```json
"Category": {
  ".read": true,
  "$categoryId": {
    ".write": "auth != null && root.child('Admins').child(auth.uid).child('isAdmin').val() == true"
  }
}
```

- ✅ Anyone can read categories
- ✅ Only authenticated admins can modify

### **Banner** Node

```json
"Banner": {
  ".read": true,
  "$bannerId": {
    ".write": "auth != null && root.child('Admins').child(auth.uid).child('isAdmin').val() == true"
  }
}
```

- ✅ Anyone can read banners
- ✅ Only authenticated admins can modify

### **Orders** Node

```json
"Orders": {
  "$orderId": {
    ".read": "auth != null && (data.child('userId').val() == auth.uid || root.child('Admins').child(auth.uid).child('isAdmin').val() == true)",
    ".write": "auth != null"
  }
}
```

- ✅ Users can read only their own orders
- ✅ Admins can read all orders
- ✅ Authenticated users can create orders

---

## 🚀 How to Apply Rules

### Method 1: Firebase Console (Recommended)

1. **Go to Firebase Console**

   - Visit: https://console.firebase.google.com/
   - Select project: **ecommerce-app-ba8ed**

2. **Navigate to Database Rules**

   - Click **Realtime Database** in the left sidebar
   - Click **Rules** tab at the top

3. **Copy and Paste Rules**

   - Open `database.rules.json` file
   - Copy the entire content
   - Paste into the Firebase Console editor
   - Click **Publish**

4. **Verify**
   - Rules should show "Published" status
   - Check for any syntax errors

### Method 2: Firebase CLI

```bash
# Install Firebase CLI (if not installed)
npm install -g firebase-tools

# Login to Firebase
firebase login

# Initialize Firebase in your project
cd C:\Users\Admin\Desktop\FULL-ECOMMERCE-APp\AdminApp
firebase init database

# Select your project: ecommerce-app-ba8ed
# Use existing database.rules.json file

# Deploy rules
firebase deploy --only database
```

### Method 3: Using the JSON File Directly

1. Copy the content from `database.rules.json`
2. Go to Firebase Console → Realtime Database → Rules
3. Replace all content with the rules from the file
4. Click **Publish**

---

## ✅ Testing the Rules

### Test 1: Public Read Access

```javascript
// Products should be readable without authentication
firebase
  .database()
  .ref('Items')
  .once('value')
  .then((snapshot) => console.log('✅ Public read works'))
  .catch((error) => console.log('❌ Public read failed'));
```

### Test 2: Admin Write Access

```javascript
// Only admins should be able to write products
firebase
  .database()
  .ref('Items/test-product')
  .set({
    title: 'Test Product',
    price: 99.99,
  })
  .then(() => console.log('✅ Admin write works'))
  .catch((error) => console.log('❌ Admin write failed'));
```

### Test 3: User Read Own Data

```javascript
// Users should read only their own data
firebase
  .database()
  .ref(`users/${currentUser.uid}`)
  .once('value')
  .then((snapshot) => console.log('✅ User read own data works'))
  .catch((error) => console.log('❌ User read failed'));
```

### Test 4: Order Privacy

```javascript
// Users should NOT read other users' orders
firebase
  .database()
  .ref('Orders/other-user-order')
  .once('value')
  .then(() => console.log('❌ Security breach!'))
  .catch((error) => console.log('✅ Order privacy protected'));
```

---

## 🔧 Simulator Testing

Firebase Console provides a rules simulator:

1. Go to **Realtime Database** → **Rules**
2. Click **Simulator** tab
3. Test scenarios:

### Scenario 1: Anonymous User Reading Products

```
Location: /Items
Read: true
Auth: null (not authenticated)
Expected: ✅ Allowed
```

### Scenario 2: Non-Admin Writing Products

```
Location: /Items/product1
Write: true
Auth: { uid: "user123" }
Admins/user123/isAdmin: false
Expected: ❌ Denied
```

### Scenario 3: Admin Writing Products

```
Location: /Items/product1
Write: true
Auth: { uid: "admin123" }
Admins/admin123/isAdmin: true
Expected: ✅ Allowed
```

---

## 🚨 Common Issues

### Issue 1: "Permission Denied" for Admin

**Cause**: Admin entry missing in database  
**Solution**: Create admin entry under `Admins/{uid}` with `isAdmin: true`

### Issue 2: Rules Not Taking Effect

**Cause**: Rules not published  
**Solution**: Click **Publish** in Firebase Console

### Issue 3: Public Data Not Accessible

**Cause**: `.read: true` not set correctly  
**Solution**: Verify `Items`, `Category`, `Banner` have `.read: true` at root level

### Issue 4: Users Can Read Other Users' Data

**Cause**: Incorrect UID check  
**Solution**: Verify rule uses `$uid === auth.uid`

---

## 📊 Security Best Practices

### ✅ DO:

- Use `.read` and `.write` separately
- Validate admin status from database
- Use authentication for sensitive operations
- Test rules thoroughly before deploying
- Keep rules as restrictive as possible
- Document your rule changes

### ❌ DON'T:

- Use `.read: true` and `.write: true` for sensitive data
- Store admin flags in user-modifiable locations
- Allow unauthenticated writes
- Expose user personal data publicly
- Trust client-side validation only

---

## 🔄 Migration from Development Rules

If you currently have open rules (`.read: true`, `.write: true`):

### Step 1: Backup Current Data

```bash
firebase database:get / > backup.json
```

### Step 2: Apply New Rules Gradually

1. **Phase 1**: Apply read restrictions only
2. **Phase 2**: Test with admin users
3. **Phase 3**: Apply write restrictions
4. **Phase 4**: Test all operations
5. **Phase 5**: Monitor for errors

### Step 3: Update App Code

Ensure your app handles permission errors:

```kotlin
database.reference.child("Items").setValue(data)
    .addOnSuccessListener { /* Success */ }
    .addOnFailureListener { error ->
        if (error.message?.contains("permission") == true) {
            // Handle permission denied
            showError("You don't have permission to perform this action")
        }
    }
```

---

## 📝 Rule Validation Checklist

Before deploying rules, verify:

- [ ] Admins can read their own admin status
- [ ] Admins can write to Items, Category, Banner
- [ ] Non-admins cannot write to Items, Category, Banner
- [ ] Everyone can read Items, Category, Banner
- [ ] Users can only read their own user data
- [ ] Users can only read their own orders
- [ ] Admins can read all orders
- [ ] Unauthenticated users cannot write anywhere
- [ ] No syntax errors in rules JSON

---

## 🔗 Resources

- [Firebase Security Rules Documentation](https://firebase.google.com/docs/database/security)
- [Rules Simulator Guide](https://firebase.google.com/docs/database/security/rules-simulator)
- [Firebase CLI Reference](https://firebase.google.com/docs/cli)

---

## 📞 Support

If you encounter issues:

1. Check Firebase Console → Realtime Database → Rules
2. Use Rules Simulator to test specific scenarios
3. Review app logs for permission errors
4. Verify admin entries exist in database
5. Confirm authentication is working

---

**Last Updated**: December 13, 2024  
**Firebase Project**: ecommerce-app-ba8ed  
**Rules Version**: 1.0.0
