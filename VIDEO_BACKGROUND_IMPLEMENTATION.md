# Implementasi Video Background Dinamis

## Ringkasan
Telah berhasil mengimplementasikan background video MP4 yang dinamis berdasarkan kondisi cuaca yang sedang terjadi.

## Perubahan yang Dilakukan

### 1. **WeatherVideoBackground Component** (`WeatherVideoBackground.kt`)
- Membuat komponen Compose baru yang menggunakan ExoPlayer untuk memutar video
- Video akan diputar secara loop tanpa suara
- Menggunakan AndroidView untuk integrasi dengan Media3 ExoPlayer

### 2. **WeatherCodeMapper Utility** (`WeatherCodeMapper.kt`)
- Menambahkan fungsi `getWeatherVideo()` untuk memetakan weather code ke video resource
- Mapping video berdasarkan kondisi cuaca:
  - **Clear sky (0)** → `video_forecast.mp4` (cerah/sunny)
  - **Cloudy (1,2,3,45,48,71-77,85,86)** → `video_cloudy.mp4` (berawan)
  - **Rain (51-55,61-65,80-82)** → `video_rain.mp4` (hujan)
  - **Storm (95,96,99)** → `video_storm.mp4` (badai petir)

### 3. **WeatherViewModel** (`WeatherViewModel.kt`)
- Menambahkan field `currentWeatherVideo` ke `WeatherUiState`
- Mengupdate state dengan video resource yang sesuai saat weather data dimuat

### 4. **WeatherScreen** (`WeatherScreen.kt`)
- Mengubah struktur layout untuk mendukung video background
- Menambahkan `Box` wrapper dengan video background di layer paling bawah
- Membuat `Scaffold` transparan agar video terlihat di background
- Video hanya ditampilkan saat tidak loading dan tidak ada error

### 5. **Dependencies** (gradle files)
- Menambahkan Media3 ExoPlayer ke `libs.versions.toml`:
  - `androidx.media3:media3-exoplayer:1.2.1`
  - `androidx.media3:media3-ui:1.2.1`
- Mengupdate `app/build.gradle.kts` untuk menggunakan dependency tersebut

## Cara Kerja

1. Saat aplikasi memuat data cuaca, `WeatherViewModel` akan mendapatkan `weatherCode` dari API
2. `WeatherCodeMapper.getWeatherVideo()` akan memetakan weather code ke video resource yang sesuai
3. Video resource ID disimpan di `WeatherUiState.currentWeatherVideo`
4. `WeatherScreen` akan merender `WeatherVideoBackground` dengan video yang sesuai
5. Video akan diputar secara loop sebagai background, dengan konten cuaca di atasnya

## Video yang Tersedia
- `video_forecast.mp4` - Untuk cuaca cerah
- `video_cloudy.mp4` - Untuk cuaca berawan/berkabut/bersalju
- `video_rain.mp4` - Untuk hujan
- `video_storm.mp4` - Untuk badai petir

## Testing
Untuk menguji implementasi:
1. Sync Gradle untuk download dependency Media3
2. Build dan jalankan aplikasi
3. Video background akan otomatis berubah sesuai kondisi cuaca lokasi yang dipilih
4. Coba ganti lokasi untuk melihat video background yang berbeda

## Catatan
- Video akan dimute (tanpa suara) untuk pengalaman yang lebih baik
- Video menggunakan resize mode ZOOM untuk memenuhi seluruh layar
- ExoPlayer akan di-release saat komponen di-dispose untuk menghemat memori

