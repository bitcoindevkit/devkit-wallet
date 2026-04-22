package org.bitcoindevkit.devkitwallet

import org.bitcoindevkit.devkitwallet.domain.utils.WifParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class WifParserTest {
    // ─── isLikelyWif ────────────────────────────────────────────────────────

    @Test
    fun `isLikelyWif accepts valid-looking testnet compressed WIF`() {
        // 'c' prefix + 51 more base-58 chars = 52 total
        val wif = "c" + "1".repeat(51)
        assertTrue(WifParser.isLikelyWif(wif))
    }

    @Test
    fun `isLikelyWif accepts valid-looking mainnet compressed WIF starting with K`() {
        val wif = "K" + "1".repeat(51)
        assertTrue(WifParser.isLikelyWif(wif))
    }

    @Test
    fun `isLikelyWif accepts valid-looking mainnet compressed WIF starting with L`() {
        val wif = "L" + "1".repeat(51)
        assertTrue(WifParser.isLikelyWif(wif))
    }

    @Test
    fun `isLikelyWif accepts valid-looking mainnet uncompressed WIF starting with 5`() {
        // '5' prefix + 50 more base-58 chars = 51 total
        val wif = "5" + "1".repeat(50)
        assertTrue(WifParser.isLikelyWif(wif))
    }

    @Test
    fun `isLikelyWif accepts valid-looking testnet uncompressed WIF starting with 9`() {
        val wif = "9" + "1".repeat(50)
        assertTrue(WifParser.isLikelyWif(wif))
    }

    @Test
    fun `isLikelyWif rejects string that is too short`() {
        assertFalse(WifParser.isLikelyWif("c" + "1".repeat(10)))
    }

    @Test
    fun `isLikelyWif rejects string that is too long`() {
        assertFalse(WifParser.isLikelyWif("c" + "1".repeat(55)))
    }

    @Test
    fun `isLikelyWif rejects string with wrong first character`() {
        // 'A' is not a valid WIF first character
        assertFalse(WifParser.isLikelyWif("A" + "1".repeat(51)))
    }

    @Test
    fun `isLikelyWif rejects string with non-base58 characters`() {
        // '0' is not in the base-58 alphabet
        val wif = "c" + "0".repeat(51)
        assertFalse(WifParser.isLikelyWif(wif))
    }

    @Test
    fun `isLikelyWif rejects a regular Bitcoin address`() {
        // A bech32 address is clearly not a WIF
        assertFalse(WifParser.isLikelyWif("tb1qw508d6qejxtdg4y5r3zarvary0c5xw7kxpjzsx"))
    }

    // ─── extract: raw input ──────────────────────────────────────────────────

    @Test
    fun `extract returns raw WIF when input is already a valid WIF`() {
        val wif = "c" + "1".repeat(51)
        assertEquals(wif, WifParser.extract(wif))
    }

    @Test
    fun `extract handles leading and trailing whitespace`() {
        val wif = "c" + "1".repeat(51)
        assertEquals(wif, WifParser.extract("  $wif  "))
    }

    // ─── extract: wif: prefix ────────────────────────────────────────────────

    @Test
    fun `extract strips wif colon prefix`() {
        val wif = "c" + "1".repeat(51)
        assertEquals(wif, WifParser.extract("wif:$wif"))
    }

    @Test
    fun `extract strips WIF colon prefix case-insensitively`() {
        val wif = "c" + "1".repeat(51)
        assertEquals(wif, WifParser.extract("WIF:$wif"))
    }

    // ─── extract: bitcoin URI ────────────────────────────────────────────────

    @Test
    fun `extract reads wif query parameter from bitcoin URI`() {
        val wif = "c" + "1".repeat(51)
        assertEquals(wif, WifParser.extract("bitcoin:?wif=$wif"))
    }

    @Test
    fun `extract reads privkey query parameter from bitcoin URI`() {
        val wif = "c" + "1".repeat(51)
        assertEquals(wif, WifParser.extract("bitcoin:?privkey=$wif"))
    }

    @Test
    fun `extract reads private_key query parameter from bitcoin URI`() {
        val wif = "c" + "1".repeat(51)
        assertEquals(wif, WifParser.extract("bitcoin:?private_key=$wif"))
    }

    @Test
    fun `extract reads privatekey query parameter from bitcoin URI`() {
        val wif = "c" + "1".repeat(51)
        assertEquals(wif, WifParser.extract("bitcoin:?privatekey=$wif"))
    }

    @Test
    fun `extract works when WIF param is not the first query parameter`() {
        val wif = "c" + "1".repeat(51)
        assertEquals(wif, WifParser.extract("bitcoin:tb1qsomeaddress?amount=0.001&wif=$wif"))
    }

    // ─── extract: negative cases ─────────────────────────────────────────────

    @Test
    fun `extract returns null for a random non-WIF string`() {
        assertNull(WifParser.extract("12cUi8cuUJRiFmGEu4jCAsonSS1dkVyaD7Aoo6URRiXpmaokikuyM778786"))
    }

    @Test
    fun `extract returns null for a bech32 Bitcoin address`() {
        assertNull(WifParser.extract("tb1qw508d6qejxtdg4y5r3zarvary0c5xw7kxpjzsx"))
    }

    @Test
    fun `extract returns null for an empty string`() {
        assertNull(WifParser.extract(""))
    }

    @Test
    fun `extract returns null for a bitcoin URI without a WIF param`() {
        assertNull(WifParser.extract("bitcoin:tb1qsomeaddress?amount=0.001"))
    }

    @Test
    fun `extract returns null for a bitcoin URI with unrecognised param key`() {
        val wif = "c" + "1".repeat(51)
        // "key" is not a recognised param name
        assertNull(WifParser.extract("bitcoin:?key=$wif"))
    }

    // ─── extract: real WIF across all formats ───────────────────────────────

    @Test
    fun `extract handles real testnet WIF in all supported formats`() {
        val wif = "cUkUX6eBYEiXULiJiDz5Cgvm5DQAZsMEw3mC6qd275kW6dk9hY8y"
        assertTrue("Real WIF should pass isLikelyWif", WifParser.isLikelyWif(wif))
        assertEquals(wif, WifParser.extract(wif))
        assertEquals(wif, WifParser.extract("wif:$wif"))
        assertEquals(wif, WifParser.extract("WIF:$wif"))
        assertEquals(wif, WifParser.extract("bitcoin:?wif=$wif"))
        assertEquals(wif, WifParser.extract("bitcoin:?privkey=$wif"))
        assertEquals(wif, WifParser.extract("  $wif\n"))
    }
}
