package com.vtex.ads.sdk

import com.vtex.ads.sdk.models.Channel
import com.vtex.ads.sdk.models.ConversionRequest
import com.vtex.ads.sdk.models.ConversionItem
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class VtexAdsLoggingIntegrationTest {

    @Test
    fun `deliveryBeaconEvent should log impression with correct template`() {
        val writer = RecordingWriter()
        val client = VtexAdsClient(
            publisherId = "test-publisher",
            sessionIdProvider = { "test-session" },
            userIdProvider = { "test-user" },
            channel = Channel.SITE,
            debug = debugOf(VtexAdsDebug.EVENTS_IMPRESSION),
            debugFunction = writer
        )

        // This will fail the actual HTTP call, but we're testing the logging
        client.events.deliveryBeaconEvent(
            eventUrl = "https://example.com/impression?ad_id=123",
            placement = "home.hero"
        )

        // Wait a bit for async operation
        Thread.sleep(100)

        val eventsEntries = writer.getEntriesWithLabel("VtexAds/Events")
        assertTrue(eventsEntries.isNotEmpty())
        
        val successEntry = eventsEntries.find { it.message.contains("impression success") }
        assertTrue(successEntry != null)
        assertTrue(successEntry!!.message.contains("placement=home.hero"))
        
        client.close()
    }

    @Test
    fun `deliveryBeaconEvent should log view with correct template`() {
        val writer = RecordingWriter()
        val client = VtexAdsClient(
            publisherId = "test-publisher",
            sessionIdProvider = { "test-session" },
            userIdProvider = { "test-user" },
            channel = Channel.SITE,
            debug = debugOf(VtexAdsDebug.EVENTS_VIEW),
            debugFunction = writer
        )

        client.events.deliveryBeaconEvent(
            eventUrl = "https://example.com/view?ad_id=456",
            placement = "search.top"
        )

        Thread.sleep(100)

        val eventsEntries = writer.getEntriesWithLabel("VtexAds/Events")
        assertTrue(eventsEntries.isNotEmpty())
        
        val successEntry = eventsEntries.find { it.message.contains("view success") }
        assertTrue(successEntry != null)
        assertTrue(successEntry!!.message.contains("placement=search.top"))
        
        client.close()
    }

    @Test
    fun `deliveryBeaconEvent should log click with correct template`() {
        val writer = RecordingWriter()
        val client = VtexAdsClient(
            publisherId = "test-publisher",
            sessionIdProvider = { "test-session" },
            userIdProvider = { "test-user" },
            channel = Channel.SITE,
            debug = debugOf(VtexAdsDebug.EVENTS_CLICK),
            debugFunction = writer
        )

        client.events.deliveryBeaconEvent(
            eventUrl = "https://example.com/click?ad_id=789",
            placement = "category.banner"
        )

        Thread.sleep(100)

        val eventsEntries = writer.getEntriesWithLabel("VtexAds/Events")
        assertTrue(eventsEntries.isNotEmpty())
        
        val successEntry = eventsEntries.find { it.message.contains("click success") }
        assertTrue(successEntry != null)
        assertTrue(successEntry!!.message.contains("placement=category.banner"))
        
        client.close()
    }

    @Test
    fun `sendConversion should log with correct template`() {
        val writer = RecordingWriter()
        val client = VtexAdsClient(
            publisherId = "test-publisher",
            sessionIdProvider = { "test-session" },
            userIdProvider = { "test-user" },
            channel = Channel.SITE,
            debug = debugOf(VtexAdsDebug.EVENTS_CONVERSION),
            debugFunction = writer
        )

        val conversionRequest = ConversionRequest(
            publisherId = "test-publisher",
            userId = "test-user",
            sessionId = "test-session",
            orderId = "order-123",
            createdAt = "2024-01-01T00:00:00Z",
            items = listOf(
                ConversionItem("SKU-1", 2, 99.99),
                ConversionItem("SKU-2", 1, 149.99)
            ),
            channel = Channel.SITE,
            emailHashed = "hashed-email",
            phoneHashed = "hashed-phone"
        )

        client.events.sendConversion(conversionRequest)

        Thread.sleep(100)

        val eventsEntries = writer.getEntriesWithLabel("VtexAds/Events")
        assertTrue(eventsEntries.isNotEmpty())
        
        val successEntry = eventsEntries.find { it.message.contains("conversion success") }
        assertTrue(successEntry != null)
        assertTrue(successEntry!!.message.contains("orderId=order-123"))
        assertTrue(successEntry.message.contains("userId=test-user"))
        assertTrue(successEntry.message.contains("items=2"))
        
        client.close()
    }

    @Test
    fun `EVENTS_ALL should log all event types but not ADS_LOAD`() {
        val writer = RecordingWriter()
        val client = VtexAdsClient(
            publisherId = "test-publisher",
            sessionIdProvider = { "test-session" },
            userIdProvider = { "test-user" },
            channel = Channel.SITE,
            debug = debugOf(VtexAdsDebug.EVENTS_ALL),
            debugFunction = writer
        )

        // Send different event types
        client.events.deliveryBeaconEvent("https://example.com/impression?ad_id=1", "home.hero")
        client.events.deliveryBeaconEvent("https://example.com/view?ad_id=2", "search.top")
        client.events.deliveryBeaconEvent("https://example.com/click?ad_id=3", "category.banner")

        Thread.sleep(100)

        val eventsEntries = writer.getEntriesWithLabel("VtexAds/Events")
        val adsLoadEntries = writer.getEntriesWithLabel("VtexAds/AdsLoad")
        
        // Should have logged events but not ads load
        assertTrue(eventsEntries.isNotEmpty())
        assertEquals(0, adsLoadEntries.size)
        
        // Check that all event types were logged
        assertTrue(eventsEntries.any { it.message.contains("impression success") })
        assertTrue(eventsEntries.any { it.message.contains("view success") })
        assertTrue(eventsEntries.any { it.message.contains("click success") })
        
        client.close()
    }

    @Test
    fun `ADS_LOAD should be independent of EVENTS_ALL`() {
        val writer = RecordingWriter()
        val client = VtexAdsClient(
            publisherId = "test-publisher",
            sessionIdProvider = { "test-session" },
            userIdProvider = { "test-user" },
            channel = Channel.SITE,
            debug = debugOf(VtexAdsDebug.ADS_LOAD),
            debugFunction = writer
        )

        // Send event (should not be logged)
        client.events.deliveryBeaconEvent("https://example.com/impression?ad_id=1", "home.hero")

        Thread.sleep(100)

        val eventsEntries = writer.getEntriesWithLabel("VtexAds/Events")
        val adsLoadEntries = writer.getEntriesWithLabel("VtexAds/AdsLoad")
        
        // Should not have logged events
        assertEquals(0, eventsEntries.size)
        
        client.close()
    }

    @Test
    fun `empty debug set should not log anything`() {
        val writer = RecordingWriter()
        val client = VtexAdsClient(
            publisherId = "test-publisher",
            sessionIdProvider = { "test-session" },
            userIdProvider = { "test-user" },
            channel = Channel.SITE,
            debug = emptySet(),
            debugFunction = writer
        )

        // Send events and try to load ads
        client.events.deliveryBeaconEvent("https://example.com/impression?ad_id=1", "home.hero")

        Thread.sleep(100)

        // Should not have logged anything
        assertEquals(0, writer.entries.size)
        
        client.close()
    }

    @Test
    fun `should handle debug function exceptions gracefully`() {
        val client = VtexAdsClient(
            publisherId = "test-publisher",
            sessionIdProvider = { "test-session" },
            userIdProvider = { "test-user" },
            channel = Channel.SITE,
            debug = debugOf(VtexAdsDebug.EVENTS_ALL),
            debugFunction = { _, _ -> throw RuntimeException("Debug function error") }
        )

        // Should not throw exception
        client.events.deliveryBeaconEvent("https://example.com/impression?ad_id=1", "home.hero")

        Thread.sleep(100)
        
        client.close()
    }
}
