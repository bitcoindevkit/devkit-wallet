/*
 * Copyright 2021-2026 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.presentation.ui.screens.drawer

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import org.bitcoindevkit.devkitwallet.R
import org.bitcoindevkit.devkitwallet.presentation.theme.inter
import org.bitcoindevkit.devkitwallet.presentation.ui.components.SecondaryScreensAppBar

private val message: String =
    """
    This wallet is build for:

    1. Developers interested in learning how to leverage the Bitcoin Development Kit on Android.

    2. Any bitcoiner looking for a Signet/Testnet/Regtest wallet!
    """.trimIndent()

@Composable
internal fun AboutScreen(navController: NavController) {
    val colorScheme = MaterialTheme.colorScheme
    val mUriHandler = LocalUriHandler.current
    val openSourceRepository =
        remember { { mUriHandler.openUri("https://github.com/bitcoindevkit/bdk-kotlin-example-wallet") } }

    Scaffold(
        topBar = {
            SecondaryScreensAppBar(
                title = "About",
                navigation = { navController.popBackStack() },
            )
        },
        containerColor = colorScheme.surface,
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.padding(24.dp))
            Image(
                painter = painterResource(id = R.drawable.bdk_logo),
                contentDescription = "Old School BDK Logo",
                Modifier.size(180.dp),
            )
            Spacer(modifier = Modifier.padding(24.dp))
            Text(
                text = message,
                color = colorScheme.onSurface,
                fontFamily = inter,
                fontSize = 16.sp,
                lineHeight = 26.sp,
                modifier = Modifier.padding(all = 8.dp),
            )
            Spacer(modifier = Modifier.padding(8.dp))
            Text(
                text = "You are using the Compact Block Filters (CBF) version of the wallet.",
                color = colorScheme.onSurface,
                fontFamily = inter,
                fontSize = 16.sp,
                lineHeight = 26.sp,
                modifier = Modifier.padding(all = 8.dp),
            )
            Spacer(modifier = Modifier.padding(8.dp))
            Text(
                text = "Check out the source code for the wallet on GitHub.",
                color = colorScheme.primary,
                fontFamily = inter,
                fontSize = 16.sp,
                textDecoration = TextDecoration.Underline,
                lineHeight = 26.sp,
                modifier =
                    Modifier
                        .padding(all = 8.dp)
                        .clickable(onClick = openSourceRepository),
            )
        }
    }
}

@Preview(device = Devices.PIXEL_4, showBackground = true)
@Composable
internal fun PreviewAboutScreen() {
    AboutScreen(rememberNavController())
}
