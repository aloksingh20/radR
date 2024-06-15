package com.example.radr.presentation.home

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.radr.data.ProductDetail
import com.example.radr.data.ProductRepositoryImpl
import com.example.radr.utils.Resource
import kotlinx.coroutines.launch

class ProductViewmodel : ViewModel() {

    private val repository = ProductRepositoryImpl()

    // Expose state using a backing property
    private val _state = mutableStateOf(HomeUiState())
    val state: HomeUiState
        get() = _state.value


    fun addProduct(productDetail: ProductDetail) {
        viewModelScope.launch {
            val addProduct = repository.addProduct(productDetail)
            _state.value = when (addProduct) {
                is Resource.Error -> {
                    _state.value.copy(error = addProduct.message.toString())
                }
                is Resource.Success -> {
                    _state.value.copy(data = _state.value.data!!.toMutableList().apply { add(productDetail) })
                }
            }
        }
    }

    fun getProducts() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val products = repository.getAllProducts()
            _state.value = when (products) {
                is Resource.Error -> {
                    _state.value.copy(isLoading = false, error = products.message.toString())
                }
                is Resource.Success -> {
                    Log.d("ProductViewModel", "getProducts: ${products.data}")
                    _state.value.copy(isLoading = false, data = products.data!!.toMutableList())
                }
            }
        }
    }

    fun deleteProduct(productId:String) {
        viewModelScope.launch {
            val deleteProduct = repository.deleteProduct(productId)
            _state.value = _state.value.copy(data = _state.value.data!!.toMutableList().apply {
                removeAll { it.productId == productId }
            })
        }
    }

    fun getProductDetail(productId: String) {

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val productDetail = repository.getProduct(productId)
            _state.value = when (productDetail) {
                is Resource.Error -> {
                    Log.d("ProductViewModel", "getProducts: ${productDetail.message}")
                    _state.value.copy(isLoading = false, error = productDetail.message.toString())
                }
                is Resource.Success -> {
                    Log.d("ProductViewModel", "getProducts: ${productDetail.data}")
                    _state.value.copy(isLoading = false, productDetail = productDetail.data!!)
                }
            }
        }
    }

}
