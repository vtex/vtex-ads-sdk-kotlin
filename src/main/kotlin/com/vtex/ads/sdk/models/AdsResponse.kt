package com.vtex.ads.sdk.models

/**
 * Response from the ads query API.
 * Contains a map of placement names to lists of ads.
 */
data class AdsResponse(
    val placements: Map<String, List<Ad>>
) {
    /**
     * Gets ads for a specific placement by name.
     */
    fun getPlacement(placementName: String): List<Ad> {
        return placements[placementName] ?: emptyList()
    }

    /**
     * Gets all ads across all placements.
     */
    fun getAllAds(): List<Ad> {
        return placements.values.flatten()
    }
}
