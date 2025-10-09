# VtexAds SDK kotlin

A modern, type-safe Kotlin SDK for the VTEX Ads API. Built with Kotlin Coroutines for async operations and designed to work seamlessly with both Android and JVM projects.

## Features

- **Kotlin-first design** - Idiomatic Kotlin with coroutines support
- **Type-safe** - Strong typing for all models and API responses
- **Cross-platform** - Works with Android, server-side Kotlin, and other JVM platforms
- **Modern HTTP** - Uses OkHttp for reliable network operations
- **Easy to use** - Simple, intuitive API design

## Installation

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

## Quick Start

```kotlin
import com.vtex.ads.sdk.VtexAdsClient
import com.vtex.ads.sdk.VtexAdsConfig
import com.vtex.ads.sdk.Channel
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    // Configure the SDK
    val config = VtexAdsConfig(
        publisherId = "your-publisher-id",
        channel = Channel.WEB
    )

    // Create client
    val client = VtexAdsClient(config)

    // Use the SDK
    try {
        // API calls will be added here
        println("VTEX Ads SDK initialized successfully!")
    } finally {
        client.close()
    }
}
```

## Requirements

- JDK 11 or higher
- Kotlin 1.9.22 or higher

## Building from Source

```bash
# Clone the repository
git clone https://github.com/vtex/vtex-ads-sdk-kotlin.git
cd vtex-ads-sdk-kotlin

# Build the project
./gradlew build

# Run tests
./gradlew test
```

## Documentation

For detailed documentation, see [CLAUDE.md](CLAUDE.md).

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

MIT License - see LICENSE file for details.

## Support

- Issues: https://github.com/vtex/vtex-ads-sdk-kotlin/issues
- Documentation: https://developers.vtex.com
