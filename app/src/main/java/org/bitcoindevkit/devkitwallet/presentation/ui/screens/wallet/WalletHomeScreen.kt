/*
 * Copyright 2021-2024 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.presentation.ui.screens.wallet

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CurrencyBitcoin
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Menu
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.bitcoindevkit.devkitwallet.domain.CurrencyUnit
import org.bitcoindevkit.devkitwallet.domain.utils.formatInBtc
import org.bitcoindevkit.devkitwallet.presentation.navigation.ReceiveScreen
import org.bitcoindevkit.devkitwallet.presentation.navigation.SendScreen
import org.bitcoindevkit.devkitwallet.presentation.navigation.TransactionHistoryScreen
import org.bitcoindevkit.devkitwallet.presentation.theme.DevkitWalletColors
import org.bitcoindevkit.devkitwallet.presentation.theme.monoRegular
import org.bitcoindevkit.devkitwallet.presentation.theme.quattroBold
import org.bitcoindevkit.devkitwallet.presentation.ui.components.NeutralButton
import org.bitcoindevkit.devkitwallet.presentation.viewmodels.mvi.WalletScreenAction
import org.bitcoindevkit.devkitwallet.presentation.viewmodels.mvi.WalletScreenState

private const val TAG = "WalletHomeScreen"

@Composable
internal fun WalletHomeScreen(
    state: WalletScreenState,
    onAction: (WalletScreenAction) -> Unit,
    drawerState: DrawerState,
    navController: NavHostController,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val networkAvailable: Boolean = isOnline(LocalContext.current)
    val interactionSource = remember { MutableInteractionSource() }
    val scope: CoroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        onAction(WalletScreenAction.UpdateBalance)
    }

    Scaffold(
        topBar = { WalletAppBar(scope = scope, drawerState = drawerState) },
        containerColor = DevkitWalletColors.primary,
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->

        // If a new snackbar has be triggered, show it
        state.snackbarMessage?.let { message ->
            Log.i("WalletHomeScreen", "Showing snackbar: $message")
            LaunchedEffect(message) {
                scope.launch {
                    snackbarHostState.showSnackbar(message)
                    onAction(WalletScreenAction.ClearSnackbar)
                }
            }
        }

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.padding(24.dp))
            Row(
                Modifier
                    .clickable(
                        interactionSource,
                        indication = null,
                        onClick = { onAction(WalletScreenAction.SwitchUnit) },
                    ).fillMaxWidth(0.9f)
                    .padding(horizontal = 8.dp)
                    .background(
                        color = DevkitWalletColors.primaryLight,
                        shape = RoundedCornerShape(16.dp),
                    ).height(100.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                when (state.unit) {
                    CurrencyUnit.Bitcoin -> {
                        Icon(
                            imageVector = Icons.Rounded.CurrencyBitcoin,
                            tint = DevkitWalletColors.white,
                            contentDescription = "Bitcoin testnet logo",
                            modifier =
                                Modifier
                                    .align(Alignment.CenterVertically)
                                    .size(48.dp),
                        )
                        Text(
                            text = state.balance.formatInBtc(),
                            fontFamily = monoRegular,
                            fontSize = 32.sp,
                            color = DevkitWalletColors.white,
                        )
                    }
                    CurrencyUnit.Satoshi -> {
                        Text(
                            text = "${state.balance} sat",
                            fontFamily = monoRegular,
                            fontSize = 32.sp,
                            color = DevkitWalletColors.white,
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.padding(4.dp))
            if (networkAvailable) {
                Row(
                    modifier = Modifier.height(40.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // if (state.syncing) LoadingAnimation()
                }
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
                        fontFamily = monoRegular,
                        fontSize = 16.sp,
                        color = DevkitWalletColors.white,
                    )
                }
            }

            NeutralButton(
                text = "transaction history",
                enabled = networkAvailable,
                onClick = { navController.navigate(TransactionHistoryScreen) },
            )

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier =
                    Modifier
                        .height(140.dp)
                        .fillMaxWidth(0.9f),
            ) {
                Button(
                    onClick = { navController.navigate(ReceiveScreen) },
                    colors = ButtonDefaults.buttonColors(DevkitWalletColors.accent1),
                    shape = RoundedCornerShape(16.dp),
                    modifier =
                        Modifier
                            .height(160.dp)
                            .padding(vertical = 8.dp, horizontal = 8.dp)
                            .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp)),
                ) {
                    Text(
                        text = "receive",
                        fontSize = 16.sp,
                        textAlign = TextAlign.End,
                        lineHeight = 28.sp,
                        modifier =
                            Modifier
                                .fillMaxWidth(0.4f)
                                .align(Alignment.Bottom),
                    )
                }

                Button(
                    onClick = { navController.navigate(SendScreen) },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = DevkitWalletColors.accent2,
                            disabledContainerColor = DevkitWalletColors.accent2,
                        ),
                    shape = RoundedCornerShape(16.dp),
                    enabled = networkAvailable,
                    modifier =
                        Modifier
                            .height(160.dp)
                            .padding(vertical = 8.dp, horizontal = 8.dp)
                            .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp)),
                ) {
                    Text(
                        text = "send",
                        fontSize = 16.sp,
                        textAlign = TextAlign.End,
                        lineHeight = 28.sp,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .align(Alignment.Bottom),
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
                text = "Devkit Wallet",
                color = DevkitWalletColors.white,
                // fontFamily = quattroRegular,
                fontFamily = quattroBold,
                fontSize = 20.sp,
            )
        },
        navigationIcon = {
            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                Icon(
                    imageVector = Lucide.Menu,
                    contentDescription = "Open drawer",
                    tint = DevkitWalletColors.white,
                )
            }
        },
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = DevkitWalletColors.primaryDark,
            ),
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
