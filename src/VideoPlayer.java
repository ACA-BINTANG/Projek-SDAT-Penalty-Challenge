import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class VideoPlayer extends JPanel {
    private BufferedImage placeholderImage;
    private boolean hasVideo = false;
    private Timer animationTimer;
    private int frameCount = 0;

    public VideoPlayer(String videoPath) {
        setOpaque(false);

        // Coba load video placeholder
        File videoFile = new File(videoPath);
        if (videoFile.exists()) {
            hasVideo = true;
            System.out.println("Video found: " + videoPath);
            // Note: Untuk video sebenarnya, gunakan JavaFX MediaPlayer atau VLCJ
            loadPlaceholderVideo();
        } else {
            System.out.println("Video not found, using placeholder: " + videoPath);
            createPlaceholderAnimation();
        }

        startAnimation();
    }

    private void createPlaceholderAnimation() {
        // Buat gambar animasi sederhana
        placeholderImage = new BufferedImage(1920, 1080, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = placeholderImage.createGraphics();

        // Background gradient
        GradientPaint gradient = new GradientPaint(
                0, 0, new Color(0, 20, 50),
                1920, 1080, new Color(0, 50, 100)
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, 1920, 1080);

        g2d.dispose();
    }

    private void loadPlaceholderVideo() {
        // Simulasi video dengan animasi
        createPlaceholderAnimation();
        animationTimer = new Timer(50, e -> {
            frameCount++;
            repaint();
        });
    }

    private void startAnimation() {
        if (animationTimer == null) {
            animationTimer = new Timer(50, e -> {
                frameCount++;
                repaint();
            });
        }
        animationTimer.start();
    }

    public void stopAnimation() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
    }

    public void paintVideo(Graphics2D g2d, int width, int height) {
        if (placeholderImage != null) {
            g2d.drawImage(placeholderImage, 0, 0, width, height, this);
        }

        // Tambahkan efek animasi particle
        drawParticles(g2d, width, height);
    }

    private void drawParticles(Graphics2D g2d, int width, int height) {
        // Animasi bola bergerak (simulasi video)
        g2d.setColor(new Color(255, 255, 255, 100));

        for (int i = 0; i < 5; i++) {
            int x = (int)(Math.sin((frameCount + i * 50) * 0.02) * width/3 + width/2);
            int y = (int)(Math.cos((frameCount + i * 50) * 0.03) * height/3 + height/2);
            int size = 20 + (i * 5);

            // Draw soccer ball particle
            g2d.fillOval(x - size/2, y - size/2, size, size);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (placeholderImage != null) {
            Graphics2D g2d = (Graphics2D) g;
            paintVideo(g2d, getWidth(), getHeight());
        }
    }
}