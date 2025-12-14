package com.ecommerce.adminapp.utils

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

class CloudinaryHelper(private val context: Context) {
    
    private val client = OkHttpClient()
    
    suspend fun uploadImage(uri: Uri): Result<String> = withContext(Dispatchers.IO) {
        try {
            // Create temporary file from URI
            val inputStream = context.contentResolver.openInputStream(uri)
            val tempFile = File.createTempFile("upload", ".jpg", context.cacheDir)
            FileOutputStream(tempFile).use { output ->
                inputStream?.copyTo(output)
            }
            
            // Create multipart request
            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "file",
                    tempFile.name,
                    tempFile.asRequestBody("image/*".toMediaTypeOrNull())
                )
                .addFormDataPart("upload_preset", Constants.CLOUDINARY_UPLOAD_PRESET)
                .build()
            
            val request = Request.Builder()
                .url(Constants.CLOUDINARY_UPLOAD_URL)
                .post(requestBody)
                .build()
            
            val response = client.newCall(request).execute()
            
            tempFile.delete()
            
            if (response.isSuccessful) {
                val jsonResponse = JSONObject(response.body?.string() ?: "")
                val imageUrl = jsonResponse.getString("secure_url")
                Result.success(imageUrl)
            } else {
                Result.failure(Exception("Upload failed: ${response.code}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun uploadMultipleImages(uris: List<Uri>): Result<List<String>> = withContext(Dispatchers.IO) {
        try {
            val uploadedUrls = mutableListOf<String>()
            for (uri in uris) {
                val result = uploadImage(uri)
                if (result.isSuccess) {
                    uploadedUrls.add(result.getOrNull() ?: "")
                } else {
                    return@withContext Result.failure(result.exceptionOrNull() ?: Exception("Upload failed"))
                }
            }
            Result.success(uploadedUrls)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
