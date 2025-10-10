package com.vtex.ads.sdk.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Request body for sending ad events (impression, view, click).
 */
@JsonClass(generateAdapter = true)
data class EventRequest(
    @Json(name = "user_id")
    val userId: String? = null,

    @Json(name = "session_id")
    val sessionId: String
)

/**
 * Request body for sending conversion events.
 */
@JsonClass(generateAdapter = true)
data class ConversionRequest(
    @Json(name = "publisher_id")
    val publisherId: String,

    @Json(name = "user_id")
    val userId: String,

    @Json(name = "session_id")
    val sessionId: String,

    @Json(name = "order_id")
    val orderId: String,

    @Json(name = "created_at")
    val createdAt: String,

    @Json(name = "items")
    val items: List<ConversionItem>,

    @Json(name = "channel")
    val channel: Channel,

    @Json(name = "brand")
    val brand: String? = null,

    @Json(name = "gender")
    val gender: String? = null,

    @Json(name = "uf")
    val uf: String? = null,

    @Json(name = "city")
    val city: String? = null,

    @Json(name = "is_company")
    val isCompany: Boolean? = null,

    @Json(name = "email_hashed")
    val emailHashed: String,

    @Json(name = "phone_hashed")
    val phoneHashed: String? = null,

    @Json(name = "social_id_hashed")
    val socialIdHashed: String? = null,

    @Json(name = "first_name_hashed")
    val firstNameHashed: String? = null,

    @Json(name = "last_name_hashed")
    val lastNameHashed: String? = null
)

/**
 * Item in a conversion event.
 */
@JsonClass(generateAdapter = true)
data class ConversionItem(
    @Json(name = "product_sku")
    val productSku: String,

    @Json(name = "quantity")
    val quantity: Int,

    @Json(name = "price")
    val price: Double,

    @Json(name = "seller_id")
    val sellerId: String? = null
)
