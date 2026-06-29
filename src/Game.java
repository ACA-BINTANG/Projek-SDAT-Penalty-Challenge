import java.util.Random;

public class Game {
    private Player player;
    private Random random;
    private String[] goalPositions = {
            "KIRI ATAS", "KIRI BAWAH",
            "TENGAH ATAS", "TENGAH BAWAH",
            "KANAN ATAS", "KANAN BAWAH"
    };

    private String lastKickPosition;
    private String lastKeeperPosition;
    private boolean lastKickResult;
    private int streakCount = 0;

    public Game(Player player) {
        this.player = player;
        this.random = new Random();
    }

    public String getRandomKeeperPosition() {
        return goalPositions[random.nextInt(goalPositions.length)];
    }

    public String getRandomKickPosition() {
        return goalPositions[random.nextInt(goalPositions.length)];
    }

    public boolean processKick(String kickPosition) {
        lastKickPosition = kickPosition;
        lastKeeperPosition = getRandomKeeperPosition();

        // Cek apakah gol
        lastKickResult = !kickPosition.equals(lastKeeperPosition);

        if (lastKickResult) {
            // GOAL!
            int points = 10;

            // Bonus points untuk streak
            streakCount++;
            if (streakCount >= 5) {
                points += 5; // Bonus 5 points untuk 5 streak
            }
            if (streakCount >= 10) {
                points += 10; // Bonus 10 points untuk 10 streak
            }

            player.addScore(points);
            player.incrementKicks();

        } else {
            // Saved/Miss
            player.loseLife();
            streakCount = 0;
        }

        return lastKickResult;
    }

    public boolean processSave(String savePosition) {
        lastKeeperPosition = savePosition;
        lastKickPosition = getRandomKickPosition();

        // Cek apakah berhasil save
        lastKickResult = savePosition.equals(lastKickPosition);

        if (lastKickResult) {
            // SAVE!
            int points = 15;

            // Bonus points untuk streak
            streakCount++;
            if (streakCount >= 3) {
                points += 10; // Bonus untuk multiple saves
            }

            player.addScore(points);
            player.incrementSaves();

        } else {
            // Kebobolan
            player.loseLife();
            streakCount = 0;
        }

        return lastKickResult;
    }

    public String getLastKickPosition() {
        return lastKickPosition;
    }

    public String getLastKeeperPosition() {
        return lastKeeperPosition;
    }

    public boolean isLastKickResult() {
        return lastKickResult;
    }

    public int getStreakCount() {
        return streakCount;
    }

    public Player getPlayer() {
        return player;
    }
}