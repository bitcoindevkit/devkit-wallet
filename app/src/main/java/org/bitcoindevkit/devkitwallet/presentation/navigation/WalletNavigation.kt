/*
 * Copyright 2021-2026 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import org.bitcoindevkit.devkitwallet.domain.Wallet
import org.bitcoindevkit.devkitwallet.presentation.ui.screens.wallet.RBFScreen
import org.bitcoindevkit.devkitwallet.presentation.ui.screens.wallet.ReceiveScreen
import org.bitcoindevkit.devkitwallet.presentation.ui.screens.wallet.SendScreen
import org.bitcoindevkit.devkitwallet.presentation.ui.screens.wallet.TransactionHistoryScreen
import org.bitcoindevkit.devkitwallet.presentation.ui.screens.wallet.TransactionScreen
import org.bitcoindevkit.devkitwallet.presentation.ui.screens.wallet.WalletHomeScreen
import org.bitcoindevkit.devkitwallet.presentation.viewmodels.AddressViewModel
import org.bitcoindevkit.devkitwallet.presentation.viewmodels.SendViewModel
import org.bitcoindevkit.devkitwallet.presentation.viewmodels.WalletViewModel

private const val ANIMATION_DURATION: Int = 400

@Composable
internal fun WalletNavigation(drawerState: DrawerState, walletViewModel: WalletViewModel, activeWallet: Wallet) {
    val navController: NavHostController = rememberNavController()
    // val walletViewModel = WalletViewModel(activeWallet)
    val addressViewModel = AddressViewModel(activeWallet)
    val sendViewModel = SendViewModel(activeWallet)

    NavHost(
        navController = navController,
        startDestination = HomeScreen,
    ) {
        composable<HomeScreen>(
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(ANIMATION_DURATION)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(ANIMATION_DURATION)
                )
            },
        ) {
            WalletHomeScreen(
                state = walletViewModel.state,
                onAction = walletViewModel::onAction,
                drawerState = drawerState,
                navController = navController,
            )
        }

        composable<ReceiveScreen>(
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(ANIMATION_DURATION)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(ANIMATION_DURATION)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(ANIMATION_DURATION)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(ANIMATION_DURATION)
                )
            },
        ) {
            ReceiveScreen(
                state = addressViewModel.state,
                onAction = addressViewModel::onAction,
                navController = navController,
            )
        }

        composable<SendScreen>(
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(ANIMATION_DURATION)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(ANIMATION_DURATION)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(ANIMATION_DURATION)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(ANIMATION_DURATION)
                )
            },
        ) { SendScreen(navController, sendViewModel) }

        composable<RbfScreen>(
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(ANIMATION_DURATION)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(ANIMATION_DURATION)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(ANIMATION_DURATION)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(ANIMATION_DURATION)
                )
            },
        ) {
            val args = it.toRoute<RbfScreen>()
            RBFScreen(args.txid, navController)
        }

        composable<TransactionHistoryScreen>(
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(ANIMATION_DURATION)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(ANIMATION_DURATION)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(ANIMATION_DURATION)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(ANIMATION_DURATION)
                )
            },
        ) { TransactionHistoryScreen(navController, activeWallet) }

        composable<TransactionScreen>(
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(ANIMATION_DURATION)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(ANIMATION_DURATION)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(ANIMATION_DURATION)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(ANIMATION_DURATION)
                )
            },
        ) {
            val args = it.toRoute<TransactionScreen>()
            TransactionScreen(args.txid, navController)
        }
    }
}
