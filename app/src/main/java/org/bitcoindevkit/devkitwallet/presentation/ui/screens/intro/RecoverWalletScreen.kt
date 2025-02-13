/*
 * Copyright 2021-2024 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.presentation.ui.screens.intro

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import org.bitcoindevkit.devkitwallet.presentation.WalletCreateType
import org.bitcoindevkit.devkitwallet.data.ActiveWalletScriptType
import org.bitcoindevkit.devkitwallet.data.RecoverWalletConfig
import org.bitcoindevkit.devkitwallet.presentation.ui.components.NeutralButton
import org.bitcoindevkit.devkitwallet.presentation.ui.components.SecondaryScreensAppBar
import org.bitcoindevkit.devkitwallet.presentation.theme.DevkitWalletColors
import org.bitcoindevkit.devkitwallet.presentation.theme.quattroRegular
import org.rustbitcoin.bitcoin.Network

@Composable
internal fun RecoverWalletScreen(
    navController: NavController,
    onBuildWalletButtonClicked: (WalletCreateType) -> Unit
) {
    Scaffold(
        topBar = {
            SecondaryScreensAppBar(title = "Recover a Wallet", navigation = { navController.navigateUp() })
        },
        containerColor = DevkitWalletColors.primary
    ) { paddingValues ->

        // the screen is broken into 2 parts: the screen title and the body
        ConstraintLayout(
            modifier = Modifier
                .fillMaxHeight(1f)
                .padding(paddingValues)
        ) {

            val (screenTitle, body) = createRefs()

            val emptyRecoveryPhrase: Map<Int, String> = mapOf(
                1 to "", 2 to "", 3 to "", 4 to "", 5 to "", 6 to "",
                7 to "", 8 to "", 9 to "", 10 to "", 11 to "", 12 to ""
            )
            val (recoveryPhraseWordMap, setRecoveryPhraseWordMap) = remember { mutableStateOf(emptyRecoveryPhrase) }
            val walletName: MutableState<String> = remember { mutableStateOf("") }
            var selectedNetwork: Network by remember { mutableStateOf(Network.TESTNET) }
            val network = listOf(Network.TESTNET)
            var selectedScriptType: ActiveWalletScriptType by remember { mutableStateOf(ActiveWalletScriptType.P2WPKH) }
            val scriptTypes = listOf(ActiveWalletScriptType.P2WPKH)

            // the app name
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .constrainAs(screenTitle) {
                        top.linkTo(parent.top)
                    }
            ) {
                Column {
                    OutlinedTextField(
                        modifier = Modifier
                            .padding(vertical = 8.dp),
                        value = walletName.value,
                        onValueChange = { walletName.value = it },
                        label = {
                            Text(
                                text = "Wallet Name",
                                color = DevkitWalletColors.white,
                            )
                        },
                        singleLine = true,
                        textStyle = TextStyle(fontFamily = quattroRegular, color = DevkitWalletColors.white),
                        colors = OutlinedTextFieldDefaults.colors(
                            cursorColor = DevkitWalletColors.accent1,
                            focusedBorderColor = DevkitWalletColors.accent1,
                            unfocusedBorderColor = DevkitWalletColors.white,
                        ),
                    )

                    network.forEach {
                        RadioButtonWithLabel(
                            label = it.name,
                            isSelected = selectedNetwork == it,
                            onSelect = { selectedNetwork = it }
                        )
                    }
                    Spacer(modifier = Modifier.padding(8.dp))

                    scriptTypes.forEach {
                        RadioButtonWithLabel(
                            label = it.name,
                            isSelected = selectedScriptType == it,
                            onSelect = { selectedScriptType = it }
                        )
                    }
                }
            }

            // the body
            MyList(
                recoveryPhraseWordMap,
                setRecoveryPhraseWordMap,
                modifier = Modifier
                    .constrainAs(body) {
                        top.linkTo(screenTitle.bottom)
                        bottom.linkTo(parent.bottom)
                        height = Dimension.fillToConstraints
                    },
                onClick = {
                    val recoverWalletConfig = RecoverWalletConfig(
                        name = walletName.value,
                        network = selectedNetwork,
                        scriptType = selectedScriptType,
                        recoveryPhrase = buildRecoveryPhrase(recoveryPhraseWordMap),
                    )
                    onBuildWalletButtonClicked(WalletCreateType.RECOVER(recoverWalletConfig))
                }
            )
        }
    }
}

@Composable
fun MyList(
    recoveryPhraseWordMap: Map<Int, String>,
    setRecoveryPhraseWordMap: (Map<Int, String>) -> Unit,
    modifier: Modifier,
    onClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    Column(
        modifier
            .fillMaxWidth(1f)
            .verticalScroll(state = scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val focusManager = LocalFocusManager.current
        for (i in 1..12) {
            WordField(wordNumber = i, recoveryPhraseWordMap, setRecoveryPhraseWordMap, focusManager)
        }

        NeutralButton(
            text = "Recover Wallet",
            enabled = true,
            onClick = { onClick() }
        )
    }
}

@Composable
fun WordField(
    wordNumber: Int,
    recoveryWordMap: Map<Int, String>,
    setRecoveryPhraseWordMap: (Map<Int, String>) -> Unit,
    focusManager: FocusManager
) {
    OutlinedTextField(
        value = recoveryWordMap[wordNumber] ?: "elvis is here",
        onValueChange = { newText ->
            val newMap: MutableMap<Int, String> = recoveryWordMap.toMutableMap()
            newMap[wordNumber] = newText

            val updatedMap = newMap.toMap()
            setRecoveryPhraseWordMap(updatedMap)
        },
        label = {
            Text(
                text = "Word $wordNumber",
                color = DevkitWalletColors.white,
            )
        },
        textStyle = TextStyle(
            fontSize = 16.sp,
            color = DevkitWalletColors.white
        ),
        colors = OutlinedTextFieldDefaults.colors(
            cursorColor = DevkitWalletColors.accent1,
            focusedBorderColor = DevkitWalletColors.accent1,
            unfocusedBorderColor = DevkitWalletColors.white,
        ),
        modifier = Modifier
            .padding(4.dp),
        keyboardOptions = when (wordNumber) {
            12 -> KeyboardOptions(imeAction = ImeAction.Done)
            else -> KeyboardOptions(imeAction = ImeAction.Next)
        },
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) },
            onDone = { focusManager.clearFocus() }
        ),
        singleLine = true,
        // contentPadding = TextFieldDefaults.contentPaddingWithoutLabel(
        //     start = 8.dp,
        //     top = 4.dp,
        //     end = 8.dp,
        //     bottom = 4.dp,
        // )
    )
}

// input words can have capital letters, space around them, space inside of them
private fun buildRecoveryPhrase(recoveryPhraseWordMap: Map<Int, String>): String {
    var recoveryPhrase = ""
    recoveryPhraseWordMap.values.forEach {
        recoveryPhrase = recoveryPhrase.plus(it.trim().replace(" ", "").lowercase().plus(" "))
    }
    return recoveryPhrase.trim()
}

// @Preview(device = Devices.PIXEL_4, showBackground = true)
// @Composable
// internal fun PreviewWalletRecoveryScreen() {
//     WalletRecoveryScreen()
// }
