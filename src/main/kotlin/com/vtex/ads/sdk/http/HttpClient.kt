package com.vtex.ads.sdk.http

import com.vtex.ads.sdk.VtexAdsConfig
import com.vtex.ads.sdk.exceptions.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * HTTP client wrapper for making API requests to VTEX Ads.
 */
class HttpClient(private val config: VtexAdsConfig) {

    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(config.timeout.inWholeSeconds, TimeUnit.SECONDS)
        .readTimeout(config.timeout.inWholeSeconds, TimeUnit.SECONDS)
        .writeTimeout(config.timeout.inWholeSeconds, TimeUnit.SECONDS)
        .addInterceptor(AuthInterceptor(config.apiKey))
        .addInterceptor(LoggingInterceptor(config.debug))
        .build()

    /**
     * Makes a GET request to the specified endpoint.
     */
    suspend fun get(endpoint: String, queryParams: Map<String, String> = emptyMap()): Response {
        val url = buildUrl(endpoint, queryParams)
        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        return executeRequest(request)
    }

    /**
     * Makes a POST request to the specified endpoint.
     */
    suspend fun post(endpoint: String, body: String): Response {
        val url = buildUrl(endpoint)
        val requestBody = body.toRequestBody(JSON_MEDIA_TYPE)
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        return executeRequest(request)
    }

    /**
     * Makes a PUT request to the specified endpoint.
     */
    suspend fun put(endpoint: String, body: String): Response {
        val url = buildUrl(endpoint)
        val requestBody = body.toRequestBody(JSON_MEDIA_TYPE)
        val request = Request.Builder()
            .url(url)
            .put(requestBody)
            .build()

        return executeRequest(request)
    }

    /**
     * Makes a DELETE request to the specified endpoint.
     */
    suspend fun delete(endpoint: String): Response {
        val url = buildUrl(endpoint)
        val request = Request.Builder()
            .url(url)
            .delete()
            .build()

        return executeRequest(request)
    }

    /**
     * Executes a request with retry logic and error handling.
     */
    private suspend fun executeRequest(request: Request, retryCount: Int = 0): Response {
        try {
            val response = client.newCall(request).execute()

            when (response.code) {
                in 200..299 -> return response
                401 -> throw VtexAdsAuthenticationException("Invalid API key or unauthorized")
                404 -> throw VtexAdsNotFoundException("Resource not found: ${request.url}")
                429 -> {
                    val retryAfter = response.header("Retry-After")?.toLongOrNull()
                    throw VtexAdsRateLimitException(retryAfter = retryAfter)
                }
                in 400..499 -> throw VtexAdsValidationException(
                    "Client error: ${response.code} - ${response.body?.string()}"
                )
                in 500..599 -> {
                    if (retryCount < config.maxRetries) {
                        return executeRequest(request, retryCount + 1)
                    }
                    throw VtexAdsException("Server error: ${response.code}")
                }
                else -> throw VtexAdsException("Unexpected response code: ${response.code}")
            }
        } catch (e: IOException) {
            if (retryCount < config.maxRetries) {
                return executeRequest(request, retryCount + 1)
            }
            throw VtexAdsNetworkException("Network error: ${e.message}", e)
        }
    }

    /**
     * Builds a complete URL with the base URL, endpoint, and query parameters.
     */
    private fun buildUrl(endpoint: String, queryParams: Map<String, String> = emptyMap()): HttpUrl {
        val urlBuilder = HttpUrl.Builder()
            .scheme("https")
            .host(config.baseUrl.removePrefix("https://").removePrefix("http://"))
            .addPathSegments(endpoint.removePrefix("/"))

        queryParams.forEach { (key, value) ->
            urlBuilder.addQueryParameter(key, value)
        }

        return urlBuilder.build()
    }

    /**
     * Closes the HTTP client and releases resources.
     */
    fun close() {
        client.dispatcher.executorService.shutdown()
        client.connectionPool.evictAll()
    }

    companion object {
        private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
    }
}

/**
 * Interceptor for adding authentication headers to requests.
 */
private class AuthInterceptor(private val apiKey: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .build()
        return chain.proceed(request)
    }
}

/**
 * Interceptor for logging requests and responses (debug mode).
 */
private class LoggingInterceptor(private val enabled: Boolean) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        if (enabled) {
            println("Request: ${request.method} ${request.url}")
        }

        val response = chain.proceed(request)

        if (enabled) {
            println("Response: ${response.code} for ${request.url}")
        }

        return response
    }
}
