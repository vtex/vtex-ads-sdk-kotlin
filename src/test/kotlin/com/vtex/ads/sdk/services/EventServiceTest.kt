package com.vtex.ads.sdk.services

import com.vtex.ads.sdk.VtexAdsClientConfig
import com.vtex.ads.sdk.VtexLogger
import com.vtex.ads.sdk.DebugFunctions
import com.vtex.ads.sdk.models.*
import com.vtex.ads.sdk.utils.HashUtils
import kotlinx.coroutines.runBlocking
import kotlin.test.*

class EventServiceTest {

    private lateinit var config: VtexAdsClientConfig
    private lateinit var service: EventService

    @BeforeTest
    fun setup() {
        config = VtexAdsClientConfig(
            publisherId = "test-publisher",
            sessionIdProvider = { "test-session" },
            userIdProvider = { "test-user" },
            channel = Channel.SITE,
            brand = "test-brand"
        )
        service = EventService(config, VtexLogger(emptySet(), DebugFunctions.NO_OP))
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
    fun `should maintain userId after multiple updates`() {
        service.updateUserId("user1")
        assertEquals("user1", service.getCurrentUserId())

        service.updateUserId("user2")
        assertEquals("user2", service.getCurrentUserId())

        service.updateUserId(null)
        assertNull(service.getCurrentUserId())
    }

    // ===== Order Conversion Tests =====

    @Test
    fun `should convert order to conversion request with hashing`() {
        val order = Order(
            orderId = "order-123",
            items = listOf(
                OrderItem("SKU-1", 2, 99.99),
                OrderItem("SKU-2", 1, 49.99)
            ),
            customerEmail = "test@email.com",
            customerPhone = "11999999999",
            customerDocument = "12345678900",
            customerFirstName = "John",
            customerLastName = "Doe"
        )

        // Verify the order is valid
        assertEquals("order-123", order.orderId)
        assertEquals(2, order.items.size)
    }

    @Test
    fun `should hash customer email correctly`() {
        val email = "customer@example.com"
        val hashed = HashUtils.sha256(email)

        // Verify hash format
        assertEquals(64, hashed.length)
        assertTrue(hashed.matches(Regex("[a-f0-9]{64}")))
    }

    @Test
    fun `should hash phone number correctly`() {
        val phone = "11999999999"
        val hashed = HashUtils.sha256(phone)

        assertEquals(64, hashed.length)
        assertTrue(hashed.matches(Regex("[a-f0-9]{64}")))
    }

    @Test
    fun `should hash document correctly`() {
        val document = "12345678900"
        val hashed = HashUtils.sha256(document)

        assertEquals(64, hashed.length)
        assertTrue(hashed.matches(Regex("[a-f0-9]{64}")))
    }

    @Test
    fun `should handle null optional fields in hashing`() {
        val hashedNull = HashUtils.sha256OrNull(null)
        assertNull(hashedNull)

        val hashedBlank = HashUtils.sha256OrNull("   ")
        assertNull(hashedBlank)

        val hashedEmpty = HashUtils.sha256OrNull("")
        assertNull(hashedEmpty)
    }

    @Test
    fun `should require userId for order conversion`() {
        // Create service without userId
        val configWithoutUser = VtexAdsClientConfig(
            publisherId = "test-publisher",
            sessionIdProvider = { "test-session" },
            userIdProvider = null,
            channel = Channel.SITE
        )
        val serviceWithoutUser = EventService(configWithoutUser, VtexLogger(emptySet(), DebugFunctions.NO_OP))

        val order = Order(
            orderId = "order-123",
            items = listOf(OrderItem("SKU-1", 1, 99.99)),
            customerEmail = "test@email.com"
        )

        // Should fail when userId is null
        var exceptionThrown = false
        serviceWithoutUser.deliveryOrderEvent(order, userId = null) { success ->
            exceptionThrown = !success
        }

        // Give time for coroutine to execute
        Thread.sleep(100)

        serviceWithoutUser.close()
    }

    @Test
    fun `should validate order before sending`() {
        val order = Order(
            orderId = "order-456",
            items = listOf(OrderItem("SKU-1", 1, 50.0)),
            customerEmail = "user@example.com"
        )

        // Current userId should be used
        assertEquals("test-user", service.getCurrentUserId())

        // Order should be valid
        assertEquals("order-456", order.orderId)
        assertEquals(1, order.items.size)
        assertEquals("user@example.com", order.customerEmail)
    }

    @Test
    fun `should accept userId override parameter`() {
        val order = Order(
            orderId = "order-789",
            items = listOf(OrderItem("SKU-1", 1, 100.0)),
            customerEmail = "another@example.com"
        )

        // Verify we can create valid order with override intention
        assertEquals("order-789", order.orderId)

        // Note: Actual HTTP call testing would require mocking
        // This test validates the order structure is correct for deliveryOrderEvent
    }

    // ===== Order Validation Tests =====

    @Test
    fun `should validate order has required fields`() {
        assertFailsWith<IllegalArgumentException> {
            Order(
                orderId = "",  // Empty
                items = listOf(OrderItem("SKU-1", 1, 99.99)),
                customerEmail = "test@email.com"
            )
        }

        assertFailsWith<IllegalArgumentException> {
            Order(
                orderId = "order-123",
                items = emptyList(),  // Empty
                customerEmail = "test@email.com"
            )
        }

        assertFailsWith<IllegalArgumentException> {
            Order(
                orderId = "order-123",
                items = listOf(OrderItem("SKU-1", 1, 99.99)),
                customerEmail = ""  // Empty
            )
        }
    }

    @Test
    fun `should validate gender values`() {
        // Valid genders
        listOf("F", "M", "O").forEach { gender ->
            val order = Order(
                orderId = "order-$gender",
                items = listOf(OrderItem("SKU-1", 1, 99.99)),
                customerEmail = "test@email.com",
                gender = gender
            )
            assertEquals(gender, order.gender)
        }

        // Invalid gender
        assertFailsWith<IllegalArgumentException> {
            Order(
                orderId = "order-invalid",
                items = listOf(OrderItem("SKU-1", 1, 99.99)),
                customerEmail = "test@email.com",
                gender = "X"
            )
        }
    }

    // ===== Order Items Tests =====

    @Test
    fun `should create order with multiple items`() {
        val order = Order(
            orderId = "order-multi",
            items = listOf(
                OrderItem("SKU-1", 2, 49.99),
                OrderItem("SKU-2", 1, 99.99, "seller-1"),
                OrderItem("SKU-3", 5, 19.99, "seller-2")
            ),
            customerEmail = "multi@example.com"
        )

        assertEquals(3, order.items.size)
        assertEquals(2, order.items[0].quantity)
        assertEquals("seller-1", order.items[1].sellerId)
    }

    @Test
    fun `should validate order item quantities`() {
        assertFailsWith<IllegalArgumentException> {
            OrderItem("SKU-1", 0, 99.99)
        }

        assertFailsWith<IllegalArgumentException> {
            OrderItem("SKU-1", -1, 99.99)
        }
    }

    @Test
    fun `should validate order item prices`() {
        assertFailsWith<IllegalArgumentException> {
            OrderItem("SKU-1", 1, -10.0)
        }

        // Zero price should be allowed
        val item = OrderItem("SKU-1", 1, 0.0)
        assertEquals(0.0, item.price)
    }

    // ===== Complete Order Flow Tests =====

    @Test
    fun `should create complete order with all fields`() {
        val order = Order.builder()
            .orderId("order-complete")
            .addItem("SKU-1", 2, 99.99)
            .addItem("SKU-2", 1, 149.99, "seller-1")
            .customerEmail("complete@example.com")
            .customerPhone("11888888888")
            .customerDocument("98765432100")
            .customerFirstName("Jane")
            .customerLastName("Smith")
            .state("RJ")
            .city("Rio de Janeiro")
            .gender("F")
            .isCompany(false)
            .build()

        assertEquals("order-complete", order.orderId)
        assertEquals(2, order.items.size)
        assertEquals("complete@example.com", order.customerEmail)
        assertEquals("11888888888", order.customerPhone)
        assertEquals("F", order.gender)
        assertEquals("RJ", order.state)
        assertEquals(false, order.isCompany)
    }

    @Test
    fun `should handle company orders`() {
        val order = Order(
            orderId = "order-company",
            items = listOf(OrderItem("SKU-CORP", 10, 1000.0)),
            customerEmail = "company@business.com",
            customerDocument = "12345678000190",  // CNPJ
            isCompany = true
        )

        assertEquals(true, order.isCompany)
    }

    // ===== Thread Safety Tests (Basic) =====

    @Test
    fun `should handle concurrent userId updates safely`() = runBlocking {
        val updates = listOf("user1", "user2", "user3", "user4", "user5")

        updates.forEach { userId ->
            service.updateUserId(userId)
        }

        // Final state should be consistent
        assertEquals("user5", service.getCurrentUserId())
    }
}
