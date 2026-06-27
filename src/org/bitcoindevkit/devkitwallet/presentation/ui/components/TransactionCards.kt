/*
 * Copyright 2021-2026 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.presentation.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.bitcoindevkit.devkitwallet.data.TxDetails
import org.bitcoindevkit.devkitwallet.domain.utils.timestampToString
import org.bitcoindevkit.devkitwallet.presentation.theme.DayGlowHistoryAccent
import org.bitcoindevkit.devkitwallet.presentation.theme.inter
import org.bitcoindevkit.devkitwallet.presentation.ui.screens.wallet.viewTransaction

private const val TAG = "TransactionCards"

@Composable
fun ConfirmedTransactionCard(details: TxDetails, navController: NavController) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        Modifier
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .fillMaxWidth()
            .background(
                color = colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(16.dp),
            ).border(
                width = 1.dp,
                color = colorScheme.outline.copy(alpha = 0.08f),
                shape = RoundedCornerShape(16.dp),
            ).clickable { viewTransaction(navController = navController, txid = details.txid) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Absolute.SpaceBetween,
    ) {
        Text(
            confirmedTransactionsItem(details),
            fontFamily = inter,
            fontSize = 12.sp,
            lineHeight = 20.sp,
            color = colorScheme.onSurface,
            modifier = Modifier.padding(16.dp),
        )
        Box(
            modifier =
                Modifier
                    .padding(top = 16.dp, end = 16.dp)
                    .size(24.dp)
                    .clip(shape = CircleShape)
                    .background(colorScheme.tertiary.copy(alpha = 0.6f))
                    .align(Alignment.Top),
        )
    }
}

@Composable
fun PendingTransactionCard(details: TxDetails, navController: NavController) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        Modifier
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .fillMaxWidth()
            .background(
                color = colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(16.dp),
            ).border(
                width = 1.5.dp,
                color = DayGlowHistoryAccent.copy(alpha = 0.5f),
                shape = RoundedCornerShape(16.dp),
            ).clickable {
                viewTransaction(
                    navController = navController,
                    txid = details.txid,
                )
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Absolute.SpaceBetween,
    ) {
        Text(
            pendingTransactionsItem(details),
            fontFamily = inter,
            fontSize = 12.sp,
            color = colorScheme.onSurface,
            modifier = Modifier.padding(16.dp),
        )
        Box(
            modifier =
                Modifier
                    .padding(top = 16.dp, end = 16.dp)
                    .size(24.dp)
                    .clip(shape = CircleShape)
                    .background(DayGlowHistoryAccent.copy(alpha = 0.6f))
                    .align(Alignment.Top),
        )
    }
}

fun pendingTransactionsItem(txDetails: TxDetails): String {
    return buildString {
        Log.i(TAG, "Pending transaction list item: $txDetails")

        appendLine("Confirmation time: Pending")
        appendLine("Received: ${txDetails.received}")
        appendLine("Sent: ${txDetails.sent}")
        appendLine("Total fee: ${txDetails.fee} sat")
        appendLine("Fee rate: ${txDetails.feeRate?.toSatPerVbCeil() ?: 0} sat/vbyte")
        append("Txid: ${txDetails.txid.take(n = 8)}...${txDetails.txid.takeLast(n = 8)}")
    }
}

fun confirmedTransactionsItem(txDetails: TxDetails): String {
    return buildString {
        Log.i(TAG, "Transaction list item: $txDetails")

        appendLine("Confirmation time: ${txDetails.confirmationTimestamp?.timestamp?.timestampToString()}")
        appendLine("Received: ${txDetails.received} sat")
        appendLine("Sent: ${txDetails.sent} sat")
        appendLine("Total fee: ${txDetails.fee} sat")
        appendLine("Fee rate: ${txDetails.feeRate?.toSatPerVbCeil() ?: 0} sat/vbyte")
        appendLine("Block: ${txDetails.confirmationBlock?.height}")
        append("Txid: ${txDetails.txid.take(n = 8)}...${txDetails.txid.takeLast(n = 8)}")
    }
}
