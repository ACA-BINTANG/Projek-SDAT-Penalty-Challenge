UPDATE RETRO ARC + CHOPPY MOTION

Perubahan utama:
1. Dive keeper ke samping dibuat lebih melengkung / setengah lingkaran, bukan segitiga.
2. Keeper tetap turun lagi ke tanah setelah loncat.
3. Gerakan bola dan keeper dibuat lebih choppy agar terasa retro.
4. Bola diberi rotasi step-by-step agar terasa seperti game retro.

Konstanta yang bisa diubah di src/Main.java:
- KEEPER_SIDE_DIVE_ARC_SECONDS = durasi gerakan lompat samping.
- KEEPER_SIDE_DIVE_ARC_HEIGHT_RATIO = tinggi lengkungan lompat samping.
- KEEPER_FALL_TO_GROUND_SECONDS = durasi jatuh keeper ke tanah.
- KEEPER_FALL_ARC_HEIGHT_RATIO = lengkungan saat keeper turun ke tanah.
- RETRO_MOTION_FPS = semakin kecil, gerakan semakin patah-patah / retro.
- RETRO_PIXEL_SNAP = semakin besar, posisi gerak semakin kasar / pixelated.
- BALL_RETRO_ROTATION_DEGREES_PER_SECOND = kecepatan putar bola.
- BALL_ROTATION_SNAP_DEGREES = snap rotasi bola agar terasa retro.
