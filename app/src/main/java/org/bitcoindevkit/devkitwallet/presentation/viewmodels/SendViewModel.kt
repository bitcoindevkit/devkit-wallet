/*
 * Copyright 2021-2025 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bitcoindevkit.FeeRate
import org.bitcoindevkit.Psbt
import org.bitcoindevkit.devkitwallet.domain.DwLogger
import org.bitcoindevkit.devkitwallet.domain.DwLogger.LogLevel.ERROR
import org.bitcoindevkit.devkitwallet.domain.DwLogger.LogLevel.INFO
import org.bitcoindevkit.devkitwallet.domain.Wallet

private const val TAG = "SendViewModel"

sealed class SendScreenAction {
    data class Broadcast(val txDataBundle: TxDataBundle) : SendScreenAction()
}

data class TxDataBundle(
    val recipients: List<Recipient>,
    val feeRate: ULong,
    val transactionType: TransactionType,
    val wif: String? = null,
)

data class Recipient(var address: String, var amount: ULong)

enum class TransactionType {
    STANDARD,
    SEND_ALL,
    SWEEP,
}

sealed class BroadcastResult {
    data class Success(val txid: String) : BroadcastResult()

    data class Error(val message: String) : BroadcastResult()
}

internal class SendViewModel(private val wallet: Wallet) : ViewModel() {
    private val _broadcastResult = MutableStateFlow<BroadcastResult?>(null)
    val broadcastResult: StateFlow<BroadcastResult?> = _broadcastResult.asStateFlow()

    fun clearBroadcastResult() {
        _broadcastResult.value = null
    }

    fun onAction(action: SendScreenAction) {
        when (action) {
            is SendScreenAction.Broadcast -> broadcast(action.txDataBundle)
        }
    }

    private fun broadcast(txInfo: TxDataBundle) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val psbt: Psbt =
                    when (txInfo.transactionType) {
                        TransactionType.STANDARD -> {
                            val unsignedPsbt = wallet.createTransaction(
                                recipientList = txInfo.recipients,
                                feeRate = FeeRate.fromSatPerVb(txInfo.feeRate),
                            )
                            wallet.sign(unsignedPsbt)
                            unsignedPsbt
                        }

                        TransactionType.SWEEP -> {
                            val feeRate = FeeRate.fromSatPerVb(txInfo.feeRate)
                            val rawWif =
                                txInfo.wif ?: throw java.lang.IllegalStateException("WIF missing in sweep payload")
                            wallet.sweep(rawWif, feeRate)
                        }

                        // TransactionType.SEND_ALL -> Wallet.createSendAllTransaction(recipientList[0].address, FeeRate.fromSatPerVb(feeRate), rbfEnabled, opReturnMsg)
                        TransactionType.SEND_ALL -> {
                            throw NotImplementedError("Send all not implemented")
                        }
                    }

                val txid: String = wallet.broadcast(psbt)
                Log.i(TAG, "Transaction was broadcast! txid: $txid")
                DwLogger.log(INFO, "Broadcast success: txid=$txid")
                withContext(Dispatchers.Main) {
                    _broadcastResult.value = BroadcastResult.Success(txid)
                }
            } catch (e: Throwable) {
                Log.e(TAG, "Broadcast error: ${e.message}", e)
                DwLogger.log(ERROR, "Broadcast failed: ${e.message}")
                withContext(Dispatchers.Main) {
                    _broadcastResult.value = BroadcastResult.Error(e.message ?: "Unknown error")
                }
            }
        }
    }
}
