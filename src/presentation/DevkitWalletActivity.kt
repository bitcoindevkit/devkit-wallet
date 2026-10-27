/*
 * Copyright 2021-2026 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE.txt file.
 */

package org.bitcoindevkit.devkitwallet.presentation

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.graphics.drawable.toDrawable
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.bitcoindevkit.devkitwallet.data.NewWalletConfig
import org.bitcoindevkit.devkitwallet.data.RecoverWalletConfig
import org.bitcoindevkit.devkitwallet.data.datastore.AppSettings
import org.bitcoindevkit.devkitwallet.data.datastore.AppSettingsSerializer
import org.bitcoindevkit.devkitwallet.data.datastore.StoredWallet
import org.bitcoindevkit.devkitwallet.data.datastore.WalletData
import org.bitcoindevkit.devkitwallet.data.datastore.WalletDataSerializer
import org.bitcoindevkit.devkitwallet.domain.AppSettingsRepository
import org.bitcoindevkit.devkitwallet.domain.DwLogger
import org.bitcoindevkit.devkitwallet.domain.DwLogger.LogLevel.INFO
import org.bitcoindevkit.devkitwallet.domain.Wallet
import org.bitcoindevkit.devkitwallet.domain.WalletRepository
import org.bitcoindevkit.devkitwallet.presentation.navigation.AppNavigation
import org.bitcoindevkit.devkitwallet.presentation.theme.DevkitTheme
import org.bitcoindevkit.devkitwallet.presentation.theme.themeSurfaceColor
import org.bitcoindevkit.devkitwallet.presentation.ui.screens.intro.OnboardingScreen

private const val TAG = "DevkitWalletActivity"
private val Context.appSettingsStore: DataStore<AppSettings> by
    dataStore(
        fileName = "app_settings.json",
        serializer = AppSettingsSerializer,
    )
private val Context.walletDataStore: DataStore<WalletData> by
    dataStore(
        fileName = "wallet_data.json",
        serializer = WalletDataSerializer,
    )

/**
 * Entry-point for the Devkit Wallet.
 *
 * Responsibilities include:
 * - Installing the splash screen.
 * - Loading persisted app settings and wallet metadata from DataStore.
 * - Instantiating (or restoring) the active [Wallet].
 * - Hosting the Compose UI: either the onboarding flow or the main [AppNavigation] graph.
 * - Synchronizing the system window background with the current dark/light theme to avoid color flashes during
 *   navigation transitions.
 */
class DevkitWalletActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        // Initialize Devkit Wallet Logger (used in the LogsScreen)
        DwLogger.log(INFO, "Devkit Wallet app started")

        val appSettingsRepository = AppSettingsRepository(appSettingsStore)
        val walletRepository = WalletRepository(walletDataStore)

        var activeWallet: Wallet? by mutableStateOf(null)
        var activeWallets: List<StoredWallet> by mutableStateOf(emptyList())
        var onboardingDone: Boolean by mutableStateOf(false)
        var useDarkTheme: Boolean by mutableStateOf(true)
        var preferencesLoaded: Boolean by mutableStateOf(false)

        val onBuildWalletButtonClicked: (WalletCreateType) -> Unit = { walletCreateType ->
            try {
                activeWallet =
                    when (walletCreateType) {
                        is WalletCreateType.FROMSCRATCH -> {
                            Wallet.createWallet(
                                newWalletConfig = walletCreateType.newWalletConfig,
                                internalAppFilesPath = filesDir.absolutePath,
                                walletRepository = walletRepository,
                            )
                        }

                        is WalletCreateType.LOADEXISTING -> {
                            Wallet.loadActiveWallet(
                                activeWallet = walletCreateType.activeWallet,
                                internalAppFilesPath = filesDir.absolutePath,
                                walletRepository = walletRepository,
                            )
                        }

                        is WalletCreateType.RECOVER -> {
                            Wallet.recoverWallet(
                                recoverWalletConfig = walletCreateType.recoverWalletConfig,
                                internalAppFilesPath = filesDir.absolutePath,
                                walletRepository = walletRepository,
                            )
                        }
                    }
            } catch (e: Throwable) {
                Log.i(TAG, "Could not build wallet: $e")
            }
        }

        val onToggleTheme: () -> Unit = {
            useDarkTheme = !useDarkTheme
            // Keep the window background in sync with the Compose theme. Navigation transitions
            // include a fade-out, which causes the window background to show through briefly.
            // Updating it here (synchronously, before Compose recomposes) prevents a color flash.
            window.setBackgroundDrawable(themeSurfaceColor(useDarkTheme).toDrawable())
            lifecycleScope.launch { appSettingsRepository.setDarkTheme(useDarkTheme) }
        }

        lifecycleScope.launch {
            activeWallets =
                async {
                    walletRepository.fetchWallets()
                }
                    .await()

            onboardingDone =
                async {
                    appSettingsRepository.fetchIntroDone()
                }
                    .await()

            useDarkTheme =
                async {
                    appSettingsRepository.fetchDarkTheme()
                }
                    .await()

            // Set the window background before allowing the UI to render for the first time,
            // so the correct surface color is already in place when Compose draws its first frame.
            window.setBackgroundDrawable(ColorDrawable(themeSurfaceColor(useDarkTheme)))
            preferencesLoaded = true
        }

        val onFinishOnboarding: () -> Unit = {
            lifecycleScope.launch { appSettingsRepository.setIntroDone() }
            onboardingDone = true
        }

        setContent {
            if (!preferencesLoaded) return@setContent

            if (!onboardingDone) {
                DwLogger.log(INFO, "First time opening the app, triggering onboarding screen")
                OnboardingScreen(onFinishOnboarding)
            } else {
                DevkitTheme(darkTheme = useDarkTheme) {
                    AppNavigation(
                        activeWallet = activeWallet,
                        activeWallets = activeWallets,
                        onBuildWalletButtonClicked = onBuildWalletButtonClicked,
                        useDarkTheme = useDarkTheme,
                        onToggleTheme = onToggleTheme,
                    )
                }
            }
        }
    }
}

/** The three ways a wallet can be brought into existence in the app. */
sealed class WalletCreateType {
    /** Create a new wallet with a randomly generated seed. */
    data class FROMSCRATCH(val newWalletConfig: NewWalletConfig) : WalletCreateType()

    /** Load a previously-created wallet that was persisted in the local datastore. */
    data class LOADEXISTING(val activeWallet: StoredWallet) : WalletCreateType()

    /** Recover a wallet from a recovery phrase or from output descriptors. */
    data class RECOVER(val recoverWalletConfig: RecoverWalletConfig) : WalletCreateType()
}
