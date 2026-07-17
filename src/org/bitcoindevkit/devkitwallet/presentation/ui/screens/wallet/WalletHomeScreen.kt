/*
 * Copyright 2021-2026 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE.txt file.
 */

package org.bitcoindevkit.devkitwallet.presentation.ui.screens.wallet

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.composables.icons.lucide.ArrowDownLeft
import com.composables.icons.lucide.ArrowUpRight
import com.composables.icons.lucide.History
import com.composables.icons.lucide.List
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Monitor
import com.composables.icons.lucide.Settings
import com.composables.icons.lucide.Shield
import kotlinx.coroutines.flow.Flow
import org.bitcoindevkit.Network
import org.bitcoindevkit.devkitwallet.domain.CurrencyUnit
import org.bitcoindevkit.devkitwallet.domain.utils.formatInBtc
import org.bitcoindevkit.devkitwallet.presentation.navigation.BlockchainClientScreen
import org.bitcoindevkit.devkitwallet.presentation.navigation.ReceiveScreen
import org.bitcoindevkit.devkitwallet.presentation.navigation.RecoveryPhraseScreen
import org.bitcoindevkit.devkitwallet.presentation.navigation.SendScreen
import org.bitcoindevkit.devkitwallet.presentation.navigation.SettingsScreen
import org.bitcoindevkit.devkitwallet.presentation.navigation.TransactionHistoryScreen
import org.bitcoindevkit.devkitwallet.presentation.theme.DayGlowHistoryAccent
import org.bitcoindevkit.devkitwallet.presentation.theme.NightGlowSubtle
import org.bitcoindevkit.devkitwallet.presentation.theme.inter
import org.bitcoindevkit.devkitwallet.presentation.ui.components.CustomSnackbar
import org.bitcoindevkit.devkitwallet.presentation.viewmodels.mvi.WalletScreenAction
import org.bitcoindevkit.devkitwallet.presentation.viewmodels.mvi.WalletScreenState

private const val TAG = "WalletHomeScreen"

/**
 * Main wallet dashboard displaying balance, network label, and quick-action cards.
 *
 * Tapping the balance toggles between BTC and satoshi display. Snackbar messages (e.g. new block notifications) are
 * shown via a [CustomSnackbar].
 */
@Composable
internal fun WalletHomeScreen(
    state: WalletScreenState,
    onAction: (WalletScreenAction) -> Unit,
    snackbarMessages: Flow<String>,
    navController: NavHostController,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val networkAvailable: Boolean = isOnline(LocalContext.current)
    val interactionSource = remember { MutableInteractionSource() }
    val colorScheme = MaterialTheme.colorScheme

    LaunchedEffect(Unit) {
        onAction(WalletScreenAction.UpdateBalance)
    }

    LaunchedEffect(Unit) {
        snackbarMessages.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        topBar = { WalletAppBar(onSettingsClick = { navController.navigate(SettingsScreen) }) },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                CustomSnackbar(data)
            }
        },
    ) { paddingValues ->
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .padding(paddingValues)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = { onAction(WalletScreenAction.SwitchUnit) },
                    ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(32.dp))

            // Balance
            when (state.unit) {
                CurrencyUnit.Bitcoin -> {
                    Text(
                        text = state.balance.formatInBtc(),
                        fontFamily = inter,
                        // fontFamily = googleSansCode,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Light,
                        color = colorScheme.onSurface,
                    )
                }

                CurrencyUnit.Satoshi -> {
                    Text(
                        text = "${state.balance} sat",
                        fontFamily = inter,
                        // fontFamily = googleSansCode,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Light,
                        color = colorScheme.onSurface,
                    )
                }
            }
            Text(
                text = state.network.asHomeLabel(),
                fontSize = 14.sp,
                color = NightGlowSubtle,
                letterSpacing = 2.sp,
            )

            Spacer(Modifier.height(24.dp))

            // Receive / Send row
            Row(
                modifier = Modifier.fillMaxWidth(0.9f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // Receive card
                OutlinedCard(
                    onClick = { navController.navigate(ReceiveScreen) },
                    modifier = Modifier.weight(1f).height(120.dp),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.5.dp, colorScheme.outline.copy(alpha = 0.15f)),
                    colors = CardDefaults.outlinedCardColors(containerColor = Color.Transparent),
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Box(
                            modifier =
                                Modifier.size(52.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(colorScheme.onSurfaceVariant.copy(alpha = 0.08f))
                                    .border(
                                        width = 1.dp,
                                        color = colorScheme.onSurfaceVariant.copy(alpha = 0.12f),
                                        shape = RoundedCornerShape(16.dp),
                                    ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Lucide.ArrowDownLeft,
                                contentDescription = "Receive",
                                tint = colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(24.dp),
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "RECEIVE",
                            color = colorScheme.onSurfaceVariant,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 1.sp,
                        )
                    }
                }

                // Send card
                OutlinedCard(
                    onClick = { navController.navigate(SendScreen) },
                    enabled = networkAvailable,
                    modifier = Modifier.weight(1f).height(120.dp),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.5.dp, colorScheme.outline.copy(alpha = 0.15f)),
                    colors = CardDefaults.outlinedCardColors(containerColor = Color.Transparent),
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Box(
                            modifier =
                                Modifier.size(52.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(colorScheme.onSurfaceVariant.copy(alpha = 0.08f))
                                    .border(
                                        width = 1.dp,
                                        color = colorScheme.onSurfaceVariant.copy(alpha = 0.12f),
                                        shape = RoundedCornerShape(16.dp),
                                    ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Lucide.ArrowUpRight,
                                contentDescription = "Send",
                                tint = colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(24.dp),
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "SEND",
                            color = colorScheme.onSurfaceVariant,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 1.sp,
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(
                thickness = 1.dp,
                color = colorScheme.outline.copy(alpha = 0.08f),
                modifier = Modifier.fillMaxWidth(0.9f),
            )
            Spacer(Modifier.height(16.dp))

            // Quick actions row
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                QuickAction(
                    icon = Lucide.List,
                    label = "UTXOs",
                    tint = colorScheme.primary,
                    onClick = {},
                )
                QuickAction(
                    icon = Lucide.Shield,
                    label = "Security",
                    tint = colorScheme.secondary,
                    onClick = { navController.navigate(RecoveryPhraseScreen) },
                )
                QuickAction(
                    icon = Lucide.Monitor,
                    label = "Node",
                    tint = colorScheme.tertiary,
                    onClick = { navController.navigate(BlockchainClientScreen) },
                )
                QuickAction(
                    icon = Lucide.History,
                    label = "History",
                    tint = DayGlowHistoryAccent,
                    onClick = { navController.navigate(TransactionHistoryScreen) },
                )
            }

            // Network unavailable banner
            if (!networkAvailable) {
                Spacer(Modifier.height(16.dp))
                Row(
                    Modifier.fillMaxWidth().background(color = colorScheme.primary.copy(alpha = 0.12f)).height(40.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = "Network unavailable",
                        fontFamily = inter,
                        fontSize = 14.sp,
                        color = colorScheme.onSurface,
                    )
                }
            }
        }
    }
}

/** Converts a [Network] into the uppercase label shown below the balance on [WalletHomeScreen]. */
private fun Network.asHomeLabel(): String =
    when (this) {
        Network.BITCOIN -> "BITCOIN"
        Network.TESTNET -> "TESTNET BITCOIN"
        Network.TESTNET4 -> "TESTNET 4 BITCOIN"
        Network.SIGNET -> "SIGNET BITCOIN"
        Network.REGTEST -> "REGTEST BITCOIN"
    }

/** Circular icon button with a label used in the quick-actions row on [WalletHomeScreen]. */
@Composable
private fun QuickAction(icon: ImageVector, label: String, tint: Color, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick).padding(horizontal = 4.dp),
    ) {
        Box(
            modifier =
                Modifier.size(56.dp)
                    .border(
                        width = 1.5.dp,
                        color = tint.copy(alpha = 0.20f),
                        shape = CircleShape,
                    ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(22.dp),
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = NightGlowSubtle,
        )
    }
}

/** Top app bar for the wallet home screen; contains only a settings icon action. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun WalletAppBar(onSettingsClick: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "",
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = inter,
                fontSize = 20.sp,
            )
        },
        actions = {
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Lucide.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
    )
}

/** Checks whether the device currently has an active data connection (cellular, Wi-Fi, or Ethernet). */
fun isOnline(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
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
