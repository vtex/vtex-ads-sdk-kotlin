# 🐛 feat: Implement Optional Debug Logging System

## 📋 Overview

This PR implements a comprehensive, optional, and backward-compatible debug logging system for the VTEX Ads SDK Kotlin. The system provides granular control over logging categories while maintaining zero performance overhead when disabled.

## ✨ Features

### 🔧 Core Components

- **VtexAdsDebug Enum**: Granular debug categories (`EVENTS_ALL`, `EVENTS_IMPRESSION`, `EVENTS_VIEW`, `EVENTS_CLICK`, `EVENTS_CONVERSION`, `ADS_LOAD`)
- **DebugFunction Typealias**: Configurable logging function with `NO_OP` default
- **VtexLogger**: Lazy evaluation, exception-safe logging with proper enablement logic
- **debugOf() Helper**: Convenient function for creating debug sets

### 📊 Logging Categories

| Category | Description | Use Case |
|----------|-------------|----------|
| `EVENTS_ALL` | All event-related logs | Comprehensive event tracking |
| `EVENTS_IMPRESSION` | Ad impression events | When ads are displayed |
| `EVENTS_VIEW` | Ad view events | When ads are actually viewed |
| `EVENTS_CLICK` | Ad click events | When users click ads |
| `EVENTS_CONVERSION` | Order conversion events | When purchases are completed |
| `ADS_LOAD` | Ad loading events | API requests and responses |

### 🎯 Log Templates

#### Event Logs
```
impression success adId=123 placement=home.hero
view success adId=456 placement=search.top
click success adId=789 placement=category.banner
conversion success orderId=order-123 userId=user-456 items=3
```

#### Ads Loading Logs
```
ads_load success requestId=req-123 status=200 latencyMs=150 count=5
ads_load error requestId=req-123 status=500 latencyMs=200 cause=IOException: timeout
```

## 🔄 API Changes

### Backward Compatible Additions

```kotlin
// New optional parameters (defaults maintain current behavior)
VtexAdsClient(
    publisherId = "pub-123",
    sessionIdProvider = { getSessionId() },
    channel = Channel.SITE,
    debug = debugOf(VtexAdsDebug.EVENTS_ALL, VtexAdsDebug.ADS_LOAD), // NEW
    debugFunction = { label, message -> Log.d(label, message) }      // NEW
)

// Enhanced deliveryBeaconEvent with placement context
client.events.deliveryBeaconEvent(
    eventUrl = ad.impressionUrl,
    placement = "home.hero"  // NEW optional parameter
)
```

## 🧪 Testing

### Test Coverage
- **158 tests passing** (100% backward compatibility)
- **New test files**: 6 comprehensive test suites
- **Contract validation**: Log templates and field validation
- **Integration tests**: Real-world usage scenarios
- **Edge cases**: Exception handling, lazy evaluation, enablement matrix

### Test Categories
- `VtexAdsDebugTest`: Helper function validation
- `DebugFunctionTest`: NO_OP and custom function behavior
- `VtexLoggerTest`: Enablement logic and lazy evaluation
- `VtexAdsClientDebugTest`: Client integration
- `VtexAdsLoggingContractTest`: Log template validation
- `VtexAdsLoggingIntegrationTest`: End-to-end scenarios

## 📚 Documentation

### README Updates
- **New section**: "🐛 Debugging (Opcional)"
- **Usage examples**: Android, server, granular configurations
- **Log format documentation**: Templates and field descriptions
- **Best practices**: Performance and security considerations

## ⚡ Performance

### Zero Overhead When Disabled
- **Lazy evaluation**: Message lambdas not executed when debug disabled
- **No-op default**: `DebugFunctions.NO_OP` discards all messages
- **Exception safety**: Logging errors never break application flow

### Enablement Matrix
- `debug = emptySet()` → No logs emitted
- `EVENTS_ALL` → All event categories enabled (not ADS_LOAD)
- `ADS_LOAD` → Independent, must be explicitly enabled
- Granular control → Specific categories only

## 🔒 Security & Privacy

- **No PII logging**: Only technical IDs and metadata
- **Exception safety**: Debug function errors never propagate
- **Configurable output**: Integrate with any logging system

## 📈 Benefits

### For Developers
- **Troubleshooting**: Clear, actionable log messages
- **Performance monitoring**: Latency and success rate tracking
- **Debug flexibility**: Enable only needed categories

### For Operations
- **Observability**: Structured logs for monitoring
- **Error tracking**: Detailed error context and causes
- **Performance insights**: Request timing and success rates

## 🚀 Usage Examples

### Basic Debug (All Events)
```kotlin
val client = VtexAdsClient(
    publisherId = "pub-123",
    sessionIdProvider = { getSessionId() },
    channel = Channel.SITE,
    debug = debugOf(VtexAdsDebug.EVENTS_ALL),
    debugFunction = { label, message -> Log.d(label, message) }
)
```

### Granular Debug (Specific Categories)
```kotlin
val client = VtexAdsClient(
    publisherId = "pub-123",
    sessionIdProvider = { getSessionId() },
    channel = Channel.SITE,
    debug = debugOf(VtexAdsDebug.EVENTS_VIEW, VtexAdsDebug.ADS_LOAD),
    debugFunction = { label, message -> logger.info("[$label] $message") }
)
```

### Server-Side Integration
```kotlin
val client = VtexAdsClient(
    publisherId = "pub-123",
    sessionIdProvider = { getSessionId() },
    channel = Channel.SITE,
    debug = debugOf(VtexAdsDebug.ADS_LOAD),
    debugFunction = { label, message -> 
        slf4jLogger.info("VTEX_ADS [{}] {}", label, message)
    }
)
```

## ✅ Checklist

- [x] **Backward Compatibility**: All existing tests pass
- [x] **Performance**: Zero overhead when disabled
- [x] **Security**: No PII, exception-safe logging
- [x] **Documentation**: Complete README section
- [x] **Testing**: Comprehensive test coverage
- [x] **Code Quality**: Clean, maintainable implementation
- [x] **API Design**: Intuitive, flexible interface

## 🔍 Breaking Changes

**None** - This is a purely additive feature with full backward compatibility.

## 📝 Migration Guide

**No migration required** - existing code continues to work unchanged. To enable debug logging, simply add the new optional parameters to your `VtexAdsClient` constructor.

---

**Ready for review and merge!** 🚀
