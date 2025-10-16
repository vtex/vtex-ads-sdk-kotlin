package com.vtex.ads.sdk.models

import com.squareup.moshi.Json

/**
 * Context where ads will be displayed.
 */
enum class Context {
    @Json(name = "home")
    HOME,

    @Json(name = "search")
    SEARCH,

    @Json(name = "category")
    CATEGORY,

    @Json(name = "product_page")
    PRODUCT_PAGE,

    @Json(name = "brand_page")
    BRAND_PAGE,

    @Json(name = "digital_signage")
    DIGITAL_SIGNAGE
}
