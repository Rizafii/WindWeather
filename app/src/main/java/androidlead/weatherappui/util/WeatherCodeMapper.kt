package androidlead.weatherappui.util

import androidlead.weatherappui.R
import androidlead.weatherappui.ui.screen.util.WeatherTipItem

object WeatherCodeMapper {

    fun getWeatherTips(weatherCode: Int, temperature: Int, uvIndex: Int = 0, humidity: Int = 0): List<WeatherTipItem> {
        val tips = mutableListOf<WeatherTipItem>()

        // Temperature-based tips
        when {
            temperature >= 30 -> {
                tips.add(WeatherTipItem(R.drawable.ic_water_bottle, "Stay Hydrated", "Drink plenty of water"))
                tips.add(WeatherTipItem(R.drawable.ic_sunscreen, "Use Sunscreen", "Protect your skin"))
                tips.add(WeatherTipItem(R.drawable.ic_hat, "Wear Hat", "Protect from heat"))
                tips.add(WeatherTipItem(R.drawable.ic_sunglasses, "Wear Sunglasses", "Protect your eyes"))
            }
            temperature >= 20 -> {
                tips.add(WeatherTipItem(R.drawable.ic_sunglasses, "Wear Sunglasses", "Nice sunny day"))
                tips.add(WeatherTipItem(R.drawable.ic_water_bottle, "Stay Hydrated", "Keep drinking water"))
            }
            temperature >= 10 -> {
                tips.add(WeatherTipItem(R.drawable.ic_jacket, "Light Jacket", "Bring a light jacket"))
            }
            temperature < 10 -> {
                tips.add(WeatherTipItem(R.drawable.ic_warm_clothes, "Warm Clothes", "Dress warmly"))
                tips.add(WeatherTipItem(R.drawable.ic_jacket, "Winter Jacket", "It's quite cold"))
            }
        }

        // Weather condition-based tips
        when (weatherCode) {
            // Rain conditions
            51, 53, 55, 61, 63, 65, 80, 81, 82 -> {
                tips.add(WeatherTipItem(R.drawable.ic_umbrella, "Bring Umbrella", "Rain expected"))
                tips.add(WeatherTipItem(R.drawable.ic_jacket, "Waterproof Jacket", "Stay dry"))
            }
            // Thunderstorm
            95, 96, 99 -> {
                tips.add(WeatherTipItem(R.drawable.ic_umbrella, "Bring Umbrella", "Storm warning"))
                tips.add(WeatherTipItem(R.drawable.ic_jacket, "Stay Indoor", "Avoid outdoor activities"))
            }
            // Fog
            45, 48 -> {
                tips.add(WeatherTipItem(R.drawable.ic_wind, "Drive Carefully", "Low visibility"))
            }
            // Clear/Sunny
            0 -> {
                if (uvIndex > 5) {
                    tips.add(WeatherTipItem(R.drawable.ic_sunscreen, "Use Sunscreen", "High UV index"))
                }
            }
        }

        // Humidity-based tips
        if (humidity > 70) {
            tips.add(WeatherTipItem(R.drawable.ic_mosquito, "Mosquito Alert", "Use repellent"))
        }

        // Return at least 3 tips, add generic ones if needed
        if (tips.size < 3) {
            tips.add(WeatherTipItem(R.drawable.ic_water_bottle, "Stay Hydrated", "Drink water regularly"))
        }

        return tips.take(6) // Maximum 6 tips
    }

    fun getWeatherVideo(weatherCode: Int): Int {
        return when (weatherCode) {
            0 -> R.raw.video_forecast // Clear sky - use forecast video for sunny day
            1, 2, 3 -> R.raw.video_cloudy // Mainly clear, partly cloudy, and overcast
            45, 48 -> R.raw.video_cloudy // Fog
            51, 53, 55 -> R.raw.video_rain // Drizzle
            61, 63, 65 -> R.raw.video_rain // Rain
            71, 73, 75 -> R.raw.video_cloudy // Snow fall
            77 -> R.raw.video_cloudy // Snow grains
            80, 81, 82 -> R.raw.video_rain // Rain showers
            85, 86 -> R.raw.video_cloudy // Snow showers
            95 -> R.raw.video_storm // Thunderstorm
            96, 99 -> R.raw.video_storm // Thunderstorm with hail
            else -> R.raw.video_cloudy
        }
    }

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

