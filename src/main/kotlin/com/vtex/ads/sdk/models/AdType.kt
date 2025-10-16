package com.vtex.ads.sdk.models

import com.squareup.moshi.Json

/**
 * Type of ad.
 */
enum class AdType {
    @Json(name = "product")
    PRODUCT,

    @Json(name = "banner")
    BANNER,

    @Json(name = "sponsored_brand")
    SPONSORED_BRAND,

    @Json(name = "digital_signage")
    DIGITAL_SIGNAGE
}
