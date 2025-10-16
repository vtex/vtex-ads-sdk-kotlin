package com.vtex.ads.sdk

import com.vtex.ads.sdk.models.Channel
import com.vtex.ads.sdk.models.Context
import com.vtex.ads.sdk.models.PlacementRequest
import com.vtex.ads.sdk.models.AdType
import com.vtex.ads.sdk.models.AssetType
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertTrue

class AdsServiceEnhancedLoggingTest {

    @Test
    fun `should log enhanced ads load success with all request details`() {
        val writer = RecordingWriter()
        val client = VtexAdsClient(
            publisherId = "test-publisher",
            sessionIdProvider = { "test-session-123456" },
            userIdProvider = { "test-user-789" },
            channel = Channel.SITE,
            debug = debugOf(VtexAdsDebug.ADS_LOAD),
            debugFunction = writer
        )

        val placements = mapOf(
            "home.hero" to PlacementRequest.builder()
                .quantity(1)
                .types(AdType.BANNER)
                .assetsType(AssetType.IMAGE)
                .build(),
            "home.products" to PlacementRequest.builder()
                .quantity(5)
                .types(AdType.PRODUCT)
                .build()
        )

        // This will fail the actual HTTP call, but we're testing the logging
        runBlocking {
            try {
                client.ads.getHomeAds(placements)
            } catch (e: Exception) {
                // Expected to fail
            }
        }

        Thread.sleep(100)

        val adsLoadEntries = writer.getEntriesWithLabel("VtexAds/AdsLoad")
        assertTrue(adsLoadEntries.isNotEmpty(), "Expected at least one ads load log entry")
        
        // Check if any entry contains the enhanced logging information
        val hasEnhancedLogging = adsLoadEntries.any { entry ->
            entry.message.contains("context=") && 
            entry.message.contains("channel=") && 
            entry.message.contains("placements=") &&
            entry.message.contains("userId=") &&
            entry.message.contains("sessionId=")
        }
        assertTrue(hasEnhancedLogging, "Expected enhanced logging with context, channel, placements, userId, and sessionId")
        
        client.close()
    }

    @Test
    fun `should log enhanced ads load success with search context`() {
        val writer = RecordingWriter()
        val client = VtexAdsClient(
            publisherId = "test-publisher",
            sessionIdProvider = { "search-session-456" },
            userIdProvider = { "search-user-123" },
            channel = Channel.APP,
            debug = debugOf(VtexAdsDebug.ADS_LOAD),
            debugFunction = writer
        )

        val placements = mapOf(
            "search.banner" to PlacementRequest.builder()
                .quantity(1)
                .types(AdType.BANNER)
                .build()
        )

        runBlocking {
            try {
                client.ads.getSearchAds("smartphone", placements)
            } catch (e: Exception) {
                // Expected to fail
            }
        }

        Thread.sleep(100)

        val adsLoadEntries = writer.getEntriesWithLabel("VtexAds/AdsLoad")
        assertTrue(adsLoadEntries.isNotEmpty(), "Expected at least one ads load log entry")
        
        // Check if any entry contains the enhanced logging information
        val hasEnhancedLogging = adsLoadEntries.any { entry ->
            entry.message.contains("context=") && 
            entry.message.contains("channel=") && 
            entry.message.contains("placements=") &&
            entry.message.contains("userId=") &&
            entry.message.contains("sessionId=")
        }
        assertTrue(hasEnhancedLogging, "Expected enhanced logging with context, channel, placements, userId, and sessionId")
        
        client.close()
    }

    @Test
    fun `should log enhanced ads load success with category context`() {
        val writer = RecordingWriter()
        val client = VtexAdsClient(
            publisherId = "test-publisher",
            sessionIdProvider = { "category-session-789" },
            userIdProvider = { "category-user-456" },
            channel = Channel.MSITE,
            debug = debugOf(VtexAdsDebug.ADS_LOAD),
            debugFunction = writer
        )

        val placements = mapOf(
            "category.banner" to PlacementRequest.builder()
                .quantity(1)
                .types(AdType.BANNER)
                .build(),
            "category.products" to PlacementRequest.builder()
                .quantity(8)
                .types(AdType.PRODUCT)
                .build(),
            "category.sponsored" to PlacementRequest.builder()
                .quantity(3)
                .types(AdType.SPONSORED_BRAND)
                .build()
        )

        runBlocking {
            try {
                client.ads.getCategoryAds("Electronics > Smartphones", placements)
            } catch (e: Exception) {
                // Expected to fail
            }
        }

        Thread.sleep(100)

        val adsLoadEntries = writer.getEntriesWithLabel("VtexAds/AdsLoad")
        assertTrue(adsLoadEntries.isNotEmpty(), "Expected at least one ads load log entry")
        
        // Check if any entry contains the enhanced logging information
        val hasEnhancedLogging = adsLoadEntries.any { entry ->
            entry.message.contains("context=") && 
            entry.message.contains("channel=") && 
            entry.message.contains("placements=") &&
            entry.message.contains("userId=") &&
            entry.message.contains("sessionId=")
        }
        assertTrue(hasEnhancedLogging, "Expected enhanced logging with context, channel, placements, userId, and sessionId")
        
        client.close()
    }
}
