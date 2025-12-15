package com.ecommerce.adminapp. ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ecommerce.adminapp.R
import com.ecommerce. adminapp.data.model.Category
import com.ecommerce. adminapp.databinding.ItemCategoryBinding

class CategoryAdapter(
    private val onEditClick: (Category) -> Unit,
    private val onDeleteClick:  (Category) -> Unit
) : ListAdapter<Category, CategoryAdapter. CategoryViewHolder>(CategoryDiffCallback()) {

    override fun onCreateViewHolder(parent:  ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CategoryViewHolder(
        private val binding: ItemCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(category: Category) {
            binding.apply {
                categoryName.text = category.name
                categoryId.text = "ID: ${category.id}"

                // Debug logging
                Log.d("CategoryAdapter", "===================")
                Log.d("CategoryAdapter", "Category: ${category.name}")
                Log.d("CategoryAdapter", "Category ID: ${category.id}")
                Log.d("CategoryAdapter", "PicUrl: '${category.picUrl}'")
                Log.d("CategoryAdapter", "PicUrl isEmpty: ${category.picUrl.isEmpty()}")
                Log.d("CategoryAdapter", "PicUrl length: ${category.picUrl.length}")
                Log.d("CategoryAdapter", "===================")

                // Load category image
                val imageUrl = category.picUrl.ifEmpty {
                    Log.e("CategoryAdapter", "⚠️ PicUrl is EMPTY for ${category.name}! Firebase not updated!")
                    "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400" // Fallback
                }
                
                Log.d("CategoryAdapter", "Loading URL: $imageUrl")
                
                try {
                    Glide.with(categoryImage.context)
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_placeholder)
                        .error(R.drawable.ic_placeholder)
                        .into(categoryImage)
                    Log.d("CategoryAdapter", "✅ Glide load initiated for ${category.name}")
                } catch (e: Exception) {
                    Log.e("CategoryAdapter", "❌ Glide exception: ${e.message}")
                    categoryImage.setImageResource(R.drawable.ic_placeholder)
                }

                // Set active status
                if (category.active) {
                    activeChip.text = "Active"
                    activeChip.setChipBackgroundColorResource(R.color.green_light)
                } else {
                    activeChip.text = "Inactive"
                    activeChip.setChipBackgroundColorResource(R.color.red_light)
                }

                // Set click listeners
                editButton.setOnClickListener {
                    onEditClick(category)
                }

                deleteButton.setOnClickListener {
                    onDeleteClick(category)
                }
            }
        }
    }

    class CategoryDiffCallback : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem == newItem
        }
    }
}