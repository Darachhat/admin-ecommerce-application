package com.ecommerce.adminapp.ui.orders

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ecommerce.adminapp.R
import com.ecommerce.adminapp.databinding.DialogOrderDetailsBinding
import com.ecommerce.adminapp.databinding.FragmentOrdersBinding
import com.ecommerce.adminapp.models.Order
import com.ecommerce.adminapp.repository.OrderRepository
import com.ecommerce.adminapp.ui.adapter.OrderAdapter
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class OrdersFragment : Fragment() {
    
    private var _binding: FragmentOrdersBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var orderRepository: OrderRepository
    private lateinit var orderAdapter: OrderAdapter
    
    private var allOrders = listOf<Order>()
    private var currentFilter = "all"
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        orderRepository = OrderRepository()
        setupRecyclerView()
        setupSearchView()
        setupFilterChips()
        loadOrders()
    }
    
    private fun setupRecyclerView() {
        orderAdapter = OrderAdapter(
            onOrderClick = { order -> showOrderDetailsDialog(order) },
            onStatusChange = { order -> showChangeStatusDialog(order) },
            onDeleteClick = { order -> confirmDeleteOrder(order) }
        )
        
        binding.recyclerViewOrders.apply {
            adapter = orderAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }
    
    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            
            override fun onQueryTextChange(newText: String?): Boolean {
                filterOrders(newText ?: "")
                return true
            }
        })
    }
    
    private fun setupFilterChips() {
        binding.chipGroupStatus.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isEmpty()) {
                currentFilter = "all"
            } else {
                val checkedChip = group.findViewById<Chip>(checkedIds[0])
                currentFilter = when (checkedChip.id) {
                    R.id.chipAll -> "all"
                    R.id.chipPending -> "pending"
                    R.id.chipProcessing -> "processing"
                    R.id.chipShipped -> "shipped"
                    R.id.chipDelivered -> "delivered"
                    R.id.chipCancelled -> "cancelled"
                    else -> "all"
                }
            }
            filterOrders(binding.searchView.query.toString())
        }
    }
    
    private fun loadOrders() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                _binding?.progressBar?.visibility = View.VISIBLE
                _binding?.emptyView?.visibility = View.GONE
                
                orderRepository.getAllOrders().collect { orders ->
                    allOrders = orders.sortedByDescending { it.orderDate }
                    updateUI(orders)
                    updateStatistics(orders)
                }
            } catch (e: Exception) {
                _binding?.progressBar?.visibility = View.GONE
                if (isAdded) {
                    Toast.makeText(
                        requireContext(),
                        "Error loading orders: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
    
    private fun updateUI(orders: List<Order>) {
        _binding?.progressBar?.visibility = View.GONE
        
        if (orders.isEmpty()) {
            _binding?.emptyView?.visibility = View.VISIBLE
            _binding?.recyclerViewOrders?.visibility = View.GONE
        } else {
            _binding?.emptyView?.visibility = View.GONE
            _binding?.recyclerViewOrders?.visibility = View.VISIBLE
            orderAdapter.submitList(orders)
        }
    }
    
    private fun updateStatistics(orders: List<Order>) {
        val totalOrders = orders.size
        val totalRevenue = orders.filter { it.status != "cancelled" }.sumOf { it.pricing.total }
        val pendingOrders = orders.count { it.status == "pending" }
        
        _binding?.apply {
            txtTotalOrders.text = totalOrders.toString()
            txtTotalRevenue.text = "$${String.format("%.2f", totalRevenue)}"
            txtPendingOrders.text = pendingOrders.toString()
        }
    }
    
    private fun filterOrders(query: String) {
        val filtered = allOrders.filter { order ->
            val matchesSearch = if (query.isEmpty()) {
                true
            } else {
                order.userEmail.contains(query, ignoreCase = true) ||
                order.deliveryInfo.fullName.contains(query, ignoreCase = true) ||
                order.id.contains(query, ignoreCase = true)
            }
            
            val matchesStatus = if (currentFilter == "all") {
                true
            } else {
                order.status == currentFilter
            }
            
            matchesSearch && matchesStatus
        }
        
        updateUI(filtered)
    }
    
    private fun showOrderDetailsDialog(order: Order) {
        val dialogBinding = DialogOrderDetailsBinding.inflate(layoutInflater)
        
        dialogBinding.apply {
            // Order info
            txtOrderIdDetail.text = "Order #${order.id.takeLast(8)}"
            
            val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
            txtOrderDateDetail.text = dateFormat.format(Date(order.orderDate))
            
            txtStatusDetail.text = order.status.capitalize()
            txtStatusDetail.setBackgroundColor(
                resources.getColor(getStatusColor(order.status), null)
            )
            
            // Customer info
            txtCustomerNameDetail.text = order.deliveryInfo.fullName
            txtCustomerEmailDetail.text = order.userEmail
            txtCustomerPhoneDetail.text = order.deliveryInfo.phone
            
            // Delivery address
            txtDeliveryAddressDetail.text = buildString {
                append(order.deliveryInfo.address)
                append("\n")
                append(order.deliveryInfo.city)
                append(", ")
                append(order.deliveryInfo.postalCode)
            }
            
            // Payment info
            txtPaymentMethodDetail.text = order.paymentMethod
            
            // Order items
            val itemsText = buildString {
                order.items.forEachIndexed { index, item ->
                    append("${index + 1}. ${item.title}\n")
                    append("   Size: ${item.size}, Color: ${item.color}\n")
                    append("   Qty: ${item.quantity} x $${String.format("%.2f", item.price)}")
                    append(" = $${String.format("%.2f", item.price * item.quantity)}\n")
                    if (index < order.items.size - 1) append("\n")
                }
            }
            txtOrderItemsDetail.text = itemsText
            
            // Pricing
            txtSubtotalDetail.text = "$${String.format("%.2f", order.pricing.subtotal)}"
            txtTaxDetail.text = "$${String.format("%.2f", order.pricing.tax)}"
            txtDeliveryDetail.text = "$${String.format("%.2f", order.pricing.delivery)}"
            txtTotalDetail.text = "$${String.format("%.2f", order.pricing.total)}"
        }
        
        MaterialAlertDialogBuilder(requireContext())
            .setView(dialogBinding.root)
            .setPositiveButton("Close", null)
            .setNeutralButton("Change Status") { _, _ ->
                showChangeStatusDialog(order)
            }
            .show()
    }
    
    private fun showChangeStatusDialog(order: Order) {
        val statuses = arrayOf("pending", "processing", "shipped", "delivered", "cancelled")
        val currentIndex = statuses.indexOf(order.status)
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Change Order Status")
            .setSingleChoiceItems(
                statuses.map { it.capitalize() }.toTypedArray(),
                currentIndex
            ) { dialog, which ->
                val newStatus = statuses[which]
                updateOrderStatus(order.id, newStatus)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun updateOrderStatus(orderId: String, newStatus: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val success = orderRepository.updateOrderStatus(orderId, newStatus)
                if (isAdded) {
                    if (success) {
                        Toast.makeText(
                            requireContext(),
                            "Order status updated to ${newStatus.capitalize()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Failed to update order status",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                if (isAdded) {
                    Toast.makeText(
                        requireContext(),
                        "Error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
    
    private fun confirmDeleteOrder(order: Order) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Order")
            .setMessage("Are you sure you want to delete order #${order.id.takeLast(8)}? This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                deleteOrder(order.id)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun deleteOrder(orderId: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val success = orderRepository.deleteOrder(orderId)
                if (isAdded) {
                    if (success) {
                        Toast.makeText(
                            requireContext(),
                            "Order deleted successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Failed to delete order",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                if (isAdded) {
                    Toast.makeText(
                        requireContext(),
                        "Error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
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
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
