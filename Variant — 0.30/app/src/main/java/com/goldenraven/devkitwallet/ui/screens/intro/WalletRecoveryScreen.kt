/*
 * Copyright 2020-2023 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package com.goldenraven.devkitwallet.ui.screens.intro

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import com.goldenraven.devkitwallet.WalletCreateType
import com.goldenraven.devkitwallet.ui.components.SecondaryScreensAppBar
import com.goldenraven.devkitwallet.ui.theme.DevkitWalletColors
import com.goldenraven.devkitwallet.ui.theme.jetBrainsMonoLight

@Composable
internal fun WalletRecoveryScreen(
    navController: NavController,
    onBuildWalletButtonClicked: (WalletCreateType) -> Unit
) {
    Scaffold(
        topBar = {
            SecondaryScreensAppBar(title = "Recover a Wallet", navigation = { navController.popBackStack() })
        }
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


            // the app name
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .background(DevkitWalletColors.primary)
                    .constrainAs(screenTitle) {
                        top.linkTo(parent.top)
                    }
            ) {
                Column {
                    Text(
                        text = "Enter your 12-word recovery phrase to recover an existing wallet.",
                        color = DevkitWalletColors.white,
                        fontSize = 14.sp,
                        fontFamily = jetBrainsMonoLight,
                        modifier = Modifier
                            .padding(top = 70.dp, bottom = 8.dp, start = 16.dp, end = 16.dp)
                            .align(Alignment.CenterHorizontally)
                    )
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
                        // bottom.linkTo(button.top)
                        height = Dimension.fillToConstraints
                    },
                onClick = {
                    onBuildWalletButtonClicked(WalletCreateType.RECOVER(buildRecoveryPhrase(recoveryPhraseWordMap)))
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
            .background(DevkitWalletColors.primary)
            .verticalScroll(state = scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val focusManager = LocalFocusManager.current
        for (i in 1..12) {
            WordField(wordNumber = i, recoveryPhraseWordMap, setRecoveryPhraseWordMap, focusManager)
        }
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(DevkitWalletColors.secondary),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .size(width = 300.dp, height = 100.dp)
                .padding(vertical = 8.dp, horizontal = 8.dp)
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp))
        ) {
            Text(
                text = "Recover Wallet",
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                lineHeight = 28.sp,
            )
        }
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
            fontSize = 18.sp,
            color = DevkitWalletColors.white
        ),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = DevkitWalletColors.accent1,
            unfocusedBorderColor = DevkitWalletColors.white,
            cursorColor = DevkitWalletColors.accent1,
        ),
        modifier = Modifier
            .padding(8.dp),
        keyboardOptions = when (wordNumber) {
            12 -> KeyboardOptions(imeAction = ImeAction.Done)
            else -> KeyboardOptions(imeAction = ImeAction.Next)
        },
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) },
            onDone = { focusManager.clearFocus() }
        ),
        singleLine = true,
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
