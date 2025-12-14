package com.ecommerce.adminapp.utils

object Constants {
    const val FIREBASE_DATABASE_URL = "https://ecommerce-app-ba8ed-default-rtdb.firebaseio.com"
    
    // Cloudinary Configuration
    const val CLOUDINARY_CLOUD_NAME = "dkikc5ywq"
    const val CLOUDINARY_UPLOAD_PRESET = "ecommerce_preset"
    const val CLOUDINARY_UPLOAD_URL = "https://api.cloudinary.com/v1_1/$CLOUDINARY_CLOUD_NAME/image/upload"
    
    // Request Codes
    const val PICK_IMAGE_REQUEST = 1001
    const val PICK_MULTIPLE_IMAGES_REQUEST = 1002
    
    // Firebase Paths
    const val PATH_ITEMS = "Items"
    const val PATH_CATEGORY = "Category"
    const val PATH_BANNER = "Banner"
    
    // Shared Preferences
    const val PREFS_NAME = "AdminAppPrefs"
    const val KEY_ADMIN_EMAIL = "admin_email"
    const val KEY_IS_LOGGED_IN = "is_logged_in"
}
