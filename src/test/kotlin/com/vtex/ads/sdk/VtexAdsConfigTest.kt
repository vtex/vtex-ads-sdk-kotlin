package com.vtex.ads.sdk

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Duration.Companion.seconds

class VtexAdsConfigTest {

    @Test
    fun `should create config with valid parameters`() {
        val config = VtexAdsConfig(
            publisherId = "test-publisher-id",
            channel = Channel.WEB
        )

        assertEquals("test-publisher-id", config.publisherId)
        assertEquals(Channel.WEB, config.channel)
        assertEquals(VtexAdsConfig.DEFAULT_BASE_URL, config.baseUrl)
        assertEquals(30.seconds, config.timeout)
        assertEquals(3, config.maxRetries)
        assertEquals(false, config.debug)
    }

    @Test
    fun `should create config with all channels`() {
        Channel.values().forEach { channel ->
            val config = VtexAdsConfig(
                publisherId = "test-publisher-id",
                channel = channel
            )
            assertEquals(channel, config.channel)
        }
    }

    @Test
    fun `should fail with blank publisher id`() {
        assertFailsWith<IllegalArgumentException> {
            VtexAdsConfig(
                publisherId = "",
                channel = Channel.WEB
            )
        }
    }

    @Test
    fun `should fail with negative timeout`() {
        assertFailsWith<IllegalArgumentException> {
            VtexAdsConfig(
                publisherId = "test-publisher-id",
                channel = Channel.WEB,
                timeout = (-1).seconds
            )
        }
    }

    @Test
    fun `should fail with negative max retries`() {
        assertFailsWith<IllegalArgumentException> {
            VtexAdsConfig(
                publisherId = "test-publisher-id",
                channel = Channel.WEB,
                maxRetries = -1
            )
        }
    }
}
