/*
 * Copyright 2021-2026 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.presentation.ui.screens.wallet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.bitcoindevkit.devkitwallet.presentation.navigation.RbfScreen
import org.bitcoindevkit.devkitwallet.presentation.theme.inter
import org.bitcoindevkit.devkitwallet.presentation.ui.components.SecondaryScreensAppBar

@Composable
internal fun TransactionScreen(txid: String?, navController: NavController) {
    val colorScheme = MaterialTheme.colorScheme

    Scaffold(
        topBar = {
            SecondaryScreensAppBar(
                title = "Transaction Details",
                navigation = { navController.navigateUp() },
            )
        },
        containerColor = colorScheme.surface,
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(48.dp))

            Text(
                text = "Transaction",
                color = colorScheme.onSurface,
                fontSize = 28.sp,
                fontFamily = inter,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.weight(1f))

            TransactionDetailButton(
                content = "increase fees",
                navController = navController,
                txid = txid,
            )

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
fun TransactionDetailButton(content: String, navController: NavController, txid: String?) {
    val colorScheme = MaterialTheme.colorScheme
    Button(
        onClick = {
            when (content) {
                "increase fees" -> {
                    navController.navigate(RbfScreen(txid!!))
                }

                "back to transaction list" -> {
                    navController.navigateUp()
                }
            }
        },
        colors = ButtonDefaults.buttonColors(colorScheme.secondary),
        shape = RoundedCornerShape(16.dp),
        modifier =
            Modifier
                .height(52.dp)
                .fillMaxWidth(),
    ) {
        Text(
            text = content,
            fontSize = 14.sp,
            fontFamily = inter,
            textAlign = TextAlign.Center,
        )
    }
}
