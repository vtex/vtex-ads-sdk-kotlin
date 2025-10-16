package com.vtex.ads.sdk.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Represents a VTEX Ads campaign.
 */
@JsonClass(generateAdapter = true)
data class Campaign(
    @Json(name = "id")
    val id: String,

    @Json(name = "name")
    val name: String,

    @Json(name = "status")
    val status: CampaignStatus,

    @Json(name = "budget")
    val budget: Budget? = null,

    @Json(name = "startDate")
    val startDate: String? = null,

    @Json(name = "endDate")
    val endDate: String? = null,

    @Json(name = "createdAt")
    val createdAt: String,

    @Json(name = "updatedAt")
    val updatedAt: String
)

/**
 * Campaign status enum.
 */
enum class CampaignStatus {
    @Json(name = "active")
    ACTIVE,

    @Json(name = "paused")
    PAUSED,

    @Json(name = "archived")
    ARCHIVED,

    @Json(name = "draft")
    DRAFT
}

/**
 * Represents campaign budget information.
 */
@JsonClass(generateAdapter = true)
data class Budget(
    @Json(name = "amount")
    val amount: Double,

    @Json(name = "currency")
    val currency: String = "USD",

    @Json(name = "type")
    val type: BudgetType = BudgetType.DAILY
)

/**
 * Budget type enum.
 */
enum class BudgetType {
    @Json(name = "daily")
    DAILY,

    @Json(name = "lifetime")
    LIFETIME
}
