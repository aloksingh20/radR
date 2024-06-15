package com.example.radr.presentation.scanner

import android.graphics.Bitmap
import com.example.radr.data.ProductDetail

data class SearchState (
    val query: Bitmap?=null,
    val productDetail: ProductDetail?=null,
    val isLoading: Boolean =false,
    val error: String?=""
)
