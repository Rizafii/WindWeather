package androidlead.weatherappui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidlead.weatherappui.R
import androidlead.weatherappui.data.model.Location
import androidlead.weatherappui.data.model.SavedLocation
import androidlead.weatherappui.data.model.WeatherResponse
import androidlead.weatherappui.data.repository.LocationRepository
import androidlead.weatherappui.data.repository.WeatherRepository
import androidlead.weatherappui.service.LocationService
import androidlead.weatherappui.ui.screen.util.AirQualityItem
import androidlead.weatherappui.ui.screen.util.ForecastItem
import androidlead.weatherappui.ui.screen.util.HourlyForecastItem
import androidlead.weatherappui.ui.screen.util.WeatherTipItem
import androidlead.weatherappui.util.WeatherCodeMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class WeatherUiState(
    val isLoading: Boolean = false,
    val currentLocation: Location? = null,
    val savedLocations: List<Location> = emptyList(),
    val weatherData: WeatherResponse? = null,
    val forecastItems: List<ForecastItem> = emptyList(),
    val hourlyForecastItems: List<HourlyForecastItem> = emptyList(),
    val airQualityItems: List<AirQualityItem> = emptyList(),
    val weatherTips: List<WeatherTipItem> = emptyList(),
    val currentTemperature: String = "21",
    val currentDescription: String = "Rain showers",
    val currentDate: String = "Monday, 12 Feb",
    val feelsLike: String = "Feels like 26°",
    val currentWeatherIcon: Int = R.drawable.img_sub_rain,
    val currentWeatherVideo: Int = R.raw.video_rain,
    val error: String? = null,
    val hasLocationPermission: Boolean = false,
    val selectedDayIndex: Int = 0
)

class WeatherViewModel(private val context: Context) : ViewModel() {

    private val repository = WeatherRepository()
    private val locationRepository = LocationRepository(context)
    private val locationService = LocationService(context)

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    // Default locations
    private val defaultLocations = listOf(
        Location("Rome", 41.9028, 12.4964),
        Location("New York", 40.7128, -74.0060),
        Location("Tokyo", 35.6762, 139.6503),
        Location("London", 51.5074, -0.1278),
        Location("Paris", 48.8566, 2.3522)
    )

    init {
        _uiState.value = _uiState.value.copy(
            savedLocations = defaultLocations,
            hasLocationPermission = locationService.hasLocationPermission()
        )
        // Load from cache first, then fetch if needed
        loadFromCacheOrFetch()
    }

    private fun loadFromCacheOrFetch() {
        viewModelScope.launch {
            // Try to load selected location from cache
            val cachedLocation = locationRepository.getSelectedLocation().first()
            val savedLocations = locationRepository.getSavedLocations().first()

            // Check if this is first launch (no saved locations and no cached selected location)
            if (savedLocations.isEmpty() && cachedLocation == null && locationService.hasLocationPermission()) {
                // First launch with permission - automatically load GPS location
                loadCurrentLocationWeatherAndSave()
            } else if (cachedLocation != null && locationRepository.isLocationCacheValid(cachedLocation)) {
                // Use cached data (loadless)
                loadFromCachedLocation(cachedLocation)
            } else if (cachedLocation != null) {
                // Show cached data first, then refresh
                loadFromCachedLocation(cachedLocation)
                loadWeather(cachedLocation.latitude, cachedLocation.longitude, cachedLocation.name)
            } else if (savedLocations.isNotEmpty()) {
                // Load first saved location if no selected location
                val firstLocation = savedLocations.first()
                loadWeather(firstLocation.latitude, firstLocation.longitude, firstLocation.name)
            } else {
                // No cache, no saved locations - load default location
                loadWeatherForLocation(defaultLocations[0])
            }
        }
    }

    private suspend fun loadCurrentLocationWeatherAndSave() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        val locationCoords = locationService.getCurrentLocation()
        if (locationCoords != null) {
            val (lat, lon) = locationCoords
            val cityName = locationService.getCityName(lat, lon)
            val currentLocation = Location(cityName, lat, lon, isCurrentLocation = true)

            _uiState.value = _uiState.value.copy(currentLocation = currentLocation)

            // Load weather and automatically save to saved locations
            loadWeatherAndSaveAsCurrentLocation(lat, lon, cityName)
        } else {
            // If GPS fails, fall back to default location
            _uiState.value = _uiState.value.copy(isLoading = false)
            loadWeatherForLocation(defaultLocations[0])
        }
    }

    private fun loadFromCachedLocation(location: SavedLocation) {
        val calendar = Calendar.getInstance()
        val formattedDate = SimpleDateFormat("EEEE, dd MMM", Locale.getDefault()).format(calendar.time)

        _uiState.value = _uiState.value.copy(
            isLoading = false,
            currentLocation = Location(location.name, location.latitude, location.longitude),
            currentTemperature = location.temperature.toInt().toString(),
            currentDescription = location.weatherCondition,
            currentDate = formattedDate,
            feelsLike = context.getString(R.string.feels_like) + " ${location.apparentTemperature.toInt()}°",
            currentWeatherIcon = WeatherCodeMapper.getWeatherIcon(location.weatherCode),
            currentWeatherVideo = WeatherCodeMapper.getWeatherVideo(location.weatherCode)
        )
    }

    fun checkAndRequestLocation() {
        if (locationService.hasLocationPermission()) {
            _uiState.value = _uiState.value.copy(hasLocationPermission = true)
            loadCurrentLocationWeather()
        } else {
            _uiState.value = _uiState.value.copy(hasLocationPermission = false)
        }
    }

    fun loadCurrentLocationWeather() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val locationCoords = locationService.getCurrentLocation()
            if (locationCoords != null) {
                val (lat, lon) = locationCoords
                val cityName = locationService.getCityName(lat, lon)
                val currentLocation = Location(cityName, lat, lon, isCurrentLocation = true)

                _uiState.value = _uiState.value.copy(currentLocation = currentLocation)
                loadWeather(lat, lon, cityName)
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to get current location"
                )
            }
        }
    }

    fun loadWeatherForLocation(location: Location) {
        loadWeather(location.latitude, location.longitude, location.name)
    }

    fun fetchWeatherByCoordinates(latitude: Double, longitude: Double, locationName: String) {
        loadWeather(latitude, longitude, locationName)
    }

    private fun loadWeather(latitude: Double, longitude: Double, locationName: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val result = repository.getWeather(latitude, longitude)

            result.onSuccess { weatherResponse ->
                val forecastItems = mapToForecastItems(weatherResponse)
                val airQualityItems = mapToAirQualityItems(weatherResponse)
                val hourlyForecastItems = mapToHourlyForecastItems(weatherResponse)

                // Get current date formatted
                val calendar = Calendar.getInstance()
                val formattedDate = SimpleDateFormat("EEEE, dd MMM", Locale.getDefault()).format(calendar.time)

                // Generate weather tips
                val weatherTips = WeatherCodeMapper.getWeatherTips(
                    context = context,
                    weatherCode = weatherResponse.current.weatherCode,
                    temperature = weatherResponse.current.temperature.toInt(),
                    uvIndex = weatherResponse.daily.uvIndexMax.firstOrNull()?.toInt() ?: 0,
                    humidity = weatherResponse.current.humidity
                )

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    weatherData = weatherResponse,
                    forecastItems = forecastItems,
                    hourlyForecastItems = hourlyForecastItems,
                    airQualityItems = airQualityItems,
                    weatherTips = weatherTips,
                    currentTemperature = weatherResponse.current.temperature.toInt().toString(),
                    currentDescription = WeatherCodeMapper.getWeatherDescription(context, weatherResponse.current.weatherCode),
                    currentDate = formattedDate,
                    feelsLike = context.getString(R.string.feels_like) + " ${weatherResponse.current.apparentTemperature.toInt()}°",
                    currentWeatherIcon = WeatherCodeMapper.getWeatherIcon(weatherResponse.current.weatherCode),
                    currentWeatherVideo = WeatherCodeMapper.getWeatherVideo(weatherResponse.current.weatherCode),
                    currentLocation = _uiState.value.currentLocation?.copy(name = locationName)
                        ?: Location(locationName, latitude, longitude)
                )

                // Save to cache for loadless experience next time
                val cachedLocation = SavedLocation(
                    id = "selected_location",
                    name = locationName,
                    latitude = latitude,
                    longitude = longitude,
                    temperature = weatherResponse.current.temperature,
                    weatherCondition = WeatherCodeMapper.getWeatherDescription(context, weatherResponse.current.weatherCode),
                    weatherIcon = WeatherCodeMapper.getWeatherIcon(weatherResponse.current.weatherCode).toString(),
                    humidity = weatherResponse.current.humidity,
                    windSpeed = weatherResponse.current.windSpeed,
                    apparentTemperature = weatherResponse.current.apparentTemperature,
                    weatherCode = weatherResponse.current.weatherCode,
                    lastUpdated = System.currentTimeMillis()
                )
                locationRepository.selectLocation(cachedLocation)
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = exception.message ?: "Failed to load weather data"
                )
            }
        }
    }

    private fun loadWeatherAndSaveAsCurrentLocation(latitude: Double, longitude: Double, locationName: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val result = repository.getWeather(latitude, longitude)

            result.onSuccess { weatherResponse ->
                val forecastItems = mapToForecastItems(weatherResponse)
                val airQualityItems = mapToAirQualityItems(weatherResponse)
                val hourlyForecastItems = mapToHourlyForecastItems(weatherResponse)

                // Get current date formatted
                val calendar = Calendar.getInstance()
                val formattedDate = SimpleDateFormat("EEEE, dd MMM", Locale.getDefault()).format(calendar.time)

                // Generate weather tips
                val weatherTips = WeatherCodeMapper.getWeatherTips(
                    context = context,
                    weatherCode = weatherResponse.current.weatherCode,
                    temperature = weatherResponse.current.temperature.toInt(),
                    uvIndex = weatherResponse.daily.uvIndexMax.firstOrNull()?.toInt() ?: 0,
                    humidity = weatherResponse.current.humidity
                )

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    weatherData = weatherResponse,
                    forecastItems = forecastItems,
                    hourlyForecastItems = hourlyForecastItems,
                    airQualityItems = airQualityItems,
                    weatherTips = weatherTips,
                    currentTemperature = weatherResponse.current.temperature.toInt().toString(),
                    currentDescription = WeatherCodeMapper.getWeatherDescription(context, weatherResponse.current.weatherCode),
                    currentDate = formattedDate,
                    feelsLike = context.getString(R.string.feels_like) + " ${weatherResponse.current.apparentTemperature.toInt()}°",
                    currentWeatherIcon = WeatherCodeMapper.getWeatherIcon(weatherResponse.current.weatherCode),
                    currentWeatherVideo = WeatherCodeMapper.getWeatherVideo(weatherResponse.current.weatherCode),
                    currentLocation = _uiState.value.currentLocation?.copy(name = locationName)
                        ?: Location(locationName, latitude, longitude)
                )

                // Save to both selected location cache AND saved locations with isCurrentLocation flag
                val currentLocationData = SavedLocation(
                    id = "current_location",
                    name = locationName,
                    country = "My Location",
                    latitude = latitude,
                    longitude = longitude,
                    temperature = weatherResponse.current.temperature,
                    weatherCondition = WeatherCodeMapper.getWeatherDescription(context, weatherResponse.current.weatherCode),
                    weatherIcon = WeatherCodeMapper.getWeatherIcon(weatherResponse.current.weatherCode).toString(),
                    humidity = weatherResponse.current.humidity,
                    windSpeed = weatherResponse.current.windSpeed,
                    apparentTemperature = weatherResponse.current.apparentTemperature,
                    weatherCode = weatherResponse.current.weatherCode,
                    isCurrentLocation = true,
                    lastUpdated = System.currentTimeMillis()
                )

                // Save as selected location
                locationRepository.selectLocation(currentLocationData)

                // Also save to saved locations list
                locationRepository.saveLocation(currentLocationData)

            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = exception.message ?: "Failed to load weather data"
                )
            }
        }
    }

    private fun mapToForecastItems(weatherResponse: WeatherResponse): List<ForecastItem> {
        val dailyData = weatherResponse.daily
        val selectedIndex = _uiState.value.selectedDayIndex
        return dailyData.time.mapIndexed { index, date ->
            val calendar = Calendar.getInstance()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            calendar.time = dateFormat.parse(date) ?: Date()

            val dayOfWeek = SimpleDateFormat("EEE", Locale.getDefault()).format(calendar.time)
            val dateStr = SimpleDateFormat("dd MMM", Locale.getDefault()).format(calendar.time)

            val weatherCode = dailyData.weatherCode[index]
            val maxTemp = dailyData.temperatureMax[index].toInt()
            val uvIndex = dailyData.uvIndexMax.getOrNull(index)?.toInt() ?: 0

            ForecastItem(
                image = WeatherCodeMapper.getWeatherIcon(weatherCode),
                dayOfWeek = dayOfWeek,
                date = dateStr,
                temperature = "$maxTemp°",
                airQuality = uvIndex.toString(),
                airQualityIndicatorColorHex = getAirQualityColor(uvIndex),
                isSelected = index == selectedIndex
            )
        }
    }

    private fun mapToAirQualityItems(weatherResponse: WeatherResponse, dayIndex: Int = 0): List<AirQualityItem> {
        val dailyData = weatherResponse.daily
        val hourlyData = weatherResponse.hourly
        val current = weatherResponse.current

        // Get data for selected day
        val targetDate = if (dayIndex < dailyData.time.size) {
            dailyData.time[dayIndex]
        } else {
            dailyData.time[0]
        }

        // Calculate average temperature and humidity from hourly data for the selected day
        var avgTemp = 0.0
        var avgHumidity = 0
        var count = 0

        hourlyData.time.forEachIndexed { index, timeString ->
            if (timeString.startsWith(targetDate)) {
                avgTemp += hourlyData.temperature[index]
                avgHumidity += hourlyData.humidity[index]
                count++
            }
        }

        if (count > 0) {
            avgTemp /= count
            avgHumidity /= count
        } else {
            // Fallback to using min/max temp average
            avgTemp = (dailyData.temperatureMax[dayIndex] + dailyData.temperatureMin[dayIndex]) / 2
            avgHumidity = current.humidity
        }

        // Get UV Index for the selected day
        val uvIndex = dailyData.uvIndexMax.getOrNull(dayIndex)?.toInt() ?: 0

        return listOf(
            AirQualityItem(
                title = context.getString(R.string.real_feel),
                value = "${avgTemp.toInt()}°",
                icon = R.drawable.ic_real_feel
            ),
            AirQualityItem(
                title = context.getString(R.string.wind),
                value = "${current.windSpeed.toInt()}${context.getString(R.string.kmh)}",
                icon = R.drawable.ic_wind_qality
            ),
            AirQualityItem(
                title = context.getString(R.string.humidity),
                value = "$avgHumidity${context.getString(R.string.percent)}",
                icon = R.drawable.ic_so2
            ),
            AirQualityItem(
                title = context.getString(R.string.rain),
                value = "0${context.getString(R.string.percent)}",
                icon = R.drawable.ic_rain_chance
            ),
            AirQualityItem(
                title = context.getString(R.string.uv_index),
                value = uvIndex.toString(),
                icon = R.drawable.ic_uv_index
            ),
            AirQualityItem(
                title = context.getString(R.string.wind_dir),
                value = "${current.windDirection}°",
                icon = R.drawable.ic_o3
            )
        )
    }

    private fun mapToHourlyForecastItems(weatherResponse: WeatherResponse, dayIndex: Int = 0): List<HourlyForecastItem> {
        val hourlyData = weatherResponse.hourly
        val dailyData = weatherResponse.daily

        // Get the target date
        val targetDate = if (dayIndex < dailyData.time.size) {
            dailyData.time[dayIndex]
        } else {
            dailyData.time[0]
        }

        // Filter hourly data for the selected day and get up to 24 hours
        val filteredHours = mutableListOf<HourlyForecastItem>()

        hourlyData.time.forEachIndexed { index, timeString ->
            if (filteredHours.size >= 24) return@forEachIndexed

            if (timeString.startsWith(targetDate)) {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
                val calendar = Calendar.getInstance()

                try {
                    calendar.time = dateFormat.parse(timeString) ?: return@forEachIndexed

                    val hourFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                    val displayTime = hourFormat.format(calendar.time)

                    val temp = hourlyData.temperature[index].toInt()
                    val weatherCode = hourlyData.weatherCode[index]
                    val humidity = hourlyData.humidity[index]

                    filteredHours.add(
                        HourlyForecastItem(
                            time = displayTime,
                            temperature = "$temp°",
                            weatherIcon = WeatherCodeMapper.getWeatherIcon(weatherCode),
                            humidity = humidity
                        )
                    )
                } catch (e: Exception) {
                    // Skip this hour if parsing fails
                }
            }
        }

        return filteredHours
    }

    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("EEEE, dd MMM", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    private fun getAirQualityColor(uvIndex: Int): String {
        return when {
            uvIndex <= 2 -> "#2dbe8d" // Green - Low
            uvIndex <= 5 -> "#f9cf5f" // Yellow - Moderate
            uvIndex <= 7 -> "#ff9966" // Orange - High
            else -> "#ff7676" // Red - Very High
        }
    }

    fun selectDay(dayIndex: Int) {
        val weatherData = _uiState.value.weatherData ?: return

        // Update selected day index
        _uiState.value = _uiState.value.copy(selectedDayIndex = dayIndex)

        // Update forecast items with new selection
        val forecastItems = mapToForecastItems(weatherData)

        // Update current weather display based on selected day
        val dailyData = weatherData.daily
        if (dayIndex < dailyData.time.size) {
            val selectedWeatherCode = dailyData.weatherCode[dayIndex]
            val selectedMaxTemp = dailyData.temperatureMax[dayIndex].toInt()
            val selectedMinTemp = dailyData.temperatureMin[dayIndex].toInt()
            val selectedDate = dailyData.time[dayIndex]

            // Format the selected date
            val calendar = Calendar.getInstance()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            calendar.time = dateFormat.parse(selectedDate) ?: Date()
            val formattedDate = SimpleDateFormat("EEEE, dd MMM", Locale.getDefault()).format(calendar.time)

            // Update hourly forecast for selected day
            val hourlyForecastItems = mapToHourlyForecastItems(weatherData, dayIndex)

            // Update air quality for selected day
            val airQualityItems = mapToAirQualityItems(weatherData, dayIndex)

            // Generate weather tips for selected day
            val weatherTips = WeatherCodeMapper.getWeatherTips(
                context = context,
                weatherCode = selectedWeatherCode,
                temperature = selectedMaxTemp,
                uvIndex = dailyData.uvIndexMax.getOrNull(dayIndex)?.toInt() ?: 0,
                humidity = weatherData.current.humidity
            )

            _uiState.value = _uiState.value.copy(
                forecastItems = forecastItems,
                hourlyForecastItems = hourlyForecastItems,
                airQualityItems = airQualityItems,
                weatherTips = weatherTips,
                currentTemperature = selectedMaxTemp.toString(),
                currentDescription = WeatherCodeMapper.getWeatherDescription(context, selectedWeatherCode),
                currentDate = formattedDate,
                feelsLike = "High $selectedMaxTemp° • Low $selectedMinTemp°",
                currentWeatherIcon = WeatherCodeMapper.getWeatherIcon(selectedWeatherCode),
                currentWeatherVideo = WeatherCodeMapper.getWeatherVideo(selectedWeatherCode)
            )
        }
    }

    fun addLocation(location: Location) {
        val updatedLocations = _uiState.value.savedLocations + location
        _uiState.value = _uiState.value.copy(savedLocations = updatedLocations)
    }

    fun removeLocation(location: Location) {
        val updatedLocations = _uiState.value.savedLocations.filter { it != location }
        _uiState.value = _uiState.value.copy(savedLocations = updatedLocations)
    }
}

