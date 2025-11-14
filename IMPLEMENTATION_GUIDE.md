# Weather App UI - Open-Meteo API Integration

Aplikasi cuaca berbasis Android dengan integrasi **Open-Meteo API**, **GPS Location**, dan **Multi-Location Support**.

## 🌟 Fitur Baru

### 1. **Open-Meteo API Integration**
- Real-time weather data dari Open-Meteo API (gratis, tanpa API key)
- Data cuaca meliputi:
  - Temperatur saat ini dan feels like
  - Kondisi cuaca (cerah, hujan, mendung, dll)
  - Kecepatan dan arah angin
  - Kelembaban udara
  - UV Index
  - Forecast 7 hari ke depan

### 2. **GPS/Location Services**
- Deteksi lokasi otomatis menggunakan GPS
- Reverse geocoding untuk mendapatkan nama kota
- Request permission otomatis untuk lokasi
- Fallback ke lokasi default jika GPS tidak tersedia

### 3. **Multi-Location Support**
- Pilih dari lokasi yang tersimpan (Rome, New York, Tokyo, London, Paris)
- Tambahkan lokasi custom
- Switch antar lokasi dengan mudah
- Dialog pemilih lokasi yang user-friendly

## 📁 Struktur Proyek

```
app/src/main/java/androidlead/weatherappui/
├── data/
│   ├── api/
│   │   ├── RetrofitClient.kt          # Retrofit client setup
│   │   └── WeatherApiService.kt       # API service interface
│   ├── model/
│   │   ├── Location.kt                # Location data model
│   │   └── WeatherResponse.kt         # Weather API response models
│   └── repository/
│       └── WeatherRepository.kt       # Data repository layer
├── service/
│   └── LocationService.kt             # GPS/Location services
├── ui/
│   ├── screen/
│   │   ├── WeatherScreen.kt           # Main weather screen
│   │   └── components/
│   │       ├── ActionBar.kt           # Top bar with location
│   │       ├── DailyForecast.kt       # Daily weather card
│   │       ├── AirQuality.kt          # Air quality indicators
│   │       ├── WeeklyForecast.kt      # 7-day forecast
│   │       └── LocationSelectorDialog.kt # Location picker
│   └── theme/                         # App theme & colors
├── util/
│   └── WeatherCodeMapper.kt           # Map weather codes to icons
├── viewmodel/
│   ├── WeatherViewModel.kt            # ViewModel with state
│   └── WeatherViewModelFactory.kt     # ViewModel factory
└── MainActivity.kt                     # Main activity
```

## 🔧 Dependencies Yang Ditambahkan

```toml
# Networking
retrofit = "2.9.0"
okhttp = "4.12.0"
moshi = "1.15.0"

# Coroutines
coroutines = "1.7.3"

# Location Services
play-services-location = "21.3.0"

# ViewModel
lifecycle-viewmodel-compose = "2.8.4"
```

## 🚀 Cara Build & Run

### Prerequisites
1. Android Studio (Hedgehog atau lebih baru)
2. JDK 17 atau lebih baru
3. Android SDK (API level 28+)

### Langkah-langkah

1. **Clone repository:**
   ```bash
   git clone <repository-url>
   cd WeatherAppUi-master
   ```

2. **Buka project di Android Studio:**
   - File → Open → Pilih folder WeatherAppUi-master

3. **Sync Gradle:**
   - Klik "Sync Now" jika muncul notifikasi
   - Atau: File → Sync Project with Gradle Files

4. **Build project:**
   ```bash
   ./gradlew build
   ```

5. **Run di emulator atau device:**
   - Klik tombol Run (▶) di Android Studio
   - Atau: Shift + F10 (Windows/Linux) / Control + R (Mac)

## 📱 Permission yang Diperlukan

Aplikasi akan request permission berikut saat pertama kali dijalankan:
- `ACCESS_FINE_LOCATION` - Untuk GPS presisi tinggi
- `ACCESS_COARSE_LOCATION` - Untuk lokasi network-based
- `INTERNET` - Untuk fetch data dari API

## 🗺️ Cara Menggunakan

### Mendapatkan Cuaca Lokasi Saat Ini
1. Buka aplikasi
2. Izinkan permission lokasi saat diminta
3. Tap icon menu (☰) di kiri atas
4. Pilih "Current Location"

### Memilih Lokasi Lain
1. Tap icon menu (☰) di kiri atas
2. Pilih salah satu lokasi yang tersedia:
   - Rome, Italy
   - New York, USA
   - Tokyo, Japan
   - London, UK
   - Paris, France

### Data yang Ditampilkan
- **Current Weather**: Temperatur, kondisi cuaca, feels like
- **Air Quality**: Real feel, Wind speed, Humidity, UV Index
- **Weekly Forecast**: Prediksi cuaca 7 hari dengan temperatur dan UV index

## 🌐 Open-Meteo API

API yang digunakan: https://api.open-meteo.com/

### Keunggulan:
- ✅ Gratis tanpa API key
- ✅ No rate limiting
- ✅ Data akurat dari berbagai sumber meteorologi
- ✅ Coverage global
- ✅ Open source

### Endpoint yang digunakan:
```
GET https://api.open-meteo.com/v1/forecast
?latitude={lat}
&longitude={lon}
&current=temperature_2m,relative_humidity_2m,apparent_temperature,weather_code,wind_speed_10m,wind_direction_10m
&hourly=temperature_2m,weather_code,relative_humidity_2m
&daily=weather_code,temperature_2m_max,temperature_2m_min,uv_index_max
&timezone=auto
&forecast_days=7
```

## 🎨 Weather Codes

Aplikasi memetakan weather codes dari API ke icon dan deskripsi:

| Code | Deskripsi | Icon |
|------|-----------|------|
| 0 | Clear sky | ☀️ Sun |
| 1-3 | Partly cloudy | ☁️ Clouds |
| 45-48 | Foggy | 🌫️ Cloudy |
| 51-55 | Drizzle | 🌧️ Rain |
| 61-65 | Rain | 🌧️ Rain |
| 71-77 | Snow | 🌨️ Cloudy |
| 80-82 | Rain showers | 🌧️ Rain |
| 95-99 | Thunderstorm | ⛈️ Thunder |

## 🔄 State Management

Aplikasi menggunakan **ViewModel** dengan **StateFlow** untuk reactive state management:

```kotlin
data class WeatherUiState(
    val isLoading: Boolean = false,
    val currentLocation: Location? = null,
    val savedLocations: List<Location> = emptyList(),
    val weatherData: WeatherResponse? = null,
    val forecastItems: List<ForecastItem> = emptyList(),
    val airQualityItems: List<AirQualityItem> = emptyList(),
    val error: String? = null
)
```

## 🐛 Troubleshooting

### Gradle Build Error
```bash
# Clear cache dan rebuild
./gradlew clean
./gradlew build --refresh-dependencies
```

### Location tidak terdeteksi
1. Pastikan GPS device aktif
2. Cek permission sudah diizinkan
3. Pastikan Google Play Services terinstall
4. Coba restart aplikasi

### API Error
1. Cek koneksi internet
2. Lihat logcat untuk detail error
3. API Open-Meteo kadang slow, tunggu beberapa detik

## 📝 TODO / Improvement Ideas

- [ ] Add search location by city name
- [ ] Save favorite locations to local database
- [ ] Add hourly forecast view
- [ ] Add weather alerts
- [ ] Dark/Light theme toggle
- [ ] Widget support
- [ ] Share weather report
- [ ] Offline cache

## 👨‍💻 Developer

Original UI by AndroidLead
API Integration & Location Features by [Your Name]

## 📄 License

MIT License - Lihat LICENSE file untuk detail

## 🙏 Credits

- Weather data: [Open-Meteo](https://open-meteo.com/)
- Original UI design: AndroidLead
- Location services: Google Play Services

