UPDATE AUDIO GAME

Perubahan:
1. Default gameplay audio memakai resources/audio/default.MP3 dan berjalan loop selama mode game aktif.
2. Saat hasil ronde GOAL, default audio dipause, resources/audio/goal.MP3 diputar sekali, lalu default audio lanjut lagi.
3. Saat hasil ronde MISSED atau SAVED/ketangkap keeper, default audio dipause, resources/audio/missed.MP3 diputar sekali, lalu default audio lanjut lagi.
4. Audio default aktif di mode Endless, Multiplayer, dan Tournament.
5. Saat kembali ke menu atau aplikasi ditutup, seluruh audio gameplay dihentikan supaya tidak overlap.

File utama yang diubah:
src/Main.java

File audio yang dipakai:
resources/audio/default.MP3
resources/audio/goal.MP3
resources/audio/missed.MP3

Catatan run JavaFX:
Pastikan VM Options tetap menambahkan javafx.media:
--module-path D:\javafx-sdk-26.0.1\lib --add-modules javafx.controls,javafx.fxml,javafx.media
