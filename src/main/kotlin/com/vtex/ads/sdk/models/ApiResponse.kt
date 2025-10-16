package com.vtex.ads.sdk.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Generic API response wrapper.
 */
@JsonClass(generateAdapter = true)
data class ApiResponse<T>(
    @Json(name = "data")
    val data: T,

    @Json(name = "success")
    val success: Boolean = true,

    @Json(name = "message")
    val message: String? = null
)

/**
 * Paginated API response.
 */
@JsonClass(generateAdapter = true)
data class PaginatedResponse<T>(
    @Json(name = "data")
    val data: List<T>,

    @Json(name = "pagination")
    val pagination: Pagination,

    @Json(name = "success")
    val success: Boolean = true
)

/**
 * Pagination metadata.
 */
@JsonClass(generateAdapter = true)
data class Pagination(
    @Json(name = "page")
    val page: Int,

    @Json(name = "pageSize")
    val pageSize: Int,

    @Json(name = "totalPages")
    val totalPages: Int,

    @Json(name = "totalItems")
    val totalItems: Int
)
