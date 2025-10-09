package com.vtex.ads.sdk

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Duration.Companion.seconds

class VtexAdsConfigTest {

    @Test
    fun `should create config with valid parameters`() {
        val config = VtexAdsConfig(
            apiKey = "test-api-key",
            accountName = "test-account"
        )

        assertEquals("test-api-key", config.apiKey)
        assertEquals("test-account", config.accountName)
        assertEquals(VtexAdsConfig.DEFAULT_BASE_URL, config.baseUrl)
        assertEquals(30.seconds, config.timeout)
        assertEquals(3, config.maxRetries)
        assertEquals(false, config.debug)
    }

    @Test
    fun `should fail with blank api key`() {
        assertFailsWith<IllegalArgumentException> {
            VtexAdsConfig(
                apiKey = "",
                accountName = "test-account"
            )
        }
    }

    @Test
    fun `should fail with blank account name`() {
        assertFailsWith<IllegalArgumentException> {
            VtexAdsConfig(
                apiKey = "test-api-key",
                accountName = ""
            )
        }
    }

    @Test
    fun `should fail with negative timeout`() {
        assertFailsWith<IllegalArgumentException> {
            VtexAdsConfig(
                apiKey = "test-api-key",
                accountName = "test-account",
                timeout = (-1).seconds
            )
        }
    }

    @Test
    fun `should fail with negative max retries`() {
        assertFailsWith<IllegalArgumentException> {
            VtexAdsConfig(
                apiKey = "test-api-key",
                accountName = "test-account",
                maxRetries = -1
            )
        }
    }
}
