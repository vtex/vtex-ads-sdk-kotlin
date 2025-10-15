package com.vtex.ads.sdk

import com.vtex.ads.sdk.models.Channel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class VtexAdsClientUserIdTest {

    @Test
    fun `should create client with userId`() {
        val client = VtexAdsClient(
            publisherId = "pub-123",
            sessionIdProvider = { "session-456" },
            userIdProvider = { "user-789" },
            channel = Channel.SITE
        )

        assertEquals("user-789", client.getCurrentUserId())

        client.close()
    }

    @Test
    fun `should create client without userId`() {
        val client = VtexAdsClient(
            publisherId = "pub-123",
            sessionIdProvider = { "session-456" },
            userIdProvider = null,
            channel = Channel.SITE
        )

        assertNull(client.getCurrentUserId())

        client.close()
    }

    @Test
    fun `should update userId from null to value`() {
        val client = VtexAdsClient(
            publisherId = "pub-123",
            sessionIdProvider = { "session-456" },
            userIdProvider = null,
            channel = Channel.SITE
        )

        assertNull(client.getCurrentUserId())

        client.updateUserId("user-new")

        assertEquals("user-new", client.getCurrentUserId())

        client.close()
    }

    @Test
    fun `should update userId from value to new value`() {
        val client = VtexAdsClient(
            publisherId = "pub-123",
            sessionIdProvider = { "session-456" },
            userIdProvider = { "user-old" },
            channel = Channel.SITE
        )

        assertEquals("user-old", client.getCurrentUserId())

        client.updateUserId("user-new")

        assertEquals("user-new", client.getCurrentUserId())

        client.close()
    }

    @Test
    fun `should update userId from value to null`() {
        val client = VtexAdsClient(
            publisherId = "pub-123",
            sessionIdProvider = { "session-456" },
            userIdProvider = { "user-123" },
            channel = Channel.SITE
        )

        assertEquals("user-123", client.getCurrentUserId())

        client.updateUserId(null)

        assertNull(client.getCurrentUserId())

        client.close()
    }

    @Test
    fun `should fail to update userId with blank string`() {
        val client = VtexAdsClient(
            publisherId = "pub-123",
            sessionIdProvider = { "session-456" },
            userIdProvider = { "user-123" },
            channel = Channel.SITE
        )

        assertFailsWith<IllegalArgumentException> {
            client.updateUserId("")
        }

        assertFailsWith<IllegalArgumentException> {
            client.updateUserId("   ")
        }

        client.close()
    }

    @Test
    fun `should propagate userId update to both services`() {
        val client = VtexAdsClient(
            publisherId = "pub-123",
            sessionIdProvider = { "session-456" },
            userIdProvider = null,
            channel = Channel.SITE
        )

        assertNull(client.ads.getCurrentUserId())
        assertNull(client.events.getCurrentUserId())

        client.updateUserId("user-propagated")

        assertEquals("user-propagated", client.ads.getCurrentUserId())
        assertEquals("user-propagated", client.events.getCurrentUserId())

        client.close()
    }

    @Test
    fun `should maintain userId consistency across services`() {
        val client = VtexAdsClient(
            publisherId = "pub-123",
            sessionIdProvider = { "session-456" },
            userIdProvider = { "user-initial" },
            channel = Channel.SITE
        )

        assertEquals(client.ads.getCurrentUserId(), client.events.getCurrentUserId())
        assertEquals(client.getCurrentUserId(), client.ads.getCurrentUserId())
        assertEquals(client.getCurrentUserId(), client.events.getCurrentUserId())

        client.updateUserId("user-updated")

        assertEquals(client.ads.getCurrentUserId(), client.events.getCurrentUserId())
        assertEquals(client.getCurrentUserId(), client.ads.getCurrentUserId())

        client.close()
    }
}
