UPDATE FALL SECONDS

Perubahan utama ada di src/Main.java.

Sekarang gerakan keeper turun/jatuh ke tanah tidak memakai speed pixel lagi, tetapi memakai durasi detik tetap.

Konstanta yang bisa diatur:

private static final double KEEPER_FALL_TO_GROUND_SECONDS = 0.45;

Artinya:
- 0.45 = keeper jatuh ke tanah selama 0,45 detik
- angka lebih kecil = jatuh lebih cepat
- angka lebih besar = jatuh lebih lambat

Contoh:
- 0.30 = sangat cepat
- 0.45 = normal
- 0.70 = lebih lambat/sinematik

Setelah keeper selesai jatuh/mendarat, ronde tetap menunggu ROUND_RESULT_DELAY_SECONDS = 2.0 sebelum lanjut.
