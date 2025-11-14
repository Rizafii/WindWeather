# Feature: Dynamic Weather-Based Card Colors

## Deskripsi
Background color card location di Locations Screen sekarang berubah secara dinamis sesuai dengan kondisi cuaca masing-masing kota.

## Implementasi

### Fungsi `getWeatherCardColor()`
Fungsi helper yang mengembalikan warna berdasarkan kondisi cuaca:

```kotlin
private fun getWeatherCardColor(weatherCondition: String): Color {
    return when {
        // Cerah/Sunny - Kuning (#FFD54F)
        weatherCondition.contains("sunny", ignoreCase = true) ||
        weatherCondition.contains("clear", ignoreCase = true) -> Color(0xFFFFD54F)
        
        // Hujan - Biru (#64B5F6)
        weatherCondition.contains("rain", ignoreCase = true) ||
        weatherCondition.contains("drizzle", ignoreCase = true) ||
        weatherCondition.contains("shower", ignoreCase = true) -> Color(0xFF64B5F6)
        
        // Berawan - Abu-abu (#B0BEC5)
        weatherCondition.contains("cloudy", ignoreCase = true) ||
        weatherCondition.contains("overcast", ignoreCase = true) ||
        weatherCondition.contains("cloud", ignoreCase = true) -> Color(0xFFB0BEC5)
        
        // Thunderstorm - Biru gelap (#5C6BC0)
        weatherCondition.contains("thunder", ignoreCase = true) ||
        weatherCondition.contains("storm", ignoreCase = true) -> Color(0xFF5C6BC0)
        
        // Fog/Mist - Abu-abu muda (#CFD8DC)
        weatherCondition.contains("fog", ignoreCase = true) ||
        weatherCondition.contains("mist", ignoreCase = true) ||
        weatherCondition.contains("haze", ignoreCase = true) -> Color(0xFFCFD8DC)
        
        // Snow - Putih kebiruan (#E1F5FE)
        weatherCondition.contains("snow", ignoreCase = true) -> Color(0xFFE1F5FE)
        
        // Default - Warna surface
        else -> ColorSurface.copy(alpha = 0.5f)
    }
}
```

## Mapping Warna Cuaca

| Kondisi Cuaca | Warna | Hex Code | Contoh |
|---------------|-------|----------|--------|
| ☀️ **Cerah/Sunny/Clear** | Kuning | #FFD54F | Sunny, Clear sky |
| 🌧️ **Hujan** | Biru | #64B5F6 | Rain, Drizzle, Shower |
| ☁️ **Berawan** | Abu-abu | #B0BEC5 | Cloudy, Overcast |
| ⛈️ **Badai Petir** | Biru gelap | #5C6BC0 | Thunder, Storm |
| 🌫️ **Kabut** | Abu-abu muda | #CFD8DC | Fog, Mist, Haze |
| ❄️ **Salju** | Putih kebiruan | #E1F5FE | Snow |
| 🔘 **Default** | Surface color | Transparan | Lainnya |

## Perubahan pada LocationItem

### Sebelumnya:
```kotlin
Card(
    colors = CardDefaults.cardColors(
        containerColor = ColorSurface.copy(alpha = 0.5f) // Static color
    )
)
```

### Sekarang:
```kotlin
val cardColor = getWeatherCardColor(location.weatherCondition)

Card(
    colors = CardDefaults.cardColors(
        containerColor = cardColor.copy(alpha = 0.7f) // Dynamic color
    )
)
```

## Fitur

### ✨ **Dynamic Color Matching**
- Warna card otomatis menyesuaikan dengan kondisi cuaca
- Case-insensitive matching (tidak peduli huruf besar/kecil)
- Mendukung berbagai variasi nama cuaca

### 🎨 **Visual Feedback**
- User dapat langsung mengetahui kondisi cuaca dari warna card
- Tidak perlu membaca teks kondisi cuaca
- Lebih cepat dan intuitif

### 🔍 **Pattern Matching**
- Menggunakan `contains()` untuk flexibilitas
- Mendukung variasi nama (contoh: "Rain", "Rainy", "Light Rain", dll)
- Fallback ke default color jika kondisi tidak dikenali

## Contoh Visual

```
┌──────────────────────────────────┐
│  Jakarta         🌤️        28°  │  ← Kuning (Partly Cloudy)
│  Indonesia                       │
│  Partly Cloudy                   │
└──────────────────────────────────┘

┌──────────────────────────────────┐
│  London          🌧️        15°  │  ← Biru (Rainy)
│  United Kingdom                  │
│  Rainy                           │
└──────────────────────────────────┘

┌──────────────────────────────────┐
│  Tokyo           ☁️        22°  │  ← Abu-abu (Cloudy)
│  Japan                           │
│  Cloudy                          │
└──────────────────────────────────┘
```

## Alpha Transparency

Semua warna menggunakan `alpha = 0.7f` untuk:
- ✅ Menjaga readability teks
- ✅ Tidak terlalu overwhelming
- ✅ Blend dengan background aplikasi
- ✅ Tetap terlihat premium dan modern

## Testing Checklist

- [ ] Card berwarna kuning untuk kondisi "Sunny"
- [ ] Card berwarna kuning untuk kondisi "Clear"
- [ ] Card berwarna biru untuk kondisi "Rain"
- [ ] Card berwarna biru untuk kondisi "Rainy"
- [ ] Card berwarna abu-abu untuk kondisi "Cloudy"
- [ ] Card berwarna abu-abu untuk kondisi "Overcast"
- [ ] Card berwarna biru gelap untuk kondisi "Thunderstorm"
- [ ] Card berwarna abu-abu muda untuk kondisi "Foggy"
- [ ] Card berwarna putih kebiruan untuk kondisi "Snow"
- [ ] Case insensitive matching (SUNNY = sunny = Sunny)
- [ ] Default color untuk kondisi yang tidak dikenali
- [ ] Teks tetap readable di semua warna

## File yang Diubah

- ✏️ `app/src/main/java/androidlead/weatherappui/ui/screen/locations/LocationsScreen.kt`
  - Menambahkan fungsi `getWeatherCardColor()`
  - Update `LocationItem` untuk menggunakan dynamic color

## Benefits

1. ✅ **Visual Recognition** - User langsung tahu cuaca tanpa baca teks
2. ✅ **Better UX** - Informasi lebih cepat dicerna
3. ✅ **Aesthetic** - Tampilan lebih colorful dan menarik
4. ✅ **Intuitive** - Warna sesuai dengan asosiasi umum (biru = hujan, kuning = cerah)
5. ✅ **Scalable** - Mudah menambah kondisi cuaca baru

## Future Improvements

- [ ] Animasi transisi warna saat cuaca berubah
- [ ] Gradient background untuk kondisi cuaca tertentu
- [ ] Icon cuaca dinamis yang match dengan warna
- [ ] Dark mode support dengan penyesuaian warna

