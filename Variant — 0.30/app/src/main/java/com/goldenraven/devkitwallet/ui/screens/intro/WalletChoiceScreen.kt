/*
 * Copyright 2020-2023 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package com.goldenraven.devkitwallet.ui.screens.intro

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.MaterialTheme
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
import com.goldenraven.devkitwallet.R
import com.goldenraven.devkitwallet.WalletCreateType
import com.goldenraven.devkitwallet.ui.Screen
import com.goldenraven.devkitwallet.ui.theme.DevkitWalletColors
import com.goldenraven.devkitwallet.ui.theme.devkitTypography
import com.goldenraven.devkitwallet.ui.theme.jetBrainsMonoLight

@Composable
internal fun WalletChoiceScreen(
    navController: NavController,
    onBuildWalletButtonClicked: (WalletCreateType) -> Unit
) {

    Scaffold { paddingValues ->
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .background(DevkitWalletColors.primary)
                .padding(paddingValues)
        ) {
            val (logo, create, recover) = createRefs()

            Row(
                modifier = Modifier
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
                    Modifier.size(90.dp)
                )
                Spacer(modifier = Modifier.padding(8.dp))
                Text(
                    text = "BDK\nSample\nTestnet\nWallet",
                    color = DevkitWalletColors.white,
                    fontSize = 28.sp,
                    fontFamily = jetBrainsMonoLight,
                )
            }

            Button(
                onClick = { onBuildWalletButtonClicked(WalletCreateType.FROMSCRATCH) },
                colors = ButtonDefaults.buttonColors(DevkitWalletColors.secondary),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .size(width = 300.dp, height = 170.dp)
                    .padding(vertical = 8.dp, horizontal = 8.dp)
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp))
                    .constrainAs(create) {
                        bottom.linkTo(recover.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            ) {
                Text(
                    text = "Create a\nNew Wallet",
                    // fontSize = 18.sp,
                    // fontFamily = jetBrainsMonoLight,
                    // textAlign = TextAlign.Center,
                    // lineHeight = 28.sp,
                )
            }

            Button(
                onClick = { navController.navigate(Screen.WalletRecoveryScreen.route) },
                colors = ButtonDefaults.buttonColors(DevkitWalletColors.secondary),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .size(width = 300.dp, height = 170.dp)
                    .padding(vertical = 8.dp, horizontal = 8.dp)
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp))
                    .constrainAs(recover) {
                        bottom.linkTo(parent.bottom, margin = 100.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            ) {
                Text(
                    text = "Recover an\nExisting Wallet",
                    // fontSize = 18.sp,
                    // fontFamily = jetBrainsMonoLight,
                    // textAlign = TextAlign.Center,
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
