package com.ecommerce.adminapp.ui.categories

import android.app.Dialog
import android.net.Uri
import android.os. Bundle
import android.view. LayoutInflater
import android.view.View
import android.view. ViewGroup
import android.widget. Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.ecommerce.adminapp.R
import com.ecommerce.adminapp. data.model.Category
import com.ecommerce.adminapp. databinding.DialogAddEditCategoryBinding
import com.ecommerce.adminapp. databinding.FragmentCategoriesBinding
import com.ecommerce.adminapp.ui.adapter. CategoryAdapter
import com.ecommerce.adminapp.utils.ImageUploadHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class CategoriesFragment : Fragment() {

    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CategoriesViewModel by viewModels()
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var imageUploadHelper: ImageUploadHelper

    private var selectedImageUri: Uri?  = null
    private var currentEditingCategory: Category? = null

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            selectedImageUri = it
            currentDialog?.let { dialog ->
                val dialogBinding = DialogAddEditCategoryBinding. bind(dialog.window!!.decorView)
                Glide.with(this)
                    .load(uri)
                    .into(dialogBinding.categoryImagePreview)
            }
        }
    }

    private var currentDialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        return binding. root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageUploadHelper = ImageUploadHelper(requireContext())

        setupRecyclerView()
        setupFab()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        categoryAdapter = CategoryAdapter(
            onEditClick = { category ->
                showAddEditDialog(category)
            },
            onDeleteClick = { category ->
                showDeleteConfirmationDialog(category)
            }
        )

        binding.categoriesRecyclerView.apply {
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

                if (categories.isEmpty()) {
                    binding.emptyStateLayout. visibility = View.VISIBLE
                    binding.categoriesRecyclerView.visibility = View.GONE
                } else {
                    binding.emptyStateLayout.visibility = View. GONE
                    binding.categoriesRecyclerView.visibility = View.VISIBLE
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.error.collect { error ->
                error?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showAddEditDialog(category: Category?) {
        val dialogBinding = DialogAddEditCategoryBinding.inflate(layoutInflater)
        currentEditingCategory = category
        selectedImageUri = null

        // Set dialog title
        dialogBinding.dialogTitle.text = if (category == null) "Add Category" else "Edit Category"

        // Pre-fill data if editing
        category?.let {
            dialogBinding.nameEditText.setText(it.name)
            dialogBinding.imageUrlEditText.setText(it.picUrl)
            dialogBinding.activeSwitch.isChecked = it.active

            // Load existing image
            Glide.with(this)
                .load(it.picUrl)
                .placeholder(R.drawable.ic_placeholder)
                .into(dialogBinding.categoryImagePreview)
        }

        // Setup image URL change listener
        dialogBinding.imageUrlEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val imageUrl = dialogBinding.imageUrlEditText.text.toString().trim()
                if (imageUrl.isNotEmpty()) {
                    Glide.with(this)
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_placeholder)
                        .into(dialogBinding.categoryImagePreview)
                }
            }
        }

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogBinding.root)
            .setCancelable(false)
            .create()

        currentDialog = dialog

        dialogBinding.cancelButton.setOnClickListener {
            dialog.dismiss()
            currentDialog = null
        }

        dialogBinding.saveButton.setOnClickListener {
            val name = dialogBinding.nameEditText.text.toString().trim()
            val imageUrl = dialogBinding.imageUrlEditText.text.toString().trim()
            val isActive = dialogBinding.activeSwitch.isChecked

            if (name.isEmpty()) {
                dialogBinding.nameInputLayout.error = "Category name is required"
                return@setOnClickListener
            }

            if (imageUrl.isEmpty()) {
                dialogBinding.imageUrlInputLayout.error = "Image URL is required"
                return@setOnClickListener
            }

            dialogBinding.nameInputLayout.error = null
            dialogBinding.imageUrlInputLayout.error = null
            dialogBinding.progressIndicator.visibility = View.VISIBLE
            dialogBinding.saveButton.isEnabled = false

            lifecycleScope.launch {
                try {

                    val categoryData = Category(
                        id = category?.id ?: "",
                        name = name,
                        picUrl = imageUrl,
                        active = isActive
                    )

                    val result = if (category == null) {
                        viewModel.addCategory(categoryData)
                    } else {
                        viewModel.updateCategory(categoryData)
                    }

                    if (result.isSuccess) {
                        Toast.makeText(
                            requireContext(),
                            if (category == null) "Category added successfully" else "Category updated successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        dialog.dismiss()
                        currentDialog = null
                    } else {
                        throw result.exceptionOrNull() ?: Exception("Failed to save category")
                    }

                } catch (e: Exception) {
                    Toast.makeText(
                        requireContext(),
                        "Error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                } finally {
                    dialogBinding.progressIndicator.visibility = View.GONE
                    dialogBinding.saveButton.isEnabled = true
                }
            }
        }

        dialog.show()
    }

    private fun showDeleteConfirmationDialog(category:  Category) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Category")
            .setMessage("Are you sure you want to delete \"${category.name}\"?")
            .setPositiveButton("Delete") { _, _ ->
                deleteCategory(category)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteCategory(category: Category) {
        lifecycleScope.launch {
            binding.progressIndicator.visibility = View.VISIBLE

            try {
                val result = viewModel.deleteCategory(category.id)

                if (result.isSuccess) {
                    Toast.makeText(
                        requireContext(),
                        "Category deleted successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Delete image from storage if exists
                    if (category.picUrl.isNotEmpty()) {
                        imageUploadHelper.deleteImage(category.picUrl)
                    }
                } else {
                    throw result. exceptionOrNull() ?: Exception("Failed to delete category")
                }

            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Error: ${e. message}",
                    Toast. LENGTH_SHORT
                ).show()
            } finally {
                binding.progressIndicator.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        currentDialog?. dismiss()
        currentDialog = null
        _binding = null
    }
}