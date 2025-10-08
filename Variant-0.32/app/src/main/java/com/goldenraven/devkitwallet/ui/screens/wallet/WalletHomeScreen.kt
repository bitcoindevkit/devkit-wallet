/*
 * Copyright 2020-2023 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package com.goldenraven.devkitwallet.ui.screens.wallet

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.goldenraven.devkitwallet.R
import com.goldenraven.devkitwallet.domain.Wallet
import com.goldenraven.devkitwallet.ui.Screen
import com.goldenraven.devkitwallet.ui.components.LoadingAnimation
import com.goldenraven.devkitwallet.ui.components.NeutralButton
import com.goldenraven.devkitwallet.ui.theme.DevkitWalletColors
import com.goldenraven.devkitwallet.ui.theme.jetBrainsMonoLight
import com.goldenraven.devkitwallet.ui.theme.jetBrainsMonoSemiBold
import com.goldenraven.devkitwallet.utils.formatInBtc
import com.goldenraven.devkitwallet.viewmodels.CurrencyUnit
import com.goldenraven.devkitwallet.viewmodels.WalletViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private const val TAG = "WalletHomeScreen"

@Composable
internal fun WalletHomeScreen(
    navController: NavHostController,
    drawerState: DrawerState,
    walletViewModel: WalletViewModel = viewModel(),
) {

    val networkAvailable: Boolean = isOnline(LocalContext.current)
    val syncing by walletViewModel.syncing.observeAsState(true)
    val balance by walletViewModel.balance.observeAsState()
    val unit by walletViewModel.unit.observeAsState()
    if (networkAvailable && !Wallet.isBlockChainCreated()) {
        Log.i(TAG, "Creating new blockchain")
        Wallet.createBlockchain()
    }

    val interactionSource = remember { MutableInteractionSource() }
    val scope: CoroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = { WalletAppBar(scope = scope, drawerState = drawerState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DevkitWalletColors.primary)
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.padding(24.dp))
            Row(
                Modifier
                    .clickable(
                        interactionSource,
                        indication = null,
                        onClick = { walletViewModel.switchUnit() }
                    )
                    .fillMaxWidth(0.9f)
                    .padding(horizontal = 8.dp)
                    .background(
                        color = DevkitWalletColors.primaryLight,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .height(100.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                when(unit) {
                    CurrencyUnit.Bitcoin, null -> {
                        Image(
                            painter = painterResource(id = R.drawable.ic_bitcoin_logo),
                            contentDescription = "Bitcoin testnet logo",
                            Modifier
                                .align(Alignment.CenterVertically)
                                .rotate(-13f)
                        )
                        Text(
                            text = balance.formatInBtc(),
                            fontFamily = jetBrainsMonoSemiBold,
                            fontSize = 32.sp,
                            color = DevkitWalletColors.white
                        )
                    }
                    CurrencyUnit.Satoshi -> {
                        Text(
                            text = "$balance sat",
                            fontFamily = jetBrainsMonoSemiBold,
                            fontSize = 32.sp,
                            color = DevkitWalletColors.white
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.padding(4.dp))
            Row(
                modifier = Modifier.height(40.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (syncing) LoadingAnimation()
            }

            if (!networkAvailable) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(color = DevkitWalletColors.accent2)
                        .height(50.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = "Network unavailable",
                        fontFamily = jetBrainsMonoLight,
                        fontSize = 18.sp,
                        color = DevkitWalletColors.white
                    )
                }
            }

            NeutralButton(
                text = "sync",
                enabled = networkAvailable,
                onClick = { walletViewModel.updateBalance() }
            )

            NeutralButton(
                text = "transaction history",
                enabled = networkAvailable,
                onClick = { navController.navigate(Screen.TransactionsScreen.route) }
            )

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .height(140.dp)
                    .fillMaxWidth(0.9f)
            ) {
                Button(
                    onClick = { navController.navigate(Screen.ReceiveScreen.route) },
                    colors = ButtonDefaults.buttonColors(DevkitWalletColors.accent1),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .height(160.dp)
                        .padding(vertical = 8.dp, horizontal = 8.dp)
                        .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp))
                ) {
                    Text(
                        text = "receive",
                        fontSize = 16.sp,
                        fontFamily = jetBrainsMonoLight,
                        textAlign = TextAlign.End,
                        lineHeight = 28.sp,
                        modifier = Modifier
                            .fillMaxWidth(0.4f)
                            .align(Alignment.Bottom)
                    )
                }

                Button(
                    onClick = { navController.navigate(Screen.SendScreen.route) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DevkitWalletColors.accent2,
                        disabledContainerColor = DevkitWalletColors.accent2,
                    ),
                    shape = RoundedCornerShape(16.dp),
                    enabled = networkAvailable,
                    modifier = Modifier
                        .height(160.dp)
                        .padding(vertical = 8.dp, horizontal = 8.dp)
                        .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp))
                ) {
                    Text(
                        text = "send",
                        fontSize = 16.sp,
                        fontFamily = jetBrainsMonoLight,
                        textAlign = TextAlign.End,
                        lineHeight = 28.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Bottom)
                    )
                }
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun WalletAppBar(scope: CoroutineScope, drawerState: DrawerState) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "BDK Sample Wallet",
                color = DevkitWalletColors.white,
                fontFamily = jetBrainsMonoSemiBold,
                fontSize = 20.sp,
            )
        },
        navigationIcon = {
            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                Icon(
                    imageVector = Icons.Rounded.Menu,
                    contentDescription = "Open drawer",
                    tint = DevkitWalletColors.white
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = DevkitWalletColors.primaryDark,
        )
    )
}

fun isOnline(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val capabilities =
        connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
    if (capabilities != null) {
        when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                return true
            }
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                return true
            }
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                return true
            }
        }
    }
    return false
}
