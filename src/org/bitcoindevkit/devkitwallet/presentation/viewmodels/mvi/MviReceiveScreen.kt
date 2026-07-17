/*
 * Copyright 2021-2026 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE.txt file.
 */

package org.bitcoindevkit.devkitwallet.presentation.viewmodels.mvi

/**
 * UI state snapshot for the receive screen.
 *
 * @property address Latest unused receiving address, or null before first load.
 * @property addressIndex Derivation index of the displayed address.
 */
data class ReceiveScreenState(
    val address: String? = null,
    val addressIndex: UInt? = null,
)

/** One-way actions that the receive screen can dispatch to its [AddressViewModel]. */
sealed interface ReceiveScreenAction {
    /** Derive and expose the next unused external address. */
    data object UpdateAddress : ReceiveScreenAction
}
