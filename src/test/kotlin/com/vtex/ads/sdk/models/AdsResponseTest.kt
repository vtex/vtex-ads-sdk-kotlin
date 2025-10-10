package com.vtex.ads.sdk.models

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AdsResponseTest {

    @Test
    fun `should get placement by name`() {
        val productAd = Ad.ProductAd(
            adId = "ad-1",
            type = AdType.PRODUCT,
            clickUrl = "http://click.url",
            impressionUrl = "http://impression.url",
            viewUrl = "http://view.url",
            sellerId = "seller-1",
            productSku = "SKU-123"
        )

        val response = AdsResponse(
            placements = mapOf(
                "home_top" to listOf(productAd)
            )
        )

        val ads = response.getPlacement("home_top")
        assertEquals(1, ads.size)
        assertEquals(productAd, ads[0])
    }

    @Test
    fun `should return empty list for non-existent placement`() {
        val response = AdsResponse(
            placements = mapOf(
                "home_top" to emptyList()
            )
        )

        val ads = response.getPlacement("non_existent")
        assertTrue(ads.isEmpty())
    }

    @Test
    fun `should get all ads across placements`() {
        val productAd = Ad.ProductAd(
            adId = "ad-1",
            type = AdType.PRODUCT,
            clickUrl = "http://click.url",
            impressionUrl = "http://impression.url",
            viewUrl = "http://view.url",
            sellerId = null,
            productSku = "SKU-123"
        )

        val bannerAd = Ad.BannerAd(
            adId = "ad-2",
            type = AdType.BANNER,
            clickUrl = "http://click.url",
            impressionUrl = "http://impression.url",
            viewUrl = "http://view.url",
            sellerId = null,
            mediaUrl = "http://media.url"
        )

        val response = AdsResponse(
            placements = mapOf(
                "home_top" to listOf(productAd),
                "home_banner" to listOf(bannerAd)
            )
        )

        val allAds = response.getAllAds()
        assertEquals(2, allAds.size)
        assertTrue(allAds.contains(productAd))
        assertTrue(allAds.contains(bannerAd))
    }

    @Test
    fun `should handle multiple ads in single placement`() {
        val ad1 = Ad.ProductAd(
            adId = "ad-1",
            type = AdType.PRODUCT,
            clickUrl = "http://click.url/1",
            impressionUrl = "http://impression.url/1",
            viewUrl = "http://view.url/1",
            sellerId = null,
            productSku = "SKU-1"
        )

        val ad2 = Ad.ProductAd(
            adId = "ad-2",
            type = AdType.PRODUCT,
            clickUrl = "http://click.url/2",
            impressionUrl = "http://impression.url/2",
            viewUrl = "http://view.url/2",
            sellerId = null,
            productSku = "SKU-2"
        )

        val response = AdsResponse(
            placements = mapOf(
                "search_products" to listOf(ad1, ad2)
            )
        )

        val ads = response.getPlacement("search_products")
        assertEquals(2, ads.size)
    }
}
