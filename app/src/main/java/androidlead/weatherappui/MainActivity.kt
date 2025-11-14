package androidlead.weatherappui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidlead.weatherappui.navigation.WeatherNavHost
import androidlead.weatherappui.ui.theme.WeatherAppUiTheme
import androidlead.weatherappui.viewmodel.WeatherViewModel
import androidlead.weatherappui.viewmodel.WeatherViewModelFactory
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel as composeViewModel

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Check if all permissions are granted
        if (permissions.values.all { it }) {
            // Permission granted - ViewModel will automatically handle location fetch if needed
        }
        // If permission denied, app will use default location (Rome)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Check and request location permissions
        checkLocationPermissions()

        setContent {
            WeatherAppUiTheme {
                val viewModel: WeatherViewModel = composeViewModel(
                    factory = WeatherViewModelFactory(applicationContext)
                )
                WeatherNavHost(weatherViewModel = viewModel)
            }
        }
    }

    private fun checkLocationPermissions() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        val hasPermissions = permissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }

        if (!hasPermissions) {
            requestPermissionLauncher.launch(permissions)
        }
    }

}
