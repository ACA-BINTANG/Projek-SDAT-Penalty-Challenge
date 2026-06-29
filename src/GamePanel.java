import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class GamePanel extends JPanel {
    private GameGUI parent;
    private Game game;
    private Player player;

    // Animasi
    private Timer animationTimer;
    private float ballX = 400, ballY = 300;
    private float ballTargetX = 400, ballTargetY = 300;
    private boolean isAnimating = false;
    private String animationType = ""; // "GOAL", "SAVE", "MISS"
    private int animationFrame = 0;

    // UI Elements
    private Rectangle[] goalAreas;
    private Rectangle goalFrame;
    private boolean showResult = false;
    private String resultText = "";
    private Color resultColor = Color.WHITE;

    public GamePanel(GameGUI parent, Game game) {
        this.parent = parent;
        this.game = game;
        this.player = game.getPlayer();

        setPreferredSize(new Dimension(800, 600));
        setBackground(new Color(0, 100, 0)); // Green pitch

        initGoalAreas();
        setupMouseListener();
        startAnimationLoop();
    }

    private void initGoalAreas() {
        // Frame gawang
        int goalWidth = 300;
        int goalHeight = 200;
        int goalX = (800 - goalWidth) / 2;
        int goalY = 100;
        goalFrame = new Rectangle(goalX, goalY, goalWidth, goalHeight);

        // 6 area gawang (2 kolom x 3 baris)
        goalAreas = new Rectangle[6];
        int areaWidth = goalWidth / 3;
        int areaHeight = goalHeight / 2;

        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 3; col++) {
                int index = row * 3 + col;
                goalAreas[index] = new Rectangle(
                        goalX + col * areaWidth,
                        goalY + row * areaHeight,
                        areaWidth,
                        areaHeight
                );
            }
        }
    }

    private void setupMouseListener() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isAnimating) return;

                // Cek klik di area gawang
                for (int i = 0; i < goalAreas.length; i++) {
                    if (goalAreas[i].contains(e.getPoint())) {
                        handleKick(i);
                        break;
                    }
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                // Cursor berubah saat hover di gawang
                boolean overGoal = false;
                for (Rectangle area : goalAreas) {
                    if (area.contains(e.getPoint())) {
                        overGoal = true;
                        break;
                    }
                }
                setCursor(overGoal ?
                        new Cursor(Cursor.HAND_CURSOR) :
                        new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
    }

    private void handleKick(int areaIndex) {
        if (!player.isAlive()) return;

        String[] positions = {"KIRI ATAS", "KIRI BAWAH",
                "TENGAH ATAS", "TENGAH BAWAH",
                "KANAN ATAS", "KANAN BAWAH"};

        String kickPos = positions[areaIndex];
        boolean isGoal;

        if (player.getRole().equals("KICKER")) {
            isGoal = game.processKick(kickPos);
            SoundManager.getInstance().playSFX("kick");

            // Animasi
            animateBall(isGoal);

            if (isGoal) {
                showResult = true;
                resultText = "⚽ GOOOLLL!!!";
                resultColor = Color.GREEN;
                SoundManager.getInstance().playSFX("goal");
            } else {
                showResult = true;
                resultText = "❌ Diselamatkan!";
                resultColor = Color.RED;
                SoundManager.getInstance().playSFX("save");
            }
        } else {
            isGoal = game.processSave(kickPos);

            // Animasi untuk keeper
            animateBall(isGoal);

            if (!isGoal) {
                showResult = true;
                resultText = "🧤 SAVE!";
                resultColor = Color.GREEN;
                SoundManager.getInstance().playSFX("save");
            } else {
                showResult = true;
                resultText = "❌ Kebobolan!";
                resultColor = Color.RED;
                SoundManager.getInstance().playSFX("goal");
            }
        }

        // Update UI
        parent.updateUI();

        // Hide result after delay
        Timer hideResult = new Timer(1500, e -> {
            showResult = false;
            repaint();
        });
        hideResult.setRepeats(false);
        hideResult.start();

        // Check game over
        if (!player.isAlive()) {
            Timer gameOverTimer = new Timer(1000, e -> {
                SoundManager.getInstance().playSFX("gameover");
                parent.gameOver();
            });
            gameOverTimer.setRepeats(false);
            gameOverTimer.start();
        }
    }

    private void animateBall(boolean isGoal) {
        isAnimating = true;
        animationFrame = 0;

        // Set target position
        ballTargetX = 400;
        ballTargetY = isGoal ? -50 : 300; // Goal: ke atas, Save: ke tengah

        animationTimer = new Timer(16, e -> {
            animationFrame++;

            // Move ball toward target
            ballX += (ballTargetX - ballX) * 0.1f;
            ballY += (ballTargetY - ballY) * 0.1f;

            if (animationFrame > 60) {
                isAnimating = false;
                animationTimer.stop();
                ballX = 400;
                ballY = 300;
            }
            repaint();
        });
        animationTimer.start();
    }

    private void startAnimationLoop() {
        // General animation loop
        Timer loop = new Timer(30, e -> repaint());
        loop.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw pitch
        drawPitch(g2d);

        // Draw goal
        drawGoal(g2d);

        // Draw goalkeeper
        drawGoalkeeper(g2d);

        // Draw ball
        drawBall(g2d);

        // Draw result overlay
        if (showResult) {
            drawResult(g2d);
        }

        // Draw UI info
        drawGameInfo(g2d);
    }

    private void drawPitch(Graphics2D g2d) {
        int w = getWidth();
        int h = getHeight();

        // Grass gradient
        GradientPaint grassGradient = new GradientPaint(
                0, 0, new Color(34, 139, 34),
                0, h, new Color(0, 100, 0)
        );
        g2d.setPaint(grassGradient);
        g2d.fillRect(0, 0, w, h);

        // Pitch lines
        g2d.setColor(new Color(255, 255, 255, 100));
        g2d.setStroke(new BasicStroke(2));

        // Center line
        g2d.drawLine(0, h/2, w, h/2);

        // Center circle
        g2d.drawOval(w/2 - 50, h/2 - 50, 100, 100);

        // Penalty spot
        g2d.fillOval(w/2 - 5, h - 150, 10, 10);
    }

    private void drawGoal(Graphics2D g2d) {
        // Goal frame
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(4));
        g2d.draw(goalFrame);

        // Goal net
        g2d.setColor(new Color(255, 255, 255, 30));
        g2d.fill(goalFrame);

        // Net pattern
        g2d.setColor(new Color(255, 255, 255, 50));
        g2d.setStroke(new BasicStroke(1));

        // Vertical lines
        for (int x = goalFrame.x; x <= goalFrame.x + goalFrame.width; x += 30) {
            g2d.drawLine(x, goalFrame.y, x, goalFrame.y + goalFrame.height);
        }

        // Horizontal lines
        for (int y = goalFrame.y; y <= goalFrame.y + goalFrame.height; y += 30) {
            g2d.drawLine(goalFrame.x, y, goalFrame.x + goalFrame.width, y);
        }

        // Area labels
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 11));
        String[][] labels = {{"KIRI ATAS", "TENGAH ATAS", "KANAN ATAS"},
                {"KIRI BAWAH", "TENGAH BAWAH", "KANAN BAWAH"}};

        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 3; col++) {
                Rectangle area = goalAreas[row * 3 + col];
                String label = labels[row][col];
                FontMetrics fm = g2d.getFontMetrics();
                int textX = area.x + (area.width - fm.stringWidth(label)) / 2;
                int textY = area.y + (area.height + fm.getAscent()) / 2;
                g2d.drawString(label, textX, textY);
            }
        }

        // Hover effect
        Point mousePos = getMousePosition();
        if (mousePos != null) {
            for (Rectangle area : goalAreas) {
                if (area.contains(mousePos)) {
                    g2d.setColor(new Color(255, 255, 0, 100));
                    g2d.fill(area);
                    g2d.setColor(Color.YELLOW);
                    g2d.setStroke(new BasicStroke(3));
                    g2d.draw(area);
                    break;
                }
            }
        }
    }

    private void drawGoalkeeper(Graphics2D g2d) {
        // Simple goalkeeper figure
        int keeperX = goalFrame.x + goalFrame.width / 2;
        int keeperY = goalFrame.y + goalFrame.height / 2;

        // Body
        g2d.setColor(new Color(255, 200, 0));
        g2d.fillOval(keeperX - 20, keeperY - 20, 40, 40);

        // Head
        g2d.setColor(new Color(255, 220, 150));
        g2d.fillOval(keeperX - 12, keeperY - 35, 24, 24);

        // Gloves
        g2d.setColor(Color.RED);
        g2d.fillOval(keeperX - 30, keeperY - 15, 15, 15);
        g2d.fillOval(keeperX + 15, keeperY - 15, 15, 15);
    }

    private void drawBall(Graphics2D g2d) {
        // Soccer ball
        int ballSize = 30;

        // Ball shadow
        g2d.setColor(new Color(0, 0, 0, 50));
        g2d.fillOval((int)ballX - ballSize/2 + 3,
                (int)ballY - ballSize/2 + 3,
                ballSize, ballSize);

        // Ball body
        g2d.setColor(Color.WHITE);
        g2d.fillOval((int)ballX - ballSize/2,
                (int)ballY - ballSize/2,
                ballSize, ballSize);

        // Ball pattern
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval((int)ballX - ballSize/2,
                (int)ballY - ballSize/2,
                ballSize, ballSize);

        // Pentagon pattern
        int[] pentagonX = {(int)ballX - 8, (int)ballX, (int)ballX + 8,
                (int)ballX + 5, (int)ballX - 5};
        int[] pentagonY = {(int)ballY - 5, (int)ballY - 12, (int)ballY - 5,
                (int)ballY + 5, (int)ballY + 5};
        g2d.fillPolygon(pentagonX, pentagonY, 5);
    }

    private void drawResult(Graphics2D g2d) {
        // Semi-transparent overlay
        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.fillRect(0, getHeight()/2 - 50, getWidth(), 100);

        // Result text
        g2d.setFont(new Font("Arial Black", Font.BOLD, 36));
        FontMetrics fm = g2d.getFontMetrics();
        int textX = (getWidth() - fm.stringWidth(resultText)) / 2;
        int textY = getHeight()/2 + fm.getAscent()/2;

        // Text shadow
        g2d.setColor(Color.BLACK);
        g2d.drawString(resultText, textX + 2, textY + 2);

        // Text
        g2d.setColor(resultColor);
        g2d.drawString(resultText, textX, textY);
    }

    private void drawGameInfo(Graphics2D g2d) {
        int margin = 20;

        // Player info
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        g2d.setColor(Color.WHITE);
        g2d.drawString("Player: " + player.getName(), margin, 30);
        g2d.drawString("Role: " + (player.getRole().equals("KICKER") ? "Penendang" : "Kiper"),
                margin, 55);
        g2d.drawString("Score: " + player.getScore(), margin, 80);

        // Lives (hearts)
        g2d.setColor(Color.RED);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        StringBuilder lives = new StringBuilder("❤️".repeat(Math.max(0, player.getLives())));
        g2d.drawString("Lives: " + lives, margin, 110);

        // Streak
        if (game.getStreakCount() > 0) {
            g2d.setColor(Color.YELLOW);
            g2d.setFont(new Font("Arial", Font.BOLD, 18));
            g2d.drawString("🔥 Streak: " + game.getStreakCount() + "x",
                    getWidth() - 200, 30);
        }

        // Instructions
        g2d.setFont(new Font("Arial", Font.PLAIN, 14));
        g2d.setColor(new Color(255, 255, 255, 150));
        String instruction = player.getRole().equals("KICKER") ?
                "Klik area gawang untuk menendang" :
                "Klik area gawang untuk menangkap";
        g2d.drawString(instruction, getWidth() - 300, getHeight() - 20);
    }
}