Update loncat vertikal:

1. Keeper tidak lagi memakai animasi return/jalan balik ke posisi awal setelah loncat.
2. Untuk bola atas, keeper bergerak naik lalu turun lagi ke tanah pada X yang sama.
3. Gerak turun memakai konstanta di src/Main.java:
   private static final double KEEPER_FALL_TO_GROUND_SECONDS = 0.45;
4. Ronde berikutnya tetap menunggu ROUND_RESULT_DELAY_SECONDS = 2.0 setelah animasi keeper selesai.
5. Perubahan berlaku untuk mode Endless dan Tournament.
