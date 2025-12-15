package com.ecommerce.adminapp.ui.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.ecommerce.adminapp.data.model.Category
import com.ecommerce.adminapp.databinding.DialogAddEditCategoryBinding
import com.ecommerce.adminapp.databinding.FragmentCategoriesBinding
import com.ecommerce.adminapp.ui.adapter.CategoryAdapter
import kotlinx.coroutines.launch

class CategoriesFragment : Fragment() {
    
    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: CategoriesViewModel by viewModels()
    private lateinit var categoryAdapter: CategoryAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupFab()
        observeViewModel()
    }
    
    private fun setupRecyclerView() {
        categoryAdapter = CategoryAdapter(
            onEditClick = { category -> showAddEditDialog(category) },
            onDeleteClick = { category -> showDeleteConfirmation(category) }
        )
        
        binding.recyclerCategories.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = categoryAdapter
        }
    }
    
    private fun setupFab() {
        binding.fabAddCategory.setOnClickListener {
            showAddEditDialog(null)
        }
    }
    
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.categories.collect { categories ->
                categoryAdapter.submitList(categories)
                binding.textEmpty.visibility = if (categories.isEmpty()) View.VISIBLE else View.GONE
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.saveResult.collect { result ->
                result?.let {
                    if (it.isSuccess) {
                        Toast.makeText(requireContext(), "Category saved successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Error: ${it.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
                    }
                    viewModel.clearSaveResult()
                }
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.deleteResult.collect { result ->
                result?.let {
                    if (it.isSuccess) {
                        Toast.makeText(requireContext(), "Category deleted successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Error: ${it.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
                    }
                    viewModel.clearDeleteResult()
                }
            }
        }
    }
    
    private fun showAddEditDialog(category: Category?) {
        val dialogBinding = DialogAddEditCategoryBinding.inflate(layoutInflater)
        
        // Set dialog title
        dialogBinding.textDialogTitle.text = if (category == null) "Add Category" else "Edit Category"
        
        // Pre-fill data if editing
        category?.let {
            dialogBinding.editCategoryId.setText(it.id.toString())
            dialogBinding.editCategoryId.isEnabled = false // Don't allow changing ID when editing
            dialogBinding.editCategoryTitle.setText(it.title)
            dialogBinding.editCategoryImage.setText(it.picUrl)
            
            // Show image preview
            if (it.picUrl.isNotEmpty()) {
                dialogBinding.imagePreview.visibility = View.VISIBLE
                Glide.with(requireContext())
                    .load(it.picUrl)
                    .into(dialogBinding.imagePreview)
            }
        }
        
        // Add text change listener for image preview
        dialogBinding.editCategoryImage.addTextChangedListener { text ->
            val url = text.toString()
            if (url.isNotEmpty()) {
                dialogBinding.imagePreview.visibility = View.VISIBLE
                Glide.with(requireContext())
                    .load(url)
                    .into(dialogBinding.imagePreview)
            } else {
                dialogBinding.imagePreview.visibility = View.GONE
            }
        }
        
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()
        
        dialogBinding.buttonCancel.setOnClickListener {
            dialog.dismiss()
        }
        
        dialogBinding.buttonSave.setOnClickListener {
            val id = dialogBinding.editCategoryId.text.toString().toIntOrNull() ?: 0
            val title = dialogBinding.editCategoryTitle.text.toString().trim()
            val picUrl = dialogBinding.editCategoryImage.text.toString().trim()
            
            // Validate
            val validationError = viewModel.validateCategory(id, title, picUrl)
            if (validationError != null) {
                Toast.makeText(requireContext(), validationError, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            val newCategory = Category(id = id, title = title, picUrl = picUrl)
            
            if (category == null) {
                // Add new category
                viewModel.addCategory(newCategory)
            } else {
                // Update existing category
                val key = "cat_${category.id}"
                viewModel.updateCategory(key, newCategory)
            }
            
            dialog.dismiss()
        }
        
        dialog.show()
    }
    
    private fun showDeleteConfirmation(category: Category) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Category")
            .setMessage("Are you sure you want to delete '${category.title}'?")
            .setPositiveButton("Delete") { _, _ ->
                val key = "cat_${category.id}"
                viewModel.deleteCategory(key)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

