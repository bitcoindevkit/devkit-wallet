/*
 * Copyright 2021-2026 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.presentation.ui.screens.wallet

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.createBitmap
import androidx.navigation.NavController
import com.composables.icons.lucide.ClipboardCopy
import com.composables.icons.lucide.Lucide
import com.google.zxing.BarcodeFormat
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.bitcoindevkit.devkitwallet.presentation.navigation.HomeScreen
import org.bitcoindevkit.devkitwallet.presentation.theme.inter
import org.bitcoindevkit.devkitwallet.presentation.theme.monoRegular
import org.bitcoindevkit.devkitwallet.presentation.ui.components.SecondaryScreensAppBar
import org.bitcoindevkit.devkitwallet.presentation.viewmodels.mvi.ReceiveScreenAction
import org.bitcoindevkit.devkitwallet.presentation.viewmodels.mvi.ReceiveScreenState

private const val TAG = "ReceiveScreen"

@Composable
internal fun ReceiveScreen(
    state: ReceiveScreenState,
    onAction: (ReceiveScreenAction) -> Unit,
    navController: NavController,
) {
    Log.i(TAG, "We are recomposing the ReceiveScreen")
    val snackbarHostState = remember { SnackbarHostState() }
    val colorScheme = MaterialTheme.colorScheme
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            SecondaryScreensAppBar(
                title = "Receive Address",
                navigation = { navController.navigate(HomeScreen) },
            )
        },
        containerColor = colorScheme.surface,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(24.dp))

            val qrBitmap: ImageBitmap? = state.address?.let { addressToQR(it) }
            Log.i(TAG, "New receive address is ${state.address}")

            if (qrBitmap != null) {
                // QR code in outlined container
                Box(
                    modifier = Modifier
                        .border(
                            width = 1.5.dp,
                            color = colorScheme.outline.copy(alpha = 0.10f),
                            shape = RoundedCornerShape(24.dp),
                        )
                        .clip(RoundedCornerShape(24.dp))
                        .padding(20.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        bitmap = qrBitmap,
                        contentDescription = "Bitcoin address QR code",
                        Modifier
                            .size(230.dp)
                            .clip(RoundedCornerShape(12.dp)),
                    )
                }

                Spacer(Modifier.height(24.dp))

                // Address card with copy button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .border(
                            width = 1.5.dp,
                            color = colorScheme.outline.copy(alpha = 0.10f),
                            shape = RoundedCornerShape(16.dp),
                        )
                        .clip(RoundedCornerShape(16.dp))
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                ) {
                    Text(
                        text = state.address.chunked(4).joinToString(" "),
                        fontFamily = monoRegular,
                        fontSize = 13.sp,
                        color = colorScheme.onSurface,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(end = 40.dp),
                    )
                    IconButton(
                        onClick = {
                            copyToClipboard(
                                state.address,
                                context,
                                scope,
                                snackbarHostState,
                                null,
                            )
                        },
                        modifier = Modifier
                            .size(28.dp)
                            .align(Alignment.CenterEnd),
                    ) {
                        Icon(
                            Lucide.ClipboardCopy,
                            tint = colorScheme.onSurface.copy(alpha = 0.5f),
                            contentDescription = "Copy to clipboard",
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Address index
                Text(
                    text = "Address index: ${state.addressIndex}",
                    fontFamily = inter,
                    fontSize = 12.sp,
                    color = colorScheme.onSurface.copy(alpha = 0.4f),
                )
            }

            // Generate new address button
            OutlinedButton(
                onClick = { onAction(ReceiveScreenAction.UpdateAddress) },
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.5.dp, colorScheme.primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(52.dp),
            ) {
                Text(
                    text = "Generate New Address",
                    fontFamily = inter,
                    fontSize = 15.sp,
                    color = colorScheme.primary,
                )
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

private fun addressToQR(address: String): ImageBitmap? {
    Log.i(TAG, "We are generating the QR code for address $address")
    try {
        val qrCodeWriter = QRCodeWriter()
        val bitMatrix: BitMatrix = qrCodeWriter.encode(address, BarcodeFormat.QR_CODE, 1000, 1000)
        val bitMap = createBitmap(1000, 1000)
        for (x in 0 until 1000) {
            for (y in 0 until 1000) {
                bitMap.setPixel(x, y, if (bitMatrix[x, y]) 0xFF1C1B1F.toInt() else 0xFFE6E1E5.toInt())
            }
        }
        return bitMap.asImageBitmap()
    } catch (e: Throwable) {
        Log.i(TAG, "Error with QRCode generation, $e")
    }
    return null
}

fun copyToClipboard(
    content: String,
    context: Context,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    setCopyClicked: (
        (Boolean) -> Unit
    )?,
) {
    val clipboard: ClipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip: ClipData = ClipData.newPlainText("", content)
    clipboard.setPrimaryClip(clip)
    scope.launch {
        snackbarHostState.showSnackbar("Copied address to clipboard!")
        delay(1000)
        if (setCopyClicked != null) {
            setCopyClicked(false)
        }
    }
}
