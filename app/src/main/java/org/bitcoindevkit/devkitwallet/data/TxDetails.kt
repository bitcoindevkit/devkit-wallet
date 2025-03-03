package org.bitcoindevkit.devkitwallet.data

import org.bitcoindevkit.FeeRate
import org.bitcoindevkit.Transaction

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

@JvmInline
value class Timestamp(val timestamp: ULong)

@JvmInline
value class ConfirmationBlock(val height: UInt)
