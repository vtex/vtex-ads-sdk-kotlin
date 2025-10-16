package com.vtex.ads.sdk

import com.vtex.ads.sdk.models.Channel

/**
 * Simplified configuration for VTEX Ads SDK client focused on ad queries.
 *
 * @property publisherId Publisher ID (required for API calls)
 * @property sessionIdProvider Function that returns the current session ID (required, called on each request)
 * @property userIdProvider Function that returns the current user ID (optional, called on each request)
 * @property channel Channel of access (SITE, MSITE, APP)
 * @property brand Brand/site name (required when publisher has multiple sites)
 * @property timeout Request timeout in milliseconds (default: 500ms, max: 10000ms)
 * @property maxRetries Maximum number of retry attempts on network errors or 5xx responses (default: 3)
 * @property retryDelayMs Delay between retry attempts in milliseconds (default: 100ms)
 * @property debug Set of debug categories to enable (default: empty, no debug logging)
 * @property debugFunction Function to write debug messages (default: NO_OP, discards all messages)
 */
data class VtexAdsClientConfig(
    val publisherId: String,
    val sessionIdProvider: () -> String,
    val userIdProvider: (() -> String?)? = null,
    val channel: Channel,
    val brand: String? = null,
    val timeout: Long = Constants.DEFAULT_TIMEOUT_MS,  // Default 500ms
    val maxRetries: Int = Constants.DEFAULT_MAX_RETRIES,    // Default 3 retries
    val retryDelayMs: Long = Constants.DEFAULT_RETRY_DELAY_MS,  // Default 100ms between retries
    val debug: Set<VtexAdsDebug> = emptySet(),  // Default no debug
    val debugFunction: DebugFunction = DebugFunctions.NO_OP  // Default no-op
) {
    init {
        require(publisherId.isNotBlank()) { "Publisher ID cannot be blank" }
        require(timeout > 0) { "Timeout must be positive" }
        require(timeout <= Constants.MAX_TIMEOUT_MS) { "Timeout cannot exceed ${Constants.MAX_TIMEOUT_MS}ms" }
        require(maxRetries >= 0) { "Max retries cannot be negative" }
        require(retryDelayMs >= 0) { "Retry delay cannot be negative" }
    }

    /**
     * Gets the current session ID by calling the provider function.
     * @return The current session ID
     * @throws IllegalStateException if the session ID is blank
     */
    fun getSessionId(): String {
        val sessionId = sessionIdProvider()
        require(sessionId.isNotBlank()) { "Session ID cannot be blank" }
        return sessionId
    }

    /**
     * Gets the current user ID by calling the provider function.
     * @return The current user ID, or null if no provider is set
     */
    fun getUserId(): String? = userIdProvider?.invoke()

    companion object {
        // Constants moved to Constants.kt for better organization
        // Use Constants.ADS_BASE_URL and Constants.EVENTS_BASE_URL instead
    }
}
