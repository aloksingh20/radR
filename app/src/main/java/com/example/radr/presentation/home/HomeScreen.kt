package com.example.radr.presentation.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.radr.data.ProductDetail
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun  HomeScreen(
    viewmodel: ProductViewmodel,
    navController: NavController
) {

    val productState = viewmodel.state
    LaunchedEffect(key1 = true){
        viewmodel.getProducts()
    }
    Scaffold(modifier = Modifier.fillMaxSize(),
        topBar = {

            TopAppBar(title = {
                Text(text = "radR", fontWeight = FontWeight.Bold)
            },
                actions = {
                    IconButton(
                        onClick = {
                            navController.navigate("scanner")
                        },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = "Qr scanner"
                        )
                    }
                }
            )
        }
    ) {
        Box(modifier = Modifier
            .padding(it)
            .fillMaxSize()){

            Column (modifier = Modifier.align(Alignment.Center)){


                if(productState.isLoading){
                    CircularProgressIndicator()
                }

            }
            Column {
                if(productState.data!!.isNotEmpty()){
                    Text(text = "Product List", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 10.dp, start = 10.dp, bottom = 10.dp))

                    LazyColumn(modifier = Modifier.padding(10.dp)){


                        items(productState.data){
                            val showDeleteDialog = remember { mutableStateOf(false) }

                            val delete = SwipeAction(
                                icon = { Icon(Icons.Default.Delete, contentDescription = null , modifier = Modifier.size(52.dp).padding(start = 15.dp)) },
                                background = MaterialTheme.colorScheme.error,
                                isUndo = true,
                                onSwipe = {
                                    showDeleteDialog.value = true
                                }
                            )
                            SwipeableActionsBox (
                                endActions = listOf(delete)
                            ){
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 10.dp)
                                ){
                                    CardItems(productDetail = it, modifier = Modifier,
                                        onClick = {
                                        navController.navigate("productDetail/${it.productId}")
                                    })

                                }
                            }
                            if (showDeleteDialog.value) {
                                DeleteAlertDialog(
                                    onDismissRequest = { showDeleteDialog.value = false },
                                    onConfirmDelete = {

                                        viewmodel.deleteProduct(it.productId!!)
                                        showDeleteDialog.value = false

                                    }
                                )
                            }

                        }
                    }

                }else{
                    if(!productState.isLoading){
                        if(productState.error.isNotEmpty()){
                            Text(text = productState.error)
                        }else{
                            Text(text = "No Data")

                        }
                    }
                }
            }

        }
    }
}

@Composable
fun CardItems(productDetail: ProductDetail, modifier: Modifier=Modifier, onClick: () -> Unit){
    Card(modifier = Modifier.padding(5.dp), onClick = {
        onClick()
    }) {
        Row(modifier=Modifier.padding(10.dp)) {
            AsyncImage(
                model = productDetail.productImageUrl,
                contentDescription = "product image",
                modifier = Modifier.size(74.dp).clip(RoundedCornerShape(16.dp)).padding(5.dp),
                contentScale = ContentScale.Fit
            )
            Column (modifier=Modifier.padding(start = 5.dp)){
                Text(text = productDetail.productName)
                Text(text = productDetail.productDescription, maxLines = 1, softWrap = true, overflow = TextOverflow.Ellipsis )
            }
        }
    }
}
@Composable
fun DeleteAlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmDelete: () -> Unit,
){

    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        confirmButton = {
            TextButton(onClick = {
                onConfirmDelete()
            }) {
                Text(text = "Yes")
            }
        }, dismissButton = {
            TextButton(onClick = { onDismissRequest() }) {
                Text(text = "No")
            }
        },
        title = { Text("Delete Item?") },
        text = { Text("Are you sure you want to delete this item?") }
    )

}
