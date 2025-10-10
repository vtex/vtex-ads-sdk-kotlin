package com.vtex.ads.sdk.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Request for a specific placement (ad space).
 *
 * @property quantity Number of ads desired
 * @property types Allowed ad types (product, banner, sponsored_brand, etc.)
 * @property size Expected size (for banners: "desktop", "mobile", etc.; for videos: "720p", "1080p", etc.)
 * @property assetsType Media types accepted (image, video). Default: ["image"]
 * @property allowSkuDuplications Allow ads with the same SKU in the same placement. Default: false
 */
@JsonClass(generateAdapter = true)
data class PlacementRequest(
    @Json(name = "quantity")
    val quantity: Int,

    @Json(name = "types")
    val types: List<AdType>,

    @Json(name = "size")
    val size: String? = null,

    @Json(name = "assets_type")
    val assetsType: List<AssetType>? = null,

    @Json(name = "allow_sku_duplications")
    val allowSkuDuplications: Boolean = false
) {
    init {
        require(quantity > 0) { "Quantity must be greater than 0" }
        require(types.isNotEmpty()) { "At least one ad type must be specified" }
    }

    /**
     * Builder for creating PlacementRequest instances.
     */
    class Builder {
        private var quantity: Int = 1
        private var types: List<AdType> = emptyList()
        private var size: String? = null
        private var assetsType: List<AssetType>? = null
        private var allowSkuDuplications: Boolean = false

        fun quantity(quantity: Int) = apply { this.quantity = quantity }
        fun types(vararg types: AdType) = apply { this.types = types.toList() }
        fun types(types: List<AdType>) = apply { this.types = types }
        fun size(size: String) = apply { this.size = size }
        fun videoSize(videoSize: VideoSize) = apply { this.size = videoSize.value }
        fun assetsType(vararg assetsType: AssetType) = apply { this.assetsType = assetsType.toList() }
        fun assetsType(assetsType: List<AssetType>) = apply { this.assetsType = assetsType }
        fun allowSkuDuplications(allow: Boolean) = apply { this.allowSkuDuplications = allow }

        fun build() = PlacementRequest(
            quantity = quantity,
            types = types,
            size = size,
            assetsType = assetsType,
            allowSkuDuplications = allowSkuDuplications
        )
    }

    companion object {
        fun builder() = Builder()
    }
}
