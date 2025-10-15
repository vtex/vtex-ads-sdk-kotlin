package com.vtex.ads.sdk

import com.vtex.ads.sdk.models.Channel
import com.vtex.ads.sdk.models.Order
import com.vtex.ads.sdk.services.AdsService
import com.vtex.ads.sdk.services.EventService
import java.io.Closeable

/**
 * Main client for interacting with the VTEX Ads API.
 *
 * This client provides a simple interface for querying ads and sending events.
 *
 * Example usage:
 * ```
 * val client = VtexAdsClient(
 *     publisherId = "your-publisher-id",
 *     sessionIdProvider = { getCurrentSessionId() },
 *     userIdProvider = { getCurrentUserId() },
 *     channel = Channel.SITE
 * )
 *
 * // Query ads
 * val ads = client.ads.getHomeAds(
 *     placements = mapOf(
 *         "home_banner" to PlacementRequest.builder()
 *             .quantity(1)
 *             .types(AdType.BANNER)
 *             .build()
 *     )
 * )
 *
 * // Send events (non-blocking)
 * ads.getAllAds().forEach { ad ->
 *     client.events.deliveryBeaconEvent(ad.impressionUrl)
 * }
 *
 * // Don't forget to close when done
 * client.close()
 * ```
 *
 * @property config Configuration for the SDK
 * @property ads Service for querying ads
 * @property events Service for sending events (non-blocking)
 */
class VtexAdsClient private constructor(
    val config: VtexAdsClientConfig
) : Closeable {

    /**
     * Service for querying ads from the VTEX Ads API.
     */
    val ads: AdsService = AdsService(config)

    /**
     * Service for sending ad events asynchronously (delivery events and conversions).
     */
    val events: EventService = EventService(config)

    /**
     * Updates the user ID for both ads and events services.
     * This is useful when a user logs in after the client was created.
     *
     * Example:
     * ```
     * // Client created with anonymous user
     * val client = VtexAdsClient(
     *     publisherId = "pub-123",
     *     sessionIdProvider = { getCurrentSessionId() },
     *     userIdProvider = null, // Anonymous user
     *     channel = Channel.SITE
     * )
     *
     * // User logs in
     * client.updateUserId("user-789")
     *
     * // Now ads queries and events will use the new user ID
     * ```
     *
     * @param newUserId The new user ID (can be null for anonymous users)
     */
    fun updateUserId(newUserId: String?) {
        ads.updateUserId(newUserId)
        events.updateUserId(newUserId)
    }

    /**
     * Gets the current user ID.
     * @return The current user ID, or null if user is anonymous
     */
    fun getCurrentUserId(): String? = events.getCurrentUserId()

    /**
     * Sends an order conversion event (non-blocking).
     * This is a simplified wrapper that automatically uses the current user ID
     * and configuration from the client.
     *
     * Example:
     * ```
     * client.deliveryOrderEvent(
     *     Order.builder()
     *         .orderId("order-123")
     *         .addItem("SKU-1", quantity = 2, price = 99.99)
     *         .customerEmail("user@email.com")
     *         .customerPhone("11999999999")
     *         .state("SP")
     *         .city("São Paulo")
     *         .build()
     * ) { success ->
     *     if (success) {
     *         println("Order tracked successfully!")
     *     }
     * }
     * ```
     *
     * @param order The order data
     * @param onComplete Optional callback with success/failure status
     */
    fun deliveryOrderEvent(
        order: Order,
        onComplete: ((Boolean) -> Unit)? = null
    ) {
        events.deliveryOrderEvent(order, getCurrentUserId(), onComplete)
    }

    /**
     * Closes the client and releases all resources.
     * Should be called when the client is no longer needed.
     */
    override fun close() {
        ads.close()
        events.close()
    }

    companion object {
        /**
         * SDK version
         */
        const val VERSION = "0.1.0-SNAPSHOT"

        /**
         * Creates a new VtexAdsClient with the simplified configuration.
         *
         * @param publisherId Publisher ID (required)
         * @param sessionIdProvider Function that returns the current session ID (required)
         * @param userIdProvider Function that returns the current user ID (optional)
         * @param channel Channel (SITE, MSITE, APP)
         * @param brand Brand/site name (optional, required when publisher has multiple sites)
         * @param timeout Request timeout in milliseconds (default: 500ms, max: 10000ms)
         * @param maxRetries Maximum number of retry attempts (default: 3)
         * @param retryDelayMs Delay between retries in milliseconds (default: 100ms)
         */
        operator fun invoke(
            publisherId: String,
            sessionIdProvider: () -> String,
            userIdProvider: (() -> String?)? = null,
            channel: Channel,
            brand: String? = null,
            timeout: Long = 500L,
            maxRetries: Int = 3,
            retryDelayMs: Long = 100L
        ): VtexAdsClient {
            val config = VtexAdsClientConfig(
                publisherId = publisherId,
                sessionIdProvider = sessionIdProvider,
                userIdProvider = userIdProvider,
                channel = channel,
                brand = brand,
                timeout = timeout,
                maxRetries = maxRetries,
                retryDelayMs = retryDelayMs
            )
            return VtexAdsClient(config)
        }

        /**
         * Creates a new VtexAdsClient with static values (backward compatibility).
         * This method wraps static values in provider functions.
         *
         * @param publisherId Publisher ID (required)
         * @param sessionId Static session ID (required)
         * @param userId Static user ID (optional)
         * @param channel Channel (SITE, MSITE, APP)
         * @param brand Brand/site name (optional, required when publisher has multiple sites)
         * @param timeout Request timeout in milliseconds (default: 500ms, max: 10000ms)
         * @param maxRetries Maximum number of retry attempts (default: 3)
         * @param retryDelayMs Delay between retries in milliseconds (default: 100ms)
         */
        fun createWithStaticValues(
            publisherId: String,
            sessionId: String,
            userId: String? = null,
            channel: Channel,
            brand: String? = null,
            timeout: Long = 500L,
            maxRetries: Int = 3,
            retryDelayMs: Long = 100L
        ): VtexAdsClient {
            return invoke(
                publisherId = publisherId,
                sessionIdProvider = { sessionId },
                userIdProvider = userId?.let { { it } },
                channel = channel,
                brand = brand,
                timeout = timeout,
                maxRetries = maxRetries,
                retryDelayMs = retryDelayMs
            )
        }

        /**
         * Creates a new VtexAdsClient from a configuration object.
         */
        operator fun invoke(config: VtexAdsClientConfig): VtexAdsClient {
            return VtexAdsClient(config)
        }
    }
}
