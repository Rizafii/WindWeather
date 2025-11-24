package androidlead.weatherappui.data

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import java.util.Locale

class PreferencesManager(private val context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "weather_app_prefs"
        private const val KEY_LANGUAGE = "language"
        private const val KEY_THEME = "theme"

        const val LANGUAGE_INDONESIAN = "id"
        const val LANGUAGE_ENGLISH = "en"

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

    var language: String
        get() = sharedPreferences.getString(KEY_LANGUAGE, LANGUAGE_ENGLISH) ?: LANGUAGE_ENGLISH
        set(value) {
            val currentLanguage = language
            sharedPreferences.edit().putString(KEY_LANGUAGE, value).apply()

            // Only restart if language actually changed
            if (currentLanguage != value) {
                restartApp()
            }
        }

    var theme: String
        get() = sharedPreferences.getString(KEY_THEME, THEME_DARK) ?: THEME_DARK
        set(value) {
            sharedPreferences.edit().putString(KEY_THEME, value).apply()
            // Theme akan diimplementasikan nanti
        }

    fun applyLanguage(context: Context) {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        @Suppress("DEPRECATION")
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

    private fun restartApp() {
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)

        // Try to finish current activity if it's an Activity context
        if (context is Activity) {
            context.finish()
        }

        // Force exit to ensure restart
        Runtime.getRuntime().exit(0)
    }
}

