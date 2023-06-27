package com.example.shopify.api

import com.example.shopify.model.APIResponse
import retrofit2.Response
import retrofit2.http.GET

interface ProductAPI {

    @GET("productdetails/6701/253620?lang=en&store=KWD/")
    suspend fun getProductResponse(): Response<APIResponse>
}