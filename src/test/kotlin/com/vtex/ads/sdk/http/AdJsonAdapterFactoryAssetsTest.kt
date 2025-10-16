package com.vtex.ads.sdk.http

import com.squareup.moshi.Moshi
import com.vtex.ads.sdk.models.Ad
import com.vtex.ads.sdk.models.AdType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class AdJsonAdapterFactoryAssetsTest {

    private val moshi = Moshi.Builder()
        .add(AdJsonAdapterFactory())
        .build()

    private val adapter = moshi.adapter(Ad::class.java)

    @Test
    fun `should parse sponsored brand ad with assets array`() {
        val json = """
            {
              "ad_id":"c2e6fbf0-09ec-48cd-92d4-6d2ac50ae60d",
              "asset_type":"image",
              "assets":[
                {"dimension":"344x132","type":"image","url":"https://cdn.newtail.com.br/retail_media/ads/2025/05/28/cc4c5dc58e13d8ffaf4eaa829ab1318f.jpeg"},
                {"dimension":"712x176","type":"image","url":"https://cdn.newtail.com.br/retail_media/ads/2025/05/28/f92ad579b38db7c712fde8381535a595.jpeg"}
              ],
              "brand_name":"Test Brand",
              "brand_url":"https://example.com/logo.png",
              "campaign_name":"test campaign",
              "click_url":"https://example.com/click",
              "destination_url":null,
              "impression_url":"https://example.com/impression",
              "position":0,
              "products":[],
              "seller_id":null,
              "type":"sponsored_brand",
              "view_url":"https://example.com/view"
            }
        """.trimIndent()

        val ad = adapter.fromJson(json)

        assertNotNull(ad)
        assertIs<Ad.SponsoredBrandAd>(ad)
        assertEquals("c2e6fbf0-09ec-48cd-92d4-6d2ac50ae60d", ad.adId)
        assertEquals(AdType.SPONSORED_BRAND, ad.type)
        assertEquals("https://cdn.newtail.com.br/retail_media/ads/2025/05/28/cc4c5dc58e13d8ffaf4eaa829ab1318f.jpeg", ad.mediaUrl)
        assertEquals("https://example.com/click", ad.clickUrl)
        assertEquals("https://example.com/impression", ad.impressionUrl)
        assertEquals("https://example.com/view", ad.viewUrl)
    }

    @Test
    fun `should parse banner ad with direct media_url`() {
        val json = """
            {
              "ad_id":"22267c2f-48d0-425b-b6a0-4258c7e5757f",
              "type":"banner",
              "click_url":"https://example.com/click",
              "impression_url":"https://example.com/impression",
              "view_url":"https://example.com/view",
              "seller_id":null,
              "media_url":"https://cdn2.newtail.com.br/retail_media/ads/2023/08/17/afee7ac2b254fe5879f6ec175c04f6b9-1280x256-red.png"
            }
        """.trimIndent()

        val ad = adapter.fromJson(json)

        assertNotNull(ad)
        assertIs<Ad.BannerAd>(ad)
        assertEquals("22267c2f-48d0-425b-b6a0-4258c7e5757f", ad.adId)
        assertEquals(AdType.BANNER, ad.type)
        assertEquals("https://cdn2.newtail.com.br/retail_media/ads/2023/08/17/afee7ac2b254fe5879f6ec175c04f6b9-1280x256-red.png", ad.mediaUrl)
    }

    @Test
    fun `should parse banner ad with assets array`() {
        val json = """
            {
              "ad_id":"9c919c46-c143-4070-bc9d-b535c555c6bf",
              "additional_assets":[
                {"asset_category":"video_background","asset_name":"main_background","asset_url":"https://example.com/bg1.jpeg","dimension":"1400x400","type":"image"}
              ],
              "asset_type":"video",
              "type":"banner",
              "click_url":"https://example.com/click",
              "impression_url":"https://example.com/impression",
              "view_url":"https://example.com/view",
              "seller_id":null,
              "media_url":"https://cdn2.newtail.com.br/retail_media/ads-video/2025/07/29/66eba95faa554aef80f4a6c1edd1c425.mp4"
            }
        """.trimIndent()

        val ad = adapter.fromJson(json)

        assertNotNull(ad)
        assertIs<Ad.BannerAd>(ad)
        assertEquals("9c919c46-c143-4070-bc9d-b535c555c6bf", ad.adId)
        assertEquals("https://cdn2.newtail.com.br/retail_media/ads-video/2025/07/29/66eba95faa554aef80f4a6c1edd1c425.mp4", ad.mediaUrl)
    }

    @Test
    fun `should parse sponsored brand ad with single asset`() {
        val json = """
            {
              "ad_id":"3c34b0a5-11a0-4ef3-896c-2aaa4739992e",
              "asset_type":"image",
              "assets":[
                {"dimension":null,"type":"image","url":"https://cdn.newtail.com.br/retail_media/ads/2024/04/05/CONSUL970x250.jpeg"}
              ],
              "brand_name":"Consul",
              "brand_url":"https://cdn.newtail.com.br/retail_media/ads/2024/04/05/logo-consul.png",
              "campaign_name":"Campanha Consul Brand Categoria",
              "click_url":"https://example.com/click",
              "description":"Os Eletrodomésticos que você ama",
              "destination_url":null,
              "headline":"Bem Pensado",
              "impression_url":"https://example.com/impression",
              "position":0,
              "products":[],
              "seller_id":null,
              "type":"sponsored_brand",
              "view_url":"https://example.com/view"
            }
        """.trimIndent()

        val ad = adapter.fromJson(json)

        assertNotNull(ad)
        assertIs<Ad.SponsoredBrandAd>(ad)
        assertEquals("3c34b0a5-11a0-4ef3-896c-2aaa4739992e", ad.adId)
        assertEquals("https://cdn.newtail.com.br/retail_media/ads/2024/04/05/CONSUL970x250.jpeg", ad.mediaUrl)
    }
}
