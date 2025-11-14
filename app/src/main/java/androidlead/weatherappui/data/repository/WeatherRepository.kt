package androidlead.weatherappui.data.repository

import androidlead.weatherappui.data.api.RetrofitClient
import androidlead.weatherappui.data.model.WeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeatherRepository {

    private val apiService = RetrofitClient.weatherApiService

    suspend fun getWeather(latitude: Double, longitude: Double): Result<WeatherResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getWeather(latitude, longitude)
                Result.success(response)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}

