import javax.swing.*;
import java.awt.*;

public class GameGUI extends JFrame {
    private Game game;
    private Player player;
    private GamePanel gamePanel;
    private JLabel scoreLabel;
    private JLabel livesLabel;
    private JPanel infoPanel;

    public GameGUI(Player player) {
        this.player = player;
        this.game = new Game(player);

        setTitle("Penalty Shootout - " + player.getRole());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);

        initComponents();

        // Play game BGM
        SoundManager.getInstance().playBGM("game_bgm");

        setVisible(true);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Info Panel (Top)
        infoPanel = createInfoPanel();
        add(infoPanel, BorderLayout.NORTH);

        // Game Panel (Center)
        gamePanel = new GamePanel(this, game);
        add(gamePanel, BorderLayout.CENTER);

        // Control Panel (Bottom)
        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.SOUTH);

        // Key bindings
        setupKeyBindings();
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4));
        panel.setBackground(new Color(0, 0, 0, 200));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Player Name
        JLabel nameLabel = new JLabel("👤 " + player.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        nameLabel.setForeground(Color.WHITE);
        panel.add(nameLabel);

        // Role
        JLabel roleLabel = new JLabel("🎯 " +
                (player.getRole().equals("KICKER") ? "Penendang" : "Kiper"));
        roleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        roleLabel.setForeground(Color.YELLOW);
        panel.add(roleLabel);

        // Score
        scoreLabel = new JLabel("⭐ Score: 0");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 20));
        scoreLabel.setForeground(Color.GREEN);
        panel.add(scoreLabel);

        // Lives
        livesLabel = new JLabel("❤️ Lives: 3");
        livesLabel.setFont(new Font("Arial", Font.BOLD, 20));
        livesLabel.setForeground(Color.RED);
        panel.add(livesLabel);

        return panel;
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBackground(new Color(0, 0, 0, 200));

        // Pause button
        JButton pauseBtn = createControlButton("⏸️ PAUSE");
        pauseBtn.addActionListener(e -> togglePause());
        panel.add(pauseBtn);

        // Sound toggle
        JButton soundBtn = createControlButton("🔊 SOUND");
        soundBtn.addActionListener(e -> {
            SoundManager.getInstance().toggleMute();
            soundBtn.setText(SoundManager.getInstance().isMuted() ?
                    "🔇 MUTED" : "🔊 SOUND");
        });
        panel.add(soundBtn);

        // Exit button
        JButton exitBtn = createControlButton("🚪 EXIT");
        exitBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Yakin mau keluar? Progress akan hilang!",
                    "Konfirmasi",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                SoundManager.getInstance().stopAll();
                dispose();
                new GameMenu();
            }
        });
        panel.add(exitBtn);

        return panel;
    }

    private JButton createControlButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(52, 73, 94));
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(44, 62, 80));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(52, 73, 94));
            }

            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                SoundManager.getInstance().playSFX("button_click");
            }
        });

        return button;
    }

    private void setupKeyBindings() {
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("ESCAPE"), "pause");
        getRootPane().getActionMap().put("pause", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                togglePause();
            }
        });
    }

    private void togglePause() {
        // Implement pause functionality
        JOptionPane.showMessageDialog(this, "Game Paused", "Pause",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void updateUI() {
        scoreLabel.setText("⭐ Score: " + player.getScore());
        livesLabel.setText("❤️ Lives: " + player.getLives());
    }

    public void gameOver() {
        SoundManager.getInstance().playSFX("gameover");
        SoundManager.getInstance().stopBGM();

        // Save player data
        SaveData.savePlayer(player);

        // Show game over dialog
        SwingUtilities.invokeLater(() -> {
            GameOverDialog dialog = new GameOverDialog(this, player);
            dialog.setVisible(true);

            if (dialog.isPlayAgain()) {
                player.resetGame();
                dispose();
                new GameGUI(player);
            } else {
                dispose();
                new GameMenu();
            }
        });
    }
}