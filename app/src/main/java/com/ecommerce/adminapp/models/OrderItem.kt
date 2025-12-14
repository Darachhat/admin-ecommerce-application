package com.ecommerce.adminapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderItem(
    val variantId: String = "",
    val productId: String = "",
    val price: Double = 0.0,
    val quantity: Int = 0,
    val subtotal: Double = 0.0
) : Parcelable {
    // No-argument constructor for Firebase
    constructor() : this("", "", 0.0, 0, 0.0)
}
