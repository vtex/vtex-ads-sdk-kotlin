package com.vtex.ads.sdk

import com.vtex.ads.sdk.models.Channel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EventServiceUrlParamsTest {

    @Test
    fun `should extract all parameters from complete event URL`() {
        val writer = RecordingWriter()
        val client = VtexAdsClient(
            publisherId = "test-publisher",
            sessionIdProvider = { "test-session" },
            userIdProvider = { "test-user" },
            channel = Channel.SITE,
            debug = debugOf(VtexAdsDebug.EVENTS_VIEW),
            debugFunction = writer
        )

        val eventUrl = "https://events.newtail-media.newtail.com.br/v1/beacon/view/acf4e62c-c46f-4b84-8ea2-187f36704c01?publisher_id=d4dff0cb-1f21-4a96-9acf-d9426a5ed08c&ad_type=banner&campaign_id=e48f7340-f123-46d8-8fa1-4e09454239e5&pname=home_top_banner&ad_size=mobile&context=home&channel=app&event_id=5b0e50a8-682a-4cb1-8c83-6bffe5da3179&request_id=2d8a63ad-a885-4d1d-87e1-794120a8c521&session_id=session-id&user_id=user-id&requested_at=1760616781024&sign=cfb0b76a66a6aa21347574debab5220f738dcdb9b0aedf1c1144629f8395f33d"

        client.events.deliveryBeaconEvent(
            eventUrl = eventUrl,
            placement = "home.hero"
        )

        Thread.sleep(100)

        val eventsEntries = writer.getEntriesWithLabel("VtexAds/Events")
        assertTrue(eventsEntries.isNotEmpty())
        
        val successEntry = eventsEntries.find { it.message.contains("view success") }
        assertTrue(successEntry != null)
        
        val message = successEntry!!.message
        assertTrue(message.contains("placement=home.hero"))
        assertTrue(message.contains("requestId=2d8a63ad-a885-4d1d-87e1-794120a8c521"))
        assertTrue(message.contains("campaignId=e48f7340-f123-46d8-8fa1-4e09454239e5"))
        assertTrue(message.contains("adType=banner"))
        assertTrue(message.contains("pname=home_top_banner"))
        assertTrue(message.contains("context=home"))
        assertTrue(message.contains("channel=app"))
        assertTrue(message.contains("adSize=mobile"))
        assertTrue(message.contains("requestedAt=1760616781024"))
        
        client.close()
    }

    @Test
    fun `should extract partial parameters from incomplete event URL`() {
        val writer = RecordingWriter()
        val client = VtexAdsClient(
            publisherId = "test-publisher",
            sessionIdProvider = { "test-session" },
            userIdProvider = { "test-user" },
            channel = Channel.SITE,
            debug = debugOf(VtexAdsDebug.EVENTS_IMPRESSION),
            debugFunction = writer
        )

        val eventUrl = "https://events.example.com/v1/beacon/impression/test-id?ad_type=product&context=search&channel=site"

        client.events.deliveryBeaconEvent(
            eventUrl = eventUrl,
            placement = "search.results"
        )

        Thread.sleep(100)

        val eventsEntries = writer.getEntriesWithLabel("VtexAds/Events")
        assertTrue(eventsEntries.isNotEmpty())
        
        val successEntry = eventsEntries.find { it.message.contains("impression success") }
        assertTrue(successEntry != null)
        
        val message = successEntry!!.message
        assertTrue(message.contains("placement=search.results"))
        assertTrue(message.contains("adType=product"))
        assertTrue(message.contains("context=search"))
        assertTrue(message.contains("channel=site"))
        
        // Should not contain parameters that weren't in the URL
        assertTrue(!message.contains("requestId="))
        assertTrue(!message.contains("campaignId="))
        
        client.close()
    }

    @Test
    fun `should handle malformed URLs gracefully`() {
        val writer = RecordingWriter()
        val client = VtexAdsClient(
            publisherId = "test-publisher",
            sessionIdProvider = { "test-session" },
            userIdProvider = { "test-user" },
            channel = Channel.SITE,
            debug = debugOf(VtexAdsDebug.EVENTS_CLICK),
            debugFunction = writer
        )

        val malformedUrl = "not-a-valid-url"

        // This should not throw an exception
        client.events.deliveryBeaconEvent(
            eventUrl = malformedUrl,
            placement = "test.placement"
        )

        Thread.sleep(100)

        // The main test is that the function doesn't crash
        // We don't need to verify specific log content for malformed URLs
        client.close()
    }

    @Test
    fun `should handle URLs without query parameters`() {
        val writer = RecordingWriter()
        val client = VtexAdsClient(
            publisherId = "test-publisher",
            sessionIdProvider = { "test-session" },
            userIdProvider = { "test-user" },
            channel = Channel.SITE,
            debug = debugOf(VtexAdsDebug.EVENTS_VIEW),
            debugFunction = writer
        )

        val urlWithoutParams = "https://events.example.com/v1/beacon/view/simple-id"

        client.events.deliveryBeaconEvent(
            eventUrl = urlWithoutParams,
            placement = "simple.placement"
        )

        Thread.sleep(100)

        val eventsEntries = writer.getEntriesWithLabel("VtexAds/Events")
        assertTrue(eventsEntries.isNotEmpty())
        
        val successEntry = eventsEntries.find { it.message.contains("view success") }
        assertTrue(successEntry != null)
        
        val message = successEntry!!.message
        assertTrue(message.contains("placement=simple.placement"))
        // Should not contain any URL parameters
        assertTrue(!message.contains("requestId="))
        assertTrue(!message.contains("adType="))
        
        client.close()
    }
}
