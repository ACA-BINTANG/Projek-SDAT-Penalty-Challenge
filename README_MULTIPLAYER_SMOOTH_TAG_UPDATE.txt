UPDATE MULTIPLAYER SMOOTH TAG

Perubahan:
1. Gerakan bola dan keeper dibuat smooth lagi. RETRO_MOTION_ENABLED diset false di src/Main.java.
2. Titik target kuning dan titik pilihan keeper biru di mode multiplayer tidak ditampilkan lagi.
3. PLAYER 1 diberi label merah dan PLAYER 2 diberi label biru.
4. Label player mengikuti role: kalau sedang jadi penendang label muncul di samping bola, kalau jadi keeper label muncul di samping keeper.
5. Label player memakai efek fade in/fade out ringan.

File utama yang berubah:
src/Main.java
