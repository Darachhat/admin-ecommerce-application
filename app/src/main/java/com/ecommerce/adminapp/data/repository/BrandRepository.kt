package com.ecommerce.adminapp.data.repository

import android.util.Log
import com.ecommerce.adminapp.data.model.Brand
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class BrandRepository {

    private val database = FirebaseDatabase.getInstance()
    private val brandsRef = database.getReference("brands")

    /**
     * Get all brands as a Flow
     */
    fun getAllBrands(): Flow<List<Brand>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("BrandRepository", "Firebase data received. Children count: ${snapshot.childrenCount}")
                val brands = snapshot.children.mapNotNull {
                    val brand = it.getValue(Brand::class.java)?.copy(id = it.key ?: "")
                    Log.d("BrandRepository", "Brand: ${brand?.name}, picUrl: ${brand?.picUrl}")
                    brand
                }
                Log.d("BrandRepository", "Total brands loaded: ${brands.size}")
                trySend(brands)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("BrandRepository", "Firebase error: ${error.message}")
                close(error.toException())
            }
        }

        brandsRef.addValueEventListener(listener)
        awaitClose { brandsRef.removeEventListener(listener) }
    }

    /**
     * Get a single brand by ID
     */
    suspend fun getBrand(brandId: String): Brand? {
        return try {
            val snapshot = brandsRef.child(brandId).get().await()
            snapshot.getValue(Brand::class.java)?.copy(id = brandId)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Add a new brand
     */
    suspend fun addBrand(brand: Brand): Result<String> {
        return try {
            val brandId = if (brand.id.isEmpty()) {
                brandsRef.push().key ?: throw Exception("Failed to generate brand ID")
            } else {
                brand.id
            }
            
            brandsRef.child(brandId).setValue(brand.toMap()).await()
            Result.success(brandId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Update an existing brand
     */
    suspend fun updateBrand(brand: Brand): Result<Unit> {
        return try {
            brandsRef.child(brand.id).setValue(brand.toMap()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Delete a brand
     */
    suspend fun deleteBrand(brandId: String): Result<Unit> {
        return try {
            brandsRef.child(brandId).removeValue().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
