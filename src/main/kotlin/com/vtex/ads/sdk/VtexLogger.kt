package com.vtex.ads.sdk

/**
 * Logger for the VTEX Ads SDK.
 * 
 * This logger respects the debug configuration and only evaluates
 * message lambdas when the corresponding debug category is enabled.
 */
class VtexLogger(
    private val enabled: Set<VtexAdsDebug>,
    private val writer: DebugFunction
) {
    
    /**
     * Logs a message if the debug category is enabled.
     * 
     * The message lambda is only evaluated if logging is enabled,
     * ensuring no performance impact when debug is disabled.
     * 
     * @param kind The debug category
     * @param label The log label (e.g., "VtexAds/Events")
     * @param message Lambda that returns the log message (lazy evaluation)
     */
    fun log(kind: VtexAdsDebug, label: String, message: () -> String) {
        if (!isEnabled(kind)) return
        
        try {
            writer(label, message())
        } catch (e: Exception) {
            // Never propagate exceptions from debug logging
            // This ensures debug logging never breaks the app
        }
    }
    
    /**
     * Determines if a debug category is enabled.
     * 
     * Rules:
     * - ADS_LOAD is enabled only if ADS_LOAD is in the enabled set
     * - EVENTS_* categories are enabled if either:
     *   - EVENTS_ALL is in the enabled set, OR
     *   - The specific EVENTS_* category is in the enabled set
     */
    private fun isEnabled(kind: VtexAdsDebug): Boolean {
        return when (kind) {
            VtexAdsDebug.ADS_LOAD -> VtexAdsDebug.ADS_LOAD in enabled
            VtexAdsDebug.EVENTS_ALL -> VtexAdsDebug.EVENTS_ALL in enabled
            VtexAdsDebug.EVENTS_IMPRESSION,
            VtexAdsDebug.EVENTS_VIEW,
            VtexAdsDebug.EVENTS_CLICK,
            VtexAdsDebug.EVENTS_CONVERSION -> {
                VtexAdsDebug.EVENTS_ALL in enabled || kind in enabled
            }
        }
    }
}
