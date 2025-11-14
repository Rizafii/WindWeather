package androidlead.weatherappui.navigation

import androidlead.weatherappui.data.repository.LocationRepository
import androidlead.weatherappui.ui.screen.WeatherScreen
import androidlead.weatherappui.ui.screen.locations.LocationsScreen
import androidlead.weatherappui.ui.screen.locations.LocationsViewModel
import androidlead.weatherappui.viewmodel.WeatherViewModel
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun WeatherNavHost(
    weatherViewModel: WeatherViewModel,
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val locationRepository = LocationRepository(context)

    NavHost(
        navController = navController,
        startDestination = Screen.Weather.route
    ) {
        composable(Screen.Weather.route) {
            WeatherScreen(
                viewModel = weatherViewModel,
                onLocationClick = {
                    navController.navigate(Screen.Locations.route)
                }
            )
        }

        composable(Screen.Locations.route) {
            val locationsViewModel = LocationsViewModel(locationRepository, context)
            LocationsScreen(
                viewModel = locationsViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onLocationSelected = { location ->
                    // Update weather for selected location
                    weatherViewModel.fetchWeatherByCoordinates(
                        location.latitude,
                        location.longitude,
                        location.name
                    )
                }
            )
        }
    }
}

