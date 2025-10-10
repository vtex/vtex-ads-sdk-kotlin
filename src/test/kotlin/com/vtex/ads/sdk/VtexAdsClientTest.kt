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
            sessionId = "test-session",
            userId = "test-user",
            channel = Channel.SITE,
            brand = "test-brand"
        )

        assertNotNull(client.config)
        assertEquals("test-publisher", client.config.publisherId)
        assertEquals("test-session", client.config.sessionId)
        assertEquals("test-user", client.config.userId)
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
            sessionId = "test-session",
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
            sessionId = "test-session",
            channel = Channel.APP
        )

        assertNotNull(client)
        assertEquals("test-publisher", client.config.publisherId)
        assertEquals("test-session", client.config.sessionId)
        assertEquals(null, client.config.userId)
        assertEquals(null, client.config.brand)

        client.close()
    }

    @Test
    fun `should create client with custom timeout`() {
        val client = VtexAdsClient(
            publisherId = "test-publisher",
            sessionId = "test-session",
            channel = Channel.SITE,
            timeout = 1000L
        )

        assertEquals(1000L, client.config.timeout)

        client.close()
    }
}
