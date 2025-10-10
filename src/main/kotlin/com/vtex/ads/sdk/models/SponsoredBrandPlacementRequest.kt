package com.vtex.ads.sdk.models

/**
 * Specialized builder for Sponsored Brand ad placements.
 * Ensures type safety and validation for sponsored brand-specific requirements.
 *
 * This is a wrapper around PlacementRequest that provides a type-safe API
 * specifically for sponsored brand ads.
 */
object SponsoredBrandPlacementRequest {

    class Builder {
        private var quantity: Int = 1
        private var assetsType: MutableList<AssetType> = mutableListOf()
        private var videoSize: VideoSize? = null

        /**
         * Sets the number of sponsored brand ads to request.
         * @param quantity Number of ads (must be positive)
         */
        fun quantity(quantity: Int) = apply {
            require(quantity > 0) { "Quantity must be positive" }
            this.quantity = quantity
        }

        /**
         * Requests sponsored brand ads with image assets.
         */
        fun withImageAssets() = apply {
            if (!assetsType.contains(AssetType.IMAGE)) {
                assetsType.add(AssetType.IMAGE)
            }
        }

        /**
         * Requests sponsored brand ads with video assets.
         * @param videoSize Optional video resolution filter
         */
        fun withVideoAssets(videoSize: VideoSize? = null) = apply {
            if (!assetsType.contains(AssetType.VIDEO)) {
                assetsType.add(AssetType.VIDEO)
            }
            this.videoSize = videoSize
        }

        /**
         * Requests sponsored brand ads with both image and video assets.
         * @param videoSize Optional video resolution filter for video assets
         */
        fun withImageAndVideoAssets(videoSize: VideoSize? = null) = apply {
            withImageAssets()
            withVideoAssets(videoSize)
        }

        /**
         * Sets specific asset types for the sponsored brand ads.
         * @param types One or more asset types
         */
        fun assetTypes(vararg types: AssetType) = apply {
            assetsType.clear()
            assetsType.addAll(types)
        }

        /**
         * Sets the video resolution filter.
         * @param size Video size filter
         */
        fun videoSize(size: VideoSize) = apply {
            this.videoSize = size
        }

        fun build(): PlacementRequest {
            // Default to IMAGE if no asset type specified
            val finalAssetType = if (assetsType.isEmpty()) {
                listOf(AssetType.IMAGE)
            } else {
                assetsType.toList()
            }

            return PlacementRequest(
                quantity = quantity,
                types = listOf(AdType.SPONSORED_BRAND),
                size = videoSize?.value,
                assetsType = finalAssetType,
                allowSkuDuplications = false
            )
        }
    }

    /**
     * Creates a new builder for sponsored brand placements.
     */
    @JvmStatic
    fun builder(): Builder = Builder()

    /**
     * Quick factory method for image-based sponsored brands.
     */
    @JvmStatic
    fun image(quantity: Int): PlacementRequest {
        return builder()
            .quantity(quantity)
            .withImageAssets()
            .build()
    }

    /**
     * Quick factory method for video-based sponsored brands.
     */
    @JvmStatic
    fun video(quantity: Int, videoSize: VideoSize? = null): PlacementRequest {
        return builder()
            .quantity(quantity)
            .withVideoAssets(videoSize)
            .build()
    }

    /**
     * Quick factory method for sponsored brands with both image and video.
     */
    @JvmStatic
    fun imageAndVideo(quantity: Int, videoSize: VideoSize? = null): PlacementRequest {
        return builder()
            .quantity(quantity)
            .withImageAndVideoAssets(videoSize)
            .build()
    }
}
