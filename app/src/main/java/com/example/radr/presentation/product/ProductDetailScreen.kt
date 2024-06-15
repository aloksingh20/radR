package com.example.radr.presentation.product

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.radr.presentation.home.ProductViewmodel

@Composable
fun ProductDetailScreen(navController: NavHostController, viewModel: ProductViewmodel, productId: String) {

    val state = viewModel.state

    LaunchedEffect(productId) {
        viewModel.getProductDetail(productId)
    }

    Scaffold(
        topBar = {
            Text(text = "Product Details", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp))
        }
    ) {
        Surface(modifier = Modifier
            .fillMaxSize()
            .padding(it)) {
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(top = 50.dp)) {
                when {
                    state.isLoading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }

                    state.error.isNotBlank() -> {
                        Text(text = state.error.toString(), color = MaterialTheme.colorScheme.error)
                    }

                    state.productDetail != null -> {
                        Card(modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .align(Alignment.TopCenter)) {
                            Column(modifier = Modifier
                                .padding(16.dp)
                                .verticalScroll(enabled = true, state = rememberScrollState())) {
                                AsyncImage(
                                    model = state.productDetail.productImageUrl,
                                    contentDescription = "product image",
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .width(200.dp)
                                        .height(350.dp)
                                        .align(Alignment.CenterHorizontally)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = (state.productDetail.productName).trim(),
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Description: ", fontWeight = FontWeight.Bold)
                                Text(text = state.productDetail.productDescription.trim())
                                Spacer(modifier = Modifier.height(8.dp))
                                Row {
                                    Text(text = "Color: ", fontWeight = FontWeight.Bold)
                                    Text(text = state.productDetail.productColor)
                                }
                                Spacer(modifier = Modifier.height(16.dp))

                            }
                        }
                        Button(onClick = {
                            navController.popBackStack()
                            navController.navigate("home")
                        }, modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 50.dp)
                            .width(100.dp)) {
                            Text(text = "Ok", modifier = Modifier.align(Alignment.CenterVertically))
                        }
                    }

                }
            }
        }
    }
}