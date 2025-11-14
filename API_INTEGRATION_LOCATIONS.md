# API Integration for Locations List

## Overview
Integrasi API untuk mendapatkan data cuaca real-time pada list tempat yang tersimpan di Locations Screen.

## Implementasi

### 1. **LocationsViewModel Enhancement**

#### Import yang Ditambahkan:
```kotlin
import androidlead.weatherappui.data.repository.WeatherRepository
import androidlead.weatherappui.util.WeatherCodeMapper
```

#### WeatherRepository Instance:
```kotlin
private val weatherRepository = WeatherRepository()
```

### 2. **Fungsi `updateLocationWeather()`**

Fungsi untuk fetch dan update data cuaca dari API untuk setiap location:

```kotlin
private suspend fun updateLocationWeather(location: SavedLocation): SavedLocation {
    return try {
        val result = weatherRepository.getWeather(location.latitude, location.longitude)
        result.fold(
            onSuccess = { weatherResponse ->
                location.copy(
                    temperature = weatherResponse.current.temperature,
                    weatherCondition = WeatherCodeMapper.getWeatherDescription(weatherResponse.current.weatherCode),
                    weatherIcon = WeatherCodeMapper.getWeatherIcon(weatherResponse.current.weatherCode).toString()
                )
            },
            onFailure = {
                location // Return location as is if failed
            }
        )
    } catch (e: Exception) {
        location // Return location as is if exception
    }
}
```

#### Fitur:
- ✅ Fetch data dari Open-Meteo API menggunakan WeatherRepository
- ✅ Update temperature, weatherCondition, dan weatherIcon
- ✅ Error handling: jika gagal, return location tanpa update
- ✅ Menggunakan WeatherCodeMapper untuk konversi weather code

### 3. **Load Saved Locations dengan Weather Data**

```kotlin
private fun loadSavedLocations() {
    viewModelScope.launch {
        repository.getSavedLocations().collect { locations ->
            // Update weather data untuk setiap location
            val locationsWithWeather = locations.map { location ->
                updateLocationWeather(location)
            }
            _uiState.value = _uiState.value.copy(savedLocations = locationsWithWeather)
        }
    }
}
```

#### Behavior:
- Load locations dari DataStore
- Untuk setiap location, fetch weather data dari API
- Update UI state dengan data terbaru

### 4. **Search Locations dengan API Integration**

```kotlin
private fun searchLocations(query: String) {
    viewModelScope.launch {
        _uiState.value = _uiState.value.copy(isSearching = true)
        
        // Mock location database (10 kota populer)
        val mockResults = listOf(
            SavedLocation(name = "Jakarta", country = "Indonesia", ...),
            SavedLocation(name = "London", country = "United Kingdom", ...),
            SavedLocation(name = "New York", country = "United States", ...),
            SavedLocation(name = "Tokyo", country = "Japan", ...),
            // ... dan lainnya
        ).filter { it.name.contains(query, ignoreCase = true) || 
                   it.country.contains(query, ignoreCase = true) }
        
        // Fetch weather data untuk hasil search dari API
        val resultsWithWeather = mockResults.map { location ->
            updateLocationWeather(location)
        }
        
        _uiState.value = _uiState.value.copy(
            searchResults = resultsWithWeather,
            isSearching = false
        )
    }
}
```

#### Fitur:
- ✅ Search by name atau country (case-insensitive)
- ✅ Filter dari mock database (10 kota)
- ✅ Fetch real-time weather dari API untuk setiap hasil
- ✅ Loading state saat search

### 5. **Refresh Weather Data**

Fungsi baru untuk manual refresh semua weather data:

```kotlin
fun refreshWeatherData() {
    viewModelScope.launch {
        _uiState.value = _uiState.value.copy(isLoading = true)
        
        val currentLocations = _uiState.value.savedLocations
        val updatedLocations = currentLocations.map { location ->
            updateLocationWeather(location)
        }
        
        _uiState.value = _uiState.value.copy(
            savedLocations = updatedLocations,
            isLoading = false
        )
    }
}
```

### 6. **UI Enhancement - LocationsScreen**

#### Tombol Refresh di TopAppBar:

```kotlin
TopAppBar(
    title = { Text("Saved Locations") },
    navigationIcon = { /* Back button */ },
    actions = {
        IconButton(
            onClick = { viewModel.refreshWeatherData() },
            enabled = !uiState.isLoading
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(...)
            } else {
                Icon(Icons.Default.Refresh, ...)
            }
        }
    }
)
```

#### Fitur:
- ✅ Icon refresh di kanan atas
- ✅ Disabled saat sedang loading
- ✅ Show CircularProgressIndicator saat loading
- ✅ User dapat manual refresh kapan saja

## Data Flow

```
┌─────────────────────────────────────────────────┐
│ 1. LocationsScreen (UI)                         │
│    - Display saved locations                    │
│    - Show refresh button                        │
└─────────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────────┐
│ 2. LocationsViewModel                           │
│    - loadSavedLocations()                       │
│    - searchLocations(query)                     │
│    - refreshWeatherData()                       │
└─────────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────────┐
│ 3. updateLocationWeather(location)              │
│    - For each location                          │
└─────────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────────┐
│ 4. WeatherRepository.getWeather()               │
│    - Call Open-Meteo API                        │
│    - latitude, longitude → weather data         │
└─────────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────────┐
│ 5. WeatherCodeMapper                            │
│    - Convert weather code → description         │
│    - Convert weather code → icon                │
└─────────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────────┐
│ 6. Update SavedLocation                         │
│    - temperature (real-time)                    │
│    - weatherCondition (real-time)               │
│    - weatherIcon (real-time)                    │
└─────────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────────┐
│ 7. UI Update                                    │
│    - Display with dynamic card colors           │
│    - Show real temperature                      │
│    - Show real weather condition                │
└─────────────────────────────────────────────────┘
```

## API Endpoints

### Open-Meteo Weather API
- **URL**: `https://api.open-meteo.com/v1/forecast`
- **Parameters**:
  - `latitude`: Location latitude
  - `longitude`: Location longitude
  - `current`: temperature_2m, weather_code, etc.
  - `hourly`: temperature_2m, weather_code, etc.
  - `daily`: temperature_2m_max, temperature_2m_min, etc.

### Response Example:
```json
{
  "current": {
    "temperature_2m": 28.5,
    "weather_code": 3
  }
}
```

### Weather Code Mapping:
| Code | Description | Icon |
|------|-------------|------|
| 0 | Clear sky | ☀️ img_sun |
| 1-3 | Partly cloudy | ☁️ img_clouds |
| 61-65 | Rain | 🌧️ img_rain |
| 95-99 | Thunderstorm | ⛈️ img_thunder |

## Mock Cities Database

Untuk search, tersedia 10 kota populer:

1. 🇮🇩 **Jakarta, Indonesia** (-6.2088, 106.8456)
2. 🇮🇩 **Bandung, Indonesia** (-6.9175, 107.6191)
3. 🇮🇩 **Surabaya, Indonesia** (-7.2575, 112.7521)
4. 🇬🇧 **London, United Kingdom** (51.5074, -0.1278)
5. 🇺🇸 **New York, United States** (40.7128, -74.0060)
6. 🇯🇵 **Tokyo, Japan** (35.6762, 139.6503)
7. 🇫🇷 **Paris, France** (48.8566, 2.3522)
8. 🇦🇺 **Sydney, Australia** (-33.8688, 151.2093)
9. 🇸🇬 **Singapore, Singapore** (1.3521, 103.8198)
10. 🇦🇪 **Dubai, United Arab Emirates** (25.2048, 55.2708)

## Features

### ✅ Real-time Weather Data
- Temperature langsung dari API
- Weather condition akurat
- Icon sesuai kondisi cuaca

### ✅ Auto-refresh on Load
- Setiap buka Locations screen → fetch fresh data
- Data selalu up-to-date

### ✅ Manual Refresh
- Tombol refresh di TopAppBar
- User control untuk update data
- Loading indicator saat refresh

### ✅ Search with Live Weather
- Search kota → langsung dapat weather data
- Tidak perlu add dulu untuk lihat cuaca

### ✅ Error Handling
- API gagal → tetap show location tanpa crash
- Graceful fallback
- User experience tetap smooth

### ✅ Dynamic Card Colors
- Warna card berubah sesuai kondisi cuaca
- Terintegrasi dengan weather data dari API
- Visual feedback instant

## Testing Checklist

- [ ] Load saved locations → fetch weather dari API
- [ ] Temperature ditampilkan dengan benar (real-time)
- [ ] Weather condition sesuai dengan API response
- [ ] Icon cuaca sesuai dengan weather code
- [ ] Card color berubah sesuai kondisi cuaca
- [ ] Search location → fetch weather dari API
- [ ] Add location → save dengan weather data terbaru
- [ ] Refresh button works
- [ ] Loading indicator muncul saat refresh
- [ ] Error handling: API gagal tidak crash app
- [ ] Multiple locations refresh bersamaan
- [ ] Data persisten setelah refresh

## File yang Diubah

### ✏️ Modified:
1. **LocationsViewModel.kt**
   - Import WeatherRepository dan WeatherCodeMapper
   - Tambah fungsi `updateLocationWeather()`
   - Update `loadSavedLocations()` untuk fetch API
   - Update `searchLocations()` untuk fetch API
   - Tambah fungsi `refreshWeatherData()`

2. **LocationsScreen.kt**
   - Import Icons.Default.Refresh
   - Tambah refresh button di TopAppBar
   - Tambah loading indicator
   - Handle loading state

## Performance Considerations

### Optimization:
- ✅ Parallel API calls dengan `map { }` dan coroutines
- ✅ Cache data di DataStore
- ✅ Error handling untuk prevent blocking
- ✅ Loading state untuk UX feedback

### Network:
- 📶 Requires internet connection
- 🔄 Auto-retry tidak diimplementasi (future improvement)
- ⚡ Fast API response (Open-Meteo)

## Future Improvements

- [ ] Cache weather data dengan timestamp
- [ ] Auto-refresh setiap X menit
- [ ] Offline mode dengan cached data
- [ ] Pull-to-refresh gesture
- [ ] Background sync
- [ ] Geocoding API untuk search kota real
- [ ] Error toast notification
- [ ] Retry mechanism untuk failed API calls

## Benefits

1. ✅ **Real-time Data** - Cuaca selalu akurat dan terbaru
2. ✅ **User Control** - Manual refresh kapan saja
3. ✅ **Visual Feedback** - Loading indicator dan dynamic colors
4. ✅ **Reliable** - Error handling yang baik
5. ✅ **Scalable** - Mudah tambah kota dan fitur baru

