/*
 * Copyright 2021-2026 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.presentation.ui.screens.drawer

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.composables.icons.lucide.ClipboardCopy
import com.composables.icons.lucide.Lucide
import org.bitcoindevkit.devkitwallet.domain.WalletSecrets
import org.bitcoindevkit.devkitwallet.presentation.theme.DevkitWalletColors
import org.bitcoindevkit.devkitwallet.presentation.theme.googleSansCode
import org.bitcoindevkit.devkitwallet.presentation.theme.inter
import org.bitcoindevkit.devkitwallet.presentation.ui.components.NeutralButton
import org.bitcoindevkit.devkitwallet.presentation.ui.components.SecondaryScreensAppBar

private val MESSAGE: String =
    """
    The next screen will show your recovery phrase and descriptors. Make sure no one else is looking at your screen.
    """.trimIndent()

@Composable
internal fun RecoveryDataScreen(walletSecrets: WalletSecrets, navController: NavController) {
    val (currentIndex, setCurrentIndex) = remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            SecondaryScreensAppBar(
                title = "Your Wallet Recovery Data",
                navigation = { navController.popBackStack() },
            )
        },
        containerColor = DevkitWalletColors.primary,
    ) { paddingValues ->
        Crossfade(
            modifier = Modifier.padding(paddingValues),
            targetState = currentIndex,
            label = "",
            animationSpec =
                tween(
                    durationMillis = 1000,
                    delayMillis = 200,
                ),
        ) { screen ->
            when (screen) {
                0 -> WarningText(setCurrentIndex = setCurrentIndex)
                1 -> RecoveryPhrase(walletSecrets = walletSecrets)
            }
        }
    }
}

@Composable
fun WarningText(setCurrentIndex: (Int) -> Unit) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = MESSAGE,
            color = DevkitWalletColors.white,
            fontFamily = inter,
        )
        Spacer(modifier = Modifier.padding(16.dp))
        NeutralButton(
            text = "See my recovery data",
            enabled = true,
        ) { setCurrentIndex(1) }
    }
}

@Composable
fun RecoveryPhrase(walletSecrets: WalletSecrets) {
    val context = LocalContext.current
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(all = 32.dp),
    ) {
        Text(
            text = "Write down your recovery phrase and keep it in a safe place.",
            color = DevkitWalletColors.white,
            fontFamily = inter,
        )
        Spacer(modifier = Modifier.padding(8.dp))
        Box {
            SelectionContainer {
                Text(
                    modifier =
                        Modifier
                            .clickable {
                                simpleCopyClipboard(
                                    walletSecrets.recoveryPhrase,
                                    context,
                                )
                            }.background(
                                color = DevkitWalletColors.primaryLight,
                                shape = RoundedCornerShape(16.dp),
                            ).padding(12.dp),
                    text = walletSecrets.recoveryPhrase,
                    fontFamily = googleSansCode,
                    color = DevkitWalletColors.white,
                )
            }
            Icon(
                Lucide.ClipboardCopy,
                tint = Color.White,
                contentDescription = "Copy to clipboard",
                modifier =
                    Modifier
                        .padding(8.dp)
                        .size(20.dp)
                        .align(Alignment.BottomEnd),
            )
        }
        Spacer(modifier = Modifier.padding(16.dp))
        Text(
            text = "These are your descriptors.",
            color = DevkitWalletColors.white,
            fontFamily = inter,
        )
        Spacer(modifier = Modifier.padding(8.dp))
        Box {
            SelectionContainer {
                Text(
                    modifier =
                        Modifier
                            .clickable {
                                simpleCopyClipboard(
                                    walletSecrets.descriptor.toStringWithSecret(),
                                    context,
                                )
                            }.background(
                                color = DevkitWalletColors.primaryLight,
                                shape = RoundedCornerShape(16.dp),
                            ).padding(12.dp),
                    text = walletSecrets.descriptor.toStringWithSecret(),
                    fontFamily = googleSansCode,
                    color = DevkitWalletColors.white,
                )
            }
            Icon(
                Lucide.ClipboardCopy,
                tint = Color.White,
                contentDescription = "Copy to clipboard",
                modifier =
                    Modifier
                        .padding(8.dp)
                        .size(20.dp)
                        .align(Alignment.BottomEnd),
            )
        }
        Spacer(modifier = Modifier.padding(4.dp))
        Box {
            SelectionContainer {
                Text(
                    modifier =
                        Modifier
                            .clickable {
                                simpleCopyClipboard(
                                    walletSecrets.changeDescriptor.toStringWithSecret(),
                                    context,
                                )
                            }.background(
                                color = DevkitWalletColors.primaryLight,
                                shape = RoundedCornerShape(16.dp),
                            ).padding(12.dp),
                    text = walletSecrets.changeDescriptor.toStringWithSecret(),
                    fontFamily = googleSansCode,
                    color = DevkitWalletColors.white,
                )
            }
            Icon(
                Lucide.ClipboardCopy,
                tint = Color.White,
                contentDescription = "Copy to clipboard",
                modifier =
                    Modifier
                        .padding(8.dp)
                        .size(20.dp)
                        .align(Alignment.BottomEnd),
            )
        }
    }
}

fun simpleCopyClipboard(content: String, context: Context) {
    val clipboard: ClipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip: ClipData = ClipData.newPlainText("", content)
    clipboard.setPrimaryClip(clip)
}

// @Preview(device = Devices.PIXEL_4, showBackground = true)
// @Composable
// internal fun PreviewRecoveryPhraseScreen() {
//     RecoveryPhraseScreen()
// }
