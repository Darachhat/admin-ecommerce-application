package com.ecommerce.adminapp.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ecommerce.adminapp.R
import com.ecommerce.adminapp.data.model.Brand
import com.ecommerce.adminapp.databinding.ItemBrandBinding

class BrandAdapter(
    private val onEditClick: (Brand) -> Unit,
    private val onDeleteClick: (Brand) -> Unit
) : ListAdapter<Brand, BrandAdapter.BrandViewHolder>(BrandDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrandViewHolder {
        val binding = ItemBrandBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BrandViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BrandViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class BrandViewHolder(
        private val binding: ItemBrandBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(brand: Brand) {
            binding.apply {
                brandName.text = brand.name
                brandId.text = "ID: ${brand.id}"

                // Debug logging
                Log.d("BrandAdapter", "Loading brand: ${brand.name}, picUrl: ${brand.picUrl}")

                // Load brand image
                val imageUrl = brand.picUrl.ifEmpty {
                    Log.e("BrandAdapter", "⚠️ PicUrl is EMPTY for ${brand.name}!")
                    "https://images.unsplash.com/photo-1491553895911-0055eca6402d?w=400"
                }
                
                try {
                    Glide.with(brandImage.context)
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_placeholder)
                        .error(R.drawable.ic_placeholder)
                        .into(brandImage)
                    Log.d("BrandAdapter", "✅ Glide load initiated for ${brand.name}")
                } catch (e: Exception) {
                    Log.e("BrandAdapter", "❌ Glide exception: ${e.message}")
                    brandImage.setImageResource(R.drawable.ic_placeholder)
                }

                // Set active status
                if (brand.active) {
                    activeChip.text = "Active"
                    activeChip.setChipBackgroundColorResource(R.color.green_light)
                } else {
                    activeChip.text = "Inactive"
                    activeChip.setChipBackgroundColorResource(R.color.red_light)
                }

                // Set click listeners
                editButton.setOnClickListener {
                    onEditClick(brand)
                }

                deleteButton.setOnClickListener {
                    onDeleteClick(brand)
                }
            }
        }
    }

    class BrandDiffCallback : DiffUtil.ItemCallback<Brand>() {
        override fun areItemsTheSame(oldItem: Brand, newItem: Brand): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Brand, newItem: Brand): Boolean {
            return oldItem == newItem
        }
    }
}
