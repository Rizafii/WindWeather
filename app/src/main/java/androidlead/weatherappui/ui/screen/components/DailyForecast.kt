package androidlead.weatherappui.ui.screen.components

import androidlead.weatherappui.R
import androidlead.weatherappui.ui.screen.util.HourlyForecastItem
import androidlead.weatherappui.ui.theme.ColorGradient1
import androidlead.weatherappui.ui.theme.ColorGradient2
import androidlead.weatherappui.ui.theme.ColorGradient3
import androidlead.weatherappui.ui.theme.ColorTextSecondary
import androidlead.weatherappui.ui.theme.ColorTextSecondaryVariant
import androidlead.weatherappui.ui.theme.ColorWindForecast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.compose.ui.tooling.preview.Preview

@Preview(
    showBackground = true,
    backgroundColor = 0xFF101010,
    showSystemUi = false
)
@Composable
fun DailyForecastPreview() {
    val sampleHourly = listOf(
        HourlyForecastItem(time = "09:00", temperature = "22°", humidity = 80, weatherIcon = R.drawable.img_sub_rain),
        HourlyForecastItem(time = "10:00", temperature = "23°", humidity = 78, weatherIcon = R.drawable.img_sub_rain),
        HourlyForecastItem(time = "11:00", temperature = "24°", humidity = 75, weatherIcon = R.drawable.img_sub_rain),
        HourlyForecastItem(time = "12:00", temperature = "25°", humidity = 70, weatherIcon = R.drawable.img_sub_rain),
    )

    DailyForecast(
        forecast = "Rain Showers",
        date = "Monday, 12 Feb",
        degree = "21",
        description = "Feels like 26°",
        weatherIcon = R.drawable.img_sub_rain,
        hourlyForecasts = sampleHourly
    )
}


@Composable
fun DailyForecast(
    modifier: Modifier = Modifier,
    forecast: String = "Rain showers",
    date: String = "Monday, 12 Feb",
    degree: String = "21",
    description: String = "Feels like 26°",
    weatherIcon: Int = R.drawable.img_sub_rain,
    hourlyForecasts: List<HourlyForecastItem> = emptyList()
) {
    MainForecastCard(
        modifier = modifier,
        forecast = forecast,
        date = date,
        degree = degree,
        description = description,
        weatherIcon = weatherIcon,
        hourlyForecasts = hourlyForecasts
    )
}

@Composable
private fun MainForecastCard(
    modifier: Modifier = Modifier,
    forecast: String,
    date: String,
    degree: String,
    description: String,
    weatherIcon: Int,
    hourlyForecasts: List<HourlyForecastItem>
) {
    ConstraintLayout(
        modifier = modifier.fillMaxWidth()
    ) {
        val (forecastImage, forecastValue, windImage, title, descriptionText, hourlySection, background) = createRefs()

        CardBackground(
            modifier = Modifier.constrainAs(background) {
                linkTo(
                    start = parent.start,
                    end = parent.end,
                    top = parent.top,
                    bottom = if (hourlyForecasts.isNotEmpty()) hourlySection.bottom else descriptionText.bottom,
                    topMargin = 24.dp,
                    bottomMargin = if (hourlyForecasts.isNotEmpty()) 24.dp else 0.dp
                )
                height = Dimension.fillToConstraints
            }
        )

        Image(
            painter = painterResource(weatherIcon),
            contentDescription = null,
            contentScale = ContentScale.FillHeight,
            modifier = Modifier
                .height(175.dp)
                .constrainAs(forecastImage) {
                    start.linkTo(anchor = parent.start, margin = 4.dp)
                    top.linkTo(parent.top)
                }
        )

        Text(
            text = forecast,
            style = MaterialTheme.typography.titleLarge,
            color = ColorTextSecondary,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.constrainAs(title) {
                start.linkTo(anchor = parent.start, margin = 24.dp)
                top.linkTo(anchor = forecastImage.bottom)
            }
        )

        Text(
            text = date,
            style = MaterialTheme.typography.bodyMedium,
            color = ColorTextSecondaryVariant,
            modifier = Modifier
                .constrainAs(descriptionText) {
                    start.linkTo(anchor = title.start)
                    top.linkTo(anchor = title.bottom)
                }
                .padding(bottom = if (hourlyForecasts.isNotEmpty()) 16.dp else 24.dp)
        )

        ForecastValue(
            modifier = Modifier.constrainAs(forecastValue) {
                end.linkTo(anchor = parent.end, margin = 24.dp)
                top.linkTo(forecastImage.top)
                bottom.linkTo(forecastImage.bottom)
            },
            degree = degree,
            description = description
        )

        WindForecastImage(
            modifier = Modifier.constrainAs(windImage) {
                linkTo(
                    top = title.top,
                    bottom = title.bottom
                )
                end.linkTo(anchor = parent.end, margin = 24.dp)
            }
        )

        // Hourly forecast section di dalam card yang sama
        if (hourlyForecasts.isNotEmpty()) {
            HourlyForecastSection(
                hourlyForecasts = hourlyForecasts,
                modifier = Modifier.constrainAs(hourlySection) {
                    start.linkTo(parent.start, margin = 24.dp)
                    end.linkTo(parent.end, margin = 24.dp)
                    top.linkTo(descriptionText.bottom)
                    width = Dimension.fillToConstraints
                }
            )
        }
    }
}

@Composable
private fun CardBackground(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(
                    0f to ColorGradient1,
                    0.5f to ColorGradient2,
                    1f to ColorGradient3
                ),
                shape = RoundedCornerShape(32.dp)
            )
    )
}

@Composable
private fun ForecastValue(
    modifier: Modifier = Modifier,
    degree: String = "21",
    description: String = "Feels like 26°"
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start
    ) {
        Box(
            contentAlignment = Alignment.TopEnd
        ) {
            Text(
                text = degree,
                letterSpacing = 0.sp,
                style = TextStyle(
                    brush = Brush.verticalGradient(
                        0f to Color.White,
                        1f to Color.White.copy(alpha = 0.3f)
                    ),
                    fontSize = 80.sp,
                    fontWeight = FontWeight.Black
                ),
                modifier = Modifier.padding(end = 16.dp)
            )
            Text(
                text = "°",
                style = TextStyle(
                    brush = Brush.verticalGradient(
                        0f to Color.White,
                        1f to Color.White.copy(alpha = 0.3f)
                    ),
                    fontSize = 70.sp,
                    fontWeight = FontWeight.Light,
                ),
                modifier = Modifier.padding(top = 2.dp)
            )
        }
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = ColorTextSecondaryVariant
        )
    }
}

@Composable
private fun WindForecastImage(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_frosty),
            contentDescription = null,
            modifier = Modifier.size(60.dp),
            tint = ColorWindForecast
        )
        Icon(
            painter = painterResource(R.drawable.ic_wind),
            contentDescription = null,
            modifier = Modifier.size(60.dp),
            tint = ColorWindForecast
        )
    }
}

@Composable
private fun HourlyForecastSection(
    hourlyForecasts: List<HourlyForecastItem>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .padding(bottom =24.dp)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        hourlyForecasts.forEach { item ->
            HourlyForecastItem(item = item)
        }
    }
}

@Composable
private fun HourlyForecastItem(
    item: HourlyForecastItem
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(65.dp)
            .padding(vertical = 10.dp, horizontal = 6.dp)
    ) {
        Text(
            text = item.time,
            style = MaterialTheme.typography.bodySmall,
            color = ColorTextSecondaryVariant,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Image(
            painter = painterResource(item.weatherIcon),
            contentDescription = null,
            modifier = Modifier.size(32.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = item.temperature,
            style = MaterialTheme.typography.titleSmall,
            color = ColorTextSecondary,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(3.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_so2),
                contentDescription = null,
                modifier = Modifier.size(10.dp),
                tint = ColorTextSecondaryVariant.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = "${item.humidity}%",
                style = MaterialTheme.typography.bodySmall,
                color = ColorTextSecondaryVariant,
                fontSize = 9.sp
            )
        }
    }
}


