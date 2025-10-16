package com.vtex.ads.sdk.models

/**
 * Specialized builder for Banner ad placements.
 * Ensures type safety and validation for banner-specific requirements.
 *
 * This is a wrapper around PlacementRequest that provides a type-safe API
 * specifically for banner ads.
 */
object BannerPlacementRequest {

    class Builder {
        private var quantity: Int = 1
        private var size: String? = null
        private var assetsType: List<AssetType>? = null
        private var videoSize: VideoSize? = null

        /**
         * Sets the number of banner ads to request.
         * @param quantity Number of ads (must be positive)
         */
        fun quantity(quantity: Int) = apply {
            require(quantity > 0) { "Quantity must be positive" }
            this.quantity = quantity
        }

        /**
         * Requests image banner ads.
         * @param size Optional size specification (e.g., "desktop", "mobile")
         */
        fun image(size: String? = null) = apply {
            this.assetsType = listOf(AssetType.IMAGE)
            this.size = size
        }

        /**
         * Requests video banner ads.
         * @param size Video size specification (required for video banners)
         * @param videoSize Optional video resolution filter
         */
        fun video(size: String, videoSize: VideoSize? = null) = apply {
            this.assetsType = listOf(AssetType.VIDEO)
            this.size = if (videoSize != null) videoSize.value else size
            this.videoSize = videoSize
        }

        /**
         * Requests both image and video banner ads.
         * @param size Size specification
         * @param videoSize Optional video resolution filter
         */
        fun imageAndVideo(size: String? = null, videoSize: VideoSize? = null) = apply {
            this.assetsType = listOf(AssetType.IMAGE, AssetType.VIDEO)
            this.size = if (videoSize != null) videoSize.value else size
            this.videoSize = videoSize
        }

        /**
         * Sets a custom size for the banner placement.
         * @param size Size specification
         */
        fun size(size: String) = apply {
            this.size = size
        }

        fun build(): PlacementRequest {
            // Default to IMAGE if no asset type specified
            val finalAssetType = assetsType ?: listOf(AssetType.IMAGE)

            // Validate video requirements
            val sizeValue = size
            if (finalAssetType.contains(AssetType.VIDEO)) {
                if (sizeValue == null || sizeValue.isBlank()) {
                    throw IllegalArgumentException("Size is required for video banner ads")
                }
            }

            return PlacementRequest(
                quantity = quantity,
                types = listOf(AdType.BANNER),
                size = sizeValue,
                assetsType = finalAssetType,
                allowSkuDuplications = false
            )
        }
    }

    /**
     * Creates a new builder for banner placements.
     */
    @JvmStatic
    fun builder(): Builder = Builder()

    /**
     * Quick factory method for image banners.
     */
    @JvmStatic
    fun image(quantity: Int, size: String? = null): PlacementRequest {
        return builder()
            .quantity(quantity)
            .image(size)
            .build()
    }

    /**
     * Quick factory method for video banners.
     */
    @JvmStatic
    fun video(quantity: Int, size: String, videoSize: VideoSize? = null): PlacementRequest {
        return builder()
            .quantity(quantity)
            .video(size, videoSize)
            .build()
    }
}
