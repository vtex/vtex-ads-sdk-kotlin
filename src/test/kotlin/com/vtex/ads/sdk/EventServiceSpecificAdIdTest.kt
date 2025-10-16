package com.vtex.ads.sdk

import com.vtex.ads.sdk.models.Channel
import kotlin.test.Test
import kotlin.test.assertTrue

class EventServiceSpecificAdIdTest {

    @Test
    fun `should extract specific adId from v134-beta URL`() {
        val writer = RecordingWriter()
        val client = VtexAdsClient(
            publisherId = "test-publisher",
            sessionIdProvider = { "test-session" },
            userIdProvider = { "test-user" },
            channel = Channel.SITE,
            debug = debugOf(VtexAdsDebug.EVENTS_VIEW),
            debugFunction = writer
        )

        // Test the specific URL pattern mentioned by user
        val specificUrl = "https://events.example.com/v134-beta/beacon/view/0498230948324?param=value"

        client.events.deliveryBeaconEvent(
            eventUrl = specificUrl,
            placement = "test.placement"
        )

        Thread.sleep(100)

        val eventsEntries = writer.getEntriesWithLabel("VtexAds/Events")
        assertTrue(eventsEntries.isNotEmpty(), "Expected event log entries")
        
        val successEntry = eventsEntries.find { it.message.contains("view success") }
        assertTrue(successEntry != null, "Expected view success log")
        
        // Check if the specific adId is extracted
        val hasSpecificAdId = successEntry!!.message.contains("adId=0498230948324")
        assertTrue(hasSpecificAdId, "Expected adId=0498230948324 to be extracted from v134-beta URL")
        
        println("Log message: ${successEntry.message}")
        
        client.close()
    }

    @Test
    fun `should extract adId from various complex version patterns`() {
        val writer = RecordingWriter()
        val client = VtexAdsClient(
            publisherId = "test-publisher",
            sessionIdProvider = { "test-session" },
            userIdProvider = { "test-user" },
            channel = Channel.SITE,
            debug = debugOf(VtexAdsDebug.EVENTS_ALL),
            debugFunction = writer
        )

        // Test various complex version patterns
        val testCases = listOf(
            "https://events.example.com/v134-beta/beacon/view/0498230948324" to "0498230948324",
            "https://events.example.com/v2.1/beacon/impression/ad-123" to "ad-123",
            "https://events.example.com/v10-alpha/beacon/click/ad-456" to "ad-456",
            "https://events.example.com/v999-gamma/beacon/conversion/ad-789" to "ad-789",
            "https://events.example.com/v1.2.3/beacon/view/ad-abc" to "ad-abc",
            "https://events.example.com/v2024/beacon/impression/ad-xyz" to "ad-xyz"
        )

        testCases.forEach { (url, expectedAdId) ->
            client.events.deliveryBeaconEvent(
                eventUrl = url,
                placement = "test.placement"
            )
        }

        Thread.sleep(200)

        val eventsEntries = writer.getEntriesWithLabel("VtexAds/Events")
        assertTrue(eventsEntries.isNotEmpty(), "Expected event log entries")
        
        val successEntries = eventsEntries.filter { it.message.contains("success") }
        assertTrue(successEntries.isNotEmpty(), "Expected success log entries")
        
        // Check each expected adId
        testCases.forEach { (_, expectedAdId) ->
            val hasAdId = successEntries.any { entry ->
                entry.message.contains("adId=$expectedAdId")
            }
            assertTrue(hasAdId, "Expected adId=$expectedAdId to be extracted")
        }
        
        client.close()
    }
}
