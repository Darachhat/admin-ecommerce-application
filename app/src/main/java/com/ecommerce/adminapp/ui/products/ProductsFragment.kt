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
