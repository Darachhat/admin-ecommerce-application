package com.ecommerce.adminapp.ui.products

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ecommerce.adminapp.R
import com.ecommerce.adminapp.databinding.FragmentProductsBinding
import com.ecommerce.adminapp.ui.adapter.ProductAdapter
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class ProductsFragment : Fragment() {
    
    private var _binding: FragmentProductsBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: ProductsViewModel by viewModels()
    private lateinit var adapter: ProductAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupFab()
        setupFilters()
        observeProducts()
    }
    
    private fun setupRecyclerView() {
        adapter = ProductAdapter(
            onEditClick = { product ->
                val bundle = Bundle().apply {
                    putString("productId", product.id)
                }
                findNavController().navigate(R.id.action_products_to_add_edit_product, bundle)
            },
            onDeleteClick = { product ->
                deleteProduct(product.id)
            }
        )
        
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@ProductsFragment.adapter
        }
    }
    
    private fun setupFab() {
        binding.fabAdd.setOnClickListener {
            findNavController().navigate(R.id.action_products_to_add_edit_product)
        }
    }
    
    private fun observeProducts() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.products.collect { products ->
                adapter.submitList(products)
                binding.progressBar.visibility = View.GONE
                
                if (products.isEmpty()) {
                    binding.emptyView.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                } else {
                    binding.emptyView.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setupFilters() {
        val brandNames = mutableListOf("All")
        val brandIds = mutableListOf("")
        val categoryNames = mutableListOf("All")
        val categoryIds = mutableListOf("")
        val ratingOptions = listOf("All", "≥ 4.0", "≥ 4.5", "≥ 4.8")
        val sortOptions = listOf("Rating", "Price ↑", "Price ↓", "Newest")

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.brands.collect { brands ->
                brandNames.clear(); brandIds.clear()
                brandNames.add("All"); brandIds.add("")
                brands.forEach { b -> brandNames.add(b.name); brandIds.add(b.id) }
                val adapterSpinner = android.widget.ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, brandNames)
                adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerBrand.adapter = adapterSpinner
            }
        }
        binding.spinnerBrand.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.selectedBrandId.value = brandIds.getOrNull(position) ?: ""
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.categories.collect { cats ->
                categoryNames.clear(); categoryIds.clear()
                categoryNames.add("All"); categoryIds.add("")
                cats.forEach { c -> categoryNames.add(c.name); categoryIds.add(c.id) }
                val adapterSpinner = android.widget.ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categoryNames)
                adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerCategory.adapter = adapterSpinner
            }
        }
        binding.spinnerCategory.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.selectedCategoryId.value = categoryIds.getOrNull(position) ?: ""
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }

        val ratingAdapter = android.widget.ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, ratingOptions)
        ratingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerRating.adapter = ratingAdapter
        binding.spinnerRating.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                val opt = ratingOptions[position]
                viewModel.minRating.value = when (opt) {
                    "≥ 4.0" -> 4.0
                    "≥ 4.5" -> 4.5
                    "≥ 4.8" -> 4.8
                    else -> 0.0
                }
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }

        val sortAdapter = android.widget.ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, listOf("Rating", "Price ↑", "Price ↓", "Newest"))
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSort.adapter = sortAdapter
        binding.spinnerSort.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.sortOption.value = listOf("Rating", "Price ↑", "Price ↓", "Newest")[position]
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }

        binding.searchInput.setOnEditorActionListener { v, a, e ->
            viewModel.searchQuery.value = v.text.toString().trim()
            false
        }
    }
    
    private fun deleteProduct(productId: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            val result = viewModel.deleteProduct(productId)
            if (result.isSuccess) {
                Snackbar.make(binding.root, "Product deleted", Snackbar.LENGTH_SHORT).show()
            } else {
                Snackbar.make(binding.root, "Failed to delete product", Snackbar.LENGTH_SHORT).show()
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
