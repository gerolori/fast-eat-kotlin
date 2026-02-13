# Mangia e Basta - Drone Food Delivery Platform

An Android application built with Kotlin and Jetpack Compose that demonstrates modern Android development practices. The app implements a food delivery platform where users can browse restaurants, place orders, and track real-time drone deliveries on an interactive map.

## Project Overview

Mangia e Basta is a full-featured food delivery application built to showcase practical Android development skills. Users can discover nearby restaurants, manage profiles with payment information, and track orders in real-time.

### Key Features
- Browse and filter menus from nearby restaurants with delivery times
- Interactive map displaying drone position, restaurant location, and delivery destination
- User profile management with credit card information
- Real-time order status tracking with ETA updates
- Order history and cancellation capabilities
- Material Design 3 interface built with Jetpack Compose

## Technology Stack

### Architecture and Design Patterns
- Clean Architecture with separation of concerns (presentation, domain, data layers)
- MVVM pattern with ViewModels and Jetpack Compose
- Repository pattern for data source abstraction

### Android and Kotlin
- Kotlin 2.3.10 with coroutines support
- Jetpack Compose for declarative UI
- Android Gradle Plugin 9.0.1 with KSP (Kotlin Symbol Processing)
- Target SDK 36 with minimum SDK 34 support

### Data Persistence
- Room Database for SQLite-based local storage with pre-configured schemas
- DataStore for key-value storage
- GSON for JSON serialization

### Networking
- Ktor Client 3.4.0 for HTTP communication
- Kotlin Serialization for automatic JSON parsing
- Built-in logging and error handling

### Location and Maps
- Mapbox Maps SDK for interactive map functionality
- Google Play Services Location for GPS and geolocation
- Maps Compose library for Compose integration

### Testing
- JUnit 4 for unit testing
- Mockito for test mocking
- Espresso for UI testing
- Room in-memory database for integration tests

## Project Structure

```
app/src/main/java/com/example/mangiaebasta/
├── features/
│   ├── menu/
│   │   ├── presentation/
│   │   │   ├── MenuPage.kt
│   │   │   ├── MenuDetailsPage.kt
│   │   │   └── ConfirmOrderPage.kt
│   │   └── data/
│   │       └── ImagesDao.kt
│   ├── order/
│   │   └── presentation/
│   │       └── OrderPage.kt
│   └── profile/
│       └── presentation/
│           ├── ProfilePage.kt
│           └── ProfileForm.kt
├── common/
│   ├── data/
│   ├── model/
│   │   └── DataClasses.kt
│   ├── presentation/
│   │   ├── theme/
│   │   ├── Root.kt
│   │   ├── FirstLaunch.kt
│   │   └── SplashLoadingScreen.kt
│   └── utils/
│       ├── PositionManager.kt
│       └── CoroutineDispatchers.kt
├── AppViewModel.kt
└── MainActivity.kt
```

## Core Features

### Feature 1: Menu Discovery
- Browse available menus from nearby restaurants
- Filter by flight distance and ETA
- View detailed menu information with high-resolution images
- One-click ordering with input validation
- Business constraints: Cannot order if a previous order is pending, user must complete profile information before ordering
- Technologies: Compose UI, Room Database, Ktor networking

### Feature 2: User Profile
- Edit personal information (name, surname - max 15 chars)
- Manage credit card details with validation (max 31 chars for card number)
- View order history with last order details
- Technologies: DataStore, form validation, secure storage

### Feature 3: Real-time Order Tracking
- Live order status (pending → in progress → delivered)
- Interactive map showing restaurant departure point, drone position, and delivery destination
- Real-time ETA updates
- Order cancellation capability
- Order history with delivery confirmations
- Technologies: Mapbox SDK, Coroutines, Location Services

## Getting Started

### Prerequisites
- Android Studio Hedgehog or later
- SDK 36 (Android 15) installed
- Kotlin 2.3.10 plugin
- JDK 17 or higher

### Setup

1. Clone the repository
   ```bash
   git clone [repository-url]
   cd mangia-e-basta-kotlin
   ```

2. Configure Local Properties
   - Copy `local.properties.example` to `local.properties`
   - Add your API credentials:
     ```properties
     API_BASE_URL=https://your-api-endpoint.com
     MAPBOX_ACCESS_TOKEN=your_mapbox_token
     MAPBOX_STYLE_URL=your_mapbox_style_url
     ```

3. Build and Run
   ```bash
   ./gradlew build
   ./gradlew installDebug
   ```

### Running Tests
```bash
# Unit tests
./gradlew test

# Instrumented tests (Android device/emulator required)
./gradlew connectedAndroidTest
```

## Security Considerations

- API credentials managed via `local.properties` (excluded from version control)
- Card data handled with PCI-DSS compliance considerations
- Location data requires runtime permissions with user consent
- Session management using secure session ID (SID) storage

## Code Quality and Best Practices

The project follows SOLID principles:
- Single Responsibility: Feature-based modular architecture
- Open/Closed: Extension via composition and inheritance
- Liskov Substitution: Interface-based dependencies
- Interface Segregation: Typed repositories and data sources
- Dependency Inversion: Abstract layer dependencies

Reactive programming approach:
- Coroutines for async operations
- Flow for reactive streams
- LiveData for state management

Material Design 3 implementation:
- Consistent theming with custom colors
- Adaptive typography
- Modern component library

Type safety:
- Null-safe Kotlin with optional types
- Sealed classes for error handling
- Compile-time checked serialization

## Performance Optimizations

- Image caching via DAO to reduce network requests
- Database indexing with Room schema optimization
- Efficient Compose rendering with lazy loading
- Proper coroutine scope management with lifecycle awareness

## API Integration

The application communicates with a backend API for all data operations. Key endpoints:

| Endpoint | Method | Purpose |
|----------|--------|---------|
| `/auth/login` | POST | User authentication |
| `/user/info` | GET | Fetch user profile |
| `/user/update` | PUT | Update user information |
| `/menus` | GET | List available menus |
| `/orders` | POST | Create new order |
| `/orders/{id}` | GET | Track order status |
| `/orders/{id}/cancel` | DELETE | Cancel order |

All API communication uses JSON serialization via Kotlin's kotlinx.serialization.

## UI and User Experience

- Splash screen with app initialization
- First launch onboarding flow
- Jetpack Navigation Compose with type-safe routes
- Material Design 3 theming with dynamic colors
- Responsive layouts for various screen sizes

## Testing

The project includes unit tests, integration tests, and UI tests:
- Unit tests for data layer and utility functions
- Integration tests for Room database operations
- UI tests for Compose component interactions
- Mock data fixtures for test isolation

## Learning Outcomes

This project demonstrates experience with:
- Advanced Kotlin features (coroutines, sealed classes, extension functions)
- Modern Android architecture with Jetpack libraries
- API integration patterns
- Database design with Room ORM
- Reactive programming with Flow and LiveData
- UI implementation with Jetpack Compose
- Real-world app features (maps, location)
- Testing best practices
- Version control and git workflow

## Technical Details

Implementation notes:
- First app launch asks for SID and stores it locally for use in every server call
- Application maintains last visited page state when returning to it
- All images are square and Base64 encoded (no HTML prefix)
- Developed and tested on Pixel 7 API 36

### Client-Server Sequence
[![client-server sequence diagram](https://mermaid.ink/img/pako:eNp9UT1PwzAQ_SunWxhIq6ZJm9ZDByhIHbpQJpTFjY_UamwH20ENUf47TgNIDHDT3bv3Ifs6LIwgZOjorSFd0Fby0nKVawh1X0nSfrLZ3B7IvpNl8Cit83A0xkegiQQcdtuROzImgTwZZQyeT2QJWtNAaf4y3N0oKHhVSV0C10Ba1Ebq4D5obxyo9t-IJ3K10Y7gyIszcAf-SxdS7aDECBVZxaUIj-wGnxwDR1GOLLSC23OO0YgXFXduwLsxL8eTFPSgat_uSR3JujtzGfbeNjRQ-lz3IYA33hxaXSAbFhFa05QnZK-8cmFqasH997_-oDXXL8b8mpF1eEG2nE_XWbZaJkkcZ8tssYqwRRYnyTSdrdNFmqSLbJbE8z7Cj6tDHCEJ6Y3dj6e8XrT_BHs2l2E?type=png)](https://mermaid.live/edit#pako:eNp9UT1PwzAQ_SunWxhIq6ZJm9ZDByhIHbpQJpTFjY_UamwH20ENUf47TgNIDHDT3bv3Ifs6LIwgZOjorSFd0Fby0nKVawh1X0nSfrLZ3B7IvpNl8Cit83A0xkegiQQcdtuROzImgTwZZQyeT2QJWtNAaf4y3N0oKHhVSV0C10Ba1Ebq4D5obxyo9t-IJ3K10Y7gyIszcAf-SxdS7aDECBVZxaUIj-wGnxwDR1GOLLSC23OO0YgXFXduwLsxL8eTFPSgat_uSR3JujtzGfbeNjRQ-lz3IYA33hxaXSAbFhFa05QnZK-8cmFqasH997_-oDXXL8b8mpF1eEG2nE_XWbZaJkkcZ8tssYqwRRYnyTSdrdNFmqSLbJbE8z7Cj6tDHCEJ6Y3dj6e8XrT_B1Hs2l2E)