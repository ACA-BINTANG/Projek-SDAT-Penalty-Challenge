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
import javafx.scene.effect.DropShadow;
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
        // Tournament mulai dari halaman bagan, jadi actor harus tersembunyi sebelum Scene dipasang.
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

        // HUD hitam hanya dipakai saat pertandingan berlangsung.
        // Pada halaman pemilihan 4/8 tim, kotak dan seluruh tulisan HUD disembunyikan.
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

        Button menuButton = createGameplayMenuButton(stage);
        StackPane.setAlignment(menuButton, Pos.TOP_RIGHT);
        StackPane.setMargin(menuButton, new Insets(18, 28, 0, 0));

        Text goalText = createGoalText(root);

        Rectangle resultOverlay = new Rectangle();
        resultOverlay.widthProperty().bind(root.widthProperty());
        resultOverlay.heightProperty().bind(root.heightProperty());
        resultOverlay.setFill(Color.rgb(0, 0, 0, 0.62));
        resultOverlay.setVisible(false);

        ImageView resultTrophy = createImageView(TOURNAMENT_TROPHY_PATH);
        resultTrophy.setFitWidth(245);
        resultTrophy.setFitHeight(245);
        resultTrophy.setPreserveRatio(true);
        resultTrophy.setMouseTransparent(true);
        DropShadow trophyGlow = new DropShadow();
        trophyGlow.setColor(Color.rgb(255, 205, 35, 0.98));
        trophyGlow.setRadius(62);
        trophyGlow.setSpread(0.48);
        trophyGlow.setOffsetX(0);
        trophyGlow.setOffsetY(0);
        resultTrophy.setEffect(trophyGlow);
        resultTrophy.setVisible(false);
        resultTrophy.setManaged(false);

        Text resultTitle = new Text();
        resultTitle.setFill(Color.rgb(255, 220, 55));
        resultTitle.setStroke(Color.rgb(0, 0, 0, 0.96));
        resultTitle.setStrokeWidth(2.2);
        resultTitle.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        resultTitle.setFont(loadFont(MENU_FONT_PATH, 46, Font.font("Arial Black", FontWeight.EXTRA_BOLD, 46)));
        resultTitle.setMouseTransparent(true);

        Text resultDetail = new Text();
        resultDetail.setFill(Color.rgb(255, 230, 120));
        resultDetail.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        resultDetail.setFont(loadFont(MENU_FONT_PATH, 19, Font.font("Arial", FontWeight.BOLD, 19)));
        resultDetail.setMouseTransparent(true);
        resultDetail.setVisible(false);
        resultDetail.setManaged(false);

        Button primaryButton = new Button("CONTINUE");
        primaryButton.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 15));
        primaryButton.setCursor(Cursor.HAND);
        applyTournamentBlueButtonStyle(primaryButton);

        HBox resultButtons = new HBox(12, primaryButton);
        resultButtons.setAlignment(Pos.CENTER);

        VBox resultBox = new VBox(12, resultTrophy, resultTitle, resultDetail, resultButtons);
        resultBox.setAlignment(Pos.CENTER);
        resultBox.setPadding(new Insets(18));
        resultBox.setBackground(Background.EMPTY);
        resultBox.setMaxWidth(560);
        resultBox.getProperties().put("resultTrophy", resultTrophy);
        resultBox.layoutXProperty().bind(Bindings.createDoubleBinding(
                () -> (root.getWidth() - resultBox.getBoundsInLocal().getWidth()) / 2,
                root.widthProperty(),
                resultBox.boundsInLocalProperty()
        ));
        resultBox.layoutYProperty().bind(Bindings.createDoubleBinding(
                () -> Math.max(40, (root.getHeight() - resultBox.getBoundsInLocal().getHeight()) / 2),
                root.heightProperty(),
                resultBox.boundsInLocalProperty()
        ));
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

        // Saat bagan/pilihan 4 dan 8 tim tampil, HUD gameplay harus benar-benar hilang.
        // Begitu START ditekan dan bracket disembunyikan, HUD otomatis muncul kembali.
        topHudBackground.visibleProperty().bind(bracketOverlay.visibleProperty().not());
        roundText.visibleProperty().bind(bracketOverlay.visibleProperty().not());
        targetText.visibleProperty().bind(bracketOverlay.visibleProperty().not());
        shotsText.visibleProperty().bind(bracketOverlay.visibleProperty().not());

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
                keeper,
                ball,
                goalText,
                topHudBackground,
                roundText,
                targetText,
                shotsText,
                resultOverlay,
                resultBox,
                bracketOverlay
        );
        root.getChildren().addAll(background, playLayer, menuButton);

        Scene scene = new Scene(root, 1280, 720);
        ball.setCursor(Cursor.DEFAULT);
        setTournamentPlayObjectsVisible(ball, keeper, pullLine, targetMarker, false);
        setSceneSmooth(stage, scene, this::startGameplayDefaultAudio);

        // 2. MENYIAPKAN DATA / STATE PERMAINAN

        TournamentState state = new TournamentState();
        state.teamCount = 8;
        state.playerTeamName = "PLAYER FC";
        prepareEightTeamBracket(state);
        Runnable resetRound = () -> {
            resetEndlessRound(root, ball, keeper, pullLine, targetMarker, state, keeperAnimator);
            state.ballBehindKeeper = false;
            moveBallInFrontOfKeeper(ball, keeper);

            // resetEndlessRound selalu menampilkan bola. Saat halaman bagan terbuka,
            // paksa semua actor gameplay tetap tersembunyi agar bola tidak muncul di tengah lapangan.
            if (bracketOverlay.isVisible()) {
                setTournamentPlayObjectsVisible(ball, keeper, pullLine, targetMarker, false);
                ball.setCursor(Cursor.DEFAULT);
            }
        };
        Runnable refreshUi = () -> {
            updateTournamentTexts(roundText, targetText, shotsText, state);
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
            setupStatusText.setText("");
            setupStatusText.setVisible(false);
            startMatchButton.setText("START");
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
            if (!newValue.trim().isEmpty()) {
                setupStatusText.setText("");
                setupStatusText.setVisible(false);
            }
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
            // Setelah popup hasil ditekan, barulah berpindah dari halaman gawang ke bagan tournament.
            resultOverlay.setVisible(false);
            resultBox.setVisible(false);
            setImage(background, TOURNAMENT_BACKGROUND_PATH);
            bracketOverlay.setVisible(true);
            ball.setCursor(Cursor.DEFAULT);

            if (state.champion || state.eliminated) {
                startMatchButton.setText("RESTART");
                refreshUi.run();
                resetRound.run();
                setTournamentPlayObjectsVisible(ball, keeper, pullLine, targetMarker, false);
                return;
            }

            state.roundIndex++;
            state.roundGoals = 0;
            state.shotsTaken = 0;
            state.roundFinished = false;
            startMatchButton.setText("START ROUND");
            refreshUi.run();
            resetRound.run();
            setTournamentPlayObjectsVisible(ball, keeper, pullLine, targetMarker, false);
        });
        fourTeamButton.setOnAction(event -> {
            state.teamCount = 4;
            prepareFourTeamBracket(state);
            rebuildTournamentBracketBoard(bracketHolder, bracketLabels, state.teamCount);
            setupStatusText.setText("");
            setupStatusText.setVisible(false);
            refreshUi.run();
        });
        eightTeamButton.setOnAction(event -> {
            state.teamCount = 8;
            prepareEightTeamBracket(state);
            state.fourTeamParticipants = null;
            state.fourTeamPlayerSlot = -1;
            rebuildTournamentBracketBoard(bracketHolder, bracketLabels, state.teamCount);
            setupStatusText.setText("");
            setupStatusText.setVisible(false);
            refreshUi.run();
        });
        startMatchButton.setOnAction(event -> {
            if (state.champion || state.eliminated) {
                startMatchButton.setText("START");
                restartTournament.run();
                return;
            }
            if (!state.setupDone) {
                String teamName = teamNameInput.getText().trim();
                if (teamName.isEmpty()) {
                    setupStatusText.setText("Team name is required");
                    setupStatusText.setVisible(true);
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
                setupStatusText.setText("");
                setupStatusText.setVisible(false);
                lockTournamentSetup.run();
                refreshUi.run();
            }
            setImage(background, GOAL_BACKGROUND_PATH);
            bracketOverlay.setVisible(false);
            // Hitung posisi dulu dalam keadaan tersembunyi, lalu tampilkan tanpa kedipan.
            resetRound.run();
            setTournamentPlayObjectsVisible(ball, keeper, pullLine, targetMarker, true);
            ball.setCursor(Cursor.HAND);
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
            // Aim marker sengaja disembunyikan di semua mode selain Tutorial.
            targetMarker.setVisible(false);
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
