package androidlead.weatherappui.data.model

import kotlinx.serialization.Serializable

@Serializable
data class SavedLocation(
    val id: String = "",
    val name: String = "",
    val country: String = "",
    val state: String = "", // State/Province/Admin1
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val temperature: Double = 0.0,
    val weatherCondition: String = "",
    val weatherIcon: String = "",
    val isSelected: Boolean = false,
    val isCurrentLocation: Boolean = false, // Flag untuk lokasi GPS user
    val lastUpdated: Long = 0L, // Timestamp untuk cache invalidation
    val humidity: Int = 0,
    val windSpeed: Double = 0.0,
    val apparentTemperature: Double = 0.0,
    val weatherCode: Int = 0
)

