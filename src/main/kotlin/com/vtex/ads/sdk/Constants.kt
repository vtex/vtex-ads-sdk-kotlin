package com.vtex.ads.sdk

/**
 * Centralized constants for the VTEX Ads SDK.
 * 
 * This file contains all static values used across the SDK, making it easier
 * to maintain and update configuration values. In the future, these values
 * can be easily replaced with BuildConfig or properties-based configuration.
 */
object Constants {
    
    // ============================================================================
    // API URLs
    // ============================================================================
    
    /**
     * Base URL for ads API requests.
     * Used for fetching ads data.
     */
    const val ADS_BASE_URL = "https://newtail-media.newtail.com.br"
    
    /**
     * Base URL for events API requests.
     * Used for sending impression, view, click, and conversion events.
     */
    const val EVENTS_BASE_URL = "https://newtail-media.newtail.com.br"
    
    // ============================================================================
    // SDK Configuration
    // ============================================================================
    
    /**
     * SDK version.
     */
    const val SDK_VERSION = "0.1.0-SNAPSHOT"
    
    /**
     * Maximum allowed timeout for API requests (10 seconds).
     */
    const val MAX_TIMEOUT_MS = 10000L
    
    /**
     * Default timeout for API requests (500ms).
     */
    const val DEFAULT_TIMEOUT_MS = 500L
    
    /**
     * Default maximum number of retry attempts.
     */
    const val DEFAULT_MAX_RETRIES = 3
    
    /**
     * Default delay between retry attempts (100ms).
     */
    const val DEFAULT_RETRY_DELAY_MS = 100L
    
    // ============================================================================
    // HTTP Configuration
    // ============================================================================
    
    /**
     * JSON media type for HTTP requests.
     */
    const val JSON_MEDIA_TYPE = "application/json; charset=utf-8"
    
    /**
     * Expected HTTP status code for successful event delivery (202 Accepted).
     */
    const val EVENT_SUCCESS_STATUS_CODE = 202
    
    // ============================================================================
    // Debug Configuration
    // ============================================================================
    
    /**
     * Default debug timeout for logging operations (600ms).
     */
    const val DEFAULT_DEBUG_TIMEOUT_MS = 600L
}
