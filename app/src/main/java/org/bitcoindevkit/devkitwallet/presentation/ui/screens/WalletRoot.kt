/*
 * Copyright 2021-2024 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.presentation.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationDrawerItemDefaults.colors
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import org.bitcoindevkit.devkitwallet.BuildConfig
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.composables.icons.lucide.History
import com.composables.icons.lucide.Info
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Satellite
import com.composables.icons.lucide.SatelliteDish
import com.composables.icons.lucide.ScrollText
import org.bitcoindevkit.devkitwallet.presentation.theme.DevkitWalletColors
import org.bitcoindevkit.devkitwallet.presentation.navigation.WalletNavigation
import org.bitcoindevkit.devkitwallet.R
import org.bitcoindevkit.devkitwallet.domain.Wallet
import org.bitcoindevkit.devkitwallet.presentation.navigation.AboutScreen
import org.bitcoindevkit.devkitwallet.presentation.navigation.BlockchainClientScreen
import org.bitcoindevkit.devkitwallet.presentation.navigation.LogsScreen
import org.bitcoindevkit.devkitwallet.presentation.navigation.RecoveryPhraseScreen
import org.bitcoindevkit.devkitwallet.presentation.theme.devkitTypography
import org.bitcoindevkit.devkitwallet.presentation.theme.quattroRegular
import org.bitcoindevkit.devkitwallet.presentation.theme.standardText
import org.bitcoindevkit.devkitwallet.presentation.viewmodels.WalletViewModel

@OptIn(androidx.compose.animation.ExperimentalAnimationApi::class)
@Composable
internal fun WalletRoot(
    activeWallet: Wallet,
    walletViewModel: WalletViewModel,
    navController: NavController,
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    val items = listOf(Icons.Default.Favorite, Icons.Default.Face, Icons.Default.Email, Icons.Default.Face)
    val selectedItem = remember { mutableStateOf(items[0]) }

    val navigationItemColors = colors(
        selectedContainerColor = DevkitWalletColors.primary,
        unselectedContainerColor = DevkitWalletColors.primary,
        selectedTextColor = DevkitWalletColors.white,
        unselectedTextColor = DevkitWalletColors.white
    )

    ModalNavigationDrawer (
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = DevkitWalletColors.primary
            ) {
                Column(
                    Modifier
                        .background(color = DevkitWalletColors.secondary)
                        .height(300.dp)
                        .fillMaxHeight()
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_testnet_logo),
                        contentDescription = "Bitcoin testnet logo",
                        Modifier
                            .size(90.dp)
                            .padding(bottom = 16.dp)
                    )
                    Text(
                        text = "Devkit Wallet",
                        color = DevkitWalletColors.white,
                        fontFamily = quattroRegular,
                    )
                    Spacer(modifier = Modifier.padding(4.dp))
                    Text(
                        text = "The sample wallet on Android for BDK.",
                        color = DevkitWalletColors.white,
                        fontFamily = quattroRegular,
                        fontSize = 12.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                    Spacer(modifier = Modifier.padding(16.dp))
                    Text(
                        text = BuildConfig.VARIANT_NAME,
                        style = standardText
                    )
                }
                Column(
                    Modifier
                        .fillMaxHeight()
                        .background(color = DevkitWalletColors.primary)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    NavigationDrawerItem(
                        icon = { Icon(Lucide.Info, contentDescription = "About", tint = DevkitWalletColors.white) },
                        label = { DrawerItemLabel("About") },
                        selected = items[0] == selectedItem.value,
                        onClick = { navController.navigate(AboutScreen) },
                        colors = navigationItemColors,
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                    )
                    NavigationDrawerItem(
                        icon = { Icon(Lucide.History, contentDescription = "Wallet Recovery Data", tint = DevkitWalletColors.white) },
                        label = { DrawerItemLabel("Wallet Recovery Data") },
                        selected = items[1] == selectedItem.value,
                        onClick = { navController.navigate(RecoveryPhraseScreen) },
                        colors = navigationItemColors,
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                    )
                    NavigationDrawerItem(
                        icon = { Icon(Lucide.SatelliteDish, contentDescription = "Esplora Client", tint = DevkitWalletColors.white) },
                        label = { DrawerItemLabel("Compact Block Filters Node") },
                        selected = items[2] == selectedItem.value,
                        onClick = { navController.navigate(BlockchainClientScreen) },
                        colors = navigationItemColors,
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                    )
                    NavigationDrawerItem(
                        icon = { Icon(Lucide.ScrollText, contentDescription = "Logs", tint = DevkitWalletColors.white) },
                        label = { DrawerItemLabel("Logs") },
                        selected = items[3] == selectedItem.value,
                        onClick = { navController.navigate(LogsScreen) },
                        colors = navigationItemColors,
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                    )
                    NavigationDrawerItem(
                        icon = { Icon(Lucide.ScrollText, contentDescription = "Logs", tint = DevkitWalletColors.white) },
                        label = { DrawerItemLabel("Logs") },
                        selected = items[3] == selectedItem.value,
                        onClick = { navController.navigate(LogsScreen) },
                        colors = navigationItemColors,
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                    )
                }
            }
        },
        content = {
            WalletNavigation(
                drawerState = drawerState,
                activeWallet = activeWallet,
                walletViewModel = walletViewModel
            )
        }
    )
}

@Composable
fun DrawerItemLabel(text: String) {
    Text(
        text = text,
        fontFamily = quattroRegular,
    )
}
