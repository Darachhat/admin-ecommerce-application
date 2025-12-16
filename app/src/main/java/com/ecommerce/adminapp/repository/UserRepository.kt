package com.ecommerce.adminapp.repository

import com.ecommerce.adminapp.models.User
import com.ecommerce.adminapp.utils.DatabasePaths
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class UserRepository {
    
    private val database = FirebaseDatabase.getInstance()
    private val usersRef = database.getReference(DatabasePaths.USERS)
    
    /**
     * Get all users as a Flow
     */
    fun getAllUsers(): Flow<List<User>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val users = snapshot.children.mapNotNull { 
                    it.getValue(User::class.java)?.copy(id = it.key ?: "")
                }
                trySend(users)
            }
            
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        
        usersRef.addValueEventListener(listener)
        awaitClose { usersRef.removeEventListener(listener) }
    }
    
    /**
     * Get a single user by ID
     */
    suspend fun getUser(userId: String): User? {
        return try {
            val snapshot = usersRef.child(userId).get().await()
            snapshot.getValue(User::class.java)?.copy(id = userId)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Update user information
     */
    suspend fun updateUser(userId: String, updates: Map<String, Any>): Boolean {
        return try {
            usersRef.child(userId).updateChildren(updates).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    /**
     * Delete a user
     */
    suspend fun deleteUser(userId: String): Boolean {
        return try {
            usersRef.child(userId).removeValue().await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    /**
     * Update user role (admin/user)
     */
    suspend fun updateUserRole(userId: String, role: String): Boolean {
        return try {
            usersRef.child(userId).child("role").setValue(role).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    /**
     * Search users by email
     */
    suspend fun searchUsersByEmail(email: String): List<User> {
        return try {
            val snapshot = usersRef
                .orderByChild("email")
                .startAt(email)
                .endAt(email + "\uf8ff")
                .get()
                .await()
            
            snapshot.children.mapNotNull { 
                it.getValue(User::class.java)?.copy(id = it.key ?: "")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
    
    /**
     * Get users by role
     */
    suspend fun getUsersByRole(role: String): List<User> {
        return try {
            val snapshot = usersRef
                .orderByChild("role")
                .equalTo(role)
                .get()
                .await()
            
            snapshot.children.mapNotNull { 
                it.getValue(User::class.java)?.copy(id = it.key ?: "")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
