package androidlead.weatherappui

import android.app.Application
import androidlead.weatherappui.data.PreferencesManager

class WeatherApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize language preference
        val prefsManager = PreferencesManager.getInstance(this)

        // Apply saved language
        prefsManager.applyLanguage(this)
    }
}

