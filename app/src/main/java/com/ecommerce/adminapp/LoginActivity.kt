package com.ecommerce.adminapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ecommerce.adminapp.databinding.ActivityLoginBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var prefs: SharedPreferences

    companion object {
        private const val TAG = "LoginActivity"
        private const val PREFS_NAME = "AdminAppPrefs"
        private const val KEY_REMEMBER_ME = "remember_me"
        private const val KEY_EMAIL = "email"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        Log.d(TAG, "Firebase initialized. App: ${auth.app.name}")
        
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

        // Load saved email if remember me was checked
        if (prefs.getBoolean(KEY_REMEMBER_ME, false)) {
            binding.editTextEmail.setText(prefs.getString(KEY_EMAIL, ""))
            binding.checkBoxRememberMe.isChecked = true
        }

        setupListeners()
    }

    private fun setupListeners() {
        binding.buttonLogin.setOnClickListener {
            val email = binding.editTextEmail.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()

            if (validateInput(email, password)) {
                loginWithEmail(email, password)
            }
        }

        binding.buttonForgotPassword.setOnClickListener {
            val email = binding.editTextEmail.text.toString().trim()
            if (email.isEmpty()) {
                showError("Please enter your email address")
            } else {
                resetPassword(email)
            }
        }

        binding.buttonGoogleSignIn.setOnClickListener {
            showError("Google Sign-In will be implemented in the next phase")
            // TODO: Implement Google Sign-In
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            binding.textInputLayoutEmail.error = "Email is required"
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.textInputLayoutEmail.error = "Invalid email format"
            return false
        }
        binding.textInputLayoutEmail.error = null

        if (password.isEmpty()) {
            binding.textInputLayoutPassword.error = "Password is required"
            return false
        }
        if (password.length < 6) {
            binding.textInputLayoutPassword.error = "Password must be at least 6 characters"
            return false
        }
        binding.textInputLayoutPassword.error = null

        return true
    }

    private fun loginWithEmail(email: String, password: String) {
        showLoading(true)
        Log.d(TAG, "Attempting login with email: $email")
        Log.d(TAG, "Firebase Auth instance: ${auth.app.name}")
        Log.d(TAG, "Database URL: ${FirebaseDatabase.getInstance().reference}")
        
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                
                if (task.isSuccessful) {
                    // Sign in success
                    val user = auth.currentUser
                    Log.d(TAG, "Login successful. User UID: ${user?.uid}")
                    Log.d(TAG, "User Email: ${user?.email}")
                    Log.d(TAG, "User Email Verified: ${user?.isEmailVerified}")
                    Log.d(TAG, "User Metadata: Created=${user?.metadata?.creationTimestamp}, LastSignIn=${user?.metadata?.lastSignInTimestamp}")
                    if (user != null) {
                        // Save remember me preference
                        if (binding.checkBoxRememberMe.isChecked) {
                            prefs.edit().apply {
                                putBoolean(KEY_REMEMBER_ME, true)
                                putString(KEY_EMAIL, email)
                                apply()
                            }
                        } else {
                            prefs.edit().clear().apply()
                        }
                        
                        // Check if user has admin privileges
                        checkAdminPrivileges(user.uid)
                    }
                } else {
                    // Sign in failed
                    showLoading(false)
                    val errorMessage = task.exception?.message ?: "Authentication failed"
                    Log.e(TAG, "Login failed: $errorMessage", task.exception)
                    
                    // Provide more user-friendly error messages
                    val userMessage = when {
                        errorMessage.contains("no user record", ignoreCase = true) || 
                        errorMessage.contains("user not found", ignoreCase = true) -> 
                            "No account found with this email.\n\n✓ Create user in Firebase Console:\n   Authentication → Add User\n   Email: $email"
                        errorMessage.contains("password is invalid", ignoreCase = true) || 
                        errorMessage.contains("wrong password", ignoreCase = true) -> 
                            "Incorrect password. Please try again."
                        errorMessage.contains("network", ignoreCase = true) -> 
                            "Network error. Please check your internet connection."
                        errorMessage.contains("disabled", ignoreCase = true) -> 
                            "This account has been disabled."
                        else -> "Login failed: $errorMessage"
                    }
                    showError(userMessage)
                }
            }
    }

    private fun checkAdminPrivileges(userId: String) {
        Log.d(TAG, "=== CHECKING ADMIN PRIVILEGES ===")
        Log.d(TAG, "User ID: $userId")
        
        // Add timeout for database query (10 seconds)
        val timeoutHandler = android.os.Handler(android.os.Looper.getMainLooper())
        var queryCompleted = false
        val timeoutRunnable = Runnable {
            if (!queryCompleted) {
                Log.e(TAG, "Database query timeout - no response from Firebase")
                showLoading(false)
                auth.signOut()
                showError("Database timeout. Please check:\n1. Realtime Database is created\n2. Admin data is imported\n3. Database rules allow reading Admins node\n4. Internet connection is working")
            }
        }
        timeoutHandler.postDelayed(timeoutRunnable, 10000)
        
        // Check if user has admin role in Firebase Database
        val database = FirebaseDatabase.getInstance()
        Log.d(TAG, "Database instance: ${database.reference.database.app.name}")
        Log.d(TAG, "Database URL: ${database.reference.toString()}")
        
        val adminRef = database.getReference("Admins").child(userId)
        Log.d(TAG, "Admin ref path: Admins/$userId")
        
        adminRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                queryCompleted = true
                timeoutHandler.removeCallbacks(timeoutRunnable)
                showLoading(false)
                
                Log.d(TAG, "=== DATABASE QUERY RESULT ===")
                Log.d(TAG, "Snapshot exists: ${snapshot.exists()}")
                Log.d(TAG, "Snapshot key: ${snapshot.key}")
                Log.d(TAG, "Snapshot path: ${snapshot.ref}")
                if (snapshot.exists()) {
                    Log.d(TAG, "Snapshot value: ${snapshot.value}")
                    Log.d(TAG, "isAdmin field exists: ${snapshot.hasChild("isAdmin")}")
                    Log.d(TAG, "isAdmin value: ${snapshot.child("isAdmin").getValue(Boolean::class.java)}")
                    Log.d(TAG, "email value: ${snapshot.child("email").getValue(String::class.java)}")
                } else {
                    Log.e(TAG, "Snapshot does NOT exist for path: Admins/$userId")
                }
                
                val isAdmin = snapshot.child("isAdmin").getValue(Boolean::class.java)
                Log.d(TAG, "Final isAdmin check result: $isAdmin")
                
                if (snapshot.exists() && isAdmin == true) {
                    // User is admin, proceed to main activity
                    Log.d(TAG, "✓ User is verified admin, navigating to main")
                    navigateToMain()
                } else {
                    // User is not admin
                    auth.signOut()
                    if (!snapshot.exists()) {
                        Log.e(TAG, "✗ No admin entry found for user: $userId")
                        showError("Admin account not configured.\n\n" +
                            "Your UID: $userId\n\n" +
                            "SOLUTION:\n" +
                            "1. Go to Firebase Console → Realtime Database\n" +
                            "2. Add entry: Admins/$userId\n" +
                            "3. Set: isAdmin = true\n" +
                            "4. Set: email = your email\n\n" +
                            "OR use import-admin.bat script")
                    } else {
                        Log.e(TAG, "✗ User exists but isAdmin is false or null: $isAdmin")
                        showError("You don't have admin privileges. Please contact the administrator.")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                queryCompleted = true
                timeoutHandler.removeCallbacks(timeoutRunnable)
                showLoading(false)
                auth.signOut()
                Log.e(TAG, "=== DATABASE ERROR ===")
                Log.e(TAG, "Error code: ${error.code}")
                Log.e(TAG, "Error message: ${error.message}")
                Log.e(TAG, "Error details: ${error.details}")
                Log.e(TAG, "Exception: ", error.toException())
                
                val userMessage = when (error.code) {
                    DatabaseError.PERMISSION_DENIED -> 
                        "Database permission denied.\n\n" +
                        "Your UID: $userId\n\n" +
                        "Check Firebase Console:\n" +
                        "1. Database Rules allow reading Admins/$userId\n" +
                        "2. You are authenticated\n\n" +
                        "Rule should be:\n" +
                        "Admins/\$uid/.read = \"auth.uid == \$uid\""
                    DatabaseError.DISCONNECTED -> 
                        "Network disconnected.\n\n" +
                        "Please check:\n" +
                        "1. Internet connection\n" +
                        "2. Firebase project status"
                    DatabaseError.NETWORK_ERROR -> 
                        "Network error.\n\n" +
                        "Please verify:\n" +
                        "1. Internet connection\n" +
                        "2. Firebase configuration\n" +
                        "3. Database URL is correct"
                    DatabaseError.UNAVAILABLE ->
                        "Database unavailable.\n\n" +
                        "The Firebase Realtime Database\n" +
                        "might not be created yet.\n\n" +
                        "Go to Firebase Console and\n" +
                        "create a Realtime Database."
                    else -> 
                        "Database error: ${error.message}\n\n" +
                        "Code: ${error.code}\n" +
                        "Details: ${error.details}\n\n" +
                        "Your UID: $userId"
                }
                showError(userMessage)
            }
        })
    }

    private fun resetPassword(email: String) {
        showLoading(true)
        
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                showLoading(false)
                if (task.isSuccessful) {
                    Snackbar.make(
                        binding.root,
                        "Password reset email sent to $email",
                        Snackbar.LENGTH_LONG
                    ).show()
                } else {
                    showError(task.exception?.message ?: "Failed to send reset email")
                }
            }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.buttonLogin.isEnabled = !isLoading
        binding.buttonGoogleSignIn.isEnabled = !isLoading
        binding.buttonForgotPassword.isEnabled = !isLoading
        binding.editTextEmail.isEnabled = !isLoading
        binding.editTextPassword.isEnabled = !isLoading
    }

    private fun showError(message: String) {
        binding.textViewError.text = message
        binding.textViewError.visibility = View.VISIBLE
        
        // Auto-hide error after 5 seconds
        binding.textViewError.postDelayed({
            binding.textViewError.visibility = View.GONE
        }, 5000)
    }
}
