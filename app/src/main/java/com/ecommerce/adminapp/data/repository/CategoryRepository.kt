package com.ecommerce.adminapp.data.repository

import com.ecommerce.adminapp.data.model.Category
import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class CategoryRepository {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val categoriesRef = database.child("Category")

    fun getAllCategories(): Flow<List<Category>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val categories = mutableListOf<Category>()
                snapshot.children.forEach { child ->
                    child.getValue(Category::class.java)?.let { category ->
                        categories.add(category)
                    }
                }
                trySend(categories)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        categoriesRef.addValueEventListener(listener)
        awaitClose { categoriesRef.removeEventListener(listener) }
    }

    suspend fun addCategory(key: String, category: Category): Result<Unit> {
        return try {
            categoriesRef.child(key).setValue(category.toMap()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateCategory(key: String, category: Category): Result<Unit> {
        return try {
            categoriesRef.child(key).setValue(category.toMap()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteCategory(key: String): Result<Unit> {
        return try {
            categoriesRef.child(key).removeValue().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
