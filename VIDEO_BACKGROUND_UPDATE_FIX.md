# Fix Background Video Not Changing

## Masalah
Background video tidak berubah saat user mengklik weekly forecast yang berbeda, meskipun data cuaca lainnya (temperature, description, air quality) sudah berubah dengan benar.

## Penyebab
Di `WeatherVideoBackground.kt`, ExoPlayer tidak di-reset dengan benar saat `videoResId` berubah. Meskipun `DisposableEffect(videoResId)` sudah mendeteksi perubahan, ExoPlayer masih memutar video lama karena:
1. Media item lama tidak dihapus sebelum set media item baru
2. Player tidak di-stop sebelum diganti videonya
3. `onDispose` block merelease seluruh player, bukan hanya membersihkan media

## Solusi

### Perubahan di `WeatherVideoBackground.kt`

#### 1. Pisahkan Inisialisasi ExoPlayer
```kotlin
val exoPlayer = remember {
    ExoPlayer.Builder(context).build().apply {
        repeatMode = Player.REPEAT_MODE_ALL
        volume = 0f // Mute the video
    }
}
```
ExoPlayer sekarang diinisialisasi tanpa media item, hanya dengan konfigurasi dasar.

#### 2. Update DisposableEffect untuk Mengganti Video
```kotlin
DisposableEffect(videoResId) {
    val uri = Uri.parse("android.resource://${context.packageName}/$videoResId")
    exoPlayer.apply {
        stop()                    // Stop video yang sedang berjalan
        clearMediaItems()         // Hapus semua media item
        setMediaItem(MediaItem.fromUri(uri))  // Set media baru
        prepare()                 // Prepare player
        playWhenReady = true      // Auto play
    }

    onDispose {
        exoPlayer.stop()          // Stop saat dispose
        exoPlayer.clearMediaItems() // Clear media items
    }
}
```

**Key changes:**
- `stop()` - Menghentikan playback sebelum mengganti video
- `clearMediaItems()` - Menghapus video lama dari queue
- `onDispose` hanya membersihkan media, bukan release player

#### 3. Tambah DisposableEffect Terpisah untuk Release
```kotlin
DisposableEffect(Unit) {
    onDispose {
        exoPlayer.release()  // Release player saat component destroyed
    }
}
```

Ini memastikan ExoPlayer hanya di-release saat component benar-benar destroyed (bukan saat video berubah).

## Flow Kerja Setelah Fix

1. **User klik weekly forecast** → `viewModel.selectDay(dayIndex)` dipanggil
2. **ViewModel update state** → `currentWeatherVideo` berubah sesuai weather code
3. **WeatherScreen re-compose** → `WeatherVideoBackground` menerima `videoResId` baru
4. **DisposableEffect triggered** → Karena `videoResId` berubah
5. **ExoPlayer update**:
   - Stop video lama
   - Clear media items
   - Load video baru
   - Prepare dan play
6. **Video background berubah** → User melihat video sesuai cuaca yang dipilih

## Testing

1. Jalankan aplikasi
2. Tunggu data cuaca dimuat
3. Perhatikan background video awal (misalnya cloudy)
4. **Klik pada hari dengan kondisi cuaca berbeda** (misalnya sunny atau rainy)
5. ✅ Verify bahwa background video berubah sesuai kondisi cuaca
6. Klik beberapa hari berbeda untuk memastikan transisi video berjalan lancar

## Hasil

✅ **Background video sekarang dinamis** dan berubah sesuai kondisi cuaca yang dipilih
✅ **Smooth transition** saat mengganti video
✅ **No memory leaks** karena proper cleanup di onDispose
✅ **Konsisten** dengan data cuaca yang ditampilkan
✅ **User experience cohesive** - semua UI element (temperature, description, icon, video, air quality) sync dengan hari yang dipilih

## Video Mapping

Reminder mapping weather code ke video:
- **Clear sky (0)** → `video_forecast.mp4`
- **Cloudy (1-3, 45, 48, 71-77, 85-86)** → `video_cloudy.mp4`
- **Rain (51-55, 61-65, 80-82)** → `video_rain.mp4`
- **Storm (95, 96, 99)** → `video_storm.mp4`

## Technical Notes

- ExoPlayer instance di-reuse untuk performa yang lebih baik
- `stop()` dan `clearMediaItems()` penting untuk mencegah overlap video
- `DisposableEffect(videoResId)` memastikan video update saat ID berubah
- Separate `DisposableEffect(Unit)` untuk lifecycle management
- `repeatMode = REPEAT_MODE_ALL` untuk looping video
- `volume = 0f` untuk mute video background

