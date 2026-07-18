/*
 * Copyright 2021-2026 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE.txt file.
 */

package org.bitcoindevkit.devkitwallet.presentation.viewmodels.mvi

// data class SendScreenState(
//     val address: String? = null,
// )

/** One-way actions that the send screen can dispatch to its [SendViewModel]. */
sealed class SendScreenAction {
    /** Build, sign, and broadcast the transaction described by [txDataBundle]. */
    data class Broadcast(val txDataBundle: TxDataBundle) : SendScreenAction()
}

/**
 * Immutable payload describing a transaction to be broadcast.
 *
 * @property recipients Destinations and amounts.
 * @property feeRate Fee rate in sat/vB.
 * @property transactionType Standard payment or drain-the-wallet.
 * @property rbfDisabled True to opt-out of RBF signaling.
 */
data class TxDataBundle(
    val recipients: List<Recipient>,
    val feeRate: ULong,
    val transactionType: TransactionType,
    val rbfDisabled: Boolean = false,
)

/**
 * Single recipient entry on the send screen.
 *
 * @property address Bitcoin address string.
 * @property amount Satoshi amount to send.
 */
data class Recipient(var address: String, var amount: ULong)

/** Transaction construction strategy. */
enum class TransactionType {
    /** Standard multi-recipient payment at the given fee rate. */
    STANDARD,
    /** Send the entire wallet balance to a single address (not yet implemented). */
    SEND_ALL,
}
