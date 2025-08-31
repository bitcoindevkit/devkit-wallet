/*
 * Copyright 2021-2024 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.presentation.ui.screens.wallet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.bitcoindevkit.devkitwallet.domain.Wallet
import org.bitcoindevkit.devkitwallet.presentation.navigation.HomeScreen
import org.bitcoindevkit.devkitwallet.presentation.navigation.TransactionScreen
import org.bitcoindevkit.devkitwallet.presentation.theme.DevkitWalletColors
import org.bitcoindevkit.devkitwallet.presentation.ui.components.ConfirmedTransactionCard
import org.bitcoindevkit.devkitwallet.presentation.ui.components.PendingTransactionCard
import org.bitcoindevkit.devkitwallet.presentation.ui.components.SecondaryScreensAppBar

private const val TAG = "TransactionHistoryScreen"

@Composable
internal fun TransactionHistoryScreen(navController: NavController, activeWallet: Wallet) {
    val (pendingTransactions, confirmedTransactions) = activeWallet.getAllTxDetails().partition { it.pending }

    Scaffold(
        topBar = {
            SecondaryScreensAppBar(
                title = "Transaction History",
                navigation = { navController.navigate(HomeScreen) },
            )
        },
        containerColor = DevkitWalletColors.primary,
    ) { paddingValues ->
        val scrollState = rememberScrollState()
        Column(
            modifier =
                Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(top = 6.dp)
                    .verticalScroll(state = scrollState),
        ) {
            if (pendingTransactions.isNotEmpty()) {
                pendingTransactions.forEach {
                    PendingTransactionCard(details = it, navController = navController)
                }
            }
            if (confirmedTransactions.isNotEmpty()) {
                confirmedTransactions.sortedBy { it.confirmationBlock?.height }.forEach {
                    ConfirmedTransactionCard(it, navController)
                }
            }
        }
    }
}

fun viewTransaction(navController: NavController, txid: String) {
    navController.navigate(TransactionScreen(txid))
}

// @Preview(device = Devices.PIXEL_4, showBackground = true)
// @Composable
// internal fun PreviewTransactionsScreen() {
//     TransactionsScreen(rememberNavController())
// }
