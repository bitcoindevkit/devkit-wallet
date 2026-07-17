package org.bitcoindevkit.devkitwallet.data.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import java.io.InputStream
import java.io.OutputStream
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

/** Bitcoin networks supported by the app. */
@Serializable
enum class ActiveWalletNetwork {
    TESTNET,
    SIGNET,
    REGTEST,
}

/** Address script types supported by the app. */
@Serializable
enum class ActiveWalletScriptType {
    P2WPKH,
    P2TR,
    UNKNOWN,
}

/**
 * Serializable representation of a single wallet saved to local DataStore storage.
 *
 * @property id Unique wallet identifier.
 * @property name User-facing wallet label.
 * @property network The network this wallet was created for.
 * @property scriptType The script type used for address derivation.
 * @property descriptor External descriptor string (includes secret keys).
 * @property changeDescriptor Internal descriptor string (includes secret keys).
 * @property recoveryPhrase BIP-39 mnemonic, or empty string if not known.
 * @property fullScanCompleted True if a full blockchain scan has been completed at least once.
 */
@Serializable
data class StoredWallet(
    val id: String,
    val name: String,
    val network: ActiveWalletNetwork,
    val scriptType: ActiveWalletScriptType,
    val descriptor: String,
    val changeDescriptor: String,
    val recoveryPhrase: String = "",
    val fullScanCompleted: Boolean = false,
)

/**
 * Root container for all wallet metadata persisted by the app.
 *
 * @property wallets List of stored wallets.
 */
@Serializable
data class WalletData(
    val wallets: List<StoredWallet> = emptyList()
    // network config fields go here alongside wallets
)

/** [Serializer] implementation for [WalletData] using kotlinx.serialization JSON. */
object WalletDataSerializer : Serializer<WalletData> {
    override val defaultValue = WalletData()

    override suspend fun readFrom(input: InputStream): WalletData {
        try {
            return Json.decodeFromString(input.readBytes().decodeToString())
        } catch (e: SerializationException) {
            throw CorruptionException("Cannot read AppSettings.", e)
        }
    }

    override suspend fun writeTo(t: WalletData, output: OutputStream) {
        output.write(Json.encodeToString(t).encodeToByteArray())
    }
}
