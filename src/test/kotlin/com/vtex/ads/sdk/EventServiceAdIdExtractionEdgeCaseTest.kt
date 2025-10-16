package com.vtex.ads.sdk

import com.vtex.ads.sdk.models.Channel
import kotlin.test.Test
import kotlin.test.assertTrue

class EventServiceAdIdExtractionEdgeCaseTest {

    @Test
    fun `should extract adId from complex version patterns`() {
        val writer = RecordingWriter()
        val client = VtexAdsClient(
            publisherId = "test-publisher",
            sessionIdProvider = { "test-session" },
            userIdProvider = { "test-user" },
            channel = Channel.SITE,
            debug = debugOf(VtexAdsDebug.EVENTS_VIEW),
            debugFunction = writer
        )

        // Test complex version patterns
        val complexUrls = listOf(
            "https://events.example.com/v134-beta/beacon/view/0498230948324?param=value",
            "https://events.example.com/v2.1/beacon/impression/ad-123",
            "https://events.example.com/v10-alpha/beacon/click/ad-456",
            "https://events.example.com/v999-gamma/beacon/conversion/ad-789",
            "https://events.example.com/v1.2.3/beacon/view/ad-abc",
            "https://events.example.com/v2024/beacon/impression/ad-xyz"
        )

        complexUrls.forEachIndexed { index, url ->
            client.events.deliveryBeaconEvent(
                eventUrl = url,
                placement = "test.placement.$index"
            )
        }

        Thread.sleep(200)

        val eventsEntries = writer.getEntriesWithLabel("VtexAds/Events")
        assertTrue(eventsEntries.isNotEmpty(), "Expected event log entries")
        
        val successEntries = eventsEntries.filter { it.message.contains("success") }
        assertTrue(successEntries.isNotEmpty(), "Expected success log entries")
        
        // Check if adIds are extracted from complex patterns
        val hasComplexAdIds = successEntries.any { entry ->
            entry.message.contains("adId=") && 
            (entry.message.contains("0498230948324") || 
             entry.message.contains("ad-123") || 
             entry.message.contains("ad-456") ||
             entry.message.contains("ad-789") ||
             entry.message.contains("ad-abc") ||
             entry.message.contains("ad-xyz"))
        }
        assertTrue(hasComplexAdIds, "Expected adIds to be extracted from complex version patterns")
        
        client.close()
    }

    @Test
    fun `should handle edge case URLs gracefully`() {
        val writer = RecordingWriter()
        val client = VtexAdsClient(
            publisherId = "test-publisher",
            sessionIdProvider = { "test-session" },
            userIdProvider = { "test-user" },
            channel = Channel.SITE,
            debug = debugOf(VtexAdsDebug.EVENTS_IMPRESSION),
            debugFunction = writer
        )

        // Test edge cases
        val edgeCaseUrls = listOf(
            "https://events.example.com/v134-beta/beacon/view/0498230948324",
            "https://events.example.com/v1.2.3-beta/beacon/impression/ad-123",
            "https://events.example.com/v2024-alpha/beacon/click/ad-456",
            "https://events.example.com/v999-gamma/beacon/conversion/ad-789"
        )

        edgeCaseUrls.forEach { url ->
            client.events.deliveryBeaconEvent(
                eventUrl = url,
                placement = "test.placement"
            )
        }

        Thread.sleep(100)

        val eventsEntries = writer.getEntriesWithLabel("VtexAds/Events")
        assertTrue(eventsEntries.isNotEmpty(), "Expected event log entries")
        
        // Should not crash and should still log
        val successEntries = eventsEntries.filter { it.message.contains("impression success") }
        assertTrue(successEntries.isNotEmpty(), "Expected success log entries")
        
        client.close()
    }
}
