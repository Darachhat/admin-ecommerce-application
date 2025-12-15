package com.ecommerce.adminapp.ui.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecommerce.adminapp.data.model.Category
import com.ecommerce.adminapp.data.repository.CategoryRepository
import kotlinx.coroutines.flow. MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines. launch

class CategoriesViewModel :  ViewModel() {

    private val repository = CategoryRepository()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String? >(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getAllCategories().collect { categoryList ->
                    _categories. value = categoryList
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }

    suspend fun addCategory(category: Category): Result<String> {
        return repository.addCategory(category)
    }

    suspend fun updateCategory(category: Category): Result<Unit> {
        return repository.updateCategory(category)
    }

    suspend fun deleteCategory(categoryId: String): Result<Unit> {
        return repository.deleteCategory(categoryId)
    }
}