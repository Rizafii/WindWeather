package androidlead.weatherappui.ui.screen.util

import androidlead.weatherappui.R
import androidx.annotation.DrawableRes

data class AirQualityItem(
    @DrawableRes val icon: Int,
    val title: String,
    val value: String
)

// Sample data for preview - actual data comes from WeatherViewModel
val AirQualityData = listOf(
    AirQualityItem(
        title = "Real Feel",
        value = "23.8°",
        icon = R.drawable.ic_real_feel
    ),
    AirQualityItem(
        title = "Wind",
        value = "9km/h",
        icon = R.drawable.ic_wind_qality,
    ),
    AirQualityItem(
        title = "Humidity",
        value = "0.9%",
        icon = R.drawable.ic_so2
    ),
    AirQualityItem(
        title = "Rain",
        value = "68%",
        icon = R.drawable.ic_rain_chance
    ),
    AirQualityItem(
        title = "UV Index",
        value = "3",
        icon = R.drawable.ic_uv_index
    ),
    AirQualityItem(
        title = "Wind Dir",
        value = "50°",
        icon = R.drawable.ic_o3
    )
)