package androidlead.weatherappui.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidlead.weatherappui.data.model.SavedLocation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "locations")

class LocationRepository(private val context: Context) {
    private val SAVED_LOCATIONS_KEY = stringPreferencesKey("saved_locations")
    private val SELECTED_LOCATION_KEY = stringPreferencesKey("selected_location")

    // Cache validity duration: 30 minutes
    private val CACHE_VALIDITY_DURATION = 30 * 60 * 1000L // 30 minutes in milliseconds

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    fun getSavedLocations(): Flow<List<SavedLocation>> {
        return context.dataStore.data.map { preferences ->
            val locationsJson = preferences[SAVED_LOCATIONS_KEY] ?: "[]"
            try {
                json.decodeFromString<List<SavedLocation>>(locationsJson)
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    fun getSelectedLocation(): Flow<SavedLocation?> {
        return context.dataStore.data.map { preferences ->
            val locationJson = preferences[SELECTED_LOCATION_KEY]
            locationJson?.let {
                try {
                    json.decodeFromString<SavedLocation>(it)
                } catch (e: Exception) {
                    null
                }
            }
        }
    }

    suspend fun saveLocation(location: SavedLocation) {
        context.dataStore.edit { preferences ->
            val currentLocations = getSavedLocationsSync(preferences)
            val locationWithTimestamp = location.copy(lastUpdated = System.currentTimeMillis())
            val updatedLocations = if (currentLocations.any { it.id == location.id }) {
                currentLocations.map { if (it.id == location.id) locationWithTimestamp else it }
            } else {
                currentLocations + locationWithTimestamp
            }
            preferences[SAVED_LOCATIONS_KEY] = json.encodeToString(updatedLocations)
        }
    }

    suspend fun updateLocationWeatherData(locationId: String, weatherData: SavedLocation) {
        context.dataStore.edit { preferences ->
            val currentLocations = getSavedLocationsSync(preferences)
            val updatedLocations = currentLocations.map { location ->
                if (location.id == locationId) {
                    weatherData.copy(
                        id = location.id,
                        name = location.name,
                        country = location.country,
                        state = location.state,
                        latitude = location.latitude,
                        longitude = location.longitude,
                        isSelected = location.isSelected,
                        isCurrentLocation = location.isCurrentLocation,
                        lastUpdated = System.currentTimeMillis()
                    )
                } else {
                    location
                }
            }
            preferences[SAVED_LOCATIONS_KEY] = json.encodeToString(updatedLocations)
        }
    }

    suspend fun deleteLocation(locationId: String) {
        context.dataStore.edit { preferences ->
            val currentLocations = getSavedLocationsSync(preferences)
            val updatedLocations = currentLocations.filter { it.id != locationId }
            preferences[SAVED_LOCATIONS_KEY] = json.encodeToString(updatedLocations)
        }
    }

    suspend fun selectLocation(location: SavedLocation) {
        context.dataStore.edit { preferences ->
            val locationWithTimestamp = location.copy(lastUpdated = System.currentTimeMillis())
            preferences[SELECTED_LOCATION_KEY] = json.encodeToString(locationWithTimestamp)
        }
    }

    // Check if location cache is still valid
    fun isLocationCacheValid(location: SavedLocation): Boolean {
        val currentTime = System.currentTimeMillis()
        val cacheAge = currentTime - location.lastUpdated
        return cacheAge < CACHE_VALIDITY_DURATION
    }

    // Get location that needs weather update
    suspend fun getLocationsNeedingUpdate(): List<SavedLocation> {
        var result: List<SavedLocation> = emptyList()
        context.dataStore.data.map { preferences ->
            val locations = getSavedLocationsSync(preferences)
            result = locations.filter { !isLocationCacheValid(it) }
        }
        return result
    }

    private fun getSavedLocationsSync(preferences: Preferences): List<SavedLocation> {
        val locationsJson = preferences[SAVED_LOCATIONS_KEY] ?: "[]"
        return try {
            json.decodeFromString<List<SavedLocation>>(locationsJson)
        } catch (e: Exception) {
            emptyList()
        }
    }
}

