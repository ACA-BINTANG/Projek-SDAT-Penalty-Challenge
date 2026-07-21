package modes.tutorial;

import core.GameEngine;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
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
/**
 * MODE TUTORIAL
 *
 * Alur dasar:
 * 1. Pemain belajar menendang terlebih dahulu.
 * 2. Setelah mencetak goal, pemain belajar menjadi keeper.
 * 3. Petunjuk berubah sesuai tahap tutorial.
 * 4. Tutorial selesai setelah semua tahap berhasil.
 */
public class TutorialMode extends GameEngine {
    @Override
    public void showTutorialMode(Stage stage) {
        // 1. MEMBUAT TAMPILAN MODE

        StackPane root = new StackPane();
        Pane playLayer = new Pane();
        playLayer.setPickOnBounds(true);

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
        targetMarker.setStroke(Color.WHITE);
        targetMarker.setStrokeWidth(2);
        targetMarker.setMouseTransparent(true);
        targetMarker.setVisible(false);

        Rectangle keeperBoxOverlay = new Rectangle();
        keeperBoxOverlay.setFill(Color.rgb(0, 255, 80, 0.18));
        keeperBoxOverlay.setStroke(Color.rgb(0, 255, 80, 0.82));
        keeperBoxOverlay.setStrokeWidth(3);
        keeperBoxOverlay.setMouseTransparent(true);
        keeperBoxOverlay.setVisible(SHOW_DEBUG_BOXES);

        Rectangle topHudBackground = createTopHudBackground(root, 118);

        Text titleText = new Text("TUTORIAL");
        titleText.setFill(Color.WHITE);
        titleText.setFont(loadFont(MENU_FONT_PATH, 30, Font.font("Arial", FontWeight.EXTRA_BOLD, 30)));
        titleText.layoutXProperty().bind(Bindings.createDoubleBinding(
                () -> (root.getWidth() - titleText.getLayoutBounds().getWidth()) / 2,
                root.widthProperty(),
                titleText.layoutBoundsProperty()
        ));
        titleText.setLayoutY(52);

        Text hintText = new Text("LANGKAH 1: Klik kiri dan tahan bola.");
        hintText.setFill(Color.rgb(255, 255, 255, 0.92));
        hintText.setFont(loadFont(MENU_FONT_PATH, 18, Font.font("Arial", FontWeight.BOLD, 18)));
        hintText.layoutXProperty().bind(Bindings.createDoubleBinding(
                () -> (root.getWidth() - hintText.getLayoutBounds().getWidth()) / 2,
                root.widthProperty(),
                hintText.layoutBoundsProperty()
        ));
        hintText.setLayoutY(88);

        Text shortcutText = new Text("PRESS ESC TO EXIT");
        shortcutText.setFill(Color.rgb(255, 255, 255, 0.84));
        shortcutText.setFont(loadFont(MENU_FONT_PATH, 16, Font.font("Arial", FontWeight.BOLD, 16)));
        shortcutText.layoutXProperty().bind(Bindings.createDoubleBinding(
                () -> root.getWidth() - shortcutText.getLayoutBounds().getWidth() - 32,
                root.widthProperty(),
                shortcutText.layoutBoundsProperty()
        ));
        shortcutText.setLayoutY(40);

        Text goalText = createGoalText(root);

        Rectangle tutorialCompleteOverlay = new Rectangle();
        tutorialCompleteOverlay.widthProperty().bind(root.widthProperty());
        tutorialCompleteOverlay.heightProperty().bind(root.heightProperty());
        tutorialCompleteOverlay.setFill(Color.rgb(0, 0, 0, 0.68));
        tutorialCompleteOverlay.setVisible(false);

        Text tutorialCompleteTitle = new Text("SELAMAT!");
        tutorialCompleteTitle.setFill(Color.WHITE);
        tutorialCompleteTitle.setFont(loadFont(MENU_FONT_PATH, 38, Font.font("Arial", FontWeight.EXTRA_BOLD, 38)));

        Text tutorialCompleteMessage = new Text("Anda telah menyelesaikan tutorial.");
        tutorialCompleteMessage.setFill(Color.rgb(255, 255, 255, 0.92));
        tutorialCompleteMessage.setFont(loadFont(MENU_FONT_PATH, 21, Font.font("Arial", FontWeight.BOLD, 21)));

        Button tutorialNextButton = new Button("SELANJUTNYA");
        tutorialNextButton.setFont(loadFont(MENU_FONT_PATH, 18, Font.font("Arial", FontWeight.EXTRA_BOLD, 18)));
        tutorialNextButton.setCursor(Cursor.HAND);
        tutorialNextButton.setStyle(
                "-fx-background-color: #f3c742;"
                        + "-fx-text-fill: #111111;"
                        + "-fx-background-radius: 8;"
                        + "-fx-padding: 12 28 12 28;"
        );
        tutorialNextButton.setOnAction(event -> showMenu(stage));

        VBox tutorialCompleteBox = new VBox(18, tutorialCompleteTitle, tutorialCompleteMessage, tutorialNextButton);
        tutorialCompleteBox.setAlignment(Pos.CENTER);
        tutorialCompleteBox.setPadding(new Insets(30, 42, 30, 42));
        tutorialCompleteBox.setBackground(new Background(new BackgroundFill(
                Color.rgb(0, 0, 0, 0.84),
                new CornerRadii(12),
                Insets.EMPTY
        )));
        tutorialCompleteBox.setMaxWidth(620);
        tutorialCompleteBox.setVisible(false);

        StackPane.setAlignment(tutorialCompleteBox, Pos.CENTER);

        playLayer.getChildren().addAll(
                keeperBoxOverlay,
                pullLine,
                targetMarker,
                keeper,
                ball,
                goalText,
                topHudBackground,
                titleText,
                hintText,
                shortcutText
        );
        root.getChildren().addAll(background, playLayer, tutorialCompleteOverlay, tutorialCompleteBox);

        Scene scene = new Scene(root, 1280, 720);
        setSceneSmooth(stage, scene, this::startGameplayDefaultAudio);
        scene.setCursor(Cursor.DEFAULT);

        // 2. MENYIAPKAN DATA / STATE PERMAINAN

        TutorialState state = new TutorialState();
        Runnable resetRound = () -> {
            resetEndlessRound(
                    root,
                    ball,
                    keeper,
                    pullLine,
                    targetMarker,
                    state,
                    keeperAnimator
            );

            if (state.phase == TutorialPhase.KEEPER_AIM) {
                prepareTutorialAutoShot(root, ball, targetMarker, state);
                ball.setCursor(Cursor.DEFAULT);
            } else if (state.phase == TutorialPhase.KICKER) {
                ball.setCursor(Cursor.HAND);
            }
        };

        Runnable showTutorialCompletePopup = () -> {
            state.tutorialComplete = true;
            state.gameOver = true;
            state.phase = TutorialPhase.COMPLETE;
            state.ballMoving = false;
            state.dragging = false;
            ball.setCursor(Cursor.DEFAULT);
            titleText.setText("TUTORIAL SELESAI");
            hintText.setText("Latihan penendang dan keeper sudah selesai.");
            tutorialCompleteOverlay.setVisible(true);
            tutorialCompleteBox.setVisible(true);
            Platform.runLater(tutorialNextButton::requestFocus);
        };

        // 3. INPUT KEYBOARD

        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                showMenu(stage);
            }
        });

        root.widthProperty().addListener((observable, oldValue, newValue) -> {
            if (!state.tutorialComplete && !state.ballMoving && !state.roundResolving) {
                resetRound.run();
            }
        });
        root.heightProperty().addListener((observable, oldValue, newValue) -> {
            if (!state.tutorialComplete && !state.ballMoving && !state.roundResolving) {
                resetRound.run();
            }
        });
        Platform.runLater(resetRound);

        // Tutorial benar-benar hanya menerima klik kiri.
        // Status drag dicatat sendiri agar klik kanan tidak pernah dapat memindahkan bola,
        // termasuk saat tombol kanan ditahan sambil mouse digerakkan.
        final boolean[] tutorialPrimaryButtonHeld = {false};

        root.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                tutorialPrimaryButtonHeld[0] = true;
                return;
            }

            // Semua tombol selain klik kiri diblokir. Jika klik kanan ditekan ketika
            // bola sedang ditarik, batalkan tarikan dan kembalikan bola ke titik awal.
            tutorialPrimaryButtonHeld[0] = false;
            if (state.dragging && state.phase == TutorialPhase.KICKER && !state.ballMoving) {
                state.dragging = false;
                pullLine.setVisible(false);
                targetMarker.setVisible(false);
                setCenter(ball, state.anchorX, state.anchorY);
                hintText.setText("LANGKAH 1: Klik kiri dan tahan bola.");
            }
            event.consume();
        });

        root.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {
            // Drag sah hanya bila benar-benar dimulai dan masih ditahan dengan klik kiri.
            if (!tutorialPrimaryButtonHeld[0]
                    || !event.isPrimaryButtonDown()
                    || event.isSecondaryButtonDown()) {
                event.consume();
            }
        });

        root.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                tutorialPrimaryButtonHeld[0] = false;
            } else {
                event.consume();
            }
        });

        root.addEventFilter(MouseEvent.DRAG_DETECTED, event -> {
            if (!tutorialPrimaryButtonHeld[0]
                    || !event.isPrimaryButtonDown()
                    || event.isSecondaryButtonDown()) {
                event.consume();
            }
        });

        root.setOnContextMenuRequested(event -> event.consume());
        ball.setOnContextMenuRequested(event -> event.consume());

        // 4. INPUT MOUSE UNTUK TENDANGAN

        ball.setOnMousePressed(event -> {
            // Tutorial menendang hanya menerima klik kiri. Klik kanan diabaikan agar
            // tidak memulai drag yang bisa membuat posisi bola meloncat/glitch.
            if (event.getButton() != MouseButton.PRIMARY) {
                event.consume();
                return;
            }

            if (state.tutorialComplete || state.phase != TutorialPhase.KICKER || state.ballMoving || state.roundResolving) {
                return;
            }

            tutorialPrimaryButtonHeld[0] = true;
            state.dragging = true;
            state.anchorX = getCenterX(ball);
            state.anchorY = getCenterY(ball);
            pullLine.setStartX(state.anchorX);
            pullLine.setStartY(state.anchorY);
            pullLine.setEndX(state.anchorX);
            pullLine.setEndY(state.anchorY);
            pullLine.setVisible(true);
            targetMarker.setVisible(false);
            hintText.setText("LANGKAH 2: Tetap tahan klik kiri lalu tarik bola untuk menentukan arah tendangan.");
        });

        ball.setOnMouseDragged(event -> {
            // Bola hanya boleh bergerak jika drag benar-benar dimulai dengan klik kiri.
            // Klik kanan, termasuk klik kanan yang ditahan, tidak pernah lolos kondisi ini.
            if (!tutorialPrimaryButtonHeld[0]
                    || !event.isPrimaryButtonDown()
                    || event.isSecondaryButtonDown()) {
                event.consume();
                return;
            }

            if (state.tutorialComplete || state.phase != TutorialPhase.KICKER || !state.dragging || state.ballMoving || state.roundResolving) {
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

            if (distance > MAX_PULL_DISTANCE * 0.28) {
                hintText.setText("LANGKAH 3: Lepaskan klik kiri untuk menendang bola.");
            }
        });

        ball.setOnMouseReleased(event -> {
            // Hanya pelepasan klik kiri yang mengeksekusi tendangan.
            if (event.getButton() != MouseButton.PRIMARY) {
                event.consume();
                return;
            }

            if (state.tutorialComplete || state.phase != TutorialPhase.KICKER || !state.dragging || state.ballMoving || state.roundResolving) {
                return;
            }

            tutorialPrimaryButtonHeld[0] = false;
            state.dragging = false;
            pullLine.setVisible(false);
            targetMarker.setVisible(false);
            applyKickForce(root, ball, state);

            if (state.shotSpeed < MIN_BALL_SPEED + 20) {
                setCenter(ball, state.anchorX, state.anchorY);
                hintText.setText("Tarikan terlalu lemah. Klik kiri bola lalu tarik lebih jauh.");
                return;
            }

            hintText.setText("LANGKAH 4: Perhatikan tendanganmu dan coba cetak GOAL!");
            chooseKeeperTarget(root, state);
            keeperAnimator.startDive(state.keeperDiveDirection, state.keeperWillCatch, state.keeperVerticalJump);
            state.ballMoving = true;
            startKeeperJumpMovement(root, keeper, state, KEEPER_MOVE_SPEED);
        });

        playLayer.setOnMouseClicked(event -> {
            // Arah keeper di Tutorial juga hanya boleh dipilih dengan klik kiri.
            if (event.getButton() != MouseButton.PRIMARY) {
                event.consume();
                return;
            }

            if (state.tutorialComplete
                    || state.phase != TutorialPhase.KEEPER_AIM
                    || state.ballMoving
                    || state.roundResolving) {
                return;
            }

            double selectedX = event.getX();
            double selectedY = event.getY();
            if (!isPointInsidePointBox(root, selectedX, selectedY)) {
                hintText.setText("LANGKAH 6: Klik lingkaran kuning di area gawang untuk mengarahkan keeper.");
                return;
            }

            prepareTutorialKeeperTarget(root, state, selectedX, selectedY);
            targetMarker.setVisible(false);
            keeperAnimator.startDive(state.keeperDiveDirection, state.keeperWillCatch, state.keeperVerticalJump);
            state.phase = TutorialPhase.KEEPER_EXECUTING;
            state.ballMoving = true;
            startKeeperJumpMovement(root, keeper, state, KEEPER_MOVE_SPEED);
            hintText.setText("LANGKAH 7: Keeper bergerak. Coba tahan tendangan lawan!");
            event.consume();
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
                updateTutorial(
                        root,
                        ball,
                        keeper,
                        keeperAnimator,
                        keeperBoxOverlay,
                        goalText,
                        titleText,
                        hintText,
                        shortcutText,
                        state,
                        resetRound,
                        showTutorialCompletePopup,
                        deltaSeconds
                );
            }
        };
        gameLoop.start();
    }
}
