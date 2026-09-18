/*
 * Copyright 2021-2026 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE.txt file.
 */

package org.bitcoindevkit.devkitwallet.domain

import org.bitcoindevkit.BlockHash
import org.bitcoindevkit.BlockId
import org.bitcoindevkit.Network
import org.bitcoindevkit.RecoveryPoint

/**
 * A block hardcoded in the app, used as the starting point of a recovery scan for a wallet that has no history of its
 * own yet.
 *
 * Recovering from the genesis block makes the Kyoto node walk every block filter the network ever produced, which is a
 * lot of bandwidth for a wallet created today that cannot possibly hold coins older than its own keys. Each network
 * therefore ships a checkpoint reasonably far along its chain, and a wallet created after that block still finds all of
 * its history starting from there.
 *
 * @property height Height of the checkpoint block.
 * @property hash Hash of the checkpoint block, as a hex string.
 */
data class BundledCheckpoint(val height: UInt, val hash: String) {
    /** Turns the checkpoint into the [RecoveryPoint] the Kyoto node's recovery scan starts from. */
    fun toRecoveryPoint(): RecoveryPoint = RecoveryPoint.Other(BlockId(height, BlockHash.fromString(hash)))
}

/**
 * The checkpoint the app ships for this network, or null on a network where the genesis block is the only sensible
 * starting point.
 *
 * Regtest chains are created locally and start at their own genesis block, so they ship no checkpoint.
 */
val Network.bundledCheckpoint: BundledCheckpoint?
    get() =
        when (this) {
            Network.SIGNET ->
                BundledCheckpoint(
                    height = 320_000u,
                    hash = "0000000740ae66b284da84387dcfa14d7b1385b0bad482005ba4e770ea6c4b95",
                )
            Network.TESTNET ->
                BundledCheckpoint(
                    height = 5_128_000u,
                    hash = "000000000001d3a7821a20f7c2a07143705ad249b1514ecb4ec7f3add4f1c54b",
                )
            Network.TESTNET4 ->
                BundledCheckpoint(
                    height = 150_000u,
                    hash = "0000000000d9877342754dea8ec1eb24631517d38e3443c370465ee53a8b7434",
                )
            Network.REGTEST -> null
            Network.BITCOIN -> throw IllegalArgumentException("Bitcoin mainnet network is not supported")
        }
