package org.bitcoindevkit.devkitwallet.data.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import java.io.InputStream
import java.io.OutputStream
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

@Serializable
enum class ActiveWalletNetwork {
    TESTNET,
    SIGNET,
    REGTEST,
}

@Serializable
enum class ActiveWalletScriptType {
    P2WPKH,
    P2TR,
    UNKNOWN,
}

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

@Serializable
data class WalletData(
    val wallets: List<StoredWallet> = emptyList()
    // network config fields go here alongside wallets
)

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
