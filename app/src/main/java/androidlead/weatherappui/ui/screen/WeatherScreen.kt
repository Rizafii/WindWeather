package androidlead.weatherappui.ui.screen

import androidlead.weatherappui.ui.screen.components.ActionBar
import androidlead.weatherappui.ui.screen.components.AirQuality
import androidlead.weatherappui.ui.screen.components.DailyForecast
import androidlead.weatherappui.ui.screen.components.LocationSelectorDialog
import androidlead.weatherappui.ui.screen.components.WeeklyForecast
import androidlead.weatherappui.ui.theme.ColorBackground
import androidlead.weatherappui.viewmodel.WeatherViewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel,
    onLocationClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = ColorBackground
    ) { paddings ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddings)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.White
                )
            } else if (uiState.error != null) {
                Text(
                    text = uiState.error ?: "Unknown error",
                    color = Color.Red,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 10.dp)
                ) {
                    ActionBar(
                        location = uiState.currentLocation?.name ?: "Loading...",
                        onLocationClick = onLocationClick
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    DailyForecast(
                        forecast = uiState.currentDescription,
                        date = uiState.currentDate,
                        degree = uiState.currentTemperature,
                        description = uiState.feelsLike,
                        weatherIcon = uiState.currentWeatherIcon,
                        hourlyForecasts = uiState.hourlyForecastItems
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    AirQuality(
                        airQualityItems = uiState.airQualityItems
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    WeeklyForecast(
                        forecastItems = uiState.forecastItems
                    )
                }
            }
        }
    }
}