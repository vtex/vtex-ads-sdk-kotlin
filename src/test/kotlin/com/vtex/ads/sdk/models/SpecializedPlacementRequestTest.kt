package com.vtex.ads.sdk.models

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SpecializedPlacementRequestTest {

    // ===== BannerPlacementRequest Tests =====

    @Test
    fun `BannerPlacementRequest builder creates image banner by default`() {
        val placement = BannerPlacementRequest.builder()
            .quantity(5)
            .build()

        assertEquals(5, placement.quantity)
        assertEquals(listOf(AdType.BANNER), placement.types)
        assertEquals(listOf(AssetType.IMAGE), placement.assetsType)
    }

    @Test
    fun `BannerPlacementRequest builder creates image banner with size`() {
        val placement = BannerPlacementRequest.builder()
            .quantity(3)
            .image("desktop")
            .build()

        assertEquals(3, placement.quantity)
        assertEquals(listOf(AssetType.IMAGE), placement.assetsType)
        assertEquals("desktop", placement.size)
    }

    @Test
    fun `BannerPlacementRequest builder creates video banner with size`() {
        val placement = BannerPlacementRequest.builder()
            .quantity(2)
            .video("720p", VideoSize.P720)
            .build()

        assertEquals(2, placement.quantity)
        assertEquals(listOf(AssetType.VIDEO), placement.assetsType)
        assertEquals(VideoSize.P720.value, placement.size)  // videoSize is converted to size
    }

    @Test
    fun `BannerPlacementRequest builder creates mixed image and video banner`() {
        val placement = BannerPlacementRequest.builder()
            .quantity(10)
            .imageAndVideo("desktop", VideoSize.P1080)
            .build()

        assertEquals(10, placement.quantity)
        assertEquals(listOf(AssetType.IMAGE, AssetType.VIDEO), placement.assetsType)
        assertEquals(VideoSize.P1080.value, placement.size)  // videoSize is converted to size
    }

    @Test
    fun `BannerPlacementRequest video requires size`() {
        assertThrows<IllegalArgumentException> {
            BannerPlacementRequest.builder()
                .quantity(1)
                .video("", null)  // Empty size should trigger validation
                .build()
        }
    }

    @Test
    fun `BannerPlacementRequest factory method creates image banner`() {
        val placement = BannerPlacementRequest.image(5, "mobile")

        assertEquals(5, placement.quantity)
        assertEquals(listOf(AssetType.IMAGE), placement.assetsType)
        assertEquals("mobile", placement.size)
    }

    @Test
    fun `BannerPlacementRequest factory method creates video banner`() {
        val placement = BannerPlacementRequest.video(3, "1080p", VideoSize.P1080)

        assertEquals(3, placement.quantity)
        assertEquals(listOf(AssetType.VIDEO), placement.assetsType)
        assertEquals(VideoSize.P1080.value, placement.size)  // videoSize is converted to size
    }

    @Test
    fun `BannerPlacementRequest validates positive quantity`() {
        assertThrows<IllegalArgumentException> {
            BannerPlacementRequest.builder()
                .quantity(0)
                .build()
        }

        assertThrows<IllegalArgumentException> {
            BannerPlacementRequest.builder()
                .quantity(-1)
                .build()
        }
    }

    // ===== ProductPlacementRequest Tests =====

    @Test
    fun `ProductPlacementRequest builder creates product placement`() {
        val placement = ProductPlacementRequest.builder()
            .quantity(10)
            .build()

        assertEquals(10, placement.quantity)
        assertEquals(listOf(AdType.PRODUCT), placement.types)
        assertTrue(placement.allowSkuDuplications)
    }

    @Test
    fun `ProductPlacementRequest builder allows disabling SKU duplications`() {
        val placement = ProductPlacementRequest.builder()
            .quantity(5)
            .allowSkuDuplications(false)
            .build()

        assertEquals(5, placement.quantity)
        assertFalse(placement.allowSkuDuplications)
    }

    @Test
    fun `ProductPlacementRequest builder has uniqueSkus convenience method`() {
        val placement = ProductPlacementRequest.builder()
            .quantity(8)
            .uniqueSkus()
            .build()

        assertEquals(8, placement.quantity)
        assertFalse(placement.allowSkuDuplications)
    }

    @Test
    fun `ProductPlacementRequest factory method creates product placement`() {
        val placement = ProductPlacementRequest.create(10, allowSkuDuplications = true)

        assertEquals(10, placement.quantity)
        assertTrue(placement.allowSkuDuplications)
    }

    @Test
    fun `ProductPlacementRequest unique factory method disables duplications`() {
        val placement = ProductPlacementRequest.unique(7)

        assertEquals(7, placement.quantity)
        assertFalse(placement.allowSkuDuplications)
    }

    @Test
    fun `ProductPlacementRequest validates positive quantity`() {
        assertThrows<IllegalArgumentException> {
            ProductPlacementRequest.builder()
                .quantity(0)
                .build()
        }

        assertThrows<IllegalArgumentException> {
            ProductPlacementRequest.builder()
                .quantity(-5)
                .build()
        }
    }

    // ===== SponsoredBrandPlacementRequest Tests =====

    @Test
    fun `SponsoredBrandPlacementRequest builder creates image placement by default`() {
        val placement = SponsoredBrandPlacementRequest.builder()
            .quantity(3)
            .build()

        assertEquals(3, placement.quantity)
        assertEquals(listOf(AdType.SPONSORED_BRAND), placement.types)
        assertEquals(listOf(AssetType.IMAGE), placement.assetsType)
    }

    @Test
    fun `SponsoredBrandPlacementRequest builder creates image placement`() {
        val placement = SponsoredBrandPlacementRequest.builder()
            .quantity(5)
            .withImageAssets()
            .build()

        assertEquals(5, placement.quantity)
        assertEquals(listOf(AssetType.IMAGE), placement.assetsType)
    }

    @Test
    fun `SponsoredBrandPlacementRequest builder creates video placement`() {
        val placement = SponsoredBrandPlacementRequest.builder()
            .quantity(2)
            .withVideoAssets(VideoSize.P720)
            .build()

        assertEquals(2, placement.quantity)
        assertEquals(listOf(AssetType.VIDEO), placement.assetsType)
        assertEquals(VideoSize.P720.value, placement.size)  // videoSize is converted to size
    }

    @Test
    fun `SponsoredBrandPlacementRequest builder creates mixed placement`() {
        val placement = SponsoredBrandPlacementRequest.builder()
            .quantity(10)
            .withImageAndVideoAssets(VideoSize.P1080)
            .build()

        assertEquals(10, placement.quantity)
        assertEquals(listOf(AssetType.IMAGE, AssetType.VIDEO), placement.assetsType)
        assertEquals(VideoSize.P1080.value, placement.size)  // videoSize is converted to size
    }

    @Test
    fun `SponsoredBrandPlacementRequest builder allows custom asset types`() {
        val placement = SponsoredBrandPlacementRequest.builder()
            .quantity(4)
            .assetTypes(AssetType.VIDEO, AssetType.IMAGE)
            .build()

        assertEquals(4, placement.quantity)
        assertEquals(listOf(AssetType.VIDEO, AssetType.IMAGE), placement.assetsType)
    }

    @Test
    fun `SponsoredBrandPlacementRequest factory method creates image placement`() {
        val placement = SponsoredBrandPlacementRequest.image(6)

        assertEquals(6, placement.quantity)
        assertEquals(listOf(AssetType.IMAGE), placement.assetsType)
    }

    @Test
    fun `SponsoredBrandPlacementRequest factory method creates video placement`() {
        val placement = SponsoredBrandPlacementRequest.video(3, VideoSize.P1080)

        assertEquals(3, placement.quantity)
        assertEquals(listOf(AssetType.VIDEO), placement.assetsType)
        assertEquals(VideoSize.P1080.value, placement.size)  // videoSize is converted to size
    }

    @Test
    fun `SponsoredBrandPlacementRequest factory method creates mixed placement`() {
        val placement = SponsoredBrandPlacementRequest.imageAndVideo(8, VideoSize.P720)

        assertEquals(8, placement.quantity)
        assertEquals(listOf(AssetType.IMAGE, AssetType.VIDEO), placement.assetsType)
        assertEquals(VideoSize.P720.value, placement.size)  // videoSize is converted to size
    }

    @Test
    fun `SponsoredBrandPlacementRequest validates positive quantity`() {
        assertThrows<IllegalArgumentException> {
            SponsoredBrandPlacementRequest.builder()
                .quantity(0)
                .build()
        }

        assertThrows<IllegalArgumentException> {
            SponsoredBrandPlacementRequest.builder()
                .quantity(-3)
                .build()
        }
    }

    @Test
    fun `SponsoredBrandPlacementRequest builder does not add duplicate asset types`() {
        val placement = SponsoredBrandPlacementRequest.builder()
            .quantity(5)
            .withImageAssets()
            .withImageAssets()  // Called twice
            .withVideoAssets()
            .withVideoAssets()  // Called twice
            .build()

        assertEquals(5, placement.quantity)
        assertEquals(listOf(AssetType.IMAGE, AssetType.VIDEO), placement.assetsType)
    }
}
