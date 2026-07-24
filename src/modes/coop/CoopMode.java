package modes.coop;

import core.GameEngine;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
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
 * MODE CO-OP
 *
 * Alur dasar:
 * 1. Membuat tampilan dua pemain.
 * 2. Player 1 menendang dan Player 2 memilih arah keeper.
 * 3. Setelah satu giliran selesai, peran pemain ditukar.
 * 4. Skor dicatat sampai jumlah tendangan selesai.
 * 5. Hasil akhir menentukan pemenang.
 */
public class CoopMode extends GameEngine {
    @Override
    public void showMultiplayerMode(Stage stage) {
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
        // Actor belum boleh terlihat saat masih memakai posisi default JavaFX (0,0).
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

        Circle targetMarker = new Circle(8);
        targetMarker.setFill(Color.rgb(255, 235, 95, 0.82));
        targetMarker.setStroke(Color.rgb(255, 255, 255, 0.9));
        targetMarker.setStrokeWidth(2);
        targetMarker.setMouseTransparent(true);
        targetMarker.setVisible(false);
        targetMarker.setOpacity(0);

        Circle keeperChoiceMarker = new Circle(12);
        keeperChoiceMarker.setFill(Color.rgb(80, 210, 255, 0.50));
        keeperChoiceMarker.setStroke(Color.WHITE);
        keeperChoiceMarker.setStrokeWidth(3);
        keeperChoiceMarker.setMouseTransparent(true);
        keeperChoiceMarker.setVisible(false);
        keeperChoiceMarker.setOpacity(0);

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

        Rectangle topHudBackground = createTopHudBackground(root, 126);

        Text roleText = new Text("PLAYER 1 SHOOTER  |  PLAYER 2 KEEPER");
        roleText.setFill(Color.WHITE);
        roleText.setStroke(Color.rgb(0, 0, 0, 0.95));
        roleText.setStrokeWidth(2.6);
        roleText.setFont(Font.font("Arial Black", FontWeight.EXTRA_BOLD, 28));
        roleText.layoutXProperty().bind(Bindings.createDoubleBinding(
                () -> (root.getWidth() - roleText.getLayoutBounds().getWidth()) / 2,
                root.widthProperty(),
                roleText.layoutBoundsProperty()
        ));
        roleText.setLayoutY(45);

        Text scoreText = new Text("P1: 0/5    P2: 0/5");
        scoreText.setFill(Color.rgb(255, 235, 120));
        scoreText.setStroke(Color.rgb(0, 0, 0, 0.96));
        scoreText.setStrokeWidth(2.0);
        scoreText.setFont(Font.font("Arial Black", FontWeight.EXTRA_BOLD, 23));
        scoreText.layoutXProperty().bind(Bindings.createDoubleBinding(
                () -> (root.getWidth() - scoreText.getLayoutBounds().getWidth()) / 2,
                root.widthProperty(),
                scoreText.layoutBoundsProperty()
        ));
        scoreText.setLayoutY(78);

        Text shotText = new Text("SHOT P1: 0/5  |  SHOT P2: 0/5");
        shotText.setFill(Color.WHITE);
        shotText.setStroke(Color.rgb(0, 0, 0, 0.96));
        shotText.setStrokeWidth(2.0);
        shotText.setFont(Font.font("Arial Black", FontWeight.EXTRA_BOLD, 19));
        shotText.setLayoutX(32);
        shotText.setLayoutY(50);

        Text hintText = new Text("PLAYER 1: drag the ball, then release");
        hintText.setFill(Color.WHITE);
        hintText.setStroke(Color.rgb(0, 0, 0, 0.98));
        hintText.setStrokeWidth(2.2);
        hintText.setFont(Font.font("Arial Black", FontWeight.EXTRA_BOLD, 19));
        hintText.layoutXProperty().bind(Bindings.createDoubleBinding(
                () -> (root.getWidth() - hintText.getLayoutBounds().getWidth()) / 2,
                root.widthProperty(),
                hintText.layoutBoundsProperty()
        ));
        hintText.setLayoutY(108);

        Circle[] playerOneScoreCircles = new Circle[MULTIPLAYER_SHOTS_PER_PLAYER];
        Circle[] playerTwoScoreCircles = new Circle[MULTIPLAYER_SHOTS_PER_PLAYER];
        VBox multiplayerScoreBoard = createMultiplayerScoreBoard(playerOneScoreCircles, playerTwoScoreCircles);
        multiplayerScoreBoard.setLayoutX(32);
        multiplayerScoreBoard.setLayoutY(28);

        hintText.setVisible(false);

        javafx.scene.control.Button menuButton = createGameplayMenuButton(stage);
        StackPane.setAlignment(menuButton, Pos.TOP_RIGHT);
        StackPane.setMargin(menuButton, new Insets(18, 28, 0, 0));

        Text playerOneTag = createMultiplayerPlayerTag("PLAYER 1", Color.rgb(235, 55, 55));
        Text playerTwoTag = createMultiplayerPlayerTag("PLAYER 2", Color.rgb(70, 170, 255));
        Text goalText = createGoalText(root);

        Rectangle resultOverlay = new Rectangle();
        resultOverlay.widthProperty().bind(root.widthProperty());
        resultOverlay.heightProperty().bind(root.heightProperty());
        resultOverlay.setFill(Color.rgb(0, 0, 0, 0.62));
        resultOverlay.setMouseTransparent(true);
        resultOverlay.setVisible(false);

        ImageView resultTrophy = createImageView(TOURNAMENT_TROPHY_PATH);
        resultTrophy.setFitWidth(235);
        resultTrophy.setFitHeight(235);
        resultTrophy.setPreserveRatio(true);
        resultTrophy.setMouseTransparent(true);
        DropShadow coopTrophyGlow = new DropShadow();
        coopTrophyGlow.setColor(Color.rgb(255, 205, 35, 0.98));
        coopTrophyGlow.setRadius(62);
        coopTrophyGlow.setSpread(0.48);
        coopTrophyGlow.setOffsetX(0);
        coopTrophyGlow.setOffsetY(0);
        resultTrophy.setEffect(coopTrophyGlow);
        resultTrophy.setVisible(false);
        resultTrophy.setManaged(false);

        Text resultTitle = new Text("PLAYER 1 VICTORY");
        resultTitle.setFill(Color.rgb(255, 220, 55));
        resultTitle.setStroke(Color.rgb(0, 0, 0, 0.98));
        resultTitle.setStrokeWidth(2.4);
        resultTitle.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        resultTitle.setFont(loadFont(MENU_FONT_PATH, 48, Font.font("Arial Black", FontWeight.EXTRA_BOLD, 48)));

        Text resultDetail = new Text("P1: 0/5\nP2: 0/5");
        resultDetail.setFill(Color.rgb(255, 235, 120));
        resultDetail.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        resultDetail.setFont(loadFont(MENU_FONT_PATH, 22, Font.font("Arial", FontWeight.BOLD, 22)));

        Text resultShortcutText = new Text("Click the MENU button in the top-right to return.");
        resultShortcutText.setFill(Color.rgb(255, 255, 255, 0.86));
        resultShortcutText.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        resultShortcutText.setFont(loadFont(MENU_FONT_PATH, 16, Font.font("Arial", FontWeight.BOLD, 16)));

        VBox resultBox = new VBox(14, resultTrophy, resultTitle, resultDetail, resultShortcutText);
        resultBox.setAlignment(Pos.CENTER);
        resultBox.getProperties().put("resultTrophy", resultTrophy);
        resultBox.setPadding(new Insets(24));
        resultBox.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0.74), new CornerRadii(10), Insets.EMPTY)));
        resultBox.setMaxWidth(520);
        resultBox.layoutXProperty().bind(Bindings.createDoubleBinding(
                () -> (root.getWidth() - resultBox.getBoundsInLocal().getWidth()) / 2,
                root.widthProperty(),
                resultBox.boundsInLocalProperty()
        ));
        resultBox.layoutYProperty().bind(root.heightProperty().multiply(0.36));
        resultBox.setVisible(false);

        playLayer.getChildren().addAll(
                pointBoxOverlay,
                keeperBoxOverlay,
                pullLine,
                keeper,
                ball,
                topHudBackground,
                multiplayerScoreBoard,
                playerOneTag,
                playerTwoTag,
                goalText,
                roleText,
                hintText,
                resultOverlay,
                resultBox
        );
        root.getChildren().addAll(background, playLayer, menuButton);

        Scene scene = new Scene(root, 1280, 720);
        setSceneSmooth(stage, scene, this::startGameplayDefaultAudio);
        scene.setCursor(Cursor.DEFAULT);

        // 2. MENYIAPKAN DATA / STATE PERMAINAN

        MultiplayerState state = new MultiplayerState();
        Runnable resetRound = () -> {
            resetMultiplayerRound(
                    root,
                    ball,
                    keeper,
                    pullLine,
                    targetMarker,
                    keeperChoiceMarker,
                    keeperAnimator,
                    state,
                    roleText,
                    scoreText,
                    shotText,
                    hintText
            );
            updateMultiplayerScoreBoard(state, playerOneScoreCircles, playerTwoScoreCircles);
            updateMultiplayerPlayerTags(root, ball, keeper, playerOneTag, playerTwoTag, state, 0);
            if (root.getWidth() > 0 && root.getHeight() > 0) {
                keeper.setVisible(true);
                ball.setVisible(true);
            }
        };
        // 3. INPUT KEYBOARD

        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                showMenu(stage);
            }
        });

        root.widthProperty().addListener((observable, oldValue, newValue) -> {
            if (!state.ballMoving && !state.roundResolving) {
                resetRound.run();
            }
        });
        root.heightProperty().addListener((observable, oldValue, newValue) -> {
            if (!state.ballMoving && !state.roundResolving) {
                resetRound.run();
            }
        });
        Platform.runLater(resetRound);

        // 4. INPUT MOUSE UNTUK TENDANGAN

        ball.setOnMousePressed(event -> {
            if (state.gameOver || state.phase != MultiplayerPhase.KICKER_AIM || state.ballMoving) {
                event.consume();
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
            keeperChoiceMarker.setVisible(false);
            event.consume();
        });

        ball.setOnMouseDragged(event -> {
            if (state.gameOver || state.phase != MultiplayerPhase.KICKER_AIM || !state.dragging || state.ballMoving) {
                event.consume();
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

            // Multiplayer memakai tarikan virtual: bola tetap diam agar arah tendangan
            // tidak terlihat jelas oleh pemain keeper, sedangkan garis tetap mengikuti mouse.
            setCenter(ball, state.anchorX, state.anchorY);
            pullLine.setEndX(state.anchorX + dx);
            pullLine.setEndY(state.anchorY + dy);
            // Aim marker sengaja disembunyikan di semua mode selain Tutorial.
            targetMarker.setVisible(false);
            event.consume();
        });

        ball.setOnMouseReleased(event -> {
            if (state.gameOver || state.phase != MultiplayerPhase.KICKER_AIM || !state.dragging || state.ballMoving) {
                event.consume();
                return;
            }

            double virtualPullX = pullLine.getEndX();
            double virtualPullY = pullLine.getEndY();
            state.dragging = false;
            pullLine.setVisible(false);
            targetMarker.setVisible(false);

            // Hitung tendangan dari ujung garis tanpa membiarkan bola terlihat berpindah.
            setCenter(ball, virtualPullX, virtualPullY);
            applyKickForce(root, ball, state);
            setCenter(ball, state.anchorX, state.anchorY);
            if (state.shotSpeed < MIN_BALL_SPEED + 20) {
                hintText.setText("The pull is too weak. PLAYER " + state.shooterPlayer + ": aim the shot again.");
                setCenter(ball, state.anchorX, state.anchorY);
                event.consume();
                return;
            }

            state.phase = MultiplayerPhase.KEEPER_AIM;
            state.playerTagFadeTimerSeconds = 0;
            ball.setCursor(Cursor.DEFAULT);
            updateMultiplayerTexts(roleText, scoreText, shotText, hintText, state);
            event.consume();
        });

        playLayer.setOnMouseClicked(event -> {
            if (state.gameOver || state.phase != MultiplayerPhase.KEEPER_AIM || state.ballMoving || state.roundResolving) {
                return;
            }

            double selectedX = event.getX();
            double selectedY = event.getY();
            if (!isPointInsidePointBox(root, selectedX, selectedY)) {
                hintText.setText("PLAYER " + state.keeperPlayer + ": click the goal area to aim the keeper.");
                return;
            }

            keeperChoiceMarker.setCenterX(selectedX);
            keeperChoiceMarker.setCenterY(selectedY);
            keeperChoiceMarker.setVisible(false);
            prepareMultiplayerKeeperTarget(root, state, selectedX, selectedY);
            keeperAnimator.startDive(state.keeperDiveDirection, state.keeperWillCatch, state.keeperVerticalJump);
            state.phase = MultiplayerPhase.EXECUTING;
            state.ballMoving = true;
            startKeeperJumpMovement(root, keeper, state, KEEPER_MOVE_SPEED);
            updateMultiplayerTexts(roleText, scoreText, shotText, hintText, state);
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
                updateMultiplayer(
                        root,
                        ball,
                        keeper,
                        keeperAnimator,
                        keeperBoxOverlay,
                        goalText,
                        roleText,
                        scoreText,
                        shotText,
                        hintText,
                        resultOverlay,
                        resultBox,
                        resultTitle,
                        resultDetail,
                        state,
                        resetRound,
                        deltaSeconds
                );
                updateMultiplayerScoreBoard(state, playerOneScoreCircles, playerTwoScoreCircles);
                updateMultiplayerPlayerTags(root, ball, keeper, playerOneTag, playerTwoTag, state, deltaSeconds);
            }
        };
        gameLoop.start();
    }
}
