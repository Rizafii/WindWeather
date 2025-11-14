# ✅ Quick Start Checklist

## Sebelum Build

- [ ] Android Studio sudah terinstall (Hedgehog atau lebih baru)
- [ ] JDK 17 atau lebih baru sudah terinstall
- [ ] Android SDK API Level 28+ sudah terinstall
- [ ] Koneksi internet stabil (untuk download dependencies)

## Build Steps

### 1. Buka Project
- [ ] Buka Android Studio
- [ ] File → Open
- [ ] Pilih folder `WeatherAppUi-master`
- [ ] Klik OK

### 2. Sync Gradle (PENTING!)
- [ ] Tunggu notifikasi "Gradle files have changed"
- [ ] Klik **"Sync Now"**
- [ ] Tunggu sampai selesai (lihat progress bar di bawah)
- [ ] Pastikan tidak ada error di "Build" tab

### 3. Verify Dependencies
Cek file-file ini sudah update:
- [ ] `gradle/libs.versions.toml` - Ada retrofit, moshi, coroutines, location
- [ ] `app/build.gradle.kts` - Ada implementation networking, coroutines, location, viewmodel
- [ ] `AndroidManifest.xml` - Ada permission INTERNET, ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION

### 4. Build Project
- [ ] Build → Make Project (Ctrl+F9)
- [ ] Tunggu sampai "BUILD SUCCESSFUL"
- [ ] Jika error, cek Gradle Console untuk detail

### 5. Setup Device/Emulator
Pilih salah satu:

**Opsi A: Emulator**
- [ ] Tools → Device Manager
- [ ] Create Device (jika belum ada)
- [ ] Pilih Pixel 5 atau device lain
- [ ] API Level 28 atau lebih tinggi
- [ ] Start emulator

**Opsi B: Physical Device**
- [ ] Enable Developer Options di device
- [ ] Enable USB Debugging
- [ ] Connect via USB
- [ ] Verifikasi device muncul di Android Studio

### 6. Run App
- [ ] Pilih device/emulator di toolbar
- [ ] Klik Run ▶ (atau Shift+F10)
- [ ] Tunggu install selesai
- [ ] App akan terbuka

### 7. Test App
- [ ] App minta permission lokasi → **Allow**
- [ ] Tunggu loading (white spinner)
- [ ] Cuaca Rome harus muncul (default)
- [ ] Tap icon ☰ (menu) kiri atas
- [ ] Dialog lokasi muncul
- [ ] Coba pilih Tokyo → Data berubah
- [ ] Coba pilih Current Location → Pakai GPS

## ✅ Verification Checklist

### Data Tampil Benar
- [ ] Temperatur muncul (angka besar)
- [ ] Deskripsi cuaca (Clear sky, Rain, dll)
- [ ] Feels like temperature
- [ ] Tanggal hari ini
- [ ] 6 Air Quality items (Real Feel, Wind, Humidity, dll)
- [ ] 7 hari forecast (horizontal scroll)

### Fitur Berfungsi
- [ ] Tap menu → Dialog muncul
- [ ] Switch location → Data berubah
- [ ] Current Location → Pakai GPS
- [ ] Loading spinner muncul saat fetch data
- [ ] Error message muncul jika no internet

## 🐛 Troubleshooting

### Gradle Sync Failed
```
Fix:
1. File → Invalidate Caches → Invalidate and Restart
2. Tunggu Android Studio restart
3. Build → Clean Project
4. File → Sync Project with Gradle Files
```

### Build Error: "Unresolved reference"
```
Fix:
1. Sync Gradle belum selesai → Tunggu sampai done
2. Dependencies belum download → Cek internet connection
3. Cache corrupt → Invalidate Caches (lihat di atas)
```

### App Crash on Launch
```
Fix:
1. Cek Logcat untuk stack trace
2. Verifikasi permissions di AndroidManifest.xml
3. Verifikasi ViewModel factory di MainActivity
```

### Location Tidak Terdeteksi
```
Fix:
1. Cek permission sudah di-allow
2. Cek GPS device ON
3. Coba restart app
4. Coba di outdoor (GPS signal lebih baik)
```

### API Error / No Data
```
Fix:
1. Cek koneksi internet
2. Cek Logcat untuk HTTP error
3. Open-Meteo API mungkin slow → Tunggu 10 detik
4. Coba switch ke location lain
```

## 📱 Expected Result

### Home Screen Seharusnya:
```
┌─────────────────────────┐
│ ☰    📍 Rome        👤  │ ← ActionBar
├─────────────────────────┤
│                         │
│    ☁️                   │
│         21°             │ ← DailyForecast
│   Feels like 26°        │
│   Partly cloudy         │
│   Monday, 13 Nov        │
│                         │
├─────────────────────────┤
│ Air Quality             │
│ ┌────┬────┬────┐       │
│ │Real│Wind│Hum │       │ ← AirQuality (6 items)
│ │Feel│9km │68% │       │
│ └────┴────┴────┘       │
├─────────────────────────┤
│ Weekly Forecast         │
│ ┌──┬──┬──┬──┬──┬──┬──┐│
│ │Mo│Tu│We│Th│Fr│Sa│Su││ ← 7-day forecast
│ │☀️│☁️│🌧️│☁️│☀️│🌧️│⛈️││
│ │26│18│16│20│34│28│24││
│ └──┴──┴──┴──┴──┴──┴──┘│
└─────────────────────────┘
```

### Dialog Location:
```
┌─────────────────────┐
│ Select Location     │
├─────────────────────┤
│ 📍 Current Location │ ← Pakai GPS
├─────────────────────┤
│ 🌍 Rome             │
│ 🌍 New York         │
│ 🌍 Tokyo            │
│ 🌍 London           │
│ 🌍 Paris            │
└─────────────────────┘
```

## 🎯 Success Criteria

✅ App build without errors
✅ App launch successfully  
✅ Default location (Rome) weather shows
✅ Can switch between locations
✅ GPS location works (with permission)
✅ Loading spinner shows when fetching
✅ Error message shows when offline
✅ Weekly forecast shows 7 days
✅ Air quality shows 6 metrics

## 📞 Need Help?

1. Cek **RINGKASAN_IMPLEMENTASI.md** untuk detail teknis
2. Cek **IMPLEMENTATION_GUIDE.md** untuk dokumentasi lengkap
3. Lihat Logcat di Android Studio untuk error details
4. Google error message jika masih stuck

## 🎉 Done!

Jika semua checklist ✅, aplikasi siap digunakan!

Happy coding! 🚀

