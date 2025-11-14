# Panduan Fitur Location Management

## Fitur yang Telah Ditambahkan

### 1. **Lokasi yang Dapat Diklik**
- Nama lokasi di bagian tengah ActionBar sekarang dapat diklik
- Klik pada nama lokasi akan membuka halaman manajemen lokasi
- Tombol settings di kiri atas tidak lagi berfungsi sebagai trigger

### 2. **Halaman Manajemen Lokasi (LocationsScreen)**
Fitur-fitur:
- Menampilkan daftar lokasi yang tersimpan
- Setiap item menampilkan:
  - Nama kota
  - Negara
  - Kondisi cuaca saat ini
  - Suhu
  - Icon cuaca
- Tombol delete untuk menghapus lokasi
- Klik pada item lokasi untuk memilih dan kembali ke halaman utama

### 3. **Tambah Lokasi dengan Search**
- Tombol FAB (+) untuk menambah lokasi baru
- Dialog pencarian lokasi dengan fitur:
  - Search field untuk mencari kota
  - Real-time search results
  - Menampilkan suhu dan kondisi cuaca untuk setiap hasil pencarian
  - Klik hasil pencarian untuk menambahkan ke daftar

### 4. **Local Storage (DataStore)**
- Semua lokasi tersimpan secara persisten menggunakan DataStore Preferences
- Data disimpan dalam format JSON
- Otomatis load saat aplikasi dibuka
- Mendukung operasi CRUD:
  - Create: Tambah lokasi baru
  - Read: Load semua lokasi tersimpan
  - Update: Pilih lokasi sebagai lokasi aktif
  - Delete: Hapus lokasi dari daftar

## Struktur File Baru

```
app/src/main/java/androidlead/weatherappui/
├── data/
│   ├── model/
│   │   └── SavedLocation.kt          # Model data untuk lokasi tersimpan
│   └── repository/
│       └── LocationRepository.kt      # Repository untuk DataStore
├── navigation/
│   ├── Screen.kt                      # Sealed class untuk routes
│   └── WeatherNavHost.kt             # Navigation host untuk routing
└── ui/
    └── screen/
        └── locations/
            ├── LocationsScreen.kt     # UI halaman lokasi
            └── LocationsViewModel.kt   # ViewModel untuk manajemen state

Modified Files:
├── MainActivity.kt                    # Updated untuk menggunakan navigation
├── ui/screen/WeatherScreen.kt        # Updated dengan parameter onLocationClick
└── ui/screen/components/ActionBar.kt  # LocationInfo sekarang clickable
```

## Dependencies Baru

### Sudah Ditambahkan di build.gradle.kts:
```kotlin
// DataStore untuk local storage
implementation("androidx.datastore:datastore-preferences:1.1.1")

// Navigation Compose untuk routing
implementation("androidx.navigation:navigation-compose:2.7.7")

// Kotlinx Serialization untuk JSON
implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
```

### Plugin yang Ditambahkan:
```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kotlin.serialization)  // <- Baru
}
```

## Cara Menggunakan

### 1. Membuka Halaman Lokasi
```kotlin
// User klik pada nama lokasi di ActionBar
// Otomatis navigate ke LocationsScreen
```

### 2. Menambah Lokasi Baru
```kotlin
// 1. Klik tombol FAB (+) di LocationsScreen
// 2. Ketik nama kota di search field
// 3. Pilih dari hasil pencarian
// 4. Lokasi otomatis tersimpan dan muncul di daftar
```

### 3. Memilih Lokasi
```kotlin
// 1. Klik pada item lokasi di daftar
// 2. Weather data akan di-fetch untuk lokasi tersebut
// 3. Otomatis kembali ke halaman weather
```

### 4. Menghapus Lokasi
```kotlin
// Klik icon delete di setiap item lokasi
// Lokasi akan dihapus dari daftar dan storage
```

## API Integration (TODO)

Saat ini search menggunakan mock data. Untuk implementasi real:

### Gunakan Geocoding API
```kotlin
// Contoh: OpenWeather Geocoding API
// GET http://api.openweathermap.org/geo/1.0/direct?q={city}&limit=5&appid={API_KEY}

private suspend fun searchLocations(query: String): List<SavedLocation> {
    val response = geocodingApi.searchCity(query)
    return response.map { 
        SavedLocation(
            id = UUID.randomUUID().toString(),
            name = it.name,
            country = it.country,
            latitude = it.lat,
            longitude = it.lon,
            // Fetch weather untuk setiap lokasi
            ...
        )
    }
}
```

## Testing

### Untuk menguji fitur:
1. **Gradle Sync**: Jalankan gradle sync untuk download dependencies
2. **Build**: Build project
3. **Run**: Install di device/emulator
4. **Test flow**:
   - Klik nama lokasi → Halaman lokasi terbuka
   - Klik FAB (+) → Dialog search muncul
   - Ketik "Jakarta" → Hasil muncul
   - Klik hasil → Lokasi ditambahkan
   - Klik item lokasi → Weather update dan kembali ke home

## Troubleshooting

### Error: Navigation not found
**Solusi**: Gradle sync belum selesai. Tunggu dependencies download selesai.

### Error: DataStore not found
**Solusi**: Clean project dan rebuild
```bash
./gradlew clean build
```

### Search tidak menampilkan hasil
**Solusi**: Saat ini menggunakan mock data. Implementasikan API geocoding untuk data real.

### Lokasi tidak tersimpan
**Solusi**: Periksa permissions di AndroidManifest.xml dan pastikan DataStore initialized dengan benar.

## Next Steps

1. ✅ Implementasi UI dan Navigation
2. ✅ Implementasi DataStore untuk storage
3. ⏳ Integrasi dengan Geocoding API untuk search real
4. ⏳ Fetch weather data untuk setiap lokasi di search results
5. ⏳ Add loading states dan error handling
6. ⏳ Add animations untuk transitions
7. ⏳ Add swipe-to-delete gesture
8. ⏳ Add location dari GPS current location

## Notes

- Mock data saat ini menampilkan 3 kota Indonesia (Jakarta, Bandung, Surabaya)
- Untuk production, ganti dengan API call ke geocoding service
- Weather data di search results harus di-fetch dari weather API
- Consider caching untuk performa lebih baik

