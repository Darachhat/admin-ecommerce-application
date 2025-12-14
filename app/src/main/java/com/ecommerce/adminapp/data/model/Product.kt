package com.ecommerce.adminapp.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    var id: String = "",
    var title: String = "",
    var description: String = "",
    var picUrl: List<String> = emptyList(),
    var price: Double = 0.0,
    var oldPrice: Double? = null,
    var rating: Double = 0.0,
    var size: List<String> = emptyList(),
    var color: List<String> = emptyList(),
    var categoryId: Int = 0,
    var showRecommended: Boolean = false,
    var numberInCart: Int = 0
) : Parcelable {
    // Firebase requires a no-argument constructor
    constructor() : this("", "", "", emptyList(), 0.0, null, 0.0, emptyList(), emptyList(), 0, false, 0)
    
    fun toMap(): Map<String, Any?> {
        return hashMapOf(
            "title" to title,
            "description" to description,
            "picUrl" to picUrl,
            "price" to price,
            "oldPrice" to oldPrice,
            "rating" to rating,
            "size" to size,
            "color" to color,
            "categoryId" to categoryId,
            "showRecommended" to showRecommended,
            "numberInCart" to numberInCart
        )
    }
}
