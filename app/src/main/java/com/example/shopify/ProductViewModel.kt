package com.example.shopify

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shopify.model.APIResponse
import com.example.shopify.repository.ProductRepository
import com.example.shopify.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(private val repository: ProductRepository): ViewModel(){

    val productResponseLiveData: LiveData<NetworkResult<APIResponse>>
        get() = repository.productResponseLiveData
    fun getProductDetails() {
        viewModelScope.launch {
            repository.getProductDetails()
        }
    }
}