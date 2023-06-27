package com.example.shopify.model

data class APIResponse(
    val data: Data,
    val message: String,
    val status: Int
)