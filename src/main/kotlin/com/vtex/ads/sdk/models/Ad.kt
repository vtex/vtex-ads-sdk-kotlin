package com.vtex.ads.sdk.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Base class for all ad types.
 */
sealed class Ad {
    abstract val adId: String
    abstract val type: AdType
    abstract val clickUrl: String
    abstract val impressionUrl: String
    abstract val viewUrl: String
    abstract val sellerId: String?

    /**
     * Product ad containing a product SKU.
     */
    @JsonClass(generateAdapter = true)
    data class ProductAd(
        @Json(name = "ad_id")
        override val adId: String,

        @Json(name = "type")
        override val type: AdType,

        @Json(name = "click_url")
        override val clickUrl: String,

        @Json(name = "impression_url")
        override val impressionUrl: String,

        @Json(name = "view_url")
        override val viewUrl: String,

        @Json(name = "seller_id")
        override val sellerId: String? = null,

        @Json(name = "product_sku")
        val productSku: String
    ) : Ad()

    /**
     * Banner ad with media URL (image or video).
     */
    @JsonClass(generateAdapter = true)
    data class BannerAd(
        @Json(name = "ad_id")
        override val adId: String,

        @Json(name = "type")
        override val type: AdType,

        @Json(name = "click_url")
        override val clickUrl: String,

        @Json(name = "impression_url")
        override val impressionUrl: String,

        @Json(name = "view_url")
        override val viewUrl: String,

        @Json(name = "seller_id")
        override val sellerId: String? = null,

        @Json(name = "media_url")
        val mediaUrl: String
    ) : Ad()

    /**
     * Sponsored brand ad with media URL and associated products.
     */
    @JsonClass(generateAdapter = true)
    data class SponsoredBrandAd(
        @Json(name = "ad_id")
        override val adId: String,

        @Json(name = "type")
        override val type: AdType,

        @Json(name = "click_url")
        override val clickUrl: String,

        @Json(name = "impression_url")
        override val impressionUrl: String,

        @Json(name = "view_url")
        override val viewUrl: String,

        @Json(name = "seller_id")
        override val sellerId: String? = null,

        @Json(name = "media_url")
        val mediaUrl: String,

        @Json(name = "products")
        val products: List<BrandProduct> = emptyList()
    ) : Ad()

    /**
     * Digital signage ad with media URL and duration.
     */
    @JsonClass(generateAdapter = true)
    data class DigitalSignageAd(
        @Json(name = "ad_id")
        override val adId: String,

        @Json(name = "type")
        override val type: AdType,

        @Json(name = "click_url")
        override val clickUrl: String,

        @Json(name = "impression_url")
        override val impressionUrl: String,

        @Json(name = "view_url")
        override val viewUrl: String,

        @Json(name = "seller_id")
        override val sellerId: String? = null,

        @Json(name = "media_url")
        val mediaUrl: String,

        @Json(name = "duration")
        val duration: Int
    ) : Ad()
}

/**
 * Product associated with a sponsored brand ad.
 */
@JsonClass(generateAdapter = true)
data class BrandProduct(
    @Json(name = "product_sku")
    val productSku: String,

    @Json(name = "media_url")
    val mediaUrl: String? = null
)
