/*
 * Copyright 2021-2026 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE.txt file.
 */

package org.bitcoindevkit.devkitwallet.domain

/** Denotes the display unit for wallet balances and amounts. */
enum class CurrencyUnit {
    /** Whole bitcoin (1 BTC = 100,000,000 sats). */
    Bitcoin,
    /** The smallest on-chain unit of account. */
    Satoshi,
}
