/*
 * Copyright 2021-2025 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.domain

import org.bitcoindevkit.FullScanRequest
import org.bitcoindevkit.SyncRequest
import org.bitcoindevkit.Transaction
import org.bitcoindevkit.Update
import org.bitcoindevkit.EsploraClient as BdkEsploraClient

interface BlockchainClient {
    fun clientId(): String

    fun fullScan(fullScanRequest: FullScanRequest, stopGap: ULong): Update

    fun sync(syncRequest: SyncRequest): Update

    fun broadcast(transaction: Transaction): Unit

    fun endpoint(): String
}

class EsploraClient(private val url: String) : BlockchainClient {
    private val client = BdkEsploraClient(url)

    override fun clientId(): String {
        return url
    }

    override fun fullScan(fullScanRequest: FullScanRequest, stopGap: ULong): Update {
        return client.fullScan(fullScanRequest, stopGap, parallelRequests = 2u)
    }

    override fun sync(syncRequest: SyncRequest): Update {
        return client.sync(syncRequest, parallelRequests = 2u)
    }

    override fun broadcast(transaction: Transaction) {
        client.broadcast(transaction)
    }

    override fun endpoint(): String {
        return url
    }
}
