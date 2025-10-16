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
            sessionIdProvider = { "test-session" },
            userIdProvider = { "test-user" },
            channel = Channel.SITE,
            brand = "test-brand"
        )

        assertEquals("test-publisher", config.publisherId)
        assertEquals("test-session", config.getSessionId())
        assertEquals("test-user", config.getUserId())
        assertEquals(Channel.SITE, config.channel)
        assertEquals("test-brand", config.brand)
        assertEquals(500L, config.timeout)  // Default is 500ms
    }

    @Test
    fun `should fail with blank publisher id`() {
        assertFailsWith<IllegalArgumentException> {
            VtexAdsClientConfig(
                publisherId = "",
                sessionIdProvider = { "test-session" },
                channel = Channel.SITE
            )
        }
    }

    @Test
    fun `should fail with blank session id`() {
        val config = VtexAdsClientConfig(
            publisherId = "test-publisher",
            sessionIdProvider = { "" },
            channel = Channel.SITE
        )
        
        assertFailsWith<IllegalArgumentException> {
            config.getSessionId()
        }
    }

    @Test
    fun `should fail with negative timeout`() {
        assertFailsWith<IllegalArgumentException> {
            VtexAdsClientConfig(
                publisherId = "test-publisher",
                sessionIdProvider = { "test-session" },
                channel = Channel.SITE,
                timeout = -1
            )
        }
    }

    @Test
    fun `should allow null userId and brand`() {
        val config = VtexAdsClientConfig(
            publisherId = "test-publisher",
            sessionIdProvider = { "test-session" },
            userIdProvider = null,
            channel = Channel.MSITE,
            brand = null
        )

        assertEquals(null, config.getUserId())
        assertEquals(null, config.brand)
    }

    @Test
    fun `should call sessionId provider function`() {
        var callCount = 0
        val config = VtexAdsClientConfig(
            publisherId = "test-publisher",
            sessionIdProvider = { 
                callCount++
                "session-$callCount"
            },
            channel = Channel.SITE
        )

        assertEquals("session-1", config.getSessionId())
        assertEquals("session-2", config.getSessionId())
        assertEquals(2, callCount)
    }

    @Test
    fun `should call userId provider function`() {
        var callCount = 0
        val config = VtexAdsClientConfig(
            publisherId = "test-publisher",
            sessionIdProvider = { "test-session" },
            userIdProvider = { 
                callCount++
                "user-$callCount"
            },
            channel = Channel.SITE
        )

        assertEquals("user-1", config.getUserId())
        assertEquals("user-2", config.getUserId())
        assertEquals(2, callCount)
    }

    @Test
    fun `should fail when sessionId provider returns blank`() {
        val config = VtexAdsClientConfig(
            publisherId = "test-publisher",
            sessionIdProvider = { "" },
            channel = Channel.SITE
        )

        assertFailsWith<IllegalArgumentException> {
            config.getSessionId()
        }
    }
}
