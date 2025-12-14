package com.ecommerce.adminapp.utils

import android.content.Context
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import java.util.UUID

class ImageUploadHelper(private val context: Context) {
    
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    private val storageRef: StorageReference = storage.reference
    
    /**
     * Upload image to Firebase Storage
     * @param imageUri The URI of the image to upload
     * @param folder The folder name in Firebase Storage (default: "products")
     * @return Result containing the download URL or an error
     */
    suspend fun uploadImage(
        imageUri: Uri,
        folder: String = "products"
    ): Result<String> {
        return try {
            // Generate unique filename
            val filename = "${UUID.randomUUID()}.jpg"
            val imageRef = storageRef.child("$folder/$filename")
            
            // Upload file
            val uploadTask = imageRef.putFile(imageUri).await()
            
            // Get download URL
            val downloadUrl = imageRef.downloadUrl.await()
            
            Result.success(downloadUrl.toString())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Upload multiple images to Firebase Storage
     * @param imageUris List of image URIs to upload
     * @param folder The folder name in Firebase Storage
     * @param onProgress Callback for progress updates (current, total)
     * @return Result containing list of download URLs or an error
     */
    suspend fun uploadMultipleImages(
        imageUris: List<Uri>,
        folder: String = "products",
        onProgress: ((current: Int, total: Int) -> Unit)? = null
    ): Result<List<String>> {
        return try {
            val downloadUrls = mutableListOf<String>()
            
            imageUris.forEachIndexed { index, uri ->
                val result = uploadImage(uri, folder)
                if (result.isSuccess) {
                    downloadUrls.add(result.getOrThrow())
                    onProgress?.invoke(index + 1, imageUris.size)
                } else {
                    return Result.failure(result.exceptionOrNull() ?: Exception("Upload failed"))
                }
            }
            
            Result.success(downloadUrls)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Delete image from Firebase Storage
     * @param imageUrl The download URL of the image to delete
     * @return Result indicating success or failure
     */
    suspend fun deleteImage(imageUrl: String): Result<Unit> {
        return try {
            val imageRef = storage.getReferenceFromUrl(imageUrl)
            imageRef.delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Delete multiple images from Firebase Storage
     * @param imageUrls List of download URLs to delete
     * @return Result indicating success or failure
     */
    suspend fun deleteMultipleImages(imageUrls: List<String>): Result<Unit> {
        return try {
            imageUrls.forEach { url ->
                try {
                    val imageRef = storage.getReferenceFromUrl(url)
                    imageRef.delete().await()
                } catch (e: Exception) {
                    // Continue deleting other images even if one fails
                    android.util.Log.e("ImageUploadHelper", "Failed to delete: $url", e)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
