# Fix Air Quality & Video Background Update

## Masalah yang Diperbaiki

### 1. **Air Quality tidak update saat hari dipilih**
Air Quality tetap menampilkan data hari pertama (hari ini) meskipun user sudah memilih hari lain di weekly forecast.

### 2. **Video Background mungkin tidak konsisten**
Video background harus selalu sesuai dengan kondisi cuaca hari yang dipilih.

## Solusi yang Diterapkan

### 1. **Update `mapToAirQualityItems()` Function**

Menambahkan parameter `dayIndex: Int = 0` sehingga fungsi dapat menerima index hari yang dipilih.

#### Perubahan Logic:
- **Real Feel**: Menghitung rata-rata temperature dari data hourly untuk hari yang dipilih
- **Wind**: Menggunakan data current (karena API tidak menyediakan wind data per hari)
- **Humidity**: Menghitung rata-rata humidity dari data hourly untuk hari yang dipilih
- **Rain**: Menggunakan 0% (API tidak menyediakan precipitation probability)
- **UV Index**: Mengambil dari `dailyData.uvIndexMax[dayIndex]` - data UV yang tepat untuk hari tersebut
- **Wind Direction**: Menggunakan data current (karena API tidak menyediakan wind direction per hari)

#### Filter Data per Hari:
```kotlin
hourlyData.time.forEachIndexed { index, timeString ->
    if (timeString.startsWith(targetDate)) {
        // Hanya ambil data hourly yang match dengan tanggal yang dipilih
        avgTemp += hourlyData.temperature[index]
        avgHumidity += hourlyData.humidity[index]
        count++
    }
}
```

### 2. **Update `selectDay()` Function**

Menambahkan update untuk `airQualityItems`:

```kotlin
// Update air quality for selected day
val airQualityItems = mapToAirQualityItems(weatherData, dayIndex)

_uiState.value = _uiState.value.copy(
    // ...existing updates...
    airQualityItems = airQualityItems,
    // ...
)
```

### 3. **Video Background Already Fixed**

Video background sudah di-update dalam `selectDay()` function:
```kotlin
currentWeatherVideo = WeatherCodeMapper.getWeatherVideo(selectedWeatherCode)
```

Dan juga di `loadWeather()` saat pertama kali load data.

## Hasil Setelah Fix

### Saat User Memilih Hari Tertentu:

✅ **Daily Forecast** - Menampilkan suhu, description, dan tanggal hari yang dipilih

✅ **Hourly Forecast** - Menampilkan 24 jam prakiraan untuk hari yang dipilih

✅ **Air Quality** - Sekarang update dengan data yang sesuai:
  - Real Feel: Average temperature hari tersebut
  - Humidity: Average humidity hari tersebut
  - UV Index: UV Index maksimal hari tersebut
  - Wind & Wind Dir: Data current (karena API limitation)

✅ **Weekly Forecast** - Visual selection dengan glass morphism effect

✅ **Video Background** - Berubah sesuai weather code hari yang dipilih:
  - Clear sky → video_forecast
  - Cloudy → video_cloudy
  - Rain → video_rain
  - Storm → video_storm

## Data yang Sekarang Dinamis per Hari

| Component | Data Source | Status |
|-----------|-------------|--------|
| Temperature | `dailyData.temperatureMax[dayIndex]` | ✅ Dynamic |
| Description | `WeatherCodeMapper.getWeatherDescription(weatherCode[dayIndex])` | ✅ Dynamic |
| Date | Formatted from `dailyData.time[dayIndex]` | ✅ Dynamic |
| High/Low | `temperatureMax[dayIndex]` & `temperatureMin[dayIndex]` | ✅ Dynamic |
| Weather Icon | `WeatherCodeMapper.getWeatherIcon(weatherCode[dayIndex])` | ✅ Dynamic |
| Video Background | `WeatherCodeMapper.getWeatherVideo(weatherCode[dayIndex])` | ✅ Dynamic |
| Hourly Forecast | Filtered by `targetDate` | ✅ Dynamic |
| Real Feel | Average from hourly data of selected day | ✅ Dynamic |
| Humidity | Average from hourly data of selected day | ✅ Dynamic |
| UV Index | `dailyData.uvIndexMax[dayIndex]` | ✅ Dynamic |
| Wind Speed | Current weather (API limitation) | ⚠️ Static |
| Wind Direction | Current weather (API limitation) | ⚠️ Static |

## API Limitations

Beberapa data tidak tersedia per hari di API Open-Meteo:
- ❌ Wind speed per day
- ❌ Wind direction per day  
- ❌ Precipitation probability

Solusi: Menggunakan data current untuk wind, dan 0% untuk rain probability.

## Testing

1. Jalankan aplikasi
2. Tunggu data cuaca dimuat
3. Klik pada hari yang berbeda di weekly forecast
4. Verify bahwa semua data berubah:
   - ✅ Temperature card menampilkan suhu hari yang dipilih
   - ✅ Description sesuai kondisi cuaca hari tersebut
   - ✅ Hourly forecast menampilkan jam-jam dari hari yang dipilih
   - ✅ Air Quality menampilkan data yang berubah (Real Feel, Humidity, UV Index)
   - ✅ Video background berubah sesuai kondisi cuaca
   - ✅ Card weekly forecast menunjukkan selection dengan visual yang jelas

## Summary

✅ Air Quality sekarang **dinamis** berdasarkan hari yang dipilih
✅ Video Background sekarang **konsisten** dengan kondisi cuaca yang ditampilkan
✅ Semua komponen UI **sinkron** dengan hari yang dipilih user
✅ User experience lebih **cohesive** dan **informatif**

