import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class Scoreboard {

    public static void displayLeaderboard(Component parent) {
        List<Player> players = SaveData.loadAllPlayers();

        // Sort by high score
        players.sort((p1, p2) -> Integer.compare(p2.getHighScore(), p1.getHighScore()));

        // Create dialog
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent),
                "🏆 LEADERBOARD", true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(parent);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(0, 30, 60));

        // Title
        JLabel titleLabel = new JLabel("🏆 TOP PLAYERS 🏆");
        titleLabel.setFont(new Font("Arial Black", Font.BOLD, 28));
        titleLabel.setForeground(Color.YELLOW);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Table
        if (players.isEmpty()) {
            JLabel emptyLabel = new JLabel("Belum ada data player");
            emptyLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            emptyLabel.setForeground(Color.WHITE);
            emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
            mainPanel.add(emptyLabel, BorderLayout.CENTER);
        } else {
            // Column names
            String[] columns = {"Rank", "Nama", "Role", "High Score", "Tanggal"};

            // Data
            Object[][] data = new Object[Math.min(players.size(), 10)][5];
            for (int i = 0; i < Math.min(players.size(), 10); i++) {
                Player p = players.get(i);
                data[i][0] = i + 1;
                data[i][1] = p.getName();
                data[i][2] = p.getRole().equals("KICKER") ? "🥅 Penendang" : "🧤 Kiper";
                data[i][3] = p.getHighScore();
                data[i][4] = p.getDate().substring(0, 10);
            }

            // Create table
            JTable table = new JTable(data, columns);
            styleTable(table);

            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            scrollPane.setOpaque(false);
            scrollPane.getViewport().setOpaque(false);

            mainPanel.add(scrollPane, BorderLayout.CENTER);
        }

        // Close button
        JButton closeBtn = new JButton("TUTUP");
        closeBtn.setFont(new Font("Arial", Font.BOLD, 16));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setBackground(new Color(231, 76, 60));
        closeBtn.setFocusPainted(false);
        closeBtn.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(closeBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private static void styleTable(JTable table) {
        // Table styling
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setForeground(Color.WHITE);
        table.setBackground(new Color(0, 50, 100));
        table.setGridColor(new Color(255, 255, 255, 50));
        table.setRowHeight(30);
        table.setShowVerticalLines(false);

        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setBackground(new Color(0, 70, 140));
        header.setForeground(Color.YELLOW);

        // Cell renderer for alternating colors
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table,
                                                           Object value, boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                if (row % 2 == 0) {
                    c.setBackground(new Color(0, 60, 120));
                } else {
                    c.setBackground(new Color(0, 50, 100));
                }

                setHorizontalAlignment(column == 0 ? CENTER : LEFT);

                // Gold, Silver, Bronze untuk top 3
                if (column == 0 && row < 3) {
                    String[] medals = {"🥇", "🥈", "🥉"};
                    setText(medals[row]);
                    setFont(new Font("Arial", Font.BOLD, 18));
                }

                return c;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
    }
}