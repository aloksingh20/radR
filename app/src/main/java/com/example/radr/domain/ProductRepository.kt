package com.example.radr.domain

import android.net.Uri
import com.example.radr.data.ProductDetail
import com.example.radr.utils.Resource

interface ProductRepository {

    suspend fun addProduct(productDetail: ProductDetail):Resource<String>
    suspend fun getProduct(productId: String): Resource<ProductDetail?>
    suspend fun deleteProduct(productId: String)
    suspend fun getAllProducts(): Resource<List<ProductDetail>>
    suspend fun uploadProductImage(imageUri: Uri): Resource<String>

}