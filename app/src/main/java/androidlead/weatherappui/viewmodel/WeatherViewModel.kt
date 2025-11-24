package androidlead.weatherappui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidlead.weatherappui.R
import androidlead.weatherappui.data.model.Location
import androidlead.weatherappui.data.model.WeatherResponse
import androidlead.weatherappui.data.repository.WeatherRepository
import androidlead.weatherappui.service.LocationService
import androidlead.weatherappui.ui.screen.util.AirQualityItem
import androidlead.weatherappui.ui.screen.util.ForecastItem
import androidlead.weatherappui.ui.screen.util.HourlyForecastItem
import androidlead.weatherappui.util.WeatherCodeMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    val currentTemperature: String = "21",
    val currentDescription: String = "Rain showers",
    val currentDate: String = "Monday, 12 Feb",
    val feelsLike: String = "Feels like 26°",
    val currentWeatherIcon: Int = R.drawable.img_sub_rain,
    val currentWeatherVideo: Int = R.raw.video_rain,
    val error: String? = null,
    val hasLocationPermission: Boolean = false
)

class WeatherViewModel(context: Context) : ViewModel() {

    private val repository = WeatherRepository()
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
        // Load default location weather
        loadWeatherForLocation(defaultLocations[0])
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

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    weatherData = weatherResponse,
                    forecastItems = forecastItems,
                    hourlyForecastItems = hourlyForecastItems,
                    airQualityItems = airQualityItems,
                    currentTemperature = weatherResponse.daily.temperatureMax.firstOrNull()?.toInt()?.toString() ?: "21",
                    currentDescription = WeatherCodeMapper.getWeatherDescription(weatherResponse.current.weatherCode),
                    currentDate = getCurrentDate(),
                    feelsLike = "Current ${weatherResponse.current.temperature.toInt()}° • Feels like ${weatherResponse.current.apparentTemperature.toInt()}°",
                    currentWeatherIcon = WeatherCodeMapper.getWeatherIcon(weatherResponse.current.weatherCode),
                    currentWeatherVideo = WeatherCodeMapper.getWeatherVideo(weatherResponse.current.weatherCode),
                    currentLocation = _uiState.value.currentLocation?.copy(name = locationName)
                        ?: Location(locationName, latitude, longitude)
                )
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
                isSelected = index == 0
            )
        }
    }

    private fun mapToAirQualityItems(weatherResponse: WeatherResponse): List<AirQualityItem> {
        val current = weatherResponse.current
        return listOf(
            AirQualityItem(
                title = "Real Feel",
                value = "${current.apparentTemperature.toInt()}°",
                icon = R.drawable.ic_real_feel
            ),
            AirQualityItem(
                title = "Wind",
                value = "${current.windSpeed.toInt()}km/h",
                icon = R.drawable.ic_wind_qality
            ),
            AirQualityItem(
                title = "Humidity",
                value = "${current.humidity}%",
                icon = R.drawable.ic_so2
            ),
            AirQualityItem(
                title = "Rain",
                value = "0%", // Open-Meteo doesn't provide rain chance in current weather
                icon = R.drawable.ic_rain_chance
            ),
            AirQualityItem(
                title = "UV Index",
                value = weatherResponse.daily.uvIndexMax.firstOrNull()?.toInt()?.toString() ?: "0",
                icon = R.drawable.ic_uv_index
            ),
            AirQualityItem(
                title = "Wind Dir",
                value = "${current.windDirection}°",
                icon = R.drawable.ic_o3
            )
        )
    }

    private fun mapToHourlyForecastItems(weatherResponse: WeatherResponse): List<HourlyForecastItem> {
        val hourlyData = weatherResponse.hourly

        // Ambil 24 jam ke depan
        return (0 until 24).mapNotNull { index ->
            if (index < hourlyData.time.size) {
                val timeString = hourlyData.time[index]
                val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
                val calendar = Calendar.getInstance()

                try {
                    calendar.time = dateFormat.parse(timeString) ?: return@mapNotNull null

                    val hourFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                    val displayTime = hourFormat.format(calendar.time)

                    val temp = hourlyData.temperature[index].toInt()
                    val weatherCode = hourlyData.weatherCode[index]
                    val humidity = hourlyData.humidity[index]

                    HourlyForecastItem(
                        time = displayTime,
                        temperature = "$temp°",
                        weatherIcon = WeatherCodeMapper.getWeatherIcon(weatherCode),
                        humidity = humidity
                    )
                } catch (e: Exception) {
                    null
                }
            } else {
                null
            }
        }
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

    fun addLocation(location: Location) {
        val updatedLocations = _uiState.value.savedLocations + location
        _uiState.value = _uiState.value.copy(savedLocations = updatedLocations)
    }

    fun removeLocation(location: Location) {
        val updatedLocations = _uiState.value.savedLocations.filter { it != location }
        _uiState.value = _uiState.value.copy(savedLocations = updatedLocations)
    }
}

