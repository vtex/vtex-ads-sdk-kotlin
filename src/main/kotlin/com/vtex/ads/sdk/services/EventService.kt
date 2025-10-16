package com.vtex.ads.sdk.services

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.vtex.ads.sdk.VtexAdsClientConfig
import com.vtex.ads.sdk.VtexLogger
import com.vtex.ads.sdk.VtexAdsDebug
import com.vtex.ads.sdk.models.*
import com.vtex.ads.sdk.utils.HashUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

/**
 * Service for sending ad events asynchronously (non-blocking).
 *
 * Events are sent in a fire-and-forget manner to avoid blocking the UI.
 *
 * @property config Client configuration
 * @property logger Internal logger for debug messages
 */
class EventService(
    private val config: VtexAdsClientConfig,
    private val logger: VtexLogger
) {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val currentUserId = AtomicReference(config.getUserId())

    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(1000, TimeUnit.MILLISECONDS)
        .readTimeout(1000, TimeUnit.MILLISECONDS)
        .writeTimeout(1000, TimeUnit.MILLISECONDS)
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Content-Type", "application/json")
                .addHeader("Connection", "keep-alive")
                .build()
            chain.proceed(request)
        }
        .build()

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val eventRequestAdapter = moshi.adapter(EventRequest::class.java)
    private val conversionRequestAdapter = moshi.adapter(ConversionRequest::class.java)

    /**
     * Sends a delivery event (impression, view, or click) in a non-blocking manner.
     * This is a unified method for sending all types of ad interaction events.
     *
     * Use this method to send:
     * - Impression events: when an ad is loaded and visible in the viewport
     * - View events: when an ad is actually viewed by the user
     * - Click events: when a user clicks on an ad
     *
     * @param eventUrl The event URL from the ad response (impressionUrl, viewUrl, or clickUrl)
     * @param placement Optional placement name for logging context
     * @param onComplete Optional callback with success/failure status
     */
    fun deliveryBeaconEvent(
        eventUrl: String,
        placement: String? = null,
        onComplete: ((Boolean) -> Unit)? = null
    ) {
        // Determine event type from URL for logging
        val eventType = when {
            eventUrl.contains("impression") -> VtexAdsDebug.EVENTS_IMPRESSION
            eventUrl.contains("view") -> VtexAdsDebug.EVENTS_VIEW
            eventUrl.contains("click") -> VtexAdsDebug.EVENTS_CLICK
            else -> VtexAdsDebug.EVENTS_IMPRESSION // Default fallback
        }
        
        val placementInfo = placement?.let { "placement=$it" } ?: ""
        
        logger.log(eventType, "VtexAds/Events") {
            // Only process URL parsing when debug is enabled
            val urlParams = extractUrlParams(eventUrl)
            val action = when (eventType) {
                VtexAdsDebug.EVENTS_IMPRESSION -> "impression"
                VtexAdsDebug.EVENTS_VIEW -> "view"
                VtexAdsDebug.EVENTS_CLICK -> "click"
                else -> "delivery_beacon_event"
            }
            "$action success $placementInfo $urlParams".trim()
        }
        
        scope.launch {
            val success = sendEvent(eventUrl)
            
            if (!success) {
                logger.log(eventType, "VtexAds/Events") {
                    // Only process URL parsing when debug is enabled
                    val urlParams = extractUrlParams(eventUrl)
                    val action = when (eventType) {
                        VtexAdsDebug.EVENTS_IMPRESSION -> "impression"
                        VtexAdsDebug.EVENTS_VIEW -> "view"
                        VtexAdsDebug.EVENTS_CLICK -> "click"
                        else -> "delivery_beacon_event"
                    }
                    "$action error $placementInfo $urlParams reason=network_error".trim()
                }
            }
            
            onComplete?.invoke(success)
        }
    }

    /**
     * Sends a conversion event (non-blocking).
     * This is called when a purchase is completed.
     *
     * @param conversionRequest The conversion data
     * @param onComplete Optional callback with success/failure status
     */
    fun sendConversion(
        conversionRequest: ConversionRequest,
        onComplete: ((Boolean) -> Unit)? = null
    ) {
        logger.log(VtexAdsDebug.EVENTS_CONVERSION, "VtexAds/Events") {
            "conversion success orderId=${conversionRequest.orderId} userId=${conversionRequest.userId} items=${conversionRequest.items.size}"
        }
        
        scope.launch {
            val success = sendConversionEvent(conversionRequest)
            
            if (!success) {
                logger.log(VtexAdsDebug.EVENTS_CONVERSION, "VtexAds/Events") {
                    "conversion error orderId=${conversionRequest.orderId} userId=${conversionRequest.userId} reason=network_error"
                }
            }
            
            onComplete?.invoke(success)
        }
    }

    /**
     * Updates the current user ID.
     * This is useful when a user logs in after the client was created.
     *
     * @param newUserId The new user ID (can be null for anonymous users)
     */
    fun updateUserId(newUserId: String?) {
        if (newUserId != null) {
            require(newUserId.isNotBlank()) { "User ID cannot be blank" }
        }
        currentUserId.set(newUserId)
    }

    /**
     * Gets the current user ID.
     */
    fun getCurrentUserId(): String? = currentUserId.get()

    /**
     * Sends an order conversion event (non-blocking).
     * This is a simplified method that automatically hashes customer data
     * and uses configuration from the client.
     *
     * @param order The order data
     * @param userId Optional user ID override (uses current user ID if not provided)
     * @param onComplete Optional callback with success/failure status
     */
    fun deliveryOrderEvent(
        order: Order,
        userId: String? = getCurrentUserId(),
        onComplete: ((Boolean) -> Unit)? = null
    ) {
        scope.launch {
            val conversionRequest = ConversionRequest(
                publisherId = config.publisherId,
                userId = userId ?: throw IllegalArgumentException("User ID is required for order conversion"),
                sessionId = config.getSessionId(),
                orderId = order.orderId,
                createdAt = order.createdAt,
                channel = config.channel,
                brand = config.brand,
                items = order.items.map { item ->
                    ConversionItem(
                        productSku = item.productSku,
                        quantity = item.quantity,
                        price = item.price,
                        sellerId = item.sellerId
                    )
                },
                emailHashed = HashUtils.sha256(order.customerEmail),
                phoneHashed = HashUtils.sha256OrNull(order.customerPhone),
                socialIdHashed = HashUtils.sha256OrNull(order.customerDocument),
                firstNameHashed = HashUtils.sha256OrNull(order.customerFirstName),
                lastNameHashed = HashUtils.sha256OrNull(order.customerLastName),
                gender = order.gender,
                uf = order.state,
                city = order.city,
                isCompany = order.isCompany
            )

            val success = sendConversionEvent(conversionRequest)
            onComplete?.invoke(success)
        }
    }

    /**
     * Internal method to send impression/view/click events.
     */
    private fun sendEvent(eventUrl: String): Boolean {
        return try {
            val eventRequest = EventRequest(
                userId = getCurrentUserId(),
                sessionId = config.getSessionId()
            )

            val json = eventRequestAdapter.toJson(eventRequest)
            val body = json.toRequestBody(JSON_MEDIA_TYPE)

            val request = Request.Builder()
                .url(eventUrl)
                .post(body)
                .build()

            client.newCall(request).execute().use { response ->
                response.code == 202 // HTTP 202 Accepted
            }
        } catch (e: Exception) {
            // Log error but don't throw - events should not break the app
            println("Error sending event: ${e.message}")
            false
        }
    }

    /**
     * Internal method to send conversion events.
     */
    private fun sendConversionEvent(conversionRequest: ConversionRequest): Boolean {
        return try {
            val url = "${VtexAdsClientConfig.DEFAULT_EVENTS_BASE_URL}/v1/beacon/conversion"
            val json = conversionRequestAdapter.toJson(conversionRequest)
            val body = json.toRequestBody(JSON_MEDIA_TYPE)

            val request = Request.Builder()
                .url(url)
                .post(body)
                .build()

            client.newCall(request).execute().use { response ->
                response.isSuccessful
            }
        } catch (e: Exception) {
            println("Error sending conversion event: ${e.message}")
            false
        }
    }

    /**
     * Closes the service and releases resources.
     */
    fun close() {
        client.dispatcher.executorService.shutdown()
        client.connectionPool.evictAll()
    }

    /**
     * Extracts useful parameters from event URL for logging purposes.
     * Returns a formatted string with available parameters.
     */
    private fun extractUrlParams(url: String): String {
        val params = mutableListOf<String>()
        
        try {
            val uri = java.net.URI(url)
            val query = uri.query ?: return ""
            
            val paramMap = query.split("&").associate { param ->
                val (key, value) = param.split("=", limit = 2)
                key to value
            }
            
            // Extract useful parameters in order of importance
            paramMap["request_id"]?.let { params += "requestId=$it" }
            paramMap["campaign_id"]?.let { params += "campaignId=$it" }
            paramMap["ad_type"]?.let { params += "adType=$it" }
            paramMap["pname"]?.let { params += "pname=$it" }
            paramMap["context"]?.let { params += "context=$it" }
            paramMap["channel"]?.let { params += "channel=$it" }
            paramMap["ad_size"]?.let { params += "adSize=$it" }
            paramMap["requested_at"]?.let { params += "requestedAt=$it" }
            
        } catch (e: Exception) {
            // If URL parsing fails, return empty string
            return ""
        }
        
        return params.joinToString(" ")
    }

    companion object {
        private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
    }
}
