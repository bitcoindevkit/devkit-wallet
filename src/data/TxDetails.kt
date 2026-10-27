/*
 * Copyright 2021-2026 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE.txt file.
 */

package org.bitcoindevkit.devkitwallet.data

import org.bitcoindevkit.FeeRate
import org.bitcoindevkit.Transaction

/**
 * Aggregated details for a single wallet transaction.
 *
 * @property transaction The raw [Transaction] object from the BDK library.
 * @property txid The transaction ID as a hex string.
 * @property satoshis Sent from the wallet.
 * @property satoshis Received by the wallet.
 * @property fee Total fee paid for this transaction, in satoshis.
 * @property feeRate Fee rate at which the transaction was mined, if calculable.
 * @property pending True if the transaction has not yet been confirmed in a block.
 * @property confirmationBlock The block that confirmed this transaction, if any.
 * @property confirmationTimestamp Unix timestamp of the confirming block, if confirmed.
 */
data class TxDetails(
    val transaction: Transaction,
    val txid: String,
    val sent: ULong,
    val received: ULong,
    val fee: ULong,
    val feeRate: FeeRate?,
    val pending: Boolean,
    val confirmationBlock: ConfirmationBlock?,
    val confirmationTimestamp: Timestamp?,
)

/** Wrapper for a Unix timestamp in seconds since epoch. */
@JvmInline value class Timestamp(val timestamp: ULong)

/** Wrapper for the block height at which a transaction was confirmed. */
@JvmInline value class ConfirmationBlock(val height: UInt)
