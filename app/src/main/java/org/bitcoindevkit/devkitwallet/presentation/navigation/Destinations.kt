/*
 * Copyright 2021-2026 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.presentation.navigation

import kotlinx.serialization.Serializable

// Create wallet navigation destinations
@Serializable
object WalletChoiceScreen

@Serializable
object ActiveWalletsScreen

@Serializable
object CreateNewWalletScreen

@Serializable
object WalletRecoveryScreen

// Home navigation destinations
@Serializable
object WalletScreen

@Serializable
object AboutScreen

@Serializable
object RecoveryPhraseScreen

@Serializable
object BlockchainClientScreen

@Serializable
object LogsScreen

// Wallet navigation destinations
@Serializable
object HomeScreen

@Serializable
object ReceiveScreen

@Serializable
object SendScreen

@Serializable
object TransactionHistoryScreen

@Serializable
data class TransactionScreen(val txid: String)

@Serializable
data class RbfScreen(val txid: String)
