package com.vtex.ads.sdk.utils

import java.security.MessageDigest

/**
 * Utility functions for hashing sensitive data.
 */
object HashUtils {

    /**
     * Generates SHA-256 hash of the input string.
     * Used for hashing sensitive customer data (email, phone, document, etc.)
     * as required by VTEX Ads API.
     *
     * @param text The text to hash
     * @return Hexadecimal string representation of the SHA-256 hash
     */
    fun sha256(text: String): String {
        return MessageDigest
            .getInstance("SHA-256")
            .digest(text.toByteArray())
            .joinToString("") { "%02x".format(it) }
    }

    /**
     * Generates SHA-256 hash of the input string if not null or blank.
     * Returns null if input is null or blank.
     *
     * @param text The text to hash (nullable)
     * @return Hexadecimal string representation of the SHA-256 hash, or null
     */
    fun sha256OrNull(text: String?): String? {
        return if (text.isNullOrBlank()) null else sha256(text)
    }
}
