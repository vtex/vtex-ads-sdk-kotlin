package com.vtex.ads.sdk.exceptions

/**
 * Base exception class for all VTEX Ads SDK exceptions.
 */
open class VtexAdsException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)

/**
 * Exception thrown when authentication fails.
 */
class VtexAdsAuthenticationException(
    message: String = "Authentication failed",
    cause: Throwable? = null
) : VtexAdsException(message, cause)

/**
 * Exception thrown when request validation fails.
 */
class VtexAdsValidationException(
    message: String,
    cause: Throwable? = null
) : VtexAdsException(message, cause)

/**
 * Exception thrown when a network error occurs.
 */
class VtexAdsNetworkException(
    message: String,
    cause: Throwable? = null
) : VtexAdsException(message, cause)

/**
 * Exception thrown when rate limit is exceeded.
 */
class VtexAdsRateLimitException(
    message: String = "Rate limit exceeded",
    val retryAfter: Long? = null,
    cause: Throwable? = null
) : VtexAdsException(message, cause)

/**
 * Exception thrown when a resource is not found.
 */
class VtexAdsNotFoundException(
    message: String = "Resource not found",
    cause: Throwable? = null
) : VtexAdsException(message, cause)
