package com.vtex.ads.sdk.models

import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class OrderTest {

    @Test
    fun `should create order with required fields`() {
        val order = Order(
            orderId = "order-123",
            items = listOf(
                OrderItem("SKU-1", 1, 99.99)
            ),
            customerEmail = "user@email.com"
        )

        assertEquals("order-123", order.orderId)
        assertEquals(1, order.items.size)
        assertEquals("user@email.com", order.customerEmail)
    }

    @Test
    fun `should create order with all fields`() {
        val order = Order(
            orderId = "order-456",
            createdAt = "2025-01-01T12:00:00Z",
            items = listOf(
                OrderItem("SKU-1", 2, 49.99),
                OrderItem("SKU-2", 1, 99.99)
            ),
            customerEmail = "customer@example.com",
            customerPhone = "11999999999",
            customerDocument = "12345678900",
            customerFirstName = "John",
            customerLastName = "Doe",
            gender = "M",
            state = "SP",
            city = "São Paulo",
            isCompany = false
        )

        assertEquals("order-456", order.orderId)
        assertEquals(2, order.items.size)
        assertEquals("customer@example.com", order.customerEmail)
        assertEquals("11999999999", order.customerPhone)
        assertEquals("M", order.gender)
        assertEquals("SP", order.state)
    }

    @Test
    fun `should fail with blank order id`() {
        assertFailsWith<IllegalArgumentException> {
            Order(
                orderId = "",
                items = listOf(OrderItem("SKU-1", 1, 99.99)),
                customerEmail = "user@email.com"
            )
        }
    }

    @Test
    fun `should fail with empty items list`() {
        assertFailsWith<IllegalArgumentException> {
            Order(
                orderId = "order-123",
                items = emptyList(),
                customerEmail = "user@email.com"
            )
        }
    }

    @Test
    fun `should fail with blank customer email`() {
        assertFailsWith<IllegalArgumentException> {
            Order(
                orderId = "order-123",
                items = listOf(OrderItem("SKU-1", 1, 99.99)),
                customerEmail = ""
            )
        }
    }

    @Test
    fun `should fail with invalid gender`() {
        assertFailsWith<IllegalArgumentException> {
            Order(
                orderId = "order-123",
                items = listOf(OrderItem("SKU-1", 1, 99.99)),
                customerEmail = "user@email.com",
                gender = "X"  // Invalid
            )
        }
    }

    @Test
    fun `should accept valid genders`() {
        val genders = listOf("F", "M", "O")

        genders.forEach { gender ->
            val order = Order(
                orderId = "order-123",
                items = listOf(OrderItem("SKU-1", 1, 99.99)),
                customerEmail = "user@email.com",
                gender = gender
            )
            assertEquals(gender, order.gender)
        }
    }

    @Test
    fun `should build order with builder`() {
        val order = Order.builder()
            .orderId("order-789")
            .addItem("SKU-1", 2, 49.99)
            .addItem("SKU-2", 1, 99.99, "seller-1")
            .customerEmail("builder@example.com")
            .customerPhone("11888888888")
            .state("RJ")
            .city("Rio de Janeiro")
            .build()

        assertEquals("order-789", order.orderId)
        assertEquals(2, order.items.size)
        assertEquals("builder@example.com", order.customerEmail)
        assertEquals("RJ", order.state)
    }

    @Test
    fun `should build order with createdAt instant`() {
        val instant = Instant.parse("2025-01-15T10:30:00Z")

        val order = Order.builder()
            .orderId("order-999")
            .createdAt(instant)
            .addItem("SKU-1", 1, 100.0)
            .customerEmail("test@example.com")
            .build()

        assertEquals("2025-01-15T10:30:00Z", order.createdAt)
    }

    @Test
    fun `should build order with item list`() {
        val items = listOf(
            OrderItem("SKU-1", 1, 50.0),
            OrderItem("SKU-2", 2, 30.0)
        )

        val order = Order.builder()
            .orderId("order-111")
            .items(items)
            .customerEmail("list@example.com")
            .build()

        assertEquals(2, order.items.size)
        assertEquals(items, order.items)
    }

    @Test
    fun `should create order item with valid data`() {
        val item = OrderItem("SKU-123", 3, 29.99, "seller-1")

        assertEquals("SKU-123", item.productSku)
        assertEquals(3, item.quantity)
        assertEquals(29.99, item.price)
        assertEquals("seller-1", item.sellerId)
    }

    @Test
    fun `should fail order item with blank SKU`() {
        assertFailsWith<IllegalArgumentException> {
            OrderItem("", 1, 99.99)
        }
    }

    @Test
    fun `should fail order item with zero quantity`() {
        assertFailsWith<IllegalArgumentException> {
            OrderItem("SKU-1", 0, 99.99)
        }
    }

    @Test
    fun `should fail order item with negative quantity`() {
        assertFailsWith<IllegalArgumentException> {
            OrderItem("SKU-1", -1, 99.99)
        }
    }

    @Test
    fun `should fail order item with negative price`() {
        assertFailsWith<IllegalArgumentException> {
            OrderItem("SKU-1", 1, -10.0)
        }
    }

    @Test
    fun `should allow order item with zero price`() {
        val item = OrderItem("SKU-1", 1, 0.0)
        assertEquals(0.0, item.price)
    }
}
