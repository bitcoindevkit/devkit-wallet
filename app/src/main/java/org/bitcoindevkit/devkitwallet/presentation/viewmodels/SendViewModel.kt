/*
 * Copyright 2021-2026 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import org.bitcoindevkit.FeeRate
import org.bitcoindevkit.Psbt
import org.bitcoindevkit.devkitwallet.domain.Wallet
import org.bitcoindevkit.devkitwallet.presentation.viewmodels.mvi.SendScreenAction
import org.bitcoindevkit.devkitwallet.presentation.viewmodels.mvi.TransactionType
import org.bitcoindevkit.devkitwallet.presentation.viewmodels.mvi.TxDataBundle

private const val TAG = "SendViewModel"

internal class SendViewModel(private val wallet: Wallet) : ViewModel() {
    fun onAction(action: SendScreenAction) {
        when (action) {
            is SendScreenAction.Broadcast -> broadcast(action.txDataBundle)
        }
    }

    private fun broadcast(txInfo: TxDataBundle) {
        try {
            // Create, sign, and broadcast
            val psbt: Psbt =
                when (txInfo.transactionType) {
                    TransactionType.STANDARD ->
                        wallet.createTransaction(
                            recipientList = txInfo.recipients,
                            feeRate = FeeRate.fromSatPerVb(txInfo.feeRate),
                            opReturnMsg = txInfo.opReturnMsg,
                        )
                    // TransactionType.SEND_ALL -> Wallet.createSendAllTransaction(recipientList[0].address, FeeRate.fromSatPerVb(feeRate), rbfEnabled, opReturnMsg)
                    TransactionType.SEND_ALL -> throw NotImplementedError("Send all not implemented")
                }
            val isSigned = wallet.sign(psbt)
            if (isSigned) {
                val txid: String = wallet.broadcast(psbt)
                Log.i(TAG, "Transaction was broadcast! txid: $txid")
            } else {
                Log.i(TAG, "Transaction not signed.")
            }
        } catch (e: Throwable) {
            Log.i(TAG, "Broadcast error: ${e.message}")
        }
    }
}
