# Fitur Multi Bahasa (Multi Language)

## Ringkasan
Fitur multi bahasa telah diimplementasikan untuk mendukung bahasa Indonesia dan Inggris. Pengguna dapat mengubah bahasa aplikasi melalui modal pengaturan yang dapat diakses dengan menekan tombol control di ActionBar.

## File-file yang Dibuat/Dimodifikasi

### 1. String Resources
- **`app/src/main/res/values/strings.xml`**: String default (Bahasa Inggris)
- **`app/src/main/res/values-id/strings.xml`**: String untuk Bahasa Indonesia

### 2. Locale Configuration
- **`app/src/main/res/xml/locales_config.xml`**: Konfigurasi bahasa yang didukung aplikasi

### 3. PreferencesManager
- **`app/src/main/java/androidlead/weatherappui/data/PreferencesManager.kt`**
  - Menyimpan preferensi bahasa dan tema
  - Menerapkan perubahan bahasa dengan mengubah Locale sistem
  - Melakukan restart aplikasi saat bahasa diubah untuk memastikan semua string resources ter-update

### 4. Application Class
- **`app/src/main/java/androidlead/weatherappui/WeatherApplication.kt`**
  - Menginisialisasi bahasa yang disimpan saat aplikasi dimulai
  - Memastikan bahasa yang dipilih tetap konsisten

### 5. ActionBar Component
- **`app/src/main/java/androidlead/weatherappui/ui/screen/components/ActionBar.kt`**
  - Menambahkan fungsi klik pada ControlButton
  - Menampilkan SettingsModal dengan opsi bahasa dan tema
  - Menggunakan `stringResource()` untuk mendukung multi bahasa

### 6. MainActivity
- **`app/src/main/java/androidlead/weatherappui/MainActivity.kt`**
  - Override `attachBaseContext()` untuk menerapkan bahasa sebelum context digunakan
  - Memastikan bahasa yang dipilih diterapkan setiap kali Activity dibuat

### 7. AndroidManifest
- **`app/src/main/AndroidManifest.xml`**
  - Menambahkan `android:name=".WeatherApplication"` untuk custom Application class
  - Menambahkan `android:localeConfig="@xml/locales_config"` untuk mendukung locale config

### 8. Gradle Dependencies
- **`gradle/libs.versions.toml`**
  - Menambahkan `androidx.appcompat` versi 1.7.0 (untuk kompatibilitas future features)

## Cara Kerja

### 1. Mengubah Bahasa
```kotlin
val prefsManager = PreferencesManager.getInstance(context)

// Ubah ke Bahasa Indonesia
prefsManager.language = PreferencesManager.LANGUAGE_INDONESIAN

// Ubah ke Bahasa Inggris
prefsManager.language = PreferencesManager.LANGUAGE_ENGLISH
```

Ketika bahasa diubah:
1. Preferensi disimpan ke SharedPreferences
2. Aplikasi akan otomatis restart untuk menerapkan bahasa baru
3. Pada saat restart, `attachBaseContext()` di MainActivity akan memanggil `applyLanguage()`
4. Locale di-update menggunakan `Locale.setDefault()` dan `Configuration.setLocale()`
5. Semua teks yang menggunakan `stringResource()` akan otomatis berubah

### 2. Menggunakan String Resources di Composable
```kotlin
@Composable
fun MyComponent() {
    Text(text = stringResource(R.string.settings_title))
}
```

### 3. Menggunakan String Resources di Kotlin Class
```kotlin
fun myFunction(context: Context) {
    val text = context.getString(R.string.settings_title)
}
```

## String Resources yang Tersedia

### Settings Modal
- `settings_title`: "Settings" / "Pengaturan"
- `language_section`: "Language" / "Bahasa"
- `theme_section`: "Display Mode" / "Mode Tampilan"
- `close_button`: "Close" / "Tutup"

### Language Options
- `language_indonesian`: "Indonesian" / "Indonesia"
- `language_english`: "English" / "Inggris"

### Theme Options
- `theme_dark`: "Dark Mode" / "Mode Gelap"
- `theme_light`: "Light Mode" / "Mode Terang"

### Weather Info
- `feels_like`: "Feels like" / "Terasa seperti"
- `humidity`: "Humidity" / "Kelembaban"
- `wind_speed`: "Wind Speed" / "Kecepatan Angin"
- `uv_index`: "UV Index" / "Indeks UV"
- `air_quality`: "Air Quality" / "Kualitas Udara"
- `visibility`: "Visibility" / "Jarak Pandang"
- `pressure`: "Pressure" / "Tekanan"

### Days of Week
- `monday` - `sunday`: Nama-nama hari dalam kedua bahasa

### Weather Forecast
- `hourly_forecast`: "Hourly Forecast" / "Prakiraan Per Jam"
- `weekly_forecast`: "7-Day Forecast" / "Prakiraan 7 Hari"
- `today`: "Today" / "Hari Ini"

### Location
- `add_location`: "Add Location" / "Tambah Lokasi"
- `search_location`: "Search location..." / "Cari lokasi..."
- `current_location`: "Current Location" / "Lokasi Saat Ini"

## Cara Menambahkan String Baru

1. **Tambahkan di `values/strings.xml` (Bahasa Inggris)**
```xml
<string name="new_string">English Text</string>
```

2. **Tambahkan di `values-id/strings.xml` (Bahasa Indonesia)**
```xml
<string name="new_string">Teks Indonesia</string>
```

3. **Gunakan di code**
```kotlin
@Composable
fun MyComponent() {
    Text(text = stringResource(R.string.new_string))
}
```

## Catatan

### Dark/Light Mode
Fitur dark/light mode sudah ada UI-nya di modal settings, namun fungsionalitasnya belum diimplementasikan. Saat ini hanya menyimpan preferensi, tetapi tidak mengubah tema aplikasi.

### Restart Aplikasi
Ketika pengguna mengubah bahasa, aplikasi akan otomatis restart untuk menerapkan bahasa baru. Ini diperlukan karena:
1. String resources perlu di-reload dengan Locale baru
2. Configuration perlu di-update secara menyeluruh
3. Memastikan semua komponen UI menampilkan teks dalam bahasa yang dipilih

### Bahasa Sistem
Jika pengguna belum memilih bahasa, aplikasi akan menggunakan bahasa Inggris sebagai default. Aplikasi tidak otomatis mengikuti bahasa sistem.

## Testing

Untuk menguji fitur multi bahasa:

1. Jalankan aplikasi
2. Klik tombol control (ikon roda gigi) di pojok kiri atas
3. Modal settings akan muncul
4. Pilih bahasa yang diinginkan (Indonesia/English)
5. Aplikasi akan restart dan menampilkan teks dalam bahasa yang dipilih
6. Preferensi bahasa akan tersimpan dan tetap konsisten setelah aplikasi ditutup dan dibuka kembali

## Dependencies Tambahan

```toml
# gradle/libs.versions.toml
[versions]
androidx-appcompat = "1.7.0"

[libraries]
androidx-appcompat = { group = "androidx.appcompat", name = "appcompat", version.ref = "androidx-appcompat" }

[bundles]
androidX = ["androidx-core", "androidx-lifecycle", "androidx-appcompat"]
```

## Struktur File

```
app/
├── src/main/
│   ├── java/androidlead/weatherappui/
│   │   ├── WeatherApplication.kt (NEW)
│   │   ├── MainActivity.kt (MODIFIED)
│   │   ├── data/
│   │   │   └── PreferencesManager.kt (NEW)
│   │   └── ui/screen/components/
│   │       └── ActionBar.kt (MODIFIED)
│   ├── res/
│   │   ├── values/
│   │   │   └── strings.xml (MODIFIED)
│   │   ├── values-id/ (NEW)
│   │   │   └── strings.xml (NEW)
│   │   └── xml/
│   │       └── locales_config.xml (NEW)
│   └── AndroidManifest.xml (MODIFIED)
└── build.gradle.kts
```

