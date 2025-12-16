package com.ecommerce.adminapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ecommerce.adminapp.databinding.ItemUserBinding
import com.ecommerce.adminapp.models.User
import java.text.SimpleDateFormat
import java.util.*

class UserAdapter(
    private val onUserClick: (User) -> Unit,
    private val onDeleteClick: (User) -> Unit,
    private val onRoleToggle: (User) -> Unit
) : ListAdapter<User, UserAdapter.UserViewHolder>(UserDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UserViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class UserViewHolder(
        private val binding: ItemUserBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(user: User) {
            binding.apply {
                textEmail.text = user.email
                textUserId.text = "ID: ${user.id}"
                
                // Display role with badge
                chipRole.text = user.role.uppercase()
                chipRole.isChecked = user.role == "admin"
                
                // Format and display creation date
                val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
                val date = Date(user.createdAt * 1000) // Convert from seconds to milliseconds
                textCreatedAt.text = "Created: ${dateFormat.format(date)}"
                
                // Set click listeners
                root.setOnClickListener { onUserClick(user) }
                
                buttonDelete.setOnClickListener { onDeleteClick(user) }
                
                chipRole.setOnClickListener { 
                    onRoleToggle(user)
                }
            }
        }
    }
    
    private class UserDiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }
}
