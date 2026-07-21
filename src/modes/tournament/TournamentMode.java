package modes.tournament;

import core.GameEngine;
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
/**
 * MODE TOURNAMENT
 *
 * Alur dasar:
 * 1. Pemain memilih jumlah tim dan nama tim.
 * 2. Nama peserta tournament dibuat dan diacak.
 * 3. Pemain menjalani pertandingan tiap ronde.
 * 4. Pemenang maju ke babak berikutnya.
 * 5. Bagan diperbarui sampai juara ditemukan.
 */
public class TournamentMode extends GameEngine {
    @Override
    public void showTournamentMode(Stage stage) {
        // 1. MEMBUAT TAMPILAN MODE

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

        Rectangle topHudBackground = createTopHudBackground(root, 122);

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

        Text shortcutText = new Text("ESC = MENU");
        shortcutText.setFill(Color.rgb(255, 255, 255, 0.9));
        shortcutText.setFont(loadFont(MENU_FONT_PATH, 15, Font.font("Arial", FontWeight.BOLD, 15)));
        shortcutText.setLayoutX(32);
        shortcutText.setLayoutY(108);

        Text goalText = createGoalText(root);

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
        primaryButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        primaryButton.setCursor(Cursor.HAND);

        HBox resultButtons = new HBox(12, primaryButton);
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
                goalText,
                topHudBackground,
                roundText,
                targetText,
                shotsText,
                totalText,
                hintText,
                shortcutText,
                resultOverlay,
                resultBox,
                bracketOverlay
        );
        root.getChildren().addAll(background, playLayer);

        Scene scene = new Scene(root, 1280, 720);
        setSceneSmooth(stage, scene, this::startGameplayDefaultAudio);
        ball.setCursor(Cursor.DEFAULT);
        setTournamentPlayObjectsVisible(ball, keeper, pullLine, targetMarker, false);

        // 2. MENYIAPKAN DATA / STATE PERMAINAN

        TournamentState state = new TournamentState();
        state.teamCount = 8;
        state.playerTeamName = "PLAYER FC";
        prepareEightTeamBracket(state);
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
            if (state.teamCount <= 4) {
                prepareFourTeamBracket(state);
            } else {
                prepareEightTeamBracket(state);
                state.fourTeamParticipants = null;
                state.fourTeamPlayerSlot = -1;
            }
            teamNameInput.setText(state.playerTeamName);
            setupStatusText.setText("Isi nama tim, pilih jumlah tim, lalu mulai");
            startMatchButton.setText("MULAI");
            unlockTournamentSetup.run();
            ball.setCursor(Cursor.DEFAULT);
            resultOverlay.setVisible(false);
            resultBox.setVisible(false);
            setImage(background, TOURNAMENT_BACKGROUND_PATH);
            bracketOverlay.setVisible(true);
            setTournamentPlayObjectsVisible(ball, keeper, pullLine, targetMarker, false);
            refreshUi.run();
            resetRound.run();
        };

        root.widthProperty().addListener((observable, oldValue, newValue) -> resetRound.run());
        root.heightProperty().addListener((observable, oldValue, newValue) -> resetRound.run());
        teamNameInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!state.setupDone) {
                state.playerTeamName = cleanTeamName(newValue);
                if (state.teamCount <= 4
                        && state.fourTeamParticipants != null
                        && state.fourTeamPlayerSlot >= 0
                        && state.fourTeamPlayerSlot < state.fourTeamParticipants.length) {
                    state.fourTeamParticipants[state.fourTeamPlayerSlot] = state.playerTeamName.isEmpty()
                            ? "PLAYER FC"
                            : state.playerTeamName;
                }
                if (state.teamCount > 4
                        && state.eightTeamParticipants != null
                        && state.eightTeamPlayerSlot >= 0
                        && state.eightTeamPlayerSlot < state.eightTeamParticipants.length) {
                    state.eightTeamParticipants[state.eightTeamPlayerSlot] = state.playerTeamName.isEmpty()
                            ? "PLAYER FC"
                            : state.playerTeamName;
                }
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
            setTournamentPlayObjectsVisible(ball, keeper, pullLine, targetMarker, false);
            ball.setCursor(Cursor.DEFAULT);
            refreshUi.run();
            resetRound.run();
        });
        fourTeamButton.setOnAction(event -> {
            state.teamCount = 4;
            prepareFourTeamBracket(state);
            rebuildTournamentBracketBoard(bracketHolder, bracketLabels, state.teamCount);
            setupStatusText.setText("Turnamen 4 tim dipilih - posisi tim diacak");
            refreshUi.run();
        });
        eightTeamButton.setOnAction(event -> {
            state.teamCount = 8;
            prepareEightTeamBracket(state);
            state.fourTeamParticipants = null;
            state.fourTeamPlayerSlot = -1;
            rebuildTournamentBracketBoard(bracketHolder, bracketLabels, state.teamCount);
            setupStatusText.setText("Turnamen 8 tim dipilih - posisi tim diacak");
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
                if (state.teamCount <= 4) {
                    if (state.fourTeamParticipants == null
                            || state.fourTeamParticipants.length != 4
                            || state.fourTeamPlayerSlot < 0
                            || state.fourTeamPlayerSlot >= 4) {
                        prepareFourTeamBracket(state);
                    } else {
                        state.fourTeamParticipants[state.fourTeamPlayerSlot] = state.playerTeamName;
                    }
                } else {
                    if (state.eightTeamParticipants == null
                            || state.eightTeamParticipants.length != 8
                            || state.eightTeamPlayerSlot < 0
                            || state.eightTeamPlayerSlot >= 8) {
                        prepareEightTeamBracket(state);
                    } else {
                        state.eightTeamParticipants[state.eightTeamPlayerSlot] = state.playerTeamName;
                    }
                }
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
            setTournamentPlayObjectsVisible(ball, keeper, pullLine, targetMarker, true);
            ball.setCursor(Cursor.HAND);
            resetRound.run();
        });
        // 3. INPUT KEYBOARD

        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                showMenu(stage);
            }
        });

        // 4. INPUT MOUSE UNTUK TENDANGAN

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
                playRoundResultAudio(ROUND_RESULT_MISS);
                registerTournamentShot(false, ball, keeper, state, resultOverlay, resultBox, resultTitle, resultDetail, primaryButton, background, bracketOverlay, startMatchButton, bracketLabels, refreshUi, resetRound);
                return;
            }

            chooseKeeperTarget(root, state);
            keeperAnimator.startDive(state.keeperDiveDirection, state.keeperWillCatch, state.keeperVerticalJump);
            state.ballMoving = true;
            startKeeperJumpMovement(root, keeper, state, KEEPER_MOVE_SPEED + state.roundIndex * 45);
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
                updateTournament(
                        root,
                        ball,
                        keeper,
                        keeperAnimator,
                        goalText,
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
}
