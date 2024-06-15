// Import required Compose libraries
package com.example.radr.presentation.scanner


import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture.OnImageCapturedCallback
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController


// Modify the CameraPreview composable to accept a NavController parameter
@Composable
fun ScannerScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    var hasCamPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCamPermission = granted
        }
    )
    LaunchedEffect(key1 = true) {
        launcher.launch(Manifest.permission.CAMERA)
    }

    var hasImage by remember { mutableStateOf(false) }
    var imageBitmap: Bitmap? by remember { mutableStateOf(null) }
    val searchState = viewModel.searchState

    // State to track flashlight status
    var isFlashOn by remember { mutableStateOf(false) }

    val controller = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(
                CameraController.IMAGE_CAPTURE or CameraController.VIDEO_CAPTURE
            )
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        if (hasCamPermission) {
            if (!hasImage) {
                CameraPreview(controller = controller, modifier = Modifier.fillMaxSize())
                var isProcessing by remember {
                    mutableStateOf(false)
                }
                IconButton(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(20.dp)
                        .size(32.dp),
                    onClick = {
                        isProcessing = true
                        controller.takePicture(
                            ContextCompat.getMainExecutor(context),
                            object : OnImageCapturedCallback() {
                                override fun onCaptureSuccess(image: ImageProxy) {
                                    super.onCaptureSuccess(image)
                                    val matrix = Matrix().apply {
                                        postRotate(image.imageInfo.rotationDegrees.toFloat())
                                    }
                                    val rotatedBitmap = Bitmap.createBitmap(
                                        image.toBitmap(),
                                        0,
                                        0,
                                        image.width,
                                        image.height,
                                        matrix,
                                        true
                                    )

                                    imageBitmap = rotatedBitmap
                                    hasImage = true
                                    isProcessing = false
                                }

                                override fun onError(exception: ImageCaptureException) {
                                    super.onError(exception)
                                    Log.e("Camera", "Couldn't take photo", exception)
                                }
                            }
                        )

                    }) {
                    if (!isProcessing) {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = "Take Picture",
                            tint = Color.White
                        )
                    }
                }

                if (isProcessing) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(20.dp)
                            .size(32.dp)
                    )
                }

                IconButton(
                    onClick = {
                        controller.cameraSelector =
                            if (controller.cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA) {
                                CameraSelector.DEFAULT_BACK_CAMERA
                            } else {
                                CameraSelector.DEFAULT_FRONT_CAMERA
                            }
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 40.dp, end = 40.dp)
                ) {
                    Icon(
                        Icons.Default.Cameraswitch,
                        contentDescription = "Switch camera",
                        tint = Color.White
                    )
                }

                // Toggle flashlight button
                IconButton(
                    onClick = {
                        isFlashOn = !isFlashOn
                        controller.enableTorch(isFlashOn)
                    },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(top = 40.dp, start = 40.dp)
                ) {
                    Icon(
                        imageVector = if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                        contentDescription = "Toggle Flashlight",
                        tint = Color.White
                    )
                }
            }
        }

        if (hasImage) {
            Image(
                bitmap = imageBitmap?.asImageBitmap()!!,
                contentDescription = "Captured Image",
                modifier = Modifier.fillMaxSize()
            )
            if (!searchState.isLoading) {
                IconButton(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(20.dp),
                    onClick = {
                        hasImage = false
                        imageBitmap = null
                    }
                ) {
                    Icon(Icons.Outlined.Clear, contentDescription = "Clear image")
                }
            }
            IconButton(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(20.dp),
                onClick = {
                    viewModel.updateQuery(imageBitmap!!)
                    viewModel.search()
                }
            ) {
                if (searchState.isLoading) {
                    CircularProgressIndicator()
                } else {
                    Icon(Icons.Outlined.Search, contentDescription = "Search image")
                }
            }
        }

            if (searchState.productDetail != null) {
                val currProduct =searchState.productDetail
                hasImage = false
                imageBitmap = null
                viewModel.updateSearchToDefault()
                navController.popBackStack()
                navController.navigate("productDetail/${currProduct.productId}")
            }
        }

}
