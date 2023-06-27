package com.example.shopify.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.shopify.api.ProductAPI
import com.example.shopify.model.APIResponse
import com.example.shopify.utils.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class ProductRepository @Inject constructor(private val productAPI: ProductAPI) {

    private val _productResponseLiveData = MutableStateFlow<NetworkResult<APIResponse>>(NetworkResult.Loading())
    val productResponseLiveData: MutableStateFlow<NetworkResult<APIResponse>>
        get() = _productResponseLiveData

    suspend fun getProductDetails() {
        try {
            val response = productAPI.getProductResponse()
            if (response.isSuccessful && response.body() != null) {
                _productResponseLiveData.emit(NetworkResult.Success(response.body()!!))
            } else if (response.errorBody() != null) {
                _productResponseLiveData.emit(NetworkResult.Error("Something went wrong."))
            } else {
                _productResponseLiveData.emit(NetworkResult.Error("Something went wrong."))
            }

        } catch (e: Exception) {
            _productResponseLiveData.emit(NetworkResult.Error(e.message))
        }

    }
}