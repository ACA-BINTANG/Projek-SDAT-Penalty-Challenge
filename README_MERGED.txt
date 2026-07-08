Project hasil gabungan:

- Basis project: versi yang punya mode Tournament dan bracket.
- Asset keeper hijau + animasi frame-to-frame: diambil dari resources/images/karakter/keper.
- Main.java sudah diarahkan ke asset keeper hijau, bukan KEEPER-01.png statis.
- Endless dan Tournament sama-sama memakai KeeperAnimator.

Cara run di IntelliJ:
1. Buka folder project ini.
2. Pastikan JavaFX SDK sudah dipasang.
3. VM Options contoh:
   --module-path D:\javafx-sdk-26.0.1\lib --add-modules javafx.controls,javafx.fxml,javafx.media
4. Run Main.java.
