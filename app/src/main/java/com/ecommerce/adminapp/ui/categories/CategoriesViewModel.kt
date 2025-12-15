package com.ecommerce.adminapp.ui.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecommerce.adminapp.data.model.Category
import com.ecommerce.adminapp.data.repository.CategoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CategoriesViewModel : ViewModel() {
    
    private val repository = CategoryRepository()
    
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _saveResult = MutableStateFlow<Result<Unit>?>(null)
    val saveResult: StateFlow<Result<Unit>?> = _saveResult.asStateFlow()
    
    private val _deleteResult = MutableStateFlow<Result<Unit>?>(null)
    val deleteResult: StateFlow<Result<Unit>?> = _deleteResult.asStateFlow()
    
    init {
        loadCategories()
    }
    
    private fun loadCategories() {
        viewModelScope.launch {
            repository.getAllCategories().collect { categoryList ->
                _categories.value = categoryList.sortedBy { it.id }
            }
        }
    }
    
    fun addCategory(category: Category) {
        viewModelScope.launch {
            _isLoading.value = true
            val key = "cat_${category.id}"
            val result = repository.addCategory(key, category)
            _saveResult.value = result
            _isLoading.value = false
        }
    }
    
    fun updateCategory(key: String, category: Category) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.updateCategory(key, category)
            _saveResult.value = result
            _isLoading.value = false
        }
    }
    
    fun deleteCategory(key: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.deleteCategory(key)
            _deleteResult.value = result
            _isLoading.value = false
        }
    }
    
    fun clearSaveResult() {
        _saveResult.value = null
    }
    
    fun clearDeleteResult() {
        _deleteResult.value = null
    }
    
    fun validateCategory(id: Int, title: String, picUrl: String): String? {
        return when {
            id <= 0 -> "Category ID must be greater than 0"
            title.isBlank() -> "Title cannot be empty"
            picUrl.isBlank() -> "Image URL cannot be empty"
            else -> null
        }
    }
}
