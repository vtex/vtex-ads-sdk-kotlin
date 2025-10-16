package com.vtex.ads.sdk

/**
 * Function type for debug logging.
 * 
 * @param label The log label/category (e.g., "VtexAds/Events")
 * @param message The log message
 */
typealias DebugFunction = (label: String, message: String) -> Unit

/**
 * Predefined debug functions for common logging scenarios.
 */
object DebugFunctions {
    /**
     * No-op debug function that discards all log messages.
     * This is the default when debug is not enabled.
     */
    val NO_OP: DebugFunction = { _, _ -> }
}
