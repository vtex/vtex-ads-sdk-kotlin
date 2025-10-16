package com.vtex.ads.sdk.http

import com.squareup.moshi.*
import com.vtex.ads.sdk.models.Ad
import com.vtex.ads.sdk.models.AdType
import com.vtex.ads.sdk.models.BrandProduct
import java.lang.reflect.Type

/**
 * Custom JSON adapter factory for the Ad sealed class.
 * Parses different ad types based on the "type" field.
 */
class AdJsonAdapterFactory : JsonAdapter.Factory {
    override fun create(type: Type, annotations: MutableSet<out Annotation>, moshi: Moshi): JsonAdapter<*>? {
        if (type != Ad::class.java) {
            return null
        }

        return AdJsonAdapter()
    }

    private class AdJsonAdapter : JsonAdapter<Ad>() {

        private val options: JsonReader.Options = JsonReader.Options.of(
            "ad_id", "type", "click_url", "impression_url", "view_url", "seller_id",
            "product_sku", "media_url", "products", "duration", "assets"
        )

        override fun fromJson(reader: JsonReader): Ad? {
            var adId: String? = null
            var type: String? = null
            var clickUrl: String? = null
            var impressionUrl: String? = null
            var viewUrl: String? = null
            var sellerId: String? = null
            var productSku: String? = null
            var mediaUrl: String? = null
            var products: List<BrandProduct>? = null
            var duration: Int? = null

            reader.beginObject()
            while (reader.hasNext()) {
                when (reader.selectName(options)) {
                    0 -> adId = reader.nextString()
                    1 -> type = reader.nextString()
                    2 -> clickUrl = reader.nextString()
                    3 -> impressionUrl = reader.nextString()
                    4 -> viewUrl = reader.nextString()
                    5 -> sellerId = if (reader.peek() == JsonReader.Token.NULL) {
                        reader.nextNull()
                    } else {
                        reader.nextString()
                    }
                    6 -> productSku = reader.nextString()
                    7 -> mediaUrl = reader.nextString()
                    8 -> products = parseBrandProducts(reader)
                    9 -> duration = reader.nextInt()
                    10 -> {
                        // Parse assets array and extract media_url from first asset
                        if (mediaUrl == null) {
                            mediaUrl = parseAssetsForMediaUrl(reader)
                        } else {
                            reader.skipValue()
                        }
                    }
                    else -> reader.skipValue()
                }
            }
            reader.endObject()

            requireNotNull(adId) { "ad_id is required" }
            requireNotNull(type) { "type is required" }
            requireNotNull(clickUrl) { "click_url is required" }
            requireNotNull(impressionUrl) { "impression_url is required" }
            requireNotNull(viewUrl) { "view_url is required" }

            val adType = AdType.valueOf(type.uppercase())

            return when (adType) {
                AdType.PRODUCT -> {
                    requireNotNull(productSku) { "product_sku is required for product ads" }
                    Ad.ProductAd(adId, adType, clickUrl, impressionUrl, viewUrl, sellerId, productSku)
                }
                AdType.BANNER -> {
                    requireNotNull(mediaUrl) { "media_url is required for banner ads" }
                    Ad.BannerAd(adId, adType, clickUrl, impressionUrl, viewUrl, sellerId, mediaUrl)
                }
                AdType.SPONSORED_BRAND -> {
                    requireNotNull(mediaUrl) { "media_url is required for sponsored brand ads" }
                    Ad.SponsoredBrandAd(adId, adType, clickUrl, impressionUrl, viewUrl, sellerId, mediaUrl, products ?: emptyList())
                }
                AdType.DIGITAL_SIGNAGE -> {
                    requireNotNull(mediaUrl) { "media_url is required for digital signage ads" }
                    requireNotNull(duration) { "duration is required for digital signage ads" }
                    Ad.DigitalSignageAd(adId, adType, clickUrl, impressionUrl, viewUrl, sellerId, mediaUrl, duration)
                }
            }
        }

        private fun parseAssetsForMediaUrl(reader: JsonReader): String? {
            var mediaUrl: String? = null
            reader.beginArray()
            while (reader.hasNext()) {
                reader.beginObject()
                while (reader.hasNext()) {
                    when (reader.nextName()) {
                        "url" -> {
                            // Take the first URL we find
                            if (mediaUrl == null) {
                                mediaUrl = reader.nextString()
                            } else {
                                reader.skipValue()
                            }
                        }
                        else -> reader.skipValue()
                    }
                }
                reader.endObject()
            }
            reader.endArray()
            return mediaUrl
        }

        private fun parseBrandProducts(reader: JsonReader): List<BrandProduct> {
            val products = mutableListOf<BrandProduct>()
            reader.beginArray()
            while (reader.hasNext()) {
                var productSku: String? = null
                var mediaUrl: String? = null

                reader.beginObject()
                while (reader.hasNext()) {
                    when (reader.nextName()) {
                        "product_sku" -> productSku = reader.nextString()
                        "media_url" -> mediaUrl = if (reader.peek() == JsonReader.Token.NULL) {
                            reader.nextNull()
                        } else {
                            reader.nextString()
                        }
                        else -> reader.skipValue()
                    }
                }
                reader.endObject()

                requireNotNull(productSku) { "product_sku is required for brand products" }
                products.add(BrandProduct(productSku, mediaUrl))
            }
            reader.endArray()
            return products
        }

        override fun toJson(writer: JsonWriter, value: Ad?) {
            throw UnsupportedOperationException("Serializing Ad is not supported")
        }
    }
}
