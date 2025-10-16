package com.vtex.ads.sdk

import com.vtex.ads.sdk.models.Channel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class VtexAdsDebugTest {

    @Test
    fun `debugOf should create set with specified items`() {
        val debugSet = debugOf(VtexAdsDebug.EVENTS_ALL, VtexAdsDebug.ADS_LOAD)
        
        assertEquals(2, debugSet.size)
        assertTrue(debugSet.contains(VtexAdsDebug.EVENTS_ALL))
        assertTrue(debugSet.contains(VtexAdsDebug.ADS_LOAD))
    }

    @Test
    fun `debugOf should create empty set when no items provided`() {
        val debugSet = debugOf()
        
        assertTrue(debugSet.isEmpty())
    }

    @Test
    fun `debugOf should handle single item`() {
        val debugSet = debugOf(VtexAdsDebug.EVENTS_IMPRESSION)
        
        assertEquals(1, debugSet.size)
        assertTrue(debugSet.contains(VtexAdsDebug.EVENTS_IMPRESSION))
    }
}
