package com.ecommerce.adminapp.data.repository

import com.ecommerce.adminapp.data.model.Banner
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class BannerRepository {

    private val database = FirebaseDatabase.getInstance()
    private val bannersRef = database.getReference("banners")

    fun getAllBanners(): Flow<List<Banner>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val banners = snapshot.children.mapNotNull {
                    it.getValue(Banner::class.java)?.copy(id = it.key ?: "")
                }
                trySend(banners)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        bannersRef.addValueEventListener(listener)
        awaitClose { bannersRef.removeEventListener(listener) }
    }

    suspend fun addBanner(banner: Banner): Result<String> {
        return try {
            val bannerId = if (banner.id.isEmpty()) {
                bannersRef.push().key ?: throw Exception("Failed to generate banner ID")
            } else {
                banner.id
            }
            val bannerData = banner.copy(id = bannerId)

            bannersRef.child(bannerId).setValue(bannerData.toMap()).await()

            Result.success(bannerId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateBanner(banner: Banner): Result<Unit> {
        return try {
            if (banner.id.isEmpty()) {
                return Result.failure(Exception("Banner ID is required"))
            }

            bannersRef.child(banner.id).setValue(banner.toMap()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteBanner(bannerId: String): Result<Unit> {
        return try {
            bannersRef.child(bannerId).removeValue().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
