package androidlead.weatherappui.ui.screen.locations

import androidlead.weatherappui.R
import androidlead.weatherappui.data.model.SavedLocation
import androidlead.weatherappui.ui.theme.ColorGradient3
import androidlead.weatherappui.ui.theme.ColorSurface
import androidlead.weatherappui.ui.theme.ColorTextPrimary
import androidlead.weatherappui.ui.theme.ColorTextSecondary
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

// Helper function untuk translate kondisi cuaca dengan deteksi English dan Indonesian
@Composable
private fun getTranslatedWeatherCondition(weatherCondition: String): String {
    return when {
        // Cerah/Sunny/Clear
        weatherCondition.contains("sunny", ignoreCase = true) ||
        weatherCondition.contains("clear", ignoreCase = true) ||
        weatherCondition.contains("cerah", ignoreCase = true) -> stringResource(R.string.weather_sunny)

        // Hujan/Rain
        weatherCondition.contains("rain", ignoreCase = true) ||
        weatherCondition.contains("hujan", ignoreCase = true) -> stringResource(R.string.weather_rain)

        // Gerimis/Drizzle
        weatherCondition.contains("drizzle", ignoreCase = true) ||
        weatherCondition.contains("gerimis", ignoreCase = true) -> stringResource(R.string.weather_drizzle)

        // Hujan Lokal/Shower
        weatherCondition.contains("shower", ignoreCase = true) ||
        weatherCondition.contains("hujan lokal", ignoreCase = true) -> stringResource(R.string.weather_shower)

        // Berawan/Cloudy
        weatherCondition.contains("cloudy", ignoreCase = true) ||
        weatherCondition.contains("berawan", ignoreCase = true) ||
        weatherCondition.contains("partly", ignoreCase = true) -> stringResource(R.string.weather_cloudy)

        // Mendung/Overcast
        weatherCondition.contains("overcast", ignoreCase = true) ||
        weatherCondition.contains("mendung", ignoreCase = true) -> stringResource(R.string.weather_overcast)

        // Cloud
        weatherCondition.contains("cloud", ignoreCase = true) ||
        weatherCondition.contains("awan", ignoreCase = true) -> stringResource(R.string.weather_cloud)

        // Petir/Thunder/Thunderstorm
        weatherCondition.contains("thunder", ignoreCase = true) ||
        weatherCondition.contains("petir", ignoreCase = true) -> stringResource(R.string.weather_thunder)

        // Badai/Storm
        weatherCondition.contains("storm", ignoreCase = true) ||
        weatherCondition.contains("badai", ignoreCase = true) -> stringResource(R.string.weather_storm)

        // Kabut/Fog
        weatherCondition.contains("fog", ignoreCase = true) ||
        weatherCondition.contains("kabut", ignoreCase = true) -> stringResource(R.string.weather_fog)

        // Kabut Tipis/Mist
        weatherCondition.contains("mist", ignoreCase = true) ||
        weatherCondition.contains("kabut tipis", ignoreCase = true) -> stringResource(R.string.weather_mist)

        // Berkabut/Haze
        weatherCondition.contains("haze", ignoreCase = true) ||
        weatherCondition.contains("berkabut", ignoreCase = true) -> stringResource(R.string.weather_haze)

        // Salju/Snow
        weatherCondition.contains("snow", ignoreCase = true) ||
        weatherCondition.contains("salju", ignoreCase = true) -> stringResource(R.string.weather_snow)

        // Default - return original
        else -> weatherCondition
    }
}

// Helper function untuk mendapatkan warna card berdasarkan kondisi cuaca
private fun getWeatherCardColor(weatherCondition: String): Color {
    return when {
        // Cerah/Sunny - Kuning
        weatherCondition.contains("sunny", ignoreCase = true) ||
        weatherCondition.contains("clear", ignoreCase = true) ||
        weatherCondition.contains("cerah", ignoreCase = true) -> Color(0xFFFFC900)

        // Hujan - Biru
        weatherCondition.contains("rain", ignoreCase = true) ||
        weatherCondition.contains("drizzle", ignoreCase = true) ||
        weatherCondition.contains("shower", ignoreCase = true) ||
        weatherCondition.contains("hujan", ignoreCase = true) ||
        weatherCondition.contains("gerimis", ignoreCase = true) -> Color(0xFF0062B2)

        // Berawan - Abu-abu
        weatherCondition.contains("cloudy", ignoreCase = true) ||
        weatherCondition.contains("overcast", ignoreCase = true) ||
        weatherCondition.contains("cloud", ignoreCase = true) ||
        weatherCondition.contains("berawan", ignoreCase = true) ||
        weatherCondition.contains("mendung", ignoreCase = true) ||
        weatherCondition.contains("awan", ignoreCase = true) -> Color(0xFFB0BEC5)

        // Thunderstorm - Biru gelap
        weatherCondition.contains("thunder", ignoreCase = true) ||
        weatherCondition.contains("storm", ignoreCase = true) ||
        weatherCondition.contains("petir", ignoreCase = true) ||
        weatherCondition.contains("badai", ignoreCase = true) -> Color(0xFF000B5D)

        // Fog/Mist - Abu-abu muda
        weatherCondition.contains("fog", ignoreCase = true) ||
        weatherCondition.contains("mist", ignoreCase = true) ||
        weatherCondition.contains("haze", ignoreCase = true) ||
        weatherCondition.contains("kabut", ignoreCase = true) ||
        weatherCondition.contains("berkabut", ignoreCase = true) -> Color(0xFFCFD8DC)

        // Snow - Putih kebiruan
        weatherCondition.contains("snow", ignoreCase = true) ||
        weatherCondition.contains("salju", ignoreCase = true) -> Color(0xFF56C9FF)

        // Default - Warna surface
        else -> ColorSurface.copy(alpha = 0.5f)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationsScreen(
    viewModel: LocationsViewModel,
    onNavigateBack: () -> Unit,
    onLocationSelected: (SavedLocation) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.saved_locations), color = ColorTextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = ColorTextPrimary
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.refreshWeatherData() },
                        enabled = !uiState.isLoading
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = ColorTextPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = stringResource(R.string.refresh_weather),
                                tint = ColorTextPrimary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showAddDialog() },
                containerColor = ColorSurface,
                contentColor = ColorTextPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_location))
            }
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (uiState.savedLocations.isEmpty()) {
                EmptyLocationsList()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Button untuk add current location
                    item {
                        AddCurrentLocationButton(
                            onClick = { viewModel.addCurrentLocation() },
                            hasPermission = viewModel.hasLocationPermission()
                        )
                    }

                    items(uiState.savedLocations) { location ->
                        LocationItem(
                            location = location,
                            onClick = {
                                viewModel.selectLocation(location)
                                onLocationSelected(location)
                                onNavigateBack()
                            },
                            onDelete = { viewModel.deleteLocation(location.id) }
                        )
                    }
                }
            }

            if (uiState.showAddDialog) {
                AddLocationDialog(
                    searchQuery = uiState.searchQuery,
                    searchResults = uiState.searchResults,
                    isSearching = uiState.isSearching,
                    onSearchQueryChange = { viewModel.onSearchQueryChange(it) },
                    onLocationSelect = { viewModel.addLocation(it) },
                    onDismiss = { viewModel.hideAddDialog() }
                )
            }
        }
    }
}

@Composable
private fun AddCurrentLocationButton(
    onClick: () -> Unit,
    hasPermission: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = ColorGradient3
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = stringResource(R.string.add_current_location),
                    modifier = Modifier.size(32.dp),
                    tint = Color.White
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(R.string.use_gps_location),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (hasPermission) stringResource(R.string.add_current_location) else stringResource(R.string.location_permission_required),
                    style = MaterialTheme.typography.bodySmall,
                    color = ColorTextSecondary
                )
            }

            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(R.string.add_location),
                tint = Color.White
            )
        }
    }
}

@Composable
private fun EmptyLocationsList() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_location_pin),
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = ColorTextSecondary
            )
            Text(
                text = stringResource(R.string.no_locations),
                style = MaterialTheme.typography.bodyLarge,
                color = ColorTextSecondary
            )
            Text(
                text = stringResource(R.string.add_first_location),
                style = MaterialTheme.typography.bodyMedium,
                color = ColorTextSecondary
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LocationItem(
    location: SavedLocation,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var showDelete by remember { mutableStateOf(false) }
    val cardColor = getWeatherCardColor(location.weatherCondition)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    if (showDelete) {
                        showDelete = false
                    } else {
                        onClick()
                    }
                },
                onLongClick = {
                    showDelete = !showDelete
                }
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Weather Icon atau Pin Map untuk current location
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    if (location.isCurrentLocation) {
                        // Pin map icon untuk lokasi user
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Current Location",
                            modifier = Modifier.size(32.dp),
                            tint = ColorGradient3
                        )
                    } else {
                        // Weather icon untuk lokasi lain
                        Image(
                            painter = painterResource(id = R.drawable.img_sun),
                            contentDescription = null,
                            modifier = Modifier.size(50.dp)
                        )
                    }
                }

                // Location Info
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            text = location.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                shadow = Shadow(
                                    color = Color.Black.copy(alpha = 0.5f),
                                    offset = androidx.compose.ui.geometry.Offset(2f, 2f),
                                    blurRadius = 1f
                                )
                            ),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                        )
                        // Badge untuk current location
                        if (location.isCurrentLocation) {
                            Text(
                                text = "GPS",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                modifier = Modifier
                                    .background(
                                        color = ColorGradient3,
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp),
                                fontSize = 10.sp
                            )
                        }
                    }
                    Text(
                        text = location.country,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.4f),
                                offset = androidx.compose.ui.geometry.Offset(1f, 1f),
                                blurRadius = 1f
                            )
                        ),
                        color = ColorTextSecondary
                    )
                    Text(
                        text = getTranslatedWeatherCondition(location.weatherCondition),
                        style = MaterialTheme.typography.bodySmall.copy(
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.4f),
                                offset = androidx.compose.ui.geometry.Offset(1f, 1f),
                                blurRadius = 1f
                            )
                        ),
                        color = ColorTextSecondary
                    )
                }
            }

            // Temperature and Delete
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${location.temperature.toInt()}°",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.5f),
                            offset = androidx.compose.ui.geometry.Offset(2f, 2f),
                            blurRadius = 1f
                        )
                    ),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                // Delete button hanya muncul saat long press
                if (showDelete) {
                    IconButton(onClick = {
                        onDelete()
                        showDelete = false
                    }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = ColorTextSecondary
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddLocationDialog(
    searchQuery: String,
    searchResults: List<SavedLocation>,
    isSearching: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onLocationSelect: (SavedLocation) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 600.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = ColorSurface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Header dengan Gradient
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                                colors = listOf(
                                    ColorGradient3,
                                    Color(0xFF5E72E4)
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Add Location",
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Search for cities worldwide",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .background(
                                    color = Color.White.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Search Field dengan styling baru
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        modifier = Modifier
                            .fillMaxWidth(),
                        placeholder = {
                            Text(
                                "Search city name...",
                                color = ColorTextSecondary.copy(alpha = 0.6f)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = null,
                                tint = ColorGradient3,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { onSearchQueryChange("") }) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Clear",
                                        tint = ColorTextSecondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = ColorTextPrimary,
                            unfocusedTextColor = ColorTextPrimary,
                            focusedBorderColor = ColorGradient3,
                            unfocusedBorderColor = ColorGradient3,
                            cursorColor = ColorGradient3
                        ),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true
                    )

                    // Info text
                    if (searchQuery.isEmpty()) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_location_pin),
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = ColorTextPrimary.copy(alpha = 0.6f)
                            )
                            Text(
                                text = "Type at least 2 characters to search",
                                style = MaterialTheme.typography.bodySmall,
                                color = ColorTextPrimary.copy(alpha = 0.6f)
                            )
                        }
                    }

                    // Search Results
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 200.dp, max = 350.dp)
                    ) {
                        when {
                            isSearching -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        CircularProgressIndicator(
                                            color = ColorGradient3,
                                            strokeWidth = 3.dp
                                        )
                                        Text(
                                            text = "Searching locations...",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = ColorTextPrimary.copy(alpha = 0.6f)
                                        )
                                    }
                                }
                            }
                            searchResults.isNotEmpty() -> {
                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    items(searchResults) { location ->
                                        SearchResultItem(
                                            location = location,
                                            onClick = { onLocationSelect(location) }
                                        )
                                    }
                                }
                            }
                            searchQuery.length >= 2 -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Search,
                                            contentDescription = null,
                                            modifier = Modifier.size(48.dp),
                                            tint = ColorTextSecondary.copy(alpha = 0.3f)
                                        )
                                        Text(
                                            text = "No locations found",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = ColorTextSecondary,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = "Try a different search term",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = ColorTextSecondary.copy(alpha = 0.7f)
                                        )
                                    }
                                }
                            }
                            else -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_location_pin),
                                            contentDescription = null,
                                            modifier = Modifier.size(64.dp),
                                            tint = ColorTextPrimary.copy(alpha = 0.3f)
                                        )
                                        Text(
                                            text = "Start typing to search",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = ColorTextPrimary.copy(alpha = 0.6f),
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = "Find cities from around the world",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = ColorTextPrimary.copy(alpha = 0.6f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchResultItem(
    location: SavedLocation,
    onClick: () -> Unit
) {
    val cardColor = getWeatherCardColor(location.weatherCondition)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardColor.copy(alpha = 0.15f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp,
            pressedElevation = 4.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Weather Icon dengan background
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = cardColor.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_sun),
                        contentDescription = null,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Location info
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = location.name,
                        style = MaterialTheme.typography.bodyLarge,
                        color = ColorTextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_location_pin),
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = ColorTextPrimary.copy(alpha = 0.6f)
                        )
                        Text(
                            text = location.country,
                            style = MaterialTheme.typography.bodyMedium,
                            color = ColorTextPrimary.copy(alpha = 0.6f)
                        )
                    }
                    if (location.weatherCondition.isNotEmpty()) {
                        Text(
                            text = location.weatherCondition,
                            style = MaterialTheme.typography.bodySmall,
                            color = ColorTextPrimary.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            // Temperature dengan styling
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "${location.temperature.toInt()}°",
                    style = MaterialTheme.typography.headlineMedium,
                    color = ColorTextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Box(
                    modifier = Modifier
                        .background(
                            color = ColorGradient3.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "Add",
                        style = MaterialTheme.typography.labelSmall,
                        color = ColorGradient3,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

