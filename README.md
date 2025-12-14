# Admin App - E-commerce Admin Panel (Android)

A Kotlin-based Android admin application for managing an e-commerce platform with Firebase integration.

## Features

- ✅ **Products Management**: Full CRUD operations for products
- ✅ **Categories Management**: Manage product categories
- ✅ **Orders Management**: View and manage customer orders
- ✅ **Banners Management**: Manage promotional banners
- ✅ **Firebase Realtime Database**: Real-time data synchronization
- ✅ **Image Upload**: Cloudinary integration for image storage
- ✅ **Material Design 3**: Modern UI with Material Design components
- ✅ **MVVM Architecture**: Clean architecture with ViewModels and Repositories

## Tech Stack

- **Language**: Kotlin
- **Architecture**: MVVM (Model-View-ViewModel)
- **Database**: Firebase Realtime Database
- **Authentication**: Firebase Authentication
- **Image Storage**: Cloudinary
- **UI Components**: Material Design 3, ViewBinding
- **Navigation**: Navigation Component
- **Async**: Kotlin Coroutines & Flow
- **Image Loading**: Glide

## Project Structure

```
AdminApp/
├── app/
│   ├── src/main/
│   │   ├── java/com/ecommerce/adminapp/
│   │   │   ├── data/
│   │   │   │   ├── model/           # Data models (Product, Category, Banner)
│   │   │   │   └── repository/      # Repository pattern for data access
│   │   │   ├── ui/
│   │   │   │   ├── products/        # Products UI and ViewModel
│   │   │   │   ├── categories/      # Categories UI
│   │   │   │   ├── orders/          # Orders UI
│   │   │   │   ├── banners/         # Banners UI
│   │   │   │   └── adapter/         # RecyclerView adapters
│   │   │   ├── utils/               # Utilities and constants
│   │   │   └── MainActivity.kt      # Main activity
│   │   ├── res/
│   │   │   ├── layout/              # XML layouts
│   │   │   ├── navigation/          # Navigation graph
│   │   │   ├── menu/                # Bottom navigation menu
│   │   │   └── values/              # Strings, colors, themes
│   │   └── AndroidManifest.xml
│   ├── build.gradle.kts
│   └── google-services.json
├── gradle/
└── build.gradle.kts
```

## Setup Instructions

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- JDK 17 or higher
- Android SDK (API 24+)
- Firebase account

### 1. Firebase Configuration

1. **Copy google-services.json**:

   - Copy the `google-services.json` from your EcommerceApp
   - Place it in `AdminApp/app/google-services.json`
   - Or download it from Firebase Console for the admin app

2. **Update package name** (if needed):
   - If using a separate Firebase project, ensure package name is `com.ecommerce.adminapp`

### 2. Cloudinary Configuration

Update `Constants.kt` with your Cloudinary credentials:

```kotlin
const val CLOUDINARY_CLOUD_NAME = "your_cloud_name"
const val CLOUDINARY_UPLOAD_PRESET = "your_upload_preset"
```

### 3. Build and Run

1. Open the project in Android Studio
2. Sync Gradle files
3. Connect an Android device or start an emulator
4. Click Run ▶️

## Firebase Database Structure

The app uses the same Firebase structure as the EcommerceApp:

```
{
  "Items": {
    "item1": {
      "title": "Product Name",
      "description": "Product description",
      "picUrl": ["url1", "url2"],
      "price": 99.99,
      "oldPrice": 149.99,
      "rating": 4.5,
      "size": ["S", "M", "L"],
      "color": ["#000000", "#FFFFFF"],
      "categoryId": 1,
      "showRecommended": true,
      "numberInCart": 50
    }
  },
  "Category": {
    "cat1": {
      "id": 1,
      "title": "Electronics",
      "picUrl": "category_image_url"
    }
  },
  "Banner": {
    "banner1": {
      "id": 1,
      "url": "banner_image_url"
    }
  }
}
```

## Dependencies

Key dependencies (defined in `app/build.gradle.kts`):

- **Firebase BOM**: 32.7.0
- **Material Design**: 1.11.0
- **Navigation Component**: 2.7.6
- **Lifecycle (ViewModel, LiveData)**: 2.7.0
- **Coroutines**: 1.7.3
- **Glide**: 4.16.0
- **OkHttp**: 4.12.0

## Current Implementation Status

### ✅ Completed

- Project structure
- Data models (Product, Category, Banner)
- Repository pattern for Products and Categories
- Products listing with RecyclerView
- Product ViewModel with Flow
- Firebase integration
- Cloudinary helper for image uploads
- Bottom navigation
- Material Design 3 theme

### 🔄 To Implement

- **Add/Edit Product Form**: Complete form with image upload
- **Categories CRUD**: Full category management
- **Orders Management**: View and update order status
- **Banners CRUD**: Manage promotional banners
- **Firebase Authentication**: Admin login
- **Image picker**: Multiple image selection
- **Form validation**: Input validation for all forms
- **Loading states**: Progress indicators
- **Error handling**: User-friendly error messages

## Building the App

### Debug Build

```bash
./gradlew assembleDebug
```

### Release Build

```bash
./gradlew assembleRelease
```

APK will be in: `app/build/outputs/apk/`

## Data Flow

1. **UI Layer** (Fragments) → User interactions
2. **ViewModel** → Business logic, state management
3. **Repository** → Data operations abstraction
4. **Firebase** → Cloud database operations

## Next Steps

1. **Implement Add/Edit Product Form**:

   - Create detailed form layout
   - Add image picker functionality
   - Implement save/update logic

2. **Complete Categories Management**:

   - Category list with RecyclerView
   - Add/Edit category dialog
   - Delete confirmation

3. **Orders Management**:

   - Fetch orders from Firebase
   - Display order details
   - Update order status

4. **Banners Management**:
   - Banner list and CRUD operations
   - Image upload for banners

## Notes

- The app uses the same Firebase database as EcommerceApp
- Products created here will appear in the customer app
- ViewBinding is enabled for type-safe view access
- Offline persistence is enabled for Firebase

## Troubleshooting

### Build Issues

- Clean and rebuild: `Build > Clean Project` then `Build > Rebuild Project`
- Invalidate caches: `File > Invalidate Caches / Restart`

### Firebase Connection Issues

- Verify google-services.json is in correct location
- Check Firebase database rules allow read/write
- Ensure internet permissions are granted

### Image Upload Issues

- Verify Cloudinary credentials
- Check internet connectivity
- Ensure READ_EXTERNAL_STORAGE permission is granted

## License

This project is part of the E-commerce application suite.
