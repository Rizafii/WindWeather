# Feature: Hourly Weather Forecast (24 Hours)

## Deskripsi
Menambahkan fitur prakiraan cuaca per jam (24 jam ke depan) yang ditampilkan di bawah Daily Forecast dengan horizontal scroll.

## Fitur yang Ditambahkan

### 1. **HourlyForecastItem Data Model**
Model data untuk item prakiraan per jam:
```kotlin
data class HourlyForecastItem(
    val time: String,           // Format: HH:mm (e.g., "14:00")
    val temperature: String,     // Format: "25°"
    val weatherIcon: Int,        // Resource ID icon cuaca
    val humidity: Int            // Kelembaban (%)
)
```

### 2. **ViewModel Enhancement**
- Menambahkan field `hourlyForecastItems: List<HourlyForecastItem>` di `WeatherUiState`
- Menambahkan fungsi `mapToHourlyForecastItems()` untuk memproses data hourly dari API
- Mengambil 24 jam ke depan dari data hourly weather API

### 3. **DailyForecast Component Update**
- Menambahkan parameter `hourlyForecasts: List<HourlyForecastItem>`
- Mengubah layout dari `ConstraintLayout` menjadi `Column` untuk menampung main card + hourly section
- Menambahkan `HourlyForecastSection` component dengan horizontal scroll
- Setiap item menampilkan:
  - ⏰ Waktu (jam:menit)
  - 🌤️ Icon cuaca (dinamis sesuai weather code)
  - 🌡️ Suhu
  - 💧 Kelembaban

## Tampilan UI

### HourlyForecastSection
- **Header**: "24-Hour Forecast"
- **Layout**: Horizontal scrollable row
- **Background**: Gradient dengan transparansi
- **Border Radius**: 20dp

### HourlyForecastItem Card
- **Width**: 70dp (fixed)
- **Background**: White dengan alpha 0.1
- **Border Radius**: 16dp
- **Spacing**: 16dp antar item
- **Content**:
  - Time (top)
  - Weather icon (40x40dp)
  - Temperature (bold)
  - Humidity with icon (bottom)

## API Data Source
Menggunakan data dari Open-Meteo API:
- Endpoint: `/hourly`
- Fields:
  - `time`: Array waktu per jam
  - `temperature_2m`: Suhu per jam
  - `weather_code`: Kode cuaca per jam
  - `relative_humidity_2m`: Kelembaban per jam

## Mapping Weather Icon
Menggunakan `WeatherCodeMapper.getWeatherIcon(weatherCode)` untuk mengkonversi weather code menjadi icon yang sesuai, sama seperti daily dan weekly forecast.

## File yang Diubah/Ditambahkan

### ✨ File Baru:
- `app/src/main/java/androidlead/weatherappui/ui/screen/util/HourlyForecastData.kt`

### ✏️ File yang Dimodifikasi:
1. **WeatherViewModel.kt**
   - Import `HourlyForecastItem`
   - Update `WeatherUiState` dengan field `hourlyForecastItems`
   - Tambah fungsi `mapToHourlyForecastItems()`
   - Update `loadWeather()` untuk mapping hourly data

2. **DailyForecast.kt**
   - Tambah import untuk horizontal scroll dan layout
   - Update function signature dengan parameter `hourlyForecasts`
   - Refactor main forecast ke `MainForecastCard` composable
   - Tambah `HourlyForecastSection` composable
   - Tambah `HourlyForecastItem` composable

3. **WeatherScreen.kt**
   - Update `DailyForecast()` call dengan parameter `hourlyForecasts = uiState.hourlyForecastItems`

## User Experience

### Interaksi:
- ✅ Scroll horizontal untuk melihat prakiraan 24 jam ke depan
- ✅ Smooth scrolling dengan `rememberScrollState()`
- ✅ Visual feedback dengan gradient background
- ✅ Icon cuaca dinamis sesuai kondisi

### Data yang Ditampilkan:
- ⏰ Jam dalam format 24 jam (HH:mm)
- 🌡️ Suhu dalam Celsius
- 🌤️ Icon cuaca sesuai kondisi (cerah, hujan, berawan, dll)
- 💧 Persentase kelembaban

## Testing Checklist

- [ ] Hourly forecast muncul di bawah Daily Forecast
- [ ] Menampilkan 24 item (24 jam)
- [ ] Icon cuaca sesuai dengan kondisi per jam
- [ ] Scroll horizontal berfungsi dengan baik
- [ ] Temperature dan humidity ditampilkan dengan benar
- [ ] Format waktu sesuai (HH:mm)
- [ ] UI responsive dan tidak lag saat scroll
- [ ] Gradient background terlihat dengan baik

## Screenshot / Preview
```
┌────────────────────────────────────┐
│   Daily Forecast Card (Main)       │
│   [Weather Icon] [Temperature]     │
└────────────────────────────────────┘
        ↓
┌────────────────────────────────────┐
│ 24-Hour Forecast                   │
│ ┌─────┐ ┌─────┐ ┌─────┐ ┌─────┐ → │
│ │14:00│ │15:00│ │16:00│ │17:00│   │
│ │ ☀️  │ │ ⛅  │ │ ☁️  │ │ 🌧️  │   │
│ │ 28° │ │ 27° │ │ 26° │ │ 25° │   │
│ │💧80%│ │💧75%│ │💧82%│ │💧90%│   │
│ └─────┘ └─────┘ └─────┘ └─────┘   │
└────────────────────────────────────┘
```

## Benefits
1. ✅ Memberikan informasi cuaca lebih detail per jam
2. ✅ Membantu user merencanakan aktivitas berdasarkan perubahan cuaca per jam
3. ✅ UI yang menarik dan mudah dipahami
4. ✅ Konsisten dengan design pattern aplikasi
5. ✅ Data real-time dari API

## Next Improvements (Optional)
- [ ] Tambahkan kecepatan angin per jam
- [ ] Tambahkan kemungkinan hujan per jam
- [ ] Highlight jam saat ini dengan warna berbeda
- [ ] Tambahkan animasi saat scroll
- [ ] Option untuk toggle antara 12h dan 24h format

