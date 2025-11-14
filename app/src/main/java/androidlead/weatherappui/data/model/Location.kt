package androidlead.weatherappui.data.model

data class Location(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val isCurrentLocation: Boolean = false
)

