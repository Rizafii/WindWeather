package androidlead.weatherappui.ui.screen.util

import androidx.annotation.DrawableRes

data class WeatherTipItem(
    @DrawableRes val icon: Int,
    val title: String,
    val description: String
)

