/*
 * Copyright 2021-2026 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE.txt file.
 */

package org.bitcoindevkit.devkitwallet.presentation.navigation

import kotlinx.serialization.Serializable

/** Onboarding: landing screen where the user chooses to create or load a wallet. */
@Serializable object WalletChoiceScreen

/** Onboarding: list of wallets already persisted on the device. */
@Serializable object ActiveWalletsScreen

/** Onboarding: form for creating a brand-new wallet. */
@Serializable object CreateNewWalletScreen

/** Onboarding: form for recovering a wallet from a seed phrase or descriptors. */
@Serializable object WalletRecoveryScreen

/** Drawer: root settings screen. */
@Serializable object SettingsScreen

/** Drawer: app version and credits. */
@Serializable object AboutScreen

/** Drawer: screen displaying the wallet recovery phrase and descriptors. */
@Serializable object RecoveryPhraseScreen

/** Drawer: screen for managing the Kyoto CBF client and peers. */
@Serializable object BlockchainClientScreen

/** Drawer: in-app log viewer. */
@Serializable object LogsScreen

/** Drawer: theme toggle screen. */
@Serializable object ThemeScreen

/** Wallet: main dashboard showing balance and block height. */
@Serializable object HomeScreen

/** Wallet: screen showing the latest receiving address. */
@Serializable object ReceiveScreen

/** Wallet: screen for constructing and broadcasting transactions. */
@Serializable object SendScreen

/** Wallet: scrollable list of all wallet transactions. */
@Serializable object TransactionHistoryScreen

/** Wallet: detailed view for a single transaction, identified by [txid]. */
@Serializable data class TransactionScreen(val txid: String)

/** Wallet: Replace-By-Fee screen for bumping a transaction, identified by [txid]. */
@Serializable data class RbfScreen(val txid: String)
