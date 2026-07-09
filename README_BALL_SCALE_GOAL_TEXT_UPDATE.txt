Update: Ball Perspective + GOAL Text Animation

Perubahan:
1. Bola sekarang mengecil perlahan saat bergerak menjauh ke arah gawang.
2. Ukuran bola tidak dibuat terlalu kecil. Batas minimalnya ada di BALL_MIN_PERSPECTIVE_SCALE.
3. Jika bola masuk goal, muncul teks besar "GOOOAL!" dengan animasi pop, pulse, dan fade out.
4. Efek ini diterapkan ke Endless, Tournament, dan Multiplayer.

Pengaturan utama di src/Main.java:
- BALL_MIN_PERSPECTIVE_SCALE = batas ukuran terkecil bola saat jauh.
- BALL_PERSPECTIVE_CURVE = seberapa cepat bola mengecil.
- GOAL_TEXT_DURATION_SECONDS = durasi animasi teks goal.
- GOAL_TEXT_POP_SECONDS = kecepatan efek membesar di awal.
- GOAL_TEXT_FADE_OUT_SECONDS = durasi fade out teks goal.
