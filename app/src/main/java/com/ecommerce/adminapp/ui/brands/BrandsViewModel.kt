package com.ecommerce.adminapp.ui.brands

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecommerce.adminapp.data.model.Brand
import com.ecommerce.adminapp.data.repository.BrandRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BrandsViewModel : ViewModel() {

    private val repository = BrandRepository()

    private val _brands = MutableStateFlow<List<Brand>>(emptyList())
    val brands: StateFlow<List<Brand>> = _brands.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadBrands()
    }

    private fun loadBrands() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getAllBrands().collect { brandList ->
                    _brands.value = brandList
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }

    suspend fun addBrand(brand: Brand): Result<String> {
        return repository.addBrand(brand)
    }

    suspend fun updateBrand(brand: Brand): Result<Unit> {
        return repository.updateBrand(brand)
    }

    suspend fun deleteBrand(brandId: String): Result<Unit> {
        return repository.deleteBrand(brandId)
    }
}
