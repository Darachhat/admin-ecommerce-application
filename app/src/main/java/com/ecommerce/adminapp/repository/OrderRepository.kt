package com.ecommerce.adminapp.repository

import com.ecommerce.adminapp.models.Order
import com.ecommerce.adminapp.utils.DatabasePaths
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class OrderRepository {
    
    private val database = FirebaseDatabase.getInstance()
    private val ordersRef = database.getReference(DatabasePaths.ORDERS)
    
    /**
     * Get all orders as a Flow
     */
    fun getAllOrders(): Flow<List<Order>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orders = snapshot.children.mapNotNull { 
                    it.getValue(Order::class.java)?.copy(id = it.key ?: "")
                }
                trySend(orders)
            }
            
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        
        ordersRef.addValueEventListener(listener)
        awaitClose { ordersRef.removeEventListener(listener) }
    }
    
    /**
     * Get a single order by ID
     */
    suspend fun getOrder(orderId: String): Order? {
        return try {
            val snapshot = ordersRef.child(orderId).get().await()
            snapshot.getValue(Order::class.java)?.copy(id = orderId)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Update order status
     */
    suspend fun updateOrderStatus(orderId: String, status: String): Boolean {
        return try {
            ordersRef.child(orderId).child("status").setValue(status).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    /**
     * Delete an order
     */
    suspend fun deleteOrder(orderId: String): Boolean {
        return try {
            ordersRef.child(orderId).removeValue().await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    /**
     * Search orders by customer email
     */
    fun searchOrdersByEmail(email: String): Flow<List<Order>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orders = snapshot.children.mapNotNull { 
                    it.getValue(Order::class.java)?.copy(id = it.key ?: "")
                }.filter { 
                    it.userEmail.contains(email, ignoreCase = true)
                }
                trySend(orders)
            }
            
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        
        ordersRef.addValueEventListener(listener)
        awaitClose { ordersRef.removeEventListener(listener) }
    }
    
    /**
     * Get orders by status
     */
    fun getOrdersByStatus(status: String): Flow<List<Order>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orders = snapshot.children.mapNotNull { 
                    it.getValue(Order::class.java)?.copy(id = it.key ?: "")
                }.filter { it.status == status }
                trySend(orders)
            }
            
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        
        ordersRef.addValueEventListener(listener)
        awaitClose { ordersRef.removeEventListener(listener) }
    }
    
    /**
     * Get total revenue
     */
    suspend fun getTotalRevenue(): Double {
        return try {
            val snapshot = ordersRef.get().await()
            snapshot.children.mapNotNull { 
                it.getValue(Order::class.java)
            }.filter { 
                it.status != "cancelled" 
            }.sumOf { 
                it.pricing.total 
            }
        } catch (e: Exception) {
            e.printStackTrace()
            0.0
        }
    }
}
