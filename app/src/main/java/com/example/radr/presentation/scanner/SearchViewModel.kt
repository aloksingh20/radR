package com.example.radr.presentation.scanner

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.radr.data.ProductDetail
import com.example.radr.data.ProductRepositoryImpl
import com.example.radr.data.SearchRepositoryImpl
import com.example.radr.utils.Resource
import com.example.radr.utils.SearchPrompt
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class SearchViewModel:ViewModel() {

    var searchState by mutableStateOf(SearchState())
    val searchRepository = SearchRepositoryImpl()
    val productRepository = ProductRepositoryImpl()


    fun updateQuery(query:Bitmap){
        searchState=searchState.copy(query = query)
    }

    fun search(){
        viewModelScope.launch {
            var productName = ""
            var productDescription = ""
            var productColor = ""
            searchState=searchState.copy(isLoading = true)

            if(searchState.query!=null){
                productName = try {
                    searchRepository.search(SearchPrompt.PRODUCT_NAME_PROMPT, searchState.query!!).data?:""
                }catch (e:Exception){
                    ""
                }
                productDescription = try {
                    searchRepository.search(SearchPrompt.PRODUCT_DESCRIPTION_PROMPT, searchState.query!!).data?:""
                }catch (e:Exception){
                    ""
                }
                productColor = try {
                    searchRepository.search(SearchPrompt.PRODUCT_COLOR_PROMPT, searchState.query!!).data?:""
                }catch (e:Exception){
                    ""
                }

                if(productName.isNotEmpty() && productDescription.isNotEmpty() && productColor.isNotEmpty() ){

                    val uploadProductImage =
                        productRepository.uploadProductImage(getImageUri(searchState.query!!))
                    val productDetail =ProductDetail("",productName,productDescription,productColor,uploadProductImage.data!!)
                    Log.d("productDetail",productDetail.toString())
                    val addProduct = productRepository.addProduct(productDetail)
                    searchState = when(addProduct){
                        is Resource.Success->{
                            productDetail.productId = addProduct.data!!
                            searchState.copy(isLoading = false,query = null, productDetail = productDetail)

                        }
                        is Resource.Error->{
                            searchState.copy(isLoading = false,query = null)
                        }
                    }
                }
                else{
                    searchState=searchState.copy(isLoading = false)
                }
                searchState=searchState.copy(isLoading = false,query = null)
            }

        }
    }

    private fun getImageUri(imageBitmap: Bitmap): Uri {
        val bitmap = imageBitmap
        val tempFile = File.createTempFile("image", ".jpg")  // Replace ".jpg" with desired format
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, FileOutputStream(tempFile))
        val imageUri = Uri.fromFile(tempFile)
        return imageUri
    }


    fun updateSearchToDefault() {
        searchState = searchState.copy(query = null, productDetail = null)
    }


}