package com.example.radr.data

import android.net.Uri
import android.util.Log
import com.example.radr.domain.ProductRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.example.radr.utils.Resource
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class ProductRepositoryImpl : ProductRepository {

    private val db = FirebaseFirestore.getInstance()
    private val firebaseStorage = FirebaseStorage.getInstance()
    private val database = FirebaseDatabase.getInstance().reference


    override suspend fun addProduct(productDetail: ProductDetail): Resource<String> {
        return try {
            val newProductRef = database.child("products").push()
            val updatedProductDetail = productDetail.copy(productId = newProductRef.key!!)
            newProductRef.setValue(updatedProductDetail).await()
            Resource.Success(newProductRef.key!!) // You can return Unit here if the ID is already accessible
        } catch (e: Exception) {
            Log.e("ProductRepository", "Error adding product", e)
            Resource.Error(e.message.toString())
        }
    }

    override suspend fun getProduct(productId: String): Resource<ProductDetail?> {
        return try {
            val dbSnapshot = database.child("products").child(productId).get().await()

            // Check if snapshot exists
            val productDetail = convertDataSnapshotToProductDetail(dbSnapshot)

            Resource.Success(productDetail)
        } catch (e: Exception) {
            Resource.Error("Could not load the product: ${e.message}")
        }
    }


    override suspend fun deleteProduct(productId: String) {
        database.child("products").child(productId).removeValue()

    }

    override suspend fun getAllProducts(): Resource<List<ProductDetail>> {
        return try {
            val collectionRef = database.child("products")
            val snapshot = collectionRef.get().await()

            if (snapshot.exists()) {
                val products = snapshot.children.map { dataSnapshot ->
                    Log.d("ProductRepository", dataSnapshot.toString())
                    convertDataSnapshotToProductDetail(dataSnapshot)
                }
                Log.d("ProductRepository", "Data exists")
                Resource.Success(products)
            } else {
                Log.d("ProductRepository", "Data does not exist")
                Resource.Error("Data does not exist")
            }
        } catch (e: Exception) {
            Log.e("ProductRepository", "Error fetching data", e)
            Resource.Error(e.message.toString())
        }
    }

    override suspend fun uploadProductImage(imageUri: Uri): Resource<String> {
        val reference = firebaseStorage.reference.child("images/${UUID.randomUUID()}")
        return try {
            // Upload file to Firebase Storage
            val uploadTask = reference.putFile(imageUri)
            uploadTask.await() // Wait for the upload to complete

            // Get download URL
            val downloadUrl = reference.downloadUrl.await().toString()

            // Return success with the download URL
            Resource.Success(downloadUrl)
        } catch (e: Exception) {
            Resource.Error(e.message.toString())
        }
    }

    private fun convertDataSnapshotToProductDetail(dataSnapshot: DataSnapshot): ProductDetail {
        val productMap = dataSnapshot.value as HashMap<*, *>
        val productDetail = ProductDetail(
            productId = productMap["productId"] as String?,
            productName = productMap["productName"] as String,
            productDescription = productMap["productDescription"] as String,
            productColor = productMap["productColor"] as String,
            productImageUrl = productMap["productImageUrl"] as String
        )
        return productDetail
    }

}