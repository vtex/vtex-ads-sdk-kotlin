package com.vtex.ads.sdk.models

import java.time.Instant

/**
 * Simplified order representation for sending conversion events.
 * This class provides an easier way to send order data compared to manually
 * constructing a ConversionRequest.
 *
 * @property orderId Unique order identifier
 * @property createdAt Order creation timestamp (ISO 8601 in UTC)
 * @property items List of order items
 * @property customerEmail Customer's email address (will be hashed automatically)
 * @property customerPhone Customer's phone number (will be hashed automatically)
 * @property customerDocument Customer's tax document/CPF/CNPJ (will be hashed automatically)
 * @property customerFirstName Customer's first name (will be hashed automatically)
 * @property customerLastName Customer's last name (will be hashed automatically)
 * @property gender Customer's gender (F, M, O, or null)
 * @property state Customer's state/UF
 * @property city Customer's city
 * @property isCompany Whether the sale was to a company (true) or individual (false)
 */
data class Order(
    val orderId: String,
    val createdAt: String = Instant.now().toString(),
    val items: List<OrderItem>,
    val customerEmail: String,
    val customerPhone: String? = null,
    val customerDocument: String? = null,
    val customerFirstName: String? = null,
    val customerLastName: String? = null,
    val gender: String? = null,
    val state: String? = null,
    val city: String? = null,
    val isCompany: Boolean? = null
) {
    init {
        require(orderId.isNotBlank()) { "Order ID cannot be blank" }
        require(items.isNotEmpty()) { "Order must have at least one item" }
        require(customerEmail.isNotBlank()) { "Customer email cannot be blank" }
        gender?.let {
            require(it in listOf("F", "M", "O")) {
                "Gender must be F (female), M (male), or O (other)"
            }
        }
    }

    /**
     * Builder for constructing Order instances with a fluent API.
     */
    class Builder {
        private var orderId: String = ""
        private var createdAt: String = Instant.now().toString()
        private val items: MutableList<OrderItem> = mutableListOf()
        private var customerEmail: String = ""
        private var customerPhone: String? = null
        private var customerDocument: String? = null
        private var customerFirstName: String? = null
        private var customerLastName: String? = null
        private var gender: String? = null
        private var state: String? = null
        private var city: String? = null
        private var isCompany: Boolean? = null

        fun orderId(orderId: String) = apply { this.orderId = orderId }
        fun createdAt(createdAt: String) = apply { this.createdAt = createdAt }
        fun createdAt(instant: Instant) = apply { this.createdAt = instant.toString() }

        fun addItem(item: OrderItem) = apply { this.items.add(item) }
        fun addItem(productSku: String, quantity: Int, price: Double, sellerId: String? = null) = apply {
            this.items.add(OrderItem(productSku, quantity, price, sellerId))
        }
        fun items(items: List<OrderItem>) = apply {
            this.items.clear()
            this.items.addAll(items)
        }

        fun customerEmail(email: String) = apply { this.customerEmail = email }
        fun customerPhone(phone: String?) = apply { this.customerPhone = phone }
        fun customerDocument(document: String?) = apply { this.customerDocument = document }
        fun customerFirstName(firstName: String?) = apply { this.customerFirstName = firstName }
        fun customerLastName(lastName: String?) = apply { this.customerLastName = lastName }
        fun gender(gender: String?) = apply { this.gender = gender }
        fun state(state: String?) = apply { this.state = state }
        fun city(city: String?) = apply { this.city = city }
        fun isCompany(isCompany: Boolean?) = apply { this.isCompany = isCompany }

        fun build(): Order = Order(
            orderId = orderId,
            createdAt = createdAt,
            items = items.toList(),
            customerEmail = customerEmail,
            customerPhone = customerPhone,
            customerDocument = customerDocument,
            customerFirstName = customerFirstName,
            customerLastName = customerLastName,
            gender = gender,
            state = state,
            city = city,
            isCompany = isCompany
        )
    }

    companion object {
        /**
         * Creates a new builder for constructing an Order.
         */
        fun builder() = Builder()
    }
}

/**
 * Item in an order.
 *
 * @property productSku Product SKU
 * @property quantity Quantity purchased
 * @property price Unit price (NOT multiplied by quantity)
 * @property sellerId Optional seller ID
 */
data class OrderItem(
    val productSku: String,
    val quantity: Int,
    val price: Double,
    val sellerId: String? = null
) {
    init {
        require(productSku.isNotBlank()) { "Product SKU cannot be blank" }
        require(quantity > 0) { "Quantity must be greater than 0" }
        require(price >= 0) { "Price cannot be negative" }
    }
}
