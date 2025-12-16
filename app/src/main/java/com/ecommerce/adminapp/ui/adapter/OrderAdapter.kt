package com.ecommerce.adminapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ecommerce.adminapp.R
import com.ecommerce.adminapp.databinding.ItemOrderBinding
import com.ecommerce.adminapp.models.Order
import java.text.SimpleDateFormat
import java.util.*

class OrderAdapter(
    private val onOrderClick: (Order) -> Unit,
    private val onStatusChange: (Order) -> Unit,
    private val onDeleteClick: (Order) -> Unit
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {
    
    private var orders = listOf<Order>()
    
    fun submitList(newOrders: List<Order>) {
        val diffCallback = OrderDiffCallback(orders, newOrders)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        orders = newOrders
        diffResult.dispatchUpdatesTo(this)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(orders[position])
    }
    
    override fun getItemCount() = orders.size
    
    inner class OrderViewHolder(
        private val binding: ItemOrderBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(order: Order) {
            binding.apply {
                // Order ID
                txtOrderId.text = "Order #${order.id.takeLast(8)}"
                
                // Customer info
                txtCustomerName.text = order.deliveryInfo.fullName
                txtCustomerEmail.text = order.userEmail
                
                // Order date
                val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
                txtOrderDate.text = dateFormat.format(Date(order.orderDate))
                
                // Total amount
                txtTotalAmount.text = "$${String.format("%.2f", order.pricing.total)}"
                
                // Items count
                val itemCount = order.items.sumOf { it.quantity }
                txtItemsCount.text = "$itemCount ${if (itemCount == 1) "item" else "items"}"
                
                // Status chip
                chipStatus.text = order.status.capitalize()
                chipStatus.setChipBackgroundColorResource(getStatusColor(order.status))
                
                // Payment method
                txtPaymentMethod.text = order.paymentMethod
                
                // Delivery address
                txtDeliveryAddress.text = "${order.deliveryInfo.address}, ${order.deliveryInfo.city}"
                
                // Click listeners
                root.setOnClickListener { onOrderClick(order) }
                btnChangeStatus.setOnClickListener { onStatusChange(order) }
                btnDelete.setOnClickListener { onDeleteClick(order) }
            }
        }
        
        private fun getStatusColor(status: String): Int {
            return when (status.lowercase()) {
                "pending" -> R.color.status_pending
                "processing" -> R.color.status_processing
                "shipped" -> R.color.status_shipped
                "delivered" -> R.color.status_delivered
                "cancelled" -> R.color.status_cancelled
                else -> R.color.status_pending
            }
        }
    }
    
    private class OrderDiffCallback(
        private val oldList: List<Order>,
        private val newList: List<Order>
    ) : DiffUtil.Callback() {
        
        override fun getOldListSize() = oldList.size
        override fun getNewListSize() = newList.size
        
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }
        
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}
