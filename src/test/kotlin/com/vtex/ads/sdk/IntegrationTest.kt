package com.vtex.ads.sdk

import com.vtex.ads.sdk.models.*
import kotlin.test.*

/**
 * Integration tests covering complete end-to-end flows.
 */
class IntegrationTest {

    // ===== Complete Flow: Anonymous → Logged In → Purchase =====

    @Test
    fun `should handle complete anonymous to logged in flow`() {
        // 1. Create client as anonymous user
        val client = VtexAdsClient(
            publisherId = "test-pub",
            sessionId = "session-123",
            userId = null,  // Anonymous
            channel = Channel.SITE
        )

        assertNull(client.getCurrentUserId())

        // 2. User logs in
        client.updateUserId("user-456")
        assertEquals("user-456", client.getCurrentUserId())

        // 3. Verify userId propagated to services
        assertEquals("user-456", client.ads.getCurrentUserId())
        assertEquals("user-456", client.events.getCurrentUserId())

        client.close()
    }

    @Test
    fun `should handle complete order flow with logged in user`() {
        // 1. Create client with logged in user
        val client = VtexAdsClient(
            publisherId = "test-pub",
            sessionId = "session-789",
            userId = "user-789",
            channel = Channel.SITE
        )

        // 2. Create order
        val order = Order.builder()
            .orderId("order-integration-test")
            .addItem("SKU-TEST-1", 2, 49.99)
            .addItem("SKU-TEST-2", 1, 99.99)
            .customerEmail("integration@test.com")
            .customerPhone("11999999999")
            .state("SP")
            .city("São Paulo")
            .build()

        // 3. Verify order is valid
        assertEquals("order-integration-test", order.orderId)
        assertEquals(2, order.items.size)
        assertEquals("user-789", client.getCurrentUserId())

        // 4. Order can be sent (we won't actually send in test)
        assertNotNull(order)

        client.close()
    }

    @Test
    fun `should handle user logout flow`() {
        // 1. Start with logged in user
        val client = VtexAdsClient(
            publisherId = "test-pub",
            sessionId = "session-logout",
            userId = "user-logout",
            channel = Channel.SITE
        )

        assertEquals("user-logout", client.getCurrentUserId())

        // 2. User logs out (userId becomes null)
        client.updateUserId(null)
        assertNull(client.getCurrentUserId())

        // 3. Services should handle null userId
        assertNull(client.ads.getCurrentUserId())
        assertNull(client.events.getCurrentUserId())

        client.close()
    }

    // ===== Multi-User Session Tests =====

    @Test
    fun `should handle switching between users in same session`() {
        val client = VtexAdsClient(
            publisherId = "test-pub",
            sessionId = "session-switch",
            userId = "user-a",
            channel = Channel.SITE
        )

        assertEquals("user-a", client.getCurrentUserId())

        // Switch to user B
        client.updateUserId("user-b")
        assertEquals("user-b", client.getCurrentUserId())

        // Switch to user C
        client.updateUserId("user-c")
        assertEquals("user-c", client.getCurrentUserId())

        client.close()
    }

    // ===== Configuration Tests =====

    @Test
    fun `should create client with minimal config`() {
        val client = VtexAdsClient(
            publisherId = "minimal-pub",
            sessionId = "minimal-session",
            channel = Channel.MSITE
        )

        assertEquals("minimal-pub", client.config.publisherId)
        assertEquals("minimal-session", client.config.sessionId)
        assertNull(client.config.userId)
        assertNull(client.config.brand)
        assertEquals(Channel.MSITE, client.config.channel)

        client.close()
    }

    @Test
    fun `should create client with full config`() {
        val client = VtexAdsClient(
            publisherId = "full-pub",
            sessionId = "full-session",
            userId = "full-user",
            channel = Channel.APP,
            brand = "full-brand",
            timeout = 1000L
        )

        assertEquals("full-pub", client.config.publisherId)
        assertEquals("full-session", client.config.sessionId)
        assertEquals("full-user", client.config.userId)
        assertEquals("full-brand", client.config.brand)
        assertEquals(Channel.APP, client.config.channel)
        assertEquals(1000L, client.config.timeout)

        client.close()
    }

    @Test
    fun `should create client for different channels`() {
        val siteClient = VtexAdsClient(
            publisherId = "pub",
            sessionId = "session",
            channel = Channel.SITE
        )
        assertEquals(Channel.SITE, siteClient.config.channel)
        siteClient.close()

        val msiteClient = VtexAdsClient(
            publisherId = "pub",
            sessionId = "session",
            channel = Channel.MSITE
        )
        assertEquals(Channel.MSITE, msiteClient.config.channel)
        msiteClient.close()

        val appClient = VtexAdsClient(
            publisherId = "pub",
            sessionId = "session",
            channel = Channel.APP
        )
        assertEquals(Channel.APP, appClient.config.channel)
        appClient.close()
    }

    // ===== Order Builder Integration Tests =====

    @Test
    fun `should build complete order for checkout flow`() {
        val order = Order.builder()
            .orderId("checkout-order-123")
            .addItem("PRODUCT-A", 2, 199.99, "seller-1")
            .addItem("PRODUCT-B", 1, 299.99, "seller-2")
            .addItem("PRODUCT-C", 3, 49.99)
            .customerEmail("checkout@customer.com")
            .customerPhone("11888888888")
            .customerDocument("12345678900")
            .customerFirstName("Maria")
            .customerLastName("Silva")
            .state("RJ")
            .city("Rio de Janeiro")
            .gender("F")
            .isCompany(false)
            .build()

        // Verify order structure
        assertEquals("checkout-order-123", order.orderId)
        assertEquals(3, order.items.size)

        // Verify items
        assertEquals("PRODUCT-A", order.items[0].productSku)
        assertEquals(2, order.items[0].quantity)
        assertEquals(199.99, order.items[0].price)
        assertEquals("seller-1", order.items[0].sellerId)

        // Verify customer data
        assertEquals("checkout@customer.com", order.customerEmail)
        assertEquals("11888888888", order.customerPhone)
        assertEquals("F", order.gender)
        assertEquals("RJ", order.state)
        assertEquals(false, order.isCompany)
    }

    @Test
    fun `should build minimal order`() {
        val order = Order(
            orderId = "minimal-order",
            items = listOf(OrderItem("SKU-MIN", 1, 10.0)),
            customerEmail = "minimal@test.com"
        )

        assertEquals("minimal-order", order.orderId)
        assertEquals(1, order.items.size)
        assertEquals("minimal@test.com", order.customerEmail)
        assertNull(order.customerPhone)
        assertNull(order.state)
    }

    // ===== Placement Configuration Integration Tests =====

    @Test
    fun `should create multiple placements for home page`() {
        val placements = mapOf(
            "home_hero_banner" to PlacementRequest.builder()
                .quantity(1)
                .types(AdType.BANNER)
                .assetsType(AssetType.IMAGE)
                .size("desktop")
                .build(),

            "home_featured_products" to PlacementRequest.builder()
                .quantity(8)
                .types(AdType.PRODUCT)
                .build(),

            "home_sponsored_brands" to PlacementRequest.builder()
                .quantity(3)
                .types(AdType.SPONSORED_BRAND)
                .assetsType(AssetType.IMAGE, AssetType.VIDEO)
                .build()
        )

        assertEquals(3, placements.size)
        assertEquals(1, placements["home_hero_banner"]?.quantity)
        assertEquals(8, placements["home_featured_products"]?.quantity)
        assertEquals(3, placements["home_sponsored_brands"]?.quantity)
    }

    @Test
    fun `should create placements for search page`() {
        val placements = mapOf(
            "search_top_banner" to PlacementRequest.builder()
                .quantity(1)
                .types(AdType.BANNER, AdType.SPONSORED_BRAND)
                .assetsType(AssetType.VIDEO)
                .videoSize(VideoSize.P720)
                .build(),

            "search_sponsored_products" to PlacementRequest.builder()
                .quantity(5)
                .types(AdType.PRODUCT)
                .allowSkuDuplications(false)
                .build()
        )

        assertEquals(2, placements.size)
        assertEquals("720p", placements["search_top_banner"]?.size)
        assertEquals(false, placements["search_sponsored_products"]?.allowSkuDuplications)
    }

    // ===== Error Scenarios =====

    @Test
    fun `should fail with invalid config`() {
        assertFailsWith<IllegalArgumentException> {
            VtexAdsClient(
                publisherId = "",  // Empty
                sessionId = "session",
                channel = Channel.SITE
            )
        }

        assertFailsWith<IllegalArgumentException> {
            VtexAdsClient(
                publisherId = "pub",
                sessionId = "",  // Empty
                channel = Channel.SITE
            )
        }

        assertFailsWith<IllegalArgumentException> {
            VtexAdsClient(
                publisherId = "pub",
                sessionId = "session",
                channel = Channel.SITE,
                timeout = -100  // Negative
            )
        }
    }

    @Test
    fun `should fail with invalid order`() {
        assertFailsWith<IllegalArgumentException> {
            Order(
                orderId = "",
                items = listOf(OrderItem("SKU", 1, 10.0)),
                customerEmail = "test@test.com"
            )
        }

        assertFailsWith<IllegalArgumentException> {
            Order(
                orderId = "order",
                items = emptyList(),
                customerEmail = "test@test.com"
            )
        }

        assertFailsWith<IllegalArgumentException> {
            Order(
                orderId = "order",
                items = listOf(OrderItem("SKU", 1, 10.0)),
                customerEmail = ""
            )
        }
    }

    // ===== Service Cleanup Tests =====

    @Test
    fun `should properly close all services`() {
        val client = VtexAdsClient(
            publisherId = "cleanup-pub",
            sessionId = "cleanup-session",
            channel = Channel.SITE
        )

        // Services should be created
        assertNotNull(client.ads)
        assertNotNull(client.events)

        // Should close without errors
        client.close()
    }
}
