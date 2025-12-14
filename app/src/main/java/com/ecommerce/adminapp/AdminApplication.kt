package com.ecommerce.adminapp

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase

class AdminApplication : Application() {

    companion object {
        private const val TAG = "AdminApplication"
        // Firebase Realtime Database URL for the project
        private const val DATABASE_URL = "https://ecommerce-app-ba8ed-default-rtdb.firebaseio.com/"
    }

    override fun onCreate() {
        super.onCreate()
        
        Log.d(TAG, "Initializing AdminApplication")
        
        try {
            // Initialize Firebase
            FirebaseApp.initializeApp(this)
            Log.d(TAG, "Firebase initialized successfully")
            
            // Get Firebase Database instance with explicit URL
            val database = FirebaseDatabase.getInstance(DATABASE_URL)
            
            // Enable offline persistence for better performance
            database.setPersistenceEnabled(true)
            Log.d(TAG, "Database persistence enabled")
            
            // Set database URL as default
            FirebaseDatabase.getInstance().reference.database.setPersistenceEnabled(true)
            
            Log.d(TAG, "Firebase Database URL: $DATABASE_URL")
            Log.d(TAG, "Database Reference: ${database.reference}")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing Firebase", e)
            // Continue anyway - Firebase might already be initialized
        }
    }
}
