package com.ecommerce.adminapp.data.model

import android.os.Parcelable
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize

@Parcelize
@IgnoreExtraProperties
data class Category(
    var id: String = "",
    var name: String = "",
    var picUrl: String = "",
    var active: Boolean = true
) : Parcelable {
    constructor() : this("", "", "", true)

    fun toMap(): Map<String, Any> {
        return hashMapOf(
            "name" to name,
            "picUrl" to picUrl,
            "active" to active
        )
    }
}
