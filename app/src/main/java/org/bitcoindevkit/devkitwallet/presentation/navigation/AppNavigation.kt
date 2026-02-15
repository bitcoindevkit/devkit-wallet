/*
 * Copyright 2021-2026 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.presentation.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import org.bitcoindevkit.devkitwallet.data.SingleWallet
import org.bitcoindevkit.devkitwallet.domain.Wallet
import org.bitcoindevkit.devkitwallet.presentation.WalletCreateType
import org.bitcoindevkit.devkitwallet.presentation.ui.screens.drawer.AboutScreen
import org.bitcoindevkit.devkitwallet.presentation.ui.screens.drawer.BlockchainClientScreen
import org.bitcoindevkit.devkitwallet.presentation.ui.screens.drawer.LogsScreen
import org.bitcoindevkit.devkitwallet.presentation.ui.screens.drawer.RecoveryDataScreen
import org.bitcoindevkit.devkitwallet.presentation.ui.screens.drawer.SettingsScreen
import org.bitcoindevkit.devkitwallet.presentation.ui.screens.intro.ActiveWalletsScreen
import org.bitcoindevkit.devkitwallet.presentation.ui.screens.intro.CreateNewWalletScreen
import org.bitcoindevkit.devkitwallet.presentation.ui.screens.intro.RecoverWalletScreen
import org.bitcoindevkit.devkitwallet.presentation.ui.screens.intro.WalletChoiceScreen
import org.bitcoindevkit.devkitwallet.presentation.ui.screens.wallet.RBFScreen
import org.bitcoindevkit.devkitwallet.presentation.ui.screens.wallet.ReceiveScreen
import org.bitcoindevkit.devkitwallet.presentation.ui.screens.wallet.SendScreen
import org.bitcoindevkit.devkitwallet.presentation.ui.screens.wallet.TransactionHistoryScreen
import org.bitcoindevkit.devkitwallet.presentation.ui.screens.wallet.TransactionScreen
import org.bitcoindevkit.devkitwallet.presentation.ui.screens.wallet.WalletHomeScreen
import org.bitcoindevkit.devkitwallet.presentation.viewmodels.AddressViewModel
import org.bitcoindevkit.devkitwallet.presentation.viewmodels.SendViewModel
import org.bitcoindevkit.devkitwallet.presentation.viewmodels.WalletViewModel

// M3 motion easing curves
private val EmphasizedDecelerate = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1.0f)
private val EmphasizedAccelerate = CubicBezierEasing(0.3f, 0.0f, 0.8f, 0.15f)

private const val ENTER_DURATION = 400
private const val EXIT_DURATION = 200
private const val FADE_IN_DURATION = 300
private const val FADE_OUT_DURATION = 150
private const val SLIDE_DISTANCE_DP = 30

// Forward: entering screen slides in from right and fades in (decelerate)
private val m3ForwardEnter: EnterTransition =
    slideInHorizontally(
        animationSpec = tween(ENTER_DURATION, easing = EmphasizedDecelerate),
        initialOffsetX = { SLIDE_DISTANCE_DP * 3 },
    ) + fadeIn(
        animationSpec = tween(FADE_IN_DURATION, delayMillis = 50, easing = EmphasizedDecelerate),
    )

// Forward: outgoing screen slides out to left and fades out (accelerate)
private val m3ForwardExit: ExitTransition =
    slideOutHorizontally(
        animationSpec = tween(EXIT_DURATION, easing = EmphasizedAccelerate),
        targetOffsetX = { -SLIDE_DISTANCE_DP * 3 },
    ) + fadeOut(
        animationSpec = tween(FADE_OUT_DURATION, easing = EmphasizedAccelerate),
    )

// Backward: returning screen slides in from left and fades in (decelerate)
private val m3BackwardEnter: EnterTransition =
    slideInHorizontally(
        animationSpec = tween(ENTER_DURATION, easing = EmphasizedDecelerate),
        initialOffsetX = { -SLIDE_DISTANCE_DP * 3 },
    ) + fadeIn(
        animationSpec = tween(FADE_IN_DURATION, delayMillis = 50, easing = EmphasizedDecelerate),
    )

// Backward: outgoing screen slides out to right and fades out (accelerate)
private val m3BackwardExit: ExitTransition =
    slideOutHorizontally(
        animationSpec = tween(EXIT_DURATION, easing = EmphasizedAccelerate),
        targetOffsetX = { SLIDE_DISTANCE_DP * 3 },
    ) + fadeOut(
        animationSpec = tween(FADE_OUT_DURATION, easing = EmphasizedAccelerate),
    )

@Composable
fun AppNavigation(
    activeWallet: Wallet?,
    activeWallets: List<SingleWallet>,
    onBuildWalletButtonClicked: (WalletCreateType) -> Unit,
) {
    val navController: NavHostController = rememberNavController()

    val walletViewModel = remember(activeWallet) { activeWallet?.let { WalletViewModel(it) } }
    val addressViewModel = remember(activeWallet) { activeWallet?.let { AddressViewModel(it) } }
    val sendViewModel = remember(activeWallet) { activeWallet?.let { SendViewModel(it) } }

    LaunchedEffect(activeWallet) {
        if (activeWallet != null) {
            navController.navigate(HomeScreen) {
                popUpTo(WalletChoiceScreen) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = WalletChoiceScreen,
        enterTransition = { m3ForwardEnter },
        exitTransition = { m3ForwardExit },
        popEnterTransition = { m3BackwardEnter },
        popExitTransition = { m3BackwardExit },
    ) {
        // Create-wallet flow destinations
        composable<WalletChoiceScreen> {
            WalletChoiceScreen(navController = navController)
        }

        composable<ActiveWalletsScreen> {
            ActiveWalletsScreen(
                activeWallets = activeWallets,
                navController = navController,
                onBuildWalletButtonClicked
            )
        }

        composable<CreateNewWalletScreen> {
            CreateNewWalletScreen(navController = navController, onBuildWalletButtonClicked)
        }

        composable<WalletRecoveryScreen> {
            RecoverWalletScreen(onAction = onBuildWalletButtonClicked, navController = navController)
        }

        // Wallet screens
        composable<HomeScreen> {
            WalletHomeScreen(
                state = walletViewModel!!.state,
                onAction = walletViewModel::onAction,
                navController = navController,
            )
        }

        composable<ReceiveScreen> {
            ReceiveScreen(
                state = addressViewModel!!.state,
                onAction = addressViewModel::onAction,
                navController = navController,
            )
        }

        composable<SendScreen> { SendScreen(navController, sendViewModel!!) }

        composable<RbfScreen> {
            val args = it.toRoute<RbfScreen>()
            RBFScreen(args.txid, navController)
        }

        composable<TransactionHistoryScreen> { TransactionHistoryScreen(navController, activeWallet!!) }

        composable<TransactionScreen> {
            val args = it.toRoute<TransactionScreen>()
            TransactionScreen(args.txid, navController)
        }

        // Settings/drawer screens
        composable<SettingsScreen> { SettingsScreen(navController = navController) }

        composable<AboutScreen> { AboutScreen(navController = navController) }

        composable<RecoveryPhraseScreen> { RecoveryDataScreen(activeWallet!!.getWalletSecrets(), navController = navController) }

        composable<BlockchainClientScreen> {
            BlockchainClientScreen(
                state = walletViewModel!!.state,
                onAction = walletViewModel::onAction,
                navController = navController,
            )
        }

        composable<LogsScreen> { LogsScreen(navController = navController) }
    }
}
