# VTEX Ads SDK for Kotlin

## Overview

This is a Kotlin SDK for the VTEX Ads API, focused on ad querying and event tracking. It's designed to be reusable across any Kotlin project, including Android applications and server-side JVM applications.

## Features

- **Simple initialization**: Easy setup with publisher ID, session ID, and channel
- **Kotlin-first design**: Built with Kotlin idioms and coroutines
- **Type-safe ad responses**: Sealed classes for different ad types (Product, Banner, Sponsored Brand)
- **Context-aware queries**: Dedicated methods for home, search, category, and product pages
- **Non-blocking events**: Fire-and-forget event tracking (impression, view, click, conversion)
- **Cross-platform**: Works with Android, JVM backend, and other Kotlin platforms
- **Flexible placement configuration**: Builder pattern for defining ad placements
- **Video support**: Video ads with resolution filtering (720p, 1080p, etc.)

## Project Structure

```
vtex-ads-sdk-kotlin/
├── src/
│   ├── main/kotlin/com/vtex/ads/sdk/
│   │   ├── VtexAdsClient.kt              # Main SDK client
│   │   ├── VtexAdsClientConfig.kt        # Configuration
│   │   ├── models/                       # Data models
│   │   │   ├── Ad.kt                     # Sealed class for ad types
│   │   │   ├── AdsRequest.kt             # Request models
│   │   │   ├── AdsResponse.kt            # Response models
│   │   │   ├── PlacementRequest.kt       # Placement builder
│   │   │   ├── Channel.kt                # SITE, MSITE, APP
│   │   │   ├── Context.kt                # HOME, SEARCH, etc.
│   │   │   ├── AdType.kt                 # PRODUCT, BANNER, etc.
│   │   │   ├── AssetType.kt              # IMAGE, VIDEO
│   │   │   └── VideoSize.kt              # P720, P1080, etc.
│   │   ├── services/
│   │   │   ├── AdsService.kt             # Ad querying service
│   │   │   └── EventService.kt           # Event tracking service
│   │   ├── http/                         # HTTP client & adapters
│   │   └── exceptions/                   # Custom exceptions
│   └── test/                             # Unit tests
└── ...
```

## Installation

### Gradle (Kotlin DSL)

```kotlin
dependencies {
    implementation("com.vtex.ads:vtex-ads-sdk-kotlin:0.1.0-SNAPSHOT")
}
```

### Gradle (Groovy DSL)

```groovy
dependencies {
    implementation 'com.vtex.ads:vtex-ads-sdk-kotlin:0.1.0-SNAPSHOT'
}
```

## Quick Start

### 1. Initialize the Client

```kotlin
import com.vtex.ads.sdk.VtexAdsClient
import com.vtex.ads.sdk.models.Channel

val client = VtexAdsClient(
    publisherId = "your-publisher-id",
    sessionId = "user-session-id",
    userId = "user-id",  // optional
    channel = Channel.SITE,
    brand = "your-brand"  // optional, required for multi-brand publishers
)
```

### 2. Query Ads for Home Page

```kotlin
import com.vtex.ads.sdk.models.*
import kotlinx.coroutines.runBlocking

runBlocking {
    val ads = client.ads.getHomeAds(
        placements = mapOf(
            "home_banner_top" to PlacementRequest.builder()
                .quantity(1)
                .types(AdType.BANNER)
                .assetsType(AssetType.IMAGE)
                .size("desktop")
                .build(),

            "home_products_shelf" to PlacementRequest.builder()
                .quantity(10)
                .types(AdType.PRODUCT)
                .build()
        )
    )

    // Access ads by placement
    val banners = ads.getPlacement("home_banner_top")
    val products = ads.getPlacement("home_products_shelf")

    println("Found ${products.size} product ads")
}
```

### 3. Send Events (Non-Blocking)

```kotlin
// Send impression event when ad is visible
ads.getAllAds().forEach { ad ->
    client.events.deliveryBeaconEvent(ad.impressionUrl)
}

// Send click event when user clicks on ad
client.events.deliveryBeaconEvent(ad.clickUrl)

// Events are fire-and-forget and won't block your UI
```

## Usage Examples

### Search Page Ads

```kotlin
suspend fun loadSearchAds(searchTerm: String) {
    val ads = client.ads.getSearchAds(
        term = searchTerm,
        placements = mapOf(
            "search_sponsored_products" to PlacementRequest.builder()
                .quantity(5)
                .types(AdType.PRODUCT)
                .build(),

            "search_banner" to PlacementRequest.builder()
                .quantity(1)
                .types(AdType.BANNER, AdType.SPONSORED_BRAND)
                .assetsType(AssetType.IMAGE, AssetType.VIDEO)
                .videoSize(VideoSize.P720)
                .build()
        )
    )

    // Display ads in UI
    displayAds(ads)
}
```

### Category Page Ads

```kotlin
suspend fun loadCategoryAds(categoryPath: String) {
    val ads = client.ads.getCategoryAds(
        categoryName = "Electronics > Smartphones",
        placements = mapOf(
            "category_banner" to PlacementRequest.builder()
                .quantity(1)
                .types(AdType.BANNER, AdType.SPONSORED_BRAND)
                .assetsType(AssetType.VIDEO)
                .videoSize(VideoSize.P1080)
                .build(),

            "category_products" to PlacementRequest.builder()
                .quantity(8)
                .types(AdType.PRODUCT)
                .allowSkuDuplications(false)
                .build()
        )
    )
}
```

### Product Page Ads

```kotlin
suspend fun loadProductPageAds(productSku: String) {
    val ads = client.ads.getProductPageAds(
        productSku = productSku,
        placements = mapOf(
            "product_related" to PlacementRequest.builder()
                .quantity(4)
                .types(AdType.PRODUCT)
                .build()
        )
    )
}
```

### Working with Different Ad Types

```kotlin
ads.getAllAds().forEach { ad ->
    when (ad) {
        is Ad.ProductAd -> {
            println("Product SKU: ${ad.productSku}")
            // Show product ad with SKU
        }
        is Ad.BannerAd -> {
            println("Banner media: ${ad.mediaUrl}")
            // Display banner image or video
        }
        is Ad.SponsoredBrandAd -> {
            println("Brand media: ${ad.mediaUrl}")
            println("Products: ${ad.products.size}")
            // Show sponsored brand with products
        }
        is Ad.DigitalSignageAd -> {
            println("Signage duration: ${ad.duration}s")
            // Display for digital signage
        }
    }
}
```

### Advanced: Segmentation

```kotlin
val ads = client.ads.getHomeAds(
    placements = placements,
    segmentation = listOf(
        Segmentation(
            key = "STATE",
            values = listOf("SP", "RJ")
        ),
        Segmentation(
            key = "GENDER",
            values = listOf("M")
        ),
        Segmentation(
            key = "AUDIENCES",
            values = listOf("high_value_customers")
        )
    ),
    tags = listOf("electronics", "premium"),
    dedupCampaignAds = true,
    dedupAds = true
)
```

### Dynamic User ID Management

The SDK supports updating the user ID dynamically, which is useful when a user logs in after the client was created.

```kotlin
// 1. Create client with anonymous user
val client = VtexAdsClient(
    publisherId = "pub-123",
    sessionId = "session-456",
    userId = null,  // Anonymous user
    channel = Channel.SITE
)

// 2. Query ads (works without userId)
val ads = client.ads.getHomeAds(placements)
client.events.deliveryBeaconEvent(ad.impressionUrl)

// 3. User logs in → update userId
client.updateUserId("user-789")

// 4. Now all ads queries and events use the new user ID
val currentUserId = client.getCurrentUserId()  // Returns "user-789"
```

### Sending Order Events (Simplified)

The SDK provides a simplified way to send order conversion events. Customer data is automatically hashed with SHA-256 as required by the API.

#### Simple Order Tracking

```kotlin
import com.vtex.ads.sdk.models.Order

// Simplified order tracking with auto-hashing
client.deliveryOrderEvent(
    Order.builder()
        .orderId("order-123")
        .addItem("SKU-1", quantity = 2, price = 99.99)
        .addItem("SKU-2", quantity = 1, price = 149.99, sellerId = "seller-1")
        .customerEmail("customer@example.com")
        .customerPhone("11999999999")
        .customerDocument("12345678900")  // CPF/CNPJ
        .customerFirstName("John")
        .customerLastName("Doe")
        .state("SP")
        .city("São Paulo")
        .gender("M")  // F, M, or O
        .isCompany(false)
        .build()
) { success ->
    if (success) {
        println("Order tracked successfully!")
    }
}
```

#### Direct Order Construction

```kotlin
val order = Order(
    orderId = "order-456",
    items = listOf(
        OrderItem("SKU-1", 1, 49.99),
        OrderItem("SKU-2", 3, 29.99)
    ),
    customerEmail = "user@email.com",
    customerPhone = "11888888888",
    state = "RJ",
    city = "Rio de Janeiro"
)

client.deliveryOrderEvent(order) { success ->
    println("Order sent: $success")
}
```

#### Complete Checkout Flow Example

```kotlin
class CheckoutViewModel(
    private val adsClient: VtexAdsClient
) : ViewModel() {

    fun completeOrder(order: CheckoutOrder) {
        viewModelScope.launch {
            try {
                // Convert your checkout order to SDK order
                val adsOrder = Order.builder()
                    .orderId(order.id)
                    .createdAt(order.createdAt)
                    .customerEmail(order.customer.email)
                    .customerPhone(order.customer.phone)
                    .customerDocument(order.customer.document)
                    .customerFirstName(order.customer.firstName)
                    .customerLastName(order.customer.lastName)
                    .state(order.shippingAddress.state)
                    .city(order.shippingAddress.city)
                    .gender(order.customer.gender)
                    .isCompany(order.customer.isCompany)
                    .apply {
                        order.items.forEach { item ->
                            addItem(
                                productSku = item.sku,
                                quantity = item.quantity,
                                price = item.unitPrice,  // NOT multiplied by quantity
                                sellerId = item.sellerId
                            )
                        }
                    }
                    .build()

                // Send order event (non-blocking)
                adsClient.deliveryOrderEvent(adsOrder) { success ->
                    if (success) {
                        Log.d("Checkout", "Order conversion tracked")
                    } else {
                        Log.w("Checkout", "Failed to track conversion")
                    }
                }

                // Continue with your checkout flow
                navigateToSuccess()

            } catch (e: Exception) {
                handleError(e)
            }
        }
    }
}
```

### Advanced: Manual Conversion Tracking

For advanced use cases where you need full control over the conversion data:

```kotlin
import com.vtex.ads.sdk.utils.HashUtils

client.events.sendConversion(
    ConversionRequest(
        publisherId = "your-publisher-id",
        userId = "user-id",
        sessionId = "session-id",
        orderId = "order-123",
        createdAt = Instant.now().toString(),
        channel = Channel.SITE,
        items = listOf(
            ConversionItem(
                productSku = "SKU-123",
                quantity = 2,
                price = 99.99,
                sellerId = "seller-1"
            )
        ),
        emailHashed = HashUtils.sha256("user@email.com"),
        phoneHashed = HashUtils.sha256("11999999999"),
        socialIdHashed = HashUtils.sha256("12345678900"),
        brand = "your-brand"
    )
) { success ->
    println("Conversion tracked: $success")
}
```

## Android Integration

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
                        "search_products" to PlacementRequest.builder()
                            .quantity(10)
                            .types(AdType.PRODUCT)
                            .build()
                    )
                )
                _ads.value = response

                // Track impressions
                response.getAllAds().forEach { ad ->
                    adsClient.events.deliveryBeaconEvent(ad.impressionUrl)
                }
            } catch (e: Exception) {
                // Handle error
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

                // Open ad destination
                // ... navigate to product/page
            }
    )

    LaunchedEffect(ad.adId) {
        // Track view event when ad becomes visible
        adsClient.events.deliveryBeaconEvent(ad.viewUrl)
    }
}
```

## Video Ads

### Supported Video Sizes

- **P1080** (1920x1080) - Full screen only
- **P720** (1280x720) - Full screen only
- **P480** (854x480)
- **P360** (640x360)
- **P320** (568x320) - Mobile recommended

### Requesting Video Ads

```kotlin
val videoAds = client.ads.getHomeAds(
    placements = mapOf(
        "home_video_banner" to PlacementRequest.builder()
            .quantity(1)
            .types(AdType.BANNER, AdType.SPONSORED_BRAND)
            .assetsType(AssetType.VIDEO)
            .videoSize(VideoSize.P720)
            .build()
    )
)
```

## Best Practices

### Placement Naming Convention

Use the pattern: `{channel}_{context}_{position}_{type}`

Examples:
- `site_home_middle_banner`
- `msite_search_top-shelf_product`
- `app_category_bottom-vitrine_sponsored_brand`

### Timeout Configuration

The SDK uses 600ms timeout by default (as per API recommendations). You can customize:

```kotlin
val client = VtexAdsClient(
    publisherId = "publisher-id",
    sessionId = "session-id",
    channel = Channel.SITE,
    timeout = 500L  // milliseconds
)
```

### Session ID Management

- Should be unique per user
- Should have a lifetime of at least 14 days (conversion window)
- Ideally, the session ID should not expire
- Must be consistent throughout the user's navigation

### Event Tracking

- Always send impression events when ads are displayed
- Send view events when ads are actually viewed by the user
- Send click events when users interact with ads
- Events are deduplicated:
  - Impressions: 1 minute
  - Clicks: 1 hour
  - Conversions: Not deduplicated (except same order_id within 30 days)

## Configuration

### VtexAdsClientConfig

```kotlin
data class VtexAdsClientConfig(
    val publisherId: String,           // Required
    val sessionId: String,             // Required
    val userId: String? = null,        // Optional
    val channel: Channel,              // Required (SITE, MSITE, APP)
    val brand: String? = null,         // Required for multi-brand publishers
    val baseUrl: String = DEFAULT_BASE_URL,
    val eventsBaseUrl: String = DEFAULT_EVENTS_BASE_URL,
    val timeout: Long = 600L           // milliseconds
)
```

## Error Handling

```kotlin
try {
    val ads = client.ads.getHomeAds(placements)
} catch (e: VtexAdsException) {
    when (e) {
        is VtexAdsAuthenticationException -> {
            // Handle authentication error
        }
        is VtexAdsValidationException -> {
            // Handle validation error
        }
        is VtexAdsNetworkException -> {
            // Handle network error
        }
        is VtexAdsRateLimitException -> {
            // Handle rate limit (retry after e.retryAfter)
        }
        else -> {
            // Handle other errors
        }
    }
}
```

## Testing

Run tests with:

```bash
./gradlew test
```

Test coverage includes:
- Configuration validation
- Placement request builder
- Ad JSON parsing
- Response handling
- Client initialization
- Dynamic user ID updates
- Order builder and validation
- SHA-256 hashing utilities

## Building

```bash
# Build project
./gradlew build

# Generate JAR
./gradlew jar

# Publish to local Maven
./gradlew publishToMavenLocal
```

## API Reference

### Ad Types

- `Ad.ProductAd`: Product advertisement with SKU
- `Ad.BannerAd`: Banner with image or video
- `Ad.SponsoredBrandAd`: Sponsored brand with media and product list
- `Ad.DigitalSignageAd`: Digital signage with duration

### Contexts

- `HOME`: Home page
- `SEARCH`: Search results page (requires `term`)
- `CATEGORY`: Category page (requires `categoryName`)
- `PRODUCT_PAGE`: Product details page (requires `productSku`)
- `BRAND_PAGE`: Brand page (requires `brandName`)

### Channels

- `SITE`: Desktop website
- `MSITE`: Mobile website
- `APP`: Mobile application

## License

MIT License - See LICENSE file for details

## Support

- GitHub Issues: https://github.com/vtex/vtex-ads-sdk-kotlin/issues
- VTEX Developer Portal: https://developers.vtex.com

## Changelog

### 0.1.0-SNAPSHOT
- Ad querying service with context-specific methods
- Event tracking service (non-blocking)
- Support for product, banner, sponsored brand, and digital signage ads
- Video ads support with resolution filtering
- Placement builder with fluent API
- Segmentation and targeting support
- Dynamic user ID management (thread-safe)
- Simplified order tracking with automatic data hashing
- Order builder with fluent API
- SHA-256 hashing utilities for sensitive data
- Complete test coverage (63 tests)
