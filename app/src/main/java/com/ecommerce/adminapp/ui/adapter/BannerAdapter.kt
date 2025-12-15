package com.ecommerce.adminapp.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ecommerce.adminapp.R
import com.ecommerce.adminapp.data.model.Banner
import com.ecommerce.adminapp.databinding.ItemBannerBinding

class BannerAdapter(
    private val onEditClick: (Banner) -> Unit,
    private val onDeleteClick: (Banner) -> Unit
) : ListAdapter<Banner, BannerAdapter.BannerViewHolder>(BannerDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val binding = ItemBannerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BannerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        val banner = getItem(position)
        holder.bind(banner)
    }

    inner class BannerViewHolder(private val binding: ItemBannerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(banner: Banner) {
            binding.apply {
                // Debug logging
                Log.d("BannerAdapter", "===================")
                Log.d("BannerAdapter", "Banner ID: ${banner.id}")
                Log.d("BannerAdapter", "Banner name: ${banner.name}")
                Log.d("BannerAdapter", "PicUrl: '${banner.picUrl}'")
                Log.d("BannerAdapter", "PicUrl isEmpty: ${banner.picUrl.isEmpty()}")
                Log.d("BannerAdapter", "===================")

                // Load banner image
                val imageUrl = banner.picUrl.ifEmpty {
                    Log.e("BannerAdapter", "⚠️ PicUrl is EMPTY for banner ${banner.id}!")
                    "https://images.unsplash.com/photo-1460353581641-37baddab0fa2?w=800" // Fallback
                }
                
                Glide.with(itemView.context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_placeholder)
                    .into(bannerImageView)
                
                Log.d("BannerAdapter", "✅ Glide load initiated for banner ${banner.id}")

                bannerNameTextView.text = if (banner.name.isEmpty()) "Banner ${banner.id}" else banner.name
                activeStatus.text = if (banner.active) "Active" else "Inactive"

                editButton.setOnClickListener { onEditClick(banner) }
                deleteButton.setOnClickListener { onDeleteClick(banner) }
            }
        }
    }

    private class BannerDiffCallback : DiffUtil.ItemCallback<Banner>() {
        override fun areItemsTheSame(oldItem: Banner, newItem: Banner): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Banner, newItem: Banner): Boolean {
            return oldItem == newItem
        }
    }
}
