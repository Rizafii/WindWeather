package androidlead.weatherappui.data.repository

import androidlead.weatherappui.data.api.RetrofitClient
import androidlead.weatherappui.data.model.GeocodingResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeocodingRepository {

    private val apiService = RetrofitClient.geocodingApiService

    suspend fun searchLocations(query: String): Result<List<GeocodingResult>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.searchLocation(query)
                if (response.results != null) {
                    Result.success(response.results)
                } else {
                    Result.success(emptyList())
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}

