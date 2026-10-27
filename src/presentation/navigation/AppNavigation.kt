/*
 * Copyright 2021-2026 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE.txt file.
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import org.bitcoindevkit.devkitwallet.data.datastore.StoredWallet
import org.bitcoindevkit.devkitwallet.domain.Wallet
import org.bitcoindevkit.devkitwallet.presentation.WalletCreateType
import org.bitcoindevkit.devkitwallet.presentation.ui.screens.intro.ActiveWalletsScreen
import org.bitcoindevkit.devkitwallet.presentation.ui.screens.intro.CreateNewWalletScreen
import org.bitcoindevkit.devkitwallet.presentation.ui.screens.intro.RecoverWalletScreen
import org.bitcoindevkit.devkitwallet.presentation.ui.screens.intro.WalletChoiceScreen
import org.bitcoindevkit.devkitwallet.presentation.ui.screens.settings.AboutScreen
import org.bitcoindevkit.devkitwallet.presentation.ui.screens.settings.CbfNodeScreen
import org.bitcoindevkit.devkitwallet.presentation.ui.screens.settings.LogsScreen
import org.bitcoindevkit.devkitwallet.presentation.ui.screens.settings.RecoveryDataScreen
import org.bitcoindevkit.devkitwallet.presentation.ui.screens.settings.SettingsScreen
import org.bitcoindevkit.devkitwallet.presentation.ui.screens.settings.ThemeScreen
import org.bitcoindevkit.devkitwallet.presentation.ui.screens.wallet.RBFScreen
import org.bitcoindevkit.devkitwallet.presentation.ui.screens.wallet.ReceiveScreen
import org.bitcoindevkit.devkitwallet.presentation.ui.screens.wallet.SendScreen
import org.bitcoindevkit.devkitwallet.presentation.ui.screens.wallet.TransactionHistoryScreen
import org.bitcoindevkit.devkitwallet.presentation.ui.screens.wallet.TransactionScreen
import org.bitcoindevkit.devkitwallet.presentation.ui.screens.wallet.WalletHomeScreen
import org.bitcoindevkit.devkitwallet.presentation.viewmodels.AddressViewModel
import org.bitcoindevkit.devkitwallet.presentation.viewmodels.SendViewModel
import org.bitcoindevkit.devkitwallet.presentation.viewmodels.WalletViewModel

/** Material-3 Emphasized-Decelerate easing for entering screens. */
private val EmphasizedDecelerate = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1.0f)

/** Material-3 Emphasized-Accelerate easing for exiting screens. */
private val EmphasizedAccelerate = CubicBezierEasing(0.3f, 0.0f, 0.8f, 0.15f)

private const val ENTER_DURATION = 400
private const val EXIT_DURATION = 200
private const val FADE_IN_DURATION = 300
private const val FADE_OUT_DURATION = 150
private const val SLIDE_DISTANCE_DP = 30

/** Enter transition when pushing a new destination onto the back-stack. */
private val m3ForwardEnter: EnterTransition =
    slideInHorizontally(
        animationSpec = tween(ENTER_DURATION, easing = EmphasizedDecelerate),
        initialOffsetX = { SLIDE_DISTANCE_DP * 3 },
    ) + fadeIn(animationSpec = tween(FADE_IN_DURATION, delayMillis = 50, easing = EmphasizedDecelerate))

/** Exit transition when the current destination is being replaced by a push. */
private val m3ForwardExit: ExitTransition =
    slideOutHorizontally(
        animationSpec = tween(EXIT_DURATION, easing = EmphasizedAccelerate),
        targetOffsetX = { -SLIDE_DISTANCE_DP * 3 },
    ) + fadeOut(animationSpec = tween(FADE_OUT_DURATION, easing = EmphasizedAccelerate))

/** Enter transition when popping back to a previous destination. */
private val m3BackwardEnter: EnterTransition =
    slideInHorizontally(
        animationSpec = tween(ENTER_DURATION, easing = EmphasizedDecelerate),
        initialOffsetX = { -SLIDE_DISTANCE_DP * 3 },
    ) + fadeIn(animationSpec = tween(FADE_IN_DURATION, delayMillis = 50, easing = EmphasizedDecelerate))

/** Exit transition when the current destination is popped off the back-stack. */
private val m3BackwardExit: ExitTransition =
    slideOutHorizontally(
        animationSpec = tween(EXIT_DURATION, easing = EmphasizedAccelerate),
        targetOffsetX = { SLIDE_DISTANCE_DP * 3 },
    ) + fadeOut(animationSpec = tween(FADE_OUT_DURATION, easing = EmphasizedAccelerate))

/**
 * Root [NavHost] for the entire app.
 *
 * Sets up a M3-style slide+fade transition system and wires every destination to its corresponding ViewModel and
 * Compose screen.
 */
@Composable
fun AppNavigation(
    activeWallet: Wallet?,
    activeWallets: List<StoredWallet>,
    onBuildWalletButtonClicked: (WalletCreateType) -> Unit,
    useDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
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
                onBuildWalletButtonClicked,
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
            val state by walletViewModel!!.state.collectAsStateWithLifecycle()
            WalletHomeScreen(
                state = state,
                onAction = walletViewModel::onAction,
                snackbarMessages = walletViewModel.snackbarMessages,
                navController = navController,
            )
        }

        composable<ReceiveScreen> {
            val state by addressViewModel!!.state.collectAsStateWithLifecycle()
            ReceiveScreen(
                state = state,
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

        composable<RecoveryPhraseScreen> {
            RecoveryDataScreen(activeWallet!!.getWalletSecrets(), navController = navController)
        }

        composable<BlockchainClientScreen> {
            val state by walletViewModel!!.state.collectAsStateWithLifecycle()
            CbfNodeScreen(
                state = state,
                onAction = walletViewModel::onAction,
                navController = navController,
            )
        }

        composable<LogsScreen> { LogsScreen(navController = navController) }

        composable<ThemeScreen> {
            ThemeScreen(
                useDarkTheme = useDarkTheme,
                onToggleTheme = onToggleTheme,
                navController = navController,
            )
        }
    }
}
