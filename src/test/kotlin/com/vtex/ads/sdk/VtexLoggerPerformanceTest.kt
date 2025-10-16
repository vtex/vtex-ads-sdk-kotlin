package com.vtex.ads.sdk

import com.vtex.ads.sdk.models.Channel
import com.vtex.ads.sdk.models.PlacementRequest
import com.vtex.ads.sdk.models.AdType
import com.vtex.ads.sdk.models.AssetType
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class VtexLoggerPerformanceTest {

    @Test
    fun `should not process expensive operations when debug is disabled`() {
        var expensiveOperationCalled = false
        val writer = RecordingWriter()
        
        val logger = VtexLogger(
            enabled = emptySet(), // Debug disabled
            writer = writer
        )

        logger.log(VtexAdsDebug.ADS_LOAD, "VtexAds/AdsLoad") {
            // This expensive operation should NOT be called when debug is disabled
            expensiveOperationCalled = true
            "ads_load success count=5 types={PRODUCT=3, BANNER=2} responseSize=2048"
        }

        // Verify that expensive operation was not called
        assertTrue(!expensiveOperationCalled, "Expensive operation should not be called when debug is disabled")
        
        // Verify that no logs were written
        assertEquals(0, writer.entries.size, "No logs should be written when debug is disabled")
    }

    @Test
    fun `should process expensive operations when debug is enabled`() {
        var expensiveOperationCalled = false
        val writer = RecordingWriter()
        
        val logger = VtexLogger(
            enabled = setOf(VtexAdsDebug.ADS_LOAD), // Debug enabled
            writer = writer
        )

        logger.log(VtexAdsDebug.ADS_LOAD, "VtexAds/AdsLoad") {
            // This expensive operation SHOULD be called when debug is enabled
            expensiveOperationCalled = true
            "ads_load success count=5 types={PRODUCT=3, BANNER=2} responseSize=2048"
        }

        // Verify that expensive operation was called
        assertTrue(expensiveOperationCalled, "Expensive operation should be called when debug is enabled")
        
        // Verify that logs were written
        assertEquals(1, writer.entries.size, "Logs should be written when debug is enabled")
    }

    @Test
    fun `should not process URL parsing when events debug is disabled`() {
        var urlParsingCalled = false
        val writer = RecordingWriter()
        
        val logger = VtexLogger(
            enabled = emptySet(), // Debug disabled
            writer = writer
        )

        logger.log(VtexAdsDebug.EVENTS_IMPRESSION, "VtexAds/Events") {
            // This URL parsing should NOT be called when debug is disabled
            urlParsingCalled = true
            "impression success placement=home.hero requestId=req-123"
        }

        // Verify that URL parsing was not called
        assertTrue(!urlParsingCalled, "URL parsing should not be called when debug is disabled")
        
        // Verify that no logs were written
        assertEquals(0, writer.entries.size, "No logs should be written when debug is disabled")
    }

    @Test
    fun `should process URL parsing when events debug is enabled`() {
        var urlParsingCalled = false
        val writer = RecordingWriter()
        
        val logger = VtexLogger(
            enabled = setOf(VtexAdsDebug.EVENTS_IMPRESSION), // Debug enabled
            writer = writer
        )

        logger.log(VtexAdsDebug.EVENTS_IMPRESSION, "VtexAds/Events") {
            // This URL parsing SHOULD be called when debug is enabled
            urlParsingCalled = true
            "impression success placement=home.hero requestId=req-123"
        }

        // Verify that URL parsing was called
        assertTrue(urlParsingCalled, "URL parsing should be called when debug is enabled")
        
        // Verify that logs were written
        assertEquals(1, writer.entries.size, "Logs should be written when debug is enabled")
    }

    @Test
    fun `should not process any operations when debug is disabled in real client`() {
        var expensiveOperationCalled = false
        val writer = RecordingWriter()
        
        val client = VtexAdsClient(
            publisherId = "test-publisher",
            sessionIdProvider = { "test-session" },
            userIdProvider = { "test-user" },
            channel = Channel.SITE,
            debug = emptySet(), // Debug disabled
            debugFunction = writer
        )

        val placements = mapOf(
            "home.hero" to PlacementRequest.builder()
                .quantity(1)
                .types(AdType.BANNER)
                .assetsType(AssetType.IMAGE)
                .build()
        )

        // This will fail the actual HTTP call, but we're testing that no debug processing happens
        runBlocking {
            try {
                client.ads.getHomeAds(placements)
            } catch (e: Exception) {
                // Expected to fail
            }
        }

        Thread.sleep(100)

        // Verify that no debug logs were written
        assertEquals(0, writer.entries.size, "No debug logs should be written when debug is disabled")
        
        client.close()
    }

    @Test
    fun `should process operations when debug is enabled in real client`() {
        val writer = RecordingWriter()
        
        val client = VtexAdsClient(
            publisherId = "test-publisher",
            sessionIdProvider = { "test-session" },
            userIdProvider = { "test-user" },
            channel = Channel.SITE,
            debug = debugOf(VtexAdsDebug.ADS_LOAD), // Debug enabled
            debugFunction = writer
        )

        val placements = mapOf(
            "home.hero" to PlacementRequest.builder()
                .quantity(1)
                .types(AdType.BANNER)
                .assetsType(AssetType.IMAGE)
                .build()
        )

        // This will fail the actual HTTP call, but we're testing that debug processing happens
        runBlocking {
            try {
                client.ads.getHomeAds(placements)
            } catch (e: Exception) {
                // Expected to fail
            }
        }

        Thread.sleep(100)

        // Verify that debug logs were written
        assertTrue(writer.entries.isNotEmpty(), "Debug logs should be written when debug is enabled")
        
        client.close()
    }
}
