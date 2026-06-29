import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Set Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Jalankan di Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            new IntroScreen();
        });
    }
}