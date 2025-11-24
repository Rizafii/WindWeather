package androidlead.weatherappui.ui.screen.components

import androidlead.weatherappui.R
import androidlead.weatherappui.data.model.Location
import androidlead.weatherappui.ui.theme.ColorSurface
import androidlead.weatherappui.ui.theme.ColorTextPrimary
import androidlead.weatherappui.ui.theme.ColorTextSecondary
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun LocationSelectorDialog(
    locations: List<Location>,
    currentLocation: Location?,
    onLocationSelected: (Location) -> Unit,
    onDismiss: () -> Unit,
    onCurrentLocationClick: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 500.dp),
            shape = RoundedCornerShape(24.dp),
            color = ColorSurface
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.select_location),
                    style = MaterialTheme.typography.titleLarge,
                    color = ColorTextPrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Current location button
                LocationItem(
                    location = currentLocation ?: Location(
                        name = "",
                        latitude = 0.0,
                        longitude = 0.0,
                        isCurrentLocation = true
                    ),
                    isCurrentLocation = true,
                    onClick = {
                        onCurrentLocationClick()
                        onDismiss()
                    }
                )

                Divider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = ColorTextSecondary.copy(alpha = 0.2f)
                )

                // Saved locations
                LazyColumn {
                    items(locations) { location ->
                        LocationItem(
                            location = location,
                            isCurrentLocation = false,
                            onClick = {
                                onLocationSelected(location)
                                onDismiss()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LocationItem(
    location: Location,
    isCurrentLocation: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                painter = painterResource(
                    id = if (isCurrentLocation) R.drawable.ic_control else R.drawable.ic_real_feel
                ),
                contentDescription = null,
                tint = ColorTextSecondary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = if (isCurrentLocation) stringResource(R.string.current_location) else location.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = ColorTextPrimary,
                    fontWeight = FontWeight.Medium
                )
                if (!isCurrentLocation) {
                    Text(
                        text = "${location.latitude}, ${location.longitude}",
                        style = MaterialTheme.typography.bodySmall,
                        color = ColorTextSecondary
                    )
                }
            }
        }
    }
}

