/*
 * Copyright 2020-2023 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package com.goldenraven.devkitwallet.ui.screens.wallet

import android.util.Log
import androidx.compose.foundation.background
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
import com.goldenraven.devkitwallet.domain.Wallet
import com.goldenraven.devkitwallet.ui.Screen
import com.goldenraven.devkitwallet.ui.components.SecondaryScreensAppBar
import com.goldenraven.devkitwallet.ui.components.ConfirmedTransactionCard
import com.goldenraven.devkitwallet.ui.components.PendingTransactionCard
import com.goldenraven.devkitwallet.ui.theme.DevkitWalletColors
import com.goldenraven.devkitwallet.utils.timestampToString
import org.bitcoindevkit.TransactionDetails

private const val TAG = "TransactionsScreen"

@Composable
internal fun TransactionsScreen(navController: NavController) {
    val allTransactions: List<TransactionDetails> = Wallet.getAllTransactions()
    val (confirmedTransactions, unconfirmedTransactions) = sortTransactions(allTransactions)

    Scaffold(
        topBar = {
            SecondaryScreensAppBar(
                title = "Transactions History",
                navigation = { navController.navigate(Screen.HomeScreen.route) }
            )
        }
    ) { paddingValues ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(DevkitWalletColors.primary)
                .padding(top = 6.dp)
                .verticalScroll(state = scrollState)
        ) {
            if (unconfirmedTransactions.isNotEmpty()) {
                unconfirmedTransactions.forEach {
                    PendingTransactionCard(details = it, navController = navController)
                }
            }
            if (confirmedTransactions.isNotEmpty()) {
                confirmedTransactions.forEach {
                    ConfirmedTransactionCard(it, navController)
                }
            }
        }
    }
}

fun viewTransaction(navController: NavController, txid: String) {
    navController.navigate("${Screen.TransactionScreen.route}/txid=$txid")
}

private fun sortTransactions(transactions: List<TransactionDetails>): Transactions {
    val confirmedTransactions = mutableListOf<TransactionDetails>()
    val unconfirmedTransactions = mutableListOf<TransactionDetails>()
    transactions.forEach { tx ->
        if (tx.confirmationTime != null) confirmedTransactions.add(tx) else unconfirmedTransactions.add(tx)
    }
    return Transactions(
        confirmedTransactions = confirmedTransactions,
        unconfirmedTransactions = unconfirmedTransactions
    )
}

fun pendingTransactionsItem(transaction: TransactionDetails): String {
    return buildString {
        Log.i(TAG, "Pending transaction list item: $transaction")
        appendLine("Timestamp: Pending")
        appendLine("Received: ${transaction.received}")
        appendLine("Sent: ${transaction.sent}")
        appendLine("Fees: ${transaction.fee}")
        append("Txid: ${transaction.txid.take(n = 8)}...${transaction.txid.takeLast(n = 8)}")
    }
}

fun confirmedTransactionsItem(transaction: TransactionDetails): String {
    return buildString {
        Log.i(TAG, "Transaction list item: $transaction")
        appendLine("Timestamp: ${transaction.confirmationTime!!.timestamp.timestampToString()}")
        appendLine("Received: ${transaction.received}")
        appendLine("Sent: ${transaction.sent}")
        appendLine("Block: ${transaction.confirmationTime!!.height}")
        append("Txid: ${transaction.txid.take(n = 8)}...${transaction.txid.takeLast(n = 8)}")
    }
}

data class Transactions(
    val confirmedTransactions: List<TransactionDetails>,
    val unconfirmedTransactions: List<TransactionDetails>
)

// @Preview(device = Devices.PIXEL_4, showBackground = true)
// @Composable
// internal fun PreviewTransactionsScreen() {
//     TransactionsScreen(rememberNavController())
// }
