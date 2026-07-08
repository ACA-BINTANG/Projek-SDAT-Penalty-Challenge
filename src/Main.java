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
import javafx.scene.input.KeyCode;
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
import javafx.scene.effect.BlendMode;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main extends Application {
    private static final String INTRO_VIDEO_PATH = "resources/video/Intro-Awal.mp4";
    private static final String MENU_BACKGROUND_PATH = "resources/images/Tampilan-BG-Menu.png";
    private static final String MENU_BOX_OFF_PATH = "resources/images/Box-Menu-off.png";
    private static final String MENU_BOX_ON_PATH = "resources/images/Box-Menu-on.png";
    private static final String TOURNAMENT_BACKGROUND_PATH = "resources/images/Tampilan-BG-Turnament.png";
    private static final String TOURNAMENT_BRACKET_PATH = "resources/images/Bagan-Tournament.png";
    private static final String GOAL_BACKGROUND_PATH = "resources/images/GAWANG.png";
    private static final String BALL_IMAGE_PATH = "resources/images/BOLA.png";
    private static final String KEEPER_IMAGE_PATH = "resources/images/karakter/keper/berdiri/1.png";
    private static final String KEEPER_IDLE_IMAGE_PATH = "resources/images/karakter/keper/berdiri/1.png";
    private static final String KEEPER_STANDING_CATCH_IMAGE_PATH = "resources/images/karakter/keper/berdiri tangkap/2.png";
    private static final String KEEPER_RIGHT_FOLDER = "resources/images/karakter/keper/lompat kanan";
    private static final String KEEPER_RIGHT_CATCH_FOLDER = "resources/images/karakter/keper/lompatkanantangkap";
    private static final String KEEPER_LEFT_FOLDER = "resources/images/karakter/keper/lompat kiri";
    private static final String KEEPER_LEFT_CATCH_FOLDER = "resources/images/karakter/keper/lompattangkapkiri";
    private static final String TOP_SCORE_PATH = "top_scores.txt";
    private static final String START_FONT_PATH = "resources/fonts/MinecraftBoldItalic-1y1e.otf";
    private static final String MENU_FONT_PATH = "resources/fonts/MinecraftRegular-Bmg3.otf";
    private static final double MENU_OPTION_WIDTH = 286.5;
    private static final double MENU_OPTION_HEIGHT = 59.5;
    private static final double BALL_SIZE = 92;
    private static final double KEEPER_SIZE = 260;

    // Sensor keeper mengikuti pose sprite hijau.
    // Berdiri = sensor vertikal, lompat kiri/kanan = sensor horizontal.
    private static final double KEEPER_STAND_SENSOR_WIDTH_RATIO = 0.40;
    private static final double KEEPER_STAND_SENSOR_HEIGHT_RATIO = 0.86;
    private static final double KEEPER_STAND_SENSOR_Y_OFFSET_RATIO = 0.03;
    private static final double KEEPER_DIVE_SENSOR_WIDTH_RATIO = 1.44;
    private static final double KEEPER_DIVE_SENSOR_HEIGHT_RATIO = 0.46;
    private static final double KEEPER_DIVE_SENSOR_X_OFFSET_RATIO = 0.20;
    private static final double KEEPER_DIVE_SENSOR_Y_OFFSET_RATIO = 0.10;
    private static final double KEEPER_EDGE_PADDING_RATIO = 0.015;
    private static final double MAX_PULL_DISTANCE = 160;
    private static final double MIN_SHOT_DISTANCE = 150;
    private static final double MAX_SHOT_DISTANCE = 840;
    private static final double MIN_BALL_SPEED = 520;
    private static final double MAX_BALL_SPEED = 1220;
    private static final double UPWARD_SHOT_BONUS = 1.08;
    private static final double KEEPER_MAX_READ_CHANCE = 0.82;
    private static final double KEEPER_READ_GROWTH_PER_POINT = 0.08;
    private static final double KEEPER_FRAME_SECONDS = 0.19;
    private static final double KEEPER_LANDING_FRAME_HOLD_SECONDS = 1.0;
    private static final double ROUND_RESULT_DELAY_SECONDS = 0.05;
    private static final double KEEPER_DIVE_TRIGGER_RATIO = 0.045;
    private static final double KEEPER_FINAL_HOLD_SECONDS = 2.0;
    private static final double KEEPER_MOVE_SPEED = 760;
    private static final double KEEPER_CATCH_FALL_SPEED = 420;
    private static final double KEEPER_CATCH_FALL_SIDE_RATIO = 0.50;
    private static final double KEEPER_START_CENTER_Y_RATIO = 0.56;
    private static final double KEEPER_GROUND_TARGET_Y_RATIO = 0.56;
    private static final double KEEPER_FALL_FORWARD_OFFSET_RATIO = 0.030;
    private static final double KEEPER_FRAME_5_DOWN_OFFSET = 42;
    private static final double KEEPER_TOP_REACH_PADDING_RATIO = 0.012;
    private static final double KEEPER_BOTTOM_REACH_PADDING_RATIO = 0.018;
    private static final double SHOT_MEMORY_WEIGHT = 0.35;
    private static final double GOAL_LEFT_RATIO = 0.24;
    private static final double GOAL_RIGHT_RATIO = 0.76;
    private static final double GOAL_TOP_RATIO = 0.28;
    private static final double GOAL_BOTTOM_RATIO = 0.65;
    private static final double GOAL_SCORE_LINE_RATIO = 0.30;
    private static final int ROUND_RESULT_NONE = 0;
    private static final int ROUND_RESULT_SAVED = 1;
    private static final int ROUND_RESULT_GOAL = 2;
    private static final int ROUND_RESULT_MISS = 3;
    private static final boolean SHOW_DEBUG_BOXES = false;
    private static final int MAX_PLAYER_LIVES = 3;
    private static final int TOURNAMENT_SHOTS_PER_ROUND = 5;
    private static final String[] TOURNAMENT_ROUNDS = {"QUARTER FINAL", "SEMI FINAL", "FINAL"};
    private static final int[] TOURNAMENT_TARGETS = {2, 3, 4};
    private static final String[] TOURNAMENT_OPPONENTS = {
            "JAVA UNITED",
            "PIXEL CITY",
            "BYTE ROVERS",
            "GARUDA FC",
            "SDAT CLUB",
            "CODE STRIKERS",
            "IDEA ATHLETIC"
    };
    private MediaPlayer introPlayer;
    private final Random random = new Random();

    @Override
    public void start(Stage stage) {
        StackPane root = new StackPane();
        root.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));

        Path videoPath = resolveResource(INTRO_VIDEO_PATH);
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

private Path resolveResource(String relativePath) {
        Path cleanPath = Path.of(relativePath.replace("/", java.io.File.separator)).normalize();

        Path[] candidates = new Path[] {
                Path.of("").toAbsolutePath().resolve(cleanPath).normalize(),
                Path.of(System.getProperty("user.dir", ".")).toAbsolutePath().resolve(cleanPath).normalize(),
                Path.of(System.getProperty("user.dir", ".")).toAbsolutePath().getParent() != null
                        ? Path.of(System.getProperty("user.dir", ".")).toAbsolutePath().getParent().resolve(cleanPath).normalize()
                        : Path.of(System.getProperty("user.dir", ".")).toAbsolutePath().resolve(cleanPath).normalize(),
                Path.of("src").toAbsolutePath().resolve(cleanPath).normalize()
        };

        for (Path candidate : candidates) {
            if (Files.exists(candidate)) {
                return candidate;
            }
        }

        return candidates[0];
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
        Path path = resolveResource(fontPath);
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
            } else if ("TOURNAMENT".equals(modes[i])) {
                option.setOnMouseClicked(event -> showTournamentMode(stage));
            }
            menuBox.getChildren().add(option);
        }

        root.getChildren().addAll(background, overlay, menuBox);

        Scene scene = new Scene(root, 1280, 720);
        stage.setScene(scene);
        stage.setFullScreen(true);
        scene.setCursor(Cursor.DEFAULT);
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
        Path path = resolveResource(imagePath);
        Image image = Files.exists(path)
                ? new Image(path.toUri().toString(), false)
                : createFallbackImage();
        return new ImageView(image);
    }

private Image createFallbackImage() {
        javafx.scene.canvas.Canvas canvas = new javafx.scene.canvas.Canvas(64, 64);
        javafx.scene.canvas.GraphicsContext graphics = canvas.getGraphicsContext2D();
        graphics.setFill(Color.rgb(40, 40, 40));
        graphics.fillRect(0, 0, 64, 64);
        graphics.setStroke(Color.WHITE);
        graphics.strokeRect(1, 1, 62, 62);
        javafx.scene.SnapshotParameters parameters = new javafx.scene.SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        return canvas.snapshot(parameters, null);
    }

    private Button createNavigationButton(String text) {
        Button button = new Button(text);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        button.setCursor(Cursor.HAND);
        button.setMinWidth(86);
        button.setPrefWidth(86);
        button.setMinHeight(32);
        button.setPrefHeight(32);
        return button;
    }

    private void setImage(ImageView imageView, String imagePath) {
        Path path = resolveResource(imagePath);
        imageView.setImage(Files.exists(path) ? new Image(path.toUri().toString(), false) : createFallbackImage());
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

        Button endlessMenuButton = createNavigationButton("MENU");
        endlessMenuButton.layoutXProperty().bind(root.widthProperty().subtract(118));
        endlessMenuButton.setLayoutY(28);
        endlessMenuButton.setOnAction(event -> showMenu(stage));

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
                endlessMenuButton,
                gameOverOverlay,
                gameOverText,
                saveScoreBox
        );
        root.getChildren().addAll(background, playLayer);

        Scene scene = new Scene(root, 1280, 720);
        stage.setScene(scene);
        stage.setFullScreen(true);
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                showMenu(stage);
            }
        });

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

            chooseKeeperTarget(root, state);
            keeperAnimator.startDive(state.keeperDiveDirection, state.keeperWillCatch);
            state.ballMoving = true;
            state.keeperMoving = state.keeperJumping;
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
                        keeperAnimator,
                        keeperBoxOverlay,
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

    private void showTournamentMode(Stage stage) {
        StackPane root = new StackPane();
        Pane playLayer = new Pane();

        ImageView background = createImageView(TOURNAMENT_BACKGROUND_PATH);
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

        Text roundText = new Text();
        roundText.setFill(Color.WHITE);
        roundText.setFont(loadFont(MENU_FONT_PATH, 28, Font.font("Arial", FontWeight.BOLD, 28)));
        roundText.layoutXProperty().bind(Bindings.createDoubleBinding(
                () -> (root.getWidth() - roundText.getLayoutBounds().getWidth()) / 2,
                root.widthProperty(),
                roundText.layoutBoundsProperty()
        ));
        roundText.setLayoutY(52);

        Text targetText = new Text();
        targetText.setFill(Color.WHITE);
        targetText.setFont(loadFont(MENU_FONT_PATH, 20, Font.font("Arial", FontWeight.BOLD, 20)));
        targetText.setLayoutX(32);
        targetText.setLayoutY(48);

        Text shotsText = new Text();
        shotsText.setFill(Color.WHITE);
        shotsText.setFont(loadFont(MENU_FONT_PATH, 20, Font.font("Arial", FontWeight.BOLD, 20)));
        shotsText.setLayoutX(32);
        shotsText.setLayoutY(78);

        Text totalText = new Text();
        totalText.setFill(Color.WHITE);
        totalText.setFont(loadFont(MENU_FONT_PATH, 20, Font.font("Arial", FontWeight.BOLD, 20)));
        totalText.layoutXProperty().bind(root.widthProperty().subtract(210));
        totalText.setLayoutY(48);

        Text hintText = new Text("Tarik bola, lalu lepas");
        hintText.setFill(Color.rgb(255, 255, 255, 0.82));
        hintText.setFont(loadFont(MENU_FONT_PATH, 16, Font.font("Arial", FontWeight.BOLD, 16)));
        hintText.layoutXProperty().bind(root.widthProperty().subtract(250));
        hintText.setLayoutY(78);

        Button backToBracketButton = createNavigationButton("KEMBALI");
        backToBracketButton.layoutXProperty().bind(root.widthProperty().subtract(118));
        backToBracketButton.setLayoutY(104);
        backToBracketButton.setVisible(false);

        Rectangle resultOverlay = new Rectangle();
        resultOverlay.widthProperty().bind(root.widthProperty());
        resultOverlay.heightProperty().bind(root.heightProperty());
        resultOverlay.setFill(Color.rgb(0, 0, 0, 0.62));
        resultOverlay.setVisible(false);

        Text resultTitle = new Text();
        resultTitle.setFill(Color.WHITE);
        resultTitle.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        resultTitle.setFont(loadFont(MENU_FONT_PATH, 40, Font.font("Arial", FontWeight.EXTRA_BOLD, 40)));
        resultTitle.setMouseTransparent(true);

        Text resultDetail = new Text();
        resultDetail.setFill(Color.rgb(255, 230, 120));
        resultDetail.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        resultDetail.setFont(loadFont(MENU_FONT_PATH, 19, Font.font("Arial", FontWeight.BOLD, 19)));
        resultDetail.setMouseTransparent(true);

        Button primaryButton = new Button("LANJUT");
        Button menuButton = new Button("MENU");
        primaryButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        menuButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        primaryButton.setCursor(Cursor.HAND);
        menuButton.setCursor(Cursor.HAND);

        HBox resultButtons = new HBox(12, primaryButton, menuButton);
        resultButtons.setAlignment(Pos.CENTER);

        VBox resultBox = new VBox(14, resultTitle, resultDetail, resultButtons);
        resultBox.setAlignment(Pos.CENTER);
        resultBox.setPadding(new Insets(18));
        resultBox.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0.72), new CornerRadii(8), Insets.EMPTY)));
        resultBox.setMaxWidth(470);
        resultBox.layoutXProperty().bind(Bindings.createDoubleBinding(
                () -> (root.getWidth() - resultBox.getBoundsInLocal().getWidth()) / 2,
                root.widthProperty(),
                resultBox.boundsInLocalProperty()
        ));
        resultBox.layoutYProperty().bind(root.heightProperty().multiply(0.42));
        resultBox.setVisible(false);

        Label[] bracketLabels = new Label[15];
        StackPane bracketOverlay = createTournamentBracketOverlay(bracketLabels);
        bracketOverlay.prefWidthProperty().bind(root.widthProperty());
        bracketOverlay.prefHeightProperty().bind(root.heightProperty());
        Button startMatchButton = (Button) bracketOverlay.getProperties().get("startMatchButton");
        Button bracketMenuButton = (Button) bracketOverlay.getProperties().get("menuButton");
        Button fourTeamButton = (Button) bracketOverlay.getProperties().get("fourTeamButton");
        Button eightTeamButton = (Button) bracketOverlay.getProperties().get("eightTeamButton");
        TextField teamNameInput = (TextField) bracketOverlay.getProperties().get("teamNameInput");
        Text setupStatusText = (Text) bracketOverlay.getProperties().get("setupStatusText");
        StackPane bracketHolder = (StackPane) bracketOverlay.getProperties().get("bracketHolder");
        Runnable unlockTournamentSetup = () -> {
            teamNameInput.setDisable(false);
            fourTeamButton.setDisable(false);
            eightTeamButton.setDisable(false);
        };
        Runnable lockTournamentSetup = () -> {
            teamNameInput.setDisable(true);
            fourTeamButton.setDisable(true);
            eightTeamButton.setDisable(true);
        };

        playLayer.getChildren().addAll(
                pullLine,
                targetMarker,
                keeper,
                ball,
                roundText,
                targetText,
                shotsText,
                totalText,
                hintText,
                backToBracketButton,
                resultOverlay,
                resultBox,
                bracketOverlay
        );
        root.getChildren().addAll(background, playLayer);

        Scene scene = new Scene(root, 1280, 720);
        stage.setScene(scene);
        stage.setFullScreen(true);
        ball.setCursor(Cursor.DEFAULT);
        setTournamentPlayObjectsVisible(ball, keeper, pullLine, targetMarker, false);

        TournamentState state = new TournamentState();
        state.teamCount = 8;
        state.playerTeamName = "PLAYER FC";
        state.opponents = createTournamentOpponents(state.teamCount);
        Runnable resetRound = () -> resetEndlessRound(root, ball, keeper, pullLine, targetMarker, state, keeperAnimator);
        Runnable refreshUi = () -> {
            updateTournamentTexts(roundText, targetText, shotsText, totalText, state);
            updateTournamentBracketLabels(bracketLabels, state);
        };
        Runnable backToBracket = () -> {
            state.ballMoving = false;
            state.dragging = false;
            state.keeperMoving = false;
            pullLine.setVisible(false);
            targetMarker.setVisible(false);
            setImage(background, TOURNAMENT_BACKGROUND_PATH);
            bracketOverlay.setVisible(true);
            backToBracketButton.setVisible(false);
            setTournamentPlayObjectsVisible(ball, keeper, pullLine, targetMarker, false);
            ball.setCursor(Cursor.DEFAULT);
            refreshUi.run();
            resetRound.run();
        };
        Runnable restartTournament = () -> {
            state.roundIndex = 0;
            state.roundGoals = 0;
            state.shotsTaken = 0;
            state.totalGoals = 0;
            state.scoredShotsLearned = 0;
            state.gameOver = false;
            state.roundFinished = false;
            state.eliminated = false;
            state.champion = false;
            state.setupDone = false;
            state.opponents = createTournamentOpponents(state.teamCount);
            teamNameInput.setText(state.playerTeamName);
            setupStatusText.setText("Isi nama tim, pilih jumlah tim, lalu mulai");
            startMatchButton.setText("MULAI");
            unlockTournamentSetup.run();
            ball.setCursor(Cursor.DEFAULT);
            resultOverlay.setVisible(false);
            resultBox.setVisible(false);
            setImage(background, TOURNAMENT_BACKGROUND_PATH);
            bracketOverlay.setVisible(true);
            backToBracketButton.setVisible(false);
            setTournamentPlayObjectsVisible(ball, keeper, pullLine, targetMarker, false);
            refreshUi.run();
            resetRound.run();
        };

        root.widthProperty().addListener((observable, oldValue, newValue) -> resetRound.run());
        root.heightProperty().addListener((observable, oldValue, newValue) -> resetRound.run());
        teamNameInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!state.setupDone) {
                state.playerTeamName = cleanTeamName(newValue);
                refreshUi.run();
            }
        });
        Platform.runLater(() -> {
            refreshUi.run();
            resetRound.run();
        });

        primaryButton.setOnAction(event -> {
            if (state.champion || state.eliminated) {
                restartTournament.run();
                return;
            }

            state.roundIndex++;
            state.roundGoals = 0;
            state.shotsTaken = 0;
            state.roundFinished = false;
            resultOverlay.setVisible(false);
            resultBox.setVisible(false);
            setImage(background, TOURNAMENT_BACKGROUND_PATH);
            bracketOverlay.setVisible(true);
            backToBracketButton.setVisible(false);
            setTournamentPlayObjectsVisible(ball, keeper, pullLine, targetMarker, false);
            ball.setCursor(Cursor.DEFAULT);
            refreshUi.run();
            resetRound.run();
        });
        menuButton.setOnAction(event -> showMenu(stage));
        backToBracketButton.setOnAction(event -> backToBracket.run());
        fourTeamButton.setOnAction(event -> {
            state.teamCount = 4;
            state.opponents = createTournamentOpponents(state.teamCount);
            rebuildTournamentBracketBoard(bracketHolder, bracketLabels, state.teamCount);
            setupStatusText.setText("Turnamen 4 tim dipilih");
            refreshUi.run();
        });
        eightTeamButton.setOnAction(event -> {
            state.teamCount = 8;
            state.opponents = createTournamentOpponents(state.teamCount);
            rebuildTournamentBracketBoard(bracketHolder, bracketLabels, state.teamCount);
            setupStatusText.setText("Turnamen 8 tim dipilih");
            refreshUi.run();
        });
        startMatchButton.setOnAction(event -> {
            if (state.champion || state.eliminated) {
                startMatchButton.setText("MULAI");
                restartTournament.run();
                return;
            }
            if (!state.setupDone) {
                String teamName = teamNameInput.getText().trim();
                if (teamName.isEmpty()) {
                    setupStatusText.setText("Nama tim harus diisi");
                    return;
                }
                state.playerTeamName = cleanTeamName(teamName);
                state.roundIndex = 0;
                state.roundGoals = 0;
                state.shotsTaken = 0;
                state.totalGoals = 0;
                state.scoredShotsLearned = 0;
                state.eliminated = false;
                state.champion = false;
                state.setupDone = true;
                lockTournamentSetup.run();
                refreshUi.run();
            }
            setImage(background, GOAL_BACKGROUND_PATH);
            bracketOverlay.setVisible(false);
            backToBracketButton.setVisible(true);
            setTournamentPlayObjectsVisible(ball, keeper, pullLine, targetMarker, true);
            ball.setCursor(Cursor.HAND);
            resetRound.run();
        });
        bracketMenuButton.setOnAction(event -> showMenu(stage));

        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                if (bracketOverlay.isVisible()) {
                    showMenu(stage);
                } else {
                    backToBracket.run();
                }
            }
        });

        ball.setOnMousePressed(event -> {
            if (bracketOverlay.isVisible() || state.gameOver || state.roundFinished || state.ballMoving) {
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
            if (bracketOverlay.isVisible() || state.gameOver || state.roundFinished || !state.dragging || state.ballMoving) {
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
            if (bracketOverlay.isVisible() || state.gameOver || state.roundFinished || !state.dragging || state.ballMoving) {
                return;
            }

            state.dragging = false;
            pullLine.setVisible(false);
            targetMarker.setVisible(false);
            applyKickForce(root, ball, state);
            if (state.shotSpeed < MIN_BALL_SPEED + 20) {
                registerTournamentShot(false, ball, keeper, state, resultOverlay, resultBox, resultTitle, resultDetail, primaryButton, background, bracketOverlay, startMatchButton, bracketLabels, refreshUi, resetRound);
                return;
            }

            chooseKeeperTarget(root, state);
            keeperAnimator.startDive(state.keeperDiveDirection, state.keeperWillCatch);
            state.ballMoving = true;
            state.keeperMoving = state.keeperJumping;
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
                updateTournament(
                        root,
                        ball,
                        keeper,
                        keeperAnimator,
                        resultOverlay,
                        resultBox,
                        resultTitle,
                        resultDetail,
                        primaryButton,
                        background,
                        bracketOverlay,
                        startMatchButton,
                        bracketLabels,
                        state,
                        refreshUi,
                        resetRound,
                        deltaSeconds
                );
            }
        };
        gameLoop.start();
    }

    private StackPane createTournamentBracketOverlay(Label[] bracketLabels) {
        Rectangle dim = new Rectangle();
        dim.setFill(Color.rgb(0, 0, 0, 0.18));

        Text title = new Text("BAGAN TIM");
        title.setFill(Color.WHITE);
        title.setFont(loadFont(MENU_FONT_PATH, 34, Font.font("Arial", FontWeight.EXTRA_BOLD, 34)));

        TextField teamNameInput = new TextField("PLAYER FC");
        teamNameInput.setPromptText("Nama tim kamu");
        teamNameInput.setMaxWidth(260);
        teamNameInput.setFont(Font.font("Arial", FontWeight.BOLD, 15));

        Button fourTeamButton = new Button("4 TIM");
        Button eightTeamButton = new Button("8 TIM");
        fourTeamButton.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        eightTeamButton.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        fourTeamButton.setCursor(Cursor.HAND);
        eightTeamButton.setCursor(Cursor.HAND);

        HBox teamCountBox = new HBox(10, fourTeamButton, eightTeamButton);
        teamCountBox.setAlignment(Pos.CENTER);

        Text setupStatusText = new Text("Isi nama tim, pilih jumlah tim, lalu mulai");
        setupStatusText.setFill(Color.rgb(255, 230, 120));
        setupStatusText.setFont(Font.font("Arial", FontWeight.BOLD, 13));

        VBox setupBox = new VBox(8, teamNameInput, teamCountBox, setupStatusText);
        setupBox.setAlignment(Pos.CENTER);

        StackPane bracketHolder = new StackPane();
        rebuildTournamentBracketBoard(bracketHolder, bracketLabels, 8);

        Button startMatchButton = new Button("MULAI");
        Button menuButton = new Button("MENU");
        startMatchButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        menuButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        startMatchButton.setCursor(Cursor.HAND);
        menuButton.setCursor(Cursor.HAND);

        HBox actions = new HBox(12, startMatchButton, menuButton);
        actions.setAlignment(Pos.CENTER);

        VBox content = new VBox(14, title, setupBox, bracketHolder, actions);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(24));
        content.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        content.setMaxWidth(1020);

        StackPane overlay = new StackPane(dim, content);
        dim.widthProperty().bind(overlay.widthProperty());
        dim.heightProperty().bind(overlay.heightProperty());
        overlay.setVisible(true);
        overlay.getProperties().put("startMatchButton", startMatchButton);
        overlay.getProperties().put("menuButton", menuButton);
        overlay.getProperties().put("fourTeamButton", fourTeamButton);
        overlay.getProperties().put("eightTeamButton", eightTeamButton);
        overlay.getProperties().put("teamNameInput", teamNameInput);
        overlay.getProperties().put("setupStatusText", setupStatusText);
        overlay.getProperties().put("bracketHolder", bracketHolder);
        return overlay;
    }

    private void rebuildTournamentBracketBoard(StackPane bracketHolder, Label[] bracketLabels, int teamCount) {
        for (int i = 0; i < bracketLabels.length; i++) {
            bracketLabels[i] = null;
        }
        bracketHolder.getChildren().setAll(teamCount <= 4
                ? createFourTeamBracketBoard(bracketLabels)
                : createEightTeamBracketBoard(bracketLabels));
    }

    private StackPane createEightTeamBracketBoard(Label[] bracketLabels) {
        double boardWidth = 920;
        double boardHeight = 517.5;
        double scaleX = boardWidth / 1600.0;
        double scaleY = boardHeight / 900.0;

        ImageView bracketImage = createImageView(TOURNAMENT_BRACKET_PATH);
        bracketImage.setPreserveRatio(false);
        bracketImage.setFitWidth(boardWidth);
        bracketImage.setFitHeight(boardHeight);
        bracketImage.setBlendMode(BlendMode.SCREEN);

        Pane labels = new Pane();
        labels.setPrefSize(boardWidth, boardHeight);
        labels.setMinSize(boardWidth, boardHeight);
        labels.setMaxSize(boardWidth, boardHeight);
        labels.setMouseTransparent(true);

        double[][] labelPositions = {
                {204, 223}, {204, 367}, {204, 545}, {204, 689},
                {1397, 223}, {1397, 367}, {1397, 545}, {1397, 689},
                {514, 299}, {1085, 299}, {514, 624}, {1085, 624},
                {650, 450}, {950, 450}, {800, 450}
        };

        double[] widths = {128, 128, 128, 128, 128, 128, 128, 128, 95, 95, 95, 95, 150, 150, 178};
        for (int i = 0; i < bracketLabels.length && i < labelPositions.length; i++) {
            double labelWidth = widths[i];
            Label label = createBracketLabel(labelWidth);
            double centerX = labelPositions[i][0] * scaleX;
            double centerY = labelPositions[i][1] * scaleY;
            label.setLayoutX(centerX - labelWidth / 2);
            label.setLayoutY(centerY - 10);
            bracketLabels[i] = label;
            labels.getChildren().add(label);
        }

        StackPane board = new StackPane(bracketImage, labels);
        board.setMinSize(boardWidth, boardHeight);
        board.setMaxSize(boardWidth, boardHeight);
        return board;
    }

    private Pane createFourTeamBracketBoard(Label[] bracketLabels) {
        double boardWidth = 920;
        double boardHeight = 430;
        Pane board = new Pane();
        board.setMinSize(boardWidth, boardHeight);
        board.setPrefSize(boardWidth, boardHeight);
        board.setMaxSize(boardWidth, boardHeight);
        board.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));

        addBracketLine(board, 250, 132, 360, 132);
        addBracketLine(board, 250, 292, 360, 292);
        addBracketLine(board, 360, 132, 360, 292);
        addBracketLine(board, 360, 212, 475, 212);
        addBracketLine(board, 670, 212, 560, 212);

        addBracketSlot(board, bracketLabels, 8, 70, 100, 180, 64, false);
        addBracketSlot(board, bracketLabels, 10, 70, 260, 180, 64, false);
        addBracketSlot(board, bracketLabels, 12, 475, 180, 185, 64, false);
        addBracketSlot(board, bracketLabels, 13, 670, 180, 180, 64, false);
        addBracketSlot(board, bracketLabels, 14, 365, 28, 190, 64, true);

        return board;
    }

    private void addBracketLine(Pane board, double startX, double startY, double endX, double endY) {
        Line line = new Line(startX, startY, endX, endY);
        line.setStroke(Color.WHITE);
        line.setStrokeWidth(4);
        board.getChildren().add(line);
    }

    private void addBracketSlot(Pane board, Label[] bracketLabels, int index, double x, double y, double width, double height, boolean championSlot) {
        Rectangle frame = new Rectangle(width, height);
        frame.setLayoutX(x);
        frame.setLayoutY(y);
        frame.setArcWidth(14);
        frame.setArcHeight(14);
        frame.setFill(Color.rgb(9, 17, 23, 0.38));
        frame.setStroke(championSlot ? Color.rgb(255, 188, 42) : Color.WHITE);
        frame.setStrokeWidth(4);

        Label label = createBracketLabel(width - 20);
        label.setLayoutX(x + 10);
        label.setLayoutY(y + (height - 24) / 2);
        bracketLabels[index] = label;

        board.getChildren().addAll(frame, label);
    }

    private Label createBracketLabel(double width) {
        Label label = new Label("");
        label.setTextFill(Color.WHITE);
        label.setFont(loadFont(MENU_FONT_PATH, 10, Font.font("Arial", FontWeight.BOLD, 10)));
        label.setAlignment(Pos.CENTER);
        label.setMinWidth(width);
        label.setPrefWidth(width);
        label.setMaxWidth(width);
        label.setMinHeight(24);
        label.setPrefHeight(24);
        label.setMaxHeight(24);
        label.setMouseTransparent(true);
        return label;
    }

    private void updateTournamentBracketLabels(Label[] bracketLabels, TournamentState state) {
        for (int i = 0; i < bracketLabels.length; i++) {
            setBracketLabel(bracketLabels, i, "", false);
        }

        String playerTeam = state.playerTeamName != null && !state.playerTeamName.isEmpty()
                ? state.playerTeamName
                : "PLAYER FC";

        if (state.teamCount <= 4) {
            setBracketLabel(bracketLabels, 8, playerTeam, true);
            if (state.setupDone) {
                setBracketLabel(bracketLabels, 10, getTournamentOpponent(state, 0), false);
            }
            if (state.roundIndex > 0 || state.champion) {
                setBracketLabel(bracketLabels, 12, playerTeam, true);
                setBracketLabel(bracketLabels, 13, getTournamentOpponent(state, 1), false);
            }
        } else {
            setBracketLabel(bracketLabels, 0, playerTeam, true);
            if (state.setupDone) {
                setBracketLabel(bracketLabels, 1, getTournamentOpponent(state, 0), false);
            }
            if (state.roundIndex > 0 || state.champion) {
                setBracketLabel(bracketLabels, 8, playerTeam, true);
                setBracketLabel(bracketLabels, 10, getTournamentOpponent(state, 1), false);
            }
            if (state.roundIndex > 1 || state.champion) {
                setBracketLabel(bracketLabels, 12, playerTeam, true);
                setBracketLabel(bracketLabels, 13, getTournamentOpponent(state, 2), false);
            }
        }

        if (state.champion) {
            setBracketLabel(bracketLabels, 14, playerTeam, true);
        }

        if (state.eliminated) {
            setBracketLabel(bracketLabels, 14, "TERHENTI", false);
        }
    }

    private void setTournamentPlayObjectsVisible(
            ImageView ball,
            ImageView keeper,
            Line pullLine,
            Circle targetMarker,
            boolean visible
    ) {
        if (ball != null) {
            ball.setVisible(visible);
        }
        if (keeper != null) {
            keeper.setVisible(visible);
        }
        if (pullLine != null) {
            pullLine.setVisible(false);
        }
        if (targetMarker != null) {
            targetMarker.setVisible(false);
        }
    }

    private void setBracketLabel(Label[] bracketLabels, int index, String value, boolean playerTeam) {
        if (bracketLabels[index] == null) {
            return;
        }
        bracketLabels[index].setText(shortenBracketName(value));
        bracketLabels[index].setTextFill(playerTeam ? Color.rgb(75, 210, 255) : Color.WHITE);
    }

    private String shortenBracketName(String value) {
        if (value == null) {
            return "";
        }
        String cleanValue = value.trim();
        if (cleanValue.length() > 13) {
            return cleanValue.substring(0, 13);
        }
        return cleanValue;
    }

    private int getTournamentRoundCount(TournamentState state) {
        return state.teamCount <= 4 ? 2 : 3;
    }

    private String getTournamentRoundName(TournamentState state) {
        int roundOffset = state.teamCount <= 4 ? 1 : 0;
        int roundIndex = clampInt(state.roundIndex + roundOffset, 0, TOURNAMENT_ROUNDS.length - 1);
        return TOURNAMENT_ROUNDS[roundIndex];
    }

    private int getTournamentTarget(TournamentState state) {
        int roundOffset = state.teamCount <= 4 ? 1 : 0;
        int targetIndex = clampInt(state.roundIndex + roundOffset, 0, TOURNAMENT_TARGETS.length - 1);
        return TOURNAMENT_TARGETS[targetIndex];
    }

    private String[] createTournamentOpponents(int teamCount) {
        int opponentCount = Math.max(1, Math.min(teamCount - 1, TOURNAMENT_OPPONENTS.length));
        String[] opponents = new String[opponentCount];
        boolean[] used = new boolean[TOURNAMENT_OPPONENTS.length];
        for (int i = 0; i < opponents.length; i++) {
            int candidate = random.nextInt(TOURNAMENT_OPPONENTS.length);
            while (used[candidate]) {
                candidate = (candidate + 1) % TOURNAMENT_OPPONENTS.length;
            }
            used[candidate] = true;
            opponents[i] = TOURNAMENT_OPPONENTS[candidate];
        }
        return opponents;
    }

    private String getTournamentOpponent(TournamentState state, int roundIndex) {
        if (state.opponents == null || roundIndex < 0 || roundIndex >= state.opponents.length) {
            return "TBD";
        }
        return state.opponents[roundIndex];
    }

    private String cleanTeamName(String teamName) {
        if (teamName == null) {
            return "";
        }
        String cleanName = teamName.replace("\t", " ").replace("\r", " ").replace("\n", " ").trim();
        if (cleanName.length() > 16) {
            return cleanName.substring(0, 16);
        }
        return cleanName;
    }

    private int clampInt(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private void updateTournament(
            StackPane root,
            ImageView ball,
            ImageView keeper,
            KeeperAnimator keeperAnimator,
            Rectangle resultOverlay,
            VBox resultBox,
            Text resultTitle,
            Text resultDetail,
            Button primaryButton,
            ImageView background,
            StackPane bracketOverlay,
            Button startMatchButton,
            Label[] bracketLabels,
            TournamentState state,
            Runnable refreshUi,
            Runnable resetRound,
            double deltaSeconds
    ) {
        if (state.gameOver || state.roundFinished) {
            return;
        }

        keeperAnimator.update(deltaSeconds);

        if (keeperAnimator.consumeDiveFallEvent()) {
            if (state.keeperDiveDirection != 0) {
                startKeeperDiveFall(root, keeper, state);
            }
        }

        if (keeperAnimator.consumeCatchBallHideEvent()) {
            ball.setVisible(false);
            state.ballMoving = false;
            queueRoundResolutionAfterKeeperAnimation(state, ROUND_RESULT_SAVED);
        }

        if (state.keeperFallingAfterCatch) {
            updateKeeperDiveFall(keeper, state, deltaSeconds);
        }

        if (state.keeperMoving) {
            double keeperCenterX = getCenterX(keeper);
            double keeperCenterY = getCenterY(keeper);
            double dx = state.keeperTargetX - keeperCenterX;
            double dy = state.keeperTargetY - keeperCenterY;
            double distance = Math.hypot(dx, dy);
            double step = (KEEPER_MOVE_SPEED + state.roundIndex * 45) * deltaSeconds;
            if (distance <= step) {
                setCenter(keeper, state.keeperTargetX, state.keeperTargetY);
                state.keeperMoving = false;
            } else {
                setCenter(keeper, keeperCenterX + dx / distance * step, keeperCenterY + dy / distance * step);
            }
        }

        if (state.roundResolving) {
            state.roundResolveTimer -= deltaSeconds;
            if (state.roundResolveTimer <= 0) {
                int result = state.roundResult;
                state.roundResolving = false;
                state.awaitingKeeperAnimationFinish = false;
                state.pendingRoundResult = ROUND_RESULT_NONE;
                state.roundResult = ROUND_RESULT_NONE;
                state.roundResolveTimer = 0;

                if (result == ROUND_RESULT_GOAL) {
                    rememberScoredShot(state);
                    registerTournamentShot(true, ball, keeper, state, resultOverlay, resultBox, resultTitle, resultDetail, primaryButton, background, bracketOverlay, startMatchButton, bracketLabels, refreshUi, resetRound);
                } else {
                    registerTournamentShot(false, ball, keeper, state, resultOverlay, resultBox, resultTitle, resultDetail, primaryButton, background, bracketOverlay, startMatchButton, bracketLabels, refreshUi, resetRound);
                }
            }
            return;
        }

        if (state.awaitingKeeperAnimationFinish) {
            if (keeperAnimator.isSequenceFinished() && !state.keeperFallingAfterCatch) {
                beginRoundResolution(state, state.pendingRoundResult);
            }
            return;
        }

        if (!state.ballMoving) {
            return;
        }

        double ballCenterX = getCenterX(ball);
        double ballCenterY = getCenterY(ball);
        double distanceToTarget = Math.hypot(state.targetX - ballCenterX, state.targetY - ballCenterY);
        double step = state.shotSpeed * deltaSeconds;
        boolean reachedTarget = distanceToTarget <= step;
        if (reachedTarget) {
            setCenter(ball, state.targetX, state.targetY);
        } else {
            setCenter(
                    ball,
                    ballCenterX + (state.targetX - ballCenterX) / distanceToTarget * step,
                    ballCenterY + (state.targetY - ballCenterY) / distanceToTarget * step
            );
        }

        double width = root.getWidth();
        double height = root.getHeight();
        boolean outsideScreen = getCenterX(ball) < -80
                || getCenterX(ball) > width + 80
                || getCenterY(ball) < -80
                || getCenterY(ball) > height + 80;

        if (reachedTarget && isShotSavedByKeeper(keeper, keeperAnimator, state)) {
            state.ballMoving = false;
            return;
        }

        if (reachedTarget && isBallInsidePointBox(root, ball)) {
            queueRoundResolutionAfterKeeperAnimation(state, ROUND_RESULT_GOAL);
            return;
        }

        if (outsideScreen || reachedTarget) {
            queueRoundResolutionAfterKeeperAnimation(state, ROUND_RESULT_MISS);
        }
    }

    private void registerTournamentShot(
            boolean goal,
            ImageView ball,
            ImageView keeper,
            TournamentState state,
            Rectangle resultOverlay,
            VBox resultBox,
            Text resultTitle,
            Text resultDetail,
            Button primaryButton,
            ImageView background,
            StackPane bracketOverlay,
            Button startMatchButton,
            Label[] bracketLabels,
            Runnable refreshUi,
            Runnable resetRound
    ) {
        state.ballMoving = false;
        state.dragging = false;
        state.keeperMoving = false;
        state.shotsTaken++;

        if (goal) {
            state.roundGoals++;
            state.totalGoals++;
            state.score = state.totalGoals;
        }

        refreshUi.run();

        int target = getTournamentTarget(state);
        int shotsLeft = TOURNAMENT_SHOTS_PER_ROUND - state.shotsTaken;
        boolean impossibleToQualify = state.roundGoals + shotsLeft < target;
        boolean targetReached = state.roundGoals >= target;
        boolean shotsFinished = state.shotsTaken >= TOURNAMENT_SHOTS_PER_ROUND;

        if (targetReached) {
            state.roundFinished = true;
            boolean finalRound = state.roundIndex == getTournamentRoundCount(state) - 1;
            state.champion = finalRound;
            state.eliminated = false;
            updateTournamentBracketLabels(bracketLabels, state);
            setImage(background, TOURNAMENT_BACKGROUND_PATH);
            resultTitle.setText(finalRound ? "JUARA TURNAMEN" : "RONDE LOLOS");
            resultDetail.setText(getTournamentRoundName(state) + "\nGol: " + state.roundGoals + "/" + TOURNAMENT_SHOTS_PER_ROUND
                    + "\nTotal gol: " + state.totalGoals);
            primaryButton.setText(finalRound ? "ULANGI" : "LANJUT");
            ball.setCursor(Cursor.DEFAULT);
            if (finalRound) {
                startMatchButton.setText("ULANGI");
                resultOverlay.setVisible(false);
                resultBox.setVisible(false);
                setTournamentPlayObjectsVisible(ball, keeper, null, null, false);
                bracketOverlay.setVisible(true);
            } else {
                resultOverlay.setVisible(true);
                resultBox.setVisible(true);
            }
            return;
        }

        if (impossibleToQualify || shotsFinished) {
            state.roundFinished = true;
            state.eliminated = true;
            state.champion = false;
            updateTournamentBracketLabels(bracketLabels, state);
            setImage(background, TOURNAMENT_BACKGROUND_PATH);
            resultTitle.setText("ELIMINASI");
            resultDetail.setText(getTournamentRoundName(state) + "\nTarget: " + target
                    + " gol\nGol kamu: " + state.roundGoals + "\nTotal gol: " + state.totalGoals);
            primaryButton.setText("ULANGI");
            ball.setCursor(Cursor.DEFAULT);
            resultOverlay.setVisible(true);
            resultBox.setVisible(true);
            return;
        }

        resetRound.run();
    }

    private void updateTournamentTexts(Text roundText, Text targetText, Text shotsText, Text totalText, TournamentState state) {
        int target = getTournamentTarget(state);
        int shotsLeft = TOURNAMENT_SHOTS_PER_ROUND - state.shotsTaken;
        roundText.setText(getTournamentRoundName(state));
        targetText.setText("TARGET: " + state.roundGoals + "/" + target);
        shotsText.setText("SISA SHOT: " + shotsLeft);
        totalText.setText("TOTAL: " + state.totalGoals);
    }

    private void updateEndless(
            StackPane root,
            ImageView ball,
            ImageView keeper,
            KeeperAnimator keeperAnimator,
            Rectangle keeperBoxOverlay,
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

        keeperAnimator.update(deltaSeconds);
        updateKeeperSensorOverlay(keeperBoxOverlay, keeper, keeperAnimator, state);

        if (keeperAnimator.consumeDiveFallEvent()) {
            if (state.keeperDiveDirection != 0) {
                startKeeperDiveFall(root, keeper, state);
            }
        }

        if (keeperAnimator.consumeCatchBallHideEvent()) {
            ball.setVisible(false);
            state.ballMoving = false;
            queueRoundResolutionAfterKeeperAnimation(state, ROUND_RESULT_SAVED);
        }

        if (state.keeperFallingAfterCatch) {
            updateKeeperDiveFall(keeper, state, deltaSeconds);
            updateKeeperSensorOverlay(keeperBoxOverlay, keeper, keeperAnimator, state);
        }

        if (state.keeperMoving) {
            double keeperCenterX = getCenterX(keeper);
            double keeperCenterY = getCenterY(keeper);
            double dx = state.keeperTargetX - keeperCenterX;
            double dy = state.keeperTargetY - keeperCenterY;
            double distance = Math.hypot(dx, dy);
            double step = KEEPER_MOVE_SPEED * deltaSeconds;
            if (distance <= step) {
                setCenter(keeper, state.keeperTargetX, state.keeperTargetY);
                state.keeperMoving = false;
            } else {
                setCenter(keeper, keeperCenterX + dx / distance * step, keeperCenterY + dy / distance * step);
            }
            updateKeeperSensorOverlay(keeperBoxOverlay, keeper, keeperAnimator, state);
        }

        if (state.roundResolving) {
            state.roundResolveTimer -= deltaSeconds;
            if (state.roundResolveTimer <= 0) {
                finishRoundResolution(
                        state,
                        scoreText,
                        livesText,
                        lifeIndicators,
                        gameOverOverlay,
                        gameOverText,
                        saveScoreBox,
                        finalScoreText,
                        nameInput,
                        resetRound,
                        ball
                );
            }
            return;
        }

        if (state.awaitingKeeperAnimationFinish) {
            if (keeperAnimator.isSequenceFinished() && !state.keeperFallingAfterCatch) {
                beginRoundResolution(state, state.pendingRoundResult);
            }
            return;
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

        double width = root.getWidth();
        double height = root.getHeight();
        boolean outsideScreen = getCenterX(ball) < -80
                || getCenterX(ball) > width + 80
                || getCenterY(ball) < -80
                || getCenterY(ball) > height + 80;

        if (reachedTarget && isShotSavedByKeeper(keeper, keeperAnimator, state)) {
            state.ballMoving = false;
            return;
        }

        if (reachedTarget && isBallInsidePointBox(root, ball)) {
            queueRoundResolutionAfterKeeperAnimation(state, ROUND_RESULT_GOAL);
            return;
        }

        if (outsideScreen || reachedTarget) {
            queueRoundResolutionAfterKeeperAnimation(state, ROUND_RESULT_MISS);
        }
    }

    private void startKeeperDiveFall(StackPane root, ImageView keeper, EndlessState state) {
        // Frame 4 adalah titik ayunan/lompatan. Setelah frame ini keeper selalu turun
        // sambil tetap geser ke arah lompatan, baik bola tertangkap maupun tidak.
        state.keeperMoving = false;
        state.keeperFallingAfterCatch = true;

        double direction = state.keeperDiveDirection == 0 ? 0 : Math.signum(state.keeperDiveDirection);
        double currentX = getCenterX(keeper);
        double currentY = getCenterY(keeper);

        double fallSideDistance = KEEPER_SIZE * KEEPER_CATCH_FALL_SIDE_RATIO;
        state.keeperFallTargetX = clamp(
                currentX + direction * fallSideDistance,
                getKeeperMovementMinX(root),
                getKeeperMovementMaxX(root)
        );
        double fallTargetY = root.getHeight() * KEEPER_GROUND_TARGET_Y_RATIO
                + root.getHeight() * KEEPER_FALL_FORWARD_OFFSET_RATIO;

        state.keeperFallTargetY = Math.max(
                currentY,
                fallTargetY
        );
    }

    private void updateKeeperDiveFall(
            ImageView keeper,
            EndlessState state,
            double deltaSeconds
    ) {
        double keeperCenterX = getCenterX(keeper);
        double keeperCenterY = getCenterY(keeper);
        double dx = state.keeperFallTargetX - keeperCenterX;
        double dy = state.keeperFallTargetY - keeperCenterY;
        double distance = Math.hypot(dx, dy);
        double step = KEEPER_CATCH_FALL_SPEED * deltaSeconds;

        if (distance <= step || distance <= 1.0) {
            setCenter(keeper, state.keeperFallTargetX, state.keeperFallTargetY);
            state.keeperFallingAfterCatch = false;
            return;
        }

        setCenter(
                keeper,
                keeperCenterX + dx / distance * step,
                keeperCenterY + dy / distance * step
        );
    }

    private void beginRoundResolution(EndlessState state, int roundResult) {
        state.ballMoving = false;
        state.dragging = false;
        state.keeperMoving = false;
        state.keeperFallingAfterCatch = false;
        state.roundResolving = true;
        state.roundResult = roundResult;
        state.roundResolveTimer = ROUND_RESULT_DELAY_SECONDS;
    }

    private void queueRoundResolutionAfterKeeperAnimation(EndlessState state, int roundResult) {
        state.ballMoving = false;
        state.dragging = false;
        state.pendingRoundResult = roundResult;

        if (state.keeperDiveDirection == 0 && !state.keeperJumping) {
            beginRoundResolution(state, roundResult);
            return;
        }

        state.awaitingKeeperAnimationFinish = true;
    }

    private void finishRoundResolution(
            EndlessState state,
            Text scoreText,
            Text livesText,
            Rectangle[] lifeIndicators,
            Rectangle gameOverOverlay,
            Text gameOverText,
            VBox saveScoreBox,
            Text finalScoreText,
            TextField nameInput,
            Runnable resetRound,
            ImageView ball
    ) {
        int result = state.roundResult;
        state.roundResolving = false;
        state.awaitingKeeperAnimationFinish = false;
        state.pendingRoundResult = ROUND_RESULT_NONE;
        state.roundResult = ROUND_RESULT_NONE;
        state.roundResolveTimer = 0;

        if (result == ROUND_RESULT_GOAL) {
            rememberScoredShot(state);
            state.score++;
            scoreText.setText("POINT: " + state.score);
            resetRound.run();
            return;
        }

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

    private boolean isBallInsidePointBox(StackPane root, ImageView ball) {
        double ballCenterX = getCenterX(ball);
        double ballCenterY = getCenterY(ball);
        return isPointInsidePointBox(root, ballCenterX, ballCenterY);
    }

    private boolean canBallPassStandingKeeper(StackPane root, EndlessState state) {
        return !state.keeperJumping && isPointInsidePointBox(root, state.targetX, state.targetY);
    }

    private boolean isShotSavedByKeeper(ImageView keeper, KeeperAnimator keeperAnimator, EndlessState state) {
        if (!state.keeperWillCatch) {
            return false;
        }

        // Tangkap hanya dianggap valid jika titik bola masuk sensor keeper.
        // Sensor ini berubah bentuk mengikuti animasi: berdiri vertikal, lompat horizontal.
        return isPointInsideKeeperSensorBoxAt(
                getCenterX(keeper),
                getCenterY(keeper),
                state.keeperDiveDirection,
                keeperAnimator.getCurrentLogicalFrameNumber(),
                state.targetX,
                state.targetY
        ) || isPointInsideKeeperSensorBoxAt(
                state.keeperTargetX,
                state.keeperTargetY,
                state.keeperDiveDirection,
                4,
                state.targetX,
                state.targetY
        );
    }

    private boolean isPointInsideKeeperBox(ImageView keeper, double pointX, double pointY) {
        return isPointInsideKeeperSensorBoxAt(
                getCenterX(keeper),
                getCenterY(keeper),
                0,
                1,
                pointX,
                pointY
        );
    }

    private boolean isPointInsidePointBox(StackPane root, double pointX, double pointY) {
        double width = root.getWidth();
        double height = root.getHeight();

        return pointX >= width * GOAL_LEFT_RATIO
                && pointX <= width * GOAL_RIGHT_RATIO
                && pointY >= height * GOAL_TOP_RATIO
                && pointY <= height * GOAL_BOTTOM_RATIO;
    }

    private void setKeeperToIdlePosition(StackPane root, ImageView keeper) {
        setCenter(keeper, root.getWidth() * 0.5, root.getHeight() * KEEPER_START_CENTER_Y_RATIO);
    }

    private void resetEndlessRound(
            StackPane root,
            ImageView ball,
            ImageView keeper,
            Line pullLine,
            Circle targetMarker,
            EndlessState state,
            KeeperAnimator keeperAnimator
    ) {
        if (root.getWidth() <= 0 || root.getHeight() <= 0) {
            return;
        }

        state.ballMoving = false;
        state.dragging = false;
        state.keeperMoving = false;
        state.keeperFallingAfterCatch = false;
        state.keeperJumping = false;
        state.keeperWillCatch = false;
        state.keeperDiveDirection = 0;
        state.roundResolving = false;
        state.awaitingKeeperAnimationFinish = false;
        state.pendingRoundResult = ROUND_RESULT_NONE;
        state.roundResult = ROUND_RESULT_NONE;
        state.roundResolveTimer = 0;
        state.velocityX = 0;
        state.velocityY = 0;
        state.shotSpeed = 0;
        state.anchorX = root.getWidth() * 0.5;
        state.anchorY = root.getHeight() * 0.78;
        state.targetX = state.anchorX;
        state.targetY = state.anchorY;

        ball.setVisible(true);
        setCenter(ball, state.anchorX, state.anchorY);
        setKeeperToIdlePosition(root, keeper);
        keeperAnimator.showIdle();
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
        double centerX = root.getWidth() * 0.5;
        double centerY = root.getHeight() * KEEPER_START_CENTER_Y_RATIO;
        double sideThreshold = Math.max(42, root.getWidth() * KEEPER_DIVE_TRIGGER_RATIO);
        double targetOffsetX = state.targetX - centerX;

        int ballDirection;
        if (targetOffsetX > sideThreshold) {
            ballDirection = 1;
        } else if (targetOffsetX < -sideThreshold) {
            ballDirection = -1;
        } else {
            ballDirection = 0;
        }
        state.keeperDiveDirection = ballDirection;

        boolean shotInsideGoal = isPointInsidePointBox(root, state.targetX, state.targetY);
        double readChance = Math.min(KEEPER_MAX_READ_CHANCE, 0.36 + state.score * KEEPER_READ_GROWTH_PER_POINT);
        if (state.scoredShotsLearned > 0) {
            double learnedDistance = Math.hypot(state.targetX - state.learnedTargetX, state.targetY - state.learnedTargetY);
            if (learnedDistance < root.getWidth() * 0.11) {
                readChance = Math.min(KEEPER_MAX_READ_CHANCE, readChance + 0.16);
            }
        }
        if (ballDirection == 0) {
            readChance = Math.min(KEEPER_MAX_READ_CHANCE, readChance + 0.10);
        }

        state.keeperWillCatch = shotInsideGoal && random.nextDouble() < readChance;

        double minCenterX = getKeeperMovementMinX(root);
        double maxCenterX = getKeeperMovementMaxX(root);
        double minCenterY = getKeeperMovementMinY(root);
        double maxCenterY = getKeeperMovementMaxY(root);

        if (ballDirection == 0) {
            state.keeperTargetX = centerX;
            if (state.keeperWillCatch) {
                // Tendangan lurus ke atas tetap bisa dibaca keeper.
                // Center Y diarahkan ke titik bola, bukan dikunci di posisi berdiri.
                state.keeperTargetY = clamp(
                        state.targetY - getKeeperSensorOffsetY(0),
                        minCenterY,
                        maxCenterY
                );
            } else {
                state.keeperTargetY = centerY;
            }
            state.keeperJumping = Math.hypot(state.keeperTargetX - centerX, state.keeperTargetY - centerY) > 18;
            state.keeperDiveDirection = resolveKeeperAnimationDirection(centerX, state.keeperTargetX, ballDirection, sideThreshold);
            return;
        }

        int direction = ballDirection;
        double catchCenterY = clamp(
                state.targetY - getKeeperSensorOffsetY(direction),
                minCenterY,
                maxCenterY
        );

        if (state.keeperWillCatch) {
            // Untuk tangkapan, center keeper dihitung dari sensor horizontal.
            // Y tidak lagi dipaksa jatuh ke bawah; keeper bisa naik ke pojok atas gawang.
            state.keeperTargetX = clamp(
                    state.targetX - getKeeperSensorOffsetX(direction),
                    minCenterX,
                    maxCenterX
            );
            state.keeperTargetY = catchCenterY;

            // Jika tendangan sangat ke ujung dan sensor belum masuk titik bola, geser lagi sampai batas gerak.
            if (!isPointInsideKeeperSensorBoxAt(state.keeperTargetX, state.keeperTargetY, direction, 4, state.targetX, state.targetY)) {
                state.keeperTargetX = clamp(
                        state.targetX - getKeeperSensorOffsetX(direction) - direction * KEEPER_SIZE * 0.18,
                        minCenterX,
                        maxCenterX
                );
            }
        } else {
            // Kalau tidak menangkap, keeper sengaja dibuat meleset.
            // Arah gambar animasi nanti mengikuti arah target keeper, bukan arah bola.
            double verticalMissOffset = state.targetY < centerY ? KEEPER_SIZE * 0.22 : -KEEPER_SIZE * 0.10;
            state.keeperTargetX = clamp(
                    state.targetX - getKeeperSensorOffsetX(direction) - direction * KEEPER_SIZE * 0.72,
                    minCenterX,
                    maxCenterX
            );
            state.keeperTargetY = clamp(
                    catchCenterY + verticalMissOffset,
                    minCenterY,
                    maxCenterY
            );

            if (isPointInsideKeeperSensorBoxAt(state.keeperTargetX, state.keeperTargetY, direction, 4, state.targetX, state.targetY)) {
                state.keeperTargetX = clamp(
                        state.keeperTargetX - direction * KEEPER_SIZE * 0.55,
                        minCenterX,
                        maxCenterX
                );
            }
        }

        state.keeperJumping = Math.hypot(state.keeperTargetX - centerX, state.keeperTargetY - centerY) > 18;
        state.keeperDiveDirection = resolveKeeperAnimationDirection(centerX, state.keeperTargetX, ballDirection, sideThreshold);
    }

    private int resolveKeeperAnimationDirection(double startX, double targetX, int fallbackDirection, double sideThreshold) {
        double movementOffsetX = targetX - startX;
        double movementThreshold = Math.max(10, sideThreshold * 0.20);
        if (movementOffsetX > movementThreshold) {
            return 1;
        }
        if (movementOffsetX < -movementThreshold) {
            return -1;
        }
        return fallbackDirection;
    }

    private double getKeeperMovementMinX(StackPane root) {
        return root.getWidth() * GOAL_LEFT_RATIO + root.getWidth() * KEEPER_EDGE_PADDING_RATIO;
    }

    private double getKeeperMovementMaxX(StackPane root) {
        return root.getWidth() * GOAL_RIGHT_RATIO - root.getWidth() * KEEPER_EDGE_PADDING_RATIO;
    }

    private double getKeeperMovementMinY(StackPane root) {
        return root.getHeight() * GOAL_TOP_RATIO + root.getHeight() * KEEPER_TOP_REACH_PADDING_RATIO;
    }

    private double getKeeperMovementMaxY(StackPane root) {
        return root.getHeight() * GOAL_BOTTOM_RATIO - root.getHeight() * KEEPER_BOTTOM_REACH_PADDING_RATIO;
    }

    private double getKeeperSensorWidth(int direction, int logicalFrameNumber) {
        if (direction == 0 || logicalFrameNumber <= 1) {
            return KEEPER_SIZE * KEEPER_STAND_SENSOR_WIDTH_RATIO;
        }
        return KEEPER_SIZE * KEEPER_DIVE_SENSOR_WIDTH_RATIO;
    }

    private double getKeeperSensorHeight(int direction, int logicalFrameNumber) {
        if (direction == 0 || logicalFrameNumber <= 1) {
            return KEEPER_SIZE * KEEPER_STAND_SENSOR_HEIGHT_RATIO;
        }
        return KEEPER_SIZE * KEEPER_DIVE_SENSOR_HEIGHT_RATIO;
    }

    private double getKeeperSensorOffsetX(int direction) {
        return direction * KEEPER_SIZE * KEEPER_DIVE_SENSOR_X_OFFSET_RATIO;
    }

    private double getKeeperSensorOffsetY(int direction) {
        if (direction == 0) {
            return KEEPER_SIZE * KEEPER_STAND_SENSOR_Y_OFFSET_RATIO;
        }
        return KEEPER_SIZE * KEEPER_DIVE_SENSOR_Y_OFFSET_RATIO;
    }

    private KeeperSensorBox getKeeperSensorBoxAt(double keeperCenterX, double keeperCenterY, int direction, int logicalFrameNumber) {
        double sensorWidth = getKeeperSensorWidth(direction, logicalFrameNumber);
        double sensorHeight = getKeeperSensorHeight(direction, logicalFrameNumber);
        double sensorCenterX = keeperCenterX + getKeeperSensorOffsetX(direction);
        double sensorCenterY = keeperCenterY + getKeeperSensorOffsetY(direction);

        return new KeeperSensorBox(
                sensorCenterX - sensorWidth / 2,
                sensorCenterY - sensorHeight / 2,
                sensorWidth,
                sensorHeight
        );
    }

    private boolean isPointInsideKeeperSensorBoxAt(
            double keeperCenterX,
            double keeperCenterY,
            int direction,
            int logicalFrameNumber,
            double pointX,
            double pointY
    ) {
        KeeperSensorBox sensorBox = getKeeperSensorBoxAt(keeperCenterX, keeperCenterY, direction, logicalFrameNumber);
        return pointX >= sensorBox.x
                && pointX <= sensorBox.x + sensorBox.width
                && pointY >= sensorBox.y
                && pointY <= sensorBox.y + sensorBox.height;
    }

    private void updateKeeperSensorOverlay(
            Rectangle keeperBoxOverlay,
            ImageView keeper,
            KeeperAnimator keeperAnimator,
            EndlessState state
    ) {
        if (keeperBoxOverlay == null) {
            return;
        }

        int direction = state.keeperDiveDirection;
        int logicalFrameNumber = keeperAnimator.getCurrentLogicalFrameNumber();
        KeeperSensorBox sensorBox = getKeeperSensorBoxAt(
                getCenterX(keeper),
                getCenterY(keeper),
                direction,
                logicalFrameNumber
        );

        keeperBoxOverlay.setLayoutX(sensorBox.x);
        keeperBoxOverlay.setLayoutY(sensorBox.y);
        keeperBoxOverlay.setWidth(sensorBox.width);
        keeperBoxOverlay.setHeight(sensorBox.height);
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

        // Fix tendangan lurus ke atas.
        // Sebelumnya bola bisa melewati bagian atas layar karena target Y terlalu jauh.
        // Kalau arah bola memang ke gawang dan targetnya melewati mistar atas, hentikan titik target di area atas gawang.
        if (directionY < -0.35) {
            double startY = getBallStartY(root);
            double targetY = startY + directionY * distance;
            double goalTopY = root.getHeight() * GOAL_TOP_RATIO;
            if (targetY < goalTopY) {
                distance = Math.abs((goalTopY - startY) / directionY);
            }
        }

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

    private class KeeperAnimator {
        private final ImageView imageView;
        private final Image idleImage;
        private final Image standingCatchImage;
        private List<Image> frames;
        private List<Integer> logicalFrameNumbers;
        private int frameIndex;
        private double frameTimer;
        private boolean playing;
        private boolean sequenceFinished;
        private double finalFrameHoldTimer;
        private boolean catchBall;
        private boolean catchBallHideEventPending;
        private boolean catchBallHideEventFired;
        private boolean diveFallEventPending;
        private boolean diveFallEventFired;

        private KeeperAnimator(ImageView imageView) {
            this.imageView = imageView;
            this.idleImage = loadKeeperImage(KEEPER_IDLE_IMAGE_PATH);
            this.standingCatchImage = loadKeeperImage(KEEPER_STANDING_CATCH_IMAGE_PATH);
            this.frames = new ArrayList<>();
            this.logicalFrameNumbers = new ArrayList<>();
            this.frames.add(idleImage);
            this.logicalFrameNumbers.add(1);
        }

        private void showIdle() {
            playing = false;
            sequenceFinished = false;
            catchBall = false;
            catchBallHideEventPending = false;
            catchBallHideEventFired = false;
            diveFallEventPending = false;
            diveFallEventFired = false;
            finalFrameHoldTimer = 0;
            frameTimer = 0;
            frameIndex = 0;
            logicalFrameNumbers = new ArrayList<>();
            logicalFrameNumbers.add(1);
            frames = new ArrayList<>();
            frames.add(idleImage);
            imageView.setImage(idleImage);
            applyFrameVisualOffset();
        }

        private void startDive(int direction, boolean catchBall) {
            this.catchBall = catchBall;
            this.catchBallHideEventPending = false;
            this.catchBallHideEventFired = false;
            this.diveFallEventPending = false;
            this.diveFallEventFired = false;
            this.sequenceFinished = false;
            this.finalFrameHoldTimer = 0;
            DiveFrameSequence sequence = buildDiveFrames(direction, catchBall);
            frames = sequence.images;
            logicalFrameNumbers = sequence.logicalFrameNumbers;
            frameIndex = 0;
            frameTimer = 0;
            playing = !frames.isEmpty();
            imageView.setImage(frames.get(0));
            applyFrameVisualOffset();
        }

        private void update(double deltaSeconds) {
            if (frames.isEmpty()) {
                return;
            }

            if (!playing) {
                return;
            }

            frameTimer += deltaSeconds;
            while (playing) {
                double currentFrameDuration = getCurrentFrameDuration();
                if (frameTimer < currentFrameDuration) {
                    break;
                }

                frameTimer -= currentFrameDuration;
                if (frameIndex < frames.size() - 1) {
                    frameIndex++;
                    imageView.setImage(frames.get(frameIndex));
                    applyFrameVisualOffset();
                    int logicalFrame = getCurrentLogicalFrameNumber();
                    if (logicalFrameNumbers.size() > 2 && !diveFallEventFired && logicalFrame >= 4) {
                        diveFallEventPending = true;
                        diveFallEventFired = true;
                    }
                    if (catchBall && !catchBallHideEventFired && logicalFrame >= getCatchHideFrameNumber()) {
                        catchBallHideEventPending = true;
                        catchBallHideEventFired = true;
                    }
                    if (frameIndex == frames.size() - 1) {
                        finalFrameHoldTimer = 0;
                    }
                } else {
                    playing = false;
                    sequenceFinished = true;
                }
            }
        }

        private void applyFrameVisualOffset() {
            int logicalFrame = getCurrentLogicalFrameNumber();

            if (logicalFrame == 5) {
                imageView.setTranslateY(KEEPER_FRAME_5_DOWN_OFFSET);
            } else {
                imageView.setTranslateY(0);
            }
        }

        private double getCurrentFrameDuration() {
            int logicalFrame = getCurrentLogicalFrameNumber();
            if (logicalFrame >= 5 && logicalFrame < 7) {
                return KEEPER_LANDING_FRAME_HOLD_SECONDS;
            }
            if (logicalFrame >= 7) {
                return KEEPER_FINAL_HOLD_SECONDS;
            }
            return KEEPER_FRAME_SECONDS;
        }

        private boolean consumeDiveFallEvent() {
            boolean result = diveFallEventPending;
            diveFallEventPending = false;
            return result;
        }

        private int getCatchHideFrameNumber() {
            if (logicalFrameNumbers.size() <= 2) {
                return 2;
            }
            return 4;
        }

        private boolean consumeCatchBallHideEvent() {
            boolean result = catchBallHideEventPending;
            catchBallHideEventPending = false;
            return result;
        }

        private boolean isSequenceFinished() {
            return sequenceFinished || frames.size() <= 1;
        }

        private int getCurrentLogicalFrameNumber() {
            if (logicalFrameNumbers.isEmpty()) {
                return 1;
            }
            return logicalFrameNumbers.get(Math.max(0, Math.min(frameIndex, logicalFrameNumbers.size() - 1)));
        }

        private DiveFrameSequence buildDiveFrames(int direction, boolean catchBall) {
            List<Image> result = new ArrayList<>();
            List<Integer> frameNumbers = new ArrayList<>();
            result.add(idleImage);
            frameNumbers.add(1);

            if (direction == 0) {
                if (catchBall) {
                    result.add(standingCatchImage);
                    frameNumbers.add(2);
                }
                return new DiveFrameSequence(result, frameNumbers);
            }

            String normalFolder = direction > 0 ? KEEPER_RIGHT_FOLDER : KEEPER_LEFT_FOLDER;
            String catchFolder = direction > 0 ? KEEPER_RIGHT_CATCH_FOLDER : KEEPER_LEFT_CATCH_FOLDER;

            for (int frameNumber = 2; frameNumber <= 7; frameNumber++) {
                boolean useCatchFolder = catchBall && frameNumber >= 4;
                String primaryPath = framePath(useCatchFolder ? catchFolder : normalFolder, frameNumber);
                String fallbackPath = framePath(useCatchFolder ? normalFolder : catchFolder, frameNumber);
                result.add(loadKeeperImage(primaryPath, fallbackPath, KEEPER_IDLE_IMAGE_PATH));
                frameNumbers.add(frameNumber);
            }

            return new DiveFrameSequence(result, frameNumbers);
        }

        private String framePath(String folder, int frameNumber) {
            return folder + "/" + frameNumber + ".png";
        }

        private Image loadKeeperImage(String primaryPath) {
            return loadKeeperImage(primaryPath, null, KEEPER_IDLE_IMAGE_PATH);
        }

        private Image loadKeeperImage(String primaryPath, String secondaryPath, String fallbackPath) {
            String[] candidates = secondaryPath == null
                    ? new String[] { primaryPath, fallbackPath }
                    : new String[] { primaryPath, secondaryPath, fallbackPath };

            for (String candidate : candidates) {
                Path path = resolveResource(candidate);
                if (Files.exists(path)) {
                    return new Image(path.toUri().toString(), false);
                }
            }

            return createFallbackImage();
        }
    }

    private static class KeeperSensorBox {
        private final double x;
        private final double y;
        private final double width;
        private final double height;

        private KeeperSensorBox(double x, double y, double width, double height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }

    private static class DiveFrameSequence {
        private final List<Image> images;
        private final List<Integer> logicalFrameNumbers;

        private DiveFrameSequence(List<Image> images, List<Integer> logicalFrameNumbers) {
            this.images = images;
            this.logicalFrameNumbers = logicalFrameNumbers;
        }
    }

    private static class EndlessState {
        boolean dragging;
        boolean ballMoving;
        boolean keeperMoving;
        boolean keeperFallingAfterCatch;
        boolean keeperJumping;
        double anchorX;
        double anchorY;
        double velocityX;
        double velocityY;
        double targetX;
        double targetY;
        double shotSpeed;
        double keeperTargetX;
        double keeperTargetY;
        double keeperFallTargetX;
        double keeperFallTargetY;
        double learnedTargetX;
        double learnedTargetY;
        int scoredShotsLearned;
        int score;
        int lives;
        int keeperDiveDirection;
        int roundResult;
        double roundResolveTimer;
        boolean keeperWillCatch;
        boolean roundResolving;
        boolean awaitingKeeperAnimationFinish;
        boolean gameOver;
        int pendingRoundResult;
    }

    private static class TournamentState extends EndlessState {
        int roundIndex;
        int roundGoals;
        int shotsTaken;
        int totalGoals;
        int teamCount;
        String playerTeamName;
        String[] opponents;
        boolean setupDone;
        boolean roundFinished;
        boolean eliminated;
        boolean champion;
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
