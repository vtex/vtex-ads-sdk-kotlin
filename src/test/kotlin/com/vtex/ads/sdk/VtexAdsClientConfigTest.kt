package com.vtex.ads.sdk

import com.vtex.ads.sdk.models.Channel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class VtexAdsClientConfigTest {

    @Test
    fun `should create config with valid parameters`() {
        val config = VtexAdsClientConfig(
            publisherId = "test-publisher",
            sessionId = "test-session",
            userId = "test-user",
            channel = Channel.SITE,
            brand = "test-brand"
        )

        assertEquals("test-publisher", config.publisherId)
        assertEquals("test-session", config.sessionId)
        assertEquals("test-user", config.userId)
        assertEquals(Channel.SITE, config.channel)
        assertEquals("test-brand", config.brand)
        assertEquals(500L, config.timeout)  // Default is 500ms
    }

    @Test
    fun `should fail with blank publisher id`() {
        assertFailsWith<IllegalArgumentException> {
            VtexAdsClientConfig(
                publisherId = "",
                sessionId = "test-session",
                channel = Channel.SITE
            )
        }
    }

    @Test
    fun `should fail with blank session id`() {
        assertFailsWith<IllegalArgumentException> {
            VtexAdsClientConfig(
                publisherId = "test-publisher",
                sessionId = "",
                channel = Channel.SITE
            )
        }
    }

    @Test
    fun `should fail with negative timeout`() {
        assertFailsWith<IllegalArgumentException> {
            VtexAdsClientConfig(
                publisherId = "test-publisher",
                sessionId = "test-session",
                channel = Channel.SITE,
                timeout = -1
            )
        }
    }

    @Test
    fun `should allow null userId and brand`() {
        val config = VtexAdsClientConfig(
            publisherId = "test-publisher",
            sessionId = "test-session",
            userId = null,
            channel = Channel.MSITE,
            brand = null
        )

        assertEquals(null, config.userId)
        assertEquals(null, config.brand)
    }
}
