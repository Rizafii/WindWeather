package androidlead.weatherappui.util

import android.content.Context
import androidlead.weatherappui.R
import androidlead.weatherappui.ui.screen.util.WeatherTipItem

object WeatherCodeMapper {

    fun getWeatherTips(context: Context, weatherCode: Int, temperature: Int, uvIndex: Int = 0, humidity: Int = 0): List<WeatherTipItem> {
        val tips = mutableListOf<WeatherTipItem>()

        // Temperature-based tips
        when {
            temperature >= 30 -> {
                tips.add(WeatherTipItem(R.drawable.ic_water_bottle, context.getString(R.string.tip_stay_hydrated), context.getString(R.string.tip_drink_water)))
                tips.add(WeatherTipItem(R.drawable.ic_sunscreen, context.getString(R.string.tip_use_sunscreen), context.getString(R.string.tip_protect_skin)))
                tips.add(WeatherTipItem(R.drawable.ic_hat, context.getString(R.string.tip_wear_hat), context.getString(R.string.tip_protect_heat)))
                tips.add(WeatherTipItem(R.drawable.ic_sunglasses, context.getString(R.string.tip_wear_sunglasses), context.getString(R.string.tip_protect_eyes)))
            }
            temperature >= 20 -> {
                tips.add(WeatherTipItem(R.drawable.ic_sunglasses, context.getString(R.string.tip_wear_sunglasses), context.getString(R.string.tip_nice_day)))
                tips.add(WeatherTipItem(R.drawable.ic_water_bottle, context.getString(R.string.tip_stay_hydrated), context.getString(R.string.tip_keep_drinking)))
            }
            temperature >= 10 -> {
                tips.add(WeatherTipItem(R.drawable.ic_jacket, context.getString(R.string.tip_light_jacket), context.getString(R.string.tip_bring_jacket)))
            }
            temperature < 10 -> {
                tips.add(WeatherTipItem(R.drawable.ic_warm_clothes, context.getString(R.string.tip_warm_clothes), context.getString(R.string.tip_dress_warmly)))
                tips.add(WeatherTipItem(R.drawable.ic_jacket, context.getString(R.string.tip_winter_jacket), context.getString(R.string.tip_quite_cold)))
            }
        }

        // Weather condition-based tips
        when (weatherCode) {
            // Rain conditions
            51, 53, 55, 61, 63, 65, 80, 81, 82 -> {
                tips.add(WeatherTipItem(R.drawable.ic_umbrella, context.getString(R.string.tip_bring_umbrella), context.getString(R.string.tip_rain_expected)))
                tips.add(WeatherTipItem(R.drawable.ic_jacket, context.getString(R.string.tip_waterproof), context.getString(R.string.tip_stay_dry)))
            }
            // Thunderstorm
            95, 96, 99 -> {
                tips.add(WeatherTipItem(R.drawable.ic_umbrella, context.getString(R.string.tip_bring_umbrella), context.getString(R.string.tip_storm_warning)))
                tips.add(WeatherTipItem(R.drawable.ic_jacket, context.getString(R.string.tip_stay_indoor), context.getString(R.string.tip_avoid_outdoor)))
            }
            // Fog
            45, 48 -> {
                tips.add(WeatherTipItem(R.drawable.ic_wind, context.getString(R.string.tip_drive_carefully), context.getString(R.string.tip_low_visibility)))
            }
            // Clear/Sunny
            0 -> {
                if (uvIndex > 5) {
                    tips.add(WeatherTipItem(R.drawable.ic_sunscreen, context.getString(R.string.tip_use_sunscreen), context.getString(R.string.tip_high_uv)))
                }
            }
        }

        // Humidity-based tips
        if (humidity > 70) {
            tips.add(WeatherTipItem(R.drawable.ic_mosquito, context.getString(R.string.tip_mosquito_alert), context.getString(R.string.tip_use_repellent)))
        }

        // Return at least 3 tips, add generic ones if needed
        if (tips.size < 3) {
            tips.add(WeatherTipItem(R.drawable.ic_water_bottle, context.getString(R.string.tip_stay_hydrated), context.getString(R.string.tip_drink_regularly)))
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

    fun getWeatherDescription(context: Context, weatherCode: Int): String {
        return when (weatherCode) {
            0 -> context.getString(R.string.clear_sky)
            1 -> context.getString(R.string.mainly_clear)
            2 -> context.getString(R.string.partly_cloudy)
            3 -> context.getString(R.string.overcast)
            45, 48 -> context.getString(R.string.fog)
            51, 53, 55 -> context.getString(R.string.drizzle)
            61, 63, 65 -> context.getString(R.string.rain)
            71, 73, 75, 77 -> context.getString(R.string.snow)
            80, 81, 82 -> context.getString(R.string.rain_showers)
            85, 86 -> context.getString(R.string.snow_showers)
            95 -> context.getString(R.string.thunderstorm)
            96, 99 -> context.getString(R.string.thunderstorm_with_hail)
            else -> context.getString(R.string.partly_cloudy)
        }
    }
}

