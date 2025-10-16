package com.vtex.ads.sdk

import com.vtex.ads.sdk.models.Channel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class VtexAdsClientTest {

    @Test
    fun `should create client with invoke operator`() {
        val client = VtexAdsClient(
            publisherId = "test-publisher",
            sessionIdProvider = { "test-session" },
            userIdProvider = { "test-user" },
            channel = Channel.SITE,
            brand = "test-brand"
        )

        assertNotNull(client.config)
        assertEquals("test-publisher", client.config.publisherId)
        assertEquals("test-session", client.config.getSessionId())
        assertEquals("test-user", client.config.getUserId())
        assertEquals(Channel.SITE, client.config.channel)
        assertEquals("test-brand", client.config.brand)

        assertNotNull(client.ads)
        assertNotNull(client.events)

        client.close()
    }

    @Test
    fun `should create client with config object`() {
        val config = VtexAdsClientConfig(
            publisherId = "test-publisher",
            sessionIdProvider = { "test-session" },
            channel = Channel.MSITE
        )

        val client = VtexAdsClient(config)

        assertNotNull(client.config)
        assertEquals(config, client.config)

        assertNotNull(client.ads)
        assertNotNull(client.events)

        client.close()
    }

    @Test
    fun `should have correct SDK version`() {
        assertEquals("0.1.0-SNAPSHOT", VtexAdsClient.VERSION)
    }

    @Test
    fun `should create client with minimal parameters`() {
        val client = VtexAdsClient(
            publisherId = "test-publisher",
            sessionIdProvider = { "test-session" },
            channel = Channel.APP
        )

        assertNotNull(client)
        assertEquals("test-publisher", client.config.publisherId)
        assertEquals("test-session", client.config.getSessionId())
        assertEquals(null, client.config.getUserId())
        assertEquals(null, client.config.brand)

        client.close()
    }

    @Test
    fun `should create client with custom timeout`() {
        val client = VtexAdsClient(
            publisherId = "test-publisher",
            sessionIdProvider = { "test-session" },
            channel = Channel.SITE,
            timeout = 1000L
        )

        assertEquals(1000L, client.config.timeout)

        client.close()
    }

    @Test
    fun `should create client with static values for backward compatibility`() {
        val client = VtexAdsClient.createWithStaticValues(
            publisherId = "test-publisher",
            sessionId = "test-session",
            userId = "test-user",
            channel = Channel.SITE
        )

        assertNotNull(client)
        assertEquals("test-publisher", client.config.publisherId)
        assertEquals("test-session", client.config.getSessionId())
        assertEquals("test-user", client.config.getUserId())
        assertEquals(Channel.SITE, client.config.channel)

        client.close()
    }
}
