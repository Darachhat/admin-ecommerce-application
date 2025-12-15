package com.ecommerce.adminapp.ui.banners

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecommerce.adminapp.data.model.Banner
import com.ecommerce.adminapp.data.repository.BannerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class BannersViewModel : ViewModel() {

    private val repository = BannerRepository()

    private val _banners = MutableStateFlow<List<Banner>>(emptyList())
    val banners: StateFlow<List<Banner>> = _banners.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        fetchBanners()
    }

    private fun fetchBanners() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getAllBanners()
                .catch { e ->
                    _error.value = "Error fetching banners: ${e.message}"
                }
                .collect { bannerList ->
                    _banners.value = bannerList
                    _isLoading.value = false
                }
        }
    }

    fun addBanner(banner: Banner): kotlinx.coroutines.flow.Flow<Result<String>> = kotlinx.coroutines.flow.flow {
        _isLoading.value = true
        val result = repository.addBanner(banner)
        emit(result)
        _isLoading.value = false
    }

    fun updateBanner(banner: Banner): kotlinx.coroutines.flow.Flow<Result<Unit>> = kotlinx.coroutines.flow.flow {
        _isLoading.value = true
        val result = repository.updateBanner(banner)
        emit(result)
        _isLoading.value = false
    }

    fun deleteBanner(bannerId: String): kotlinx.coroutines.flow.Flow<Result<Unit>> = kotlinx.coroutines.flow.flow {
        _isLoading.value = true
        val result = repository.deleteBanner(bannerId)
        emit(result)
        _isLoading.value = false
    }
}