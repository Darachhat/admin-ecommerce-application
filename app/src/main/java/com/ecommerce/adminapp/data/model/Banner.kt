package com.ecommerce.adminapp.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Banner(
    var id: Int = 0,
    var url: String = ""
) : Parcelable {
    constructor() : this(0, "")
    
    fun toMap(): Map<String, Any> {
        return hashMapOf(
            "id" to id,
            "url" to url
        )
    }
}
