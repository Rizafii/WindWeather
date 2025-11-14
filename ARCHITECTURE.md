# 🏗️ Architecture Overview

## Arsitektur Aplikasi

```
┌─────────────────────────────────────────────────────────────────┐
│                          PRESENTATION LAYER                      │
│  ┌──────────────┐  ┌──────────────┐  ┌────────────────────┐   │
│  │ MainActivity │→ │WeatherScreen │→ │ UI Components      │   │
│  │              │  │              │  │ - ActionBar        │   │
│  │ - Permission │  │ - UI State   │  │ - DailyForecast    │   │
│  │ - ViewModel  │  │ - Loading    │  │ - AirQuality       │   │
│  │   Factory    │  │ - Error      │  │ - WeeklyForecast   │   │
│  └──────────────┘  └──────────────┘  │ - LocationDialog   │   │
│                                        └────────────────────┘   │
└───────────────────────────────┬─────────────────────────────────┘
                                │ observes StateFlow
                                ↓
┌─────────────────────────────────────────────────────────────────┐
│                         VIEWMODEL LAYER                          │
│  ┌────────────────────────────────────────────────────────┐    │
│  │              WeatherViewModel                          │    │
│  │  ┌──────────────────────────────────────────────┐     │    │
│  │  │         WeatherUiState                       │     │    │
│  │  │  - isLoading: Boolean                        │     │    │
│  │  │  - currentLocation: Location?                │     │    │
│  │  │  - savedLocations: List<Location>           │     │    │
│  │  │  - weatherData: WeatherResponse?            │     │    │
│  │  │  - forecastItems: List<ForecastItem>        │     │    │
│  │  │  - airQualityItems: List<AirQualityItem>    │     │    │
│  │  │  - error: String?                            │     │    │
│  │  └──────────────────────────────────────────────┘     │    │
│  │                                                        │    │
│  │  Methods:                                              │    │
│  │  - loadCurrentLocationWeather()                       │    │
│  │  - loadWeatherForLocation(location)                   │    │
│  │  - checkAndRequestLocation()                          │    │
│  └────────────────────────────────────────────────────────┘    │
└───────────┬────────────────────────────┬────────────────────────┘
            │ uses                       │ uses
            ↓                            ↓
┌─────────────────────────────┐  ┌──────────────────────────┐
│     REPOSITORY LAYER         │  │    SERVICE LAYER         │
│  ┌──────────────────────┐   │  │  ┌────────────────────┐ │
│  │ WeatherRepository    │   │  │  │ LocationService    │ │
│  │                      │   │  │  │                    │ │
│  │ - getWeather()       │   │  │  │ - getCurrentLoc()  │ │
│  │ - Result<T>          │   │  │  │ - getCityName()    │ │
│  │ - Error handling     │   │  │  │ - hasPermission()  │ │
│  └──────────────────────┘   │  │  │ - isEnabled()      │ │
└─────────────┬───────────────┘  │  └────────────────────┘ │
              │ uses              └────────────┬────────────┘
              ↓                                 │ uses
┌─────────────────────────────┐                ↓
│       DATA/API LAYER         │  ┌──────────────────────────┐
│  ┌──────────────────────┐   │  │  Android Location API    │
│  │ RetrofitClient       │   │  │  - FusedLocationClient   │
│  │ - Moshi converter    │   │  │  - Geocoder              │
│  │ - OkHttp logging     │   │  │  - LocationManager       │
│  └──────────────────────┘   │  └──────────────────────────┘
│             ↓                │
│  ┌──────────────────────┐   │
│  │ WeatherApiService    │   │
│  │ - @GET forecast      │   │
│  │ - Suspend function   │   │
│  └──────────────────────┘   │
└─────────────┬───────────────┘
              │ calls
              ↓
┌─────────────────────────────────────────┐
│        Open-Meteo API                   │
│  https://api.open-meteo.com/v1/forecast│
│  - No API key needed                    │
│  - Free & unlimited                     │
│  - Global coverage                      │
└─────────────────────────────────────────┘
```

## Data Flow

### 1. App Launch
```
MainActivity
    ↓
onCreate()
    ↓
checkLocationPermissions()
    ↓
requestPermissionLauncher
    ↓
setContent {
    WeatherViewModel (created with Factory)
        ↓
    init {
        loadWeatherForLocation(Rome) // default
    }
        ↓
    WeatherRepository.getWeather(lat, lon)
        ↓
    WeatherApiService.getWeather()
        ↓
    Open-Meteo API
        ↓
    WeatherResponse
        ↓
    mapToForecastItems()
    mapToAirQualityItems()
        ↓
    _uiState.emit(newState)
        ↓
    WeatherScreen observes
        ↓
    UI Updates
}
```

### 2. User Taps Location Menu
```
User taps ☰ button
    ↓
ActionBar.onLocationClick()
    ↓
showLocationDialog = true
    ↓
LocationSelectorDialog appears
    ↓
User selects "Tokyo"
    ↓
onLocationSelected(tokyo)
    ↓
viewModel.loadWeatherForLocation(tokyo)
    ↓
_uiState.value.copy(isLoading = true)
    ↓
WeatherScreen shows CircularProgressIndicator
    ↓
WeatherRepository.getWeather(tokyo.lat, tokyo.lon)
    ↓
API call → WeatherResponse
    ↓
_uiState.value.copy(
    isLoading = false,
    weatherData = response,
    forecastItems = mapped,
    airQualityItems = mapped
)
    ↓
WeatherScreen updates with Tokyo data
```

### 3. User Taps Current Location
```
User taps "Current Location" in dialog
    ↓
onCurrentLocationClick()
    ↓
viewModel.loadCurrentLocationWeather()
    ↓
LocationService.getCurrentLocation()
    ↓
FusedLocationProviderClient.getCurrentLocation()
    ↓
Result: (latitude, longitude)
    ↓
LocationService.getCityName(lat, lon)
    ↓
Geocoder.getFromLocation()
    ↓
Result: "Jakarta" (example)
    ↓
viewModel.loadWeather(lat, lon, "Jakarta")
    ↓
(same flow as above)
    ↓
UI shows Jakarta weather
```

## Component Responsibilities

### UI Layer
- **MainActivity**: App entry point, permission handling, ViewModel setup
- **WeatherScreen**: Main screen, observes state, handles user interactions
- **ActionBar**: Location display, menu button
- **DailyForecast**: Current weather card
- **AirQuality**: 6 weather metrics grid
- **WeeklyForecast**: 7-day forecast horizontal list
- **LocationSelectorDialog**: Location picker dialog

### ViewModel Layer
- **WeatherViewModel**: 
  - Manages UI state with StateFlow
  - Coordinates data fetching
  - Handles location changes
  - Error handling
  - Data transformation (API → UI models)

### Repository Layer
- **WeatherRepository**: 
  - Single source of truth for weather data
  - API call abstraction
  - Result<T> wrapper for success/failure
  - Coroutines for async operations

### Service Layer
- **LocationService**:
  - GPS location detection
  - Reverse geocoding
  - Permission checking
  - Location settings validation

### Data/API Layer
- **RetrofitClient**: 
  - HTTP client configuration
  - Moshi JSON converter
  - Logging interceptor
  - Base URL setup

- **WeatherApiService**: 
  - API endpoint definitions
  - Suspend functions
  - Query parameters

## Models

### Domain Models
```kotlin
Location(
    name: String,           // "Rome"
    latitude: Double,       // 41.9028
    longitude: Double,      // 12.4964
    isCurrentLocation: Boolean = false
)
```

### API Models
```kotlin
WeatherResponse(
    latitude: Double,
    longitude: Double,
    timezone: String,
    current: CurrentWeather,
    hourly: HourlyWeather,
    daily: DailyWeather
)

CurrentWeather(
    time: String,
    temperature: Double,
    humidity: Int,
    apparentTemperature: Double,
    weatherCode: Int,
    windSpeed: Double,
    windDirection: Int
)

DailyWeather(
    time: List<String>,
    weatherCode: List<Int>,
    temperatureMax: List<Double>,
    temperatureMin: List<Double>,
    uvIndexMax: List<Double>
)
```

### UI Models
```kotlin
ForecastItem(
    image: @DrawableRes Int,
    dayOfWeek: String,      // "Mon"
    date: String,           // "13 Feb"
    temperature: String,    // "26°"
    airQuality: String,     // "194"
    airQualityIndicatorColorHex: String,
    isSelected: Boolean
)

AirQualityItem(
    icon: @DrawableRes Int,
    title: String,          // "Real Feel"
    value: String          // "23.8"
)
```

## State Management Pattern

```kotlin
// ViewModel
private val _uiState = MutableStateFlow(WeatherUiState())
val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

// Update state
_uiState.value = _uiState.value.copy(
    isLoading = true
)

// UI observes
@Composable
fun WeatherScreen(viewModel: WeatherViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    
    if (uiState.isLoading) {
        CircularProgressIndicator()
    }
    
    Text(text = uiState.currentLocation?.name ?: "")
}
```

## Threading Model

```
Main Thread (UI)
    ↓
viewModel.loadWeather() [viewModelScope]
    ↓
withContext(Dispatchers.IO) [Repository]
    ↓
Retrofit/OkHttp (Background thread)
    ↓
Network call
    ↓
Response
    ↓
withContext(Dispatchers.Main)
    ↓
UI updates
```

## Error Handling Strategy

```kotlin
// Repository level
suspend fun getWeather(): Result<WeatherResponse> {
    return try {
        val response = apiService.getWeather()
        Result.success(response)
    } catch (e: Exception) {
        Result.failure(e)
    }
}

// ViewModel level
result.onSuccess { data ->
    _uiState.value = _uiState.value.copy(
        isLoading = false,
        weatherData = data
    )
}.onFailure { exception →
    _uiState.value = _uiState.value.copy(
        isLoading = false,
        error = exception.message
    )
}

// UI level
if (uiState.error != null) {
    Text(text = uiState.error, color = Color.Red)
}
```

## Key Design Decisions

### 1. MVVM Architecture
- **Why**: Separation of concerns, testability, reactive UI
- **Benefit**: Easy to maintain and scale

### 2. Repository Pattern
- **Why**: Single source of truth, abstraction
- **Benefit**: Easy to mock for testing, can add caching later

### 3. StateFlow over LiveData
- **Why**: Better Compose integration, Kotlin coroutines
- **Benefit**: More modern, less boilerplate

### 4. Moshi over Gson
- **Why**: Kotlin-first, codegen support, better performance
- **Benefit**: Type-safe, null-safe

### 5. FusedLocationProvider over LocationManager
- **Why**: More accurate, battery efficient, easier API
- **Benefit**: Better UX, simpler code

### 6. Open-Meteo over other weather APIs
- **Why**: Free, no API key, unlimited requests
- **Benefit**: Easy to use, reliable data

---

## 🎓 Learning Resources

Jika ingin belajar lebih dalam:

- **Retrofit**: https://square.github.io/retrofit/
- **Coroutines**: https://kotlinlang.org/docs/coroutines-guide.html
- **StateFlow**: https://developer.android.com/kotlin/flow/stateflow-and-sharedflow
- **Jetpack Compose**: https://developer.android.com/jetpack/compose
- **Location Services**: https://developer.android.com/training/location
- **Open-Meteo API**: https://open-meteo.com/en/docs

---

Architecture ini mengikuti best practices Android development dengan:
✅ Clean Architecture principles
✅ SOLID principles
✅ Reactive programming
✅ Dependency Injection ready (bisa tambah Hilt/Koin)
✅ Testable code
✅ Scalable structure

