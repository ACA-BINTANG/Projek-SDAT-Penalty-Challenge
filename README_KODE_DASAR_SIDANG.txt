STRUKTUR KODE DASAR UNTUK SIDANG
================================

1. src/Main.java
   - Hanya menjadi titik masuk program.
   - Menjalankan aplikasi JavaFX dengan launch(args).

2. src/core/GameApp.java
   - Mengatur intro.
   - Mengatur menu utama.
   - Mengatur audio utama.
   - Mengarahkan pilihan menu ke mode yang dipilih.

3. src/core/GameEngine.java
   - Menyimpan logika permainan yang dipakai bersama.
   - Contoh: gerak bola, animasi keeper, deteksi goal/save/miss, scoreboard, dan data state.

4. src/modes/endless/EndlessMode.java
   - Aturan khusus Endless.

5. src/modes/coop/CoopMode.java
   - Aturan khusus Co-op / multiplayer lokal.

6. src/modes/tournament/TournamentMode.java
   - Aturan khusus Tournament 4 dan 8 tim.

7. src/modes/tutorial/TutorialMode.java
   - Aturan khusus Tutorial.

ALUR PROGRAM PALING SEDERHANA
-----------------------------
Main -> Intro -> Menu -> Pilih Mode -> Mode memakai GameEngine -> ESC kembali ke Menu

CARA MENJELASKAN SAAT SIDANG
----------------------------
"Project saya menggunakan konsep modular. Main hanya menjalankan program. GameApp mengatur
tampilan utama dan navigasi. GameEngine berisi fungsi permainan yang dipakai bersama. Setiap
mode memiliki class sendiri sehingga aturan Endless, Co-op, Tournament, dan Tutorial tidak
bercampur dalam satu file."
