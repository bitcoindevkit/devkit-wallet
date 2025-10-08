/*
 * Copyright 2021-2024 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.presentation.ui.screens.intro

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import org.bitcoindevkit.devkitwallet.R
import org.bitcoindevkit.devkitwallet.presentation.navigation.ActiveWalletsScreen
import org.bitcoindevkit.devkitwallet.presentation.navigation.CreateNewWalletScreen
import org.bitcoindevkit.devkitwallet.presentation.navigation.WalletRecoveryScreen
import org.bitcoindevkit.devkitwallet.presentation.theme.DevkitWalletColors
import org.bitcoindevkit.devkitwallet.presentation.theme.monoBold

@Composable
internal fun WalletChoiceScreen(navController: NavController) {
    Scaffold(
        containerColor = DevkitWalletColors.primary,
    ) { paddingValues ->
        ConstraintLayout(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
        ) {
            val (logo, active, create, recover) = createRefs()

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 90.dp)
                        .constrainAs(logo) {
                            top.linkTo(parent.top)
                        },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_testnet_logo),
                    contentDescription = "Bitcoin testnet logo",
                    Modifier.size(90.dp),
                )
                Spacer(modifier = Modifier.padding(8.dp))
                Text(
                    text = "Devkit\nWallet",
                    color = DevkitWalletColors.white,
                    fontSize = 28.sp,
                    lineHeight = 38.sp,
                    fontFamily = monoBold,
                )
            }

            Button(
                onClick = { navController.navigate(ActiveWalletsScreen) },
                colors = ButtonDefaults.buttonColors(DevkitWalletColors.secondary),
                shape = RoundedCornerShape(16.dp),
                enabled = true,
                modifier =
                    Modifier
                        .size(width = 300.dp, height = 150.dp)
                        .padding(vertical = 8.dp, horizontal = 8.dp)
                        .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp))
                        .constrainAs(active) {
                            bottom.linkTo(create.top)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        },
            ) {
                Text(
                    text = "Use an\nActive Wallet",
                    // fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    // lineHeight = 28.sp,
                )
            }

            Button(
                onClick = { navController.navigate(CreateNewWalletScreen) },
                colors = ButtonDefaults.buttonColors(DevkitWalletColors.secondary),
                shape = RoundedCornerShape(16.dp),
                modifier =
                    Modifier
                        .size(width = 300.dp, height = 150.dp)
                        .padding(vertical = 8.dp, horizontal = 8.dp)
                        .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp))
                        .constrainAs(create) {
                            bottom.linkTo(recover.top)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        },
            ) {
                Text(
                    text = "Create a\nNew Wallet",
                    // fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    // lineHeight = 28.sp,
                )
            }

            Button(
                onClick = { navController.navigate(WalletRecoveryScreen) },
                colors = ButtonDefaults.buttonColors(DevkitWalletColors.secondary),
                shape = RoundedCornerShape(16.dp),
                modifier =
                    Modifier
                        .size(width = 300.dp, height = 150.dp)
                        .padding(vertical = 8.dp, horizontal = 8.dp)
                        .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp))
                        .constrainAs(recover) {
                            bottom.linkTo(parent.bottom, margin = 70.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        },
            ) {
                Text(
                    text = "Recover an\nExisting Wallet",
                    // fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    // lineHeight = 28.sp,
                )
            }
        }
    }
}

// @Preview(device = Devices.PIXEL_4, showBackground = true)
// @Composable
// internal fun PreviewWalletChoiceScreen() {
//     WalletChoiceScreen(rememberNavController())
// }
