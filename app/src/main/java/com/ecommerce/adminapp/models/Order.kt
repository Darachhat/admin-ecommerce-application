package com.ecommerce.adminapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Order(
    val id: String = "",
    val userId: String = "",
    val status: String = "pending", // pending, paid, shipped, delivered, cancelled
    val total: Double = 0.0,
    val currency: String = "USD",
    val createdAt: Long = System.currentTimeMillis() / 1000
) : Parcelable {
    // No-argument constructor for Firebase
    constructor() : this("", "", "pending", 0.0, "USD", System.currentTimeMillis() / 1000)
}
