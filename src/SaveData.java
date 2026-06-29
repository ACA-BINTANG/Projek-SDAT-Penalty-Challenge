import java.io.*;
import java.util.*;

public class SaveData {
    private static final String SAVE_FILE = "players_data.dat";

    public static void savePlayer(Player player) {
        List<Player> players = loadAllPlayers();

        // Update existing player or add new
        boolean found = false;
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getName().equals(player.getName()) &&
                    players.get(i).getRole().equals(player.getRole())) {
                // Update only if new high score
                if (player.getHighScore() > players.get(i).getHighScore()) {
                    players.set(i, player);
                }
                found = true;
                break;
            }
        }

        if (!found) {
            players.add(player);
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(SAVE_FILE))) {
            oos.writeObject(players);
            System.out.println("Data saved successfully!");
        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static List<Player> loadAllPlayers() {
        List<Player> players = new ArrayList<>();
        File file = new File(SAVE_FILE);

        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(file))) {
                players = (List<Player>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error loading data: " + e.getMessage());
                // Return empty list if file corrupted
                return new ArrayList<>();
            }
        }

        return players;
    }

    public static Player getPlayerHighScore(String name) {
        List<Player> players = loadAllPlayers();

        for (Player p : players) {
            if (p.getName().equals(name)) {
                return p;
            }
        }

        return null;
    }

    public static void clearAllData() {
        File file = new File(SAVE_FILE);
        if (file.exists()) {
            file.delete();
            System.out.println("All data cleared!");
        }
    }
}