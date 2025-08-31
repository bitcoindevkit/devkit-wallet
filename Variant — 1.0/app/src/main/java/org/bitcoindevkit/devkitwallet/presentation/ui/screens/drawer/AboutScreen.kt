/*
 * Copyright 2021-2025 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.presentation.ui.screens.drawer

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import org.bitcoindevkit.devkitwallet.R
import org.bitcoindevkit.devkitwallet.presentation.navigation.WalletScreen
import org.bitcoindevkit.devkitwallet.presentation.theme.DevkitWalletColors
import org.bitcoindevkit.devkitwallet.presentation.theme.quattroRegular
import org.bitcoindevkit.devkitwallet.presentation.ui.components.SecondaryScreensAppBar

private val message: String = """
    This wallet is build for:
    
    1. Developers interested in learning how to leverage the Bitcoin Development Kit on Android.
    
    2. Any bitcoiner looking for a Signet/Testnet/Regtest wallet!
""".trimIndent()

@Composable
internal fun AboutScreen(navController: NavController) {
    Scaffold(
        topBar = {
            SecondaryScreensAppBar(
                title = "About",
                navigation = { navController.navigate(WalletScreen) }
            )
        },
        containerColor = DevkitWalletColors.primary
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.padding(24.dp))
            Image(
                painter = painterResource(id = R.drawable.bdk_logo),
                contentDescription = "BDK logo",
                Modifier.size(180.dp)
            )
            Spacer(modifier = Modifier.padding(24.dp))
            Text(
                text = message,
                color = DevkitWalletColors.white,
                textAlign = TextAlign.Start,
                fontFamily = quattroRegular,
                modifier = Modifier.padding(all = 8.dp)
            )
        }
    }
}

@Preview(device = Devices.PIXEL_4, showBackground = true)
@Composable
internal fun PreviewAboutScreen() {
    AboutScreen(rememberNavController())
}
