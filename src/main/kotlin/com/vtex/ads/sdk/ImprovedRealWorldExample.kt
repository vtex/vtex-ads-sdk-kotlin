@file:OptIn(kotlin.time.ExperimentalTime::class)

package com.vtex.ads.sdk

import com.vtex.ads.sdk.models.*
import kotlinx.coroutines.runBlocking

/**
 * Example demonstrating the improved API with specialized builders.
 * This example shows both the old generic approach and the new specialized builders.
 */
fun main() = runBlocking {
    val publisherId = "d4dff0cb-1f21-4a96-9acf-d9426a5ed08c"
    
    // Example functions that return current session and user IDs
    fun getCurrentSessionId(): String = "example-session-${System.currentTimeMillis()}"
    fun getCurrentUserId(): String? = "example-user-id"

    val config = VtexAdsClientConfig(
        publisherId = publisherId,
        channel = Channel.SITE,
        sessionIdProvider = { getCurrentSessionId() },
        userIdProvider = { getCurrentUserId() },
        timeout = 500L
    )

    val client = VtexAdsClient(config)

    // Banner Placements - Type-safe and validated
    val bannerImageDesktop = BannerPlacementRequest.builder()
        .quantity(5)
        .image("desktop")  // Clearly image banner
        .build()

    val bannerVideo720p = BannerPlacementRequest.builder()
        .quantity(3)
        .video("720p", VideoSize.P720)  // Size required and validated
        .build()

    val bannerMixed = BannerPlacementRequest.builder()
        .quantity(10)
        .imageAndVideo("desktop", VideoSize.P1080)  // Both types, clear intent
        .build()

    // Product Placements - Clear and validated
    val products = ProductPlacementRequest.builder()
        .quantity(10)
        .uniqueSkus()  // Clear intent: no duplicates
        .build()

    val productsWithDuplicates = ProductPlacementRequest.builder()
        .quantity(15)
        .allowSkuDuplications(true)  // Explicit about duplicates
        .build()

    // Sponsored Brand Placements - Asset type validation
    val sponsoredBrandImage = SponsoredBrandPlacementRequest.builder()
        .quantity(5)
        .withImageAssets()  // Clear: image assets only
        .build()

    val sponsoredBrandVideo = SponsoredBrandPlacementRequest.builder()
        .quantity(3)
        .withVideoAssets(VideoSize.P320)  // Video with resolution
        .build()

    // ============================================================
    // FACTORY METHODS: Even more concise
    // ============================================================
//    println("=== FACTORY METHODS (Most Concise) ===")
//
//    val quickBannerImage = BannerPlacementRequest.image(5, "desktop")
//    val quickBannerVideo = BannerPlacementRequest.video(3, "720p", VideoSize.P720)
//
//    val quickProducts = ProductPlacementRequest.unique(10)
//    val quickProductsWithDups = ProductPlacementRequest.create(15, allowSkuDuplications = true)
//
//    val quickSponsoredImage = SponsoredBrandPlacementRequest.image(5)
//    val quickSponsoredVideo = SponsoredBrandPlacementRequest.video(3, VideoSize.P720)
//    val quickSponsoredMixed = SponsoredBrandPlacementRequest.imageAndVideo(8, VideoSize.P1080)

    // ============================================================
    // USAGE: All placement types work seamlessly together
    // ============================================================
    println("=== FETCHING ADS ===")

    val ads = client.ads.getHomeAds(
        placements = mapOf(
            // Mix of old and new styles (backward compatible)
            "banner_desktop" to bannerImageDesktop,
            "banner_video" to bannerVideo720p,
            "products_unique" to products,
            "sponsored_brands_image" to sponsoredBrandImage,
            "sponsored_brands_video" to sponsoredBrandVideo,

            // Using factory methods
            "quick_banner" to BannerPlacementRequest.image(3, "mobile"),
            "quick_products" to ProductPlacementRequest.unique(5),
            "quick_sponsored" to SponsoredBrandPlacementRequest.imageAndVideo(4)
        )
    )

    // Display results
    println("\n=== RESULTS ===")
    ads.placements.forEach { (name, adList) ->
        println("$name: ${adList.size} ads")
        adList.forEach { ad ->
            when (ad) {
                is Ad.BannerAd -> println("  - Banner: ${ad.mediaUrl}")
                is Ad.ProductAd -> println("  - Product: ${ad.productSku}")
                is Ad.SponsoredBrandAd -> println("  - Sponsored Brand: ${ad.mediaUrl} with ${ad.products.size} products")
                is Ad.DigitalSignageAd -> println("  - Digital Signage: ${ad.mediaUrl}")
            }
        }
    }

    // ============================================================
    // EXAMPLES OF VALIDATION ERRORS (commented out)
    // ============================================================

    // This would throw IllegalArgumentException: Size is required for video banner ads
    // val invalidBanner = BannerPlacementRequest.builder()
    //     .quantity(5)
    //     .video("", VideoSize.P720)  // Empty size!
    //     .build()

    // This would throw IllegalArgumentException: Quantity must be positive
    // val invalidQuantity = ProductPlacementRequest.builder()
    //     .quantity(0)  // Zero quantity!
    //     .build()
}
