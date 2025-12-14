package com.ecommerce.adminapp.ui.adapter

import android.view.LayoutInflater
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
                textDescription.text = product.description
                textPrice.text = "$${product.price}"
                textStock.text = "Stock: ${product.numberInCart}"
                
                // Load first image
                if (product.picUrl.isNotEmpty()) {
                    Glide.with(imageProduct.context)
                        .load(product.picUrl[0])
                        .placeholder(R.drawable.ic_placeholder)
                        .into(imageProduct)
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
