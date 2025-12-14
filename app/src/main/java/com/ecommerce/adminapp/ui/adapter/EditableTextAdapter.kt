package com.ecommerce.adminapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ecommerce.adminapp.databinding.ItemEditableTextBinding

class EditableTextAdapter(
    private val onDeleteClick: (String, Int) -> Unit
) : ListAdapter<String, EditableTextAdapter.ViewHolder>(TextDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemEditableTextBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }
    
    inner class ViewHolder(
        private val binding: ItemEditableTextBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(text: String, position: Int) {
            binding.apply {
                textValue.text = text
                buttonDelete.setOnClickListener {
                    onDeleteClick(text, position)
                }
            }
        }
    }
    
    private class TextDiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
        
        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }
}
