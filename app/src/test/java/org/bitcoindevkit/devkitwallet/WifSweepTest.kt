package org.bitcoindevkit.devkitwallet

import org.bitcoindevkit.devkitwallet.domain.utils.WifParser
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class WifSweepTest {
    @Test
    fun wifParserRejectsShortPlaceholderKey() {
        val placeholder = "cTjZ2BfARk264q9s3uH8Yk3fKqK3wT95JzFp8s1tXvR9k" // 45 chars
        assertFalse(
            "A 45-char string must NOT pass isLikelyWif (WIF must be 51 or 52 chars)",
            WifParser.isLikelyWif(placeholder),
        )
    }

    @Test
    fun wifParserAcceptsCorrectLengthTestnetCompressedKey() {
        val wif = "c" + "1".repeat(51)
        assertTrue(
            "A 52-char base58 string starting with 'c' should pass the heuristic",
            WifParser.isLikelyWif(wif),
        )
    }

    @Test
    fun extractReturnsNullForBitcoinAddress() {
        assertNull(
            "A bech32 address must not be mistaken for a WIF",
            WifParser.extract("tb1qw508d6qejxtdg4y5r3zarvary0c5xw7kxpjzsx"),
        )
    }

    @Test
    fun extractReturnsCandidateFromBitcoinUri() {
        val wif = "c" + "1".repeat(51)
        val result = WifParser.extract("bitcoin:?wif=$wif")
        assertTrue(
            "WIF extracted from a bitcoin URI should pass isLikelyWif",
            result != null && WifParser.isLikelyWif(result),
        )
    }
}
