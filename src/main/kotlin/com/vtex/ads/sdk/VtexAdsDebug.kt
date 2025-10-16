package com.vtex.ads.sdk

/**
 * Debug categories for the VTEX Ads SDK.
 * 
 * These categories control which debug messages are logged by the SDK.
 * Use EVENTS_ALL to enable all event-related logging, or use specific
 * categories for granular control.
 */
enum class VtexAdsDebug {
    /**
     * Enables all event-related debug messages.
     * This includes impression, view, click, and conversion events.
     */
    EVENTS_ALL,
    
    /**
     * Logs impression events (when ads are displayed).
     */
    EVENTS_IMPRESSION,
    
    /**
     * Logs view events (when ads are actually viewed by users).
     */
    EVENTS_VIEW,
    
    /**
     * Logs click events (when users click on ads).
     */
    EVENTS_CLICK,
    
    /**
     * Logs conversion events (when orders are completed).
     */
    EVENTS_CONVERSION,
    
    /**
     * Logs ads loading events (success and error cases).
     */
    ADS_LOAD
}
