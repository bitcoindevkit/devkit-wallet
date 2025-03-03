/*
 * Copyright 2021-2024 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.presentation.ui.screens.intro

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import org.bitcoindevkit.Descriptor
import org.bitcoindevkit.DescriptorSecretKey
import org.bitcoindevkit.KeychainKind
import org.bitcoindevkit.Mnemonic
import org.bitcoindevkit.devkitwallet.presentation.WalletCreateType
import org.bitcoindevkit.devkitwallet.data.ActiveWalletScriptType
import org.bitcoindevkit.devkitwallet.data.RecoverWalletConfig
import org.bitcoindevkit.devkitwallet.domain.DwLogger
import org.bitcoindevkit.devkitwallet.domain.DwLogger.LogLevel.INFO
import org.bitcoindevkit.devkitwallet.domain.bip39WordList
import org.bitcoindevkit.devkitwallet.domain.createScriptAppropriateDescriptor
import org.bitcoindevkit.devkitwallet.presentation.ui.components.NeutralButton
import org.bitcoindevkit.devkitwallet.presentation.ui.components.SecondaryScreensAppBar
import org.bitcoindevkit.devkitwallet.presentation.theme.DevkitWalletColors
import org.bitcoindevkit.devkitwallet.presentation.theme.monoRegular
import org.bitcoindevkit.devkitwallet.presentation.theme.quattroRegular
import org.bitcoindevkit.devkitwallet.presentation.theme.standardText
import org.bitcoindevkit.Network
import org.bitcoindevkit.devkitwallet.presentation.ui.components.CustomSnackbar
import org.bitcoindevkit.devkitwallet.presentation.ui.components.NetworkOptionsCard
import org.bitcoindevkit.devkitwallet.presentation.ui.components.NeutralButton
import org.bitcoindevkit.devkitwallet.presentation.ui.components.SecondaryScreensAppBar
import org.bitcoindevkit.devkitwallet.presentation.ui.components.WalletOptionsCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RecoverWalletScreen(
    navController: NavController,
    onBuildWalletButtonClicked: (WalletCreateType) -> Unit
) {
internal fun RecoverWalletScreen(onAction: (WalletCreateType) -> Unit, navController: NavController) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = { SecondaryScreensAppBar(title = "Recover a Wallet", navigation = { navController.navigateUp() }) },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                CustomSnackbar(data)
            }
        },
        containerColor = DevkitWalletColors.primary
    ) { paddingValues ->
        var selectedIndex by remember { mutableIntStateOf(0) }
        val options = listOf("Descriptor", "Recovery Phrase")

        var descriptorString by remember { mutableStateOf("") }
        var changeDescriptorString by remember { mutableStateOf("") }
        var recoveryPhrase by remember { mutableStateOf("") }

        var walletName by remember { mutableStateOf("") }
        val selectedNetwork: MutableState<Network> = remember { mutableStateOf(Network.SIGNET) }
        val selectedScriptType: MutableState<ActiveWalletScriptType> =
            remember { mutableStateOf(ActiveWalletScriptType.P2TR) }
        val scriptTypes = listOf(ActiveWalletScriptType.P2TR, ActiveWalletScriptType.P2WPKH)

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            SingleChoiceSegmentedButtonRow {
                options.forEachIndexed { index, label ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index, count = options.size
                        ), onClick = { selectedIndex = index }, selected = index == selectedIndex,
                        label = { Text(text = label, fontSize = 12.sp, color = Color.White) }, colors = SegmentedButtonDefaults.colors(
                            activeContainerColor = DevkitWalletColors.primaryLight, activeContentColor = DevkitWalletColors.primaryLight, activeBorderColor = DevkitWalletColors.primaryLight, inactiveContainerColor = DevkitWalletColors.primaryDark, inactiveContentColor = DevkitWalletColors.primaryDark,
                            inactiveBorderColor = DevkitWalletColors.primaryDark,
                        ),
                        border = BorderStroke(4.dp, DevkitWalletColors.primaryDark),
                        icon = { },
                        modifier = Modifier.width(180.dp).padding(top = 8.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.padding(12.dp))

            if (selectedIndex == 0) {
                DescriptorInput(
                    walletName,
                    descriptorString,
                    changeDescriptorString,
                    selectedNetwork,
                    walletNameOnValueChange = { walletName = it },
                    descriptorOnValueChange = { descriptorString = it },
                    changeDescriptorOnValueChange = { changeDescriptorString = it }
                )
                Spacer(modifier = Modifier.weight(1f))
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 32.dp)
                ) {
                    WalletOptionsCard(scriptTypes, selectedNetwork, selectedScriptType)
                    Spacer(modifier = Modifier.padding(12.dp))
                    OutlinedTextField(
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .fillMaxWidth()
                            .align(Alignment.CenterHorizontally),
                        value = walletName,
                        onValueChange = { walletName = it },
                        label = {
                            Text(
                                text = "Give your wallet a name",
                                style = standardText,
                            )
                        },
                        singleLine = true,
                        textStyle = TextStyle(fontFamily = monoRegular, color = DevkitWalletColors.white),
                        colors = OutlinedTextFieldDefaults.colors(
                            cursorColor = DevkitWalletColors.accent1,
                            focusedBorderColor = DevkitWalletColors.accent1,
                            unfocusedBorderColor = DevkitWalletColors.white,
                        ),
                    )
                    RecoveryPhraseInput(recoveryPhrase, onValueChange = { recoveryPhrase = it })
                }
                Spacer(modifier = Modifier.weight(1f))
            }
            NeutralButton(
                text = "Recover Wallet",
                enabled = true,
                onClick = {
                    if (descriptorString.isNotEmpty() && recoveryPhrase.isNotEmpty()) {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                "You cannot recover using both a descriptor and a recovery phrase at the same time."
                            )
                        }
                    }
                    if (descriptorString.isEmpty() && recoveryPhrase.isEmpty()) {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                "You must provide either a descriptor or a recovery phrase to recover a wallet."
                            )
                        }
                    }
                    if (descriptorString.isNotEmpty() && changeDescriptorString.isEmpty()) {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                "You must provide two descriptors for recovery."
                            )
                        }
                    }
                    if (descriptorString.isEmpty() && changeDescriptorString.isNotEmpty()) {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                "You must provide two descriptors for recovery."
                            )
                        }
                    }
                    if (recoveryPhrase.isNotEmpty()) {
                        Log.i("RecoverWalletScreen", "Recovering wallet with recovery phrase")
                        val parsingResult = parseRecoveryPhrase(recoveryPhrase)

                        if (parsingResult is RecoveryPhraseValidationResult.Invalid) {
                            scope.launch {
                                snackbarHostState.showSnackbar(parsingResult.reason)
                            }
                        } else if (parsingResult is RecoveryPhraseValidationResult.ProbablyValid) {
                            val mnemonic = Mnemonic.fromString(parsingResult.recoveryPhrase)
                            val bip32ExtendedRootKey = DescriptorSecretKey(selectedNetwork.value, mnemonic, null)
                            val descriptor = createScriptAppropriateDescriptor(
                                scriptType = selectedScriptType.value,
                                bip32ExtendedRootKey = bip32ExtendedRootKey,
                                network = selectedNetwork.value,
                                keychain = KeychainKind.EXTERNAL
                            )
                            val changeDescriptor = createScriptAppropriateDescriptor(
                                scriptType = selectedScriptType.value,
                                bip32ExtendedRootKey = bip32ExtendedRootKey,
                                network = selectedNetwork.value,
                                keychain = KeychainKind.INTERNAL
                            )
                            val recoverWalletConfig = RecoverWalletConfig(
                        name = walletName,
                                network = selectedNetwork.value,
                        scriptType = selectedScriptType.value,
                                descriptor = descriptor,
                                changeDescriptor = changeDescriptor,
                                recoveryPhrase = parsingResult.recoveryPhrase
                            )
                            DwLogger.log(INFO, "Recovering wallet with recovery phrase (name: $walletName)")
                            onAction(WalletCreateType.RECOVER(recoverWalletConfig))
                        }
                    }
                    if (descriptorString.isNotEmpty() && changeDescriptorString.isNotEmpty()) {
                        Log.i("RecoverWalletScreen", "Recovering wallet with descriptors")

                        val descriptor = Descriptor(descriptorString, selectedNetwork.value)
                        val changeDescriptor = Descriptor(changeDescriptorString, selectedNetwork.value)
                        val recoverWalletConfig = RecoverWalletConfig(
                            name = walletName,
                            network = selectedNetwork.value,
                        scriptType = null,
                            descriptor = descriptor,
                            changeDescriptor = changeDescriptor,
                        recoveryPhrase = null
                    )
                    DwLogger.log(INFO, "Recovering wallet with descriptors (name: $walletName)")
                        onAction(WalletCreateType.RECOVER(recoverWalletConfig))
                    }
                }
            )
        }
    }
}

@Composable
fun DescriptorInput(
    walletName: String,
    descriptor: String,
    changeDescriptor: String,
    selectedNetwork: MutableState<Network>,
    walletNameOnValueChange: (String) -> Unit,
    descriptorOnValueChange: (String) -> Unit,
    changeDescriptorOnValueChange: (String) -> Unit
) {
    Column(
        Modifier.padding(horizontal = 32.dp)
    ) {
        NetworkOptionsCard(
            selectedNetwork
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
                .padding(top = 16.dp),
            value = walletName,
            onValueChange = { walletNameOnValueChange(it) },
            label = {
                Text(
                    text = "Give your wallet a name",
                    style = standardText
                )
            },
            singleLine = true,
            textStyle = TextStyle(fontFamily = monoRegular, color = DevkitWalletColors.white),
            colors = OutlinedTextFieldDefaults.colors(
                cursorColor = DevkitWalletColors.accent1,
                focusedBorderColor = DevkitWalletColors.accent1,
                unfocusedBorderColor = DevkitWalletColors.white,
            ),
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            value = descriptor,
            onValueChange = { descriptorOnValueChange(it) },
            label = {
                Text(
                    text = "Input your descriptor here",
                    style = standardText
                )
            },
            singleLine = false,
            minLines = 5,
            textStyle = TextStyle(fontFamily = quattroRegular, fontSize = 12.sp, color = DevkitWalletColors.white),
            colors = OutlinedTextFieldDefaults.colors(
                cursorColor = DevkitWalletColors.accent1,
                focusedBorderColor = DevkitWalletColors.accent1,
                unfocusedBorderColor = DevkitWalletColors.white,
            ),
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            value = changeDescriptor,
            onValueChange = { changeDescriptorOnValueChange(it) },
            label = {
                Text(
                    text = "Input your change descriptor here",
                    style = standardText,
                )
            },
            singleLine = false,
            minLines = 5,
            textStyle = TextStyle(fontFamily = quattroRegular, fontSize = 12.sp, color = DevkitWalletColors.white),
            colors = OutlinedTextFieldDefaults.colors(
                cursorColor = DevkitWalletColors.accent1,
                focusedBorderColor = DevkitWalletColors.accent1,
                unfocusedBorderColor = DevkitWalletColors.white,
            ),
        )
    }
}

@Composable
fun RecoveryPhraseInput(recoveryPhrase: String, onValueChange: (String) -> Unit) {
    Column {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = recoveryPhrase,
            onValueChange = { onValueChange(it) },
            label = {
                Text(
                    text = "Input 12-word recovery phrase here",
                    style = standardText,
                )
            },
            singleLine = false,
            minLines = 5,
            textStyle = TextStyle(fontFamily = quattroRegular, fontSize = 12.sp, color = DevkitWalletColors.white),
            colors = OutlinedTextFieldDefaults.colors(
                cursorColor = DevkitWalletColors.accent1,
                focusedBorderColor = DevkitWalletColors.accent1,
                unfocusedBorderColor = DevkitWalletColors.white,
            ),
        )
    }
}

private fun parseRecoveryPhrase(recoveryPhrase: String): RecoveryPhraseValidationResult {
    val words = recoveryPhrase.trim().split(" ")if (words.size != 12){
        return RecoveryPhraseValidationResult.Invalid("Recovery phrase must have 12 words")
    }
    if (words.any { it !in bip39WordList }) {
        return RecoveryPhraseValidationResult.Invalid("Invalid word in recovery phrase")
    }
    return RecoveryPhraseValidationResult.ProbablyValid(recoveryPhrase)
}

sealed class RecoveryPhraseValidationResult {
    data class ProbablyValid(val recoveryPhrase: String) : RecoveryPhraseValidationResult()

    data class Invalid(val reason: String) : RecoveryPhraseValidationResult()
}

@Preview(device = Devices.PIXEL_4, showBackground = true)
@Composable
internal fun PreviewWalletRecoveryScreen() {
    RecoverWalletScreen(
        onAction = {},
        navController = rememberNavController()
    )
}
