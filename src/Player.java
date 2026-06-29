import java.io.Serializable;

public class Player implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private int score;
    private int highScore;
    private int lives;
    private String role; // "KICKER" atau "GOALKEEPER"
    private int totalKicks;
    private int totalSaves;
    private String date;

    public Player(String name, String role) {
        this.name = name;
        this.role = role;
        this.score = 0;
        this.highScore = 0;
        this.lives = 3;
        this.totalKicks = 0;
        this.totalSaves = 0;
        this.date = java.time.LocalDateTime.now().toString();
    }

    // Getters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getScore() { return score; }
    public int getHighScore() { return highScore; }
    public int getLives() { return lives; }
    public String getRole() { return role; }
    public int getTotalKicks() { return totalKicks; }
    public int getTotalSaves() { return totalSaves; }
    public String getDate() { return date; }

    // Game methods
    public void addScore(int points) {
        this.score += points;
        if (score > highScore) {
            highScore = score;
        }
    }

    public void loseLife() {
        this.lives--;
    }

    public boolean isAlive() {
        return lives > 0;
    }

    public void incrementKicks() {
        totalKicks++;
    }

    public void incrementSaves() {
        totalSaves++;
    }

    public void resetGame() {
        this.score = 0;
        this.lives = 3;
    }
}