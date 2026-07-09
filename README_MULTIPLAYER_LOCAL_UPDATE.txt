UPDATE MULTIPLAYER LOKAL

Mode MULTIPLAYER sekarang aktif dari menu utama.

Aturan:
1. Ada 2 player: PLAYER 1 dan PLAYER 2.
2. Setiap player mendapat 5 kesempatan menendang.
3. Giliran berjalan bergantian:
   - PLAYER 1 menendang, PLAYER 2 menjadi keeper.
   - Setelah eksekusi selesai, role bertukar.
   - PLAYER 2 menendang, PLAYER 1 menjadi keeper.
4. Player penendang mengarahkan bola terlebih dahulu dengan drag bola lalu lepas.
5. Setelah itu player keeper memilih arah tangkapan dengan klik area gawang.
6. Eksekusi baru berjalan setelah arah bola dan arah keeper sudah dipilih.
7. Jika bola masuk gawang dan tidak ditangkap keeper, skor player penendang bertambah 1.
8. Setelah kedua player sudah menendang masing-masing 5 kali, skor dibandingkan.
9. Skor lebih banyak menang. Jika sama, hasil seri.

File utama yang diubah:
src/Main.java

Tambahan kode utama:
- showMultiplayerMode(...)
- resetMultiplayerRound(...)
- updateMultiplayer(...)
- finishMultiplayerShot(...)
- MultiplayerState
- MultiplayerPhase

Mode ini tetap memakai animasi keeper hijau, gerakan busur/arc, efek retro motion, dan sistem bola yang sudah ada sebelumnya.
