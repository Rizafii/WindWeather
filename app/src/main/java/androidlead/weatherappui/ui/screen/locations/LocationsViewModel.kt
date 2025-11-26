package androidlead.weatherappui.ui.screen.locations

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidlead.weatherappui.data.model.SavedLocation
import androidlead.weatherappui.data.repository.GeocodingRepository
import androidlead.weatherappui.data.repository.LocationRepository
import androidlead.weatherappui.data.repository.WeatherRepository
import androidlead.weatherappui.service.LocationService
import androidlead.weatherappui.util.WeatherCodeMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import java.util.UUID

data class LocationsUiState(
    val savedLocations: List<SavedLocation> = emptyList(),
    val searchQuery: String = "",
    val searchResults: List<SavedLocation> = emptyList(),
    val isSearching: Boolean = false,
    val showAddDialog: Boolean = false,
    val isLoading: Boolean = false
)

class LocationsViewModel(
    private val repository: LocationRepository,
    private val context: Context
) : ViewModel() {

    private val weatherRepository = WeatherRepository()
    private val geocodingRepository = GeocodingRepository()
    private val locationService = LocationService(context)

    private val _uiState = MutableStateFlow(LocationsUiState())
    val uiState: StateFlow<LocationsUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    companion object {
        private const val SEARCH_DELAY = 500L // milliseconds
    }

    init {
        loadSavedLocations()
    }

    private fun loadSavedLocations() {
        viewModelScope.launch {
            repository.getSavedLocations().collect { locations ->
                // First, show cached data immediately (loadless)
                _uiState.value = _uiState.value.copy(savedLocations = locations)

                // Check if this is the first launch (no saved locations)
                if (locations.isEmpty() && hasLocationPermission()) {
                    // Automatically add GPS location on first launch
                    addCurrentLocationOnFirstLaunch()
                } else {
                    // Then, update only stale locations in the background
                    val staleLocations = locations.filter { !repository.isLocationCacheValid(it) }
                    if (staleLocations.isNotEmpty()) {
                        updateStaleLocations(staleLocations)
                    }
                }
            }
        }
    }

    private suspend fun addCurrentLocationOnFirstLaunch() {
        _uiState.value = _uiState.value.copy(isLoading = true)

        val locationCoords = locationService.getCurrentLocation()

        if (locationCoords != null) {
            val (lat, lon) = locationCoords
            val cityName = locationService.getCityName(lat, lon)

            // Buat SavedLocation dengan flag isCurrentLocation
            val currentLocation = SavedLocation(
                id = "current_location",
                name = cityName,
                country = "My Location",
                latitude = lat,
                longitude = lon,
                isCurrentLocation = true
            )

            // Fetch weather data
            val locationWithWeather = updateLocationWeather(currentLocation)

            // Save ke repository
            repository.saveLocation(locationWithWeather)
        }

        _uiState.value = _uiState.value.copy(isLoading = false)
    }

    private suspend fun updateStaleLocations(staleLocations: List<SavedLocation>) {
        staleLocations.forEach { location ->
            val updatedLocation = updateLocationWeather(location)
            repository.updateLocationWeatherData(location.id, updatedLocation)
        }
    }

    private suspend fun updateLocationWeather(location: SavedLocation): SavedLocation {
        return try {
            val result = weatherRepository.getWeather(location.latitude, location.longitude)
            result.fold(
                onSuccess = { weatherResponse ->
                    location.copy(
                        temperature = weatherResponse.current.temperature,
                        weatherCondition = WeatherCodeMapper.getWeatherDescription(context, weatherResponse.current.weatherCode),
                        weatherIcon = WeatherCodeMapper.getWeatherIcon(weatherResponse.current.weatherCode).toString(),
                        humidity = weatherResponse.current.humidity,
                        windSpeed = weatherResponse.current.windSpeed,
                        apparentTemperature = weatherResponse.current.apparentTemperature,
                        weatherCode = weatherResponse.current.weatherCode,
                        lastUpdated = System.currentTimeMillis()
                    )
                },
                onFailure = {
                    location // Return location as is if failed
                }
            )
        } catch (_: Exception) {
            location // Return location as is if exception
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)

        // Cancel previous search job
        searchJob?.cancel()

        if (query.length >= 2) {
            // Start new search job with delay (debounce)
            searchJob = viewModelScope.launch {
                delay(SEARCH_DELAY)
                searchLocations(query)
            }
        } else {
            _uiState.value = _uiState.value.copy(searchResults = emptyList())
        }
    }

    private fun searchLocations(query: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSearching = true)

            // Gunakan Geocoding API untuk search lokasi
            val result = geocodingRepository.searchLocations(query)

            result.fold(
                onSuccess = { geocodingResults ->
                    // Convert geocoding results to SavedLocation
                    val locations = geocodingResults.map { result ->
                        SavedLocation(
                            id = UUID.randomUUID().toString(),
                            name = result.name,
                            country = result.country ?: "",
                            state = result.admin1 ?: "", // Add state/province
                            latitude = result.latitude,
                            longitude = result.longitude
                        )
                    }

                    // Fetch weather data untuk setiap hasil
                    val locationsWithWeather = locations.map { location ->
                        updateLocationWeather(location)
                    }

                    _uiState.value = _uiState.value.copy(
                        searchResults = locationsWithWeather,
                        isSearching = false
                    )
                },
                onFailure = {
                    _uiState.value = _uiState.value.copy(
                        searchResults = emptyList(),
                        isSearching = false
                    )
                }
            )
        }
    }

    fun addLocation(location: SavedLocation) {
        viewModelScope.launch {
            repository.saveLocation(location)
            _uiState.value = _uiState.value.copy(
                showAddDialog = false,
                searchQuery = "",
                searchResults = emptyList()
            )
        }
    }

    fun deleteLocation(locationId: String) {
        viewModelScope.launch {
            repository.deleteLocation(locationId)
        }
    }

    fun selectLocation(location: SavedLocation) {
        viewModelScope.launch {
            repository.selectLocation(location)
        }
    }

    fun refreshWeatherData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val currentLocations = _uiState.value.savedLocations
            val updatedLocations = currentLocations.map { location ->
                val updated = updateLocationWeather(location)
                // Save updated data to cache
                repository.updateLocationWeatherData(location.id, updated)
                updated
            }

            _uiState.value = _uiState.value.copy(
                savedLocations = updatedLocations,
                isLoading = false
            )
        }
    }

    fun showAddDialog() {
        _uiState.value = _uiState.value.copy(showAddDialog = true)
    }

    fun hideAddDialog() {
        _uiState.value = _uiState.value.copy(
            showAddDialog = false,
            searchQuery = "",
            searchResults = emptyList()
        )
    }

    // Fungsi untuk add current location dari GPS
    fun addCurrentLocation() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val locationCoords = locationService.getCurrentLocation()

            if (locationCoords != null) {
                val (lat, lon) = locationCoords
                val cityName = locationService.getCityName(lat, lon)

                // Buat SavedLocation dengan flag isCurrentLocation
                val currentLocation = SavedLocation(
                    id = "current_location",
                    name = cityName,
                    country = "My Location",
                    latitude = lat,
                    longitude = lon,
                    isCurrentLocation = true
                )

                // Fetch weather data
                val locationWithWeather = updateLocationWeather(currentLocation)

                // Save ke repository
                repository.saveLocation(locationWithWeather)
            }

            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun hasLocationPermission(): Boolean {
        return locationService.hasLocationPermission()
    }
}

