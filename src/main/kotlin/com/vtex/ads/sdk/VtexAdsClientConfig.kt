package com.vtex.ads.sdk

import com.vtex.ads.sdk.models.Channel

/**
 * Simplified configuration for VTEX Ads SDK client focused on ad queries.
 *
 * @property publisherId Publisher ID (required for API calls)
 * @property sessionId Unique session ID (required, should be consistent throughout navigation)
 * @property userId Unique user ID (optional, but recommended)
 * @property channel Channel of access (SITE, MSITE, APP)
 * @property brand Brand/site name (required when publisher has multiple sites)
 * @property timeout Request timeout in milliseconds (default: 500ms, max: 10000ms)
 * @property maxRetries Maximum number of retry attempts on network errors or 5xx responses (default: 3)
 * @property retryDelayMs Delay between retry attempts in milliseconds (default: 100ms)
 */
data class VtexAdsClientConfig(
    val publisherId: String,
    val sessionId: String,
    val userId: String? = null,
    val channel: Channel,
    val brand: String? = null,
    val timeout: Long = 500L,  // Default 500ms
    val maxRetries: Int = 3,    // Default 3 retries
    val retryDelayMs: Long = 100L  // Default 100ms between retries
) {
    init {
        require(publisherId.isNotBlank()) { "Publisher ID cannot be blank" }
        require(sessionId.isNotBlank()) { "Session ID cannot be blank" }
        require(timeout > 0) { "Timeout must be positive" }
        require(timeout <= MAX_TIMEOUT) { "Timeout cannot exceed ${MAX_TIMEOUT}ms" }
        require(maxRetries >= 0) { "Max retries cannot be negative" }
        require(retryDelayMs >= 0) { "Retry delay cannot be negative" }
    }

    companion object {
        const val MAX_TIMEOUT = 10000L  // Maximum 10 seconds
        const val DEFAULT_BASE_URL = "https://newtail-media.newtail.com"
        const val DEFAULT_EVENTS_BASE_URL = "https://newtail-media.newtail.com"
    }
}
