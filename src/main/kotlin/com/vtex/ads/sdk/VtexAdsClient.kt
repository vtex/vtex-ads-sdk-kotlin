package com.vtex.ads.sdk

import com.vtex.ads.sdk.http.HttpClient
import java.io.Closeable

/**
 * Main client for interacting with the VTEX Ads API.
 *
 * This client provides access to all VTEX Ads API endpoints through
 * dedicated service objects.
 *
 * Example usage:
 * ```
 * val config = VtexAdsConfig(
 *     apiKey = "your-api-key",
 *     accountName = "your-account"
 * )
 * val client = VtexAdsClient(config)
 *
 * // Use the client
 * val campaigns = client.campaigns.list()
 *
 * // Don't forget to close when done
 * client.close()
 * ```
 *
 * @property config Configuration for the SDK
 */
class VtexAdsClient(
    val config: VtexAdsConfig
) : Closeable {

    internal val httpClient = HttpClient(config)

    // API service endpoints will be added here
    // val campaigns = CampaignsService(httpClient)
    // val adGroups = AdGroupsService(httpClient)
    // val ads = AdsService(httpClient)
    // etc.

    /**
     * Closes the client and releases all resources.
     * Should be called when the client is no longer needed.
     */
    override fun close() {
        httpClient.close()
    }

    companion object {
        /**
         * SDK version
         */
        const val VERSION = "0.1.0-SNAPSHOT"
    }
}
