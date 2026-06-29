import javax.swing.*;
import java.awt.*;

public class GameOverDialog extends JDialog {
    private boolean playAgain = false;

    public GameOverDialog(JFrame parent, Player player) {
        super(parent, "GAME OVER", true);

        setSize(400, 300);
        setLocationRelativeTo(parent);
        setUndecorated(true);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                // Background gradient
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(0, 0, 0),
                        0, getHeight(), new Color(50, 0, 0)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Border
                g2d.setColor(Color.YELLOW);
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRoundRect(5, 5, getWidth()-10, getHeight()-10, 20, 20);
            }
        };
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(10, 20, 10, 20);

        // Game Over Title
        JLabel titleLabel = new JLabel("GAME OVER!");
        titleLabel.setFont(new Font("Arial Black", Font.BOLD, 36));
        titleLabel.setForeground(Color.RED);
        gbc.gridy = 0;
        panel.add(titleLabel, gbc);

        // Stats
        JLabel statsLabel = new JLabel("<html>" +
                "<center>" +
                "Player: " + player.getName() + "<br>" +
                "Role: " + (player.getRole().equals("KICKER") ? "Penendang" : "Kiper") + "<br>" +
                "Final Score: " + player.getScore() + "<br>" +
                "High Score: " + player.getHighScore() + "<br>" +
                "</center>" +
                "</html>");
        statsLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        statsLabel.setForeground(Color.WHITE);
        statsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        panel.add(statsLabel, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);

        JButton playAgainBtn = new JButton("🔄 MAIN LAGI");
        playAgainBtn.setFont(new Font("Arial", Font.BOLD, 16));
        playAgainBtn.setForeground(Color.WHITE);
        playAgainBtn.setBackground(new Color(46, 204, 113));
        playAgainBtn.addActionListener(e -> {
            playAgain = true;
            dispose();
        });
        buttonPanel.add(playAgainBtn);

        JButton menuBtn = new JButton("🏠 MENU");
        menuBtn.setFont(new Font("Arial", Font.BOLD, 16));
        menuBtn.setForeground(Color.WHITE);
        menuBtn.setBackground(new Color(52, 152, 219));
        menuBtn.addActionListener(e -> {
            playAgain = false;
            dispose();
        });
        buttonPanel.add(menuBtn);

        gbc.gridy = 2;
        panel.add(buttonPanel, gbc);

        add(panel);
    }

    public boolean isPlayAgain() {
        return playAgain;
    }
}