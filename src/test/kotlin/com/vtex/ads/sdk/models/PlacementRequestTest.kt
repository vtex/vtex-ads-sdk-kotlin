package com.vtex.ads.sdk.models

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class PlacementRequestTest {

    @Test
    fun `should create placement request with required fields`() {
        val request = PlacementRequest(
            quantity = 5,
            types = listOf(AdType.PRODUCT)
        )

        assertEquals(5, request.quantity)
        assertEquals(listOf(AdType.PRODUCT), request.types)
        assertNull(request.size)
        assertNull(request.assetsType)
        assertEquals(false, request.allowSkuDuplications)
    }

    @Test
    fun `should create placement request with all fields`() {
        val request = PlacementRequest(
            quantity = 3,
            types = listOf(AdType.BANNER, AdType.SPONSORED_BRAND),
            size = "desktop",
            assetsType = listOf(AssetType.IMAGE, AssetType.VIDEO),
            allowSkuDuplications = true
        )

        assertEquals(3, request.quantity)
        assertEquals(listOf(AdType.BANNER, AdType.SPONSORED_BRAND), request.types)
        assertEquals("desktop", request.size)
        assertEquals(listOf(AssetType.IMAGE, AssetType.VIDEO), request.assetsType)
        assertEquals(true, request.allowSkuDuplications)
    }

    @Test
    fun `should fail with zero quantity`() {
        assertFailsWith<IllegalArgumentException> {
            PlacementRequest(
                quantity = 0,
                types = listOf(AdType.PRODUCT)
            )
        }
    }

    @Test
    fun `should fail with negative quantity`() {
        assertFailsWith<IllegalArgumentException> {
            PlacementRequest(
                quantity = -1,
                types = listOf(AdType.PRODUCT)
            )
        }
    }

    @Test
    fun `should fail with empty types list`() {
        assertFailsWith<IllegalArgumentException> {
            PlacementRequest(
                quantity = 5,
                types = emptyList()
            )
        }
    }

    @Test
    fun `should build placement request using builder`() {
        val request = PlacementRequest.builder()
            .quantity(10)
            .types(AdType.BANNER, AdType.PRODUCT)
            .size("mobile")
            .assetsType(AssetType.IMAGE)
            .allowSkuDuplications(true)
            .build()

        assertEquals(10, request.quantity)
        assertEquals(listOf(AdType.BANNER, AdType.PRODUCT), request.types)
        assertEquals("mobile", request.size)
        assertEquals(listOf(AssetType.IMAGE), request.assetsType)
        assertEquals(true, request.allowSkuDuplications)
    }

    @Test
    fun `should build placement request with video size`() {
        val request = PlacementRequest.builder()
            .quantity(1)
            .types(AdType.BANNER)
            .videoSize(VideoSize.P720)
            .assetsType(AssetType.VIDEO)
            .build()

        assertEquals("720p", request.size)
        assertEquals(listOf(AssetType.VIDEO), request.assetsType)
    }

    @Test
    fun `should build placement request with default values`() {
        val request = PlacementRequest.builder()
            .quantity(5)
            .types(AdType.PRODUCT)
            .build()

        assertEquals(5, request.quantity)
        assertEquals(listOf(AdType.PRODUCT), request.types)
        assertNull(request.size)
        assertNull(request.assetsType)
        assertEquals(false, request.allowSkuDuplications)
    }
}
