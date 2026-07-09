UPDATE ARC + RETRO MOTION

Perubahan di src/Main.java:
1. Gerakan keeper ke samping tidak lagi linear/segitiga.
   Keeper memakai jalur busur setengah lingkaran dengan Math.sin(Math.PI * progress).

2. Gerakan jatuh keeper setelah loncat dibuat melengkung.
   X memakai easeOutQuad, Y memakai easeInQuad supaya terlihat turun karena gravitasi.

3. Efek retro lag ditambahkan ke bola dan keeper.
   Posisi tidak di-update setiap frame monitor, tetapi per langkah FPS rendah.
   Bola juga diberi rotasi patah-patah supaya feel retro lebih terasa.

Konstanta yang bisa diatur:

KEEPER_SIDE_DIVE_ARC_SECONDS
- Durasi lompat samping keeper.
- Semakin kecil = lebih cepat.

KEEPER_SIDE_DIVE_ARC_HEIGHT_RATIO
- Tinggi busur lompat keeper ke samping.
- Semakin besar = gerakan makin melengkung/naik.

KEEPER_FALL_TO_GROUND_SECONDS
- Durasi keeper jatuh ke tanah setelah lompat.
- Semakin kecil = jatuh lebih cepat.

KEEPER_FALL_ARC_HEIGHT_RATIO
- Lengkungan saat keeper jatuh.

RETRO_MOTION_ENABLED
- true = gerakan agak patah-patah seperti game retro.
- false = gerakan halus normal.

RETRO_MOTION_FPS
- FPS gerakan retro.
- 10 sampai 14 terasa retro.
- 18 sampai 24 lebih halus.

RETRO_PIXEL_SNAP
- Besar grid pixel snapping.
- 2 lebih halus, 4 normal retro, 6 lebih patah-patah.

BALL_RETRO_ROTATION_DEGREES_PER_SECOND
- Kecepatan putaran bola.

BALL_ROTATION_SNAP_DEGREES
- Besar langkah rotasi bola.
