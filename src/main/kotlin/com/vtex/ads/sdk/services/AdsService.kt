package com.vtex.ads.sdk.services

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.vtex.ads.sdk.VtexAdsClientConfig
import com.vtex.ads.sdk.VtexLogger
import com.vtex.ads.sdk.VtexAdsDebug
import com.vtex.ads.sdk.exceptions.VtexAdsException
import com.vtex.ads.sdk.http.AdJsonAdapterFactory
import com.vtex.ads.sdk.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

/**
 * Service for querying ads from VTEX Ads API.
 *
 * @property config Client configuration
 * @property logger Internal logger for debug messages
 */
class AdsService(
    private val config: VtexAdsClientConfig,
    private val logger: VtexLogger
) {

    private val currentUserId = AtomicReference(config.getUserId())

    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(config.timeout, TimeUnit.MILLISECONDS)
        .readTimeout(config.timeout, TimeUnit.MILLISECONDS)
        .writeTimeout(config.timeout, TimeUnit.MILLISECONDS)
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Content-Type", "application/json")
                .addHeader("Connection", "keep-alive")
                .build()
            chain.proceed(request)
        }
        .build()

    private val moshi = Moshi.Builder()
        .add(AdJsonAdapterFactory())
        .add(KotlinJsonAdapterFactory())
        .build()

    private val adsRequestAdapter = moshi.adapter(AdsRequest::class.java)

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
     * Gets ads for the home page.
     *
     * @param placements Map of placement names to placement configurations
     * @param segmentation Optional segmentation data for targeting
     * @param tags Optional tags for contextualizing searches
     * @param dedupCampaignAds Deduplicate by campaign
     * @param dedupAds Deduplicate across placements
     * @return Response containing ads for each placement
     */
    suspend fun getHomeAds(
        placements: Map<String, PlacementRequest>,
        segmentation: List<Segmentation>? = null,
        tags: List<String>? = null,
        dedupCampaignAds: Boolean = false,
        dedupAds: Boolean = false
    ): AdsResponse = withContext(Dispatchers.IO) {
        val request = AdsRequest(
            sessionId = config.getSessionId(),
            userId = getCurrentUserId(),
            channel = config.channel,
            context = Context.HOME,
            placements = placements,
            brand = config.brand,
            segmentation = segmentation,
            tags = tags,
            dedupCampaignAds = dedupCampaignAds,
            dedupAds = dedupAds
        )

        println(request)

        executeAdsRequest(request)
    }

    /**
     * Gets ads for search results.
     *
     * @param term Search term
     * @param placements Map of placement names to placement configurations
     * @param segmentation Optional segmentation data for targeting
     * @param tags Optional tags for contextualizing searches
     * @param dedupCampaignAds Deduplicate by campaign
     * @param dedupAds Deduplicate across placements
     * @return Response containing ads for each placement
     */
    suspend fun getSearchAds(
        term: String,
        placements: Map<String, PlacementRequest>,
        segmentation: List<Segmentation>? = null,
        tags: List<String>? = null,
        dedupCampaignAds: Boolean = false,
        dedupAds: Boolean = false
    ): AdsResponse = withContext(Dispatchers.IO) {
        val request = AdsRequest(
            sessionId = config.getSessionId(),
            userId = getCurrentUserId(),
            channel = config.channel,
            context = Context.SEARCH,
            term = term,
            placements = placements,
            brand = config.brand,
            segmentation = segmentation,
            tags = tags,
            dedupCampaignAds = dedupCampaignAds,
            dedupAds = dedupAds
        )
        executeAdsRequest(request)
    }

    /**
     * Gets ads for a category page.
     *
     * @param categoryName Full category breadcrumb (e.g., "Electronics > Smartphones")
     * @param placements Map of placement names to placement configurations
     * @param segmentation Optional segmentation data for targeting
     * @param tags Optional tags for contextualizing searches
     * @param dedupCampaignAds Deduplicate by campaign
     * @param dedupAds Deduplicate across placements
     * @return Response containing ads for each placement
     */
    suspend fun getCategoryAds(
        categoryName: String,
        placements: Map<String, PlacementRequest>,
        segmentation: List<Segmentation>? = null,
        tags: List<String>? = null,
        dedupCampaignAds: Boolean = false,
        dedupAds: Boolean = false
    ): AdsResponse = withContext(Dispatchers.IO) {
        val request = AdsRequest(
            sessionId = config.getSessionId(),
            userId = getCurrentUserId(),
            channel = config.channel,
            context = Context.CATEGORY,
            categoryName = categoryName,
            placements = placements,
            brand = config.brand,
            segmentation = segmentation,
            tags = tags,
            dedupCampaignAds = dedupCampaignAds,
            dedupAds = dedupAds
        )
        executeAdsRequest(request)
    }

    /**
     * Gets ads for a product page.
     *
     * @param productSku SKU of the product being viewed
     * @param placements Map of placement names to placement configurations
     * @param segmentation Optional segmentation data for targeting
     * @param tags Optional tags for contextualizing searches
     * @param dedupCampaignAds Deduplicate by campaign
     * @param dedupAds Deduplicate across placements
     * @return Response containing ads for each placement
     */
    suspend fun getProductPageAds(
        productSku: String,
        placements: Map<String, PlacementRequest>,
        segmentation: List<Segmentation>? = null,
        tags: List<String>? = null,
        dedupCampaignAds: Boolean = false,
        dedupAds: Boolean = false
    ): AdsResponse = withContext(Dispatchers.IO) {
        val request = AdsRequest(
            sessionId = config.getSessionId(),
            userId = getCurrentUserId(),
            channel = config.channel,
            context = Context.PRODUCT_PAGE,
            productSku = productSku,
            placements = placements,
            brand = config.brand,
            segmentation = segmentation,
            tags = tags,
            dedupCampaignAds = dedupCampaignAds,
            dedupAds = dedupAds
        )
        executeAdsRequest(request)
    }

    /**
     * Executes an ads request to the API.
     */
    private fun executeAdsRequest(adsRequest: AdsRequest): AdsResponse {
        val url = "$BASE_URL/v1/rma/${config.publisherId}"
        val json = adsRequestAdapter.toJson(adsRequest)
        val body = json.toRequestBody(JSON_MEDIA_TYPE)
        val requestId = generateRequestId()

        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        val startTime = System.currentTimeMillis()

        try {
            client.newCall(request).execute().use { response ->
                val latencyMs = System.currentTimeMillis() - startTime
                
                if (!response.isSuccessful) {
                    logger.log(VtexAdsDebug.ADS_LOAD, "VtexAds/AdsLoad") {
                        "ads_load error requestId=$requestId status=${response.code} latencyMs=$latencyMs context=${adsRequest.context} channel=${adsRequest.channel} placements=${adsRequest.placements.size} userId=${adsRequest.userId} sessionId=${adsRequest.sessionId.take(12)} cause=VtexAdsException: Failed to get ads: ${response.code} - ${response.message}"
                    }
                    throw VtexAdsException("Failed to get ads: ${response.code} - ${response.message}")
                }

                val responseBody = response.body?.string()
                    ?: throw VtexAdsException("Empty response body")

                println(responseBody)

                val adsResponse = parseAdsResponse(responseBody)
                val totalAds = adsResponse.getAllAds().size
                
                logger.log(VtexAdsDebug.ADS_LOAD, "VtexAds/AdsLoad") {
                    val adsByType = adsResponse.getAllAds().groupBy { it.type }.mapValues { it.value.size }
                    val placementNames = adsResponse.placements.keys.joinToString(",")
                    val segmentationKeys = adsRequest.segmentation?.map { it.key }?.joinToString(",") ?: "none"
                    val tagsCount = adsRequest.tags?.size ?: 0
                    
                    "ads_load success requestId=$requestId status=${response.code} latencyMs=$latencyMs count=$totalAds context=${adsRequest.context} channel=${adsRequest.channel} placements=${adsRequest.placements.size} userId=${adsRequest.userId} sessionId=${adsRequest.sessionId.take(12)} types=$adsByType returnedPlacements=$placementNames segmentation=$segmentationKeys tagsCount=$tagsCount dedupCampaign=${adsRequest.dedupCampaignAds} dedupAds=${adsRequest.dedupAds} responseSize=${responseBody.length}"
                }
                
                return adsResponse
            }
        } catch (e: IOException) {
            val latencyMs = System.currentTimeMillis() - startTime
            logger.log(VtexAdsDebug.ADS_LOAD, "VtexAds/AdsLoad") {
                "ads_load error requestId=$requestId status=0 latencyMs=$latencyMs context=${adsRequest.context} channel=${adsRequest.channel} placements=${adsRequest.placements.size} userId=${adsRequest.userId} sessionId=${adsRequest.sessionId.take(12)} cause=${e.javaClass.simpleName}: ${e.message?.take(120)}"
            }
            throw VtexAdsException("Network error while fetching ads: ${e.message}", e)
        } catch (e: VtexAdsException) {
            val latencyMs = System.currentTimeMillis() - startTime
            logger.log(VtexAdsDebug.ADS_LOAD, "VtexAds/AdsLoad") {
                "ads_load error requestId=$requestId status=parse_error latencyMs=$latencyMs context=${adsRequest.context} channel=${adsRequest.channel} placements=${adsRequest.placements.size} userId=${adsRequest.userId} sessionId=${adsRequest.sessionId.take(12)} cause=${e.javaClass.simpleName}: ${e.message?.take(120)}"
            }
            throw e
        }
    }

    /**
     * Parses the ads response JSON into AdsResponse object.
     */
    private fun parseAdsResponse(json: String): AdsResponse {
        val raw = moshi.adapter<Map<String, Any>>(Types.newParameterizedType(Map::class.java, String::class.java, Any::class.java))
            .fromJson(json)
            ?: throw VtexAdsException("Failed to parse ads response")

        val adListType = Types.newParameterizedType(List::class.java, Ad::class.java)
        val listAdapter = moshi.adapter<List<Ad>>(adListType)

        val placements = mutableMapOf<String, List<Ad>>()

        for ((key, value) in raw) {
            if (value is List<*>) {
                // Converte apenas os campos que realmente são listas de anúncios
                val adsJson = moshi.adapter(Any::class.java).toJson(value)
                val ads = listAdapter.fromJson(adsJson) ?: emptyList()
                placements[key] = ads
            }
        }

        return AdsResponse(placements)
    }


    /**
     * Closes the service and releases resources.
     */
    fun close() {
        client.dispatcher.executorService.shutdown()
        client.connectionPool.evictAll()
    }

    /**
     * Helper function to generate a unique request ID for logging purposes.
     */
    private fun generateRequestId(): String {
        return "req_${System.currentTimeMillis()}_${(Math.random() * 1000).toInt()}"
    }

    companion object {
        private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
        private const val BASE_URL = "https://newtail-media.newtail.com.br"
    }
}
