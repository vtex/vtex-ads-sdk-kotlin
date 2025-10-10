package com.vtex.ads.sdk.models

import com.squareup.moshi.Json

/**
 * Type of media asset for ads.
 */
enum class AssetType {
    @Json(name = "image")
    IMAGE,

    @Json(name = "video")
    VIDEO
}
