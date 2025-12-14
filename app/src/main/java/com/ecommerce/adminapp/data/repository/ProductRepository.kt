package com.ecommerce.adminapp.data.repository

import com.ecommerce.adminapp.data.model.Product
import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ProductRepository {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val productsRef = database.child("products")

    fun getAllProducts(): Flow<List<Product>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val products = mutableListOf<Product>()
                snapshot.children.forEach { child ->
                    child.getValue(Product::class.java)?.let { product ->
                        product.id = child.key ?: ""
                        products.add(product)
                    }
                }
                trySend(products)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        productsRef.addValueEventListener(listener)
        awaitClose { productsRef.removeEventListener(listener) }
    }

    suspend fun getProductById(id: String): Product? {
        return try {
            val snapshot = productsRef.child(id).get().await()
            snapshot.getValue(Product::class.java)?.apply {
                this.id = id
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun addProduct(product: Product): Result<String> {
        return try {
            val newRef = productsRef.push()
            newRef.setValue(product.toMap()).await()
            Result.success(newRef.key ?: "")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProduct(id: String, product: Product): Result<Unit> {
        return try {
            productsRef.child(id).setValue(product.toMap()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteProduct(id: String): Result<Unit> {
        return try {
            productsRef.child(id).removeValue().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
