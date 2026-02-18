/*
 * Copyright 2021-2026 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.presentation

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.bitcoindevkit.devkitwallet.data.NewWalletConfig
import org.bitcoindevkit.devkitwallet.data.RecoverWalletConfig
import org.bitcoindevkit.devkitwallet.data.SingleWallet
import org.bitcoindevkit.devkitwallet.data.UserPreferences
import org.bitcoindevkit.devkitwallet.data.UserPreferencesSerializer
import org.bitcoindevkit.devkitwallet.domain.DwLogger
import org.bitcoindevkit.devkitwallet.domain.DwLogger.LogLevel.INFO
import org.bitcoindevkit.devkitwallet.domain.UserPreferencesRepository
import org.bitcoindevkit.devkitwallet.domain.Wallet
import org.bitcoindevkit.devkitwallet.presentation.navigation.AppNavigation
import org.bitcoindevkit.devkitwallet.presentation.theme.DevkitTheme
import org.bitcoindevkit.devkitwallet.presentation.ui.screens.intro.OnboardingScreen

private const val TAG = "DevkitWalletActivity"
private val Context.userPreferencesStore: DataStore<UserPreferences> by dataStore(
    fileName = "user_preferences.pb",
    serializer = UserPreferencesSerializer,
)

class DevkitWalletActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        // Initialize Devkit Wallet Logger (used in the LogsScreen)
        DwLogger.log(INFO, "Devkit Wallet app started")

        val userPreferencesRepository = UserPreferencesRepository(userPreferencesStore)

        var activeWallet: Wallet? by mutableStateOf(null)
        var activeWallets: List<SingleWallet> by mutableStateOf(emptyList())
        var onboardingDone: Boolean by mutableStateOf(false)
        var preferencesLoaded: Boolean by mutableStateOf(false)

        val onBuildWalletButtonClicked: (WalletCreateType) -> Unit = { walletCreateType ->
            try {
                activeWallet =
                    when (walletCreateType) {
                        is WalletCreateType.FROMSCRATCH ->
                            Wallet.createWallet(
                                newWalletConfig = walletCreateType.newWalletConfig,
                                internalAppFilesPath = filesDir.absolutePath,
                                userPreferencesRepository = userPreferencesRepository,
                            )
                        is WalletCreateType.LOADEXISTING ->
                            Wallet.loadActiveWallet(
                                activeWallet = walletCreateType.activeWallet,
                                internalAppFilesPath = filesDir.absolutePath,
                                userPreferencesRepository = userPreferencesRepository,
                            )
                        is WalletCreateType.RECOVER ->
                            Wallet.recoverWallet(
                                recoverWalletConfig = walletCreateType.recoverWalletConfig,
                                internalAppFilesPath = filesDir.absolutePath,
                                userPreferencesRepository = userPreferencesRepository,
                            )
                    }
            } catch (e: Throwable) {
                Log.i(TAG, "Could not build wallet: $e")
            }
        }

        lifecycleScope.launch {
            activeWallets =
                async {
                    userPreferencesRepository.fetchActiveWallets()
                }.await()

            onboardingDone =
                async {
                    userPreferencesRepository.fetchIntroDone()
                }.await()

            preferencesLoaded = true
        }

        val onFinishOnboarding: () -> Unit = {
            lifecycleScope.launch { userPreferencesRepository.setIntroDone() }
            onboardingDone = true
        }

        setContent {
            if (!preferencesLoaded) return@setContent

            if (!onboardingDone) {
                DwLogger.log(INFO, "First time opening the app, triggering onboarding screen")
                OnboardingScreen(onFinishOnboarding)
            } else {
                DevkitTheme {
                    AppNavigation(
                        activeWallet = activeWallet,
                        activeWallets = activeWallets,
                        onBuildWalletButtonClicked = onBuildWalletButtonClicked,
                    )
                }
            }
        }
    }
}

sealed class WalletCreateType {
    data class FROMSCRATCH(val newWalletConfig: NewWalletConfig) : WalletCreateType()

    data class LOADEXISTING(val activeWallet: SingleWallet) : WalletCreateType()

    data class RECOVER(val recoverWalletConfig: RecoverWalletConfig) : WalletCreateType()
}
