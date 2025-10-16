# VTEX Ads SDK for Kotlin

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.22-blue.svg)](https://kotlinlang.org)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

A modern, type-safe Kotlin SDK for the VTEX Ads API. Built with Kotlin Coroutines for async operations and designed to work seamlessly with Android, server-side Kotlin, and other JVM platforms.

## 🚀 Features

- ✅ **Type-safe API** - Specialized builders with compile-time validation
- ✅ **Kotlin Coroutines** - Non-blocking async operations
- ✅ **Cross-platform** - Android, JVM backend, and other Kotlin platforms
- ✅ **Smart defaults** - Sensible configurations out of the box
- ✅ **Comprehensive** - Ad queries, event tracking, and order conversion
- ✅ **Well-tested** - 146+ tests with high coverage
- ✅ **Easy to use** - Intuitive API with clear documentation

## 📦 Installation

### Gradle (Kotlin DSL)

```kotlin
dependencies {
    implementation("com.vtex.ads:vtex-ads-sdk-kotlin:0.1.0-SNAPSHOT")
}
```

### Gradle (Groovy)

```groovy
dependencies {
    implementation 'com.vtex.ads:vtex-ads-sdk-kotlin:0.1.0-SNAPSHOT'
}
```

## ⚡ Quick Start

```kotlin
import com.vtex.ads.sdk.*
import com.vtex.ads.sdk.models.*
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    // 1. Configure the SDK with dynamic session and user ID providers
    val client = VtexAdsClient(
        publisherId = "your-publisher-id",
        sessionIdProvider = { getCurrentSessionId() },  // Called on each request
        userIdProvider = { getCurrentUserId() },         // Called on each request
        channel = Channel.SITE
    )

    // 2. Query ads for home page
    val ads = client.ads.getHomeAds(
        placements = mapOf(
            "home_banner" to BannerPlacementRequest.image(5, "desktop"),
            "home_products" to ProductPlacementRequest.unique(10)
        )
    )

    // 3. Send impression events
    ads.getAllAds().forEach { ad ->
        client.events.deliveryBeaconEvent(ad.impressionUrl)
    }

    // 4. Send click event when user clicks
    client.events.deliveryBeaconEvent(ads.getAllAds().first().clickUrl)
}
```

---

## 📖 Table of Contents

- [Installation](#-installation)
- [Quick Start](#-quick-start)
- [Configuration](#-configuration)
- [Ad Types](#-ad-types)
- [Querying Ads](#-querying-ads)
  - [Home Page Ads](#home-page-ads)
  - [Search Page Ads](#search-page-ads)
  - [Category Page Ads](#category-page-ads)
  - [Product Page Ads](#product-page-ads)
- [Placement Builders](#-placement-builders)
  - [Banner Placements](#banner-placements)
  - [Product Placements](#product-placements)
  - [Sponsored Brand Placements](#sponsored-brand-placements)
- [Event Tracking](#-event-tracking)
- [Order Conversion](#-order-conversion)
- [Android Integration](#-android-integration)
- [Best Practices](#-best-practices)
- [API Reference](#-api-reference)

---

## ⚙️ Configuration

### Basic Configuration

```kotlin
val client = VtexAdsClient(
    publisherId = "your-publisher-id",           // Required
    sessionIdProvider = { getCurrentSessionId() }, // Required - called on each request
    userIdProvider = { getCurrentUserId() },         // Optional - called on each request
    channel = Channel.SITE                           // SITE, MSITE, or APP
)
```

### Advanced Configuration

```kotlin
val config = VtexAdsClientConfig(
    publisherId = "your-publisher-id",
    sessionIdProvider = { getCurrentSessionId() },
    userIdProvider = { getCurrentUserId() },
    channel = Channel.SITE,
    brand = "your-brand",                    // For multi-brand publishers
    timeout = 500L,                          // Request timeout in ms (default: 500ms)
    maxRetries = 3,                          // Max retry attempts (default: 3)
    retryDelayMs = 100L                      // Delay between retries (default: 100ms)
)

val client = VtexAdsClient(config)
```

### Backward Compatibility

For applications that need static values, you can use the backward compatibility method:

```kotlin
val client = VtexAdsClient.createWithStaticValues(
    publisherId = "your-publisher-id",
    sessionId = "static-session-id",
    userId = "static-user-id",
    channel = Channel.SITE
)
```

### Dynamic Session and User ID

The SDK now supports dynamic session and user ID providers, which are called on each request. This is useful when these values can change during navigation:

```kotlin
val client = VtexAdsClient(
    publisherId = "pub-123",
    sessionIdProvider = { 
        // This function is called on each request
        // Return the current session ID
        getCurrentSessionId() 
    },
    userIdProvider = { 
        // This function is called on each request
        // Return the current user ID (can be null for anonymous users)
        getCurrentUserId() 
    },
    channel = Channel.SITE
)
```

---

## 🎯 Ad Types

The SDK supports four types of ads:

| Type | Description | Use Case |
|------|-------------|----------|
| **Product Ads** | Individual product placements | Product recommendations, sponsored products |
| **Banner Ads** | Image or video banners | Hero banners, promotional content |
| **Sponsored Brand Ads** | Brand showcase with products | Brand campaigns, category takeovers |
| **Digital Signage Ads** | In-store digital displays | Physical store displays |

---

## 🔍 Querying Ads

### Home Page Ads

Query ads for your home page:

```kotlin
val ads = client.ads.getHomeAds(
    placements = mapOf(
        "home_banner_top" to BannerPlacementRequest.image(1, "desktop"),
        "home_products_shelf" to ProductPlacementRequest.unique(10),
        "home_sponsored_brands" to SponsoredBrandPlacementRequest.imageAndVideo(5)
    )
)

// Access ads by placement
val banners = ads.getPlacement("home_banner_top")
val products = ads.getPlacement("home_products_shelf")
```

### Search Page Ads

Query ads for search results:

```kotlin
val ads = client.ads.getSearchAds(
    term = "smartphone",
    placements = mapOf(
        "search_banner" to BannerPlacementRequest.video(1, "720p", VideoSize.P720),
        "search_products" to ProductPlacementRequest.unique(5)
    )
)
```

### Category Page Ads

Query ads for category pages:

```kotlin
val ads = client.ads.getCategoryAds(
    categoryName = "Electronics > Smartphones",
    placements = mapOf(
        "category_banner" to BannerPlacementRequest.image(1, "desktop"),
        "category_products" to ProductPlacementRequest.unique(8)
    )
)
```

### Product Page Ads

Query ads for product detail pages:

```kotlin
val ads = client.ads.getProductPageAds(
    productSku = "PROD-123",
    placements = mapOf(
        "pdp_related_products" to ProductPlacementRequest.unique(4)
    )
)
```

### Advanced Query Options

All query methods support additional parameters:

```kotlin
val ads = client.ads.getHomeAds(
    placements = placements,

    // Segmentation (targeting)
    segmentation = listOf(
        Segmentation(key = "STATE", values = listOf("SP", "RJ")),
        Segmentation(key = "GENDER", values = listOf("M")),
        Segmentation(key = "AUDIENCES", values = listOf("high_value_customers"))
    ),

    // Tags for filtering
    tags = listOf("electronics", "premium"),

    // Deduplication options
    dedupCampaignAds = true,  // Remove duplicate ads from same campaign
    dedupAds = true            // Remove duplicate ads across campaigns
)
```

---

## 🏗️ Placement Builders

The SDK provides specialized builders for each ad type, offering type safety and validation.

### Banner Placements

**Image Banners:**

```kotlin
// Builder pattern
val banner = BannerPlacementRequest.builder()
    .quantity(5)
    .image("desktop")
    .build()

// Factory method (concise)
val banner = BannerPlacementRequest.image(5, "desktop")
```

**Video Banners:**

```kotlin
// Builder pattern
val banner = BannerPlacementRequest.builder()
    .quantity(3)
    .video("720p", VideoSize.P720)
    .build()

// Factory method
val banner = BannerPlacementRequest.video(3, "720p", VideoSize.P720)
```

**Mixed (Image + Video):**

```kotlin
val banner = BannerPlacementRequest.builder()
    .quantity(10)
    .imageAndVideo("desktop", VideoSize.P1080)
    .build()
```

**Video Size Options:**

```kotlin
VideoSize.P1080  // 1920x1080 - Full screen only
VideoSize.P720   // 1280x720 - Full screen only
VideoSize.P480   // 854x480
VideoSize.P360   // 640x360
VideoSize.P320   // 568x320 - Mobile recommended
```

### Product Placements

**Unique Products (no duplicates):**

```kotlin
// Builder pattern
val products = ProductPlacementRequest.builder()
    .quantity(10)
    .uniqueSkus()  // Each SKU appears at most once
    .build()

// Factory method
val products = ProductPlacementRequest.unique(10)
```

**With Duplicates Allowed:**

```kotlin
// Builder pattern
val products = ProductPlacementRequest.builder()
    .quantity(15)
    .allowSkuDuplications(true)
    .build()

// Factory method
val products = ProductPlacementRequest.create(15, allowSkuDuplications = true)
```

### Sponsored Brand Placements

**Image Assets:**

```kotlin
// Builder pattern
val sponsored = SponsoredBrandPlacementRequest.builder()
    .quantity(5)
    .withImageAssets()
    .build()

// Factory method
val sponsored = SponsoredBrandPlacementRequest.image(5)
```

**Video Assets:**

```kotlin
// Builder pattern
val sponsored = SponsoredBrandPlacementRequest.builder()
    .quantity(3)
    .withVideoAssets(VideoSize.P720)
    .build()

// Factory method
val sponsored = SponsoredBrandPlacementRequest.video(3, VideoSize.P720)
```

**Mixed Assets (Image + Video):**

```kotlin
// Builder pattern
val sponsored = SponsoredBrandPlacementRequest.builder()
    .quantity(8)
    .withImageAndVideoAssets(VideoSize.P1080)
    .build()

// Factory method
val sponsored = SponsoredBrandPlacementRequest.imageAndVideo(8, VideoSize.P1080)
```

### Generic Builder (Backward Compatible)

For advanced use cases, the generic builder is still available:

```kotlin
val placement = PlacementRequest.builder()
    .quantity(5)
    .types(AdType.BANNER)
    .assetsType(AssetType.VIDEO)
    .videoSize(VideoSize.P720)
    .allowSkuDuplications(false)
    .build()
```

---

## 📊 Event Tracking

The SDK provides a unified `deliveryBeaconEvent` method for sending all types of ad interaction events (impressions, views, and clicks).

### Impression Events

Send when an ad is displayed to the user:

```kotlin
// Single ad
client.events.deliveryBeaconEvent(ad.impressionUrl)

// Multiple ads
ads.getAllAds().forEach { ad ->
    client.events.deliveryBeaconEvent(ad.impressionUrl)
}
```

### View Events

Send when an ad is actually viewed (e.g., scrolled into viewport):

```kotlin
client.events.deliveryBeaconEvent(ad.viewUrl)
```

### Click Events

Send when a user clicks on an ad:

```kotlin
client.events.deliveryBeaconEvent(ad.clickUrl)
```

### Event Deduplication

Events are automatically deduplicated by the API:
- **Impressions**: 1 minute
- **Clicks**: 1 hour
- **Conversions**: Not deduplicated (except same order_id within 30 days)

---

## 💰 Order Conversion

Track purchases to measure ad effectiveness.

### Simple Order Tracking

```kotlin
val order = Order.builder()
    .orderId("order-123")
    .addItem("SKU-1", quantity = 2, price = 99.99)
    .addItem("SKU-2", quantity = 1, price = 149.99)
    .customerEmail("customer@example.com")
    .customerPhone("11999999999")
    .customerDocument("12345678900")  // CPF/CNPJ
    .customerFirstName("John")
    .customerLastName("Doe")
    .state("SP")
    .city("São Paulo")
    .gender("M")  // M, F, or O
    .isCompany(false)
    .build()

client.deliveryOrderEvent(order) { success ->
    if (success) {
        println("Order tracked successfully!")
    }
}
```

### Complete Checkout Example

```kotlin
class CheckoutViewModel(
    private val adsClient: VtexAdsClient
) : ViewModel() {

    fun completeOrder(checkoutOrder: CheckoutOrder) {
        viewModelScope.launch {
            try {
                // Convert to SDK order format
                val adsOrder = Order.builder()
                    .orderId(checkoutOrder.id)
                    .createdAt(checkoutOrder.createdAt)
                    .customerEmail(checkoutOrder.customer.email)
                    .customerPhone(checkoutOrder.customer.phone)
                    .customerDocument(checkoutOrder.customer.document)
                    .customerFirstName(checkoutOrder.customer.firstName)
                    .customerLastName(checkoutOrder.customer.lastName)
                    .state(checkoutOrder.shippingAddress.state)
                    .city(checkoutOrder.shippingAddress.city)
                    .gender(checkoutOrder.customer.gender)
                    .isCompany(checkoutOrder.customer.isCompany)
                    .apply {
                        checkoutOrder.items.forEach { item ->
                            addItem(
                                productSku = item.sku,
                                quantity = item.quantity,
                                price = item.unitPrice,  // NOT multiplied by quantity
                                sellerId = item.sellerId
                            )
                        }
                    }
                    .build()

                // Track order conversion (non-blocking)
                adsClient.deliveryOrderEvent(adsOrder) { success ->
                    if (success) {
                        Log.d("Checkout", "Order conversion tracked")
                    } else {
                        Log.w("Checkout", "Failed to track conversion")
                    }
                }

                // Continue with checkout flow
                navigateToSuccess()

            } catch (e: Exception) {
                handleError(e)
            }
        }
    }
}
```

### Important Notes

- **Customer data is automatically hashed** with SHA-256
- **Price should be unit price**, not total (quantity is separate)
- **Fire-and-forget**: Event sending doesn't block the UI
- **Conversion window**: 14 days (orders tracked within this period)

---

## 📱 Android Integration

### ViewModel Example

```kotlin
class ProductListViewModel(
    private val adsClient: VtexAdsClient
) : ViewModel() {

    private val _ads = MutableLiveData<AdsResponse>()
    val ads: LiveData<AdsResponse> = _ads

    fun loadAds(searchTerm: String) {
        viewModelScope.launch {
            try {
                val response = adsClient.ads.getSearchAds(
                    term = searchTerm,
                    placements = mapOf(
                        "search_products" to ProductPlacementRequest.unique(10),
                        "search_banner" to BannerPlacementRequest.image(1, "mobile")
                    )
                )
                _ads.value = response

                // Track impressions
                response.getAllAds().forEach { ad ->
                    adsClient.events.deliveryBeaconEvent(ad.impressionUrl)
                }
            } catch (e: Exception) {
                // Handle error
                Log.e("ProductList", "Failed to load ads", e)
            }
        }
    }

    fun trackAdClick(ad: Ad) {
        adsClient.events.deliveryBeaconEvent(ad.clickUrl)
    }
}
```

### Compose UI Example

```kotlin
@Composable
fun AdBanner(ad: Ad.BannerAd, adsClient: VtexAdsClient) {
    val context = LocalContext.current

    AsyncImage(
        model = ad.mediaUrl,
        contentDescription = "Advertisement",
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable {
                // Track click event
                adsClient.events.deliveryBeaconEvent(ad.clickUrl)

                // Navigate to destination
                ad.destinationUrl?.let { url ->
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                }
            }
    )

    // Track view when ad becomes visible
    LaunchedEffect(ad.adId) {
        adsClient.events.deliveryBeaconEvent(ad.viewUrl)
    }
}
```

### Product Ad Item

```kotlin
@Composable
fun ProductAdItem(ad: Ad.ProductAd, adsClient: VtexAdsClient, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                adsClient.events.deliveryBeaconEvent(ad.clickUrl)
                onClick()
            }
    ) {
        Column {
            // Product image, title, price, etc.
            Text("SKU: ${ad.productSku}")
            Text("Sponsored")  // Ad indicator
        }
    }

    LaunchedEffect(ad.adId) {
        adsClient.events.deliveryBeaconEvent(ad.impressionUrl)
    }
}
```

---

## 💡 Best Practices

### Session ID Management

- **Unique per user**: Generate a unique session ID for each user
- **Persistent**: Should last at least 14 days (conversion window)
- **Ideally never expires**: Use a UUID stored in user preferences
- **Consistent**: Same session ID throughout navigation

```kotlin
// Example: Android SharedPreferences
fun getOrCreateSessionId(context: Context): String {
    val prefs = context.getSharedPreferences("vtex_ads", Context.MODE_PRIVATE)
    var sessionId = prefs.getString("session_id", null)

    if (sessionId == null) {
        sessionId = UUID.randomUUID().toString()
        prefs.edit().putString("session_id", sessionId).apply()
    }

    return sessionId
}
```

### Placement Naming Convention

Use a consistent naming pattern: `{channel}_{context}_{position}_{type}`

```kotlin
val placements = mapOf(
    "site_home_middle_banner" to BannerPlacementRequest.image(1, "desktop"),
    "msite_search_top-shelf_product" to ProductPlacementRequest.unique(5),
    "app_category_bottom-vitrine_sponsored_brand" to SponsoredBrandPlacementRequest.image(3)
)
```

### Error Handling

```kotlin
try {
    val ads = client.ads.getHomeAds(placements)
} catch (e: VtexAdsException) {
    when (e) {
        is VtexAdsAuthenticationException -> {
            // Invalid publisher ID or authentication failed
            Log.e("Ads", "Authentication error: ${e.message}")
        }
        is VtexAdsValidationException -> {
            // Invalid request parameters
            Log.e("Ads", "Validation error: ${e.message}")
        }
        is VtexAdsNetworkException -> {
            // Network connectivity issues
            Log.e("Ads", "Network error: ${e.message}")
        }
        is VtexAdsRateLimitException -> {
            // Rate limit exceeded, retry after e.retryAfter seconds
            Log.w("Ads", "Rate limited, retry after ${e.retryAfter}s")
        }
        else -> {
            // Other errors
            Log.e("Ads", "Unexpected error: ${e.message}")
        }
    }
}
```

### Timeout Configuration

- **Default: 500ms** (recommended for API guidelines)
- **Max: 10 seconds**
- Configure based on your use case:

```kotlin
val config = VtexAdsClientConfig(
    publisherId = "pub-id",
    sessionId = "session-id",
    channel = Channel.SITE,
    timeout = 500L,      // Fast for real-time queries
    maxRetries = 3,      // Retry on network errors
    retryDelayMs = 100L  // Short delay between retries
)
```

### Use Specialized Builders

✅ **Recommended** (Type-safe, validated):

```kotlin
val banner = BannerPlacementRequest.video(3, "720p", VideoSize.P720)
val products = ProductPlacementRequest.unique(10)
val sponsored = SponsoredBrandPlacementRequest.imageAndVideo(5)
```

❌ **Not Recommended** (Generic, error-prone):

```kotlin
val banner = PlacementRequest.builder()
    .quantity(3)
    .types(AdType.BANNER)  // Easy to forget
    .assetsType(AssetType.VIDEO)
    .size("720p")  // No validation
    .build()
```

---

## 🐛 Debugging (Opcional)

O SDK oferece funcionalidades de debug opcionais e retrocompatíveis para ajudar no desenvolvimento e troubleshooting. Por padrão, nenhum log é emitido.

### Configuração de Debug

```kotlin
import com.vtex.ads.sdk.*

// Sem debug (comportamento padrão - igual ao atual)
val client = VtexAdsClient(
    publisherId = "your-publisher-id",
    sessionIdProvider = { getCurrentSessionId() },
    userIdProvider = { getCurrentUserId() },
    channel = Channel.SITE
)

// Com debug habilitado
val client = VtexAdsClient(
    publisherId = "your-publisher-id",
    sessionIdProvider = { getCurrentSessionId() },
    userIdProvider = { getCurrentUserId() },
    channel = Channel.SITE,
    debug = debugOf(VtexAdsDebug.EVENTS_ALL, VtexAdsDebug.ADS_LOAD),
    debugFunction = { label, message -> android.util.Log.d(label, message) }
)
```

### Categorias de Debug Disponíveis

| Categoria | Descrição | Exemplo de Uso |
|-----------|-----------|----------------|
| `EVENTS_ALL` | Todos os eventos de interação com anúncios | Logs de impression, view, click e conversion |
| `EVENTS_IMPRESSION` | Eventos de impressão (quando anúncios são exibidos) | `delivery_beacon_event success adId=123...` |
| `EVENTS_VIEW` | Eventos de visualização (quando anúncios são vistos) | `delivery_beacon_event success adId=123...` |
| `EVENTS_CLICK` | Eventos de clique (quando usuários clicam em anúncios) | `delivery_beacon_event success adId=123...` |
| `EVENTS_CONVERSION` | Eventos de conversão (quando pedidos são completados) | `send_conversion success orderId=456...` |
| `ADS_LOAD` | Carregamento de anúncios (sucesso e erro) | `ads_load success requestId=req_123...` |

### Exemplos de Uso

#### Debug Granular (Apenas Eventos de Visualização)

```kotlin
val client = VtexAdsClient(
    publisherId = "your-publisher-id",
    sessionIdProvider = { getCurrentSessionId() },
    userIdProvider = { getCurrentUserId() },
    channel = Channel.SITE,
    debug = debugOf(VtexAdsDebug.EVENTS_VIEW),
    debugFunction = { label, message -> android.util.Log.d(label, message) }
)
```

#### Debug Completo (Todos os Eventos + Carregamento)

```kotlin
val client = VtexAdsClient(
    publisherId = "your-publisher-id",
    sessionIdProvider = { getCurrentSessionId() },
    userIdProvider = { getCurrentUserId() },
    channel = Channel.SITE,
    debug = debugOf(VtexAdsDebug.EVENTS_ALL, VtexAdsDebug.ADS_LOAD),
    debugFunction = { label, message -> android.util.Log.d(label, message) }
)
```

#### Debug Customizado para Servidor

```kotlin
val client = VtexAdsClient(
    publisherId = "your-publisher-id",
    sessionIdProvider = { getCurrentSessionId() },
    userIdProvider = { getCurrentUserId() },
    channel = Channel.SITE,
    debug = debugOf(VtexAdsDebug.ADS_LOAD),
    debugFunction = { label, message -> 
        logger.info("[$label] $message")
    }
)
```

### Formato das Mensagens de Log

#### Eventos de Anúncios
```
impression success placement=home.hero
view success placement=search.top
click success placement=category.banner
impression error placement=home.hero reason=network_error
conversion success orderId=order-123 userId=user-456 items=3
conversion error orderId=order-123 userId=user-456 reason=network_error
```

#### Carregamento de Anúncios
```
ads_load success requestId=req-123 status=200 latencyMs=150 count=5
ads_load error requestId=req-123 status=500 latencyMs=200 cause=IOException: timeout
ads_load error requestId=req-123 status=parse_error latencyMs=100 cause=VtexAdsException: Failed to parse response
```

### Função Helper

```kotlin
// Criar conjunto de categorias de debug
val debugCategories = debugOf(
    VtexAdsDebug.EVENTS_ALL,
    VtexAdsDebug.ADS_LOAD
)

// Ou usar categorias específicas
val specificDebug = debugOf(VtexAdsDebug.EVENTS_IMPRESSION)
```

### Notas Importantes

- **Retrocompatibilidade**: Sem configuração de debug, nenhum log é emitido (comportamento atual mantido)
- **Performance**: As mensagens de log são avaliadas de forma lazy - se o debug estiver desabilitado, a string da mensagem não é construída
- **Segurança**: Exceções na função de debug nunca quebram a aplicação
- **Flexibilidade**: A função de debug é injetável, permitindo integração com qualquer sistema de logging

## 📚 API Reference

### VtexAdsClient

Main client for interacting with the VTEX Ads API.

```kotlin
class VtexAdsClient(config: VtexAdsClientConfig)

// Properties
val ads: AdsService          // Ad querying service
val events: EventService     // Event tracking service

// Methods
fun updateUserId(userId: String?)
fun getCurrentUserId(): String?
```

### AdsService

Service for querying ads.

```kotlin
interface AdsService {
    suspend fun getHomeAds(
        placements: Map<String, PlacementRequest>,
        segmentation: List<Segmentation>? = null,
        tags: List<String>? = null,
        dedupCampaignAds: Boolean = false,
        dedupAds: Boolean = false
    ): AdsResponse

    suspend fun getSearchAds(
        term: String,
        placements: Map<String, PlacementRequest>,
        segmentation: List<Segmentation>? = null,
        tags: List<String>? = null,
        dedupCampaignAds: Boolean = false,
        dedupAds: Boolean = false
    ): AdsResponse

    suspend fun getCategoryAds(
        categoryName: String,
        placements: Map<String, PlacementRequest>,
        segmentation: List<Segmentation>? = null,
        tags: List<String>? = null,
        dedupCampaignAds: Boolean = false,
        dedupAds: Boolean = false
    ): AdsResponse

    suspend fun getProductPageAds(
        productSku: String,
        placements: Map<String, PlacementRequest>,
        segmentation: List<Segmentation>? = null,
        tags: List<String>? = null,
        dedupCampaignAds: Boolean = false,
        dedupAds: Boolean = false
    ): AdsResponse
}
```

### EventService

Service for tracking ad events.

```kotlin
interface EventService {
    fun deliveryBeaconEvent(eventUrl: String, callback: ((Boolean) -> Unit)? = null)
    suspend fun sendConversion(request: ConversionRequest): Boolean
}
```

### Ad Types

```kotlin
sealed class Ad {
    abstract val adId: String
    abstract val type: AdType
    abstract val clickUrl: String
    abstract val impressionUrl: String
    abstract val viewUrl: String
    abstract val sellerId: String?

    data class ProductAd(
        override val adId: String,
        override val type: AdType,
        override val clickUrl: String,
        override val impressionUrl: String,
        override val viewUrl: String,
        override val sellerId: String?,
        val productSku: String
    ) : Ad()

    data class BannerAd(
        override val adId: String,
        override val type: AdType,
        override val clickUrl: String,
        override val impressionUrl: String,
        override val viewUrl: String,
        override val sellerId: String?,
        val mediaUrl: String
    ) : Ad()

    data class SponsoredBrandAd(
        override val adId: String,
        override val type: AdType,
        override val clickUrl: String,
        override val impressionUrl: String,
        override val viewUrl: String,
        override val sellerId: String?,
        val mediaUrl: String,
        val products: List<BrandProduct>
    ) : Ad()

    data class DigitalSignageAd(
        override val adId: String,
        override val type: AdType,
        override val clickUrl: String,
        override val impressionUrl: String,
        override val viewUrl: String,
        override val sellerId: String?,
        val mediaUrl: String,
        val duration: Int
    ) : Ad()
}
```

---

## 🔧 Advanced Topics

### Specialized Builders

For detailed documentation on specialized builders, see [SPECIALIZED_BUILDERS.md](SPECIALIZED_BUILDERS.md).

### Custom HTTP Client

```kotlin
val customOkHttpClient = OkHttpClient.Builder()
    .connectTimeout(10, TimeUnit.SECONDS)
    .readTimeout(10, TimeUnit.SECONDS)
    .addInterceptor(LoggingInterceptor())
    .build()

// Note: Custom HTTP client configuration is not yet exposed in the public API
// Future versions will support custom OkHttpClient injection
```

---

## 🧪 Testing

Run the test suite:

```bash
# All tests
./gradlew test

# Specific test class
./gradlew test --tests "com.vtex.ads.sdk.models.SpecializedPlacementRequestTest"

# Integration test
./gradlew runIntegrationTest
```

**Test Coverage:**
- 146+ unit tests
- Integration tests with real API
- Mock-based tests for services
- Builder validation tests

---

## 📄 Requirements

- **JDK**: 21 or higher
- **Kotlin**: 1.9.22 or higher
- **Minimum Android SDK**: 21 (Android 5.0 Lollipop)

### Dependencies

```kotlin
dependencies {
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.22")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    // HTTP
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // JSON
    implementation("com.squareup.moshi:moshi:1.15.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.0")
}
```

---

## 📝 Changelog

### v0.1.0-SNAPSHOT (Current)

- ✅ Ad querying service (home, search, category, product pages)
- ✅ Event tracking (impression, view, click, conversion)
- ✅ Specialized builders for type-safe placements
- ✅ Order conversion tracking with auto-hashing
- ✅ Dynamic user ID management
- ✅ Retry logic with exponential backoff
- ✅ Comprehensive error handling
- ✅ Video ad support with resolution filtering
- ✅ Segmentation and targeting
- ✅ Full test coverage

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Development Setup

```bash
# Clone the repository
git clone https://github.com/vtex/vtex-ads-sdk-kotlin.git
cd vtex-ads-sdk-kotlin

# Build the project
./gradlew build

# Run tests
./gradlew test

# Run integration tests
./gradlew runIntegrationTest
```

---

## 📄 License

```
MIT License

Copyright (c) 2025 VTEX

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

## 🆘 Support

- **Issues**: [GitHub Issues](https://github.com/vtex/vtex-ads-sdk-kotlin/issues)
- **Documentation**: [VTEX Developer Portal](https://developers.vtex.com)
- **Email**: dev@vtex.com

---

## 🙏 Acknowledgments

Built with ❤️ by the VTEX Ads team.

Special thanks to:
- The Kotlin team for an amazing language
- Square for OkHttp and Moshi
- The VTEX developer community

---
---

**Made with ❤️ using [Claude Code](https://claude.com/claude-code)**
