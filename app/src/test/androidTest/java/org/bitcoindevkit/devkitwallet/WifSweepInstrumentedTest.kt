package org.bitcoindevkit.devkitwallet

import org.bitcoindevkit.Descriptor
import org.bitcoindevkit.Network
import org.bitcoindevkit.devkitwallet.domain.utils.WifParser
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4

@RunWith(AndroidJUnit4::class)
class WifSweepInstrumentedTest {

    @Test
    fun bdkDescriptorRejectsInvalidChecksumWif() {
        val fakeWif = "c" + "1".repeat(51)
        assertTrue("Fake WIF should pass the heuristic", WifParser.isLikelyWif(fakeWif))

        try {
            Descriptor("wpkh($fakeWif)", Network.TESTNET)
            fail("BDK should have thrown on an invalid-checksum WIF")
        } catch (e: Exception) {
            // Expected — BDK correctly rejected the key
            assertNotNull("Exception message should not be null", e.message)
        }
    }

    @Test
    fun bdkBuildsDescriptorsFromRealWif() {
        val wif = "cS7h4T8nZ2wDBWk271QdvPeAnvGUoP1tXQKGMUYKkkFq1Tk6aKoP"
        assertTrue(WifParser.isLikelyWif(wif))
    
        val types = listOf("pkh($wif)", "wpkh($wif)", "sh(wpkh($wif))", "tr($wif)")
        for (descStr in types) {
            val descriptor = Descriptor(descStr, Network.TESTNET)
            assertNotNull("Descriptor for $descStr should not be null", descriptor)
        }
    }
}
