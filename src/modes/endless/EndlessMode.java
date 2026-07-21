package modes.endless;

import core.GameEngine;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.List;
/**
 * MODE ENDLESS
 *
 * Alur dasar:
 * 1. Membuat tampilan mode Endless.
 * 2. Menyimpan skor dan nyawa pemain.
 * 3. Menerima input tendangan dari mouse.
 * 4. Menjalankan update permainan setiap frame.
 * 5. Menyimpan skor ke scoreboard saat game selesai.
 */
public class EndlessMode extends GameEngine {
    @Override
    public void showEndlessMode(Stage stage) {
        // 1. MEMBUAT TAMPILAN MODE

        StackPane root = new StackPane();
        Pane playLayer = new Pane();

        ImageView background = createImageView(GOAL_BACKGROUND_PATH);
        background.setPreserveRatio(false);
        background.fitWidthProperty().bind(root.widthProperty());
        background.fitHeightProperty().bind(root.heightProperty());

        ImageView keeper = createImageView(KEEPER_IMAGE_PATH);
        keeper.setFitWidth(KEEPER_SIZE);
        keeper.setFitHeight(KEEPER_SIZE);
        keeper.setPreserveRatio(true);
        KeeperAnimator keeperAnimator = new KeeperAnimator(keeper);
        keeperAnimator.showIdle();

        ImageView ball = createImageView(BALL_IMAGE_PATH);
        ball.setFitWidth(BALL_SIZE);
        ball.setFitHeight(BALL_SIZE);
        ball.setPreserveRatio(true);
        ball.setCursor(Cursor.HAND);

        Line pullLine = new Line();
        pullLine.setStroke(Color.rgb(255, 255, 255, 0.75));
        pullLine.setStrokeWidth(4);
        pullLine.setVisible(false);

        Circle targetMarker = new Circle(8);
        targetMarker.setFill(Color.rgb(255, 235, 95, 0.82));
        targetMarker.setStroke(Color.rgb(255, 255, 255, 0.9));
        targetMarker.setStrokeWidth(2);
        targetMarker.setMouseTransparent(true);
        targetMarker.setVisible(false);

        Rectangle pointBoxOverlay = new Rectangle();
        pointBoxOverlay.setFill(Color.rgb(255, 0, 0, 0.24));
        pointBoxOverlay.setStroke(Color.rgb(255, 0, 0, 0.78));
        pointBoxOverlay.setStrokeWidth(3);
        pointBoxOverlay.setMouseTransparent(true);
        pointBoxOverlay.setVisible(SHOW_DEBUG_BOXES);
        pointBoxOverlay.layoutXProperty().bind(root.widthProperty().multiply(GOAL_LEFT_RATIO));
        pointBoxOverlay.layoutYProperty().bind(root.heightProperty().multiply(GOAL_TOP_RATIO));
        pointBoxOverlay.widthProperty().bind(root.widthProperty().multiply(GOAL_RIGHT_RATIO - GOAL_LEFT_RATIO));
        pointBoxOverlay.heightProperty().bind(root.heightProperty().multiply(GOAL_BOTTOM_RATIO - GOAL_TOP_RATIO));

        Rectangle keeperBoxOverlay = new Rectangle();
        keeperBoxOverlay.setFill(Color.rgb(0, 255, 80, 0.18));
        keeperBoxOverlay.setStroke(Color.rgb(0, 255, 80, 0.82));
        keeperBoxOverlay.setStrokeWidth(3);
        keeperBoxOverlay.setMouseTransparent(true);
        keeperBoxOverlay.setVisible(SHOW_DEBUG_BOXES);

        Rectangle topHudBackground = createTopHudBackground(root, 104);

        Text scoreText = new Text("POINT: 0");
        scoreText.setFill(Color.WHITE);
        scoreText.setFont(loadFont(MENU_FONT_PATH, 30, Font.font("Arial", FontWeight.BOLD, 30)));
        scoreText.layoutXProperty().bind(Bindings.createDoubleBinding(
                () -> (root.getWidth() - scoreText.getLayoutBounds().getWidth()) / 2,
                root.widthProperty(),
                scoreText.layoutBoundsProperty()
        ));
        scoreText.setLayoutY(58);

        Text livesText = new Text("DARAH: " + MAX_PLAYER_LIVES);
        livesText.setFill(Color.WHITE);
        livesText.setFont(loadFont(MENU_FONT_PATH, 24, Font.font("Arial", FontWeight.BOLD, 24)));
        livesText.setLayoutX(32);
        livesText.setLayoutY(52);

        Rectangle[] lifeIndicators = new Rectangle[MAX_PLAYER_LIVES];
        HBox livesIndicatorBox = new HBox(8);
        livesIndicatorBox.setLayoutX(158);
        livesIndicatorBox.setLayoutY(31);
        livesIndicatorBox.setMouseTransparent(true);
        for (int i = 0; i < lifeIndicators.length; i++) {
            Rectangle indicator = new Rectangle(28, 18);
            indicator.setArcWidth(6);
            indicator.setArcHeight(6);
            indicator.setFill(Color.rgb(224, 42, 42));
            indicator.setStroke(Color.WHITE);
            indicator.setStrokeWidth(2);
            lifeIndicators[i] = indicator;
            livesIndicatorBox.getChildren().add(indicator);
        }

        Text hintText = new Text("Tarik bola, lalu lepas");
        hintText.setFill(Color.rgb(255, 255, 255, 0.82));
        hintText.setFont(loadFont(MENU_FONT_PATH, 18, Font.font("Arial", FontWeight.BOLD, 18)));
        hintText.setLayoutX(32);
        hintText.setLayoutY(82);

        Text shortcutText = new Text("ESC = MENU");
        shortcutText.setFill(Color.rgb(255, 255, 255, 0.9));
        shortcutText.setFont(loadFont(MENU_FONT_PATH, 16, Font.font("Arial", FontWeight.BOLD, 16)));
        shortcutText.layoutXProperty().bind(Bindings.createDoubleBinding(
                () -> root.getWidth() - shortcutText.getLayoutBounds().getWidth() - 32,
                root.widthProperty(),
                shortcutText.layoutBoundsProperty()
        ));
        shortcutText.setLayoutY(34);

        Button scoreboardButton = new Button("SCOREBOARD");
        scoreboardButton.setFont(loadFont(MENU_FONT_PATH, 14, Font.font("Arial", FontWeight.BOLD, 14)));
        scoreboardButton.setCursor(Cursor.HAND);
        scoreboardButton.setFocusTraversable(false);
        scoreboardButton.setStyle(
                "-fx-background-color: #111111;" +
                "-fx-text-fill: white;" +
                "-fx-border-color: white;" +
                "-fx-border-width: 2;" +
                "-fx-padding: 6 12 6 12;"
        );
        scoreboardButton.layoutXProperty().bind(Bindings.createDoubleBinding(
                () -> root.getWidth() - scoreboardButton.getBoundsInLocal().getWidth() - 32,
                root.widthProperty(),
                scoreboardButton.boundsInLocalProperty()
        ));
        scoreboardButton.setLayoutY(52);

        Text scoreboardTitle = new Text("ENDLESS SCOREBOARD");
        scoreboardTitle.setFill(Color.rgb(255, 235, 120));
        scoreboardTitle.setFont(loadFont(MENU_FONT_PATH, 28, Font.font("Arial", FontWeight.BOLD, 28)));

        Text scoreboardContent = new Text();
        scoreboardContent.setFill(Color.WHITE);
        scoreboardContent.setFont(loadFont(MENU_FONT_PATH, 19, Font.font("Monospaced", FontWeight.BOLD, 19)));

        Button previousScorePageButton = new Button("< PREV");
        Button nextScorePageButton = new Button("NEXT >");
        Text scoreboardPageText = new Text();
        scoreboardPageText.setFill(Color.rgb(255, 235, 120));
        scoreboardPageText.setFont(loadFont(MENU_FONT_PATH, 14, Font.font("Arial", FontWeight.BOLD, 14)));

        String scoreboardPageButtonStyle =
                "-fx-background-color: #111111;" +
                "-fx-text-fill: white;" +
                "-fx-border-color: white;" +
                "-fx-border-width: 2;" +
                "-fx-padding: 6 12 6 12;";
        previousScorePageButton.setStyle(scoreboardPageButtonStyle);
        nextScorePageButton.setStyle(scoreboardPageButtonStyle);
        previousScorePageButton.setCursor(Cursor.HAND);
        nextScorePageButton.setCursor(Cursor.HAND);
        previousScorePageButton.setFocusTraversable(false);
        nextScorePageButton.setFocusTraversable(false);
        previousScorePageButton.setFont(loadFont(MENU_FONT_PATH, 13, Font.font("Arial", FontWeight.BOLD, 13)));
        nextScorePageButton.setFont(loadFont(MENU_FONT_PATH, 13, Font.font("Arial", FontWeight.BOLD, 13)));

        HBox scoreboardPagination = new HBox(14, previousScorePageButton, scoreboardPageText, nextScorePageButton);
        scoreboardPagination.setAlignment(Pos.CENTER);

        final int scoreboardPageSize = 10;
        final int[] scoreboardPage = {0};
        Runnable refreshScoreboard = () -> {
            List<EndlessScoreEntry> entries = loadEndlessScoreboardEntries();
            int totalPages = Math.max(1, (entries.size() + scoreboardPageSize - 1) / scoreboardPageSize);
            if (scoreboardPage[0] >= totalPages) {
                scoreboardPage[0] = totalPages - 1;
            }
            if (scoreboardPage[0] < 0) {
                scoreboardPage[0] = 0;
            }

            scoreboardContent.setText(formatEndlessScoreboardPage(entries, scoreboardPage[0], scoreboardPageSize));
            scoreboardPageText.setText(
                    "HALAMAN " + (scoreboardPage[0] + 1) + " / " + totalPages +
                    "   •   TOTAL " + entries.size() + " DATA"
            );
            previousScorePageButton.setDisable(scoreboardPage[0] <= 0);
            nextScorePageButton.setDisable(scoreboardPage[0] >= totalPages - 1);
        };

        previousScorePageButton.setOnAction(event -> {
            if (scoreboardPage[0] > 0) {
                scoreboardPage[0]--;
                refreshScoreboard.run();
            }
        });
        nextScorePageButton.setOnAction(event -> {
            scoreboardPage[0]++;
            refreshScoreboard.run();
        });

        Text scoreboardHint = new Text("10 DATA PER HALAMAN • DIURUTKAN DARI SKOR TERTINGGI");
        scoreboardHint.setFill(Color.rgb(210, 210, 210));
        scoreboardHint.setFont(loadFont(MENU_FONT_PATH, 14, Font.font("Arial", FontWeight.BOLD, 14)));

        VBox scoreboardPanel = new VBox(16, scoreboardTitle, scoreboardContent, scoreboardPagination, scoreboardHint);
        scoreboardPanel.setAlignment(Pos.TOP_CENTER);
        scoreboardPanel.setPadding(new Insets(24, 34, 24, 34));
        scoreboardPanel.setBackground(new Background(new BackgroundFill(
                Color.rgb(0, 0, 0, 0.92), new CornerRadii(10), Insets.EMPTY
        )));
        scoreboardPanel.setStyle("-fx-border-color: white; -fx-border-width: 3;");
        scoreboardPanel.setMaxWidth(560);
        scoreboardPanel.setMinWidth(480);
        scoreboardPanel.setVisible(false);
        scoreboardPanel.layoutXProperty().bind(Bindings.createDoubleBinding(
                () -> (root.getWidth() - scoreboardPanel.getBoundsInLocal().getWidth()) / 2,
                root.widthProperty(),
                scoreboardPanel.boundsInLocalProperty()
        ));
        scoreboardPanel.layoutYProperty().bind(Bindings.createDoubleBinding(
                () -> Math.max(125, (root.getHeight() - scoreboardPanel.getBoundsInLocal().getHeight()) / 2),
                root.heightProperty(),
                scoreboardPanel.boundsInLocalProperty()
        ));

        scoreboardButton.setOnAction(event -> {
            boolean show = !scoreboardPanel.isVisible();
            if (show) {
                scoreboardPage[0] = 0;
                refreshScoreboard.run();
            }
            scoreboardPanel.setVisible(show);
            scoreboardButton.setText(show ? "TUTUP SCOREBOARD" : "SCOREBOARD");
        });

        Text goalText = createGoalText(root);

        Rectangle gameOverOverlay = new Rectangle();
        gameOverOverlay.widthProperty().bind(root.widthProperty());
        gameOverOverlay.heightProperty().bind(root.heightProperty());
        gameOverOverlay.setFill(Color.rgb(0, 0, 0, 0.58));
        gameOverOverlay.setMouseTransparent(true);
        gameOverOverlay.setVisible(false);

        Text gameOverText = new Text("GAME OVER");
        gameOverText.setFill(Color.WHITE);
        gameOverText.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        gameOverText.setFont(loadFont(MENU_FONT_PATH, 42, Font.font("Arial", FontWeight.EXTRA_BOLD, 42)));
        gameOverText.layoutXProperty().bind(Bindings.createDoubleBinding(
                () -> (root.getWidth() - gameOverText.getLayoutBounds().getWidth()) / 2,
                root.widthProperty(),
                gameOverText.layoutBoundsProperty()
        ));
        gameOverText.layoutYProperty().bind(root.heightProperty().multiply(0.46));
        gameOverText.setMouseTransparent(true);
        gameOverText.setVisible(false);

        Text finalScoreText = new Text("POINT: 0");
        finalScoreText.setFill(Color.WHITE);
        finalScoreText.setFont(loadFont(MENU_FONT_PATH, 24, Font.font("Arial", FontWeight.BOLD, 24)));

        Text nameLabel = new Text("NAMA PLAYER");
        nameLabel.setFill(Color.WHITE);
        nameLabel.setFont(loadFont(MENU_FONT_PATH, 16, Font.font("Arial", FontWeight.BOLD, 16)));

        TextField nameInput = new TextField();
        nameInput.setPromptText("Isi nama");
        nameInput.setMaxWidth(260);
        nameInput.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        Button saveButton = new Button("SIMPAN");
        Button cancelButton = new Button("BATAL");
        saveButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        cancelButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        saveButton.setCursor(Cursor.HAND);
        cancelButton.setCursor(Cursor.HAND);

        HBox scoreButtonBox = new HBox(12, saveButton, cancelButton);
        scoreButtonBox.setAlignment(Pos.CENTER);

        Text saveStatusText = new Text("");
        saveStatusText.setFill(Color.rgb(255, 230, 120));
        saveStatusText.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        VBox saveScoreBox = new VBox(12, finalScoreText, nameLabel, nameInput, scoreButtonBox, saveStatusText);
        saveScoreBox.setAlignment(Pos.CENTER);
        saveScoreBox.setPadding(new Insets(18));
        saveScoreBox.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0.72), new CornerRadii(8), Insets.EMPTY)));
        saveScoreBox.setMaxWidth(340);
        saveScoreBox.layoutXProperty().bind(Bindings.createDoubleBinding(
                () -> (root.getWidth() - saveScoreBox.getBoundsInLocal().getWidth()) / 2,
                root.widthProperty(),
                saveScoreBox.boundsInLocalProperty()
        ));
        saveScoreBox.layoutYProperty().bind(root.heightProperty().multiply(0.54));
        saveScoreBox.setVisible(false);

        playLayer.getChildren().addAll(
                pointBoxOverlay,
                keeperBoxOverlay,
                pullLine,
                targetMarker,
                keeper,
                ball,
                goalText,
                topHudBackground,
                scoreText,
                livesText,
                livesIndicatorBox,
                hintText,
                shortcutText,
                scoreboardButton,
                gameOverOverlay,
                gameOverText,
                saveScoreBox,
                scoreboardPanel
        );
        root.getChildren().addAll(background, playLayer);

        Scene scene = new Scene(root, 1280, 720);
        setSceneSmooth(stage, scene, this::startGameplayDefaultAudio);
        // 3. INPUT KEYBOARD

        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                showMenu(stage);
            }
        });

        // 2. MENYIAPKAN DATA / STATE PERMAINAN

        EndlessState state = new EndlessState();
        state.lives = MAX_PLAYER_LIVES;
        Runnable resetRound = () -> resetEndlessRound(root, ball, keeper, pullLine, targetMarker, state, keeperAnimator);
        Runnable restartGame = () -> {
            state.score = 0;
            state.lives = MAX_PLAYER_LIVES;
            state.scoredShotsLearned = 0;
            scoreText.setText("POINT: 0");
            livesText.setText("DARAH: " + state.lives);
            updateLifeIndicators(lifeIndicators, state.lives);
            gameOverOverlay.setVisible(false);
            gameOverText.setVisible(false);
            saveScoreBox.setVisible(false);
            scoreboardPanel.setVisible(false);
            scoreboardButton.setText("SCOREBOARD");
            nameInput.clear();
            saveStatusText.setText("");
            state.gameOver = false;
            ball.setCursor(Cursor.HAND);
            resetRound.run();
        };

        root.widthProperty().addListener((observable, oldValue, newValue) -> resetRound.run());
        root.heightProperty().addListener((observable, oldValue, newValue) -> resetRound.run());
        Platform.runLater(resetRound);

        saveButton.setOnAction(event -> {
            String playerName = nameInput.getText().trim();
            if (playerName.isEmpty()) {
                saveStatusText.setText("Nama harus diisi");
                return;
            }

            try {
                saveTopScore(playerName, state.score);
                restartGame.run();
            } catch (IOException exception) {
                saveStatusText.setText("Gagal menyimpan score");
                exception.printStackTrace();
            }
        });

        cancelButton.setOnAction(event -> restartGame.run());

        // 4. INPUT MOUSE UNTUK TENDANGAN

        ball.setOnMousePressed(event -> {
            if (state.gameOver || state.ballMoving || scoreboardPanel.isVisible()) {
                return;
            }
            state.dragging = true;
            state.anchorX = getCenterX(ball);
            state.anchorY = getCenterY(ball);
            pullLine.setStartX(state.anchorX);
            pullLine.setStartY(state.anchorY);
            pullLine.setEndX(state.anchorX);
            pullLine.setEndY(state.anchorY);
            pullLine.setVisible(true);
            targetMarker.setVisible(false);
        });

        ball.setOnMouseDragged(event -> {
            if (state.gameOver || !state.dragging || state.ballMoving) {
                return;
            }

            double mouseX = event.getSceneX();
            double mouseY = event.getSceneY();
            double dx = mouseX - state.anchorX;
            double dy = mouseY - state.anchorY;
            double distance = Math.hypot(dx, dy);
            if (distance > MAX_PULL_DISTANCE) {
                dx = dx / distance * MAX_PULL_DISTANCE;
                dy = dy / distance * MAX_PULL_DISTANCE;
            }

            setCenter(ball, state.anchorX + dx, state.anchorY + dy);
            pullLine.setEndX(getCenterX(ball));
            pullLine.setEndY(getCenterY(ball));
            updateTargetMarker(root, ball, targetMarker, state);
        });

        ball.setOnMouseReleased(event -> {
            if (state.gameOver || !state.dragging || state.ballMoving) {
                return;
            }

            state.dragging = false;
            pullLine.setVisible(false);
            targetMarker.setVisible(false);
            applyKickForce(root, ball, state);
            if (state.shotSpeed < MIN_BALL_SPEED + 20) {
                playRoundResultAudio(ROUND_RESULT_MISS);
                damagePlayer(
                        ball,
                        state,
                        livesText,
                        lifeIndicators,
                        gameOverOverlay,
                        gameOverText,
                        saveScoreBox,
                        finalScoreText,
                        nameInput,
                        resetRound
                );
                return;
            }

            chooseKeeperTarget(root, state);
            keeperAnimator.startDive(state.keeperDiveDirection, state.keeperWillCatch, state.keeperVerticalJump);
            state.ballMoving = true;
            startKeeperJumpMovement(root, keeper, state, KEEPER_MOVE_SPEED);
        });

        // 5. GAME LOOP: DIJALANKAN TERUS SELAMA MODE AKTIF

        AnimationTimer gameLoop = new AnimationTimer() {
            private long lastFrameTime = 0;

            @Override
            public void handle(long now) {
                if (lastFrameTime == 0) {
                    lastFrameTime = now;
                    return;
                }

                double deltaSeconds = (now - lastFrameTime) / 1_000_000_000.0;
                lastFrameTime = now;
                updateEndless(
                        root,
                        ball,
                        keeper,
                        keeperAnimator,
                        keeperBoxOverlay,
                        goalText,
                        scoreText,
                        livesText,
                        lifeIndicators,
                        gameOverOverlay,
                        gameOverText,
                        saveScoreBox,
                        finalScoreText,
                        nameInput,
                        state,
                        resetRound,
                        deltaSeconds
                );
            }
        };
        gameLoop.start();
    }
}
