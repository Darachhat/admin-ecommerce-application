package com.ecommerce.adminapp.ui.users

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.ecommerce.adminapp.R
import com.ecommerce.adminapp.databinding.FragmentUsersBinding
import com.ecommerce.adminapp.models.User
import com.ecommerce.adminapp.repository.UserRepository
import com.ecommerce.adminapp.ui.adapter.UserAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class UsersFragment : Fragment() {
    
    private var _binding: FragmentUsersBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var userRepository: UserRepository
    private lateinit var userAdapter: UserAdapter
    
    private var allUsers = listOf<User>()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUsersBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        userRepository = UserRepository()
        setupRecyclerView()
        loadUsers()

        binding.backBtn.setOnClickListener {
            findNavController().navigate(com.ecommerce.adminapp.R.id.productsFragment)
        }

        val toolbar = requireActivity().findViewById<com.google.android.material.appbar.MaterialToolbar>(com.ecommerce.adminapp.R.id.toolbar)
        toolbar?.setOnClickListener {
            findNavController().navigate(com.ecommerce.adminapp.R.id.productsFragment)
        }
    }
    
    private fun setupRecyclerView() {
        userAdapter = UserAdapter(
            onUserClick = { user -> showUserDetailsDialog(user) },
            onDeleteClick = { user -> confirmDeleteUser(user) },
            onRoleToggle = { user -> toggleUserRole(user) }
        )
        
        binding.recyclerViewUsers.apply {
            adapter = userAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }
    
    // Search removed from UI per request; filtering can still be triggered programmatically if desired
    
    private fun loadUsers() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.emptyView.visibility = View.GONE
                    userRepository.getAllUsers().collect { users ->
                        allUsers = users.sortedByDescending { it.createdAt }
                        updateUI(users)
                    }
                }
            } catch (e: Exception) {
                if (_binding != null) {
                    binding.progressBar.visibility = View.GONE
                }
                Toast.makeText(
                    requireContext(),
                    "Error loading users: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    
    private fun updateUI(users: List<User>) {
        binding.progressBar.visibility = View.GONE
        
        if (users.isEmpty()) {
            binding.emptyView.visibility = View.VISIBLE
            binding.recyclerViewUsers.visibility = View.GONE
        } else {
            binding.emptyView.visibility = View.GONE
            binding.recyclerViewUsers.visibility = View.VISIBLE
            userAdapter.submitList(users)
        }
        
        // Update stats
        val adminCount = users.count { it.role.equals("admin", ignoreCase = true) }
        val userCount = users.size - adminCount
        binding.textTotalUsers.text = "Total: ${users.size}"
        binding.textAdminCount.text = "Admins: $adminCount"
        binding.textUserCount.text = "Users: $userCount"
    }
    
    private fun filterUsers(query: String) {
        val filteredList = if (query.isEmpty()) {
            allUsers
        } else {
            allUsers.filter { user ->
                user.email.contains(query, ignoreCase = true) ||
                user.id.contains(query, ignoreCase = true) ||
                user.role.contains(query, ignoreCase = true)
            }
        }
        userAdapter.submitList(filteredList)
        
        if (filteredList.isEmpty() && query.isNotEmpty()) {
            binding.emptyView.visibility = View.VISIBLE
            binding.recyclerViewUsers.visibility = View.GONE
        } else {
            binding.emptyView.visibility = View.GONE
            binding.recyclerViewUsers.visibility = View.VISIBLE
        }
    }
    
    private fun showUserDetailsDialog(user: User) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_user_details, null)
        
        val emailInput = dialogView.findViewById<TextInputEditText>(R.id.editTextEmail)
        val roleSpinner = dialogView.findViewById<com.google.android.material.button.MaterialButtonToggleGroup>(R.id.roleToggleGroup)
        
        emailInput.setText(user.email)
        emailInput.isEnabled = false // Email is not editable
        
        // Set current role
        val userRoleButton = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.buttonUserRole)
        val adminRoleButton = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.buttonAdminRole)
        
        if (user.role == "admin") {
            adminRoleButton.isChecked = true
        } else {
            userRoleButton.isChecked = true
        }
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("User Details")
            .setView(dialogView)
            .setPositiveButton("Update") { _, _ ->
                val newRole = if (adminRoleButton.isChecked) "admin" else "user"
                if (newRole != user.role) {
                    updateUserRole(user, newRole)
                }
            }
            .setNegativeButton("Cancel", null)
            .setNeutralButton("Delete") { _, _ ->
                confirmDeleteUser(user)
            }
            .show()
    }
    
    private fun toggleUserRole(user: User) {
        val newRole = if (user.role == "admin") "user" else "admin"
        updateUserRole(user, newRole)
    }
    
    private fun updateUserRole(user: User, newRole: String) {
        lifecycleScope.launch {
            try {
                val success = userRepository.updateUserRole(user.id, newRole)
                if (success) {
                    Toast.makeText(
                        requireContext(),
                        "User role updated to $newRole",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Failed to update user role",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    
    private fun confirmDeleteUser(user: User) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete User")
            .setMessage("Are you sure you want to delete user ${user.email}? This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                deleteUser(user)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun deleteUser(user: User) {
        lifecycleScope.launch {
            try {
                binding.progressBar.visibility = View.VISIBLE
                val success = userRepository.deleteUser(user.id)
                binding.progressBar.visibility = View.GONE
                
                if (success) {
                    Toast.makeText(
                        requireContext(),
                        "User deleted successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Failed to delete user",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(
                    requireContext(),
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
