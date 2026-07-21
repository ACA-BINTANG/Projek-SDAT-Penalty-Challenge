package core;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
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
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
/**
 * MESIN PERMAINAN BERSAMA.
 *
 * Class ini berisi fungsi yang dipakai oleh lebih dari satu mode, misalnya:
 * - gerakan bola,
 * - animasi keeper,
 * - pengecekan goal / save / miss,
 * - scoreboard,
 * - data state permainan,
 * - helper tampilan tournament.
 *
 * Pemisahan ini membuat file mode tetap fokus pada aturan mode masing-masing.
 */
public class GameEngine extends GameApp {
    // ==================== 1. TAMPILAN DAN BAGAN TOURNAMENT ====================

    protected void applyTournamentBlueButtonStyle(Button button) {
        String normalStyle =
                "-fx-background-color: #1231A6;" +
                "-fx-text-fill: white;" +
                "-fx-border-color: white;" +
                "-fx-border-width: 3;" +
                "-fx-background-radius: 16;" +
                "-fx-border-radius: 16;" +
                "-fx-padding: 8 22 8 22;";
        String hoverStyle =
                "-fx-background-color: #1D46CF;" +
                "-fx-text-fill: white;" +
                "-fx-border-color: white;" +
                "-fx-border-width: 3;" +
                "-fx-background-radius: 16;" +
                "-fx-border-radius: 16;" +
                "-fx-padding: 8 22 8 22;";

        button.setStyle(normalStyle);
        button.setMinHeight(40);
        button.setOnMouseEntered(event -> {
            if (!button.isDisabled()) {
                button.setStyle(hoverStyle);
            }
        });
        button.setOnMouseExited(event -> button.setStyle(normalStyle));
    }

    protected Button createNavigationButton(String text) {
        Button button = new Button(text);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        button.setCursor(Cursor.HAND);
        button.setMinWidth(86);
        button.setPrefWidth(86);
        button.setMinHeight(32);
        button.setPrefHeight(32);
        return button;
    }

    protected Rectangle createTopHudBackground(StackPane root, double height) {
        Rectangle hudBackground = new Rectangle();
        hudBackground.widthProperty().bind(root.widthProperty());
        hudBackground.setHeight(height);
        hudBackground.setFill(Color.rgb(0, 0, 0, 0.78));
        hudBackground.setMouseTransparent(true);
        return hudBackground;
    }

    protected void setImage(ImageView imageView, String imagePath) {
        Path path = resolveResource(imagePath);
        imageView.setImage(Files.exists(path) ? new Image(path.toUri().toString(), false) : createFallbackImage());
    }

    


    

    

    protected StackPane createTournamentBracketOverlay(Label[] bracketLabels) {
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
        applyTournamentBlueButtonStyle(fourTeamButton);
        applyTournamentBlueButtonStyle(eightTeamButton);

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
        startMatchButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        startMatchButton.setCursor(Cursor.HAND);
        applyTournamentBlueButtonStyle(startMatchButton);

        Text exitHint = new Text("TEKAN ESC UNTUK KEMBALI KE MENU");
        exitHint.setFill(Color.WHITE);
        exitHint.setFont(loadFont(MENU_FONT_PATH, 14, Font.font("Arial", FontWeight.BOLD, 14)));

        HBox actions = new HBox(12, startMatchButton);
        actions.setAlignment(Pos.CENTER);

        VBox content = new VBox(14, title, setupBox, bracketHolder, actions, exitHint);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(24));
        content.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        content.setMaxWidth(1020);

        StackPane overlay = new StackPane(dim, content);
        dim.widthProperty().bind(overlay.widthProperty());
        dim.heightProperty().bind(overlay.heightProperty());
        overlay.setVisible(true);
        overlay.getProperties().put("startMatchButton", startMatchButton);
        overlay.getProperties().put("fourTeamButton", fourTeamButton);
        overlay.getProperties().put("eightTeamButton", eightTeamButton);
        overlay.getProperties().put("teamNameInput", teamNameInput);
        overlay.getProperties().put("setupStatusText", setupStatusText);
        overlay.getProperties().put("bracketHolder", bracketHolder);
        return overlay;
    }

    protected void rebuildTournamentBracketBoard(StackPane bracketHolder, Label[] bracketLabels, int teamCount) {
        for (int i = 0; i < bracketLabels.length; i++) {
            bracketLabels[i] = null;
        }
        bracketHolder.getChildren().setAll(teamCount <= 4
                ? createFourTeamBracketBoard(bracketLabels)
                : createEightTeamBracketBoard(bracketLabels));
    }

    protected StackPane createEightTeamBracketBoard(Label[] bracketLabels) {
        double boardWidth = 920;
        double boardHeight = 517.5;
        double scaleX = boardWidth / 1600.0;
        double scaleY = boardHeight / 900.0;

        ImageView bracketImage = createImageView(TOURNAMENT_BRACKET_PATH);
        bracketImage.setPreserveRatio(false);
        bracketImage.setFitWidth(boardWidth);
        bracketImage.setFitHeight(boardHeight);

        Pane labels = new Pane();
        labels.setPrefSize(boardWidth, boardHeight);
        labels.setMinSize(boardWidth, boardHeight);
        labels.setMaxSize(boardWidth, boardHeight);
        labels.setMouseTransparent(true);

        // 0-7  = delapan peserta awal.
        // 8-11 = empat pemenang quarter final.
        // 12   = kotak final/juara warna kuning.
        // Posisi disesuaikan dengan titik tengah kotak pada Bagan-Tournament.png.
        double[][] labelPositions = {
                {195, 211}, {195, 370}, {195, 536}, {195, 698},
                {1403, 211}, {1403, 370}, {1403, 536}, {1403, 698},
                {505, 297}, {1103, 297}, {505, 617}, {1103, 617},
                {800, 451}
        };

        double[] widths = {
                136, 136, 136, 136, 136, 136, 136, 136,
                96, 96, 96, 96,
                170
        };

        for (int i = 0; i < labelPositions.length; i++) {
            double labelWidth = widths[i];
            Label label = createBracketLabel(labelWidth);

            if (i < 8) {
                label.setFont(loadFont(MENU_FONT_PATH, 9.5, Font.font("Arial", FontWeight.BOLD, 9.5)));
            } else if (i < 12) {
                label.setFont(loadFont(MENU_FONT_PATH, 8.5, Font.font("Arial", FontWeight.BOLD, 8.5)));
            } else {
                label.setFont(loadFont(MENU_FONT_PATH, 9, Font.font("Arial", FontWeight.BOLD, 9)));
                label.setMinHeight(48);
                label.setPrefHeight(48);
                label.setMaxHeight(48);
                label.setWrapText(true);
            }

            double centerX = labelPositions[i][0] * scaleX;
            double centerY = labelPositions[i][1] * scaleY;
            double labelHeight = i == 12 ? 48 : 24;
            label.setLayoutX(centerX - labelWidth / 2);
            label.setLayoutY(centerY - labelHeight / 2);
            bracketLabels[i] = label;
            labels.getChildren().add(label);
        }

        StackPane board = new StackPane(bracketImage, labels);
        board.setMinSize(boardWidth, boardHeight);
        board.setMaxSize(boardWidth, boardHeight);
        return board;
    }

    protected StackPane createFourTeamBracketBoard(Label[] bracketLabels) {
        double boardWidth = 920;
        double boardHeight = 517.5;
        double scaleX = boardWidth / 1600.0;
        double scaleY = boardHeight / 900.0;

        ImageView bracketImage = createImageView(TOURNAMENT_4_BRACKET_PATH);
        bracketImage.setPreserveRatio(false);
        bracketImage.setFitWidth(boardWidth);
        bracketImage.setFitHeight(boardHeight);

        Pane labels = new Pane();
        labels.setPrefSize(boardWidth, boardHeight);
        labels.setMinSize(boardWidth, boardHeight);
        labels.setMaxSize(boardWidth, boardHeight);
        labels.setMouseTransparent(true);

        // 0-3 = peserta awal, 4-5 = pemenang semifinal, 6 = juara.
        double[][] labelPositions = {
                {238, 728}, {620, 728}, {990, 728}, {1365, 728},
                {455, 420}, {1145, 420},
                {800, 170}
        };
        double[] widths = {150, 150, 150, 150, 155, 155, 205};

        for (int i = 0; i < labelPositions.length; i++) {
            double labelWidth = widths[i];
            Label label = createBracketLabel(labelWidth);
            double centerX = labelPositions[i][0] * scaleX;
            double centerY = labelPositions[i][1] * scaleY;
            label.setLayoutX(centerX - labelWidth / 2);
            label.setLayoutY(centerY - 12);
            bracketLabels[i] = label;
            labels.getChildren().add(label);
        }

        StackPane board = new StackPane(bracketImage, labels);
        board.setMinSize(boardWidth, boardHeight);
        board.setMaxSize(boardWidth, boardHeight);
        return board;
    }

    protected void addBracketLine(Pane board, double startX, double startY, double endX, double endY) {
        Line line = new Line(startX, startY, endX, endY);
        line.setStroke(Color.WHITE);
        line.setStrokeWidth(4);
        board.getChildren().add(line);
    }

    protected void addBracketSlot(Pane board, Label[] bracketLabels, int index, double x, double y, double width, double height, boolean championSlot) {
        Rectangle frame = new Rectangle(width, height);
        frame.setLayoutX(x);
        frame.setLayoutY(y);
        frame.setArcWidth(14);
        frame.setArcHeight(14);
        frame.setFill(Color.rgb(18, 49, 166, 0.96));
        frame.setStroke(championSlot ? Color.rgb(255, 188, 42) : Color.WHITE);
        frame.setStrokeWidth(4);

        Label label = createBracketLabel(width - 20);
        label.setLayoutX(x + 10);
        label.setLayoutY(y + (height - 24) / 2);
        bracketLabels[index] = label;

        board.getChildren().addAll(frame, label);
    }

    protected Label createBracketLabel(double width) {
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

    protected void updateTournamentBracketLabels(Label[] bracketLabels, TournamentState state) {
        for (int i = 0; i < bracketLabels.length; i++) {
            setBracketLabel(bracketLabels, i, "", false);
        }

        String playerTeam = state.playerTeamName != null && !state.playerTeamName.isEmpty()
                ? state.playerTeamName
                : "PLAYER FC";

        if (state.teamCount <= 4) {
            if (state.fourTeamParticipants == null || state.fourTeamParticipants.length != 4) {
                prepareFourTeamBracket(state);
            }

            for (int i = 0; i < 4; i++) {
                setBracketLabel(
                        bracketLabels,
                        i,
                        state.fourTeamParticipants[i],
                        i == state.fourTeamPlayerSlot
                );
            }

            int playerSemiSlot = state.fourTeamPlayerSlot < 2 ? 4 : 5;
            int otherSemiSlot = playerSemiSlot == 4 ? 5 : 4;
            if (state.roundIndex > 0 || state.champion) {
                setBracketLabel(bracketLabels, playerSemiSlot, playerTeam, true);
                setBracketLabel(bracketLabels, otherSemiSlot, getTournamentOpponent(state, 1), false);
            }
        } else {
            if (state.eightTeamParticipants == null || state.eightTeamParticipants.length != 8) {
                prepareEightTeamBracket(state);
            }

            // Delapan slot awal selalu langsung terisi. Posisi PLAYER juga diacak.
            for (int i = 0; i < 8; i++) {
                setBracketLabel(
                        bracketLabels,
                        i,
                        state.eightTeamParticipants[i],
                        i == state.eightTeamPlayerSlot
                );
            }

            int playerPair = state.eightTeamPlayerSlot / 2;
            int otherPair = getEightTeamOtherPairOnSameSide(playerPair);

            if (state.roundIndex > 0 || state.champion) {
                // Isi semua kotak quarter-final winner supaya bagan tidak terlihat kosong.
                for (int pair = 0; pair < 4; pair++) {
                    int winnerLabel = getEightTeamQuarterWinnerLabel(pair);
                    String winnerName;
                    boolean isPlayerWinner = pair == playerPair;

                    if (isPlayerWinner) {
                        winnerName = playerTeam;
                    } else if (pair == otherPair) {
                        winnerName = getTournamentOpponent(state, 1);
                    } else {
                        int participantStart = pair * 2;
                        winnerName = state.eightTeamParticipants[participantStart];
                    }

                    setBracketLabel(bracketLabels, winnerLabel, winnerName, isPlayerWinner);
                }
            }

            if (state.roundIndex > 1 && !state.champion && !state.eliminated) {
                // Gambar 8 tim hanya memiliki satu kotak tengah. Gunakan kotak kuning
                // tersebut untuk menampilkan pasangan final agar nama tidak mengambang.
                setBracketLabel(
                        bracketLabels,
                        12,
                        playerTeam + "\nVS " + getTournamentOpponent(state, 2),
                        true
                );
            }
        }

        int championLabelIndex = state.teamCount <= 4 ? 6 : 12;
        if (state.champion) {
            setBracketLabel(bracketLabels, championLabelIndex, playerTeam, true);
        }

        if (state.eliminated) {
            setBracketLabel(bracketLabels, championLabelIndex, "TERHENTI", false);
        }
    }

    protected void setTournamentPlayObjectsVisible(
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

    protected void setBracketLabel(Label[] bracketLabels, int index, String value, boolean playerTeam) {
        if (index < 0 || index >= bracketLabels.length || bracketLabels[index] == null) {
            return;
        }
        bracketLabels[index].setText(shortenBracketName(value, index));
        bracketLabels[index].setTextFill(playerTeam ? Color.rgb(75, 210, 255) : Color.WHITE);
    }

    protected String shortenBracketName(String value, int index) {
        if (value == null) {
            return "";
        }

        String cleanValue = value.trim();
        if (cleanValue.contains("\n")) {
            String[] lines = cleanValue.split("\n", -1);
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < lines.length; i++) {
                if (i > 0) {
                    result.append('\n');
                }
                result.append(shortenBracketLine(lines[i], 16));
            }
            return result.toString();
        }

        int maxLength;
        if (index >= 8 && index <= 11) {
            maxLength = 11;
        } else if (index == 12 || index == 14) {
            maxLength = 18;
        } else {
            maxLength = 13;
        }
        return shortenBracketLine(cleanValue, maxLength);
    }

    protected String shortenBracketLine(String value, int maxLength) {
        String cleanValue = value == null ? "" : value.trim();
        if (cleanValue.length() > maxLength) {
            return cleanValue.substring(0, maxLength);
        }
        return cleanValue;
    }

    protected int getTournamentRoundCount(TournamentState state) {
        return state.teamCount <= 4 ? 2 : 3;
    }

    protected String getTournamentRoundName(TournamentState state) {
        int roundOffset = state.teamCount <= 4 ? 1 : 0;
        int roundIndex = clampInt(state.roundIndex + roundOffset, 0, TOURNAMENT_ROUNDS.length - 1);
        return TOURNAMENT_ROUNDS[roundIndex];
    }

    protected int getTournamentTarget(TournamentState state) {
        int roundOffset = state.teamCount <= 4 ? 1 : 0;
        int targetIndex = clampInt(state.roundIndex + roundOffset, 0, TOURNAMENT_TARGETS.length - 1);
        return TOURNAMENT_TARGETS[targetIndex];
    }

    protected void prepareFourTeamBracket(TournamentState state) {
        String playerTeam = state.playerTeamName == null || state.playerTeamName.trim().isEmpty()
                ? "PLAYER FC"
                : state.playerTeamName.trim();
        String[] randomOpponents = createTournamentOpponents(4);

        // Satu dari empat posisi awal khusus PLAYER, tetapi posisinya dipilih secara acak.
        int playerSlot = random.nextInt(4);
        String[] participants = new String[4];
        participants[playerSlot] = playerTeam;

        int opponentIndex = 0;
        for (int i = 0; i < participants.length; i++) {
            if (i != playerSlot) {
                participants[i] = randomOpponents[opponentIndex++];
            }
        }

        int semifinalOpponentSlot = playerSlot % 2 == 0 ? playerSlot + 1 : playerSlot - 1;
        int otherPairStart = playerSlot < 2 ? 2 : 0;
        int simulatedFinalistSlot = otherPairStart + random.nextInt(2);

        state.fourTeamParticipants = participants;
        state.fourTeamPlayerSlot = playerSlot;
        state.opponents = new String[] {
                participants[semifinalOpponentSlot],
                participants[simulatedFinalistSlot]
        };
    }

    protected void prepareEightTeamBracket(TournamentState state) {
        String playerTeam = state.playerTeamName == null || state.playerTeamName.trim().isEmpty()
                ? "PLAYER FC"
                : state.playerTeamName.trim();
        String[] randomOpponents = createTournamentOpponents(8);

        // Satu dari delapan slot dipilih secara acak untuk PLAYER.
        int playerSlot = random.nextInt(8);
        String[] participants = new String[8];
        participants[playerSlot] = playerTeam;

        int opponentIndex = 0;
        for (int i = 0; i < participants.length; i++) {
            if (i != playerSlot) {
                participants[i] = randomOpponents[opponentIndex++];
            }
        }

        // Lawan perempat final adalah tim yang berada tepat satu pasangan dengan PLAYER.
        int quarterFinalOpponentSlot = playerSlot % 2 == 0 ? playerSlot + 1 : playerSlot - 1;

        // Untuk semifinal, simulasikan pemenang dari pasangan lain pada sisi bracket yang sama.
        int playerPair = playerSlot / 2;
        int otherPair = getEightTeamOtherPairOnSameSide(playerPair);
        int semifinalOpponentSlot = otherPair * 2 + random.nextInt(2);

        // Untuk final, simulasikan satu finalis dari sisi bracket yang berlawanan.
        int oppositeSideStart = playerSlot < 4 ? 4 : 0;
        int finalOpponentSlot = oppositeSideStart + random.nextInt(4);

        state.eightTeamParticipants = participants;
        state.eightTeamPlayerSlot = playerSlot;
        state.opponents = new String[] {
                participants[quarterFinalOpponentSlot],
                participants[semifinalOpponentSlot],
                participants[finalOpponentSlot]
        };
    }

    protected int getEightTeamQuarterWinnerLabel(int pairIndex) {
        return switch (pairIndex) {
            case 0 -> 8;
            case 1 -> 10;
            case 2 -> 9;
            case 3 -> 11;
            default -> 8;
        };
    }

    protected int getEightTeamOtherPairOnSameSide(int pairIndex) {
        return switch (pairIndex) {
            case 0 -> 1;
            case 1 -> 0;
            case 2 -> 3;
            case 3 -> 2;
            default -> 1;
        };
    }

    protected String[] createTournamentOpponents(int teamCount) {
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

    protected String getTournamentOpponent(TournamentState state, int roundIndex) {
        if (state.opponents == null || roundIndex < 0 || roundIndex >= state.opponents.length) {
            return "TBD";
        }
        return state.opponents[roundIndex];
    }

    protected String cleanTeamName(String teamName) {
        if (teamName == null) {
            return "";
        }
        String cleanName = teamName.replace("\t", " ").replace("\r", " ").replace("\n", " ").trim();
        if (cleanName.length() > 16) {
            return cleanName.substring(0, 16);
        }
        return cleanName;
    }

    protected int clampInt(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }


    // ==================== 2. LOGIKA CO-OP / MULTIPLAYER ====================

    protected void resetMultiplayerRound(
            StackPane root,
            ImageView ball,
            ImageView keeper,
            Line pullLine,
            Circle targetMarker,
            Circle keeperChoiceMarker,
            KeeperAnimator keeperAnimator,
            MultiplayerState state,
            Text roleText,
            Text scoreText,
            Text shotText,
            Text hintText
    ) {
        if (root.getWidth() <= 0 || root.getHeight() <= 0) {
            return;
        }

        state.ballMoving = false;
        state.dragging = false;
        state.keeperMoving = false;
        state.keeperFallingAfterCatch = false;
        state.keeperReturningToIdle = false;
        state.keeperJumping = false;
        state.keeperVerticalJump = false;
        state.keeperWillCatch = false;
        state.keeperDiveDirection = 0;
        state.roundResolving = false;
        state.awaitingKeeperAnimationFinish = false;
        state.pendingRoundResult = ROUND_RESULT_NONE;
        state.roundResult = ROUND_RESULT_NONE;
        state.roundResolveTimer = 0;
        state.goalTextTimerSeconds = 0;
        state.keeperFallElapsedSeconds = 0;
        state.keeperMoveElapsedSeconds = 0;
        state.keeperMoveDurationSeconds = 0;
        state.keeperMoveArcHeight = 0;
        state.keeperFallArcHeight = 0;
        state.keeperRetroAccumulatorSeconds = 0;
        state.ballRetroAccumulatorSeconds = 0;
        state.retroMotionAccumulatorSeconds = 0;
        state.velocityX = 0;
        state.velocityY = 0;
        state.shotSpeed = 0;
        state.anchorX = root.getWidth() * 0.5;
        state.anchorY = root.getHeight() * 0.78;
        state.targetX = state.anchorX;
        state.targetY = state.anchorY;
        state.phase = state.gameOver ? MultiplayerPhase.GAME_OVER : MultiplayerPhase.KICKER_AIM;

        ball.setVisible(true);
        ball.setRotate(0);
        resetBallPerspective(ball);
        ball.setCursor(state.gameOver ? Cursor.DEFAULT : Cursor.HAND);
        setCenter(ball, state.anchorX, state.anchorY);
        setKeeperToIdlePosition(root, keeper);
        keeperAnimator.showIdle();
        pullLine.setVisible(false);
        targetMarker.setVisible(false);
        keeperChoiceMarker.setVisible(false);
        state.playerTagFadeTimerSeconds = 0;
        updateMultiplayerTexts(roleText, scoreText, shotText, hintText, state);
    }

    protected VBox createMultiplayerScoreBoard(Circle[] playerOneCircles, Circle[] playerTwoCircles) {
        Text playerOneLabel = createScoreBoardLabel("PLAYER 1", Color.rgb(235, 55, 55));
        Text playerTwoLabel = createScoreBoardLabel("PLAYER 2", Color.rgb(70, 170, 255));

        HBox playerOneRow = new HBox(8);
        playerOneRow.setAlignment(Pos.CENTER_LEFT);
        playerOneRow.getChildren().add(playerOneLabel);
        for (int i = 0; i < playerOneCircles.length; i++) {
            playerOneCircles[i] = createScoreCircle();
            playerOneRow.getChildren().add(playerOneCircles[i]);
        }

        HBox playerTwoRow = new HBox(8);
        playerTwoRow.setAlignment(Pos.CENTER_LEFT);
        playerTwoRow.getChildren().add(playerTwoLabel);
        for (int i = 0; i < playerTwoCircles.length; i++) {
            playerTwoCircles[i] = createScoreCircle();
            playerTwoRow.getChildren().add(playerTwoCircles[i]);
        }

        VBox board = new VBox(8, playerOneRow, playerTwoRow);
        board.setPadding(new Insets(10, 12, 10, 12));
        board.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0.48), new CornerRadii(8), Insets.EMPTY)));
        board.setMouseTransparent(true);
        return board;
    }

    protected Text createScoreBoardLabel(String value, Color color) {
        Text label = new Text(value);
        label.setFill(color);
        label.setFont(loadFont(MENU_FONT_PATH, 18, Font.font("Arial", FontWeight.EXTRA_BOLD, 18)));
        label.setStroke(Color.rgb(0, 0, 0, 0.45));
        label.setStrokeWidth(0.8);
        return label;
    }

    protected Circle createScoreCircle() {
        Circle circle = new Circle(9);
        circle.setFill(Color.rgb(32, 32, 32, 0.82));
        circle.setStroke(Color.rgb(255, 255, 255, 0.72));
        circle.setStrokeWidth(2);
        return circle;
    }

    protected void updateMultiplayerScoreBoard(MultiplayerState state, Circle[] playerOneCircles, Circle[] playerTwoCircles) {
        updateMultiplayerScoreRow(playerOneCircles, state.playerOneShotResults);
        updateMultiplayerScoreRow(playerTwoCircles, state.playerTwoShotResults);
    }

    protected void updateMultiplayerScoreRow(Circle[] circles, int[] shotResults) {
        for (int i = 0; i < circles.length && i < shotResults.length; i++) {
            int result = shotResults[i];
            if (result == MULTIPLAYER_SCORE_GOAL) {
                circles[i].setFill(Color.rgb(35, 220, 90, 0.95));
                circles[i].setStroke(Color.rgb(210, 255, 220, 0.95));
            } else if (result == MULTIPLAYER_SCORE_FAIL) {
                circles[i].setFill(Color.rgb(225, 45, 45, 0.95));
                circles[i].setStroke(Color.rgb(255, 210, 210, 0.95));
            } else {
                circles[i].setFill(Color.rgb(32, 32, 32, 0.82));
                circles[i].setStroke(Color.rgb(255, 255, 255, 0.72));
            }
        }
    }

    protected void clearMultiplayerShotResults(MultiplayerState state) {
        for (int i = 0; i < MULTIPLAYER_SHOTS_PER_PLAYER; i++) {
            state.playerOneShotResults[i] = MULTIPLAYER_SCORE_EMPTY;
            state.playerTwoShotResults[i] = MULTIPLAYER_SCORE_EMPTY;
        }
    }

    protected void recordMultiplayerShotResult(MultiplayerState state, boolean goal) {
        int value = goal ? MULTIPLAYER_SCORE_GOAL : MULTIPLAYER_SCORE_FAIL;
        if (state.shooterPlayer == 1) {
            int index = clampInt(state.playerOneShots, 0, MULTIPLAYER_SHOTS_PER_PLAYER - 1);
            state.playerOneShotResults[index] = value;
        } else {
            int index = clampInt(state.playerTwoShots, 0, MULTIPLAYER_SHOTS_PER_PLAYER - 1);
            state.playerTwoShotResults[index] = value;
        }
    }

    protected Text createMultiplayerPlayerTag(String value, Color color) {
        Text tag = new Text(value);
        tag.setFill(color);
        tag.setStroke(Color.rgb(0, 0, 0, 0.55));
        tag.setStrokeWidth(0.8);
        tag.setFont(loadFont(MENU_FONT_PATH, 24, Font.font("Arial", FontWeight.EXTRA_BOLD, 24)));
        tag.setMouseTransparent(true);
        tag.setOpacity(0);
        return tag;
    }

    protected void updateMultiplayerPlayerTags(
            StackPane root,
            ImageView ball,
            ImageView keeper,
            Text playerOneTag,
            Text playerTwoTag,
            MultiplayerState state,
            double deltaSeconds
    ) {
        if (root.getWidth() <= 0 || root.getHeight() <= 0) {
            return;
        }

        boolean showTags = !state.gameOver
                && (state.phase == MultiplayerPhase.KICKER_AIM || state.phase == MultiplayerPhase.KEEPER_AIM);
        if (showTags) {
            state.playerTagFadeTimerSeconds += Math.max(0, deltaSeconds);
        }

        double opacity = 0;
        if (showTags && state.playerTagFadeTimerSeconds < PLAYER_TAG_DISPLAY_SECONDS) {
            double fadeIn = clamp(state.playerTagFadeTimerSeconds / PLAYER_TAG_FADE_SECONDS, 0, 1);
            double fadeOut = clamp((PLAYER_TAG_DISPLAY_SECONDS - state.playerTagFadeTimerSeconds) / PLAYER_TAG_FADE_SECONDS, 0, 1);
            opacity = Math.min(fadeIn, fadeOut);
        }

        playerOneTag.setOpacity(opacity);
        playerTwoTag.setOpacity(opacity);

        placeMultiplayerPlayerTag(root, ball, keeper, playerOneTag, state, 1);
        placeMultiplayerPlayerTag(root, ball, keeper, playerTwoTag, state, 2);
    }

    protected void placeMultiplayerPlayerTag(
            StackPane root,
            ImageView ball,
            ImageView keeper,
            Text tag,
            MultiplayerState state,
            int playerNumber
    ) {
        boolean isShooter = state.shooterPlayer == playerNumber;
        ImageView target = isShooter ? ball : keeper;

        double xOffset = isShooter ? BALL_SIZE * 0.80 : KEEPER_SIZE * 0.28;
        double yOffset = isShooter ? -BALL_SIZE * 0.25 : -KEEPER_SIZE * 0.23;
        double x = getCenterX(target) + xOffset;
        double y = getCenterY(target) + yOffset;

        double tagWidth = tag.getLayoutBounds().getWidth();
        double tagHeight = tag.getLayoutBounds().getHeight();
        x = clamp(x, 18, Math.max(18, root.getWidth() - tagWidth - 18));
        y = clamp(y, tagHeight + 18, Math.max(tagHeight + 18, root.getHeight() - 18));

        tag.setLayoutX(x);
        tag.setLayoutY(y);
    }

    protected void updateMultiplayerTexts(
            Text roleText,
            Text scoreText,
            Text shotText,
            Text hintText,
            MultiplayerState state
    ) {
        roleText.setText("PLAYER " + state.shooterPlayer + " PENENDANG  |  PLAYER " + state.keeperPlayer + " KEEPER");
        scoreText.setText("P1: " + state.playerOneGoals + "/" + MULTIPLAYER_SHOTS_PER_PLAYER
                + "    P2: " + state.playerTwoGoals + "/" + MULTIPLAYER_SHOTS_PER_PLAYER);
        shotText.setText("SHOT P1: " + state.playerOneShots + "/" + MULTIPLAYER_SHOTS_PER_PLAYER
                + "  |  SHOT P2: " + state.playerTwoShots + "/" + MULTIPLAYER_SHOTS_PER_PLAYER);

        if (state.phase == MultiplayerPhase.KICKER_AIM) {
            hintText.setText("PLAYER " + state.shooterPlayer + ": tarik bola lalu lepas.");
        } else if (state.phase == MultiplayerPhase.KEEPER_AIM) {
            hintText.setText("PLAYER " + state.keeperPlayer + ": klik area gawang untuk arah keeper.");
        } else if (state.phase == MultiplayerPhase.EXECUTING) {
            hintText.setText("EKSEKUSI: bola dan keeper bergerak.");
        } else if (state.phase == MultiplayerPhase.ROUND_DELAY) {
            hintText.setText("Ronde selesai. Tunggu ronde berikutnya.");
        } else {
            hintText.setText("MATCH SELESAI.");
        }
    }

    protected void prepareMultiplayerKeeperTarget(
            StackPane root,
            MultiplayerState state,
            double selectedX,
            double selectedY
    ) {
        double centerX = root.getWidth() * 0.5;
        double centerY = root.getHeight() * KEEPER_START_CENTER_Y_RATIO;
        double sideThreshold = Math.max(42, root.getWidth() * KEEPER_DIVE_TRIGGER_RATIO);

        int direction;
        if (selectedX > centerX + sideThreshold) {
            direction = 1;
        } else if (selectedX < centerX - sideThreshold) {
            direction = -1;
        } else {
            direction = 0;
        }

        boolean upperCenterSelection = direction == 0 && selectedY < centerY - root.getHeight() * 0.045;
        state.keeperDiveDirection = direction;
        state.keeperVerticalJump = upperCenterSelection;

        double minCenterX = getKeeperMovementMinX(root);
        double maxCenterX = getKeeperMovementMaxX(root);
        double minCenterY = getKeeperMovementMinY(root);
        double maxCenterY = getKeeperMovementMaxY(root);

        if (direction == 0) {
            state.keeperTargetX = centerX;
            state.keeperTargetY = clamp(
                    selectedY - getKeeperSensorOffsetY(0),
                    minCenterY,
                    maxCenterY
            );
        } else {
            state.keeperTargetX = clamp(
                    selectedX - getKeeperSensorOffsetX(direction),
                    minCenterX,
                    maxCenterX
            );
            state.keeperTargetY = clamp(
                    selectedY - getKeeperSensorOffsetY(direction),
                    minCenterY,
                    maxCenterY
            );
        }

        state.keeperJumping = upperCenterSelection
                || Math.hypot(state.keeperTargetX - centerX, state.keeperTargetY - centerY) > 18;
        if (state.keeperJumping) {
            configureKeeperJumpMotion(root, state, centerX, centerY);
        }

        boolean shotInsideGoal = isPointInsidePointBox(root, state.targetX, state.targetY);
        state.keeperWillCatch = shotInsideGoal && isMultiplayerKeeperSelectionSavingShot(state);
    }

    protected boolean isMultiplayerKeeperSelectionSavingShot(MultiplayerState state) {
        int logicalFrame = state.keeperDiveDirection == 0
                ? (state.keeperVerticalJump ? 4 : 1)
                : 4;
        return isPointInsideKeeperSensorBoxAt(
                state.keeperTargetX,
                state.keeperTargetY,
                state.keeperDiveDirection,
                logicalFrame,
                state.targetX,
                state.targetY
        );
    }

    protected void updateMultiplayer(
            StackPane root,
            ImageView ball,
            ImageView keeper,
            KeeperAnimator keeperAnimator,
            Rectangle keeperBoxOverlay,
            Text goalText,
            Text roleText,
            Text scoreText,
            Text shotText,
            Text hintText,
            Rectangle resultOverlay,
            VBox resultBox,
            Text resultTitle,
            Text resultDetail,
            MultiplayerState state,
            Runnable resetRound,
            double deltaSeconds
    ) {
        updateGoalText(goalText, state, deltaSeconds);
        if (state.gameOver) {
            return;
        }

        if (state.roundResolving) {
            state.roundResolveTimer -= deltaSeconds;
            if (state.roundResolveTimer <= 0) {
                finishMultiplayerShot(
                        state,
                        roleText,
                        scoreText,
                        shotText,
                        hintText,
                        resultOverlay,
                        resultBox,
                        resultTitle,
                        resultDetail,
                        resetRound,
                        ball
                );
            }
            return;
        }

        double motionDeltaSeconds = consumeRetroMotionDelta(state, deltaSeconds);
        if (motionDeltaSeconds > 0) {
            keeperAnimator.update(motionDeltaSeconds);
            updateKeeperSensorOverlay(keeperBoxOverlay, keeper, keeperAnimator, state);

            if (keeperAnimator.consumeDiveFallEvent()) {
                if (state.keeperDiveDirection != 0 || state.keeperVerticalJump) {
                    startKeeperDiveFall(root, keeper, state);
                }
            }

            if (keeperAnimator.consumeCatchBallHideEvent()) {
                ball.setVisible(false);
                state.ballMoving = false;
                state.phase = MultiplayerPhase.ROUND_DELAY;
                updateMultiplayerTexts(roleText, scoreText, shotText, hintText, state);
                queueRoundResolutionAfterKeeperAnimation(state, ROUND_RESULT_SAVED);
            }

            if (state.keeperFallingAfterCatch) {
                updateKeeperDiveFall(keeper, state, motionDeltaSeconds);
                updateKeeperSensorOverlay(keeperBoxOverlay, keeper, keeperAnimator, state);
            }

            if (state.keeperMoving) {
                updateKeeperJumpMovement(root, keeper, state, motionDeltaSeconds);
                updateKeeperSensorOverlay(keeperBoxOverlay, keeper, keeperAnimator, state);
            }
        }

        if (state.awaitingKeeperAnimationFinish) {
            if (keeperAnimator.isSequenceFinished() && !state.keeperFallingAfterCatch) {
                beginRoundResolution(state, state.pendingRoundResult);
            }
            return;
        }

        if (state.phase != MultiplayerPhase.EXECUTING || !state.ballMoving || motionDeltaSeconds <= 0) {
            return;
        }

        double ballCenterX = getCenterX(ball);
        double ballCenterY = getCenterY(ball);
        double distanceToTarget = Math.hypot(state.targetX - ballCenterX, state.targetY - ballCenterY);
        double step = state.shotSpeed * motionDeltaSeconds;
        boolean reachedTarget = distanceToTarget <= step;
        if (reachedTarget) {
            setCenterForMotion(ball, state.targetX, state.targetY);
        } else {
            setCenterForMotion(
                    ball,
                    ballCenterX + (state.targetX - ballCenterX) / distanceToTarget * step,
                    ballCenterY + (state.targetY - ballCenterY) / distanceToTarget * step
            );
        }
        updateBallRetroRotation(ball, state, motionDeltaSeconds);
        updateBallPerspectiveScale(ball, state);

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
            state.phase = MultiplayerPhase.ROUND_DELAY;
            hintText.setText("GOAL UNTUK PLAYER " + state.shooterPlayer + ". Tunggu ronde berikutnya.");
            triggerGoalText(goalText, state);
            queueRoundResolutionAfterKeeperAnimation(state, ROUND_RESULT_GOAL);
            return;
        }

        if (outsideScreen || reachedTarget) {
            state.phase = MultiplayerPhase.ROUND_DELAY;
            hintText.setText("BOLA MELESET. Tunggu ronde berikutnya.");
            queueRoundResolutionAfterKeeperAnimation(state, ROUND_RESULT_MISS);
        }
    }

    protected void finishMultiplayerShot(
            MultiplayerState state,
            Text roleText,
            Text scoreText,
            Text shotText,
            Text hintText,
            Rectangle resultOverlay,
            VBox resultBox,
            Text resultTitle,
            Text resultDetail,
            Runnable resetRound,
            ImageView ball
    ) {
        int result = state.roundResult;
        state.roundResolving = false;
        state.awaitingKeeperAnimationFinish = false;
        state.pendingRoundResult = ROUND_RESULT_NONE;
        state.roundResult = ROUND_RESULT_NONE;
        state.roundResolveTimer = 0;
        state.ballMoving = false;
        state.dragging = false;
        state.keeperMoving = false;
        state.keeperFallingAfterCatch = false;

        boolean goal = result == ROUND_RESULT_GOAL;
        recordMultiplayerShotResult(state, goal);
        if (goal) {
            if (state.shooterPlayer == 1) {
                state.playerOneGoals++;
            } else {
                state.playerTwoGoals++;
            }
        }

        if (state.shooterPlayer == 1) {
            state.playerOneShots++;
        } else {
            state.playerTwoShots++;
        }

        if (state.playerOneShots >= MULTIPLAYER_SHOTS_PER_PLAYER
                && state.playerTwoShots >= MULTIPLAYER_SHOTS_PER_PLAYER) {
            state.gameOver = true;
            state.phase = MultiplayerPhase.GAME_OVER;
            ball.setCursor(Cursor.DEFAULT);
            updateMultiplayerTexts(roleText, scoreText, shotText, hintText, state);

            if (state.playerOneGoals > state.playerTwoGoals) {
                resultTitle.setText("PLAYER 1 MENANG");
            } else if (state.playerTwoGoals > state.playerOneGoals) {
                resultTitle.setText("PLAYER 2 MENANG");
            } else {
                resultTitle.setText("HASIL SERI");
            }
            resultDetail.setText("PLAYER 1: " + state.playerOneGoals + "/" + MULTIPLAYER_SHOTS_PER_PLAYER
                    + "\nPLAYER 2: " + state.playerTwoGoals + "/" + MULTIPLAYER_SHOTS_PER_PLAYER
                    + "\nSkor lebih banyak menang.");
            resultOverlay.setVisible(true);
            resultBox.setVisible(true);
            return;
        }

        if (state.shooterPlayer == 1) {
            state.shooterPlayer = 2;
            state.keeperPlayer = 1;
        } else {
            state.shooterPlayer = 1;
            state.keeperPlayer = 2;
        }
        state.phase = MultiplayerPhase.KICKER_AIM;
        state.playerTagFadeTimerSeconds = 0;
        resetRound.run();
    }

    // ==================== 3. LOGIKA TOURNAMENT ====================

    protected void updateTournament(
            StackPane root,
            ImageView ball,
            ImageView keeper,
            KeeperAnimator keeperAnimator,
            Text goalText,
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
        updateGoalText(goalText, state, deltaSeconds);
        if (state.gameOver || state.roundFinished) {
            return;
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

        double motionDeltaSeconds = consumeRetroMotionDelta(state, deltaSeconds);
        if (motionDeltaSeconds > 0) {
            keeperAnimator.update(motionDeltaSeconds);

            if (keeperAnimator.consumeDiveFallEvent()) {
                if (state.keeperDiveDirection != 0 || state.keeperVerticalJump) {
                    startKeeperDiveFall(root, keeper, state);
                }
            }

            if (keeperAnimator.consumeCatchBallHideEvent()) {
                ball.setVisible(false);
                state.ballMoving = false;
                queueRoundResolutionAfterKeeperAnimation(state, ROUND_RESULT_SAVED);
            }

            if (state.keeperFallingAfterCatch) {
                updateKeeperDiveFall(keeper, state, motionDeltaSeconds);
            }

            if (state.keeperMoving) {
                updateKeeperJumpMovement(root, keeper, state, motionDeltaSeconds);
            }
        }

        if (state.awaitingKeeperAnimationFinish) {
            if (keeperAnimator.isSequenceFinished() && !state.keeperFallingAfterCatch) {
                beginRoundResolution(state, state.pendingRoundResult);
            }
            return;
        }

        if (!state.ballMoving || motionDeltaSeconds <= 0) {
            return;
        }

        double ballCenterX = getCenterX(ball);
        double ballCenterY = getCenterY(ball);
        double distanceToTarget = Math.hypot(state.targetX - ballCenterX, state.targetY - ballCenterY);
        double step = state.shotSpeed * motionDeltaSeconds;
        boolean reachedTarget = distanceToTarget <= step;
        if (reachedTarget) {
            setCenterForMotion(ball, state.targetX, state.targetY);
        } else {
            setCenterForMotion(
                    ball,
                    ballCenterX + (state.targetX - ballCenterX) / distanceToTarget * step,
                    ballCenterY + (state.targetY - ballCenterY) / distanceToTarget * step
            );
        }
        updateBallRetroRotation(ball, state, motionDeltaSeconds);
        updateBallPerspectiveScale(ball, state);

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
            triggerGoalText(goalText, state);
            queueRoundResolutionAfterKeeperAnimation(state, ROUND_RESULT_GOAL);
            return;
        }

        if (outsideScreen || reachedTarget) {
            queueRoundResolutionAfterKeeperAnimation(state, ROUND_RESULT_MISS);
        }
    }

    protected void registerTournamentShot(
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

    protected void updateTournamentTexts(Text roundText, Text targetText, Text shotsText, Text totalText, TournamentState state) {
        int target = getTournamentTarget(state);
        int shotsLeft = TOURNAMENT_SHOTS_PER_ROUND - state.shotsTaken;
        roundText.setText(getTournamentRoundName(state));
        targetText.setText("TARGET: " + state.roundGoals + "/" + target);
        shotsText.setText("SISA SHOT: " + shotsLeft);
        totalText.setText("TOTAL: " + state.totalGoals);
    }

    // ==================== 4. LOGIKA ENDLESS ====================

    protected void updateEndless(
            StackPane root,
            ImageView ball,
            ImageView keeper,
            KeeperAnimator keeperAnimator,
            Rectangle keeperBoxOverlay,
            Text goalText,
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
        updateGoalText(goalText, state, deltaSeconds);
        if (state.gameOver) {
            return;
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

        double motionDeltaSeconds = consumeRetroMotionDelta(state, deltaSeconds);
        if (motionDeltaSeconds > 0) {
            keeperAnimator.update(motionDeltaSeconds);
            updateKeeperSensorOverlay(keeperBoxOverlay, keeper, keeperAnimator, state);

            if (keeperAnimator.consumeDiveFallEvent()) {
                if (state.keeperDiveDirection != 0 || state.keeperVerticalJump) {
                    startKeeperDiveFall(root, keeper, state);
                }
            }

            if (keeperAnimator.consumeCatchBallHideEvent()) {
                ball.setVisible(false);
                state.ballMoving = false;
                queueRoundResolutionAfterKeeperAnimation(state, ROUND_RESULT_SAVED);
            }

            if (state.keeperFallingAfterCatch) {
                updateKeeperDiveFall(keeper, state, motionDeltaSeconds);
                updateKeeperSensorOverlay(keeperBoxOverlay, keeper, keeperAnimator, state);
            }

            if (state.keeperMoving) {
                updateKeeperJumpMovement(root, keeper, state, motionDeltaSeconds);
                updateKeeperSensorOverlay(keeperBoxOverlay, keeper, keeperAnimator, state);
            }
        }

        if (state.awaitingKeeperAnimationFinish) {
            if (keeperAnimator.isSequenceFinished() && !state.keeperFallingAfterCatch) {
                beginRoundResolution(state, state.pendingRoundResult);
            }
            return;
        }

        if (!state.ballMoving || motionDeltaSeconds <= 0) {
            return;
        }

        double ballCenterX = getCenterX(ball);
        double ballCenterY = getCenterY(ball);
        double distanceToTarget = Math.hypot(state.targetX - ballCenterX, state.targetY - ballCenterY);
        double step = state.shotSpeed * motionDeltaSeconds;
        boolean reachedTarget = distanceToTarget <= step;
        if (distanceToTarget <= step) {
            setCenterForMotion(ball, state.targetX, state.targetY);
        } else {
            setCenterForMotion(
                    ball,
                    ballCenterX + (state.targetX - ballCenterX) / distanceToTarget * step,
                    ballCenterY + (state.targetY - ballCenterY) / distanceToTarget * step
            );
        }
        updateBallRetroRotation(ball, state, motionDeltaSeconds);
        updateBallPerspectiveScale(ball, state);

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
            triggerGoalText(goalText, state);
            queueRoundResolutionAfterKeeperAnimation(state, ROUND_RESULT_GOAL);
            return;
        }

        if (outsideScreen || reachedTarget) {
            queueRoundResolutionAfterKeeperAnimation(state, ROUND_RESULT_MISS);
        }
    }

    // ==================== 5. LOGIKA TUTORIAL ====================

    protected void prepareTutorialAutoShot(
            StackPane root,
            ImageView ball,
            Circle targetMarker,
            TutorialState state
    ) {
        double goalWidth = root.getWidth() * (GOAL_RIGHT_RATIO - GOAL_LEFT_RATIO);
        double goalHeight = root.getHeight() * (GOAL_BOTTOM_RATIO - GOAL_TOP_RATIO);
        double safeMarginX = Math.max(48, goalWidth * 0.16);
        double safeMarginY = Math.max(34, goalHeight * 0.18);

        double minX = root.getWidth() * GOAL_LEFT_RATIO + safeMarginX;
        double maxX = root.getWidth() * GOAL_RIGHT_RATIO - safeMarginX;
        double minY = root.getHeight() * GOAL_TOP_RATIO + safeMarginY;
        double maxY = root.getHeight() * GOAL_BOTTOM_RATIO - safeMarginY;

        state.targetX = randomBetween(minX, maxX);
        state.targetY = randomBetween(minY, maxY);
        state.shotSpeed = Math.max(MIN_BALL_SPEED + 160, 650);
        state.velocityX = 0;
        state.velocityY = 0;

        targetMarker.setCenterX(state.targetX);
        targetMarker.setCenterY(state.targetY);
        targetMarker.setVisible(true);
        targetMarker.setOpacity(1);
        setCenter(ball, state.anchorX, state.anchorY);
    }

    protected void prepareTutorialKeeperTarget(
            StackPane root,
            TutorialState state,
            double selectedX,
            double selectedY
    ) {
        double centerX = root.getWidth() * 0.5;
        double centerY = root.getHeight() * KEEPER_START_CENTER_Y_RATIO;
        double sideThreshold = Math.max(42, root.getWidth() * KEEPER_DIVE_TRIGGER_RATIO);

        int direction;
        if (selectedX > centerX + sideThreshold) {
            direction = 1;
        } else if (selectedX < centerX - sideThreshold) {
            direction = -1;
        } else {
            direction = 0;
        }

        boolean upperCenterSelection = direction == 0 && selectedY < centerY - root.getHeight() * 0.045;
        state.keeperDiveDirection = direction;
        state.keeperVerticalJump = upperCenterSelection;

        double minCenterX = getKeeperMovementMinX(root);
        double maxCenterX = getKeeperMovementMaxX(root);
        double minCenterY = getKeeperMovementMinY(root);
        double maxCenterY = getKeeperMovementMaxY(root);

        if (direction == 0) {
            state.keeperTargetX = centerX;
            state.keeperTargetY = clamp(
                    selectedY - getKeeperSensorOffsetY(0),
                    minCenterY,
                    maxCenterY
            );
        } else {
            state.keeperTargetX = clamp(
                    selectedX - getKeeperSensorOffsetX(direction),
                    minCenterX,
                    maxCenterX
            );
            state.keeperTargetY = clamp(
                    selectedY - getKeeperSensorOffsetY(direction),
                    minCenterY,
                    maxCenterY
            );
        }

        state.keeperJumping = upperCenterSelection
                || Math.hypot(state.keeperTargetX - centerX, state.keeperTargetY - centerY) > 18;
        if (state.keeperJumping) {
            configureKeeperJumpMotion(root, state, centerX, centerY);
        }

        boolean shotInsideGoal = isPointInsidePointBox(root, state.targetX, state.targetY);
        int logicalFrame = state.keeperDiveDirection == 0
                ? (state.keeperVerticalJump ? 4 : 1)
                : 4;
        state.keeperWillCatch = shotInsideGoal && isPointInsideKeeperSensorBoxAt(
                state.keeperTargetX,
                state.keeperTargetY,
                state.keeperDiveDirection,
                logicalFrame,
                state.targetX,
                state.targetY
        );
    }

    protected void updateTutorial(
            StackPane root,
            ImageView ball,
            ImageView keeper,
            KeeperAnimator keeperAnimator,
            Rectangle keeperBoxOverlay,
            Text goalText,
            Text titleText,
            Text hintText,
            Text shortcutText,
            TutorialState state,
            Runnable resetRound,
            Runnable showTutorialCompletePopup,
            double deltaSeconds
    ) {
        updateGoalText(goalText, state, deltaSeconds);
        if (state.tutorialComplete) {
            return;
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

                if (state.phase == TutorialPhase.KICKER) {
                    if (result == ROUND_RESULT_GOAL) {
                        state.phase = TutorialPhase.KEEPER_AIM;
                        titleText.setText("TUTORIAL - KEEPER");
                        shortcutText.setText("PRESS ESC TO EXIT");
                        resetRound.run();
                        hintText.setText("BAGUS! Sekarang kamu menjadi KEEPER untuk persiapan COOP. Klik lingkaran kuning di gawang.");
                    } else {
                        resetRound.run();
                        hintText.setText("BELUM GOL. LANGKAH 1: Klik dan tahan bola untuk mencoba lagi.");
                    }
                } else if (state.phase == TutorialPhase.KEEPER_EXECUTING) {
                    if (result == ROUND_RESULT_SAVED) {
                        showTutorialCompletePopup.run();
                    } else {
                        state.phase = TutorialPhase.KEEPER_AIM;
                        resetRound.run();
                        hintText.setText("BELUM BERHASIL MENAHAN BOLA. Klik lingkaran kuning dan coba menjadi keeper lagi.");
                    }
                }
            }
            return;
        }

        double motionDeltaSeconds = consumeRetroMotionDelta(state, deltaSeconds);
        if (motionDeltaSeconds > 0) {
            keeperAnimator.update(motionDeltaSeconds);
            updateKeeperSensorOverlay(keeperBoxOverlay, keeper, keeperAnimator, state);

            if (keeperAnimator.consumeDiveFallEvent()) {
                if (state.keeperDiveDirection != 0 || state.keeperVerticalJump) {
                    startKeeperDiveFall(root, keeper, state);
                }
            }

            if (keeperAnimator.consumeCatchBallHideEvent()) {
                ball.setVisible(false);
                state.ballMoving = false;
                queueRoundResolutionAfterKeeperAnimation(state, ROUND_RESULT_SAVED);
            }

            if (state.keeperFallingAfterCatch) {
                updateKeeperDiveFall(keeper, state, motionDeltaSeconds);
                updateKeeperSensorOverlay(keeperBoxOverlay, keeper, keeperAnimator, state);
            }

            if (state.keeperMoving) {
                updateKeeperJumpMovement(root, keeper, state, motionDeltaSeconds);
                updateKeeperSensorOverlay(keeperBoxOverlay, keeper, keeperAnimator, state);
            }
        }

        if (state.awaitingKeeperAnimationFinish) {
            if (keeperAnimator.isSequenceFinished() && !state.keeperFallingAfterCatch) {
                beginRoundResolution(state, state.pendingRoundResult);
            }
            return;
        }

        if (!state.ballMoving || motionDeltaSeconds <= 0) {
            return;
        }

        double ballCenterX = getCenterX(ball);
        double ballCenterY = getCenterY(ball);
        double distanceToTarget = Math.hypot(state.targetX - ballCenterX, state.targetY - ballCenterY);
        double step = state.shotSpeed * motionDeltaSeconds;
        boolean reachedTarget = distanceToTarget <= step;

        if (reachedTarget) {
            setCenterForMotion(ball, state.targetX, state.targetY);
        } else {
            setCenterForMotion(
                    ball,
                    ballCenterX + (state.targetX - ballCenterX) / distanceToTarget * step,
                    ballCenterY + (state.targetY - ballCenterY) / distanceToTarget * step
            );
        }

        updateBallRetroRotation(ball, state, motionDeltaSeconds);
        updateBallPerspectiveScale(ball, state);

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
            triggerGoalText(goalText, state);
            queueRoundResolutionAfterKeeperAnimation(state, ROUND_RESULT_GOAL);
            return;
        }

        if (outsideScreen || reachedTarget) {
            queueRoundResolutionAfterKeeperAnimation(state, ROUND_RESULT_MISS);
        }
    }

    // ==================== 6. HASIL RONDE: GOAL, SAVE, MISS ====================

    protected void startKeeperDiveFall(StackPane root, ImageView keeper, EndlessState state) {
        // Setelah keeper meloncat, keeper turun lagi ke tanah pada alur yang membulat,
        // bukan berhenti lalu geser balik ke posisi awal.
        state.keeperMoving = false;
        state.keeperFallingAfterCatch = true;

        double currentX = getCenterX(keeper);
        double currentY = getCenterY(keeper);
        state.keeperFallStartX = currentX;
        state.keeperFallStartY = currentY;
        state.keeperFallElapsedSeconds = 0;
        state.keeperRetroAccumulatorSeconds = 0;

        if (state.keeperVerticalJump) {
            state.keeperFallTargetX = currentX;
            state.keeperFallTargetY = Math.max(
                    currentY,
                    root.getHeight() * KEEPER_START_CENTER_Y_RATIO
            );
            state.keeperFallArcHeight = 0;
            return;
        }

        double direction = state.keeperDiveDirection == 0 ? 0 : Math.signum(state.keeperDiveDirection);
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
        state.keeperFallArcHeight = Math.max(
                KEEPER_SIZE * 0.08,
                Math.abs(state.keeperFallTargetX - currentX) * KEEPER_FALL_ARC_HEIGHT_RATIO
        );
    }

    protected void updateKeeperDiveFall(
            ImageView keeper,
            EndlessState state,
            double deltaSeconds
    ) {
        state.keeperFallElapsedSeconds += deltaSeconds;

        double progress = clamp(
                state.keeperFallElapsedSeconds / KEEPER_FALL_TO_GROUND_SECONDS,
                0,
                1
        );

        double x;
        double y;
        if (state.keeperVerticalJump) {
            x = state.keeperFallStartX;
            y = lerp(state.keeperFallStartY, state.keeperFallTargetY, easeInQuad(progress));
        } else {
            double xProgress = easeOutQuad(progress);
            double baseY = lerp(state.keeperFallStartY, state.keeperFallTargetY, easeInQuad(progress));
            x = lerp(state.keeperFallStartX, state.keeperFallTargetX, xProgress);
            y = baseY - state.keeperFallArcHeight * Math.sin(Math.PI * progress) * 0.40;
        }

        setCenter(keeper, snapToRetro(x), snapToRetro(y));

        if (progress >= 1.0) {
            setCenter(keeper, snapToRetro(state.keeperFallTargetX), snapToRetro(state.keeperFallTargetY));
            state.keeperFallingAfterCatch = false;
        }
    }

    protected void beginRoundResolution(EndlessState state, int roundResult) {
        state.ballMoving = false;
        state.dragging = false;
        state.keeperMoving = false;
        state.keeperFallingAfterCatch = false;
        state.keeperReturningToIdle = false;
        state.roundResolving = true;
        state.roundResult = roundResult;
        state.roundResolveTimer = ROUND_RESULT_DELAY_SECONDS;
    }

    protected void queueRoundResolutionAfterKeeperAnimation(EndlessState state, int roundResult) {
        playRoundResultAudio(roundResult);
        state.ballMoving = false;
        state.dragging = false;
        state.pendingRoundResult = roundResult;

        if (state.keeperDiveDirection == 0 && !state.keeperJumping) {
            beginRoundResolution(state, roundResult);
            return;
        }

        state.awaitingKeeperAnimationFinish = true;
    }

    protected void finishRoundResolution(
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

    protected boolean isBallInsidePointBox(StackPane root, ImageView ball) {
        double ballCenterX = getCenterX(ball);
        double ballCenterY = getCenterY(ball);
        return isPointInsidePointBox(root, ballCenterX, ballCenterY);
    }

    protected boolean canBallPassStandingKeeper(StackPane root, EndlessState state) {
        return !state.keeperJumping && isPointInsidePointBox(root, state.targetX, state.targetY);
    }

    protected boolean isShotSavedByKeeper(ImageView keeper, KeeperAnimator keeperAnimator, EndlessState state) {
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

    protected boolean isPointInsideKeeperBox(ImageView keeper, double pointX, double pointY) {
        return isPointInsideKeeperSensorBoxAt(
                getCenterX(keeper),
                getCenterY(keeper),
                0,
                1,
                pointX,
                pointY
        );
    }

    protected boolean isPointInsidePointBox(StackPane root, double pointX, double pointY) {
        double width = root.getWidth();
        double height = root.getHeight();

        return pointX >= width * GOAL_LEFT_RATIO
                && pointX <= width * GOAL_RIGHT_RATIO
                && pointY >= height * GOAL_TOP_RATIO
                && pointY <= height * GOAL_BOTTOM_RATIO;
    }

    protected void setKeeperToIdlePosition(StackPane root, ImageView keeper) {
        setCenter(keeper, root.getWidth() * 0.5, root.getHeight() * KEEPER_START_CENTER_Y_RATIO);
    }

    protected void resetEndlessRound(
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
        state.keeperReturningToIdle = false;
        state.keeperJumping = false;
        state.keeperVerticalJump = false;
        state.keeperWillCatch = false;
        state.keeperDiveDirection = 0;
        state.roundResolving = false;
        state.awaitingKeeperAnimationFinish = false;
        state.pendingRoundResult = ROUND_RESULT_NONE;
        state.roundResult = ROUND_RESULT_NONE;
        state.roundResolveTimer = 0;
        state.goalTextTimerSeconds = 0;
        state.keeperFallElapsedSeconds = 0;
        state.keeperMoveElapsedSeconds = 0;
        state.keeperMoveDurationSeconds = 0;
        state.keeperMoveArcHeight = 0;
        state.keeperFallArcHeight = 0;
        state.keeperRetroAccumulatorSeconds = 0;
        state.ballRetroAccumulatorSeconds = 0;
        state.retroMotionAccumulatorSeconds = 0;
        state.velocityX = 0;
        state.velocityY = 0;
        state.shotSpeed = 0;
        state.anchorX = root.getWidth() * 0.5;
        state.anchorY = root.getHeight() * 0.78;
        state.targetX = state.anchorX;
        state.targetY = state.anchorY;

        ball.setVisible(true);
        ball.setRotate(0);
        resetBallPerspective(ball);
        setCenter(ball, state.anchorX, state.anchorY);
        setKeeperToIdlePosition(root, keeper);
        keeperAnimator.showIdle();
        pullLine.setVisible(false);
        targetMarker.setVisible(false);
    }

    protected void damagePlayer(
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

    protected void updateLifeIndicators(Rectangle[] lifeIndicators, int lives) {
        for (int i = 0; i < lifeIndicators.length; i++) {
            boolean active = i < lives;
            lifeIndicators[i].setFill(active ? Color.rgb(224, 42, 42) : Color.rgb(60, 60, 60, 0.72));
            lifeIndicators[i].setStroke(active ? Color.WHITE : Color.rgb(180, 180, 180, 0.75));
        }
    }

    protected List<EndlessScoreEntry> loadEndlessScoreboardEntries() {
        Path scorePath = Path.of(TOP_SCORE_PATH).toAbsolutePath().normalize();
        List<EndlessScoreEntry> entries = new ArrayList<>();
        if (!Files.exists(scorePath)) {
            return entries;
        }

        try {
            for (String line : Files.readAllLines(scorePath, StandardCharsets.UTF_8)) {
                if (line == null || line.isBlank()) {
                    continue;
                }

                String[] parts = line.split("\t");
                if (parts.length < 3) {
                    continue;
                }

                try {
                    String playerName = parts[parts.length - 2].trim();
                    int score = Integer.parseInt(parts[parts.length - 1].trim());
                    if (playerName.isEmpty()) {
                        playerName = "PLAYER";
                    }
                    entries.add(new EndlessScoreEntry(playerName, score));
                } catch (NumberFormatException ignored) {
                    // Abaikan baris skor yang rusak agar scoreboard tetap dapat dibuka.
                }
            }
        } catch (IOException ignored) {
            return new ArrayList<>();
        }

        entries.sort((left, right) -> Integer.compare(right.score, left.score));
        return entries;
    }

    protected String formatEndlessScoreboardPage(List<EndlessScoreEntry> entries, int page, int pageSize) {
        if (entries.isEmpty()) {
            return "Belum ada skor tersimpan.";
        }

        int safePageSize = Math.max(1, pageSize);
        int totalPages = Math.max(1, (entries.size() + safePageSize - 1) / safePageSize);
        int safePage = Math.max(0, Math.min(page, totalPages - 1));
        int startIndex = safePage * safePageSize;
        int endIndex = Math.min(startIndex + safePageSize, entries.size());

        StringBuilder builder = new StringBuilder();
        for (int i = startIndex; i < endIndex; i++) {
            EndlessScoreEntry entry = entries.get(i);
            String safeName = entry.playerName.length() > 16
                    ? entry.playerName.substring(0, 16)
                    : entry.playerName;
            builder.append(String.format("%3d. %-16s  %5d POINT", i + 1, safeName, entry.score));
            if (i < endIndex - 1) {
                builder.append(System.lineSeparator());
            }
        }
        return builder.toString();
    }

    protected void saveTopScore(String playerName, int score) throws IOException {
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

    // ==================== 7. KEEPER DAN SENSOR ====================

    protected void chooseKeeperTarget(StackPane root, EndlessState state) {
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
            boolean upperCenterShot = state.targetY < centerY - root.getHeight() * 0.045;

            if (upperCenterShot) {
                // Tendangan tengah yang naik memakai animasi loncat vertikal.
                // Jika terbaca, pakai folder "loncat tangkap". Jika tidak, pakai folder "loncat" saja.
                state.keeperVerticalJump = true;
                state.keeperDiveDirection = 0;
                if (state.keeperWillCatch) {
                    state.keeperTargetY = clamp(
                            state.targetY - getKeeperSensorOffsetY(0),
                            minCenterY,
                            maxCenterY
                    );
                } else {
                    state.keeperTargetY = clamp(
                            Math.min(centerY - KEEPER_SIZE * 0.16, state.targetY + KEEPER_SIZE * 0.24),
                            minCenterY,
                            maxCenterY
                    );
                }
                state.keeperJumping = true;
                configureKeeperJumpMotion(root, state, centerX, centerY);
                return;
            }

            state.keeperVerticalJump = false;
            if (state.keeperWillCatch) {
                // Tendangan lurus rendah tetap bisa dibaca keeper dengan pose tangkap berdiri.
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
            if (state.keeperJumping) {
                configureKeeperJumpMotion(root, state, centerX, centerY);
            }
            return;
        }

        state.keeperVerticalJump = false;

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
        if (state.keeperJumping) {
            configureKeeperJumpMotion(root, state, centerX, centerY);
        }
    }

    protected int resolveKeeperAnimationDirection(double startX, double targetX, int fallbackDirection, double sideThreshold) {
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

    protected double getKeeperMovementMinX(StackPane root) {
        return root.getWidth() * GOAL_LEFT_RATIO + root.getWidth() * KEEPER_EDGE_PADDING_RATIO;
    }

    protected double getKeeperMovementMaxX(StackPane root) {
        return root.getWidth() * GOAL_RIGHT_RATIO - root.getWidth() * KEEPER_EDGE_PADDING_RATIO;
    }

    protected double getKeeperMovementMinY(StackPane root) {
        return root.getHeight() * GOAL_TOP_RATIO + root.getHeight() * KEEPER_TOP_REACH_PADDING_RATIO;
    }

    protected double getKeeperMovementMaxY(StackPane root) {
        return root.getHeight() * GOAL_BOTTOM_RATIO - root.getHeight() * KEEPER_BOTTOM_REACH_PADDING_RATIO;
    }

    protected double getKeeperSensorWidth(int direction, int logicalFrameNumber) {
        if (direction == 0 || logicalFrameNumber <= 1) {
            return KEEPER_SIZE * KEEPER_STAND_SENSOR_WIDTH_RATIO;
        }
        return KEEPER_SIZE * KEEPER_DIVE_SENSOR_WIDTH_RATIO;
    }

    protected double getKeeperSensorHeight(int direction, int logicalFrameNumber) {
        if (direction == 0 || logicalFrameNumber <= 1) {
            return KEEPER_SIZE * KEEPER_STAND_SENSOR_HEIGHT_RATIO;
        }
        return KEEPER_SIZE * KEEPER_DIVE_SENSOR_HEIGHT_RATIO;
    }

    protected double getKeeperSensorOffsetX(int direction) {
        return direction * KEEPER_SIZE * KEEPER_DIVE_SENSOR_X_OFFSET_RATIO;
    }

    protected double getKeeperSensorOffsetY(int direction) {
        if (direction == 0) {
            return KEEPER_SIZE * KEEPER_STAND_SENSOR_Y_OFFSET_RATIO;
        }
        return KEEPER_SIZE * KEEPER_DIVE_SENSOR_Y_OFFSET_RATIO;
    }

    protected KeeperSensorBox getKeeperSensorBoxAt(double keeperCenterX, double keeperCenterY, int direction, int logicalFrameNumber) {
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

    protected boolean isPointInsideKeeperSensorBoxAt(
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

    protected void updateKeeperSensorOverlay(
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

    protected void rememberScoredShot(EndlessState state) {
        if (state.scoredShotsLearned == 0) {
            state.learnedTargetX = state.targetX;
            state.learnedTargetY = state.targetY;
        } else {
            state.learnedTargetX = state.learnedTargetX * (1 - SHOT_MEMORY_WEIGHT) + state.targetX * SHOT_MEMORY_WEIGHT;
            state.learnedTargetY = state.learnedTargetY * (1 - SHOT_MEMORY_WEIGHT) + state.targetY * SHOT_MEMORY_WEIGHT;
        }
        state.scoredShotsLearned++;
    }

    // ==================== 8. GERAK KEEPER ====================

    protected void startKeeperJumpMovement(StackPane root, ImageView keeper, EndlessState state, double speed) {
        state.keeperMoving = state.keeperJumping;
        if (!state.keeperMoving) {
            return;
        }
        configureKeeperJumpMotion(root, state, getCenterX(keeper), getCenterY(keeper), speed);
    }

    protected void configureKeeperJumpMotion(StackPane root, EndlessState state, double startX, double startY) {
        configureKeeperJumpMotion(root, state, startX, startY, KEEPER_MOVE_SPEED);
    }

    protected void configureKeeperJumpMotion(StackPane root, EndlessState state, double startX, double startY, double speed) {
        state.keeperMoveStartX = startX;
        state.keeperMoveStartY = startY;
        state.keeperMoveElapsedSeconds = 0;
        state.keeperRetroAccumulatorSeconds = 0;

        double moveDistance = Math.hypot(state.keeperTargetX - startX, state.keeperTargetY - startY);
        if (state.keeperVerticalJump || state.keeperDiveDirection == 0) {
            state.keeperMoveDurationSeconds = Math.max(KEEPER_FRAME_SECONDS * 1.5, moveDistance / Math.max(speed, 1));
            state.keeperMoveArcHeight = 0;
            return;
        }

        // Untuk dive samping, durasi dibuat berbasis waktu agar alurnya membentuk busur,
        // bukan garis lurus naik-turun yang terlihat seperti segitiga.
        state.keeperMoveDurationSeconds = KEEPER_SIDE_DIVE_ARC_SECONDS;
        double horizontalDistance = Math.abs(state.keeperTargetX - startX);
        double verticalRise = Math.max(0, startY - state.keeperTargetY);
        state.keeperMoveArcHeight = Math.max(
                KEEPER_SIZE * 0.10,
                horizontalDistance * KEEPER_SIDE_DIVE_ARC_HEIGHT_RATIO + verticalRise * 0.35
        );
    }

    protected void updateKeeperJumpMovement(StackPane root, ImageView keeper, EndlessState state, double deltaSeconds) {
        state.keeperMoveElapsedSeconds += deltaSeconds;
        double progress = clamp(
                state.keeperMoveElapsedSeconds / Math.max(state.keeperMoveDurationSeconds, 0.0001),
                0,
                1
        );

        double x;
        double y;
        if (state.keeperVerticalJump || state.keeperDiveDirection == 0) {
            x = lerp(state.keeperMoveStartX, state.keeperTargetX, easeOutQuad(progress));
            y = lerp(state.keeperMoveStartY, state.keeperTargetY, easeOutQuad(progress));
        } else {
            double moveProgress = easeInOutSine(progress);
            double baseY = lerp(state.keeperMoveStartY, state.keeperTargetY, moveProgress);
            x = lerp(state.keeperMoveStartX, state.keeperTargetX, moveProgress);
            y = baseY - state.keeperMoveArcHeight * Math.sin(Math.PI * progress);
        }

        setCenterForMotion(keeper, x, y);

        if (progress >= 1.0) {
            setCenterForMotion(keeper, state.keeperTargetX, state.keeperTargetY);
            state.keeperMoving = false;
        }
    }

    protected double consumeRetroMotionDelta(EndlessState state, double deltaSeconds) {
        if (!RETRO_MOTION_ENABLED) {
            return deltaSeconds;
        }
        state.retroMotionAccumulatorSeconds = Math.min(
                state.retroMotionAccumulatorSeconds + deltaSeconds,
                (1.0 / RETRO_MOTION_FPS) * 3
        );
        if (state.retroMotionAccumulatorSeconds < RETRO_MOTION_STEP_SECONDS) {
            return 0;
        }
        double step = state.retroMotionAccumulatorSeconds;
        state.retroMotionAccumulatorSeconds = 0;
        return step;
    }

    protected void setCenterForMotion(ImageView imageView, double centerX, double centerY) {
        setCenter(imageView, snapToRetro(centerX), snapToRetro(centerY));
    }


    // ==================== 9. EFEK GOAL DAN GERAK BOLA ====================

    protected Text createGoalText(StackPane root) {
        Text goalText = new Text("GOOOAL!");
        goalText.setFill(Color.rgb(255, 224, 64));
        goalText.setStroke(Color.rgb(25, 12, 0, 0.92));
        goalText.setStrokeWidth(4.5);
        goalText.setFont(loadFont(START_FONT_PATH, 82, Font.font("Arial", FontWeight.EXTRA_BOLD, 82)));
        goalText.setMouseTransparent(true);
        goalText.setVisible(false);
        goalText.setOpacity(0);
        goalText.layoutXProperty().bind(Bindings.createDoubleBinding(
                () -> (root.getWidth() - goalText.getLayoutBounds().getWidth()) / 2,
                root.widthProperty(),
                goalText.layoutBoundsProperty()
        ));
        goalText.layoutYProperty().bind(root.heightProperty().multiply(0.24));
        return goalText;
    }

    protected void triggerGoalText(Text goalText, EndlessState state) {
        if (goalText == null) {
            return;
        }
        state.goalTextTimerSeconds = GOAL_TEXT_DURATION_SECONDS;
        goalText.setVisible(true);
        goalText.setOpacity(1);
        goalText.setScaleX(0.72);
        goalText.setScaleY(0.72);
        goalText.setRotate(-2);
    }

    protected void updateGoalText(Text goalText, EndlessState state, double deltaSeconds) {
        if (goalText == null) {
            return;
        }
        if (state.goalTextTimerSeconds <= 0) {
            goalText.setOpacity(0);
            goalText.setVisible(false);
            return;
        }

        state.goalTextTimerSeconds = Math.max(0, state.goalTextTimerSeconds - deltaSeconds);
        double elapsed = GOAL_TEXT_DURATION_SECONDS - state.goalTextTimerSeconds;
        double popProgress = clamp(elapsed / GOAL_TEXT_POP_SECONDS, 0, 1);
        double fadeOut = clamp(state.goalTextTimerSeconds / GOAL_TEXT_FADE_OUT_SECONDS, 0, 1);
        double opacity = Math.min(1, popProgress * 1.35) * fadeOut;
        double pulse = Math.sin(elapsed * 18.0) * 0.025;
        double scale = 0.72 + 0.42 * easeOutQuad(popProgress) + pulse;

        goalText.setVisible(true);
        goalText.setOpacity(opacity);
        goalText.setScaleX(scale);
        goalText.setScaleY(scale);
        goalText.setRotate(-2 + Math.sin(elapsed * 12.0) * 2.0);

        if (state.goalTextTimerSeconds <= 0) {
            goalText.setOpacity(0);
            goalText.setVisible(false);
            goalText.setScaleX(1);
            goalText.setScaleY(1);
            goalText.setRotate(0);
        }
    }

    protected void resetBallPerspective(ImageView ball) {
        ball.setScaleX(1.0);
        ball.setScaleY(1.0);
    }

    protected void updateBallPerspectiveScale(ImageView ball, EndlessState state) {
        double totalDistance = Math.hypot(state.targetX - state.anchorX, state.targetY - state.anchorY);
        if (totalDistance <= 1) {
            resetBallPerspective(ball);
            return;
        }

        double currentDistance = Math.hypot(getCenterX(ball) - state.anchorX, getCenterY(ball) - state.anchorY);
        double progress = clamp(currentDistance / totalDistance, 0, 1);
        double easedProgress = Math.pow(progress, BALL_PERSPECTIVE_CURVE);
        double scale = lerp(1.0, BALL_MIN_PERSPECTIVE_SCALE, easedProgress);
        ball.setScaleX(scale);
        ball.setScaleY(scale);
    }

    protected void updateBallRetroRotation(ImageView ball, EndlessState state, double deltaSeconds) {
        double direction = state.velocityX >= 0 ? 1.0 : -1.0;
        double rotation = ball.getRotate() + direction * BALL_RETRO_ROTATION_DEGREES_PER_SECOND * deltaSeconds;
        if (RETRO_MOTION_ENABLED) {
            rotation = Math.round(rotation / BALL_ROTATION_SNAP_DEGREES) * BALL_ROTATION_SNAP_DEGREES;
        }
        ball.setRotate(rotation);
    }

    protected double randomBetween(double min, double max) {
        if (max <= min) {
            return min;
        }
        return min + random.nextDouble() * (max - min);
    }

    protected double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    protected double lerp(double start, double end, double progress) {
        return start + (end - start) * progress;
    }

    protected double easeOutQuad(double progress) {
        return 1 - Math.pow(1 - progress, 2);
    }

    protected double easeInQuad(double progress) {
        return progress * progress;
    }

    protected double easeInOutSine(double progress) {
        return -(Math.cos(Math.PI * progress) - 1) / 2.0;
    }

    protected double snapToRetro(double value) {
        if (!RETRO_MOTION_ENABLED) {
            return value;
        }
        return Math.round(value / RETRO_PIXEL_SNAP) * RETRO_PIXEL_SNAP;
    }

    // ==================== 10. PERHITUNGAN TENDANGAN ====================

    protected void applyKickForce(StackPane root, ImageView ball, EndlessState state) {
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
        state.retroMotionAccumulatorSeconds = 0;
    }

    protected void updateTargetMarker(StackPane root, ImageView ball, Circle targetMarker, EndlessState state) {
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

    protected Shot calculateShot(StackPane root, double pullX, double pullY, double pullDistance) {
        double power = Math.min(1.0, pullDistance / MAX_PULL_DISTANCE);
        double tunedPower = Math.pow(power, SHOT_POWER_CURVE);
        double directionX = pullX / pullDistance;
        double directionY = pullY / pullDistance;

        if (directionY < 0) {
            directionY *= UPWARD_SHOT_BONUS;
            double normalized = Math.hypot(directionX, directionY);
            directionX /= normalized;
            directionY /= normalized;
        }

        double goalDistance = Math.abs(getGoalLineY(root) - getBallStartY(root));
        double maxDistance = Math.max(MAX_SHOT_DISTANCE, goalDistance * SHOT_DISTANCE_GOAL_MULTIPLIER);
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

    protected double getGoalLineY(StackPane root) {
        return root.getHeight() * GOAL_SCORE_LINE_RATIO;
    }

    protected double getBallStartY(StackPane root) {
        return root.getHeight() * 0.82;
    }

    // ==================== 11. CLASS DATA / STATE ====================

    protected static class Shot {
        protected final double directionX;
        protected final double directionY;
        protected final double distance;
        protected final double speed;

        protected Shot(double directionX, double directionY, double distance, double speed) {
            this.directionX = directionX;
            this.directionY = directionY;
            this.distance = distance;
            this.speed = speed;
        }
    }

    protected double getCenterX(ImageView imageView) {
        return imageView.getLayoutX() + imageView.getFitWidth() / 2;
    }

    protected double getCenterY(ImageView imageView) {
        return imageView.getLayoutY() + imageView.getFitHeight() / 2;
    }

    protected void setCenter(ImageView imageView, double centerX, double centerY) {
        imageView.setLayoutX(centerX - imageView.getFitWidth() / 2);
        imageView.setLayoutY(centerY - imageView.getFitHeight() / 2);
    }

    protected class KeeperAnimator {
        protected final ImageView imageView;
        protected final Image idleImage;
        protected final Image standingCatchImage;
        protected List<Image> frames;
        protected List<Integer> logicalFrameNumbers;
        protected int frameIndex;
        protected double frameTimer;
        protected boolean playing;
        protected boolean sequenceFinished;
        protected double finalFrameHoldTimer;
        protected boolean catchBall;
        protected boolean verticalJump;
        protected boolean catchBallHideEventPending;
        protected boolean catchBallHideEventFired;
        protected boolean diveFallEventPending;
        protected boolean diveFallEventFired;

        public KeeperAnimator(ImageView imageView) {
            this.imageView = imageView;
            this.idleImage = loadKeeperImage(KEEPER_IDLE_IMAGE_PATH);
            this.standingCatchImage = loadKeeperImage(KEEPER_STANDING_CATCH_IMAGE_PATH);
            this.frames = new ArrayList<>();
            this.logicalFrameNumbers = new ArrayList<>();
            this.frames.add(idleImage);
            this.logicalFrameNumbers.add(1);
        }

        public void showIdle() {
            playing = false;
            sequenceFinished = false;
            catchBall = false;
            verticalJump = false;
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

        public void startDive(int direction, boolean catchBall, boolean verticalJump) {
            this.catchBall = catchBall;
            this.verticalJump = verticalJump;
            this.catchBallHideEventPending = false;
            this.catchBallHideEventFired = false;
            this.diveFallEventPending = false;
            this.diveFallEventFired = false;
            this.sequenceFinished = false;
            this.finalFrameHoldTimer = 0;
            DiveFrameSequence sequence = buildDiveFrames(direction, catchBall, verticalJump);
            frames = sequence.images;
            logicalFrameNumbers = sequence.logicalFrameNumbers;
            frameIndex = 0;
            frameTimer = 0;
            playing = !frames.isEmpty();
            imageView.setImage(frames.get(0));
            applyFrameVisualOffset();
        }

        protected void update(double deltaSeconds) {
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

        protected void applyFrameVisualOffset() {
            int logicalFrame = getCurrentLogicalFrameNumber();

            if (logicalFrame == 5) {
                imageView.setTranslateY(KEEPER_FRAME_5_DOWN_OFFSET);
            } else {
                imageView.setTranslateY(0);
            }
        }

        protected double getCurrentFrameDuration() {
            int logicalFrame = getCurrentLogicalFrameNumber();
            if (verticalJump && logicalFrame >= 7) {
                return KEEPER_FRAME_SECONDS;
            }
            if (logicalFrame >= 5 && logicalFrame < 7) {
                return KEEPER_LANDING_FRAME_HOLD_SECONDS;
            }
            if (logicalFrame >= 7) {
                return KEEPER_FINAL_HOLD_SECONDS;
            }
            return KEEPER_FRAME_SECONDS;
        }

        protected boolean consumeDiveFallEvent() {
            boolean result = diveFallEventPending;
            diveFallEventPending = false;
            return result;
        }

        protected int getCatchHideFrameNumber() {
            if (logicalFrameNumbers.size() <= 2) {
                return 2;
            }
            return 4;
        }

        protected boolean consumeCatchBallHideEvent() {
            boolean result = catchBallHideEventPending;
            catchBallHideEventPending = false;
            return result;
        }

        protected boolean isSequenceFinished() {
            return sequenceFinished || frames.size() <= 1;
        }

        protected int getCurrentLogicalFrameNumber() {
            if (logicalFrameNumbers.isEmpty()) {
                return 1;
            }
            return logicalFrameNumbers.get(Math.max(0, Math.min(frameIndex, logicalFrameNumbers.size() - 1)));
        }

        protected DiveFrameSequence buildDiveFrames(int direction, boolean catchBall, boolean verticalJump) {
            List<Image> result = new ArrayList<>();
            List<Integer> frameNumbers = new ArrayList<>();
            result.add(idleImage);
            frameNumbers.add(1);

            if (direction == 0) {
                if (verticalJump) {
                    String primaryFolder = catchBall ? KEEPER_UP_CATCH_FOLDER : KEEPER_UP_FOLDER;
                    String fallbackFolder = catchBall ? KEEPER_UP_FOLDER : KEEPER_UP_CATCH_FOLDER;
                    int maxSourceFrame = catchBall ? 3 : 2;
                    for (int sourceFrame = 1; sourceFrame <= maxSourceFrame; sourceFrame++) {
                        int fallbackSourceFrame = Math.min(sourceFrame, 2);
                        result.add(loadKeeperImage(
                                framePath(primaryFolder, sourceFrame),
                                framePath(fallbackFolder, fallbackSourceFrame),
                                KEEPER_IDLE_IMAGE_PATH
                        ));

                        if (catchBall && sourceFrame == 2) {
                            frameNumbers.add(4);
                        } else if (sourceFrame == maxSourceFrame) {
                            frameNumbers.add(7);
                        } else {
                            frameNumbers.add(2);
                        }
                    }
                    return new DiveFrameSequence(result, frameNumbers);
                }

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

        protected String framePath(String folder, int frameNumber) {
            return folder + "/" + frameNumber + ".png";
        }

        protected Image loadKeeperImage(String primaryPath) {
            return loadKeeperImage(primaryPath, null, KEEPER_IDLE_IMAGE_PATH);
        }

        protected Image loadKeeperImage(String primaryPath, String secondaryPath, String fallbackPath) {
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

    protected static class KeeperSensorBox {
        protected final double x;
        protected final double y;
        protected final double width;
        protected final double height;

        protected KeeperSensorBox(double x, double y, double width, double height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }

    protected static class DiveFrameSequence {
        protected final List<Image> images;
        protected final List<Integer> logicalFrameNumbers;

        protected DiveFrameSequence(List<Image> images, List<Integer> logicalFrameNumbers) {
            this.images = images;
            this.logicalFrameNumbers = logicalFrameNumbers;
        }
    }


    protected enum TutorialPhase {
        KICKER,
        KEEPER_AIM,
        KEEPER_EXECUTING,
        COMPLETE
    }

    protected enum MultiplayerPhase {
        KICKER_AIM,
        KEEPER_AIM,
        EXECUTING,
        ROUND_DELAY,
        GAME_OVER
    }

    public static class TutorialState extends EndlessState {
        public boolean tutorialComplete;
        public TutorialPhase phase = TutorialPhase.KICKER;
    }

    public static class MultiplayerState extends EndlessState {
        public int playerOneGoals;
        public int playerTwoGoals;
        public int playerOneShots;
        public int playerTwoShots;
        public int shooterPlayer = 1;
        public int keeperPlayer = 2;
        public int[] playerOneShotResults = new int[MULTIPLAYER_SHOTS_PER_PLAYER];
        public int[] playerTwoShotResults = new int[MULTIPLAYER_SHOTS_PER_PLAYER];
        public MultiplayerPhase phase = MultiplayerPhase.KICKER_AIM;
        public double playerTagFadeTimerSeconds;
    }

    public static class EndlessScoreEntry {
        protected final String playerName;
        protected final int score;

        protected EndlessScoreEntry(String playerName, int score) {
            this.playerName = playerName;
            this.score = score;
        }
    }

    public static class EndlessState {
        public boolean dragging;
        public boolean ballMoving;
        public boolean keeperMoving;
        public boolean keeperFallingAfterCatch;
        public boolean keeperReturningToIdle;
        public boolean keeperJumping;
        public boolean keeperVerticalJump;
        public double anchorX;
        public double anchorY;
        public double velocityX;
        public double velocityY;
        public double targetX;
        public double targetY;
        public double shotSpeed;
        public double keeperTargetX;
        public double keeperTargetY;
        public double keeperFallStartX;
        public double keeperFallStartY;
        public double keeperFallTargetX;
        public double keeperFallTargetY;
        public double keeperFallElapsedSeconds;
        public double keeperMoveStartX;
        public double keeperMoveStartY;
        public double keeperMoveElapsedSeconds;
        public double keeperMoveDurationSeconds;
        public double keeperMoveArcHeight;
        public double keeperFallArcHeight;
        public double retroMotionAccumulatorSeconds;
        public double keeperRetroAccumulatorSeconds;
        public double ballRetroAccumulatorSeconds;
        public double keeperReturnTargetX;
        public double keeperReturnTargetY;
        public double learnedTargetX;
        public double learnedTargetY;
        public int scoredShotsLearned;
        public int score;
        public int lives;
        public int keeperDiveDirection;
        public int roundResult;
        public double roundResolveTimer;
        public double goalTextTimerSeconds;
        public boolean keeperWillCatch;
        public boolean roundResolving;
        public boolean awaitingKeeperAnimationFinish;
        public boolean gameOver;
        public int pendingRoundResult;
    }

    public static class TournamentState extends EndlessState {
        public int roundIndex;
        public int roundGoals;
        public int shotsTaken;
        public int totalGoals;
        public int teamCount;
        public String playerTeamName;
        public String[] opponents;
        public String[] fourTeamParticipants;
        public int fourTeamPlayerSlot = -1;
        public String[] eightTeamParticipants;
        public int eightTeamPlayerSlot = -1;
        public boolean setupDone;
        public boolean roundFinished;
        public boolean eliminated;
        public boolean champion;
    }

}
