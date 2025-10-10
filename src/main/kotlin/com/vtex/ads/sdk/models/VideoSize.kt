package com.vtex.ads.sdk.models

/**
 * Standard video sizes for ad requests.
 */
enum class VideoSize(val value: String) {
    P1080("1080p"),  // 1920x1080 - Full screen only
    P720("720p"),    // 1280x720 - Full screen only
    P480("480p"),    // 854x480
    P360("360p"),    // 640x360
    P320("320p");    // 568x320 - Mobile recommended

    override fun toString(): String = value
}
