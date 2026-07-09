UPDATE RETURN + DELAY

Perubahan:
1. Setelah animasi keeper lompat/tangkap/gagal selesai, keeper tidak langsung reset ronde.
2. Keeper kembali dulu ke posisi awal memakai gambar berdiri 1, jadi terlihat turun/mendarat ke posisi awal.
3. Setelah posisi keeper kembali normal, ronde berikutnya menunggu 2 detik.
4. Perubahan berlaku untuk mode Endless dan Tournament.

File utama yang diubah:
- src/Main.java

Konstanta penting:
- ROUND_RESULT_DELAY_SECONDS = 2.0
- KEEPER_RETURN_TO_IDLE_SPEED = 520
