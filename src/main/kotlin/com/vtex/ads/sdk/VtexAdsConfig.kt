package com.vtex.ads.sdk

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Configuration for the VTEX Ads SDK client.
 *
 * @property publisherId Your VTEX Ads publisher ID for identification
 * @property channel The channel through which ads are being requested (e.g., web, mobile)
 * @property baseUrl Base URL for the VTEX Ads API (defaults to production)
 * @property timeout Request timeout duration
 * @property maxRetries Maximum number of retries for failed requests
 * @property debug Enable debug logging for troubleshooting
 */
data class VtexAdsConfig(
    val publisherId: String,
    val channel: Channel,
    val baseUrl: String = DEFAULT_BASE_URL,
    val timeout: Duration = 30.seconds,
    val maxRetries: Int = 3,
    val debug: Boolean = false
) {
    init {
        require(publisherId.isNotBlank()) { "Publisher ID cannot be blank" }
        require(baseUrl.isNotBlank()) { "Base URL cannot be blank" }
        require(timeout.isPositive()) { "Timeout must be positive" }
        require(maxRetries >= 0) { "Max retries cannot be negative" }
    }

    companion object {
        const val DEFAULT_BASE_URL = "https://newtail-media.newtail.com.br"
    }
}

/**
 * Represents the channel through which ads are being requested.
 */
enum class Channel {
    /** Web browser channel */
    WEB,

    /** Mobile application channel */
    MOBILE,

    /** Desktop application channel */
    DESKTOP,

    /** API/Server-to-server channel */
    API,

    /** Other unspecified channel */
    OTHER
}
