package com.ecommerce.adminapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val id: String = "",
    val title: String = "",
    val brandId: String = "",
    val categoryId: String = "",
    val price: Double = 0.0,
    val currency: String = "USD",
    val thumbnail: String = "",
    val active: Boolean = true,
    val createdAt: Long = System.currentTimeMillis() / 1000
) : Parcelable {
    // No-argument constructor for Firebase
    constructor() : this("", "", "", "", 0.0, "USD", "", true, System.currentTimeMillis() / 1000)
}
