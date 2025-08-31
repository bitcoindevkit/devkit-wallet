package com.goldenraven.devkitwallet.ui.components

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.goldenraven.devkitwallet.ui.screens.wallet.confirmedTransactionsItem
import com.goldenraven.devkitwallet.ui.screens.wallet.pendingTransactionsItem
import com.goldenraven.devkitwallet.ui.screens.wallet.viewTransaction
import com.goldenraven.devkitwallet.ui.theme.DevkitWalletColors
import com.goldenraven.devkitwallet.ui.theme.jetBrainsMonoLight
import org.bitcoindevkit.TransactionDetails

@Composable
fun ConfirmedTransactionCard(details: TransactionDetails, navController: NavController) {
    Row(
        Modifier
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .fillMaxWidth()
            .background(
                color = DevkitWalletColors.primaryLight,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { viewTransaction(navController = navController, txid = details.txid) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Absolute.SpaceBetween
    ) {
        Text(
            confirmedTransactionsItem(details),
            fontFamily = jetBrainsMonoLight,
            fontSize = 12.sp,
            color = DevkitWalletColors.white,
            modifier = Modifier.padding(16.dp)
        )
        Box(
            modifier = Modifier
                .padding(top = 16.dp, end = 16.dp)
                .size(size = 24.dp)
                .clip(shape = CircleShape)
                .background(DevkitWalletColors.secondary)
                .align(Alignment.Top)
        )
    }
}

@Composable
fun PendingTransactionCard(details: TransactionDetails, navController: NavController) {
    Row(
        Modifier
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .fillMaxWidth()
            .background(
                color = DevkitWalletColors.primaryLight,
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 2.dp,
                color = DevkitWalletColors.accent1,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable {
                viewTransaction(
                    navController = navController,
                    txid = details.txid
                )
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Absolute.SpaceBetween
    ) {
        Text(
            pendingTransactionsItem(details),
            fontFamily = jetBrainsMonoLight,
            fontSize = 12.sp,
            color = DevkitWalletColors.white,
            modifier = Modifier.padding(16.dp)
        )
        Box(
            modifier = Modifier
                .padding(top = 16.dp, end = 16.dp)
                .size(size = 24.dp)
                .clip(shape = CircleShape)
                .background(Color(0xffE9C46A))
                .align(Alignment.Top)
        )
    }
}
