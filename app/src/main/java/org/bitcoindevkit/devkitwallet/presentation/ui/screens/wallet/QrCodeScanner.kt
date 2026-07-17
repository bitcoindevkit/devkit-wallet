/*
 * Copyright 2021-2026 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

@file:Suppress("UnsafeOptInUsageError")

package org.bitcoindevkit.devkitwallet.presentation.ui.screens.wallet

import android.Manifest
import android.util.Log
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import org.bitcoindevkit.devkitwallet.presentation.theme.DevkitWalletColors
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

private const val TAG = "QrCodeScanner"

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun QrScannerDialog(onScanned: (String) -> Unit, onDismiss: () -> Unit) {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DevkitWalletColors.primary),
            contentAlignment = Alignment.Center,
        ) {
            when {
                cameraPermissionState.status.isGranted -> {
                    CameraPreview(
                        onScanned = { rawValue ->
                            onScanned(rawValue)
                        }
                    )
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(DevkitWalletColors.accent2),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 48.dp),
                    ) {
                        Text(text = "Cancel")
                    }
                }

                cameraPermissionState.status.shouldShowRationale -> {
                    PermissionRationaleContent(
                        onRequest = { cameraPermissionState.launchPermissionRequest() },
                        onDismiss = onDismiss,
                    )
                }

                else -> {
                    DisposableEffect(Unit) {
                        cameraPermissionState.launchPermissionRequest()
                        onDispose {}
                    }
                    PermissionRationaleContent(
                        onRequest = { cameraPermissionState.launchPermissionRequest() },
                        onDismiss = onDismiss,
                    )
                }
            }
        }
    }
}

@Composable
private fun PermissionRationaleContent(onRequest: () -> Unit, onDismiss: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(32.dp),
    ) {
        Text(
            text = "Camera permission is required to scan QR codes.",
            color = DevkitWalletColors.white,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onRequest,
            colors = ButtonDefaults.buttonColors(DevkitWalletColors.accent1),
        ) {
            Text("Grant permission")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onDismiss,
            colors = ButtonDefaults.buttonColors(DevkitWalletColors.accent2),
        ) {
            Text("Cancel")
        }
    }
}

@Composable
private fun CameraPreview(onScanned: (String) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current

    val cameraExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }

    var scanned by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose { cameraExecutor.shutdown() }
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->
            val previewView = PreviewView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                )
            }

            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
            cameraProviderFuture.addListener(
                {
                    val cameraProvider = cameraProviderFuture.get()

                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    val barcodeScanner = BarcodeScanning.getClient()

                    val imageAnalysis = ImageAnalysis
                        .Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()

                    imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy: ImageProxy ->
                        if (scanned) {
                            imageProxy.close()
                            return@setAnalyzer
                        }
                        processImageProxy(
                            barcodeScanner = barcodeScanner,
                            imageProxy = imageProxy,
                            onFound = { rawValue ->
                                if (!scanned) {
                                    scanned = true
                                    onScanned(rawValue)
                                }
                            },
                        )
                    }

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            preview,
                            imageAnalysis,
                        )
                    } catch (e: Exception) {
                        Log.e(TAG, "Camera binding failed", e)
                    }
                },
                ContextCompat.getMainExecutor(ctx),
            )

            previewView
        },
    )
}

private fun processImageProxy(
    barcodeScanner: com.google.mlkit.vision.barcode.BarcodeScanner,
    imageProxy: ImageProxy,
    onFound: (String) -> Unit,
) {
    val mediaImage = imageProxy.image
    if (mediaImage == null) {
        imageProxy.close()
        return
    }

    val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
    barcodeScanner
        .process(image)
        .addOnSuccessListener { barcodes ->
            for (barcode in barcodes) {
                if (barcode.format == Barcode.FORMAT_QR_CODE) {
                    val rawValue = barcode.rawValue ?: continue
                    if (rawValue.isNotBlank()) {
                        onFound(rawValue)
                        break
                    }
                }
            }
        }.addOnFailureListener { e ->
            Log.w(TAG, "Barcode scan failed", e)
        }.addOnCompleteListener {
            imageProxy.close()
        }
}
