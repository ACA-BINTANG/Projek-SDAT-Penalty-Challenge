import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class Main extends Application {
    private static final String INTRO_VIDEO_PATH = "resources/video/Intro-Awal.mp4";
    private static final String MENU_BACKGROUND_PATH = "resources/images/Tampilan-BG-Menu.png";
    private static final String MENU_BOX_OFF_PATH = "resources/images/Box-Menu-off.png";
    private static final String MENU_BOX_ON_PATH = "resources/images/Box-Menu-on.png";
    private static final String GOAL_BACKGROUND_PATH = "resources/images/GAWANG.png";
    private static final String BALL_IMAGE_PATH = "resources/images/BOLA.png";
    private static final String KEEPER_IMAGE_PATH = "resources/images/KEEPER-01.png";
    private static final String TOP_SCORE_PATH = "top_scores.txt";
    private static final String START_FONT_PATH = "resources/fonts/MinecraftBoldItalic-1y1e.otf";
    private static final String MENU_FONT_PATH = "resources/fonts/MinecraftRegular-Bmg3.otf";
    private static final double MENU_OPTION_WIDTH = 286.5;
    private static final double MENU_OPTION_HEIGHT = 59.5;
    private static final double BALL_SIZE = 92;
    private static final double KEEPER_SIZE = 145;
    private static final double MAX_PULL_DISTANCE = 160;
    private static final double MIN_SHOT_DISTANCE = 150;
    private static final double MAX_SHOT_DISTANCE = 840;
    private static final double MIN_BALL_SPEED = 520;
    private static final double MAX_BALL_SPEED = 1220;
    private static final double UPWARD_SHOT_BONUS = 1.08;
    private static final double KEEPER_MAX_READ_CHANCE = 0.82;
    private static final double KEEPER_READ_GROWTH_PER_POINT = 0.08;
    private static final double SHOT_MEMORY_WEIGHT = 0.35;
    private static final double GOAL_LEFT_RATIO = 0.24;
    private static final double GOAL_RIGHT_RATIO = 0.76;
    private static final double GOAL_TOP_RATIO = 0.28;
    private static final double GOAL_BOTTOM_RATIO = 0.65;
    private static final double GOAL_SCORE_LINE_RATIO = 0.30;
    private static final int MAX_PLAYER_LIVES = 3;
    private MediaPlayer introPlayer;
    private final Random random = new Random();

    @Override
    public void start(Stage stage) {
        StackPane root = new StackPane();
        root.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));

        Path videoPath = Path.of(INTRO_VIDEO_PATH).toAbsolutePath().normalize();
        if (!Files.exists(videoPath)) {
            showError(root, "Video tidak ditemukan:\n" + videoPath);
        } else {
            try {
                Media media = new Media(videoPath.toUri().toString());
                MediaPlayer mediaPlayer = new MediaPlayer(media);
                mediaPlayer.setMute(true);
                mediaPlayer.setCycleCount(1);
                mediaPlayer.setOnError(() -> showError(root, "Gagal memutar video:\n" + mediaPlayer.getError()));
                media.setOnError(() -> showError(root, "Format video tidak bisa dibaca:\n" + media.getError()));
                mediaPlayer.setOnReady(() -> Platform.runLater(() -> {
                    mediaPlayer.seek(javafx.util.Duration.ZERO);
                    mediaPlayer.play();
                }));
                mediaPlayer.statusProperty().addListener((observable, oldStatus, newStatus) ->
                        System.out.println("Intro video status: " + newStatus));
                introPlayer = mediaPlayer;

                MediaView mediaView = new MediaView(mediaPlayer);
                mediaView.setPreserveRatio(false);
                mediaView.fitWidthProperty().bind(root.widthProperty());
                mediaView.fitHeightProperty().bind(root.heightProperty());
                root.getChildren().add(mediaView);
            } catch (Exception exception) {
                exception.printStackTrace();
                showError(root, "Gagal load video:\n" + exception.getMessage());
            }
        }

        Rectangle overlay = new Rectangle();
        overlay.widthProperty().bind(root.widthProperty());
        overlay.heightProperty().bind(root.heightProperty());
        overlay.setFill(Color.rgb(0, 0, 0, 0.28));
        overlay.setMouseTransparent(true);

        StackPane startButton = createStartButton();
        startButton.setOnMouseClicked(event -> {
            if (introPlayer != null) {
                introPlayer.stop();
            }
            showMenu(stage);
        });

        StackPane.setAlignment(startButton, Pos.BOTTOM_CENTER);
        StackPane.setMargin(startButton, new javafx.geometry.Insets(0, 0, 72, 0));

        root.getChildren().addAll(overlay, startButton);

        Scene scene = new Scene(root, 1280, 720);
        stage.setTitle("Penalty Challenge");
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private StackPane createStartButton() {
        Rectangle background = new Rectangle(190, 58);
        background.setArcWidth(58);
        background.setArcHeight(58);
        background.setFill(Color.rgb(12, 12, 12, 0.55));
        background.setStroke(Color.rgb(255, 255, 255, 0.9));
        background.setStrokeWidth(2);

        Text label = new Text("START");
        label.setFill(Color.WHITE);
        label.setFont(loadFont(START_FONT_PATH, 24, Font.font("Arial", FontWeight.EXTRA_BOLD, 24)));
        label.setMouseTransparent(true);

        StackPane button = new StackPane(background, label);
        button.setMinSize(190, 58);
        button.setMaxSize(190, 58);
        button.setCursor(Cursor.HAND);

        button.setOnMouseEntered(event -> {
            button.setScaleX(1.04);
            button.setScaleY(1.04);
            background.setFill(Color.rgb(255, 255, 255, 0.18));
        });
        button.setOnMouseExited(event -> {
            button.setScaleX(1.0);
            button.setScaleY(1.0);
            background.setFill(Color.rgb(12, 12, 12, 0.55));
        });
        button.setOnMousePressed(event -> {
            button.setScaleX(0.98);
            button.setScaleY(0.98);
        });
        button.setOnMouseReleased(event -> {
            button.setScaleX(1.04);
            button.setScaleY(1.04);
        });

        return button;
    }

    private Font loadFont(String fontPath, double size, Font fallbackFont) {
        Path path = Path.of(fontPath).toAbsolutePath().normalize();
        if (!Files.exists(path)) {
            return fallbackFont;
        }

        Font font = Font.loadFont(path.toUri().toString(), size);
        return font != null ? font : fallbackFont;
    }

    private void showMenu(Stage stage) {
        StackPane root = new StackPane();

        ImageView background = createImageView(MENU_BACKGROUND_PATH);
        background.setPreserveRatio(false);
        background.fitWidthProperty().bind(root.widthProperty());
        background.fitHeightProperty().bind(root.heightProperty());

        Rectangle overlay = new Rectangle();
        overlay.widthProperty().bind(root.widthProperty());
        overlay.heightProperty().bind(root.heightProperty());
        overlay.setFill(Color.rgb(0, 0, 0, 0.12));
        overlay.setMouseTransparent(true);

        VBox menuBox = new VBox(9);
        menuBox.setAlignment(Pos.CENTER);
        menuBox.setMaxWidth(MENU_OPTION_WIDTH);

        String[] modes = {"ENDLESS", "CO-OP", "TOURNAMENT", "TUTORIAL"};

        for (int i = 0; i < modes.length; i++) {
            StackPane option = createMenuOption(modes[i]);
            if ("ENDLESS".equals(modes[i])) {
                option.setOnMouseClicked(event -> showEndlessMode(stage));
            }
            menuBox.getChildren().add(option);
        }

        root.getChildren().addAll(background, overlay, menuBox);

        Scene scene = new Scene(root, 1280, 720);
        stage.setScene(scene);
        stage.setFullScreen(true);
    }

    private StackPane createMenuOption(String modeName) {
        ImageView offFrame = createImageView(MENU_BOX_OFF_PATH);
        ImageView onFrame = createImageView(MENU_BOX_ON_PATH);
        offFrame.setFitWidth(MENU_OPTION_WIDTH);
        onFrame.setFitWidth(MENU_OPTION_WIDTH);
        offFrame.setPreserveRatio(true);
        onFrame.setPreserveRatio(true);

        Text label = new Text(modeName);
        label.setFill(Color.WHITE);
        label.setFont(loadFont(MENU_FONT_PATH, 16, Font.font("Arial", FontWeight.BOLD, 16)));
        label.setMouseTransparent(true);

        StackPane option = new StackPane(offFrame, onFrame, label);
        option.setMinSize(MENU_OPTION_WIDTH, MENU_OPTION_HEIGHT);
        option.setMaxSize(MENU_OPTION_WIDTH, MENU_OPTION_HEIGHT);
        option.setCursor(Cursor.HAND);
        onFrame.setVisible(false);

        option.setOnMouseEntered(event -> {
            onFrame.setVisible(true);
            offFrame.setVisible(false);
        });
        option.setOnMouseExited(event -> {
            onFrame.setVisible(false);
            offFrame.setVisible(true);
        });

        return option;
    }

    private ImageView createImageView(String imagePath) {
        Path path = Path.of(imagePath).toAbsolutePath().normalize();
        Image image = new Image(path.toUri().toString());
        return new ImageView(image);
    }

    private void showEndlessMode(Stage stage) {
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
        pointBoxOverlay.layoutXProperty().bind(root.widthProperty().multiply(GOAL_LEFT_RATIO));
        pointBoxOverlay.layoutYProperty().bind(root.heightProperty().multiply(GOAL_TOP_RATIO));
        pointBoxOverlay.widthProperty().bind(root.widthProperty().multiply(GOAL_RIGHT_RATIO - GOAL_LEFT_RATIO));
        pointBoxOverlay.heightProperty().bind(root.heightProperty().multiply(GOAL_BOTTOM_RATIO - GOAL_TOP_RATIO));

        Rectangle keeperBoxOverlay = new Rectangle();
        keeperBoxOverlay.setFill(Color.rgb(0, 255, 80, 0.18));
        keeperBoxOverlay.setStroke(Color.rgb(0, 255, 80, 0.82));
        keeperBoxOverlay.setStrokeWidth(3);
        keeperBoxOverlay.setMouseTransparent(true);
        keeperBoxOverlay.layoutXProperty().bind(keeper.layoutXProperty());
        keeperBoxOverlay.layoutYProperty().bind(keeper.layoutYProperty());
        keeperBoxOverlay.widthProperty().bind(keeper.fitWidthProperty());
        keeperBoxOverlay.heightProperty().bind(keeper.fitHeightProperty());

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
                scoreText,
                livesText,
                livesIndicatorBox,
                hintText,
                gameOverOverlay,
                gameOverText,
                saveScoreBox
        );
        root.getChildren().addAll(background, playLayer);

        Scene scene = new Scene(root, 1280, 720);
        stage.setScene(scene);
        stage.setFullScreen(true);

        EndlessState state = new EndlessState();
        state.lives = MAX_PLAYER_LIVES;
        Runnable resetRound = () -> resetEndlessRound(root, ball, keeper, pullLine, targetMarker, state);
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

        ball.setOnMousePressed(event -> {
            if (state.gameOver || state.ballMoving) {
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

            state.ballMoving = true;
            state.keeperMoving = true;
            chooseKeeperTarget(root, state);
        });

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

    private void updateEndless(
            StackPane root,
            ImageView ball,
            ImageView keeper,
            Text scoreText,
            Text livesText,
            Rectangle[] lifeIndicators,
            Rectangle gameOverOverlay,
            Text gameOverText,
            VBox saveScoreBox,
            Text finalScoreText,
            TextField nameInput,
            EndlessState state,
            Runnable resetRound,
            double deltaSeconds
    ) {
        if (state.gameOver) {
            return;
        }

        if (state.keeperMoving) {
            double keeperCenterX = getCenterX(keeper);
            double keeperCenterY = getCenterY(keeper);
            double dx = state.keeperTargetX - keeperCenterX;
            double dy = state.keeperTargetY - keeperCenterY;
            double distance = Math.hypot(dx, dy);
            double step = 520 * deltaSeconds;
            if (distance <= step) {
                setCenter(keeper, state.keeperTargetX, state.keeperTargetY);
                state.keeperMoving = false;
            } else {
                setCenter(keeper, keeperCenterX + dx / distance * step, keeperCenterY + dy / distance * step);
            }
        }

        if (!state.ballMoving) {
            return;
        }

        double ballCenterX = getCenterX(ball);
        double ballCenterY = getCenterY(ball);
        double distanceToTarget = Math.hypot(state.targetX - ballCenterX, state.targetY - ballCenterY);
        double step = state.shotSpeed * deltaSeconds;
        boolean reachedTarget = distanceToTarget <= step;
        if (distanceToTarget <= step) {
            setCenter(ball, state.targetX, state.targetY);
        } else {
            setCenter(
                    ball,
                    ballCenterX + (state.targetX - ballCenterX) / distanceToTarget * step,
                    ballCenterY + (state.targetY - ballCenterY) / distanceToTarget * step
            );
        }

        if (reachedTarget && isShotSavedByKeeper(root, keeper, state)) {
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

        double width = root.getWidth();
        double height = root.getHeight();
        if (reachedTarget && isBallInsidePointBox(root, ball)) {
            rememberScoredShot(state);
            state.score++;
            scoreText.setText("POINT: " + state.score);
            resetRound.run();
            return;
        }

        boolean outsideScreen = getCenterX(ball) < -80
                || getCenterX(ball) > width + 80
                || getCenterY(ball) < -80
                || getCenterY(ball) > height + 80;
        if (outsideScreen || reachedTarget) {
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
        }
    }

    private boolean isBallInsidePointBox(StackPane root, ImageView ball) {
        double ballCenterX = getCenterX(ball);
        double ballCenterY = getCenterY(ball);
        return isPointInsidePointBox(root, ballCenterX, ballCenterY);
    }

    private boolean canBallPassStandingKeeper(StackPane root, EndlessState state) {
        return !state.keeperJumping && isPointInsidePointBox(root, state.targetX, state.targetY);
    }

    private boolean isShotSavedByKeeper(StackPane root, ImageView keeper, EndlessState state) {
        return isPointInsideKeeperBox(keeper, state.targetX, state.targetY)
                && !canBallPassStandingKeeper(root, state);
    }

    private boolean isPointInsideKeeperBox(ImageView keeper, double pointX, double pointY) {
        return pointX >= keeper.getLayoutX()
                && pointX <= keeper.getLayoutX() + keeper.getFitWidth()
                && pointY >= keeper.getLayoutY()
                && pointY <= keeper.getLayoutY() + keeper.getFitHeight();
    }

    private boolean isPointInsidePointBox(StackPane root, double pointX, double pointY) {
        double width = root.getWidth();
        double height = root.getHeight();

        return pointX >= width * GOAL_LEFT_RATIO
                && pointX <= width * GOAL_RIGHT_RATIO
                && pointY >= height * GOAL_TOP_RATIO
                && pointY <= height * GOAL_BOTTOM_RATIO;
    }

    private void resetEndlessRound(
            StackPane root,
            ImageView ball,
            ImageView keeper,
            Line pullLine,
            Circle targetMarker,
            EndlessState state
    ) {
        if (root.getWidth() <= 0 || root.getHeight() <= 0) {
            return;
        }

        state.ballMoving = false;
        state.dragging = false;
        state.keeperMoving = false;
        state.keeperJumping = false;
        state.velocityX = 0;
        state.velocityY = 0;
        state.shotSpeed = 0;
        state.anchorX = root.getWidth() * 0.5;
        state.anchorY = root.getHeight() * 0.82;
        state.targetX = state.anchorX;
        state.targetY = state.anchorY;

        setCenter(ball, state.anchorX, state.anchorY);
        setCenter(keeper, root.getWidth() * 0.5, root.getHeight() * 0.53);
        pullLine.setVisible(false);
        targetMarker.setVisible(false);
    }

    private void damagePlayer(
            ImageView ball,
            EndlessState state,
            Text livesText,
            Rectangle[] lifeIndicators,
            Rectangle gameOverOverlay,
            Text gameOverText,
            VBox saveScoreBox,
            Text finalScoreText,
            TextField nameInput,
            Runnable resetRound
    ) {
        state.lives--;
        livesText.setText("DARAH: " + Math.max(state.lives, 0));
        updateLifeIndicators(lifeIndicators, state.lives);

        if (state.lives <= 0) {
            state.gameOver = true;
            state.ballMoving = false;
            state.dragging = false;
            state.keeperMoving = false;
            ball.setCursor(Cursor.DEFAULT);
            gameOverOverlay.setVisible(true);
            gameOverText.setVisible(true);
            finalScoreText.setText("POINT: " + state.score);
            saveScoreBox.setVisible(true);
            Platform.runLater(nameInput::requestFocus);
            return;
        }

        resetRound.run();
    }

    private void updateLifeIndicators(Rectangle[] lifeIndicators, int lives) {
        for (int i = 0; i < lifeIndicators.length; i++) {
            boolean active = i < lives;
            lifeIndicators[i].setFill(active ? Color.rgb(224, 42, 42) : Color.rgb(60, 60, 60, 0.72));
            lifeIndicators[i].setStroke(active ? Color.WHITE : Color.rgb(180, 180, 180, 0.75));
        }
    }

    private void saveTopScore(String playerName, int score) throws IOException {
        Path scorePath = Path.of(TOP_SCORE_PATH).toAbsolutePath().normalize();
        String cleanName = playerName.replace("\t", " ").replace("\r", " ").replace("\n", " ");
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String line = timestamp + "\t" + cleanName + "\t" + score + System.lineSeparator();
        Files.writeString(
                scorePath,
                line,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
        );
    }

    private void chooseKeeperTarget(StackPane root, EndlessState state) {
        double keeperHalfWidth = KEEPER_SIZE / 2;
        double keeperHalfHeight = KEEPER_SIZE / 2;
        double left = root.getWidth() * GOAL_LEFT_RATIO + keeperHalfWidth;
        double right = root.getWidth() * GOAL_RIGHT_RATIO - keeperHalfWidth;
        double top = root.getHeight() * GOAL_TOP_RATIO + keeperHalfHeight;
        double bottom = root.getHeight() * GOAL_BOTTOM_RATIO - keeperHalfHeight;

        double readChance = Math.min(KEEPER_MAX_READ_CHANCE, state.score * KEEPER_READ_GROWTH_PER_POINT);
        boolean readPlayerHabit = state.scoredShotsLearned > 0 && random.nextDouble() < readChance;
        if (readPlayerHabit) {
            double noise = Math.max(18, root.getWidth() * (0.15 - Math.min(state.score, 10) * 0.009));
            state.keeperTargetX = clamp(state.learnedTargetX + randomBetween(-noise, noise), left, right);
            state.keeperTargetY = clamp(state.learnedTargetY + randomBetween(-noise, noise), top, bottom);
        } else {
            state.keeperTargetX = randomBetween(left, right);
            state.keeperTargetY = randomBetween(top, bottom);
        }

        state.keeperJumping = Math.hypot(
                state.keeperTargetX - root.getWidth() * 0.5,
                state.keeperTargetY - root.getHeight() * 0.53
        ) > 18;
    }

    private void rememberScoredShot(EndlessState state) {
        if (state.scoredShotsLearned == 0) {
            state.learnedTargetX = state.targetX;
            state.learnedTargetY = state.targetY;
        } else {
            state.learnedTargetX = state.learnedTargetX * (1 - SHOT_MEMORY_WEIGHT) + state.targetX * SHOT_MEMORY_WEIGHT;
            state.learnedTargetY = state.learnedTargetY * (1 - SHOT_MEMORY_WEIGHT) + state.targetY * SHOT_MEMORY_WEIGHT;
        }
        state.scoredShotsLearned++;
    }

    private double randomBetween(double min, double max) {
        if (max <= min) {
            return min;
        }
        return min + random.nextDouble() * (max - min);
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private void applyKickForce(StackPane root, ImageView ball, EndlessState state) {
        double pullX = state.anchorX - getCenterX(ball);
        double pullY = state.anchorY - getCenterY(ball);
        double pullDistance = Math.min(Math.hypot(pullX, pullY), MAX_PULL_DISTANCE);
        if (pullDistance == 0) {
            state.velocityX = 0;
            state.velocityY = 0;
            return;
        }

        Shot shot = calculateShot(root, pullX, pullY, pullDistance);
        state.velocityX = shot.directionX * shot.speed;
        state.velocityY = shot.directionY * shot.speed;
        state.shotSpeed = shot.speed;
        state.targetX = state.anchorX + shot.directionX * shot.distance;
        state.targetY = state.anchorY + shot.directionY * shot.distance;
    }

    private void updateTargetMarker(StackPane root, ImageView ball, Circle targetMarker, EndlessState state) {
        double pullX = state.anchorX - getCenterX(ball);
        double pullY = state.anchorY - getCenterY(ball);
        double pullDistance = Math.min(Math.hypot(pullX, pullY), MAX_PULL_DISTANCE);
        if (pullDistance == 0) {
            targetMarker.setVisible(false);
            return;
        }

        Shot shot = calculateShot(root, pullX, pullY, pullDistance);
        double endX = state.anchorX + shot.directionX * shot.distance;
        double endY = state.anchorY + shot.directionY * shot.distance;

        targetMarker.setCenterX(endX);
        targetMarker.setCenterY(endY);
        targetMarker.setVisible(true);
    }

    private Shot calculateShot(StackPane root, double pullX, double pullY, double pullDistance) {
        double power = Math.min(1.0, pullDistance / MAX_PULL_DISTANCE);
        double tunedPower = Math.pow(power, 0.92);
        double directionX = pullX / pullDistance;
        double directionY = pullY / pullDistance;

        if (directionY < 0) {
            directionY *= UPWARD_SHOT_BONUS;
            double normalized = Math.hypot(directionX, directionY);
            directionX /= normalized;
            directionY /= normalized;
        }

        double goalDistance = Math.abs(getGoalLineY(root) - getBallStartY(root));
        double maxDistance = Math.max(MAX_SHOT_DISTANCE, goalDistance * 1.85);
        double distance = MIN_SHOT_DISTANCE + (maxDistance - MIN_SHOT_DISTANCE) * tunedPower;
        double speed = MIN_BALL_SPEED + (MAX_BALL_SPEED - MIN_BALL_SPEED) * tunedPower;

        return new Shot(directionX, directionY, distance, speed);
    }

    private double getGoalLineY(StackPane root) {
        return root.getHeight() * GOAL_SCORE_LINE_RATIO;
    }

    private double getBallStartY(StackPane root) {
        return root.getHeight() * 0.82;
    }

    private static class Shot {
        private final double directionX;
        private final double directionY;
        private final double distance;
        private final double speed;

        private Shot(double directionX, double directionY, double distance, double speed) {
            this.directionX = directionX;
            this.directionY = directionY;
            this.distance = distance;
            this.speed = speed;
        }
    }

    private double getCenterX(ImageView imageView) {
        return imageView.getLayoutX() + imageView.getFitWidth() / 2;
    }

    private double getCenterY(ImageView imageView) {
        return imageView.getLayoutY() + imageView.getFitHeight() / 2;
    }

    private void setCenter(ImageView imageView, double centerX, double centerY) {
        imageView.setLayoutX(centerX - imageView.getFitWidth() / 2);
        imageView.setLayoutY(centerY - imageView.getFitHeight() / 2);
    }

    private static class EndlessState {
        private boolean dragging;
        private boolean ballMoving;
        private boolean keeperMoving;
        private boolean keeperJumping;
        private double anchorX;
        private double anchorY;
        private double velocityX;
        private double velocityY;
        private double targetX;
        private double targetY;
        private double shotSpeed;
        private double keeperTargetX;
        private double keeperTargetY;
        private double learnedTargetX;
        private double learnedTargetY;
        private int scoredShotsLearned;
        private int score;
        private int lives;
        private boolean gameOver;
    }

    private void showError(StackPane root, String message) {
        Label errorLabel = new Label(message);
        errorLabel.setTextFill(Color.WHITE);
        errorLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        errorLabel.setWrapText(true);
        errorLabel.setMaxWidth(760);
        errorLabel.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0.72), new CornerRadii(8), Insets.EMPTY)));
        errorLabel.setPadding(new Insets(18));
        StackPane.setAlignment(errorLabel, Pos.CENTER);
        root.getChildren().add(errorLabel);
    }
}
