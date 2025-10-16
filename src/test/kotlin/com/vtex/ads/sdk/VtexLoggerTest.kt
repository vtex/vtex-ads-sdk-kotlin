package com.vtex.ads.sdk

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class VtexLoggerTest {

    @Test
    fun `should log when EVENTS_ALL is enabled`() {
        val entries = mutableListOf<Pair<String, String>>()
        val logger = VtexLogger(
            enabled = setOf(VtexAdsDebug.EVENTS_ALL),
            writer = { label, message -> entries += label to message }
        )

        logger.log(VtexAdsDebug.EVENTS_IMPRESSION, "TestLabel") { "test message" }

        assertEquals(1, entries.size)
        assertEquals("TestLabel" to "test message", entries[0])
    }

    @Test
    fun `should log when specific EVENTS category is enabled`() {
        val entries = mutableListOf<Pair<String, String>>()
        val logger = VtexLogger(
            enabled = setOf(VtexAdsDebug.EVENTS_IMPRESSION),
            writer = { label, message -> entries += label to message }
        )

        logger.log(VtexAdsDebug.EVENTS_IMPRESSION, "TestLabel") { "test message" }

        assertEquals(1, entries.size)
        assertEquals("TestLabel" to "test message", entries[0])
    }

    @Test
    fun `should not log when specific EVENTS category is not enabled`() {
        val entries = mutableListOf<Pair<String, String>>()
        val logger = VtexLogger(
            enabled = setOf(VtexAdsDebug.EVENTS_CLICK),
            writer = { label, message -> entries += label to message }
        )

        logger.log(VtexAdsDebug.EVENTS_IMPRESSION, "TestLabel") { "test message" }

        assertTrue(entries.isEmpty())
    }

    @Test
    fun `should log when ADS_LOAD is enabled`() {
        val entries = mutableListOf<Pair<String, String>>()
        val logger = VtexLogger(
            enabled = setOf(VtexAdsDebug.ADS_LOAD),
            writer = { label, message -> entries += label to message }
        )

        logger.log(VtexAdsDebug.ADS_LOAD, "TestLabel") { "test message" }

        assertEquals(1, entries.size)
        assertEquals("TestLabel" to "test message", entries[0])
    }

    @Test
    fun `should not log when ADS_LOAD is not enabled`() {
        val entries = mutableListOf<Pair<String, String>>()
        val logger = VtexLogger(
            enabled = setOf(VtexAdsDebug.EVENTS_ALL),
            writer = { label, message -> entries += label to message }
        )

        logger.log(VtexAdsDebug.ADS_LOAD, "TestLabel") { "test message" }

        assertTrue(entries.isEmpty())
    }

    @Test
    fun `should not evaluate message lambda when disabled`() {
        var messageEvaluated = false
        val logger = VtexLogger(
            enabled = emptySet(),
            writer = { _, _ -> }
        )

        logger.log(VtexAdsDebug.EVENTS_IMPRESSION, "TestLabel") { 
            messageEvaluated = true
            "test message"
        }

        assertFalse(messageEvaluated)
    }

    @Test
    fun `should handle writer exceptions gracefully`() {
        val logger = VtexLogger(
            enabled = setOf(VtexAdsDebug.EVENTS_ALL),
            writer = { _, _ -> throw RuntimeException("Writer error") }
        )

        // Should not throw exception
        logger.log(VtexAdsDebug.EVENTS_IMPRESSION, "TestLabel") { "test message" }
    }

    @Test
    fun `should handle multiple enabled categories`() {
        val entries = mutableListOf<Pair<String, String>>()
        val logger = VtexLogger(
            enabled = setOf(VtexAdsDebug.EVENTS_ALL, VtexAdsDebug.ADS_LOAD),
            writer = { label, message -> entries += label to message }
        )

        logger.log(VtexAdsDebug.EVENTS_IMPRESSION, "EventsLabel") { "events message" }
        logger.log(VtexAdsDebug.ADS_LOAD, "AdsLabel") { "ads message" }

        assertEquals(2, entries.size)
        assertEquals("EventsLabel" to "events message", entries[0])
        assertEquals("AdsLabel" to "ads message", entries[1])
    }
}
