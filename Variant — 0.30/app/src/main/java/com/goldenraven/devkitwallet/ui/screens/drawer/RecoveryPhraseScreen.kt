/*
 * Copyright 2020-2023 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package com.goldenraven.devkitwallet.ui.screens.drawer

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.goldenraven.devkitwallet.domain.Repository
import com.goldenraven.devkitwallet.ui.Screen
import com.goldenraven.devkitwallet.ui.components.SecondaryScreensAppBar
import com.goldenraven.devkitwallet.ui.theme.DevkitWalletColors
import com.goldenraven.devkitwallet.ui.theme.jetBrainsMonoLight

@Composable
internal fun RecoveryPhraseScreen(navController: NavController) {

    val seedPhrase: String = Repository.getMnemonic()
    val wordList: List<String> = seedPhrase.split(" ")

    Scaffold(
        topBar = {
            SecondaryScreensAppBar(
                title = "Your Recovery Phrase",
                navigation = { navController.navigate(Screen.WalletScreen.route) }
            )
        },
        containerColor = DevkitWalletColors.primary
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(all = 32.dp)
        ) {
            wordList.forEachIndexed { index, item ->
                Text(
                    text = "${index + 1}. $item",
                    modifier = Modifier.weight(weight = 1F),
                    color = DevkitWalletColors.white,
                    fontFamily = jetBrainsMonoLight
                )
            }
        }
    }
}

@Preview(device = Devices.PIXEL_4, showBackground = true)
@Composable
internal fun PreviewRecoveryPhraseScreen() {
    RecoveryPhraseScreen(rememberNavController())
}
