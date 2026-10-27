/*
 * Copyright 2021-2026 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE.txt file.
 */

package org.bitcoindevkit.devkitwallet.data

import org.bitcoindevkit.Descriptor
import org.bitcoindevkit.Network
import org.bitcoindevkit.devkitwallet.data.datastore.ActiveWalletScriptType

/**
 * Configuration required to create a brand-new wallet from a fresh mnemonic.
 *
 * @property name User-facing label for the wallet.
 * @property network The bitcoin [Network] the wallet will operate on (e.g. Testnet3, Signet, Regtest).
 * @property scriptType The address script type (e.g. P2WPKH or P2TR).
 */
data class NewWalletConfig(
    val name: String,
    val network: Network,
    val scriptType: ActiveWalletScriptType,
)

/**
 * Configuration required to recover an existing wallet.
 *
 * Recovery can happen either from a BIP-39 [recoveryPhrase] (in which case [scriptType] must also be supplied) or
 * directly from [descriptor] / [changeDescriptor] strings.
 *
 * @property name User-facing label for the recovered wallet.
 * @property network The Bitcoin [Network] the wallet will operate on.
 * @property scriptType Address script type; required when recovering from a seed phrase.
 * @property recoveryPhrase Optional BIP-39 mnemonic phrase.
 * @property descriptor External descriptor; used when recovering without a seed phrase.
 * @property changeDescriptor Internal (change) descriptor; used when recovering without a seed phrase.
 */
data class RecoverWalletConfig(
    val name: String,
    val network: Network,
    val scriptType: ActiveWalletScriptType?,
    val recoveryPhrase: String?,
    val descriptor: Descriptor,
    val changeDescriptor: Descriptor,
)
