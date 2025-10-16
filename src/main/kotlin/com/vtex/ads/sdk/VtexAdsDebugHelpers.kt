package com.vtex.ads.sdk

/**
 * Helper function to create a set of debug categories.
 * 
 * This provides a convenient way to specify debug categories
 * when creating a VtexAdsClient.
 * 
 * Example:
 * ```
 * val client = VtexAdsClient(
 *     publisherId = "pub-123",
 *     sessionIdProvider = { getSessionId() },
 *     channel = Channel.SITE,
 *     debug = debugOf(VtexAdsDebug.EVENTS_ALL, VtexAdsDebug.ADS_LOAD),
 *     debugFunction = { label, message -> Log.d(label, message) }
 * )
 * ```
 * 
 * @param items Debug categories to enable
 * @return Set of enabled debug categories
 */
fun debugOf(vararg items: VtexAdsDebug): Set<VtexAdsDebug> = items.toSet()
