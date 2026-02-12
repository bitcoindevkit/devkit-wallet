/*
 * Copyright 2021-2026 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.bitcoindevkit.devkitwallet.domain.Wallet
import org.bitcoindevkit.devkitwallet.presentation.ui.screens.WalletRoot
import org.bitcoindevkit.devkitwallet.presentation.ui.screens.drawer.AboutScreen
import org.bitcoindevkit.devkitwallet.presentation.ui.screens.drawer.BlockchainClientScreen
import org.bitcoindevkit.devkitwallet.presentation.ui.screens.drawer.LogsScreen
import org.bitcoindevkit.devkitwallet.presentation.ui.screens.drawer.RecoveryDataScreen
import org.bitcoindevkit.devkitwallet.presentation.viewmodels.WalletViewModel

@Composable
fun HomeNavigation(activeWallet: Wallet) {
    val navController: NavHostController = rememberNavController()
    val animationDuration = 400
    val walletViewModel = WalletViewModel(activeWallet)

    NavHost(
        navController = navController,
        startDestination = WalletScreen,
    ) {
        composable<WalletScreen>(
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(animationDuration)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(animationDuration)
                )
            },
        ) { WalletRoot(activeWallet = activeWallet, walletViewModel = walletViewModel, navController = navController) }

        composable<AboutScreen>(
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(animationDuration)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(animationDuration)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(animationDuration)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(animationDuration)
                )
            },
        ) { AboutScreen(navController = navController) }

        composable<RecoveryPhraseScreen>(
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(animationDuration)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(animationDuration)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(animationDuration)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(animationDuration)
                )
            },
        ) { RecoveryDataScreen(activeWallet.getWalletSecrets(), navController = navController) }

        composable<BlockchainClientScreen>(
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(animationDuration)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(animationDuration)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(animationDuration)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(animationDuration)
                )
            },
        ) {
            BlockchainClientScreen(
                state = walletViewModel.state,
                onAction = walletViewModel::onAction,
                navController = navController,
            )
        }

        composable<LogsScreen>(
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(animationDuration)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(animationDuration)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(animationDuration)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(animationDuration)
                )
            },
        ) { LogsScreen(navController = navController) }

        composable<LogsScreen>(
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(animationDuration)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(animationDuration)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(animationDuration)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(animationDuration)
                )
            },
        ) { LogsScreen(navController = navController) }
    }
}
