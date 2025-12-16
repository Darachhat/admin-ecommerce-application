package com.ecommerce.adminapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Order(
    val id: String = "",
    val userId: String = "",
    val userEmail: String = "",
    val orderDate: Long = System.currentTimeMillis(),
    val status: String = "pending", // pending, processing, shipped, delivered, cancelled
    val deliveryInfo: DeliveryInfo = DeliveryInfo(),
    val paymentMethod: String = "",
    val items: List<OrderItemDetail> = emptyList(),
    val pricing: OrderPricing = OrderPricing()
) : Parcelable {
    constructor() : this("", "", "", System.currentTimeMillis(), "pending", DeliveryInfo(), "", emptyList(), OrderPricing())
}

@Parcelize
data class DeliveryInfo(
    val fullName: String = "",
    val phone: String = "",
    val address: String = "",
    val city: String = "",
    val postalCode: String = ""
) : Parcelable {
    constructor() : this("", "", "", "", "")
}

@Parcelize
data class OrderItemDetail(
    val productId: String = "",
    val title: String = "",
    val price: Double = 0.0,
    val quantity: Int = 0,
    val size: String = "",
    val color: String = "",
    val thumbnail: String = ""
) : Parcelable {
    constructor() : this("", "", 0.0, 0, "", "", "")
}

@Parcelize
data class OrderPricing(
    val subtotal: Double = 0.0,
    val tax: Double = 0.0,
    val delivery: Double = 0.0,
    val total: Double = 0.0
) : Parcelable {
    constructor() : this(0.0, 0.0, 0.0, 0.0)
}
