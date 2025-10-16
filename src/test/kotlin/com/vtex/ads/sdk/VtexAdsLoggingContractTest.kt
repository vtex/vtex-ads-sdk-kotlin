package com.vtex.ads.sdk

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Recording writer for capturing debug logs in tests.
 */
class RecordingWriter : (String, String) -> Unit {
    data class Entry(val label: String, val message: String)
    val entries = mutableListOf<Entry>()
    
    override fun invoke(label: String, message: String) { 
        entries += Entry(label, message) 
    }
    
    fun clear() {
        entries.clear()
    }
    
    fun hasEntry(label: String, messageContains: String): Boolean {
        return entries.any { it.label == label && it.message.contains(messageContains) }
    }
    
    fun getEntriesWithLabel(label: String): List<Entry> {
        return entries.filter { it.label == label }
    }
}

class VtexAdsLoggingContractTest {

    @Test
    fun `EVENTS_IMPRESSION should log with correct template`() {
        val writer = RecordingWriter()
        val logger = VtexLogger(
            enabled = setOf(VtexAdsDebug.EVENTS_IMPRESSION),
            writer = writer
        )

        logger.log(VtexAdsDebug.EVENTS_IMPRESSION, "VtexAds/Events") {
            "impression success adId=123 placement=home.hero"
        }

        assertEquals(1, writer.entries.size)
        assertEquals("VtexAds/Events", writer.entries[0].label)
        assertTrue(writer.entries[0].message.contains("impression success"))
        assertTrue(writer.entries[0].message.contains("adId=123"))
        assertTrue(writer.entries[0].message.contains("placement=home.hero"))
    }

    @Test
    fun `EVENTS_VIEW should log with correct template`() {
        val writer = RecordingWriter()
        val logger = VtexLogger(
            enabled = setOf(VtexAdsDebug.EVENTS_VIEW),
            writer = writer
        )

        logger.log(VtexAdsDebug.EVENTS_VIEW, "VtexAds/Events") {
            "view success adId=456 placement=search.top"
        }

        assertEquals(1, writer.entries.size)
        assertEquals("VtexAds/Events", writer.entries[0].label)
        assertTrue(writer.entries[0].message.contains("view success"))
        assertTrue(writer.entries[0].message.contains("adId=456"))
        assertTrue(writer.entries[0].message.contains("placement=search.top"))
    }

    @Test
    fun `EVENTS_CLICK should log with correct template`() {
        val writer = RecordingWriter()
        val logger = VtexLogger(
            enabled = setOf(VtexAdsDebug.EVENTS_CLICK),
            writer = writer
        )

        logger.log(VtexAdsDebug.EVENTS_CLICK, "VtexAds/Events") {
            "click success adId=789 placement=category.banner"
        }

        assertEquals(1, writer.entries.size)
        assertEquals("VtexAds/Events", writer.entries[0].label)
        assertTrue(writer.entries[0].message.contains("click success"))
        assertTrue(writer.entries[0].message.contains("adId=789"))
        assertTrue(writer.entries[0].message.contains("placement=category.banner"))
    }

    @Test
    fun `EVENTS_CONVERSION should log with correct template`() {
        val writer = RecordingWriter()
        val logger = VtexLogger(
            enabled = setOf(VtexAdsDebug.EVENTS_CONVERSION),
            writer = writer
        )

        logger.log(VtexAdsDebug.EVENTS_CONVERSION, "VtexAds/Events") {
            "conversion success orderId=order-123 userId=user-456 items=3"
        }

        assertEquals(1, writer.entries.size)
        assertEquals("VtexAds/Events", writer.entries[0].label)
        assertTrue(writer.entries[0].message.contains("conversion success"))
        assertTrue(writer.entries[0].message.contains("orderId=order-123"))
        assertTrue(writer.entries[0].message.contains("userId=user-456"))
        assertTrue(writer.entries[0].message.contains("items=3"))
    }

    @Test
    fun `ADS_LOAD success should log with correct template`() {
        val writer = RecordingWriter()
        val logger = VtexLogger(
            enabled = setOf(VtexAdsDebug.ADS_LOAD),
            writer = writer
        )

        logger.log(VtexAdsDebug.ADS_LOAD, "VtexAds/AdsLoad") {
            "ads_load success requestId=req-99 status=200 latencyMs=128 count=3"
        }

        assertEquals(1, writer.entries.size)
        assertEquals("VtexAds/AdsLoad", writer.entries[0].label)
        assertTrue(writer.entries[0].message.contains("ads_load success"))
        assertTrue(writer.entries[0].message.contains("requestId=req-99"))
        assertTrue(writer.entries[0].message.contains("status=200"))
        assertTrue(writer.entries[0].message.contains("latencyMs=128"))
        assertTrue(writer.entries[0].message.contains("count=3"))
    }

    @Test
    fun `ADS_LOAD error should log with correct template`() {
        val writer = RecordingWriter()
        val logger = VtexLogger(
            enabled = setOf(VtexAdsDebug.ADS_LOAD),
            writer = writer
        )

        logger.log(VtexAdsDebug.ADS_LOAD, "VtexAds/AdsLoad") {
            "ads_load error requestId=req-99 status=500 latencyMs=210 cause=IOException: timeout"
        }

        assertEquals(1, writer.entries.size)
        assertEquals("VtexAds/AdsLoad", writer.entries[0].label)
        assertTrue(writer.entries[0].message.contains("ads_load error"))
        assertTrue(writer.entries[0].message.contains("requestId=req-99"))
        assertTrue(writer.entries[0].message.contains("status=500"))
        assertTrue(writer.entries[0].message.contains("latencyMs=210"))
        assertTrue(writer.entries[0].message.contains("cause=IOException"))
    }

    @Test
    fun `EVENTS_ALL should enable all EVENTS categories`() {
        val writer = RecordingWriter()
        val logger = VtexLogger(
            enabled = setOf(VtexAdsDebug.EVENTS_ALL),
            writer = writer
        )

        logger.log(VtexAdsDebug.EVENTS_IMPRESSION, "VtexAds/Events") { "impression success" }
        logger.log(VtexAdsDebug.EVENTS_VIEW, "VtexAds/Events") { "view success" }
        logger.log(VtexAdsDebug.EVENTS_CLICK, "VtexAds/Events") { "click success" }
        logger.log(VtexAdsDebug.EVENTS_CONVERSION, "VtexAds/Events") { "conversion success" }
        logger.log(VtexAdsDebug.ADS_LOAD, "VtexAds/AdsLoad") { "ads_load success" }

        assertEquals(4, writer.entries.size) // Only EVENTS_* should be logged
        assertTrue(writer.hasEntry("VtexAds/Events", "impression success"))
        assertTrue(writer.hasEntry("VtexAds/Events", "view success"))
        assertTrue(writer.hasEntry("VtexAds/Events", "click success"))
        assertTrue(writer.hasEntry("VtexAds/Events", "conversion success"))
        assertFalse(writer.hasEntry("VtexAds/AdsLoad", "ads_load success"))
    }

    @Test
    fun `EVENTS_ALL should not enable ADS_LOAD`() {
        val writer = RecordingWriter()
        val logger = VtexLogger(
            enabled = setOf(VtexAdsDebug.EVENTS_ALL),
            writer = writer
        )

        logger.log(VtexAdsDebug.ADS_LOAD, "VtexAds/AdsLoad") { "ads_load success" }

        assertEquals(0, writer.entries.size)
    }

    @Test
    fun `ADS_LOAD should be independent of EVENTS_ALL`() {
        val writer = RecordingWriter()
        val logger = VtexLogger(
            enabled = setOf(VtexAdsDebug.ADS_LOAD),
            writer = writer
        )

        logger.log(VtexAdsDebug.EVENTS_IMPRESSION, "VtexAds/Events") { "impression success" }
        logger.log(VtexAdsDebug.ADS_LOAD, "VtexAds/AdsLoad") { "ads_load success" }

        assertEquals(1, writer.entries.size)
        assertFalse(writer.hasEntry("VtexAds/Events", "impression success"))
        assertTrue(writer.hasEntry("VtexAds/AdsLoad", "ads_load success"))
    }

    @Test
    fun `empty debug set should not log anything`() {
        val writer = RecordingWriter()
        val logger = VtexLogger(
            enabled = emptySet(),
            writer = writer
        )

        logger.log(VtexAdsDebug.EVENTS_IMPRESSION, "VtexAds/Events") { "impression success" }
        logger.log(VtexAdsDebug.ADS_LOAD, "VtexAds/AdsLoad") { "ads_load success" }

        assertEquals(0, writer.entries.size)
    }

    @Test
    fun `should handle writer exceptions gracefully`() {
        val logger = VtexLogger(
            enabled = setOf(VtexAdsDebug.EVENTS_ALL),
            writer = { _, _ -> throw RuntimeException("Writer error") }
        )

        // Should not throw exception
        logger.log(VtexAdsDebug.EVENTS_IMPRESSION, "VtexAds/Events") { "impression success" }
    }

    @Test
    fun `should not evaluate message lambda when disabled`() {
        var messageEvaluated = false
        val logger = VtexLogger(
            enabled = emptySet(),
            writer = { _, _ -> }
        )

        logger.log(VtexAdsDebug.EVENTS_IMPRESSION, "VtexAds/Events") { 
            messageEvaluated = true
            "impression success"
        }

        assertFalse(messageEvaluated)
    }
}
