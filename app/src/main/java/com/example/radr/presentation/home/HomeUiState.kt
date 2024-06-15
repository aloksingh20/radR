package com.example.radr.presentation.home

import com.example.radr.data.ProductDetail

data class HomeUiState (
    val isLoading: Boolean = false,
    val error: String = "",
    val data: MutableList<ProductDetail>? = mutableListOf(),
    val productDetail: ProductDetail? = null
)