/*
 * Copyright 2020-2023 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package com.goldenraven.devkitwallet.ui.screens.wallet

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.graphics.createBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.goldenraven.devkitwallet.ui.Screen
import com.goldenraven.devkitwallet.ui.components.SecondaryScreensAppBar
import com.goldenraven.devkitwallet.ui.theme.DevkitWalletColors
import com.goldenraven.devkitwallet.ui.theme.jetBrainsMonoRegular
import com.goldenraven.devkitwallet.viewmodels.AddressViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter

private const val TAG = "ReceiveScreen"

@Composable
internal fun ReceiveScreen(
    navController: NavController,
    addressViewModel: AddressViewModel = viewModel()
) {

    val address by addressViewModel.address.observeAsState(null)
    val addressIndex by addressViewModel.addressIndex.observeAsState("")

    Scaffold(
        topBar = {
            SecondaryScreensAppBar(
                title = "Receive Address",
                navigation = { navController.navigate(Screen.HomeScreen.route) }
            )
        }
    ) { paddingValues ->
        ConstraintLayout(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(DevkitWalletColors.primary)
        ) {
            val (QRCode, bottomButtons) = createRefs()

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.constrainAs(QRCode) {
                    top.linkTo(parent.top)
                    bottom.linkTo(bottomButtons.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    height = Dimension.fillToConstraints
                }
            ) {
                val QR: ImageBitmap? = address?.let { addressToQR(it) }
                Log.i("ReceiveScreen", "New receive address is $address")
                if (QR != null) {
                    Image(
                        bitmap = QR,
                        contentDescription = "Bitcoindevkit website QR code",
                        Modifier.size(250.dp)
                    )
                    Spacer(modifier = Modifier.padding(vertical = 8.dp))
                    SelectionContainer {
                        Text(
                            text = address!!,
                            fontFamily = jetBrainsMonoRegular,
                            color = DevkitWalletColors.white
                        )
                    }
                    Spacer(modifier = Modifier.padding(vertical = 8.dp))
                    Text(
                        text = "m/84h/1h/0h/0/$addressIndex",
                        fontFamily = jetBrainsMonoRegular,
                        color = DevkitWalletColors.white
                    )
                }
            }

            Column(
                Modifier
                    .constrainAs(bottomButtons) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .padding(bottom = 24.dp)
            ) {
                Button(
                    onClick = { addressViewModel.updateAddress() },
                    colors = ButtonDefaults.buttonColors(DevkitWalletColors.secondary),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .height(80.dp)
                        .fillMaxWidth(0.9f)
                        .padding(vertical = 8.dp, horizontal = 8.dp)
                        .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp))
                ) {
                    Text(
                        text = "generate address",
                        fontSize = 14.sp,
                        fontFamily = jetBrainsMonoRegular,
                        textAlign = TextAlign.Center,
                        lineHeight = 28.sp,
                    )
                }
            }
        }
    }
}

private fun addressToQR(address: String): ImageBitmap? {
    Log.i(TAG, "We are generating the QR code for address $address")
    try {
        val qrCodeWriter: QRCodeWriter = QRCodeWriter()
        val bitMatrix: BitMatrix = qrCodeWriter.encode(address, BarcodeFormat.QR_CODE, 1000, 1000)
        val bitMap = createBitmap(1000, 1000)
        for (x in 0 until 1000) {
            for (y in 0 until 1000) {
                // DevkitWalletColors.primaryDark for dark and DevkitWalletColors.white for light
                bitMap.setPixel(x, y, if (bitMatrix[x, y]) 0xff203b46.toInt() else 0xffffffff.toInt())
            }
        }
        return bitMap.asImageBitmap()
    } catch (e: Throwable) {
        Log.i("ReceiveScreen", "Error with QRCode generation, $e")
    }
    return null
}

// @Preview(device = Devices.PIXEL_4, showBackground = true)
// @Composable
// internal fun PreviewReceiveScreen() {
//     ReceiveScreen(rememberNavController())
// }
