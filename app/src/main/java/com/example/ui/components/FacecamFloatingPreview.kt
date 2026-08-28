package com.example.ui.components

import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.ui.theme.LocalAccentTheme
import com.example.ui.viewmodel.FacecamShape

@Composable
fun FacecamFloatingPreview(
    shape: FacecamShape = FacecamShape.CIRCLE,
    sizeDp: Int = 110,
    isFrontCamera: Boolean = true,
    hasCameraPermission: Boolean = false,
    onToggleCamera: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val accent = LocalAccentTheme.current

    val composeShape: Shape = when (shape) {
        FacecamShape.CIRCLE -> CircleShape
        FacecamShape.ROUNDED_RECT -> RoundedCornerShape(20.dp)
        FacecamShape.SQUARE -> RoundedCornerShape(8.dp)
    }

    Box(
        modifier = modifier
            .size(sizeDp.dp)
            .shadow(12.dp, composeShape)
            .clip(composeShape)
            .background(Color(0xFF1E293B))
            .border(3.dp, accent.primary, composeShape)
            .testTag("facecam_preview_box"),
        contentAlignment = Alignment.Center
    ) {
        if (hasCameraPermission) {
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx)
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        try {
                            val cameraProvider = cameraProviderFuture.get()
                            val preview = Preview.Builder().build().also {
                                it.setSurfaceProvider(previewView.surfaceProvider)
                            }
                            val cameraSelector = if (isFrontCamera) {
                                CameraSelector.DEFAULT_FRONT_CAMERA
                            } else {
                                CameraSelector.DEFAULT_BACK_CAMERA
                            }
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview)
                        } catch (_: Exception) {}
                    }, ContextCompat.getMainExecutor(ctx))
                    previewView
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Placeholder Avatar when permission not yet granted
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Facecam Placeholder",
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size((sizeDp / 2.2).dp)
                )
            }
        }

        // Camera flip button
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(28.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.6f))
                .clickable { onToggleCamera() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.FlipCameraAndroid,
                contentDescription = "Flip Facecam",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
