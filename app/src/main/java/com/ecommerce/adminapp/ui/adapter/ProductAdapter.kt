package com.ecommerce.adminapp.ui.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ecommerce.adminapp.R
import com.ecommerce.adminapp.data.model.Product
import com.ecommerce.adminapp.databinding.ItemProductBinding

class ProductAdapter(
    private val onEditClick: (Product) -> Unit,
    private val onDeleteClick: (Product) -> Unit
) : ListAdapter<Product, ProductAdapter.ProductViewHolder>(ProductDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class ProductViewHolder(
        private val binding: ItemProductBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(product: Product) {
            binding.apply {
                textTitle.text = product.title
                
                // Show current price
                textPrice.text = "$${product.price}"
                
                // Show old price with strikethrough if available
                if (product.oldPrice != null && product.oldPrice!! > product.price) {
                    textOldPrice.visibility = View.VISIBLE
                    textOldPrice.text = "$${product.oldPrice}"
                    textOldPrice.paintFlags = textOldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                } else {
                    textOldPrice.visibility = View.GONE
                }
                
                // Show stock only
                textStock.text = "Stock: ${product.stock}"
                
                // Load first image - try thumbnail first, then picUrl list
                val imageUrl = product.thumbnail ?: product.picUrl.firstOrNull()
                if (!imageUrl.isNullOrBlank()) {
                    Glide.with(imageProduct.context)
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_placeholder)
                        .error(R.drawable.ic_placeholder)
                        .centerCrop()
                        .into(imageProduct)
                } else {
                    imageProduct.setImageResource(R.drawable.ic_placeholder)
                }
                
                buttonEdit.setOnClickListener {
                    onEditClick(product)
                }
                
                buttonDelete.setOnClickListener {
                    onDeleteClick(product)
                }
            }
        }
    }
    
    private class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
}
