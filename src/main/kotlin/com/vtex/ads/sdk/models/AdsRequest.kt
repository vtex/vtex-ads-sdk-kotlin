package com.vtex.ads.sdk.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Request body for querying ads.
 */
@JsonClass(generateAdapter = true)
data class AdsRequest(
    @Json(name = "session_id")
    val sessionId: String,

    @Json(name = "user_id")
    val userId: String? = null,

    @Json(name = "channel")
    val channel: Channel,

    @Json(name = "context")
    val context: Context,

    @Json(name = "placements")
    val placements: Map<String, PlacementRequest>,

    @Json(name = "term")
    val term: String? = null,

    @Json(name = "category_name")
    val categoryName: String? = null,

    @Json(name = "product_sku")
    val productSku: String? = null,

    @Json(name = "brand_name")
    val brandName: String? = null,

    @Json(name = "brand")
    val brand: String? = null,

    @Json(name = "store_id")
    val storeId: String? = null,

    @Json(name = "device_id")
    val deviceId: String? = null,

    @Json(name = "store_name")
    val storeName: String? = null,

    @Json(name = "userAgent")
    val userAgent: String? = null,

    @Json(name = "segmentation")
    val segmentation: List<Segmentation>? = null,

    @Json(name = "tags")
    val tags: List<String>? = null,

    @Json(name = "dedup_campaign_ads")
    val dedupCampaignAds: Boolean = false,

    @Json(name = "dedup_ads")
    val dedupAds: Boolean = false
)

/**
 * Segmentation data for targeting ads.
 */
@JsonClass(generateAdapter = true)
data class Segmentation(
    @Json(name = "key")
    val key: String,

    @Json(name = "values")
    val values: List<String>
)
