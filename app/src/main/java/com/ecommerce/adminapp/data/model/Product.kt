package com.ecommerce.adminapp.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    var id: String = "",
    var title: String = "",
    var description: String = "",
    var picUrl: List<String> = emptyList(),
    var thumbnail: String? = null,  // Single thumbnail image
    var price: Double = 0.0,
    var oldPrice: Double? = null,
    var rating: Double = 0.0,
    var size: List<String> = emptyList(),
    var color: List<String> = emptyList(),
    var categoryId: String = "",  // Changed from Int to String
    var brandId: String? = null,
    var currency: String = "USD",
    var showRecommended: Boolean = false,
    var stock: Int = 0,
    var active: Boolean = true,
    var createdAt: Long = 0
) : Parcelable {
    // Firebase requires a no-argument constructor
    constructor() : this("", "", "", emptyList(), null, 0.0, null, 0.0, emptyList(), emptyList(), "", null, "USD", false, 0, true, 0)
    
    fun toMap(): Map<String, Any?> {
        return hashMapOf(
            "title" to title,
            "description" to description,
            "picUrl" to picUrl,
            "thumbnail" to thumbnail,
            "price" to price,
            "oldPrice" to oldPrice,
            "rating" to rating,
            "size" to size,
            "color" to color,
            "categoryId" to categoryId,
            "brandId" to brandId,
            "currency" to currency,
            "showRecommended" to showRecommended,
            "stock" to stock,
            "active" to active,
            "createdAt" to createdAt
        )
    }
}
