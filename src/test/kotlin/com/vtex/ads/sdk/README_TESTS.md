# VTEX Ads SDK - Test Guide

## Test Structure

This directory contains tests for the VTEX Ads SDK:

### 1. Unit Tests

- **VtexAdsClientConfigTest.kt**: Tests for configuration validation and defaults

### 2. Integration Tests

- **RealWorldIntegrationTest.kt**: Real-world integration tests using actual publisher ID

## Running Tests

### Run All Tests (excluding disabled integration tests)

```bash
./gradlew test
```

### Run Specific Test Class

```bash
./gradlew test --tests "com.vtex.ads.sdk.VtexAdsClientConfigTest"
./gradlew test --tests "com.vtex.ads.sdk.RealWorldIntegrationTest"
```

### Run Specific Test Method

```bash
./gradlew test --tests "com.vtex.ads.sdk.VtexAdsClientConfigTest.should create config with valid parameters"
```

## Real-World Integration Tests

The `RealWorldIntegrationTest` class contains tests that use a real publisher ID to validate SDK functionality. These tests are **disabled by default** to avoid making unnecessary API calls during regular test runs.

### Enabling Integration Tests

To enable integration tests, remove the `@Disabled` annotation from the test methods:

```kotlin
@Test
// @Disabled("Enable manually to test with real API")  // Comment out or remove this line
fun `should initialize SDK with real publisher ID`() {
    // Test implementation
}
```

### Available Integration Tests

1. **should initialize SDK with real publisher ID**
   - Validates SDK initialization with real configuration
   - Prints configuration details

2. **should test different channel configurations**
   - Tests SDK with all available channels (WEB, MOBILE, DESKTOP, API, OTHER)

3. **should test timeout configuration**
   - Validates custom timeout settings

4. **should test retry configuration**
   - Validates custom retry settings

5. **should test mobile channel configuration**
   - Tests SDK configured for mobile apps

6. **should test API channel configuration for server-to-server**
   - Tests SDK configured for server-to-server communication

7. **should validate publisher ID format** ✓ (Always enabled)
   - Validates publisher ID UUID format

8. **should validate all configuration defaults** ✓ (Always enabled)
   - Validates default configuration values

9. **should demonstrate SDK usage example** ✓ (Always enabled)
   - Prints SDK usage example

## Test Output

Tests marked with ✓ will display detailed output:

```
✓ SDK initialized successfully
  Publisher ID: d4dff0cb-1f21-4a96-9acf-d9426a5ed08c
  Channel: WEB
  Base URL: https://newtail-media.newtail.com.br
  Timeout: 10s
  Max Retries: 3
```

## Publisher ID

The tests use the following publisher ID:
```
d4dff0cb-1f21-4a96-9acf-d9426a5ed08c
```

To use a different publisher ID, modify the `publisherId` constant in `RealWorldIntegrationTest.kt`:

```kotlin
private val publisherId = "your-publisher-id-here"
```

## Best Practices

1. **Keep integration tests disabled by default** to avoid unnecessary API calls
2. **Enable integration tests manually** when you need to validate real API behavior
3. **Use unit tests** for regular development and CI/CD pipelines
4. **Use integration tests** for manual validation before releases

## Continuous Integration

In CI/CD pipelines, only unit tests run by default. Integration tests are skipped unless explicitly enabled.

## Debug Mode

To see detailed HTTP logs, set `debug = true` in the configuration:

```kotlin
val config = VtexAdsClientConfig(
    publisherId = publisherId,
    channel = Channel.SITE,
    debug = true  // Enable debug logging
)
```
