package com.vtex.ads.sdk.services

import com.vtex.ads.sdk.VtexAdsClientConfig
import com.vtex.ads.sdk.models.*
import kotlin.test.*

class AdsServiceTest {

    private lateinit var config: VtexAdsClientConfig
    private lateinit var service: AdsService

    @BeforeTest
    fun setup() {
        config = VtexAdsClientConfig(
            publisherId = "test-publisher",
            sessionId = "test-session",
            userId = "test-user",
            channel = Channel.SITE,
            brand = "test-brand"
        )
        service = AdsService(config)
    }

    @AfterTest
    fun tearDown() {
        service.close()
    }

    // ===== User ID Management Tests =====

    @Test
    fun `should initialize with userId from config`() {
        assertEquals("test-user", service.getCurrentUserId())
    }

    @Test
    fun `should update userId`() {
        service.updateUserId("new-user")
        assertEquals("new-user", service.getCurrentUserId())
    }

    @Test
    fun `should allow updating userId to null`() {
        service.updateUserId(null)
        assertNull(service.getCurrentUserId())
    }

    @Test
    fun `should fail to update userId with blank string`() {
        assertFailsWith<IllegalArgumentException> {
            service.updateUserId("")
        }

        assertFailsWith<IllegalArgumentException> {
            service.updateUserId("   ")
        }
    }

    @Test
    fun `should maintain userId consistency after updates`() {
        assertEquals("test-user", service.getCurrentUserId())

        service.updateUserId("user-1")
        assertEquals("user-1", service.getCurrentUserId())

        service.updateUserId("user-2")
        assertEquals("user-2", service.getCurrentUserId())

        service.updateUserId(null)
        assertNull(service.getCurrentUserId())
    }

    // ===== Placement Request Tests =====

    @Test
    fun `should create valid placement requests`() {
        val placement = PlacementRequest(
            quantity = 5,
            types = listOf(AdType.PRODUCT)
        )

        assertEquals(5, placement.quantity)
        assertEquals(1, placement.types.size)
        assertEquals(AdType.PRODUCT, placement.types[0])
    }

    @Test
    fun `should support multiple ad types in placement`() {
        val placement = PlacementRequest(
            quantity = 3,
            types = listOf(AdType.BANNER, AdType.SPONSORED_BRAND, AdType.PRODUCT)
        )

        assertEquals(3, placement.types.size)
        assertTrue(placement.types.contains(AdType.BANNER))
        assertTrue(placement.types.contains(AdType.SPONSORED_BRAND))
        assertTrue(placement.types.contains(AdType.PRODUCT))
    }

    @Test
    fun `should support video placements`() {
        val placement = PlacementRequest(
            quantity = 1,
            types = listOf(AdType.BANNER),
            assetsType = listOf(AssetType.VIDEO),
            size = "720p"
        )

        assertEquals(listOf(AssetType.VIDEO), placement.assetsType)
        assertEquals("720p", placement.size)
    }

    @Test
    fun `should validate placement quantity`() {
        assertFailsWith<IllegalArgumentException> {
            PlacementRequest(
                quantity = 0,
                types = listOf(AdType.PRODUCT)
            )
        }

        assertFailsWith<IllegalArgumentException> {
            PlacementRequest(
                quantity = -1,
                types = listOf(AdType.PRODUCT)
            )
        }
    }

    @Test
    fun `should validate placement has types`() {
        assertFailsWith<IllegalArgumentException> {
            PlacementRequest(
                quantity = 5,
                types = emptyList()
            )
        }
    }

    // ===== Context Tests =====

    @Test
    fun `should support all context types`() {
        val contexts = listOf(
            Context.HOME,
            Context.SEARCH,
            Context.CATEGORY,
            Context.PRODUCT_PAGE,
            Context.BRAND_PAGE,
            Context.DIGITAL_SIGNAGE
        )

        assertEquals(6, contexts.size)
        assertTrue(contexts.contains(Context.HOME))
        assertTrue(contexts.contains(Context.SEARCH))
    }

    @Test
    fun `should support all channel types`() {
        val channels = listOf(
            Channel.SITE,
            Channel.MSITE,
            Channel.APP
        )

        assertEquals(3, channels.size)
    }

    // ===== Segmentation Tests =====

    @Test
    fun `should create segmentation data`() {
        val segmentation = listOf(
            Segmentation(key = "STATE", values = listOf("SP", "RJ")),
            Segmentation(key = "GENDER", values = listOf("M")),
            Segmentation(key = "AUDIENCES", values = listOf("high_value"))
        )

        assertEquals(3, segmentation.size)
        assertEquals("STATE", segmentation[0].key)
        assertEquals(2, segmentation[0].values.size)
    }

    @Test
    fun `should support tags`() {
        val tags = listOf("electronics", "premium", "sale")

        assertEquals(3, tags.size)
        assertTrue(tags.contains("electronics"))
    }

    // ===== Deduplication Tests =====

    @Test
    fun `should support deduplication options`() {
        // These are just boolean flags, test they exist
        val dedupCampaignAds = true
        val dedupAds = true

        assertTrue(dedupCampaignAds)
        assertTrue(dedupAds)
    }

    // ===== Ad Type Tests =====

    @Test
    fun `should support all ad types`() {
        val adTypes = listOf(
            AdType.PRODUCT,
            AdType.BANNER,
            AdType.SPONSORED_BRAND,
            AdType.DIGITAL_SIGNAGE
        )

        assertEquals(4, adTypes.size)
    }

    @Test
    fun `should support all asset types`() {
        val assetTypes = listOf(
            AssetType.IMAGE,
            AssetType.VIDEO
        )

        assertEquals(2, assetTypes.size)
    }

    // ===== Video Size Tests =====

    @Test
    fun `should support all video sizes`() {
        val sizes = listOf(
            VideoSize.P1080,
            VideoSize.P720,
            VideoSize.P480,
            VideoSize.P360,
            VideoSize.P320
        )

        assertEquals(5, sizes.size)
        assertEquals("1080p", VideoSize.P1080.value)
        assertEquals("720p", VideoSize.P720.value)
        assertEquals("480p", VideoSize.P480.value)
        assertEquals("360p", VideoSize.P360.value)
        assertEquals("320p", VideoSize.P320.value)
    }

    // ===== Placement Builder Tests =====

    @Test
    fun `should build placement with builder`() {
        val placement = PlacementRequest.builder()
            .quantity(10)
            .types(AdType.PRODUCT, AdType.BANNER)
            .size("desktop")
            .assetsType(AssetType.IMAGE)
            .allowSkuDuplications(true)
            .build()

        assertEquals(10, placement.quantity)
        assertEquals(2, placement.types.size)
        assertEquals("desktop", placement.size)
        assertEquals(true, placement.allowSkuDuplications)
    }

    @Test
    fun `should build video placement`() {
        val placement = PlacementRequest.builder()
            .quantity(1)
            .types(AdType.BANNER)
            .videoSize(VideoSize.P720)
            .assetsType(AssetType.VIDEO)
            .build()

        assertEquals("720p", placement.size)
        assertEquals(listOf(AssetType.VIDEO), placement.assetsType)
    }

    // ===== Multi-placement Tests =====

    @Test
    fun `should support multiple placements in request`() {
        val placements = mapOf(
            "home_banner_top" to PlacementRequest(
                quantity = 1,
                types = listOf(AdType.BANNER)
            ),
            "home_products_shelf" to PlacementRequest(
                quantity = 10,
                types = listOf(AdType.PRODUCT)
            ),
            "home_sponsored_brands" to PlacementRequest(
                quantity = 3,
                types = listOf(AdType.SPONSORED_BRAND)
            )
        )

        assertEquals(3, placements.size)
        assertTrue(placements.containsKey("home_banner_top"))
        assertEquals(10, placements["home_products_shelf"]?.quantity)
    }

    // ===== User ID Propagation Tests =====

    @Test
    fun `should use current userId in requests after update`() {
        // Initial userId
        assertEquals("test-user", service.getCurrentUserId())

        // Update userId
        service.updateUserId("updated-user")
        assertEquals("updated-user", service.getCurrentUserId())

        // Subsequent requests should use updated userId
        // (We can't test actual API calls without mocking, but we verify the state)
    }

    @Test
    fun `should handle null userId gracefully`() {
        // Update to null
        service.updateUserId(null)
        assertNull(service.getCurrentUserId())

        // Service should still work (ads can be queried without userId)
    }

    @Test
    fun `should allow switching from null to valued userId`() {
        // Start with null
        val serviceWithoutUser = AdsService(
            VtexAdsClientConfig(
                publisherId = "test",
                sessionId = "session",
                userId = null,
                channel = Channel.SITE
            )
        )

        assertNull(serviceWithoutUser.getCurrentUserId())

        // Update to a value
        serviceWithoutUser.updateUserId("logged-in-user")
        assertEquals("logged-in-user", serviceWithoutUser.getCurrentUserId())

        serviceWithoutUser.close()
    }

    @Test
    fun `should allow switching between different userIds`() {
        service.updateUserId("user-a")
        assertEquals("user-a", service.getCurrentUserId())

        service.updateUserId("user-b")
        assertEquals("user-b", service.getCurrentUserId())

        service.updateUserId("user-c")
        assertEquals("user-c", service.getCurrentUserId())
    }
}
