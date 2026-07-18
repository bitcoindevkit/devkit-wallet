/*
 * Copyright 2021-2026 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE.txt file.
 */

package org.bitcoindevkit.devkitwallet.domain

import android.util.Log
import java.util.UUID
import kotlinx.coroutines.runBlocking
import org.bitcoindevkit.Address
import org.bitcoindevkit.AddressInfo
import org.bitcoindevkit.Amount
import org.bitcoindevkit.CanonicalTx
import org.bitcoindevkit.ChainPosition
import org.bitcoindevkit.Descriptor
import org.bitcoindevkit.DescriptorSecretKey
import org.bitcoindevkit.FeeRate
import org.bitcoindevkit.KeychainKind
import org.bitcoindevkit.Mnemonic
import org.bitcoindevkit.Network
import org.bitcoindevkit.NetworkKind
import org.bitcoindevkit.Persister
import org.bitcoindevkit.Psbt
import org.bitcoindevkit.Script
import org.bitcoindevkit.TxBuilder
import org.bitcoindevkit.Update
import org.bitcoindevkit.Wallet as BdkWallet
import org.bitcoindevkit.WordCount
import org.bitcoindevkit.devkitwallet.data.ConfirmationBlock
import org.bitcoindevkit.devkitwallet.data.NewWalletConfig
import org.bitcoindevkit.devkitwallet.data.RecoverWalletConfig
import org.bitcoindevkit.devkitwallet.data.Timestamp
import org.bitcoindevkit.devkitwallet.data.TxDetails
import org.bitcoindevkit.devkitwallet.data.datastore.ActiveWalletScriptType
import org.bitcoindevkit.devkitwallet.data.datastore.StoredWallet
import org.bitcoindevkit.devkitwallet.domain.utils.intoDomain
import org.bitcoindevkit.devkitwallet.domain.utils.intoStored
import org.bitcoindevkit.devkitwallet.presentation.viewmodels.mvi.Recipient

private const val TAG = "Wallet"

/**
 * Core domain wrapper around a BDK [BdkWallet] instance.
 *
 * This class manages descriptors, keys, a SQLite connection for persistence, and exposes high-level wallet operations
 * such as balance queries, transaction creation, and address derivation.
 *
 * @property wallet The underlying BDK wallet.
 * @property internalAppFilesPath Absolute path to the app's private files' directory.
 * @property network The bitcoin network this wallet operates on.
 */
class Wallet
private constructor(
    val wallet: BdkWallet,
    private val walletSecrets: WalletSecrets,
    private val connection: Persister,
    private val walletId: String,
    private val walletRepository: WalletRepository,
    val internalAppFilesPath: String,
    val network: Network,
) {
    /** Returns the sensitive material (descriptors and recovery phrase) for this wallet. */
    fun getWalletSecrets(): WalletSecrets {
        return walletSecrets
    }

    /** Returns the block height of the wallet's latest known block. */
    fun bestBlock(): UInt {
        return wallet.latestCheckpoint().height
    }

    /**
     * Builds a [Psbt] that pays the given [recipientList] at the specified [feeRate].
     *
     * @param recipientList List of recipients and amounts.
     * @param feeRate Desired fee rate.
     * @return An unsigned [Psbt].
     */
    fun createTransaction(recipientList: List<Recipient>, feeRate: FeeRate): Psbt {
        // technique 1 for adding a list of recipients to the TxBuilder
        // var txBuilder = TxBuilder()
        // for (recipient in recipientList) {
        //     txBuilder  = txBuilder.addRecipient(address = recipient.first, amount = recipient.second)
        // }
        // txBuilder = txBuilder.feeRate(satPerVbyte = fee_rate)

        // technique 2 for adding a list of recipients to the TxBuilder
        var txBuilder =
            recipientList.fold(TxBuilder()) { builder, recipient ->
                // val address = Address(recipient.address)
                val scriptPubKey: Script = Address(recipient.address, this.network).scriptPubkey()
                builder.addRecipient(scriptPubKey, Amount.fromSat(recipient.amount))
            }
        return txBuilder.feeRate(feeRate).finish(wallet)
    }

    // @OptIn(ExperimentalUnsignedTypes::class)
    // fun createSendAllTransaction(
    //     recipient: String,
    //     feeRate: Float,
    //     enableRBF: Boolean,
    //     opReturnMsg: String?
    // ): PartiallySignedTransaction {
    //     val scriptPubkey: Script = Address(recipient).scriptPubkey()
    //     var txBuilder = TxBuilder()
    //         .drainWallet()
    //         .drainTo(scriptPubkey)
    //         .feeRate(satPerVbyte = feeRate)
    //
    //     if (enableRBF) {
    //         txBuilder = txBuilder.enableRbf()
    //     }
    //     if (!opReturnMsg.isNullOrEmpty()) {
    //         txBuilder = txBuilder.addData(opReturnMsg.toByteArray(charset = Charsets.UTF_8).asUByteArray().toList())
    //     }
    //     return txBuilder.finish(wallet).psbt
    // }

    // fun createBumpFeeTransaction(txid: String, feeRate: Float): PartiallySignedTransaction {
    //     return BumpFeeTxBuilder(txid = txid, newFeeRate = feeRate)
    //         .enableRbf()
    //         .finish(wallet = wallet)
    // }

    /**
     * Signs the provided [psbt] with the wallet's private keys.
     *
     * @return True if the PSBT was finalized after signing.
     */
    fun sign(psbt: Psbt): Boolean {
        return wallet.sign(psbt)
    }

    // fun broadcast(signedPsbt: Psbt): String {
    //     currentBlockchainClient?.broadcast(signedPsbt.extractTx())
    //         ?: throw IllegalStateException("Blockchain client not initialized")
    //     return signedPsbt.extractTx().computeTxid().toString()
    // }

    /** Returns all canonical transactions known to the wallet, both confirmed and pending. */
    private fun getAllTransactions(): List<CanonicalTx> = wallet.transactions()

    /**
     * Maps every wallet transaction into a UI-friendly [TxDetails] list.
     *
     * Calculates sent/received amounts, fees, fee rates, and confirmation status.
     */
    fun getAllTxDetails(): List<TxDetails> {
        val transactions = getAllTransactions()
        return transactions.map { tx ->
            val txid = tx.transaction.computeTxid()
            val (sent, received) = wallet.sentAndReceived(tx.transaction)
            var feeRate: FeeRate? = null
            var fee: Amount? = null
            // TODO: I don't know why we're getting negative fees here, but it looks like a bug
            try {
                fee = wallet.calculateFee(tx.transaction)
            } catch (e: Exception) {
                Log.e(TAG, "Error calculating fee rate for tx $txid: $e")
            }
            try {
                feeRate = wallet.calculateFeeRate(tx.transaction)
            } catch (e: Exception) {
                Log.e(TAG, "Error calculating fee for tx $txid: $e")
            }

            val (confirmationBlock, confirmationTimestamp, pending) =
                when (val position = tx.chainPosition) {
                    is ChainPosition.Unconfirmed -> {
                        Triple(null, null, true)
                    }

                    is ChainPosition.Confirmed -> {
                        Triple(
                            ConfirmationBlock(position.confirmationBlockTime.blockId.height),
                            Timestamp(position.confirmationBlockTime.confirmationTime),
                            false,
                        )
                    }
                }
            TxDetails(
                tx.transaction,
                txid.toString(),
                sent.toSat(),
                received.toSat(),
                fee?.toSat() ?: 0uL,
                feeRate,
                pending,
                confirmationBlock,
                confirmationTimestamp,
            )
        }
    }

    // fun getTransaction(txid: String): TransactionDetails? {
    //     val allTransactions = getAllTransactions()
    //     allTransactions.forEach {
    //         if (it.txid == txid) {
    //             return it
    //         }
    //     }
    //     return null
    // }

    /** Returns the total wallet balance in satoshis. */
    fun getBalance(): ULong = wallet.balance().total.toSat()

    /** Derives and returns the next unused external receiving address. */
    fun getNewAddress(): AddressInfo = wallet.revealNextAddress(KeychainKind.EXTERNAL)

    /** Applies a BDK [Update] (e.g. from a sync) to the wallet and persists the new state to the SQLite connection. */
    fun applyUpdate(update: Update) {
        wallet.applyUpdate(update)
        wallet.persist(connection)
        Log.i("KYOTOTEST", "Wallet applied a Kyoto update")
    }

    companion object {
        /**
         * Creates a new wallet with a freshly generated 12-word mnemonic.
         *
         * Derives descriptors, persists wallet metadata via [walletRepository], and opens a SQLite connection for BDK
         * state storage.
         */
        fun createWallet(
            newWalletConfig: NewWalletConfig,
            internalAppFilesPath: String,
            walletRepository: WalletRepository,
        ): Wallet {
            val mnemonic = Mnemonic(WordCount.WORDS12)
            val bip32ExtendedRootKey = DescriptorSecretKey(NetworkKind.TEST, mnemonic, null)
            val descriptor: Descriptor =
                createScriptAppropriateDescriptor(
                    newWalletConfig.scriptType,
                    bip32ExtendedRootKey,
                    KeychainKind.EXTERNAL,
                )
            val changeDescriptor: Descriptor =
                createScriptAppropriateDescriptor(
                    newWalletConfig.scriptType,
                    bip32ExtendedRootKey,
                    KeychainKind.INTERNAL,
                )
            val walletId = UUID.randomUUID().toString()
            val connection = Persister.newSqlite("$internalAppFilesPath/wallet-${walletId.take(8)}.sqlite3")

            // Create SingleWallet object for saving to datastore
            val newWalletForDatastore: StoredWallet =
                StoredWallet(
                    id = walletId,
                    name = newWalletConfig.name,
                    network = newWalletConfig.network.intoStored(),
                    scriptType = ActiveWalletScriptType.P2WPKH,
                    descriptor = descriptor.toStringWithSecret(),
                    changeDescriptor = changeDescriptor.toStringWithSecret(),
                    recoveryPhrase = mnemonic.toString(),
                )
            // TODO: launch this correctly, not on the main thread
            // Save the new wallet to the datastore
            runBlocking { walletRepository.addWallet(newWalletForDatastore) }

            val bdkWallet =
                BdkWallet(
                    descriptor = descriptor,
                    changeDescriptor = changeDescriptor,
                    network = newWalletConfig.network,
                    persister = connection,
                )

            val walletSecrets = WalletSecrets(descriptor, changeDescriptor, mnemonic.toString())

            return Wallet(
                wallet = bdkWallet,
                walletSecrets = walletSecrets,
                connection = connection,
                walletId = walletId,
                walletRepository = walletRepository,
                internalAppFilesPath = internalAppFilesPath,
                network = newWalletConfig.network,
            )
        }

        /** Restores a [Wallet] from a previously persisted [StoredWallet]. */
        fun loadActiveWallet(
            activeWallet: StoredWallet,
            internalAppFilesPath: String,
            walletRepository: WalletRepository,
        ): Wallet {
            val descriptor = Descriptor(activeWallet.descriptor, NetworkKind.TEST)
            val changeDescriptor = Descriptor(activeWallet.changeDescriptor, NetworkKind.TEST)
            val connection = Persister.newSqlite("$internalAppFilesPath/wallet-${activeWallet.id.take(8)}.sqlite3")
            val bdkWallet =
                BdkWallet.load(
                    descriptor = descriptor,
                    changeDescriptor = changeDescriptor,
                    persister = connection,
                )

            val walletSecrets = WalletSecrets(descriptor, changeDescriptor, activeWallet.recoveryPhrase)
            return Wallet(
                wallet = bdkWallet,
                walletSecrets = walletSecrets,
                connection = connection,
                walletId = activeWallet.id,
                walletRepository = walletRepository,
                internalAppFilesPath = internalAppFilesPath,
                network = activeWallet.network.intoDomain(),
            )
        }

        /**
         * Recovers a wallet from either a BIP-39 recovery phrase + script type or explicit descriptors.
         *
         * Persists the recovered metadata and opens a SQLite connection for BDK state storage.
         */
        fun recoverWallet(
            recoverWalletConfig: RecoverWalletConfig,
            internalAppFilesPath: String,
            walletRepository: WalletRepository,
        ): Wallet {
            Log.i(TAG, "Recovering wallet with config: $recoverWalletConfig")
            var descriptor: Descriptor? = null
            var changeDescriptor: Descriptor? = null
            var mnemonicString: String = ""

            // If there is a recovery phrase, we use it to recover the wallet
            if (recoverWalletConfig.recoveryPhrase != null && recoverWalletConfig.scriptType != null) {
                val mnemonic: Mnemonic = Mnemonic.fromString(recoverWalletConfig.recoveryPhrase)
                mnemonicString = mnemonic.toString()
                val bip32ExtendedRootKey = DescriptorSecretKey(NetworkKind.TEST, mnemonic, null)
                descriptor =
                    createScriptAppropriateDescriptor(
                        recoverWalletConfig.scriptType,
                        bip32ExtendedRootKey,
                        KeychainKind.EXTERNAL,
                    )
                changeDescriptor =
                    createScriptAppropriateDescriptor(
                        recoverWalletConfig.scriptType,
                        bip32ExtendedRootKey,
                        KeychainKind.INTERNAL,
                    )
            } else {
                descriptor = recoverWalletConfig.descriptor
                changeDescriptor = recoverWalletConfig.changeDescriptor
            }
            val walletId = UUID.randomUUID().toString()
            val connection = Persister.newSqlite("$internalAppFilesPath/wallet-${walletId.take(8)}.sqlite3")

            // Create SingleWallet object for saving to datastore
            val newWalletForDatastore: StoredWallet =
                StoredWallet(
                    id = walletId,
                    name = recoverWalletConfig.name,
                    network = recoverWalletConfig.network.intoStored(),
                    scriptType = ActiveWalletScriptType.P2WPKH,
                    descriptor = descriptor.toStringWithSecret(),
                    changeDescriptor = changeDescriptor.toStringWithSecret(),
                    recoveryPhrase = mnemonicString,
                )

            // TODO: launch this correctly, not on the main thread
            // Save the new wallet to the datastore
            runBlocking { walletRepository.addWallet(newWalletForDatastore) }

            val bdkWallet =
                BdkWallet(
                    descriptor = descriptor,
                    changeDescriptor = changeDescriptor,
                    persister = connection,
                    network = recoverWalletConfig.network,
                )

            val walletSecrets = WalletSecrets(descriptor, changeDescriptor, mnemonicString)
            return Wallet(
                wallet = bdkWallet,
                walletSecrets = walletSecrets,
                connection = connection,
                walletId = walletId,
                walletRepository = walletRepository,
                internalAppFilesPath = internalAppFilesPath,
                network = recoverWalletConfig.network,
            )
        }
    }
}

/**
 * Creates a BDK [Descriptor] matching the given [scriptType] and [keychain].
 *
 * @param scriptType Determines the descriptor template (e.g. BIP-84 for P2WPKH, BIP-86 for P2TR).
 * @param bip32ExtendedRootKey The root secret key from which to derive.
 * @param keychain External or internal keychain.
 */
fun createScriptAppropriateDescriptor(
    scriptType: ActiveWalletScriptType,
    bip32ExtendedRootKey: DescriptorSecretKey,
    keychain: KeychainKind,
): Descriptor {
    return when (scriptType) {
        ActiveWalletScriptType.P2WPKH -> Descriptor.newBip84(bip32ExtendedRootKey, keychain, NetworkKind.TEST)
        ActiveWalletScriptType.P2TR -> Descriptor.newBip86(bip32ExtendedRootKey, keychain, NetworkKind.TEST)
        ActiveWalletScriptType.UNKNOWN -> TODO()
    // ActiveWalletScriptType.UNRECOGNIZED -> TODO()
    }
}

/**
 * Sensitive material associated with a wallet.
 *
 * @property descriptor External descriptor with secret key material.
 * @property changeDescriptor Internal (change) descriptor with secret key material.
 * @property recoveryPhrase BIP-39 mnemonic phrase, if available.
 */
data class WalletSecrets(
    val descriptor: Descriptor,
    val changeDescriptor: Descriptor,
    val recoveryPhrase: String,
)
