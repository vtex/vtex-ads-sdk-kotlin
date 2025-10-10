package com.vtex.ads.sdk.utils

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class HashUtilsTest {

    @Test
    fun `should generate SHA256 hash`() {
        val text = "test@email.com"

        val hash = HashUtils.sha256(text)

        // Verify it's a valid SHA-256 hash (64 characters, hex)
        assertEquals(64, hash.length)
        assert(hash.matches(Regex("[a-f0-9]{64}")))
    }

    @Test
    fun `should generate consistent hash for same input`() {
        val text = "user@example.com"

        val hash1 = HashUtils.sha256(text)
        val hash2 = HashUtils.sha256(text)

        assertEquals(hash1, hash2)
    }

    @Test
    fun `should generate different hashes for different inputs`() {
        val text1 = "user1@example.com"
        val text2 = "user2@example.com"

        val hash1 = HashUtils.sha256(text1)
        val hash2 = HashUtils.sha256(text2)

        assert(hash1 != hash2)
    }

    @Test
    fun `should hash phone number correctly`() {
        val phone = "11999999999"

        val hash = HashUtils.sha256(phone)

        // Verify it's a valid SHA-256 hash (64 characters, hex)
        assertEquals(64, hash.length)
        assert(hash.matches(Regex("[a-f0-9]{64}")))
    }

    @Test
    fun `sha256OrNull should return hash for non-blank string`() {
        val text = "test"

        val hash = HashUtils.sha256OrNull(text)

        assertEquals(HashUtils.sha256(text), hash)
    }

    @Test
    fun `sha256OrNull should return null for null input`() {
        val hash = HashUtils.sha256OrNull(null)

        assertNull(hash)
    }

    @Test
    fun `sha256OrNull should return null for blank input`() {
        val hash = HashUtils.sha256OrNull("   ")

        assertNull(hash)
    }

    @Test
    fun `sha256OrNull should return null for empty input`() {
        val hash = HashUtils.sha256OrNull("")

        assertNull(hash)
    }
}
