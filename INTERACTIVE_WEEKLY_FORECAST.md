# Interactive Weekly Forecast Feature

## Ringkasan
Telah berhasil mengimplementasikan fitur interaktif pada weekly forecast, di mana pengguna dapat mengklik hari tertentu untuk melihat detail cuaca pada hari tersebut.

## Perubahan yang Dilakukan

### 1. **WeeklyForecast Component** (`WeeklyForecast.kt`)
- Menambahkan parameter `onItemClick: (Int) -> Unit` untuk callback ketika item diklik
- Menambahkan parameter `onClick` pada `Forecast` composable
- Menambahkan `.clickable { onClick() }` modifier pada Surface untuk membuat item clickable
- Meneruskan index dari item yang diklik ke callback

### 2. **WeatherViewModel** (`WeatherViewModel.kt`)

#### a. WeatherUiState
- Menambahkan field `selectedDayIndex: Int = 0` untuk tracking hari yang dipilih

#### b. Function `selectDay(dayIndex: Int)`
Fungsi baru untuk menangani pemilihan hari:
- Mengupdate `selectedDayIndex` di state
- Mengupdate forecast items dengan selection baru
- Mengambil data cuaca untuk hari yang dipilih (suhu, weather code, tanggal)
- Mengupdate hourly forecast untuk hari yang dipilih
- Mengupdate tampilan utama dengan data hari yang dipilih:
  - Temperature (max temp)
  - Description
  - Date
  - Feels like (menampilkan high/low temp)
  - Weather icon
  - Weather video background

#### c. Function `mapToForecastItems()`
- Diupdate untuk menggunakan `selectedDayIndex` dari state
- Item akan ditandai sebagai `isSelected` berdasarkan index yang cocok

#### d. Function `mapToHourlyForecastItems()`
- Diupdate dengan parameter `dayIndex: Int = 0`
- Filter data hourly berdasarkan tanggal yang dipilih
- Hanya menampilkan 24 jam untuk hari yang dipilih
- Menggunakan `timeString.startsWith(targetDate)` untuk filter

### 3. **WeatherScreen** (`WeatherScreen.kt`)
- Menambahkan `onItemClick` handler pada `WeeklyForecast`
- Memanggil `viewModel.selectDay(dayIndex)` ketika item diklik

## Cara Kerja

### Flow Interaksi:
1. **User tap pada forecast card** → `onClick()` dipanggil dengan index
2. **WeeklyForecast** meneruskan `dayIndex` ke `onItemClick` callback
3. **WeatherScreen** memanggil `viewModel.selectDay(dayIndex)`
4. **ViewModel** melakukan:
   - Update `selectedDayIndex`
   - Re-map forecast items (mengubah `isSelected` state)
   - Ambil data cuaca untuk hari yang dipilih
   - Filter hourly forecast untuk hari tersebut
   - Update semua UI state (temp, description, date, icon, video)
5. **UI Re-render** dengan data hari yang dipilih:
   - Forecast card yang diklik menjadi selected (gradient background, no blur)
   - Forecast card lainnya menjadi unselected (blur glass effect)
   - Daily forecast menampilkan data hari yang dipilih
   - Hourly forecast menampilkan jam-jam dari hari yang dipilih
   - Video background berubah sesuai kondisi cuaca hari tersebut

## Visual Effect

### Card Selected (Diklik):
- Background gradient berwarna cerah
- Tidak ada blur effect
- Border semi-transparent
- Text lebih terang (white gradient)

### Card Unselected:
- Background semi-transparent dengan blur 20dp
- Glass morphism effect
- Border lebih terang
- Text lebih gelap (default color)

## Data yang Ditampilkan per Hari

Ketika user memilih hari tertentu, aplikasi akan menampilkan:

1. **Temperature**: Suhu maksimal hari tersebut
2. **Description**: Kondisi cuaca (Clear sky, Rain, Cloudy, dll)
3. **Date**: Tanggal lengkap (e.g., "Monday, 12 Feb")
4. **High/Low**: Suhu tertinggi dan terendah hari tersebut
5. **Weather Icon**: Icon cuaca sesuai kondisi
6. **Video Background**: Video background berubah sesuai cuaca
7. **Hourly Forecast**: Prakiraan per jam (24 jam) untuk hari yang dipilih
8. **Weekly Forecast**: Tetap menampilkan 7 hari dengan visual selection

## Testing

1. Buka aplikasi dan tunggu data cuaca dimuat
2. Scroll ke bagian "Weekly forecast"
3. Klik pada salah satu hari (bukan yang sudah terpilih)
4. Observe perubahan:
   - Card yang diklik menjadi highlighted
   - Card sebelumnya menjadi blur
   - Bagian atas menampilkan cuaca hari yang dipilih
   - Hourly forecast berubah sesuai hari yang dipilih
   - Video background mungkin berubah sesuai kondisi cuaca

## Benefits

✅ **User Experience**: User dapat menjelajahi cuaca untuk hari-hari mendatang
✅ **Interactive**: Aplikasi lebih dinamis dan engaging
✅ **Informative**: Menampilkan detail lengkap untuk setiap hari
✅ **Visual Feedback**: Jelas terlihat hari mana yang sedang dipilih
✅ **Consistent**: Video background dan icon selalu sync dengan data yang ditampilkan

## Catatan Teknis

- Default selection adalah hari pertama (index 0 = hari ini)
- Maximum 7 hari forecast sesuai data dari API
- Hourly forecast dibatasi 24 jam per hari
- State management menggunakan StateFlow dari ViewModel
- UI otomatis re-render saat state berubah (reactive)

