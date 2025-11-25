package androidlead.weatherappui.data

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(private val context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "weather_app_prefs"
        private const val KEY_THEME = "theme"

        const val THEME_DARK = "dark"
        const val THEME_LIGHT = "light"

        @Volatile
        private var instance: PreferencesManager? = null

        fun getInstance(context: Context): PreferencesManager {
            return instance ?: synchronized(this) {
                instance ?: PreferencesManager(context.applicationContext).also { instance = it }
            }
        }
    }

    var theme: String
        get() = sharedPreferences.getString(KEY_THEME, THEME_DARK) ?: THEME_DARK
        set(value) {
            sharedPreferences.edit().putString(KEY_THEME, value).apply()
            // Theme akan diimplementasikan nanti
        }
}

