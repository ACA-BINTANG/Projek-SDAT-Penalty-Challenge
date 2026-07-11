AUDIO SMOOTH + INTRO + MENU UPDATE

Perubahan utama:
1. Audio gameplay default sekarang tidak dipause mendadak saat goal/missed.
   - Saat goal/missed, default audio tetap berjalan tetapi volumenya diturunkan pelan.
   - Audio goal/missed masuk dengan fade-in singkat.
   - Setelah audio goal/missed selesai, volume default naik lagi secara halus.

2. Intro sekarang memakai 3 komponen:
   - resources/video/Intro-Awal.mp4 sebagai video intro.
   - resources/audio/Sound-Intro.mp3 sebagai sound intro sekali jalan mengikuti video.
   - resources/audio/music-intro.MP3 sebagai musik intro loop sampai tombol START diklik.

3. Video intro tidak diloop.
   - Setelah durasi video habis, video berhenti.
   - Musik intro tetap loop sampai START diklik.

4. Menu audio ditambahkan.
   - resources/audio/Sound-Menu.mp3 dipakai sebagai musik menu loop.
   - resources/audio/menu-click.wav ditambahkan sebagai sound effect klik tombol menu.

Konstanta yang bisa diatur ada di src/Main.java:
- GAME_DEFAULT_AUDIO_VOLUME
- GAME_DEFAULT_DUCK_VOLUME
- GAME_EFFECT_AUDIO_VOLUME
- GAME_AUDIO_CROSSFADE_SECONDS
- INTRO_SOUND_VOLUME
- INTRO_MUSIC_VOLUME
- MENU_AUDIO_VOLUME
- MENU_CLICK_AUDIO_VOLUME

Catatan run:
Pastikan VM Options memakai javafx.media:
--module-path D:\javafx-sdk-26.0.1\lib --add-modules javafx.controls,javafx.fxml,javafx.media
