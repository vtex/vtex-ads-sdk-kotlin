package com.vtex.ads.sdk.models

/**
 * Specialized builder for Product ad placements.
 * Ensures type safety and validation for product-specific requirements.
 *
 * This is a wrapper around PlacementRequest that provides a type-safe API
 * specifically for product ads.
 */
object ProductPlacementRequest {

    class Builder {
        private var quantity: Int = 1
        private var allowSkuDuplications: Boolean = true

        /**
         * Sets the number of product ads to request.
         * @param quantity Number of ads (must be positive)
         */
        fun quantity(quantity: Int) = apply {
            require(quantity > 0) { "Quantity must be positive" }
            this.quantity = quantity
        }

        /**
         * Controls whether duplicate SKUs are allowed in the response.
         * @param allow If false, each product SKU will appear at most once
         */
        fun allowSkuDuplications(allow: Boolean) = apply {
            this.allowSkuDuplications = allow
        }

        /**
         * Disables SKU duplications (convenience method).
         * Each product SKU will appear at most once in the response.
         */
        fun uniqueSkus() = apply {
            this.allowSkuDuplications = false
        }

        fun build(): PlacementRequest {
            return PlacementRequest(
                quantity = quantity,
                types = listOf(AdType.PRODUCT),
                size = null,
                assetsType = null,
                allowSkuDuplications = allowSkuDuplications
            )
        }
    }

    /**
     * Creates a new builder for product placements.
     */
    @JvmStatic
    fun builder(): Builder = Builder()

    /**
     * Quick factory method for product placements.
     */
    @JvmStatic
    fun create(quantity: Int, allowSkuDuplications: Boolean = true): PlacementRequest {
        return builder()
            .quantity(quantity)
            .allowSkuDuplications(allowSkuDuplications)
            .build()
    }

    /**
     * Quick factory method for unique product placements.
     */
    @JvmStatic
    fun unique(quantity: Int): PlacementRequest {
        return builder()
            .quantity(quantity)
            .uniqueSkus()
            .build()
    }
}
