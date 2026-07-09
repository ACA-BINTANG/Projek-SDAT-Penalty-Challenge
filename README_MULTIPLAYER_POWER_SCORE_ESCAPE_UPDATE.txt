UPDATE MULTIPLAYER - POWER, SCORE CIRCLES, TAG FADE, KEYBOARD MENU

Perubahan utama:
1. Power bola dikurangi.
   - MAX_PULL_DISTANCE dinaikkan dari 160 ke 230.
   - MIN/MAX speed bola diturunkan.
   - kurva power diubah ke SHOT_POWER_CURVE = 1.35 agar tarikan kecil tidak langsung terlalu kuat.

2. Score multiplayer diganti dari angka menjadi lingkaran:
   - Baris 1: PLAYER 1.
   - Baris 2: PLAYER 2.
   - Lingkaran hijau = gol.
   - Lingkaran merah = gagal / diselamatkan / meleset.
   - Setiap player tetap punya 5 kesempatan.

3. Label PLAYER 1 dan PLAYER 2 diperbaiki:
   - PLAYER 1 merah.
   - PLAYER 2 biru.
   - Stroke hitam diperkecil supaya teks tidak terlihat hitam.
   - Label fade in lalu fade out dan hilang otomatis setelah beberapa detik.

4. Tombol UI multiplayer dihapus:
   - Tidak ada tombol MENU di pojok.
   - Tidak ada tombol ULANGI/MENU di popup hasil.
   - ESC = kembali ke menu.
   - R = ulangi match saat match selesai.

File utama yang berubah:
src/Main.java
