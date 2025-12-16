package com.ecommerce.adminapp.ui.brands

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.ecommerce.adminapp.R
import com.ecommerce.adminapp.data.model.Brand
import com.ecommerce.adminapp.databinding.DialogAddEditBrandBinding
import com.ecommerce.adminapp.databinding.FragmentBrandsBinding
import com.ecommerce.adminapp.ui.adapter.BrandAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class BrandsFragment : Fragment() {

    private var _binding: FragmentBrandsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BrandsViewModel by viewModels()
    private lateinit var brandAdapter: BrandAdapter

    private var currentDialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBrandsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupFab()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        brandAdapter = BrandAdapter(
            onEditClick = { brand ->
                showAddEditDialog(brand)
            },
            onDeleteClick = { brand ->
                showDeleteConfirmationDialog(brand)
            }
        )

        binding.brandsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = brandAdapter
        }
    }

    private fun setupFab() {
        binding.fabAddBrand.setOnClickListener {
            showAddEditDialog(null)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.brands.collect { brands ->
                brandAdapter.submitList(brands)

                if (brands.isEmpty()) {
                    binding.emptyStateLayout.visibility = View.VISIBLE
                    binding.brandsRecyclerView.visibility = View.GONE
                } else {
                    binding.emptyStateLayout.visibility = View.GONE
                    binding.brandsRecyclerView.visibility = View.VISIBLE
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

    private fun showAddEditDialog(brand: Brand?) {
        val dialogBinding = DialogAddEditBrandBinding.inflate(layoutInflater)
        val isEditing = brand != null

        dialogBinding.apply {
            dialogTitle.text = if (isEditing) "Edit Brand" else "Add Brand"

            // Pre-fill data if editing
            brand?.let {
                brandNameEditText.setText(it.name)
                imageUrlEditText.setText(it.picUrl)
                activeSwitch.isChecked = it.active

                // Load existing image
                if (it.picUrl.isNotEmpty()) {
                    Glide.with(requireContext())
                        .load(it.picUrl)
                        .placeholder(R.drawable.ic_placeholder)
                        .error(R.drawable.ic_placeholder)
                        .into(brandImagePreview)
                }
            }

            // Real-time image preview
            imageUrlEditText.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    val url = imageUrlEditText.text.toString()
                    if (url.isNotEmpty()) {
                        Glide.with(requireContext())
                            .load(url)
                            .placeholder(R.drawable.ic_placeholder)
                            .error(R.drawable.ic_placeholder)
                            .into(brandImagePreview)
                    }
                }
            }
        }

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogBinding.root)
            .setCancelable(true)
            .create()

        currentDialog = dialog

        dialogBinding.cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.saveButton.setOnClickListener {
            val name = dialogBinding.brandNameEditText.text.toString().trim()
            val imageUrl = dialogBinding.imageUrlEditText.text.toString().trim()
            val isActive = dialogBinding.activeSwitch.isChecked

            if (name.isEmpty()) {
                dialogBinding.brandNameLayout.error = "Brand name is required"
                return@setOnClickListener
            }

            if (imageUrl.isEmpty()) {
                dialogBinding.imageUrlLayout.error = "Image URL is required"
                return@setOnClickListener
            }

            val brandData = Brand(
                id = brand?.id ?: "",
                name = name,
                picUrl = imageUrl,
                active = isActive
            )

            viewLifecycleOwner.lifecycleScope.launch {
                val result = if (isEditing) {
                    viewModel.updateBrand(brandData)
                } else {
                    viewModel.addBrand(brandData)
                }

                result.onSuccess {
                    Toast.makeText(
                        requireContext(),
                        if (isEditing) "Brand updated" else "Brand added",
                        Toast.LENGTH_SHORT
                    ).show()
                    dialog.dismiss()
                }.onFailure { error ->
                    Toast.makeText(
                        requireContext(),
                        "Error: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        dialog.show()
    }

    private fun showDeleteConfirmationDialog(brand: Brand) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Brand")
            .setMessage("Are you sure you want to delete ${brand.name}?")
            .setPositiveButton("Delete") { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.deleteBrand(brand.id).onSuccess {
                        Toast.makeText(
                            requireContext(),
                            "Brand deleted",
                            Toast.LENGTH_SHORT
                        ).show()
                    }.onFailure { error ->
                        Toast.makeText(
                            requireContext(),
                            "Error: ${error.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        currentDialog?.dismiss()
        _binding = null
    }
}
