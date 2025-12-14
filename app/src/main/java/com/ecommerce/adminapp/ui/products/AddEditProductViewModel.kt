package com.ecommerce.adminapp.ui.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecommerce.adminapp.data.model.Product
import com.ecommerce.adminapp.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddEditProductViewModel : ViewModel() {
    
    private val repository = ProductRepository()
    
    private val _product = MutableStateFlow<Product?>(null)
    val product: StateFlow<Product?> = _product.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _saveResult = MutableStateFlow<Result<String>?>(null)
    val saveResult: StateFlow<Result<String>?> = _saveResult.asStateFlow()
    
    private val _validationError = MutableStateFlow<String?>(null)
    val validationError: StateFlow<String?> = _validationError.asStateFlow()
    
    fun loadProduct(productId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val product = repository.getProductById(productId)
            _product.value = product
            _isLoading.value = false
        }
    }
    
    fun saveProduct(product: Product, isEdit: Boolean, editId: String? = null) {
        if (!validateProduct(product)) {
            return
        }
        
        viewModelScope.launch {
            _isLoading.value = true
            _saveResult.value = null
            
            val result = if (isEdit && editId != null) {
                repository.updateProduct(editId, product)
                    .map { editId }
            } else {
                repository.addProduct(product)
            }
            
            _saveResult.value = result
            _isLoading.value = false
        }
    }
    
    private fun validateProduct(product: Product): Boolean {
        return when {
            product.title.isBlank() -> {
                _validationError.value = "Product title is required"
                false
            }
            product.description.isBlank() -> {
                _validationError.value = "Product description is required"
                false
            }
            product.price <= 0 -> {
                _validationError.value = "Product price must be greater than 0"
                false
            }
            product.picUrl.isEmpty() -> {
                _validationError.value = "At least one product image is required"
                false
            }
            else -> {
                _validationError.value = null
                true
            }
        }
    }
    
    fun clearValidationError() {
        _validationError.value = null
    }
    
    fun clearSaveResult() {
        _saveResult.value = null
    }
}
