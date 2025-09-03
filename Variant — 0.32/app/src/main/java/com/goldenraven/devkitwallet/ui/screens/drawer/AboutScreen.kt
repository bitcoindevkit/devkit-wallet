/*
 * Copyright 2020-2023 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package com.goldenraven.devkitwallet.ui.screens.drawer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.goldenraven.devkitwallet.R
import com.goldenraven.devkitwallet.ui.Screen
import com.goldenraven.devkitwallet.ui.components.SecondaryScreensAppBar
import com.goldenraven.devkitwallet.ui.theme.DevkitWalletColors
import com.goldenraven.devkitwallet.ui.theme.jetBrainsMonoLight

@Composable
internal fun AboutScreen(navController: NavController) {
    Scaffold(
        topBar = {
            SecondaryScreensAppBar(
                title = "About",
                navigation = { navController.navigate(Screen.WalletScreen.route) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DevkitWalletColors.primary)
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(id = R.drawable.bdk_logo),
                contentDescription = "Bitcoin testnet logo",
                Modifier.size(180.dp)
            )
            Spacer(modifier = Modifier.padding(24.dp))
            Text(
                text = "This wallet is build for developers to learn how to leverage the Bitcoin Development Kit.",
                color = DevkitWalletColors.white,
                fontSize = 14.sp,
                fontFamily = jetBrainsMonoLight,
                textAlign = TextAlign.Start,
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
