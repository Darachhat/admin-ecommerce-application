# AdminApp Setup Guide

## Quick Start

1. **Copy Firebase Configuration**:

   ```bash
   # Copy google-services.json from EcommerceApp
   copy ..\EcommerceApp\app\google-services.json app\google-services.json
   ```

2. **Open in Android Studio**:

   - File → Open → Select `AdminApp` folder
   - Wait for Gradle sync to complete

3. **Update Cloudinary Settings** (if needed):

   - Open `app/src/main/java/com/ecommerce/adminapp/utils/Constants.kt`
   - Update `CLOUDINARY_CLOUD_NAME` and `CLOUDINARY_UPLOAD_PRESET`

4. **Run the App**:
   - Connect Android device or start emulator
   - Click Run ▶️ button

## Project Overview

**AdminApp** is a Kotlin Android application for managing the e-commerce platform. It connects to the same Firebase Realtime Database as the customer EcommerceApp.

### Key Features:

- Products management (view, add, edit, delete)
- Categories management
- Orders tracking
- Banners management
- Real-time data sync with Firebase
- Image uploads via Cloudinary

### Architecture:

- **MVVM Pattern**: Clear separation of concerns
- **Repository Pattern**: Abstracted data access
- **Kotlin Coroutines**: Asynchronous operations
- **Flow**: Reactive data streams
- **ViewBinding**: Type-safe view access

## Build Commands

### Using Android Studio

- **Debug**: Build → Build Bundle(s) / APK(s) → Build APK(s)
- **Release**: Build → Generate Signed Bundle / APK

### Using Gradle (Command Line)

```bash
# Debug build
gradlew assembleDebug

# Release build
gradlew assembleRelease

# Install on device
gradlew installDebug
```

## Firebase Setup

The app uses your existing Firebase project `ecommerce-app-ba8ed`.

### Database Paths:

- Products: `/Items`
- Categories: `/Category`
- Banners: `/Banner`

### Required Permissions:

Ensure Firebase Database Rules allow admin access:

```json
{
  "rules": {
    ".read": true,
    ".write": true
  }
}
```

## Development Status

### ✅ Implemented:

- Project structure and configuration
- Data models (Product, Category, Banner)
- Firebase integration
- Products listing
- Navigation structure
- Material Design 3 UI

### 🚧 TODO:

- Complete Add/Edit Product form with image upload
- Implement Categories CRUD
- Implement Orders management
- Implement Banners CRUD
- Add Firebase Authentication
- Add form validation
- Improve error handling

## Troubleshooting

### Gradle Sync Failed

```bash
# Clean and rebuild
gradlew clean
gradlew build
```

### Firebase Connection Error

- Verify `google-services.json` is in `app/` directory
- Check Firebase console for database rules
- Ensure internet connection

### Image Upload Not Working

- Verify Cloudinary credentials in `Constants.kt`
- Check storage permissions in AndroidManifest
- Ensure device has internet access

## Next Steps

1. Copy `google-services.json` from EcommerceApp
2. Open project in Android Studio
3. Let Gradle sync complete
4. Run the app on emulator or device
5. Navigate to Products tab to see existing products

The app will display products from Firebase Realtime Database that were created via the import script or the customer app.
