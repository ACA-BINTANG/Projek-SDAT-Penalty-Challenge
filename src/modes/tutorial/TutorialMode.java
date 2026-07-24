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
        // Hindari satu frame awal di pojok kiri atas sebelum layout selesai.
        keeper.setVisible(false);

        ImageView ball = createImageView(BALL_IMAGE_PATH);
        ball.setFitWidth(BALL_SIZE);
        ball.setFitHeight(BALL_SIZE);
        ball.setPreserveRatio(true);
        ball.setCursor(Cursor.HAND);
        ball.setVisible(false);

        Line pullLine = new Line();
        pullLine.setStroke(Color.rgb(255, 255, 255, 0.75));
        pullLine.setStrokeWidth(4);
        pullLine.setVisible(false);

        Circle targetMarker = new Circle(12);
        targetMarker.setFill(Color.rgb(255, 225, 35, 0.82));
        targetMarker.setStroke(Color.WHITE);
        targetMarker.setStrokeWidth(3);
        targetMarker.setMouseTransparent(true);
        targetMarker.setVisible(false);

        Circle ballGuideMarker = new Circle(BALL_SIZE * 0.62);
        ballGuideMarker.setFill(Color.rgb(255, 220, 35, 0.10));
        ballGuideMarker.setStroke(Color.rgb(255, 220, 35, 0.98));
        ballGuideMarker.setStrokeWidth(5);
        ballGuideMarker.getStrokeDashArray().addAll(12.0, 8.0);
        ballGuideMarker.setMouseTransparent(true);
        ballGuideMarker.setVisible(false);

        Circle goalGuideMarker = new Circle(34);
        goalGuideMarker.setFill(Color.rgb(255, 220, 35, 0.24));
        goalGuideMarker.setStroke(Color.rgb(255, 225, 40, 1.0));
        goalGuideMarker.setStrokeWidth(5);
        goalGuideMarker.getStrokeDashArray().addAll(10.0, 6.0);
        goalGuideMarker.setMouseTransparent(true);
        goalGuideMarker.setVisible(false);

        Text ballGuideLabel = new Text("KICK");
        ballGuideLabel.setFill(Color.BLACK);
        ballGuideLabel.setStroke(Color.TRANSPARENT);
        ballGuideLabel.setStrokeWidth(0);
        ballGuideLabel.setFont(loadFont(MENU_FONT_PATH, 15, Font.font("Arial", FontWeight.EXTRA_BOLD, 15)));
        ballGuideLabel.setMouseTransparent(true);
        ballGuideLabel.setVisible(false);

        Text goalGuideLabel = new Text("GOAL TARGET");
        goalGuideLabel.setFill(Color.BLACK);
        goalGuideLabel.setStroke(Color.TRANSPARENT);
        goalGuideLabel.setStrokeWidth(0);
        goalGuideLabel.setFont(loadFont(MENU_FONT_PATH, 15, Font.font("Arial", FontWeight.EXTRA_BOLD, 15)));
        goalGuideLabel.setMouseTransparent(true);
        goalGuideLabel.setVisible(false);

        Text keeperGuideLabel = new Text("CATCH HERE");
        keeperGuideLabel.setFill(Color.BLACK);
        keeperGuideLabel.setStroke(Color.TRANSPARENT);
        keeperGuideLabel.setStrokeWidth(0);
        keeperGuideLabel.setFont(loadFont(MENU_FONT_PATH, 15, Font.font("Arial", FontWeight.EXTRA_BOLD, 15)));
        keeperGuideLabel.setMouseTransparent(true);
        keeperGuideLabel.setVisible(false);

        Rectangle keeperBoxOverlay = new Rectangle();
        keeperBoxOverlay.setFill(Color.rgb(0, 255, 80, 0.18));
        keeperBoxOverlay.setStroke(Color.rgb(0, 255, 80, 0.82));
        keeperBoxOverlay.setStrokeWidth(3);
        keeperBoxOverlay.setMouseTransparent(true);
        keeperBoxOverlay.setVisible(SHOW_DEBUG_BOXES);

        Rectangle topHudBackground = createTopHudBackground(root, 138);

        Text tutorialTargetText = new Text("TARGET: SCORE A GOAL");
        tutorialTargetText.setFill(Color.WHITE);
        tutorialTargetText.setFont(loadFont(MENU_FONT_PATH, 17, Font.font("Arial", FontWeight.BOLD, 17)));
        tutorialTargetText.setLayoutX(32);
        tutorialTargetText.setLayoutY(42);

        Text tutorialLivesText = new Text("LIVES: UNLIMITED");
        tutorialLivesText.setFill(Color.WHITE);
        tutorialLivesText.setFont(loadFont(MENU_FONT_PATH, 17, Font.font("Arial", FontWeight.BOLD, 17)));
        tutorialLivesText.setLayoutX(32);
        tutorialLivesText.setLayoutY(70);

        Text titleText = new Text("TUTORIAL");
        titleText.setFill(Color.WHITE);
        titleText.setFont(loadFont(MENU_FONT_PATH, 30, Font.font("Arial", FontWeight.EXTRA_BOLD, 30)));
        titleText.layoutXProperty().bind(Bindings.createDoubleBinding(
                () -> (root.getWidth() - titleText.getLayoutBounds().getWidth()) / 2,
                root.widthProperty(),
                titleText.layoutBoundsProperty()
        ));
        titleText.setLayoutY(52);

        Text hintText = new Text("STEP 1: Left-click and hold the ball marked in yellow.");
        hintText.setFill(Color.rgb(255, 255, 255, 0.92));
        hintText.setFont(loadFont(MENU_FONT_PATH, 18, Font.font("Arial", FontWeight.BOLD, 18)));
        // Petunjuk Tutorial diletakkan di bawah informasi TARGET dan NYAWA.
        hintText.setLayoutX(32);
        hintText.setLayoutY(108);

        Button menuButton = createGameplayMenuButton(stage);
        StackPane.setAlignment(menuButton, Pos.TOP_RIGHT);
        StackPane.setMargin(menuButton, new Insets(18, 28, 0, 0));

        Text goalText = createGoalText(root);

        Rectangle tutorialCompleteOverlay = new Rectangle();
        tutorialCompleteOverlay.widthProperty().bind(root.widthProperty());
        tutorialCompleteOverlay.heightProperty().bind(root.heightProperty());
        tutorialCompleteOverlay.setFill(Color.rgb(0, 0, 0, 0.68));
        tutorialCompleteOverlay.setVisible(false);

        Text tutorialCompleteTitle = new Text("CONGRATULATIONS!");
        tutorialCompleteTitle.setFill(Color.WHITE);
        tutorialCompleteTitle.setFont(loadFont(MENU_FONT_PATH, 38, Font.font("Arial", FontWeight.EXTRA_BOLD, 38)));

        Text tutorialCompleteMessage = new Text("You have completed the tutorial.");
        tutorialCompleteMessage.setFill(Color.rgb(255, 255, 255, 0.92));
        tutorialCompleteMessage.setFont(loadFont(MENU_FONT_PATH, 21, Font.font("Arial", FontWeight.BOLD, 21)));

        Button tutorialNextButton = new Button("CONTINUE");
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
                goalGuideMarker,
                keeper,
                ball,
                ballGuideMarker,
                targetMarker,
                ballGuideLabel,
                goalGuideLabel,
                keeperGuideLabel,
                goalText,
                topHudBackground,
                tutorialTargetText,
                tutorialLivesText,
                titleText,
                hintText
        );
        root.getChildren().addAll(background, playLayer, tutorialCompleteOverlay, tutorialCompleteBox, menuButton);

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

            // Baru tampilkan setelah posisi keeper dan bola sudah tepat untuk ukuran layar aktual.
            if (root.getWidth() > 0 && root.getHeight() > 0) {
                keeper.setVisible(true);
                ball.setVisible(true);
            }

            if (state.phase == TutorialPhase.KEEPER_AIM) {
                tutorialTargetText.setText("TARGET: SAVE THE BALL");
                ballGuideMarker.setVisible(false);
                ballGuideLabel.setVisible(false);
                goalGuideMarker.setVisible(false);
                goalGuideLabel.setVisible(false);

                prepareTutorialAutoShot(root, ball, targetMarker, state);
                targetMarker.setRadius(28);
                targetMarker.setFill(Color.rgb(255, 220, 35, 0.34));
                targetMarker.setStroke(Color.rgb(255, 245, 120));
                targetMarker.setStrokeWidth(5);
                targetMarker.getStrokeDashArray().setAll(10.0, 6.0);

                keeperGuideLabel.setLayoutX(clamp(
                        state.targetX - keeperGuideLabel.getLayoutBounds().getWidth() / 2,
                        16,
                        root.getWidth() - keeperGuideLabel.getLayoutBounds().getWidth() - 16
                ));
                keeperGuideLabel.setLayoutY(Math.max(145, state.targetY - 42));
                keeperGuideLabel.setVisible(true);
                ball.setCursor(Cursor.DEFAULT);
            } else if (state.phase == TutorialPhase.KICKER) {
                tutorialTargetText.setText("TARGET: SCORE A GOAL");
                targetMarker.setRadius(12);
                targetMarker.setFill(Color.rgb(255, 225, 35, 0.82));
                targetMarker.setStroke(Color.WHITE);
                targetMarker.setStrokeWidth(3);
                targetMarker.getStrokeDashArray().clear();
                keeperGuideLabel.setVisible(false);

                ballGuideMarker.setCenterX(state.anchorX);
                ballGuideMarker.setCenterY(state.anchorY);
                ballGuideMarker.setVisible(true);
                ballGuideLabel.setLayoutX(state.anchorX - ballGuideLabel.getLayoutBounds().getWidth() / 2);
                ballGuideLabel.setLayoutY(state.anchorY - BALL_SIZE * 0.78);
                ballGuideLabel.setVisible(true);

                double guideX = root.getWidth() * (GOAL_LEFT_RATIO
                        + (GOAL_RIGHT_RATIO - GOAL_LEFT_RATIO) * 0.22);
                double guideY = root.getHeight() * (GOAL_TOP_RATIO
                        + (GOAL_BOTTOM_RATIO - GOAL_TOP_RATIO) * 0.38);
                goalGuideMarker.setCenterX(guideX);
                goalGuideMarker.setCenterY(guideY);
                goalGuideMarker.setVisible(true);
                goalGuideLabel.setLayoutX(guideX - goalGuideLabel.getLayoutBounds().getWidth() / 2);
                goalGuideLabel.setLayoutY(guideY - 48);
                goalGuideLabel.setVisible(true);
                ball.setCursor(Cursor.HAND);
            }
        };

        Runnable showTutorialCompletePopup = () -> {
            state.tutorialComplete = true;
            state.gameOver = true;
            state.phase = TutorialPhase.COMPLETE;
            state.ballMoving = false;
            state.dragging = false;
            ballGuideMarker.setVisible(false);
            ballGuideLabel.setVisible(false);
            goalGuideMarker.setVisible(false);
            goalGuideLabel.setVisible(false);
            keeperGuideLabel.setVisible(false);
            targetMarker.setVisible(false);
            ball.setCursor(Cursor.DEFAULT);
            titleText.setText("TUTORIAL COMPLETE");
            tutorialTargetText.setText("TARGET: COMPLETE");
            hintText.setText("Shooter and keeper training is complete.");
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
                ballGuideMarker.setCenterX(state.anchorX);
                ballGuideMarker.setCenterY(state.anchorY);
                ballGuideMarker.setVisible(true);
                ballGuideLabel.setVisible(true);
                hintText.setText("STEP 1: Left-click and hold the ball marked in yellow.");
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
            ballGuideMarker.setVisible(false);
            ballGuideLabel.setVisible(false);
            hintText.setText("STEP 2: Drag the ball until the yellow aim marker reaches the GOAL TARGET.");
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
                hintText.setText("STEP 3: Release the left mouse button to kick the ball.");
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
                ballGuideMarker.setCenterX(state.anchorX);
                ballGuideMarker.setCenterY(state.anchorY);
                ballGuideMarker.setVisible(true);
                ballGuideLabel.setVisible(true);
                hintText.setText("The pull is too weak. Drag farther toward the yellow GOAL TARGET.");
                return;
            }

            goalGuideMarker.setVisible(false);
            goalGuideLabel.setVisible(false);
            hintText.setText("STEP 4: Watch your shot and score a GOAL!");
            chooseKeeperTarget(root, state);
            // Tahap penendang dibuat ramah pemula: keeper tutorial tidak menggagalkan tendangan.
            state.keeperWillCatch = false;
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
                hintText.setText("STEP 6: Click the yellow circle in the goal area to aim the keeper.");
                return;
            }

            double distanceToCatchGuide = Math.hypot(selectedX - state.targetX, selectedY - state.targetY);
            if (distanceToCatchGuide > 62) {
                hintText.setText("STEP 6: Click directly on the yellow CATCH HERE marker.");
                return;
            }

            // Klik di area tanda kuning disnap ke pusat lintasan bola agar latihan keeper mudah.
            prepareTutorialKeeperTarget(root, state, state.targetX, state.targetY);
            state.keeperWillCatch = true;
            targetMarker.setVisible(false);
            keeperGuideLabel.setVisible(false);
            keeperAnimator.startDive(state.keeperDiveDirection, state.keeperWillCatch, state.keeperVerticalJump);
            state.phase = TutorialPhase.KEEPER_EXECUTING;
            state.ballMoving = true;
            startKeeperJumpMovement(root, keeper, state, KEEPER_MOVE_SPEED);
            hintText.setText("STEP 7: The keeper is moving. Try to save the opponent's shot!");
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
