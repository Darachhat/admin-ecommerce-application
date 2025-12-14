package com.ecommerce.adminapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class InventoryLog(
    val id: String = "",
    val variantId: String = "",
    val delta: Int = 0, // Positive for additions, negative for reductions
    val reason: String = "", // "order", "restock", "adjustment", etc.
    val refId: String = "", // Reference ID (e.g., order ID)
    val createdAt: Long = System.currentTimeMillis() / 1000
) : Parcelable {
    // No-argument constructor for Firebase
    constructor() : this("", "", 0, "", "", System.currentTimeMillis() / 1000)
}
