package com.example.radr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.radr.presentation.home.HomeScreen
import com.example.radr.presentation.product.ProductDetailScreen
import com.example.radr.presentation.home.ProductViewmodel
import com.example.radr.presentation.scanner.SearchViewModel
import com.example.radr.presentation.scanner.ScannerScreen
import com.example.radr.ui.theme.RadRTheme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContent {
            RadRTheme {
                val navController = rememberNavController()
                val searchViewModel = SearchViewModel()
                val productViewModel = ProductViewmodel()

                Surface {
                    NavHost(navController = navController, startDestination = "home") {
                        composable("home") {
                            HomeScreen(
                                viewmodel = productViewModel,
                                navController = navController
                            )
                        }
                        composable("scanner") {
                            ScannerScreen(
                                navController = navController,
                                viewModel = searchViewModel
                            )
                        }
                        composable(
                            "productDetail/{productId}",
                            arguments = listOf(navArgument("productId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val productId = backStackEntry.arguments?.getString("productId")
                            val viewmodel = ProductViewmodel()
                            ProductDetailScreen(
                                productId = productId!!,
                                navController = navController,
                                viewModel = productViewModel
                            )
                        }
                    }
                }
            }
        }
    }
}
