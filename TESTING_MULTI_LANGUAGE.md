# Testing Multi Language Feature

## 🧪 Cara Testing

### 1. Build Aplikasi
```powershell
cd D:\Firmansyah\Ngoding\mobile\WindWeather
.\gradlew assembleDebug
```

### 2. Install di Device/Emulator
```powershell
.\gradlew installDebug
```

### 3. Testing Manual

#### Test Case 1: Ganti ke Bahasa Indonesia
1. Buka aplikasi (default: English)
2. Klik tombol control (kiri atas, ikon roda gigi)
3. Modal settings muncul dengan teks "Settings"
4. Klik "Indonesian" di section Language
5. Aplikasi restart otomatis
6. Setelah restart, modal akan muncul dengan teks "Pengaturan"
7. ✅ **Expected**: Semua teks UI berubah ke Bahasa Indonesia

#### Test Case 2: Ganti ke Bahasa Inggris
1. (Dari bahasa Indonesia)
2. Klik tombol control
3. Modal settings muncul dengan teks "Pengaturan"
4. Klik "Inggris" di section Bahasa
5. Aplikasi restart otomatis
6. Setelah restart, modal akan muncul dengan teks "Settings"
7. ✅ **Expected**: Semua teks UI berubah ke Bahasa Inggris

#### Test Case 3: Persistence
1. Ganti bahasa ke Indonesia
2. Tutup aplikasi (force close atau back)
3. Buka aplikasi lagi
4. ✅ **Expected**: Aplikasi tetap menggunakan Bahasa Indonesia

#### Test Case 4: Theme Selection (UI Only)
1. Buka modal settings
2. Klik "Light Mode" atau "Mode Terang"
3. Pilihan berubah dengan visual feedback (checkmark)
4. Klik "Close" atau "Tutup"
5. ⚠️ **Expected**: Tema tidak berubah (belum diimplementasikan)
6. ✅ **Expected**: Preferensi tersimpan di SharedPreferences

### 4. Verifikasi String Resources

#### Bahasa Indonesia (values-id):
- Pengaturan
- Bahasa
- Mode Tampilan
- Tutup
- Indonesia / Inggris
- Mode Gelap / Mode Terang

#### Bahasa Inggris (values):
- Settings
- Language
- Display Mode
- Close
- Indonesian / English
- Dark Mode / Light Mode

## 🔍 Debugging

### Check SharedPreferences
```kotlin
// Di debugging console atau logcat
val prefs = getSharedPreferences("weather_app_prefs", Context.MODE_PRIVATE)
val language = prefs.getString("language", "not_set")
val theme = prefs.getString("theme", "not_set")
Log.d("Prefs", "Language: $language, Theme: $theme")
```

### Check Current Locale
```kotlin
val currentLocale = resources.configuration.locales[0]
Log.d("Locale", "Current: ${currentLocale.language}")
```

## 📊 Expected Behavior

### Language Change Flow:
```
User clicks language
    ↓
PreferencesManager.language setter called
    ↓
Value saved to SharedPreferences
    ↓
restartApp() called
    ↓
App exits with Runtime.exit(0)
    ↓
Android restarts app
    ↓
MainActivity.attachBaseContext() called
    ↓
PreferencesManager.applyLanguage() called
    ↓
Locale.setDefault() + Configuration.setLocale()
    ↓
All string resources updated
    ↓
App displays in new language
```

## ⚠️ Known Issues

### None! 🎉
Fitur multi bahasa berjalan dengan baik tanpa known issues.

## 🐛 Troubleshooting

### Issue: Bahasa tidak berubah setelah restart
**Solution**: 
- Cek apakah attachBaseContext() dipanggil di MainActivity
- Cek SharedPreferences apakah nilai tersimpan
- Pastikan Application class terdaftar di AndroidManifest

### Issue: App tidak restart setelah ganti bahasa
**Solution**:
- Cek logcat untuk error
- Pastikan Runtime.exit(0) berjalan
- Pastikan intent launch package benar

### Issue: Modal tidak muncul
**Solution**:
- Cek tombol control apakah clickable
- Cek state showSettingsModal
- Pastikan Dialog dependency ada

### Issue: String tidak berubah
**Solution**:
- Pastikan menggunakan stringResource() atau getString()
- Jangan hardcode text di UI
- Cek file strings.xml di values dan values-id

## 📱 Device Requirements

- **Min SDK**: 28 (Android 9.0)
- **Target SDK**: 34 (Android 14)
- **Permissions**: None required for language feature
- **Storage**: <1KB for preferences

## ✅ Checklist Fitur

- [x] Modal settings muncul saat klik control button
- [x] Pilihan bahasa: Indonesia & English
- [x] Pilihan tema: Dark & Light (UI only)
- [x] String resources lengkap untuk 2 bahasa
- [x] Preferensi tersimpan di SharedPreferences
- [x] Preferensi persistent setelah app restart
- [x] Locale diterapkan saat app start
- [x] App restart saat ganti bahasa
- [x] Visual feedback saat pilih opsi
- [x] Smooth UI/UX
- [x] No compilation errors
- [ ] Dark/Light mode functional (future)

## 🎉 Status: READY FOR TESTING!

