# VTEX Ads SDK for Kotlin

## Overview

This is a Kotlin SDK for the VTEX Ads API. It's designed to be reusable across any Kotlin project, including Android applications and server-side JVM applications.

## Project Structure

```
vtex-ads-sdk-kotlin/
├── src/
│   ├── main/
│   │   └── kotlin/
│   │       └── com/
│   │           └── vtex/
│   │               └── ads/
│   │                   └── sdk/
│   │                       ├── VtexAdsClient.kt       # Main SDK client
│   │                       ├── VtexAdsConfig.kt       # Configuration classes
│   │                       ├── models/                # Data models
│   │                       ├── api/                   # API endpoint interfaces
│   │                       ├── http/                  # HTTP client implementation
│   │                       └── exceptions/            # Custom exceptions
│   └── test/
│       └── kotlin/
│           └── com/
│               └── vtex/
│                   └── ads/
│                       └── sdk/
│                           └── ...                    # Test files
├── build.gradle.kts                                   # Build configuration
├── settings.gradle.kts                                # Project settings
├── gradle.properties                                  # Gradle properties
└── CLAUDE.md                                          # This file
```

## Features

- **Kotlin-first design**: Built with Kotlin idioms and best practices
- **Coroutine support**: Async/await patterns using Kotlin Coroutines
- **Type-safe**: Strong typing for all API models and responses
- **Cross-platform**: Works with Android, JVM backend, and other Kotlin platforms
- **HTTP client**: Uses OkHttp for reliable HTTP communication
- **JSON serialization**: Moshi for efficient JSON parsing

## Dependencies

- **Kotlin Standard Library**: Core Kotlin functionality
- **Kotlin Coroutines**: For asynchronous operations
- **OkHttp**: HTTP client
- **Moshi**: JSON serialization/deserialization
- **JUnit 5**: Testing framework
- **MockK**: Mocking library for tests

## Building the SDK

### Prerequisites

- JDK 11 or higher
- Gradle 8.5+ (included via Gradle Wrapper)

### Build Commands

```bash
# Build the project
./gradlew build

# Run tests
./gradlew test

# Generate JAR
./gradlew jar

# Generate sources and javadoc JARs
./gradlew sourcesJar javadocJar

# Publish to local Maven repository
./gradlew publishToMavenLocal
```

## Usage

### Adding the SDK to Your Project

#### Gradle (Kotlin DSL)

```kotlin
dependencies {
    implementation("com.vtex.ads:vtex-ads-sdk-kotlin:0.1.0-SNAPSHOT")
}
```

#### Gradle (Groovy DSL)

```groovy
dependencies {
    implementation 'com.vtex.ads:vtex-ads-sdk-kotlin:0.1.0-SNAPSHOT'
}
```

### Basic Usage Example

```kotlin
import com.vtex.ads.sdk.VtexAdsClient
import com.vtex.ads.sdk.VtexAdsConfig
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    // Create configuration
    val config = VtexAdsConfig(
        apiKey = "your-api-key",
        accountName = "your-account-name",
        baseUrl = "https://api.vtex.com"
    )

    // Initialize client
    val client = VtexAdsClient(config)

    // Use the client
    try {
        val campaigns = client.campaigns.list()
        campaigns.forEach { campaign ->
            println("Campaign: ${campaign.name}")
        }
    } catch (e: Exception) {
        println("Error: ${e.message}")
    } finally {
        client.close()
    }
}
```

### Android Usage

For Android projects, make sure to perform API calls in a coroutine scope:

```kotlin
class MyViewModel : ViewModel() {
    private val client = VtexAdsClient(config)

    fun loadCampaigns() {
        viewModelScope.launch {
            try {
                val campaigns = client.campaigns.list()
                // Update UI with campaigns
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
```

## Development

### Code Style

This project follows the official Kotlin coding conventions. Use `ktlint` for code formatting:

```bash
./gradlew ktlintFormat
```

### Testing

Tests are located in `src/test/kotlin/`. Run tests with:

```bash
./gradlew test
```

### Adding New Features

1. Create models in `models/` package
2. Define API endpoints in `api/` package
3. Implement business logic in the main SDK client
4. Add comprehensive tests
5. Update documentation

## API Coverage

The SDK aims to provide complete coverage of the VTEX Ads API:

- [ ] Campaigns Management
- [ ] Ad Groups
- [ ] Ads
- [ ] Keywords
- [ ] Bidding Strategies
- [ ] Reports and Analytics
- [ ] Budget Management
- [ ] Targeting Options

## Configuration Options

```kotlin
data class VtexAdsConfig(
    val apiKey: String,                    // Your VTEX Ads API key
    val accountName: String,               // VTEX account name
    val baseUrl: String = DEFAULT_BASE_URL, // API base URL
    val timeout: Duration = 30.seconds,    // Request timeout
    val maxRetries: Int = 3,               // Number of retries for failed requests
    val debug: Boolean = false             // Enable debug logging
)
```

## Error Handling

The SDK provides custom exceptions for different error scenarios:

- `VtexAdsException`: Base exception class
- `VtexAdsAuthenticationException`: Authentication failures
- `VtexAdsValidationException`: Request validation errors
- `VtexAdsNetworkException`: Network-related errors
- `VtexAdsRateLimitException`: Rate limit exceeded

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

MIT License - See LICENSE file for details

## Support

For issues and questions:
- GitHub Issues: https://github.com/vtex/vtex-ads-sdk-kotlin/issues
- VTEX Developer Portal: https://developers.vtex.com

## Changelog

### 0.1.0-SNAPSHOT
- Initial SDK structure
- Basic project setup with Gradle
- Core architecture design
