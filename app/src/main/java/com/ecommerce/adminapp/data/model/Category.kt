package com.ecommerce.adminapp.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Category(
    var id: Int = 0,
    var title: String = "",
    var picUrl: String = ""
) : Parcelable {
    constructor() : this(0, "", "")
    
    fun toMap(): Map<String, Any> {
        return hashMapOf(
            "id" to id,
            "title" to title,
            "picUrl" to picUrl
        )
    }
}
