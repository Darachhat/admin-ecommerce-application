package com.ecommerce.adminapp.data.repository

import android.util.Log
import com.ecommerce.adminapp.data.model.Category
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class CategoryRepository {

    private val database = FirebaseDatabase.getInstance()
    private val categoriesRef = database.getReference("categories")

    /**
     * Get all categories as a Flow
     */
    fun getAllCategories(): Flow<List<Category>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("CategoryRepository", "Firebase data received. Children count: ${snapshot.childrenCount}")
                val categories = snapshot.children.mapNotNull {
                    val category = it.getValue(Category::class.java)?.copy(id = it.key ?: "")
                    Log.d("CategoryRepository", "Category: ${category?.name}, picUrl: ${category?.picUrl}")
                    category
                }
                Log.d("CategoryRepository", "Total categories loaded: ${categories.size}")
                trySend(categories)
            }

            override fun onCancelled(error:  DatabaseError) {
                Log.e("CategoryRepository", "Firebase error: ${error.message}")
                close(error.toException())
            }
        }

        categoriesRef.addValueEventListener(listener)
        awaitClose { categoriesRef.removeEventListener(listener) }
    }

    /**
     * Get a single category by ID
     */
    suspend fun getCategory(categoryId: String): Category? {
        return try {
            val snapshot = categoriesRef.child(categoryId).get().await()
            snapshot.getValue(Category::class.java)?.copy(id = categoryId)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Add a new category
     */
    suspend fun addCategory(category: Category): Result<String> {
        return try {
            val categoryId = if (category.id.isEmpty()) {
                categoriesRef.push().key ?: throw Exception("Failed to generate category ID")
            } else {
                category.id
            }
            val categoryData = category.copy(id = categoryId)

            categoriesRef.child(categoryId).setValue(categoryData.toMap()).await()

            Result.success(categoryId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Update an existing category
     */
    suspend fun updateCategory(category: Category): Result<Unit> {
        return try {
            if (category.id.isEmpty()) {
                return Result.failure(Exception("Category ID is required"))
            }

            categoriesRef.child(category.id).setValue(category.toMap()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Delete a category
     */
    suspend fun deleteCategory(categoryId: String): Result<Unit> {
        return try {
            categoriesRef.child(categoryId).removeValue().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}