package com.example.shopify.repository

import com.example.shopify.api.ProductAPI
import com.example.shopify.model.APIResponse
import com.example.shopify.utils.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class ProductRepository @Inject constructor(private val productAPI: ProductAPI) {

    // Mutable state flow for holding the product response
    private val _productResponseStateFlow = MutableStateFlow<NetworkResult<APIResponse>>(NetworkResult.Loading())
    val productResponseStateFlow: MutableStateFlow<NetworkResult<APIResponse>>
        get() = _productResponseStateFlow

    // Function to fetch product details from the API
    suspend fun getProductDetails() {
        try {
            // Make the API call
            val response = productAPI.getProductResponse()

            // Handle the API response
            if (response.isSuccessful && response.body() != null) {
                _productResponseStateFlow.emit(NetworkResult.Success(response.body()!!))
            } else if (response.errorBody() != null) {
                _productResponseStateFlow.emit(NetworkResult.Error("Something went wrong."))
            } else {
                _productResponseStateFlow.emit(NetworkResult.Error("Something went wrong."))
            }

        } catch (e: Exception) {
            _productResponseStateFlow.emit(NetworkResult.Error(e.message))
        }

    }
}