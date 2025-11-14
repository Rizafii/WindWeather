package androidlead.weatherappui.navigation

sealed class Screen(val route: String) {
    object Weather : Screen("weather")
    object Locations : Screen("locations")
}

