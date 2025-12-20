package com.ecommerce.adminapp.ui.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecommerce.adminapp.data.model.Product
import com.ecommerce.adminapp.data.model.Brand
import com.ecommerce.adminapp.data.model.Category
import com.ecommerce.adminapp.data.repository.ProductRepository
import com.ecommerce.adminapp.data.repository.BrandRepository
import com.ecommerce.adminapp.data.repository.CategoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class ProductsViewModel : ViewModel() {
    
    private val repository = ProductRepository()
    private val brandRepository = BrandRepository()
    private val categoryRepository = CategoryRepository()
    
    private val _allProducts = MutableStateFlow<List<Product>>(emptyList())
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _brands = MutableStateFlow<List<Brand>>(emptyList())
    val brands: StateFlow<List<Brand>> = _brands.asStateFlow()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    val searchQuery = MutableStateFlow("")
    val selectedBrandId = MutableStateFlow("")
    val selectedCategoryId = MutableStateFlow("")
    val minRating = MutableStateFlow(0.0)
    val sortOption = MutableStateFlow("Rating")
    
    init {
        loadProducts()
        loadMeta()
        viewModelScope.launch {
            val filteredFlow = combine(_allProducts, searchQuery, selectedBrandId, selectedCategoryId, minRating) { list, q, bid, cid, minR ->
                var seq = list.asSequence()
                if (bid.isNotEmpty()) seq = seq.filter { it.brandId == bid }
                if (cid.isNotEmpty()) seq = seq.filter { it.categoryId == cid }
                if (q.isNotEmpty()) seq = seq.filter { it.title.contains(q, ignoreCase = true) }
                if (minR > 0.0) seq = seq.filter { it.rating >= minR }
                seq.toMutableList()
            }
            combine(filteredFlow, sortOption) { filtered, sort ->
                val res = filtered.toMutableList()
                when (sort) {
                    "Price ↑" -> res.sortBy { it.price }
                    "Price ↓" -> res.sortByDescending { it.price }
                    "Newest" -> res.sortByDescending { it.createdAt }
                    else -> res.sortByDescending { it.rating }
                }
                res
            }.collect { sorted ->
                _products.value = sorted
            }
        }
    }
    
    private fun loadProducts() {
        viewModelScope.launch {
            repository.getAllProducts().collect { productList ->
                _allProducts.value = productList
            }
        }
    }

    private fun loadMeta() {
        viewModelScope.launch {
            brandRepository.getAllBrands().collect { _brands.value = it }
        }
        viewModelScope.launch {
            categoryRepository.getAllCategories().collect { _categories.value = it }
        }
    }
    
    suspend fun deleteProduct(productId: String): Result<Unit> {
        return repository.deleteProduct(productId)
    }
}
