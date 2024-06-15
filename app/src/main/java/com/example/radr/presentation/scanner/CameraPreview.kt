package com.example.radr.presentation.scanner

import android.annotation.SuppressLint
import android.view.MotionEvent
import androidx.camera.core.FocusMeteringAction
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("ClickableViewAccessibility")
@Composable
fun CameraPreview(
    controller: LifecycleCameraController,
    modifier: Modifier = Modifier
) {
    val lifeCycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    AndroidView(factory = {
        PreviewView(it).apply {
            this.controller=controller
            controller.bindToLifecycle(lifeCycleOwner)
            // Set up touch-to-focus

        }
    },
        modifier=modifier
    )
}