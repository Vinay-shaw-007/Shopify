package com.example.shopify.ui.productDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shopify.model.APIResponse
import com.example.shopify.repository.ProductRepository
import com.example.shopify.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(private val repository: ProductRepository): ViewModel(){

    // Mutable state flow for holding the product response
    val productResponseStateFlow: MutableStateFlow<NetworkResult<APIResponse>>
        get() = repository.productResponseStateFlow

    // Function to get product details from the repository
    fun getProductDetails() {
        viewModelScope.launch {
            repository.getProductDetails()
        }
    }
}