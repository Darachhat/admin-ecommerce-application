package com.ecommerce.adminapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Brand(
    val id: String = "",
    val name: String = "",
    val active: Boolean = true
) : Parcelable {
    // No-argument constructor for Firebase
    constructor() : this("", "", true)
}
