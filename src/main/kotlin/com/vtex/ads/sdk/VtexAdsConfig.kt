package com.vtex.ads.sdk

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Configuration for the VTEX Ads SDK client.
 *
 * @property apiKey Your VTEX Ads API key for authentication
 * @property accountName VTEX account name
 * @property baseUrl Base URL for the VTEX Ads API (defaults to production)
 * @property timeout Request timeout duration
 * @property maxRetries Maximum number of retries for failed requests
 * @property debug Enable debug logging for troubleshooting
 */
data class VtexAdsConfig(
    val apiKey: String,
    val accountName: String,
    val baseUrl: String = DEFAULT_BASE_URL,
    val timeout: Duration = 30.seconds,
    val maxRetries: Int = 3,
    val debug: Boolean = false
) {
    init {
        require(apiKey.isNotBlank()) { "API key cannot be blank" }
        require(accountName.isNotBlank()) { "Account name cannot be blank" }
        require(baseUrl.isNotBlank()) { "Base URL cannot be blank" }
        require(timeout.isPositive()) { "Timeout must be positive" }
        require(maxRetries >= 0) { "Max retries cannot be negative" }
    }

    companion object {
        const val DEFAULT_BASE_URL = "https://api.vtex.com/ads"
    }
}
