package modes.endless;

import core.GameEngine;
import core.GameDataStructures.PlayerRecord;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
import java.util.HashMap;
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
        // Jangan tampilkan actor sebelum ukuran layar final tersedia.
        // Ini mencegah keeper berkedip dari koordinat default (0,0) ke tengah.
        keeper.setVisible(false);

        ImageView ball = createImageView(BALL_IMAGE_PATH);
        ball.setFitWidth(BALL_SIZE);
        ball.setFitHeight(BALL_SIZE);
        ball.setPreserveRatio(true);
        ball.setCursor(Cursor.HAND);
        // Bola juga disembunyikan sampai posisi awal sudah dihitung dari ukuran layar aktual.
        ball.setVisible(false);

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

        Text livesText = new Text("LIVES: " + MAX_PLAYER_LIVES);
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

        Text hintText = new Text("Drag the ball, then release");
        hintText.setFill(Color.rgb(255, 255, 255, 0.82));
        hintText.setFont(loadFont(MENU_FONT_PATH, 18, Font.font("Arial", FontWeight.BOLD, 18)));
        hintText.setLayoutX(32);
        hintText.setLayoutY(82);

        hintText.setVisible(false);

        Button menuButton = createGameplayMenuButton(stage);
        StackPane.setAlignment(menuButton, Pos.TOP_RIGHT);
        StackPane.setMargin(menuButton, new Insets(18, 28, 0, 0));

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
        scoreboardButton.setLayoutY(66);

        Text scoreboardTitle = new Text("ENDLESS SCOREBOARD");
        scoreboardTitle.setFill(Color.rgb(255, 235, 120));
        scoreboardTitle.setFont(loadFont(MENU_FONT_PATH, 28, Font.font("Arial", FontWeight.EXTRA_BOLD, 28)));

        String scoreboardHeaderStyle =
                "-fx-background-color: #0b3f8f;" +
                "-fx-border-color: white;" +
                "-fx-border-width: 2;" +
                "-fx-padding: 8 12 8 12;";

        Label rankHeader = new Label("RANK");
        Label playerHeader = new Label("PLAYER");
        Label pointsHeader = new Label("POINTS");
        Label[] scoreboardHeaders = {rankHeader, playerHeader, pointsHeader};
        for (Label header : scoreboardHeaders) {
            header.setTextFill(Color.WHITE);
            header.setFont(loadFont(MENU_FONT_PATH, 14, Font.font("Arial", FontWeight.EXTRA_BOLD, 14)));
            header.setMinHeight(28);
        }
        rankHeader.setPrefWidth(90);
        rankHeader.setAlignment(Pos.CENTER_LEFT);
        playerHeader.setPrefWidth(300);
        playerHeader.setAlignment(Pos.CENTER_LEFT);
        pointsHeader.setPrefWidth(130);
        pointsHeader.setAlignment(Pos.CENTER_RIGHT);

        HBox scoreboardHeader = new HBox(rankHeader, playerHeader, pointsHeader);
        scoreboardHeader.setAlignment(Pos.CENTER_LEFT);
        scoreboardHeader.setPrefWidth(544);
        scoreboardHeader.setMaxWidth(544);
        scoreboardHeader.setStyle(scoreboardHeaderStyle);

        VBox scoreboardRows = new VBox(5);
        scoreboardRows.setAlignment(Pos.TOP_CENTER);
        scoreboardRows.setMinHeight(340);
        scoreboardRows.setPrefHeight(340);
        scoreboardRows.setPrefWidth(544);
        scoreboardRows.setMaxWidth(544);

        Button previousScorePageButton = new Button("< PREV");
        Button nextScorePageButton = new Button("NEXT >");
        Text scoreboardPageText = new Text();
        scoreboardPageText.setFill(Color.rgb(255, 235, 120));
        scoreboardPageText.setFont(loadFont(MENU_FONT_PATH, 16, Font.font("Arial", FontWeight.EXTRA_BOLD, 16)));

        String scoreboardPageButtonStyle =
                "-fx-background-color: #0b63ce;" +
                "-fx-text-fill: white;" +
                "-fx-border-color: white;" +
                "-fx-border-width: 2;" +
                "-fx-background-radius: 4;" +
                "-fx-border-radius: 4;" +
                "-fx-padding: 7 14 7 14;";
        previousScorePageButton.setStyle(scoreboardPageButtonStyle);
        nextScorePageButton.setStyle(scoreboardPageButtonStyle);
        previousScorePageButton.setCursor(Cursor.HAND);
        nextScorePageButton.setCursor(Cursor.HAND);
        previousScorePageButton.setFocusTraversable(false);
        nextScorePageButton.setFocusTraversable(false);
        previousScorePageButton.setFont(loadFont(MENU_FONT_PATH, 13, Font.font("Arial", FontWeight.BOLD, 13)));
        nextScorePageButton.setFont(loadFont(MENU_FONT_PATH, 13, Font.font("Arial", FontWeight.BOLD, 13)));

        StackPane previousButtonSlot = new StackPane(previousScorePageButton);
        previousButtonSlot.setPrefWidth(125);
        previousButtonSlot.setMinWidth(125);
        previousButtonSlot.setAlignment(Pos.CENTER_LEFT);

        StackPane pageNumberSlot = new StackPane(scoreboardPageText);
        pageNumberSlot.setPrefWidth(100);
        pageNumberSlot.setMinWidth(100);
        pageNumberSlot.setAlignment(Pos.CENTER);

        StackPane nextButtonSlot = new StackPane(nextScorePageButton);
        nextButtonSlot.setPrefWidth(125);
        nextButtonSlot.setMinWidth(125);
        nextButtonSlot.setAlignment(Pos.CENTER_RIGHT);

        HBox scoreboardPagination = new HBox(8, previousButtonSlot, pageNumberSlot, nextButtonSlot);
        scoreboardPagination.setAlignment(Pos.CENTER);
        scoreboardPagination.setPrefWidth(544);
        scoreboardPagination.setMaxWidth(544);

        final int scoreboardPageSize = 10;
        final int[] scoreboardPage = {0};
        final String[] highlightedScoreId = {null};
        Runnable refreshScoreboard = () -> {
            List<PlayerRecord> entries = loadPlayerRecords();
            int totalPages = Math.max(1, (entries.size() + scoreboardPageSize - 1) / scoreboardPageSize);
            if (scoreboardPage[0] >= totalPages) {
                scoreboardPage[0] = totalPages - 1;
            }
            if (scoreboardPage[0] < 0) {
                scoreboardPage[0] = 0;
            }

            scoreboardRows.getChildren().clear();
            if (entries.isEmpty()) {
                Label emptyLabel = new Label("NO SCORES SAVED YET");
                emptyLabel.setTextFill(Color.WHITE);
                emptyLabel.setFont(loadFont(MENU_FONT_PATH, 17, Font.font("Arial", FontWeight.BOLD, 17)));
                emptyLabel.setPrefWidth(544);
                emptyLabel.setPrefHeight(52);
                emptyLabel.setAlignment(Pos.CENTER);
                emptyLabel.setStyle(
                        "-fx-background-color: #101010;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 0 1 1 1;"
                );
                scoreboardRows.getChildren().add(emptyLabel);
            } else {
                int startIndex = scoreboardPage[0] * scoreboardPageSize;
                int endIndex = Math.min(startIndex + scoreboardPageSize, entries.size());
                for (int i = startIndex; i < endIndex; i++) {
                    PlayerRecord entry = entries.get(i);
                    String safeName = entry.name().length() > 16
                            ? entry.name().substring(0, 16)
                            : entry.name();
                    boolean highlighted = highlightedScoreId[0] != null
                            && highlightedScoreId[0].equals(entry.id());

                    Label rankLabel = new Label(String.format("%02d", i + 1));
                    Label playerLabel = new Label(safeName.toUpperCase());
                    Label pointsLabel = new Label(String.valueOf(entry.score()));
                    Label[] rowLabels = {rankLabel, playerLabel, pointsLabel};
                    for (Label label : rowLabels) {
                        label.setTextFill(highlighted ? Color.BLACK : Color.WHITE);
                        label.setFont(loadFont(
                                MENU_FONT_PATH,
                                16,
                                Font.font("Arial", highlighted ? FontWeight.EXTRA_BOLD : FontWeight.BOLD, 16)
                        ));
                        label.setMinHeight(24);
                    }

                    rankLabel.setPrefWidth(90);
                    rankLabel.setAlignment(Pos.CENTER_LEFT);
                    playerLabel.setPrefWidth(300);
                    playerLabel.setAlignment(Pos.CENTER_LEFT);
                    pointsLabel.setPrefWidth(130);
                    pointsLabel.setAlignment(Pos.CENTER_RIGHT);

                    HBox scoreRow = new HBox(rankLabel, playerLabel, pointsLabel);
                    scoreRow.setAlignment(Pos.CENTER_LEFT);
                    scoreRow.setPrefWidth(544);
                    scoreRow.setMaxWidth(544);
                    scoreRow.setMinHeight(29);
                    scoreRow.setStyle(highlighted
                            ? "-fx-background-color: #ffe128;" +
                              "-fx-border-color: white;" +
                              "-fx-border-width: 1;" +
                              "-fx-padding: 5 12 5 12;"
                            : "-fx-background-color: #101010;" +
                              "-fx-border-color: #666666;" +
                              "-fx-border-width: 0 1 1 1;" +
                              "-fx-padding: 5 12 5 12;"
                    );
                    scoreboardRows.getChildren().add(scoreRow);
                }
            }

            // Hanya tampilkan angka halaman, tanpa tulisan PAGE atau TOTAL.
            scoreboardPageText.setText((scoreboardPage[0] + 1) + " / " + totalPages);

            // Slot tombol tetap memiliki lebar yang sama agar nomor halaman selalu di tengah.
            boolean hasPreviousPage = scoreboardPage[0] > 0;
            boolean hasNextPage = scoreboardPage[0] < totalPages - 1;
            previousScorePageButton.setVisible(hasPreviousPage);
            previousScorePageButton.setMouseTransparent(!hasPreviousPage);
            nextScorePageButton.setVisible(hasNextPage);
            nextScorePageButton.setMouseTransparent(!hasNextPage);
        };

        previousScorePageButton.setOnAction(event -> {
            if (scoreboardPage[0] > 0) {
                scoreboardPage[0]--;
                refreshScoreboard.run();
            }
        });
        nextScorePageButton.setOnAction(event -> {
            int totalPages = Math.max(1,
                    (loadPlayerRecords().size() + scoreboardPageSize - 1) / scoreboardPageSize);
            if (scoreboardPage[0] < totalPages - 1) {
                scoreboardPage[0]++;
                refreshScoreboard.run();
            }
        });

        VBox scoreboardPanel = new VBox(12,
                scoreboardTitle,
                scoreboardHeader,
                scoreboardRows,
                scoreboardPagination
        );
        scoreboardPanel.setAlignment(Pos.TOP_CENTER);
        scoreboardPanel.setPadding(new Insets(20, 28, 20, 28));
        // Panel dibuat hitam pekat, bukan transparan, supaya gawang dan penonton
        // tidak terlihat menembus area scoreboard.
        scoreboardPanel.setBackground(new Background(new BackgroundFill(
                Color.BLACK, new CornerRadii(10), Insets.EMPTY
        )));
        scoreboardPanel.setStyle(
                "-fx-background-color: #000000;" +
                "-fx-border-color: white;" +
                "-fx-border-width: 3;" +
                "-fx-border-radius: 10;" +
                "-fx-background-radius: 10;"
        );
        scoreboardPanel.setOpacity(1.0);
        scoreboardPanel.setPrefWidth(600);
        scoreboardPanel.setMaxWidth(600);
        scoreboardPanel.setMinWidth(600);
        scoreboardPanel.setVisible(false);
        scoreboardPanel.layoutXProperty().bind(Bindings.createDoubleBinding(
                () -> (root.getWidth() - scoreboardPanel.getBoundsInLocal().getWidth()) / 2,
                root.widthProperty(),
                scoreboardPanel.boundsInLocalProperty()
        ));
        scoreboardPanel.layoutYProperty().bind(Bindings.createDoubleBinding(
                () -> Math.max(108, (root.getHeight() - scoreboardPanel.getBoundsInLocal().getHeight()) / 2),
                root.heightProperty(),
                scoreboardPanel.boundsInLocalProperty()
        ));

        scoreboardButton.setOnAction(event -> {
            boolean show = !scoreboardPanel.isVisible();
            if (show) {
                scoreboardPage[0] = 0;
                if (highlightedScoreId[0] != null) {
                    List<PlayerRecord> entries = loadPlayerRecords();
                    HashMap<String, PlayerRecord> playerById = indexPlayersById(entries);
                    HashMap<String, Integer> rankById = indexPlayerRanksById(entries);
                    if (playerById.containsKey(highlightedScoreId[0])) {
                        Integer rankIndex = rankById.get(highlightedScoreId[0]);
                        if (rankIndex != null) {
                            scoreboardPage[0] = rankIndex / scoreboardPageSize;
                        }
                    }
                }
                refreshScoreboard.run();
            }
            scoreboardPanel.setVisible(show);
            scoreboardButton.setText(show ? "CLOSE SCOREBOARD" : "SCOREBOARD");
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

        Text nameLabel = new Text("PLAYER NAME");
        nameLabel.setFill(Color.WHITE);
        nameLabel.setFont(loadFont(MENU_FONT_PATH, 16, Font.font("Arial", FontWeight.BOLD, 16)));

        TextField nameInput = new TextField();
        nameInput.setPromptText("Enter name (minimum 3 letters)");
        nameInput.setMaxWidth(260);
        nameInput.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        // Validasi nama scoreboard Endless: minimal harus memiliki 3 huruf.
        final String nameInputDefaultStyle = nameInput.getStyle();
        final String nameInputErrorStyle = nameInputDefaultStyle
                + ";-fx-border-color: #ff3b30;"
                + "-fx-border-width: 3;"
                + "-fx-border-radius: 4;"
                + "-fx-background-radius: 4;";
        final boolean[] nameValidationActive = {false};
        java.util.function.Predicate<String> hasMinimumThreeLetters = value -> value != null
                && value.codePoints().filter(codePoint -> Character.isLetter(codePoint)).limit(3).count() >= 3;

        Button saveButton = new Button("SAVE");
        Button cancelButton = new Button("CANCEL");
        saveButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        cancelButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        saveButton.setCursor(Cursor.HAND);
        cancelButton.setCursor(Cursor.HAND);

        HBox scoreButtonBox = new HBox(12, saveButton, cancelButton);
        scoreButtonBox.setAlignment(Pos.CENTER);

        Text saveStatusText = new Text("");
        saveStatusText.setFill(Color.rgb(255, 230, 120));
        saveStatusText.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Runnable showNameValidationError = () -> {
            nameInput.setStyle(nameInputErrorStyle);
            saveStatusText.setFill(Color.rgb(255, 75, 75));
            saveStatusText.setText("NAME MUST HAVE AT LEAST 3 LETTERS");
        };
        Runnable clearNameValidationError = () -> {
            nameInput.setStyle(nameInputDefaultStyle);
            saveStatusText.setFill(Color.rgb(255, 230, 120));
            saveStatusText.setText("");
        };
        nameInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!nameValidationActive[0]) {
                return;
            }
            if (hasMinimumThreeLetters.test(newValue)) {
                clearNameValidationError.run();
            } else {
                showNameValidationError.run();
            }
        });

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
                keeper,
                ball,
                goalText,
                topHudBackground,
                scoreText,
                livesText,
                livesIndicatorBox,
                hintText,
                scoreboardButton,
                gameOverOverlay,
                gameOverText,
                saveScoreBox,
                scoreboardPanel
        );
        root.getChildren().addAll(background, playLayer, menuButton);

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
        Runnable resetRound = () -> {
            resetEndlessRound(root, ball, keeper, pullLine, targetMarker, state, keeperAnimator);
            if (root.getWidth() > 0 && root.getHeight() > 0) {
                keeper.setVisible(true);
                ball.setVisible(true);
            }
        };
        Runnable restartGame = () -> {
            state.score = 0;
            state.lives = MAX_PLAYER_LIVES;
            state.scoredShotsLearned = 0;
            scoreText.setText("POINT: 0");
            livesText.setText("LIVES: " + state.lives);
            updateLifeIndicators(lifeIndicators, state.lives);
            gameOverOverlay.setVisible(false);
            gameOverText.setVisible(false);
            saveScoreBox.setVisible(false);
            scoreboardPanel.setVisible(false);
            scoreboardButton.setText("SCOREBOARD");
            nameValidationActive[0] = false;
            clearNameValidationError.run();
            nameInput.clear();
            state.gameOver = false;
            ball.setCursor(Cursor.HAND);
            resetRound.run();
        };

        root.widthProperty().addListener((observable, oldValue, newValue) -> resetRound.run());
        root.heightProperty().addListener((observable, oldValue, newValue) -> resetRound.run());
        Platform.runLater(resetRound);

        saveButton.setOnAction(event -> {
            String playerName = nameInput.getText().trim();
            if (!hasMinimumThreeLetters.test(playerName)) {
                nameValidationActive[0] = true;
                showNameValidationError.run();
                nameInput.requestFocus();
                return;
            }

            nameValidationActive[0] = false;
            clearNameValidationError.run();
            try {
                highlightedScoreId[0] = saveTopScore(playerName, state.score);
                List<PlayerRecord> entries = loadPlayerRecords();
                HashMap<String, PlayerRecord> playerById = indexPlayersById(entries);
                HashMap<String, Integer> rankById = indexPlayerRanksById(entries);
                scoreboardPage[0] = 0;
                if (playerById.containsKey(highlightedScoreId[0])) {
                    Integer rankIndex = rankById.get(highlightedScoreId[0]);
                    if (rankIndex != null) {
                        scoreboardPage[0] = rankIndex / scoreboardPageSize;
                    }
                }

                restartGame.run();
                refreshScoreboard.run();
                scoreboardPanel.setVisible(true);
                scoreboardButton.setText("CLOSE SCOREBOARD");
            } catch (IOException exception) {
                saveStatusText.setText("Failed to save score");
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
            // Aim marker sengaja disembunyikan di semua mode selain Tutorial.
            targetMarker.setVisible(false);
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
