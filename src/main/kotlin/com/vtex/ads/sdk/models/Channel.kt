package com.vtex.ads.sdk.models

import com.squareup.moshi.Json

/**
 * Channel of access for ad requests.
 */
enum class Channel {
    @Json(name = "site")
    SITE,

    @Json(name = "msite")
    MSITE,

    @Json(name = "app")
    APP
}
