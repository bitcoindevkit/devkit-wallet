/*
 * Copyright 2021-2026 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.presentation.ui.screens.drawer

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.bitcoindevkit.devkitwallet.domain.DwLogger
import org.bitcoindevkit.devkitwallet.presentation.theme.DevkitWalletColors
import org.bitcoindevkit.devkitwallet.presentation.theme.standardText
import org.bitcoindevkit.devkitwallet.presentation.ui.components.SecondaryScreensAppBar

@Composable
fun LogsScreen(navController: NavController) {
    val logs: List<String> = remember { DwLogger.getLogs() }

    Scaffold(
        topBar = {
            SecondaryScreensAppBar(
                title = "Logs",
                navigation = { navController.popBackStack() },
            )
        },
        containerColor = DevkitWalletColors.primary,
    ) { paddingValues ->
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
        ) {
            items(logs) { logLine ->
                Text(
                    text = logLine,
                    style = standardText,
                    maxLines = 1,
                    overflow = TextOverflow.Visible,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                )
            }
        }
    }
}
