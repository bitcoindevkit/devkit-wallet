/*
 * Copyright 2020-2023 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package com.goldenraven.devkitwallet.ui.screens.drawer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Switch
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.goldenraven.devkitwallet.domain.ElectrumSettings
import com.goldenraven.devkitwallet.domain.Wallet
import com.goldenraven.devkitwallet.ui.Screen
import com.goldenraven.devkitwallet.ui.components.SecondaryScreensAppBar
import com.goldenraven.devkitwallet.ui.theme.DevkitWalletColors
import com.goldenraven.devkitwallet.ui.theme.jetBrainsMonoLight

@Composable
internal fun ElectrumScreen(navController: NavController) {
    val focusManager = LocalFocusManager.current
    val isBlockChainCreated = Wallet.isBlockChainCreated()
    val electrumServer: MutableState<String> = remember { mutableStateOf("") }
    val isChecked: MutableState<Boolean> = remember { mutableStateOf(false) }
    if (isBlockChainCreated) {
        electrumServer.value = Wallet.getElectrumURL()
        isChecked.value = Wallet.isElectrumServerDefault()
    }

    Scaffold(
        topBar = {
            SecondaryScreensAppBar(
                title = "Custom Electrum Server",
                navigation = { navController.navigate(Screen.WalletScreen.route) }
            )
        },
        containerColor = DevkitWalletColors.primary
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(all = 16.dp),
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Use default electrum URL",
                    color = DevkitWalletColors.white,
                    fontSize = 14.sp,
                    fontFamily = jetBrainsMonoLight,
                    textAlign = TextAlign.Center,
                )
                Switch(
                    checked = isChecked.value,
                    onCheckedChange = {
                        isChecked.value = it
                        if (it) {
                            Wallet.setElectrumSettings(ElectrumSettings.DEFAULT)
                        } else {
                            Wallet.setElectrumSettings(ElectrumSettings.CUSTOM)
                        }
                    },
                    enabled = isBlockChainCreated
                )
            }

            OutlinedTextField(
                    value = electrumServer.value,
                    onValueChange = { electrumServer.value = it },
                    label = {
                        Text(
                            text = "Electrum Server",
                            color = DevkitWalletColors.white,
                        )
                    },
                    singleLine = true,
                    textStyle = TextStyle(fontFamily = jetBrainsMonoLight, color = DevkitWalletColors.white),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = DevkitWalletColors.accent1,
                        unfocusedBorderColor = DevkitWalletColors.white,
                        cursorColor = DevkitWalletColors.accent1,
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.clearFocus()
                    }),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isBlockChainCreated && !isChecked.value
                )

            Button(
                onClick = {
                    Wallet.changeElectrumServer(electrumServer.value)
                    focusManager.clearFocus()
                },
                modifier = Modifier
                    .align(alignment = Alignment.End)
                    .padding(all = 8.dp),
                colors = ButtonDefaults.buttonColors(DevkitWalletColors.secondary),
                enabled = isBlockChainCreated && !isChecked.value
            ) {
                Text(
                    text = "Save",
                    color = DevkitWalletColors.white,
                    fontSize = 12.sp,
                    fontFamily = jetBrainsMonoLight,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
