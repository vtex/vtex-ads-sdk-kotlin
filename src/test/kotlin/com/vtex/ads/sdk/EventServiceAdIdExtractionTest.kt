package com.vtex.ads.sdk

import com.vtex.ads.sdk.models.Channel
import kotlin.test.Test
import kotlin.test.assertTrue

class EventServiceAdIdExtractionTest {

    @Test
    fun `should extract adId from various URL patterns`() {
        val writer = RecordingWriter()
        val client = VtexAdsClient(
            publisherId = "test-publisher",
            sessionIdProvider = { "test-session" },
            userIdProvider = { "test-user" },
            channel = Channel.SITE,
            debug = debugOf(VtexAdsDebug.EVENTS_ALL),
            debugFunction = writer
        )

        // Test different URL patterns
        val testUrls = listOf(
            "https://events.example.com/v1/beacon/view/ad-123?param=value",
            "https://events.example.com/v2/beacon/impression/ad-456?other=param",
            "https://events.example.com/v3/beacon/click/ad-789",
            "https://events.example.com/v1/beacon/view/acf4e62c-c46f-4b84-8ea2-187f36704c01?publisher_id=test",
            "https://events.example.com/v2/beacon/impression/banner-ad-001?campaign_id=123",
            "https://events.example.com/v1/beacon/click/product-ad-999?context=home"
        )

        testUrls.forEachIndexed { index, url ->
            client.events.deliveryBeaconEvent(
                eventUrl = url,
                placement = "test.placement.$index"
            )
        }

        Thread.sleep(200) // Give more time for all async operations

        val eventsEntries = writer.getEntriesWithLabel("VtexAds/Events")
        assertTrue(eventsEntries.isNotEmpty(), "Expected event log entries")
        
        // Check that adIds are extracted and logged
        val successEntries = eventsEntries.filter { it.message.contains("success") }
        assertTrue(successEntries.isNotEmpty(), "Expected success log entries")
        
        // Verify that adIds are present in logs
        val hasAdIds = successEntries.any { entry ->
            entry.message.contains("adId=") && 
            (entry.message.contains("ad-123") || 
             entry.message.contains("ad-456") || 
             entry.message.contains("ad-789") ||
             entry.message.contains("acf4e62c-c46f-4b84-8ea2-187f36704c01") ||
             entry.message.contains("banner-ad-001") ||
             entry.message.contains("product-ad-999"))
        }
        assertTrue(hasAdIds, "Expected adIds to be extracted and logged")
        
        client.close()
    }

    @Test
    fun `should handle URLs without adId gracefully`() {
        val writer = RecordingWriter()
        val client = VtexAdsClient(
            publisherId = "test-publisher",
            sessionIdProvider = { "test-session" },
            userIdProvider = { "test-user" },
            channel = Channel.SITE,
            debug = debugOf(VtexAdsDebug.EVENTS_IMPRESSION),
            debugFunction = writer
        )

        // Test URLs that don't match the pattern
        val invalidUrls = listOf(
            "https://events.example.com/v1/beacon/view?param=value",
            "https://events.example.com/v1/beacon/",
            "https://events.example.com/v1/beacon/view/",
            "https://events.example.com/other/path",
            "https://events.example.com/v1/beacon/view/ad-123/extra/path"
        )

        invalidUrls.forEach { url ->
            client.events.deliveryBeaconEvent(
                eventUrl = url,
                placement = "test.placement"
            )
        }

        Thread.sleep(100)

        val eventsEntries = writer.getEntriesWithLabel("VtexAds/Events")
        assertTrue(eventsEntries.isNotEmpty(), "Expected event log entries")
        
        // Should not crash and should still log without adId
        val successEntries = eventsEntries.filter { it.message.contains("impression success") }
        assertTrue(successEntries.isNotEmpty(), "Expected success log entries")
        
        // Should not contain adId= for invalid URLs
        val hasInvalidAdIds = successEntries.any { entry ->
            entry.message.contains("adId=") && entry.message.contains("ad-")
        }
        assertTrue(!hasInvalidAdIds, "Should not extract adId from invalid URLs")
        
        client.close()
    }

    @Test
    fun `should extract adId from real world URL example`() {
        val writer = RecordingWriter()
        val client = VtexAdsClient(
            publisherId = "test-publisher",
            sessionIdProvider = { "test-session" },
            userIdProvider = { "test-user" },
            channel = Channel.SITE,
            debug = debugOf(VtexAdsDebug.EVENTS_VIEW),
            debugFunction = writer
        )

        // Real URL from user's example
        val realUrl = "https://events.newtail-media.newtail.com.br/v1/beacon/view/acf4e62c-c46f-4b84-8ea2-187f36704c01?publisher_id=d4dff0cb-1f21-4a96-9acf-d9426a5ed08c&ad_type=banner&campaign_id=e48f7340-f123-46d8-8fa1-4e09454239e5&pname=home_top_banner&ad_size=mobile&context=home&channel=app&event_id=5b0e50a8-682a-4cb1-8c83-6bffe5da3179&request_id=2d8a63ad-a885-4d1d-87e1-794120a8c521&session_id=session-id&user_id=user-id&requested_at=1760616781024&sign=cfb0b76a66a6aa2134574debab5220f738dcdb9b0aedf1c1144629f8395f33d"

        client.events.deliveryBeaconEvent(
            eventUrl = realUrl,
            placement = "home.hero"
        )

        Thread.sleep(100)

        val eventsEntries = writer.getEntriesWithLabel("VtexAds/Events")
        assertTrue(eventsEntries.isNotEmpty(), "Expected event log entries")
        
        val successEntry = eventsEntries.find { it.message.contains("view success") }
        assertTrue(successEntry != null, "Expected view success log")
        assertTrue(successEntry!!.message.contains("adId=acf4e62c-c46f-4b84-8ea2-187f36704c01"), "Expected correct adId extraction from real URL")
        
        client.close()
    }

    @Test
    fun `should work with different event types and versions`() {
        val writer = RecordingWriter()
        val client = VtexAdsClient(
            publisherId = "test-publisher",
            sessionIdProvider = { "test-session" },
            userIdProvider = { "test-user" },
            channel = Channel.SITE,
            debug = debugOf(VtexAdsDebug.EVENTS_ALL),
            debugFunction = writer
        )

        // Test different event types and versions
        val testCases = listOf(
            "https://events.example.com/v1/beacon/impression/ad-imp-123" to "impression",
            "https://events.example.com/v2/beacon/view/ad-view-456" to "view", 
            "https://events.example.com/v3/beacon/click/ad-click-789" to "click",
            "https://events.example.com/v1/beacon/conversion/ad-conv-999" to "impression" // fallback
        )

        testCases.forEach { (url, expectedEventType) ->
            client.events.deliveryBeaconEvent(
                eventUrl = url,
                placement = "test.placement"
            )
        }

        Thread.sleep(200)

        val eventsEntries = writer.getEntriesWithLabel("VtexAds/Events")
        assertTrue(eventsEntries.isNotEmpty(), "Expected event log entries")
        
        // Check that all event types have adIds
        val successEntries = eventsEntries.filter { it.message.contains("success") }
        assertTrue(successEntries.size >= testCases.size, "Expected logs for all test cases")
        
        // Verify adIds are present for all event types
        val hasAllAdIds = successEntries.all { entry ->
            entry.message.contains("adId=") && 
            (entry.message.contains("ad-imp-123") || 
             entry.message.contains("ad-view-456") || 
             entry.message.contains("ad-click-789") ||
             entry.message.contains("ad-conv-999"))
        }
        assertTrue(hasAllAdIds, "Expected adIds to be extracted for all event types")
        
        client.close()
    }
}
