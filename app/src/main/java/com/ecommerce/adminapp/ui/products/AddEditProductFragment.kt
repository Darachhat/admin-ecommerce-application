package com.ecommerce.adminapp.ui.products

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ecommerce.adminapp.data.model.Product
import com.ecommerce.adminapp.databinding.FragmentAddEditProductBinding
import com.ecommerce.adminapp.ui.adapter.EditableTextAdapter
import com.ecommerce.adminapp.ui.adapter.ImagePreviewAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class AddEditProductFragment : Fragment() {
    
    private var _binding: FragmentAddEditProductBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: AddEditProductViewModel by viewModels()
    
    private val productId: String? by lazy {
        arguments?.getString("productId")
    }
    
    private lateinit var imagesAdapter: ImagePreviewAdapter
    private lateinit var sizesAdapter: EditableTextAdapter
    private lateinit var colorsAdapter: EditableTextAdapter
    
    private val imagesList = mutableListOf<String>()
    private val sizesList = mutableListOf<String>()
    private val colorsList = mutableListOf<String>()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditProductBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerViews()
        setupButtons()
        observeViewModel()
        
        // Load product if editing
        productId?.let { id ->
            binding.textTitle.text = "Edit Product"
            viewModel.loadProduct(id)
        } ?: run {
            binding.textTitle.text = "Add New Product"
        }
    }
    
    private fun setupRecyclerViews() {
        // Images RecyclerView
        imagesAdapter = ImagePreviewAdapter { url, position ->
            imagesList.removeAt(position)
            imagesAdapter.submitList(imagesList.toList())
        }
        binding.imagesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = imagesAdapter
        }
        
        // Sizes RecyclerView
        sizesAdapter = EditableTextAdapter { size, position ->
            sizesList.removeAt(position)
            sizesAdapter.submitList(sizesList.toList())
        }
        binding.sizesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = sizesAdapter
        }
        
        // Colors RecyclerView
        colorsAdapter = EditableTextAdapter { color, position ->
            colorsList.removeAt(position)
            colorsAdapter.submitList(colorsList.toList())
        }
        binding.colorsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = colorsAdapter
        }
        
        // Setup Category Dropdown
        setupCategoryDropdown()
    }
    
    private fun setupCategoryDropdown() {
        val categories = listOf(
            "Running Shoes",
            "Basketball",
            "Casual",
            "Training",
            "Soccer",
            "Hiking",
            "Skateboarding"
        )
        
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categories)
        binding.editCategory.setAdapter(adapter)
        
        // Allow clicking to show dropdown
        binding.editCategory.setOnClickListener {
            binding.editCategory.showDropDown()
        }
    }
    
    private fun setupButtons() {
        // Back Button
        binding.buttonBack.setOnClickListener {
            findNavController().navigateUp()
        }
        
        // Add Image URL Button
        binding.buttonAddImage.setOnClickListener {
            showAddImageDialog()
        }
        
        // Add Size Button
        binding.buttonAddSize.setOnClickListener {
            showAddTextDialog("Add Size", "Enter size (e.g., S, M, L, 8, 9, 10)") { size ->
                if (size.isNotBlank() && !sizesList.contains(size)) {
                    sizesList.add(size)
                    sizesAdapter.submitList(sizesList.toList())
                }
            }
        }
        
        // Add Color Button
        binding.buttonAddColor.setOnClickListener {
            showAddTextDialog("Add Color", "Enter color (e.g., Black, White, Red)") { color ->
                if (color.isNotBlank() && !colorsList.contains(color)) {
                    colorsList.add(color)
                    colorsAdapter.submitList(colorsList.toList())
                }
            }
        }
        
        // Cancel Button
        binding.buttonCancel.setOnClickListener {
            findNavController().navigateUp()
        }
        
        // Save Button
        binding.buttonSave.setOnClickListener {
            saveProduct()
        }
    }
    
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.product.collect { product ->
                product?.let {
                    populateFields(it)
                }
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                binding.formContainer.visibility = if (isLoading) View.GONE else View.VISIBLE
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.saveResult.collect { result ->
                result?.let {
                    if (it.isSuccess) {
                        Toast.makeText(context, "Product saved successfully", Toast.LENGTH_SHORT).show()
                        findNavController().navigateUp()
                    } else {
                        Toast.makeText(context, "Failed to save product: ${it.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                    }
                    viewModel.clearSaveResult()
                }
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.validationError.collect { error ->
                error?.let {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    viewModel.clearValidationError()
                }
            }
        }
    }
    
    private fun populateFields(product: Product) {
        binding.apply {
            editTitle.setText(product.title)
            editDescription.setText(product.description)
            editPrice.setText(product.price.toString())
            product.oldPrice?.let { editOldPrice.setText(it.toString()) }
            editRating.setText(product.rating.toString())
            editStock.setText(product.stock.toString())
            
            // Map category ID to category name
            val categoryName = when(product.categoryId) {
                "cat_running" -> "Running Shoes"
                "cat_basketball" -> "Basketball"
                "cat_casual" -> "Casual"
                "cat_training" -> "Training"
                "cat_soccer" -> "Soccer"
                "cat_hiking" -> "Hiking"
                "cat_skateboarding" -> "Skateboarding"
                else -> product.categoryId
            }
            editCategory.setText(categoryName, false)
            switchRecommended.isChecked = product.showRecommended
        }
        
        // Load images - check both picUrl and thumbnail
        imagesList.clear()
        if (product.picUrl.isNotEmpty()) {
            imagesList.addAll(product.picUrl)
        } else if (!product.thumbnail.isNullOrBlank()) {
            imagesList.add(product.thumbnail!!)
        }
        imagesAdapter.submitList(imagesList.toList())
        
        // Load sizes
        sizesList.clear()
        sizesList.addAll(product.size)
        sizesAdapter.submitList(sizesList.toList())
        
        // Load colors
        colorsList.clear()
        colorsList.addAll(product.color)
        colorsAdapter.submitList(colorsList.toList())
    }
    
    private fun saveProduct() {
        val title = binding.editTitle.text.toString().trim()
        val description = binding.editDescription.text.toString().trim()
        val priceStr = binding.editPrice.text.toString().trim()
        val oldPriceStr = binding.editOldPrice.text.toString().trim()
        val ratingStr = binding.editRating.text.toString().trim()
        val stockStr = binding.editStock.text.toString().trim()
        val categoryName = binding.editCategory.text.toString().trim()
        val showRecommended = binding.switchRecommended.isChecked
        
        // Convert category name to ID
        val categoryId = when(categoryName) {
            "Running Shoes" -> "cat_running"
            "Basketball" -> "cat_basketball"
            "Casual" -> "cat_casual"
            "Training" -> "cat_training"
            "Soccer" -> "cat_soccer"
            "Hiking" -> "cat_hiking"
            "Skateboarding" -> "cat_skateboarding"
            else -> categoryName.lowercase().replace(" ", "_")
        }
        
        val product = Product().apply {
            this.id = productId ?: ""
            this.title = title
            this.description = description
            this.picUrl = imagesList.toList()
            this.thumbnail = imagesList.firstOrNull()
            this.price = priceStr.toDoubleOrNull() ?: 0.0
            this.oldPrice = oldPriceStr.toDoubleOrNull()
            this.rating = ratingStr.toDoubleOrNull() ?: 0.0
            this.size = sizesList.toList()
            this.color = colorsList.toList()
            this.categoryId = categoryId
            this.showRecommended = showRecommended
            this.stock = stockStr.toIntOrNull() ?: 0
            this.active = true
            this.createdAt = System.currentTimeMillis()
        }
        
        viewModel.saveProduct(product, productId != null, productId)
    }
    
    private fun showAddImageDialog() {
        val input = TextInputEditText(requireContext())
        input.hint = "Enter image URL"
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add Product Image")
            .setView(input)
            .setPositiveButton("Add") { _, _ ->
                val url = input.text.toString().trim()
                if (url.isNotBlank() && !imagesList.contains(url)) {
                    imagesList.add(url)
                    imagesAdapter.submitList(imagesList.toList())
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun showAddTextDialog(title: String, hint: String, onAdd: (String) -> Unit) {
        val input = TextInputEditText(requireContext())
        input.hint = hint
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setView(input)
            .setPositiveButton("Add") { _, _ ->
                val text = input.text.toString().trim()
                onAdd(text)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
