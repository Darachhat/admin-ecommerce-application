package com.ecommerce.adminapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Banner(
    val id: String = "",
    val imageUrl: String = "",
    val active: Boolean = true
) : Parcelable {
    // No-argument constructor for Firebase
    constructor() : this("", "", true)
}
