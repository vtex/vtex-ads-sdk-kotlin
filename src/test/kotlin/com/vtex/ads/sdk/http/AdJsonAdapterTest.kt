package com.vtex.ads.sdk.http

import com.squareup.moshi.Moshi
import com.vtex.ads.sdk.models.Ad
import com.vtex.ads.sdk.models.AdType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class AdJsonAdapterTest {

    private val moshi = Moshi.Builder()
        .add(AdJsonAdapterFactory())
        .build()

    private val adapter = moshi.adapter(Ad::class.java)

    @Test
    fun `should parse product ad`() {
        val json = """
            {
                "ad_id": "ad-123",
                "type": "product",
                "click_url": "http://click.url",
                "impression_url": "http://impression.url",
                "view_url": "http://view.url",
                "seller_id": "seller-1",
                "product_sku": "SKU-123"
            }
        """.trimIndent()

        val ad = adapter.fromJson(json)
        assertNotNull(ad)
        assertIs<Ad.ProductAd>(ad)
        assertEquals("ad-123", ad.adId)
        assertEquals(AdType.PRODUCT, ad.type)
        assertEquals("http://click.url", ad.clickUrl)
        assertEquals("seller-1", ad.sellerId)
        assertEquals("SKU-123", ad.productSku)
    }

    @Test
    fun `should parse banner ad`() {
        val json = """
            {
                "ad_id": "ad-456",
                "type": "banner",
                "click_url": "http://click.url",
                "impression_url": "http://impression.url",
                "view_url": "http://view.url",
                "seller_id": null,
                "media_url": "http://media.url/banner.jpg"
            }
        """.trimIndent()

        val ad = adapter.fromJson(json)
        assertNotNull(ad)
        assertIs<Ad.BannerAd>(ad)
        assertEquals("ad-456", ad.adId)
        assertEquals(AdType.BANNER, ad.type)
        assertEquals("http://media.url/banner.jpg", ad.mediaUrl)
        assertEquals(null, ad.sellerId)
    }

    @Test
    fun `should parse sponsored brand ad with products`() {
        val json = """
            {
                "ad_id": "ad-789",
                "type": "sponsored_brand",
                "click_url": "http://click.url",
                "impression_url": "http://impression.url",
                "view_url": "http://view.url",
                "media_url": "http://media.url/brand.jpg",
                "products": [
                    {
                        "product_sku": "SKU-1",
                        "media_url": "http://product1.jpg"
                    },
                    {
                        "product_sku": "SKU-2",
                        "media_url": "http://product2.jpg"
                    }
                ]
            }
        """.trimIndent()

        val ad = adapter.fromJson(json)
        assertNotNull(ad)
        assertIs<Ad.SponsoredBrandAd>(ad)
        assertEquals("ad-789", ad.adId)
        assertEquals(AdType.SPONSORED_BRAND, ad.type)
        assertEquals("http://media.url/brand.jpg", ad.mediaUrl)
        assertEquals(2, ad.products.size)
        assertEquals("SKU-1", ad.products[0].productSku)
        assertEquals("http://product1.jpg", ad.products[0].mediaUrl)
    }

    @Test
    fun `should parse digital signage ad`() {
        val json = """
            {
                "ad_id": "ad-999",
                "type": "digital_signage",
                "click_url": "http://click.url",
                "impression_url": "http://impression.url",
                "view_url": "http://view.url",
                "media_url": "http://media.url/signage.mp4",
                "duration": 30
            }
        """.trimIndent()

        val ad = adapter.fromJson(json)
        assertNotNull(ad)
        assertIs<Ad.DigitalSignageAd>(ad)
        assertEquals("ad-999", ad.adId)
        assertEquals(AdType.DIGITAL_SIGNAGE, ad.type)
        assertEquals("http://media.url/signage.mp4", ad.mediaUrl)
        assertEquals(30, ad.duration)
    }
}
