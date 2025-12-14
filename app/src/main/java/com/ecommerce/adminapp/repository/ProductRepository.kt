package com.ecommerce.adminapp.repository

import com.ecommerce.adminapp.models.Product
import com.ecommerce.adminapp.models.ProductVariant
import com.ecommerce.adminapp.utils.DatabasePaths
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ProductRepository {
    
    private val database = FirebaseDatabase.getInstance()
    private val productsRef = database.getReference(DatabasePaths.PRODUCTS)
    private val variantsRef = database.getReference(DatabasePaths.PRODUCT_VARIANTS)
    private val variantsByProductRef = database.getReference(DatabasePaths.PRODUCT_VARIANTS_BY_PRODUCT)
    private val categoryProductsRef = database.getReference(DatabasePaths.CATEGORY_PRODUCTS)
    
    /**
     * Get all products as a Flow
     */
    fun getAllProducts(): Flow<List<Product>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val products = snapshot.children.mapNotNull { 
                    it.getValue(Product::class.java)?.copy(id = it.key ?: "")
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
    
    /**
     * Get a single product by ID
     */
    suspend fun getProduct(productId: String): Product? {
        return try {
            val snapshot = productsRef.child(productId).get().await()
            snapshot.getValue(Product::class.java)?.copy(id = productId)
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Add a new product
     */
    suspend fun addProduct(product: Product): Result<String> {
        return try {
            val productId = product.id.ifEmpty { productsRef.push().key ?: return Result.failure(Exception("Failed to generate product ID")) }
            val productData = product.copy(id = productId)
            
            // Save product
            productsRef.child(productId).setValue(productData).await()
            
            // Add to category index if category is set
            if (product.categoryId.isNotEmpty()) {
                categoryProductsRef.child(product.categoryId).child(productId).setValue(true).await()
            }
            
            Result.success(productId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Update an existing product
     */
    suspend fun updateProduct(product: Product): Result<Unit> {
        return try {
            if (product.id.isEmpty()) {
                return Result.failure(Exception("Product ID is required"))
            }
            
            // Get old product to check if category changed
            val oldProduct = getProduct(product.id)
            
            // Update product
            productsRef.child(product.id).setValue(product).await()
            
            // Update category index if category changed
            if (oldProduct != null && oldProduct.categoryId != product.categoryId) {
                // Remove from old category
                if (oldProduct.categoryId.isNotEmpty()) {
                    categoryProductsRef.child(oldProduct.categoryId).child(product.id).removeValue().await()
                }
                // Add to new category
                if (product.categoryId.isNotEmpty()) {
                    categoryProductsRef.child(product.categoryId).child(product.id).setValue(true).await()
                }
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Delete a product and its variants
     */
    suspend fun deleteProduct(productId: String): Result<Unit> {
        return try {
            val product = getProduct(productId)
            
            // Delete product
            productsRef.child(productId).removeValue().await()
            
            // Remove from category index
            if (product != null && product.categoryId.isNotEmpty()) {
                categoryProductsRef.child(product.categoryId).child(productId).removeValue().await()
            }
            
            // Delete all variants
            val variantIds = variantsByProductRef.child(productId).get().await().children.map { it.key ?: "" }
            variantIds.forEach { variantId ->
                if (variantId.isNotEmpty()) {
                    variantsRef.child(variantId).removeValue().await()
                }
            }
            
            // Delete variant index
            variantsByProductRef.child(productId).removeValue().await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get all variants for a product
     */
    fun getProductVariants(productId: String): Flow<List<ProductVariant>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val variantIds = snapshot.children.mapNotNull { it.key }
                
                // Fetch full variant data
                val variants = mutableListOf<ProductVariant>()
                var completed = 0
                
                if (variantIds.isEmpty()) {
                    trySend(emptyList())
                    return
                }
                
                variantIds.forEach { variantId ->
                    variantsRef.child(variantId).get().addOnSuccessListener { variantSnapshot ->
                        variantSnapshot.getValue(ProductVariant::class.java)?.let {
                            variants.add(it.copy(id = variantId))
                        }
                        completed++
                        if (completed == variantIds.size) {
                            trySend(variants)
                        }
                    }
                }
            }
            
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        
        variantsByProductRef.child(productId).addValueEventListener(listener)
        awaitClose { variantsByProductRef.child(productId).removeEventListener(listener) }
    }
    
    /**
     * Add a product variant
     */
    suspend fun addVariant(variant: ProductVariant): Result<String> {
        return try {
            val variantId = variant.id.ifEmpty { variantsRef.push().key ?: return Result.failure(Exception("Failed to generate variant ID")) }
            val variantData = variant.copy(id = variantId)
            
            // Save variant
            variantsRef.child(variantId).setValue(variantData).await()
            
            // Add to product index
            variantsByProductRef.child(variant.productId).child(variantId).setValue(true).await()
            
            Result.success(variantId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Update a product variant
     */
    suspend fun updateVariant(variant: ProductVariant): Result<Unit> {
        return try {
            if (variant.id.isEmpty()) {
                return Result.failure(Exception("Variant ID is required"))
            }
            
            variantsRef.child(variant.id).setValue(variant).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Delete a product variant
     */
    suspend fun deleteVariant(variantId: String, productId: String): Result<Unit> {
        return try {
            // Delete variant
            variantsRef.child(variantId).removeValue().await()
            
            // Remove from product index
            variantsByProductRef.child(productId).child(variantId).removeValue().await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
