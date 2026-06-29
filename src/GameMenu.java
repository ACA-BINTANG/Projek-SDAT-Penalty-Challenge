import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class GameMenu extends JFrame {
    private JTextField nameField;
    private JComboBox<String> roleCombo;
    private JSlider volumeSlider;
    private JCheckBox muteCheckBox;
    private JPanel mainPanel;
    private Timer animationTimer;
    private float titleBounce = 0;

    public GameMenu() {
        setTitle("Penalty Shootout - Main Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);

        initComponents();

        // Play background music
        SoundManager.getInstance().playBGM("game_bgm");

        // Start animation
        startAnimation();

        setVisible(true);
    }

    private void initComponents() {
        mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                // Background dengan gambar atau gradient
                drawBackground(g2d);

                // Animated elements
                drawAnimatedElements(g2d);
            }
        };
        mainPanel.setLayout(new GridBagLayout());

        // Panel menu
        JPanel menuPanel = createMenuPanel();
        menuPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        mainPanel.add(menuPanel, gbc);

        add(mainPanel);
    }

    private void drawBackground(Graphics2D g2d) {
        int w = getWidth();
        int h = getHeight();

        // Background gradient
        GradientPaint bgGradient = new GradientPaint(
                0, 0, new Color(0, 30, 60),
                0, h, new Color(0, 60, 100)
        );
        g2d.setPaint(bgGradient);
        g2d.fillRect(0, 0, w, h);

        // Pattern lines
        g2d.setColor(new Color(255, 255, 255, 20));
        for (int i = 0; i < w; i += 50) {
            g2d.drawLine(i, 0, i, h);
        }
    }

    private void drawAnimatedElements(Graphics2D g2d) {
        // Soccer ball decoration
        int ballX = getWidth() - 150;
        int ballY = (int)(100 + Math.sin(titleBounce) * 20);

        g2d.setColor(new Color(255, 255, 255, 30));
        g2d.fillOval(ballX, ballY, 100, 100);

        g2d.setColor(new Color(255, 255, 255, 50));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval(ballX, ballY, 100, 100);
    }

    private JPanel createMenuPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("⚽ PENALTY SHOOTOUT ⚽");
        titleLabel.setFont(new Font("Arial Black", Font.BOLD, 52));
        titleLabel.setForeground(Color.YELLOW);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 20, 30, 20);
        panel.add(titleLabel, gbc);

        // Subtitle
        JLabel subtitleLabel = new JLabel("INFINITY MODE");
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 24));
        subtitleLabel.setForeground(new Color(255, 200, 0));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 20, 20, 20);
        panel.add(subtitleLabel, gbc);

        // Separator
        JSeparator separator = new JSeparator();
        separator.setForeground(Color.WHITE);
        gbc.gridy = 2;
        gbc.insets = new Insets(10, 100, 20, 100);
        panel.add(separator, gbc);

        // Name Label
        JLabel nameLabel = new JLabel("🎮 NAMA PLAYER:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        nameLabel.setForeground(Color.WHITE);
        gbc.gridy = 3;
        gbc.insets = new Insets(10, 20, 5, 20);
        panel.add(nameLabel, gbc);

        // Name Field
        nameField = new JTextField(20);
        nameField.setFont(new Font("Arial", Font.PLAIN, 18));
        nameField.setBackground(new Color(255, 255, 255, 200));
        nameField.setForeground(Color.BLACK);
        nameField.setBorder(createRoundedBorder());
        gbc.gridy = 4;
        gbc.insets = new Insets(5, 20, 15, 20);
        gbc.ipady = 10;
        panel.add(nameField, gbc);

        // Role Label
        JLabel roleLabel = new JLabel("🎯 PILIH ROLE:");
        roleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        roleLabel.setForeground(Color.WHITE);
        gbc.gridy = 5;
        gbc.insets = new Insets(10, 20, 5, 20);
        gbc.ipady = 0;
        panel.add(roleLabel, gbc);

        // Role Combo
        String[] roles = {"🥅 Penendang (Kicker)", "🧤 Kiper (Goalkeeper)"};
        roleCombo = new JComboBox<>(roles);
        roleCombo.setFont(new Font("Arial", Font.PLAIN, 16));
        roleCombo.setBackground(new Color(255, 255, 255, 200));
        gbc.gridy = 6;
        gbc.insets = new Insets(5, 20, 15, 20);
        panel.add(roleCombo, gbc);

        // Volume Control
        JLabel volumeLabel = new JLabel("🔊 VOLUME:");
        volumeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        volumeLabel.setForeground(Color.WHITE);
        gbc.gridy = 7;
        gbc.insets = new Insets(10, 20, 5, 20);
        panel.add(volumeLabel, gbc);

        JPanel volumePanel = new JPanel(new BorderLayout());
        volumePanel.setOpaque(false);

        volumeSlider = new JSlider(0, 100, 70);
        volumeSlider.setOpaque(false);
        volumeSlider.addChangeListener(e -> {
            float volume = volumeSlider.getValue() / 100f;
            SoundManager.getInstance().setBGMVolume(volume);
        });

        muteCheckBox = new JCheckBox("MUTE");
        muteCheckBox.setFont(new Font("Arial", Font.PLAIN, 14));
        muteCheckBox.setForeground(Color.WHITE);
        muteCheckBox.setOpaque(false);
        muteCheckBox.addActionListener(e -> {
            SoundManager.getInstance().toggleMute();
        });

        volumePanel.add(volumeSlider, BorderLayout.CENTER);
        volumePanel.add(muteCheckBox, BorderLayout.EAST);

        gbc.gridy = 8;
        gbc.insets = new Insets(5, 20, 20, 20);
        panel.add(volumePanel, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        buttonPanel.setOpaque(false);

        // Start Button
        JButton startBtn = createStyledButton("⚽ MULAI GAME",
                new Color(46, 204, 113), new Color(39, 174, 96));
        startBtn.addActionListener(e -> startNewGame());
        buttonPanel.add(startBtn);

        // Leaderboard Button
        JButton leaderboardBtn = createStyledButton("🏆 LEADERBOARD",
                new Color(52, 152, 219), new Color(41, 128, 185));
        leaderboardBtn.addActionListener(e -> showLeaderboard());
        buttonPanel.add(leaderboardBtn);

        // Exit Button
        JButton exitBtn = createStyledButton("🚪 KELUAR",
                new Color(231, 76, 60), new Color(192, 57, 43));
        exitBtn.addActionListener(e -> {
            SoundManager.getInstance().stopAll();
            System.exit(0);
        });
        buttonPanel.add(exitBtn);

        gbc.gridy = 9;
        gbc.insets = new Insets(20, 20, 10, 20);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipady = 100;
        panel.add(buttonPanel, gbc);

        return panel;
    }

    private Border createRoundedBorder() {
        return BorderFactory.createCompoundBorder(
                new RoundedBorder(15, Color.WHITE),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        );
    }

    private JButton createStyledButton(String text, Color mainColor, Color hoverColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setForeground(Color.WHITE);
        button.setBackground(mainColor);
        button.setFocusPainted(false);
        button.setBorder(new RoundedBorder(10, mainColor.darker()));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
                button.setFont(new Font("Arial", Font.BOLD, 22));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(mainColor);
                button.setFont(new Font("Arial", Font.BOLD, 20));
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                SoundManager.getInstance().playSFX("button_click");
            }
        });

        return button;
    }

    private void startAnimation() {
        animationTimer = new Timer(50, e -> {
            titleBounce += 0.1;
            repaint();
        });
        animationTimer.start();
    }

    private void startNewGame() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Masukkan nama player dulu bro!",
                    "Error",
                    JOptionPane.WARNING_MESSAGE);
            nameField.requestFocus();
            return;
        }

        String role = roleCombo.getSelectedIndex() == 0 ? "KICKER" : "GOALKEEPER";
        Player player = new Player(name, role);

        if (animationTimer != null) {
            animationTimer.stop();
        }

        dispose();
        new GameGUI(player);
    }

    private void showLeaderboard() {
        SwingUtilities.invokeLater(() -> {
            Scoreboard.displayLeaderboard(this);
        });
    }

    @Override
    public void dispose() {
        super.dispose();
        if (animationTimer != null) {
            animationTimer.stop();
        }
    }
}

// Rounded Border Class
class RoundedBorder extends AbstractBorder {
    private int radius;
    private Color color;

    RoundedBorder(int radius, Color color) {
        this.radius = radius;
        this.color = color;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(color);
        g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        g2d.dispose();
    }
}