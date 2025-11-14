package androidlead.weatherappui.util

import androidlead.weatherappui.R

object WeatherCodeMapper {

    fun getWeatherIcon(weatherCode: Int): Int {
        return when (weatherCode) {
            0 -> R.drawable.img_sun // Clear sky
            1, 2, 3 -> R.drawable.img_clouds // Mainly clear, partly cloudy, and overcast
            45, 48 -> R.drawable.img_cloudy // Fog and depositing rime fog
            51, 53, 55 -> R.drawable.img_rain // Drizzle
            61, 63, 65 -> R.drawable.img_rain // Rain
            71, 73, 75 -> R.drawable.img_cloudy // Snow fall
            77 -> R.drawable.img_cloudy // Snow grains
            80, 81, 82 -> R.drawable.img_rain // Rain showers
            85, 86 -> R.drawable.img_cloudy // Snow showers
            95 -> R.drawable.img_thunder // Thunderstorm
            96, 99 -> R.drawable.img_thunder // Thunderstorm with hail
            else -> R.drawable.img_clouds
        }
    }

    fun getWeatherDescription(weatherCode: Int): String {
        return when (weatherCode) {
            0 -> "Clear sky"
            1 -> "Mainly clear"
            2 -> "Partly cloudy"
            3 -> "Overcast"
            45 -> "Foggy"
            48 -> "Depositing rime fog"
            51 -> "Light drizzle"
            53 -> "Moderate drizzle"
            55 -> "Dense drizzle"
            61 -> "Slight rain"
            63 -> "Moderate rain"
            65 -> "Heavy rain"
            71 -> "Slight snow fall"
            73 -> "Moderate snow fall"
            75 -> "Heavy snow fall"
            77 -> "Snow grains"
            80 -> "Slight rain showers"
            81 -> "Moderate rain showers"
            82 -> "Violent rain showers"
            85 -> "Slight snow showers"
            86 -> "Heavy snow showers"
            95 -> "Thunderstorm"
            96 -> "Thunderstorm with slight hail"
            99 -> "Thunderstorm with heavy hail"
            else -> "Unknown"
        }
    }
}

