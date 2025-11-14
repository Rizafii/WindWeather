package androidlead.weatherappui.ui.screen.util

import androidx.annotation.DrawableRes

data class HourlyForecastItem(
    val time: String,
    val temperature: String,
    @DrawableRes val weatherIcon: Int,
    val humidity: Int
)

