# Glass Morphism Effect on WeeklyForecast

## Perubahan yang Dilakukan

Telah berhasil menambahkan efek **glass morphism** (blur background) pada forecast card yang tidak terpilih di `WeeklyForecast.kt`.

## Detail Implementasi

### 1. **Import blur modifier**
Menambahkan import untuk `androidx.compose.ui.draw.blur` yang digunakan untuk membuat efek blur.

### 2. **Blur Modifier Conditional**
Membuat `blurModifier` yang hanya diterapkan pada card yang tidak terpilih:
```kotlin
val blurModifier = remember(item.isSelected) {
    if (!item.isSelected) {
        Modifier.blur(radius = 20.dp)
    } else {
        Modifier
    }
}
```

### 3. **Struktur Layer Baru**
Menambahkan layer Box pembungkus untuk menerapkan efek blur pada background:
- **Outer Box** dengan `blurModifier` - menciptakan efek blur
- **Inner Box** dengan semi-transparent background - menciptakan efek glassmorphic
- **Column** dengan konten forecast

## Visual Effect

### Card Terpilih (Selected):
- Background gradient berwarna (ColorGradient1, 2, 3)
- Tidak ada blur
- Tampil jelas dan menonjol

### Card Tidak Terpilih (Non-Selected):
- Background semi-transparent white (alpha 0.15f + 0.1f)
- **Blur radius 20.dp** - menciptakan efek frosted glass
- Border lebih terang (alpha 0.8f)
- Menciptakan kesan depth dan hierarchy

## Hasil

Forecast card yang tidak terpilih sekarang memiliki efek **glass morphism** yang elegan:
- Background terlihat blur seperti kaca buram
- Semi-transparent sehingga video background tetap terlihat
- Menciptakan hierarchy visual yang jelas antara card terpilih dan tidak terpilih
- Memberikan kesan modern dan premium pada UI

## Testing
Jalankan aplikasi dan scroll weekly forecast untuk melihat perbedaan antara card yang terpilih (jelas) dan tidak terpilih (blur glass effect).

