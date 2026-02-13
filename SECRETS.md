# Secrets Management

This project uses `local.properties` to store sensitive information like API keys and tokens. These values should **NEVER** be committed to version control.

## Setup

1. Copy `local.properties.example` to `local.properties`:
   ```bash
   cp local.properties.example local.properties
   ```

2. Fill in your actual values in `local.properties`:
   - `API_BASE_URL`: The base URL for API endpoints
   - `MAPBOX_ACCESS_TOKEN`: Your Mapbox access token (get one from https://account.mapbox.com/)
   - `MAPBOX_STYLE_URL`: Your Mapbox style URL

## How It Works

The build system reads values from `local.properties` and:
- Exposes them as `BuildConfig` fields in Kotlin code
- Generates string resources for XML usage (e.g., Mapbox token)

### Usage in Code

**Kotlin:**
```kotlin
import com.example.roomexample.BuildConfig

// Access API base URL
val apiUrl = BuildConfig.API_BASE_URL

// Access Mapbox style URL
val styleUrl = BuildConfig.MAPBOX_STYLE_URL
```

**XML (automatically generated):**
```xml
<!-- Mapbox token is automatically available as a string resource -->
<string name="mapbox_access_token">@string/mapbox_access_token</string>
```