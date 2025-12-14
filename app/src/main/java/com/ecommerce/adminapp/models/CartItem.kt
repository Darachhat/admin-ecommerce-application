package com.ecommerce.adminapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartItem(
    val variantId: String = "",
    val quantity: Int = 0,
    val priceSnapshot: Double = 0.0
) : Parcelable {
    // No-argument constructor for Firebase
    constructor() : this("", 0, 0.0)
}
