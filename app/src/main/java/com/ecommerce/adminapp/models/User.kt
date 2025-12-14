package com.ecommerce.adminapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: String = "",
    val email: String = "",
    val role: String = "user", // "user" or "admin"
    val createdAt: Long = System.currentTimeMillis() / 1000
) : Parcelable {
    // No-argument constructor for Firebase
    constructor() : this("", "", "user", System.currentTimeMillis() / 1000)
}
