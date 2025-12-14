package com.ecommerce.adminapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductVariant(
    val id: String = "",
    val productId: String = "",
    val size: String = "",
    val color: String = "",
    val sku: String = "",
    val price: Double = 0.0,
    val stock: Int = 0,
    val active: Boolean = true
) : Parcelable {
    // No-argument constructor for Firebase
    constructor() : this("", "", "", "", "", 0.0, 0, true)
}
