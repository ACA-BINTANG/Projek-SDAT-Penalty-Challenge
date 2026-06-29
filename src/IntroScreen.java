import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class IntroScreen extends JFrame {
    private JPanel mainPanel;
    private VideoPlayer videoPlayer;
    private JButton startButton;
    private JButton skipButton;
    private Timer fadeTimer;
    private float alpha = 0.0f;
    private boolean isTransitioning = false;

    public IntroScreen() {
        setTitle("Penalty Shootout - Infinity Mode");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);

        initComponents();

        // Play intro music
        SoundManager.getInstance().playBGM("intro_music");

        setVisible(true);

        // Auto transition setelah 10 detik
        new Timer(10000, e -> {
            if (!isTransitioning) {
                startGame();
            }
        }).start();
    }

    private void initComponents() {
        mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw video background
                if (videoPlayer != null) {
                    videoPlayer.paintVideo(g2d, getWidth(), getHeight());
                }

                // Overlay gradient (lebih gelap di bawah)
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(0, 0, 0, 100),
                        0, getHeight(), new Color(0, 0, 0, 220)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Animated title
                drawAnimatedTitle(g2d);

                // Instructions
                drawInstructions(g2d);
            }
        };
        mainPanel.setLayout(null);

        // Initialize video player
        videoPlayer = new VideoPlayer("resources/video/intro.mp4");
        videoPlayer.setBounds(0, 0, getWidth(), getHeight());

        // Create Start Button
        startButton = createStartButton();
        startButton.setBounds(getWidth()/2 - 150, getHeight() - 250, 300, 70);
        startButton.addActionListener(e -> {
            SoundManager.getInstance().playSFX("button_click");
            startGame();
        });

        // Create Skip Button
        skipButton = createSkipButton();
        skipButton.setBounds(getWidth() - 150, 20, 120, 40);
        skipButton.addActionListener(e -> startGame());

        // Add components
        mainPanel.add(startButton);
        mainPanel.add(skipButton);
        add(mainPanel);

        // Key listener
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE ||
                        e.getKeyCode() == KeyEvent.VK_ENTER) {
                    startGame();
                }
            }
        });
        setFocusable(true);

        // Fade in button
        fadeInButton();
    }

    private JButton createStartButton() {
        JButton button = new JButton("⚽ MULAI PERMAINAN") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                // Button gradient
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(255, 140, 0),
                        getWidth(), getHeight(), new Color(255, 69, 0)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);

                // Border glow
                g2d.setColor(new Color(255, 255, 255, 150));
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 25, 25);

                // Text with shadow
                g2d.setFont(new Font("Arial", Font.BOLD, 26));
                FontMetrics fm = g2d.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = (getHeight() + fm.getAscent()) / 2 - 5;

                // Shadow
                g2d.setColor(new Color(0, 0, 0, 100));
                g2d.drawString(getText(), textX + 2, textY + 2);

                // Main text
                g2d.setColor(Color.WHITE);
                g2d.drawString(getText(), textX, textY);

                g2d.dispose();
            }
        };

        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBounds(button.getX() - 5, button.getY() - 2,
                        button.getWidth() + 10, button.getHeight() + 4);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBounds(getWidth()/2 - 150, getHeight() - 250, 300, 70);
            }
        });

        return button;
    }

    private JButton createSkipButton() {
        JButton button = new JButton("SKIP »");
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    private void drawAnimatedTitle(Graphics2D g2d) {
        int baseY = getHeight() / 3;

        // Animated scale effect
        double scale = 1.0 + Math.sin(frameCount * 0.05) * 0.05;

        // Title shadow
        g2d.setFont(new Font("Arial Black", Font.BOLD, (int)(72 * scale)));
        FontMetrics fm = g2d.getFontMetrics();
        String title = "PENALTY SHOOTOUT";
        int titleX = (getWidth() - fm.stringWidth(title)) / 2;

        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.drawString(title, titleX + 3, baseY + 3);

        // Title gradient
        GradientPaint titleGradient = new GradientPaint(
                0, baseY - 50, Color.YELLOW,
                0, baseY + 20, Color.ORANGE
        );
        g2d.setPaint(titleGradient);
        g2d.drawString(title, titleX, baseY);

        // Subtitle
        g2d.setFont(new Font("Arial", Font.ITALIC, 36));
        fm = g2d.getFontMetrics();
        String subtitle = "INFINITY MODE";
        int subtitleX = (getWidth() - fm.stringWidth(subtitle)) / 2;

        g2d.setColor(new Color(255, 255, 255, 200));
        g2d.drawString(subtitle, subtitleX, baseY + 50);

        // Creator credit
        g2d.setFont(new Font("Arial", Font.PLAIN, 18));
        String credit = "Created by Bro";
        fm = g2d.getFontMetrics();
        int creditX = (getWidth() - fm.stringWidth(credit)) / 2;
        g2d.setColor(new Color(255, 255, 255, 150));
        g2d.drawString(credit, creditX, baseY + 100);
    }

    private void drawInstructions(Graphics2D g2d) {
        g2d.setFont(new Font("Arial", Font.PLAIN, 16));
        g2d.setColor(new Color(255, 255, 255, 180));

        String instruction = "Tekan SPACE atau klik tombol untuk mulai";
        FontMetrics fm = g2d.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(instruction)) / 2;
        int y = getHeight() - 100;

        // Blinking effect
        if ((System.currentTimeMillis() / 500) % 2 == 0) {
            g2d.drawString(instruction, x, y);
        }
    }

    private int frameCount = 0;

    private void fadeInButton() {
        startButton.setVisible(false);

        fadeTimer = new Timer(50, e -> {
            alpha += 0.05f;
            frameCount++;

            if (alpha >= 1.0f) {
                alpha = 1.0f;
                fadeTimer.stop();
            }
            startButton.setVisible(true);
            repaint();
        });
        fadeTimer.start();
    }

    private void startGame() {
        if (isTransitioning) return;
        isTransitioning = true;

        if (fadeTimer != null) {
            fadeTimer.stop();
        }

        if (videoPlayer != null) {
            videoPlayer.stopAnimation();
        }

        SoundManager.getInstance().stopBGM();

        // Transition effect
        Timer transitionTimer = new Timer(20, new AbstractAction() {
            float transitionAlpha = 0.0f;

            @Override
            public void actionPerformed(ActionEvent e) {
                transitionAlpha += 0.05f;

                if (transitionAlpha >= 1.0f) {
                    ((Timer)e.getSource()).stop();
                    dispose();
                    new GameMenu();
                }
                repaint();
            }
        });
        transitionTimer.start();
    }
}