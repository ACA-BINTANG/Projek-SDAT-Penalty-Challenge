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
import javafx.stage.Stage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import core.GameDataStructures.BotMatchEvent;
import core.GameDataStructures.BotMatchStage;
import core.GameDataStructures.BracketNode;
import core.GameDataStructures.KeeperPathGraph;
import core.GameDataStructures.KeeperPathNode;
import core.GameDataStructures.MultiplayerTurn;
import core.GameDataStructures.PlayerRecord;
import core.GameDataStructures.TournamentBracketTree;
import core.GameDataStructures.TutorialSnapshot;
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
    private static final KeeperPathGraph KEEPER_PATH_GRAPH = new KeeperPathGraph();

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

    protected Button createGameplayMenuButton(Stage stage) {
        String normalStyle =
                "-fx-background-color: #145AD8;" +
                "-fx-text-fill: white;" +
                "-fx-border-color: white;" +
                "-fx-border-width: 3;" +
                "-fx-background-radius: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-padding: 8 20 8 20;";
        String hoverStyle =
                "-fx-background-color: #2474F2;" +
                "-fx-text-fill: white;" +
                "-fx-border-color: white;" +
                "-fx-border-width: 3;" +
                "-fx-background-radius: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-padding: 8 20 8 20;";

        Button button = new Button("MENU");
        button.setFont(loadFont(MENU_FONT_PATH, 16, Font.font("Arial", FontWeight.EXTRA_BOLD, 16)));
        button.setCursor(Cursor.HAND);
        button.setFocusTraversable(false);
        button.setMinWidth(112);
        button.setMinHeight(42);
        button.setStyle(normalStyle);
        button.setOnMouseEntered(event -> button.setStyle(hoverStyle));
        button.setOnMouseExited(event -> button.setStyle(normalStyle));
        button.setOnAction(event -> showMenu(stage));
        return button;
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

        Text title = new Text("TEAM BRACKET");
        title.setFill(Color.WHITE);
        title.setFont(loadFont(MENU_FONT_PATH, 34, Font.font("Arial", FontWeight.EXTRA_BOLD, 34)));

        TextField teamNameInput = new TextField("PLAYER FC");
        teamNameInput.setPromptText("Your team name");
        teamNameInput.setMaxWidth(260);
        teamNameInput.setFont(Font.font("Arial", FontWeight.BOLD, 15));

        Button fourTeamButton = new Button("4 TEAMS");
        Button eightTeamButton = new Button("8 TEAMS");
        fourTeamButton.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        eightTeamButton.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        fourTeamButton.setCursor(Cursor.HAND);
        eightTeamButton.setCursor(Cursor.HAND);
        applyTournamentBlueButtonStyle(fourTeamButton);
        applyTournamentBlueButtonStyle(eightTeamButton);

        HBox teamCountBox = new HBox(10, fourTeamButton, eightTeamButton);
        teamCountBox.setAlignment(Pos.CENTER);

        // Area status hanya dipakai untuk pesan error, bukan petunjuk/tutorial.
        Text setupStatusText = new Text("");
        setupStatusText.setFill(Color.rgb(255, 230, 120));
        setupStatusText.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        setupStatusText.setVisible(false);
        setupStatusText.managedProperty().bind(setupStatusText.visibleProperty());

        VBox setupBox = new VBox(8, teamNameInput, teamCountBox, setupStatusText);
        setupBox.setAlignment(Pos.CENTER);

        StackPane bracketHolder = new StackPane();
        rebuildTournamentBracketBoard(bracketHolder, bracketLabels, 8);

        Button startMatchButton = new Button("START");
        startMatchButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        startMatchButton.setCursor(Cursor.HAND);
        applyTournamentBlueButtonStyle(startMatchButton);

        HBox actions = new HBox(12, startMatchButton);
        actions.setAlignment(Pos.CENTER);

        // Tidak ada teks tutorial/petunjuk di bawah tombol START/RESTART.
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
        // Gambar bracket baru berukuran 1600 x 800. Ukuran board mengikuti rasio
        // aslinya supaya gambar tidak tertarik/gepeng saat ditampilkan.
        double boardWidth = 920;
        double boardHeight = 460;
        double scaleX = boardWidth / 1600.0;
        double scaleY = boardHeight / 800.0;

        ImageView bracketImage = createImageView(TOURNAMENT_BRACKET_PATH);
        bracketImage.setPreserveRatio(false);
        bracketImage.setFitWidth(boardWidth);
        bracketImage.setFitHeight(boardHeight);

        Pane labels = new Pane();
        labels.setPrefSize(boardWidth, boardHeight);
        labels.setMinSize(boardWidth, boardHeight);
        labels.setMaxSize(boardWidth, boardHeight);
        labels.setMouseTransparent(true);

        // Susunan label mengikuti gambar Bagan-Tournament.png yang baru:
        // 0-7   = delapan peserta awal.
        // 8-11  = empat pemenang ronde pertama / quarter-final.
        // 12-13 = dua pemenang semifinal / finalis.
        // 14    = juara pada kotak tengah ber-outline emas.
        // Semua koordinat di bawah memakai koordinat asli gambar 1600 x 800.
        double[][] labelPositions = {
                // Tim awal - sisi kiri.
                {158, 178}, {158, 310}, {158, 491}, {158, 619},
                // Tim awal - sisi kanan.
                {1442, 178}, {1442, 310}, {1442, 491}, {1442, 619},
                // Empat pemenang ronde pertama.
                {468, 255}, {1133, 256}, {468, 554}, {1133, 554},
                // Dua finalis.
                {506, 403}, {1093, 403},
                // Juara.
                {800, 403}
        };

        // Lebar label disesuaikan dengan ruang kosong di dalam masing-masing kotak.
        double[] widths = {
                132, 132, 132, 132, 132, 132, 132, 132,
                116, 116, 116, 116,
                150, 150,
                132
        };

        for (int i = 0; i < labelPositions.length; i++) {
            double labelWidth = widths[i];
            Label label = createBracketLabel(labelWidth);

            if (i < 8) {
                label.setFont(loadFont(MENU_FONT_PATH, 9.5, Font.font("Arial", FontWeight.BOLD, 9.5)));
            } else if (i < 12) {
                label.setFont(loadFont(MENU_FONT_PATH, 9, Font.font("Arial", FontWeight.BOLD, 9)));
            } else if (i < 14) {
                label.setFont(loadFont(MENU_FONT_PATH, 9.5, Font.font("Arial", FontWeight.BOLD, 9.5)));
            } else {
                label.setFont(loadFont(MENU_FONT_PATH, 10, Font.font("Arial", FontWeight.BOLD, 10)));
            }

            double centerX = labelPositions[i][0] * scaleX;
            double centerY = labelPositions[i][1] * scaleY;
            double labelHeight = 24;
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

        syncTournamentBracketTree(state);
        if (state.bracketTree == null) {
            return;
        }

        String playerTeam = state.playerTeamName != null && !state.playerTeamName.isEmpty()
                ? state.playerTeamName
                : "PLAYER FC";

        // Bracket dirender melalui traversal Binary Tree, bukan lagi membaca label array secara langsung.
        for (BracketNode node : state.bracketTree.preOrder()) {
            if (!isTournamentTreeNodeVisible(state, node.labelIndex())) {
                continue;
            }
            String teamName = node.teamName();
            setBracketLabel(
                    bracketLabels,
                    node.labelIndex(),
                    teamName,
                    playerTeam.equals(teamName)
            );
        }
    }

    protected boolean isTournamentTreeNodeVisible(TournamentState state, int labelIndex) {
        boolean tournamentFinished = state.eliminated || state.champion;
        if (state.teamCount <= 4) {
            if (labelIndex <= 3) {
                return true;
            }
            if (labelIndex <= 5) {
                return state.roundIndex > 0 || tournamentFinished;
            }
            return tournamentFinished;
        }

        if (labelIndex <= 7) {
            return true;
        }
        if (labelIndex <= 11) {
            return state.roundIndex > 0 || tournamentFinished;
        }
        if (labelIndex <= 13) {
            return state.roundIndex > 1 || tournamentFinished;
        }
        return tournamentFinished;
    }

    protected void syncTournamentBracketTree(TournamentState state) {
        if (state == null) {
            return;
        }
        if (state.teamCount <= 4) {
            state.bracketTree = TournamentBracketTree.fourTeams(
                    state.fourTeamParticipants,
                    state.fourTeamSemifinalWinners,
                    state.tournamentChampionName
            );
        } else {
            state.bracketTree = TournamentBracketTree.eightTeams(
                    state.eightTeamParticipants,
                    state.eightTeamQuarterWinners,
                    state.eightTeamSemifinalWinners,
                    state.tournamentChampionName
            );
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

    /**
     * Mengubah urutan layer bola dan keeper tanpa mengganggu HUD atau overlay lain.
     * Bola berada di depan saat mulai menendang, lalu dipindahkan ke belakang keeper
     * ketika sudah memasuki area gawang agar tidak terlihat menembus karakter.
     */
    protected void moveBallBehindKeeper(ImageView ball, ImageView keeper) {
        swapActorLayerOrder(ball, keeper, false);
    }

    protected void moveBallInFrontOfKeeper(ImageView ball, ImageView keeper) {
        swapActorLayerOrder(ball, keeper, true);
    }

    private void swapActorLayerOrder(ImageView ball, ImageView keeper, boolean ballInFront) {
        if (ball == null || keeper == null || ball.getParent() == null || ball.getParent() != keeper.getParent()) {
            return;
        }
        if (!(ball.getParent() instanceof Pane pane)) {
            return;
        }

        int ballIndex = pane.getChildren().indexOf(ball);
        int keeperIndex = pane.getChildren().indexOf(keeper);
        if (ballIndex < 0 || keeperIndex < 0) {
            return;
        }

        boolean alreadyCorrect = ballInFront ? ballIndex > keeperIndex : ballIndex < keeperIndex;
        if (alreadyCorrect) {
            return;
        }

        // Jangan memakai Collections.swap() pada children JavaFX.
        // VetoableListDecorator bisa menganggap proses swap sebagai penambahan Node yang sama dua kali.
        // Pindahkan hanya Node bola, sehingga yang berubah cuma posisi Z relatif bola terhadap keeper.
        pane.getChildren().remove(ball);
        int currentKeeperIndex = pane.getChildren().indexOf(keeper);
        if (currentKeeperIndex < 0) {
            return;
        }

        int targetIndex = ballInFront
                ? Math.min(currentKeeperIndex + 1, pane.getChildren().size())
                : currentKeeperIndex;
        pane.getChildren().add(targetIndex, ball);
    }

    protected void playTournamentVictoryAnimation(VBox resultBox, Text resultTitle, boolean champion) {
        if (resultBox == null || resultTitle == null) {
            return;
        }

        resultBox.setOpacity(0);
        resultBox.setScaleX(0.72);
        resultBox.setScaleY(0.72);
        resultTitle.setScaleX(1);
        resultTitle.setScaleY(1);
        resultTitle.setFill(champion ? Color.rgb(255, 220, 55) : Color.WHITE);

        javafx.animation.FadeTransition fade = new javafx.animation.FadeTransition(
                javafx.util.Duration.millis(220), resultBox
        );
        fade.setFromValue(0);
        fade.setToValue(1);

        javafx.animation.ScaleTransition pop = new javafx.animation.ScaleTransition(
                javafx.util.Duration.millis(300), resultBox
        );
        pop.setFromX(0.72);
        pop.setFromY(0.72);
        pop.setToX(1.0);
        pop.setToY(1.0);

        javafx.animation.ParallelTransition intro = new javafx.animation.ParallelTransition(fade, pop);
        intro.play();

        javafx.animation.ScaleTransition titlePulse = new javafx.animation.ScaleTransition(
                javafx.util.Duration.millis(champion ? 330 : 260), resultTitle
        );
        titlePulse.setFromX(1.0);
        titlePulse.setFromY(1.0);
        titlePulse.setToX(champion ? 1.16 : 1.08);
        titlePulse.setToY(champion ? 1.16 : 1.08);
        titlePulse.setAutoReverse(true);
        titlePulse.setCycleCount(champion ? 6 : 2);
        titlePulse.play();
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
            maxLength = 14;
        } else if (index >= 12 && index <= 14) {
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

        int playerSlot = random.nextInt(4);
        String[] participants = new String[4];
        participants[playerSlot] = playerTeam;

        int opponentIndex = 0;
        for (int i = 0; i < participants.length; i++) {
            if (i != playerSlot) {
                participants[i] = randomOpponents[opponentIndex++];
            }
        }

        int playerSide = playerSlot < 2 ? 0 : 1;
        int semifinalOpponentSlot = playerSlot % 2 == 0 ? playerSlot + 1 : playerSlot - 1;
        int otherPairStart = playerSide == 0 ? 2 : 0;

        state.botMatchQueue.clear();
        state.fourTeamParticipants = participants;
        state.fourTeamPlayerSlot = playerSlot;
        state.fourTeamSemifinalWinners = new String[2];
        state.eightTeamQuarterWinners = null;
        state.eightTeamSemifinalWinners = null;
        state.tournamentChampionName = null;

        // Pertandingan bot dimasukkan ke Queue lalu diproses sesuai urutan FIFO.
        enqueueTournamentBotMatch(
                state,
                BotMatchStage.FOUR_TEAM_SEMIFINAL,
                1 - playerSide,
                participants[otherPairStart],
                participants[otherPairStart + 1]
        );
        processTournamentBotQueue(state);

        state.opponents = new String[] {
                participants[semifinalOpponentSlot],
                state.fourTeamSemifinalWinners[1 - playerSide]
        };
        syncTournamentBracketTree(state);
    }

    protected void prepareEightTeamBracket(TournamentState state) {
        String playerTeam = state.playerTeamName == null || state.playerTeamName.trim().isEmpty()
                ? "PLAYER FC"
                : state.playerTeamName.trim();
        String[] randomOpponents = createTournamentOpponents(8);

        int playerSlot = random.nextInt(8);
        String[] participants = new String[8];
        participants[playerSlot] = playerTeam;

        int opponentIndex = 0;
        for (int i = 0; i < participants.length; i++) {
            if (i != playerSlot) {
                participants[i] = randomOpponents[opponentIndex++];
            }
        }

        int playerPair = playerSlot / 2;
        int playerSide = playerSlot < 4 ? 0 : 1;
        int quarterFinalOpponentSlot = playerSlot % 2 == 0 ? playerSlot + 1 : playerSlot - 1;

        state.botMatchQueue.clear();
        state.eightTeamParticipants = participants;
        state.eightTeamPlayerSlot = playerSlot;
        state.fourTeamSemifinalWinners = null;
        state.tournamentChampionName = null;
        state.eightTeamQuarterWinners = new String[4];
        for (int pair = 0; pair < 4; pair++) {
            if (pair == playerPair) {
                continue;
            }
            int pairStart = pair * 2;
            enqueueTournamentBotMatch(
                    state,
                    BotMatchStage.EIGHT_TEAM_QUARTER_FINAL,
                    pair,
                    participants[pairStart],
                    participants[pairStart + 1]
            );
        }
        processTournamentBotQueue(state);

        state.eightTeamSemifinalWinners = new String[2];
        int oppositeSide = 1 - playerSide;
        int oppositePairStart = oppositeSide == 0 ? 0 : 2;
        enqueueTournamentBotMatch(
                state,
                BotMatchStage.EIGHT_TEAM_SEMIFINAL,
                oppositeSide,
                state.eightTeamQuarterWinners[oppositePairStart],
                state.eightTeamQuarterWinners[oppositePairStart + 1]
        );
        processTournamentBotQueue(state);

        int otherPair = getEightTeamOtherPairOnSameSide(playerPair);
        String semifinalOpponent = state.eightTeamQuarterWinners[otherPair];
        String finalOpponent = state.eightTeamSemifinalWinners[oppositeSide];

        state.opponents = new String[] {
                participants[quarterFinalOpponentSlot],
                semifinalOpponent,
                finalOpponent
        };
        syncTournamentBracketTree(state);
    }

    protected void recordTournamentRoundOutcome(TournamentState state, boolean playerWon) {
        String playerTeam = state.playerTeamName == null || state.playerTeamName.trim().isEmpty()
                ? "PLAYER FC"
                : state.playerTeamName.trim();

        if (state.teamCount <= 4) {
            if (state.fourTeamSemifinalWinners == null || state.fourTeamSemifinalWinners.length != 2) {
                state.fourTeamSemifinalWinners = new String[2];
            }
            int playerSide = state.fourTeamPlayerSlot < 2 ? 0 : 1;

            if (state.roundIndex == 0) {
                state.fourTeamSemifinalWinners[playerSide] = playerWon
                        ? playerTeam
                        : getTournamentOpponent(state, 0);

                if (!playerWon) {
                    enqueueTournamentBotMatch(
                            state,
                            BotMatchStage.FINAL,
                            0,
                            state.fourTeamSemifinalWinners[0],
                            state.fourTeamSemifinalWinners[1]
                    );
                    processTournamentBotQueue(state);
                }
            } else {
                state.tournamentChampionName = playerWon
                        ? playerTeam
                        : getTournamentOpponent(state, 1);
            }
            syncTournamentBracketTree(state);
            return;
        }

        if (state.eightTeamQuarterWinners == null || state.eightTeamQuarterWinners.length != 4) {
            state.eightTeamQuarterWinners = new String[4];
        }
        if (state.eightTeamSemifinalWinners == null || state.eightTeamSemifinalWinners.length != 2) {
            state.eightTeamSemifinalWinners = new String[2];
        }

        int playerPair = state.eightTeamPlayerSlot / 2;
        int playerSide = state.eightTeamPlayerSlot < 4 ? 0 : 1;

        if (state.roundIndex == 0) {
            state.eightTeamQuarterWinners[playerPair] = playerWon
                    ? playerTeam
                    : getTournamentOpponent(state, 0);

            if (!playerWon) {
                int sidePairStart = playerSide == 0 ? 0 : 2;
                enqueueTournamentBotMatch(
                        state,
                        BotMatchStage.EIGHT_TEAM_SEMIFINAL,
                        playerSide,
                        state.eightTeamQuarterWinners[sidePairStart],
                        state.eightTeamQuarterWinners[sidePairStart + 1]
                );
                processTournamentBotQueue(state);

                enqueueTournamentBotMatch(
                        state,
                        BotMatchStage.FINAL,
                        0,
                        state.eightTeamSemifinalWinners[0],
                        state.eightTeamSemifinalWinners[1]
                );
                processTournamentBotQueue(state);
            }
        } else if (state.roundIndex == 1) {
            state.eightTeamSemifinalWinners[playerSide] = playerWon
                    ? playerTeam
                    : getTournamentOpponent(state, 1);

            if (!playerWon) {
                enqueueTournamentBotMatch(
                        state,
                        BotMatchStage.FINAL,
                        0,
                        state.eightTeamSemifinalWinners[0],
                        state.eightTeamSemifinalWinners[1]
                );
                processTournamentBotQueue(state);
            }
        } else {
            state.tournamentChampionName = playerWon
                    ? playerTeam
                    : getTournamentOpponent(state, 2);
        }
        syncTournamentBracketTree(state);
    }

    protected void enqueueTournamentBotMatch(
            TournamentState state,
            BotMatchStage stage,
            int destinationIndex,
            String teamA,
            String teamB
    ) {
        if (state == null || teamA == null || teamB == null) {
            return;
        }
        state.botMatchQueue.offer(new BotMatchEvent(stage, destinationIndex, teamA, teamB));
    }

    protected void processTournamentBotQueue(TournamentState state) {
        while (state != null && !state.botMatchQueue.isEmpty()) {
            BotMatchEvent event = state.botMatchQueue.poll();
            String winner = random.nextBoolean() ? event.teamA() : event.teamB();

            switch (event.stage()) {
                case FOUR_TEAM_SEMIFINAL ->
                        state.fourTeamSemifinalWinners[event.destinationIndex()] = winner;
                case EIGHT_TEAM_QUARTER_FINAL ->
                        state.eightTeamQuarterWinners[event.destinationIndex()] = winner;
                case EIGHT_TEAM_SEMIFINAL ->
                        state.eightTeamSemifinalWinners[event.destinationIndex()] = winner;
                case FINAL -> state.tournamentChampionName = winner;
            }
        }
        syncTournamentBracketTree(state);
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

    protected void initializeMultiplayerTurnOrder(MultiplayerState state) {
        if (state.turnOrder.isEmpty()) {
            state.turnOrder.add(new MultiplayerTurn(1, 2));
            state.turnOrder.add(new MultiplayerTurn(2, 1));
        }
        MultiplayerTurn currentTurn = state.turnOrder.getFirst();
        state.shooterPlayer = currentTurn.shooterPlayer();
        state.keeperPlayer = currentTurn.keeperPlayer();
    }

    protected void rotateMultiplayerTurnOrder(MultiplayerState state) {
        initializeMultiplayerTurnOrder(state);
        MultiplayerTurn completedTurn = state.turnOrder.removeFirst();
        state.turnOrder.addLast(completedTurn);
        MultiplayerTurn nextTurn = state.turnOrder.getFirst();
        state.shooterPlayer = nextTurn.shooterPlayer();
        state.keeperPlayer = nextTurn.keeperPlayer();
    }

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

        initializeMultiplayerTurnOrder(state);
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
        state.ballBehindKeeper = false;
        moveBallInFrontOfKeeper(ball, keeper);
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
        label.setFont(Font.font("Arial Black", FontWeight.EXTRA_BOLD, 21));
        label.setStroke(Color.rgb(0, 0, 0, 0.98));
        label.setStrokeWidth(2.5);
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
        tag.setStroke(Color.rgb(0, 0, 0, 0.98));
        tag.setStrokeWidth(2.6);
        tag.setFont(Font.font("Arial Black", FontWeight.EXTRA_BOLD, 26));
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
        roleText.setText("PLAYER " + state.shooterPlayer + " SHOOTER  |  PLAYER " + state.keeperPlayer + " KEEPER");
        scoreText.setText("P1: " + state.playerOneGoals + "/" + MULTIPLAYER_SHOTS_PER_PLAYER
                + "    P2: " + state.playerTwoGoals + "/" + MULTIPLAYER_SHOTS_PER_PLAYER);
        shotText.setText("SHOT P1: " + state.playerOneShots + "/" + MULTIPLAYER_SHOTS_PER_PLAYER
                + "  |  SHOT P2: " + state.playerTwoShots + "/" + MULTIPLAYER_SHOTS_PER_PLAYER);

        if (state.phase == MultiplayerPhase.KICKER_AIM) {
            hintText.setText("PLAYER " + state.shooterPlayer + ": move the aim line, then release. The ball direction stays hidden.");
        } else if (state.phase == MultiplayerPhase.KEEPER_AIM) {
            hintText.setText("PLAYER " + state.keeperPlayer + ": click the goal area to aim the keeper.");
        } else if (state.phase == MultiplayerPhase.EXECUTING) {
            hintText.setText("ACTION: the ball and keeper are moving.");
        } else if (state.phase == MultiplayerPhase.ROUND_DELAY) {
            hintText.setText("Round complete. Wait for the next round.");
        } else {
            hintText.setText("MATCH OVER.");
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

        // Saat bola sudah masuk area gawang, render bola di belakang keeper.
        // Ini mencegah bola terlihat menembus badan keeper ketika keduanya bertumpuk.
        if (!state.ballBehindKeeper
                && isPointInsidePointBox(root, getCenterX(ball), getCenterY(ball))) {
            moveBallBehindKeeper(ball, keeper);
            state.ballBehindKeeper = true;
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
            state.phase = MultiplayerPhase.ROUND_DELAY;
            hintText.setText("GOAL FOR PLAYER " + state.shooterPlayer + ". Wait for the next round.");
            triggerGoalText(goalText, state);
            queueRoundResolutionAfterKeeperAnimation(state, ROUND_RESULT_GOAL);
            return;
        }

        if (outsideScreen || reachedTarget) {
            state.phase = MultiplayerPhase.ROUND_DELAY;
            hintText.setText("SHOT MISSED. Wait for the next round.");
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

            ImageView resultTrophy = null;
            Object trophyNode = resultBox.getProperties().get("resultTrophy");
            if (trophyNode instanceof ImageView) {
                resultTrophy = (ImageView) trophyNode;
            }

            boolean hasWinner = state.playerOneGoals != state.playerTwoGoals;
            if (state.playerOneGoals > state.playerTwoGoals) {
                resultTitle.setText("PLAYER 1 VICTORY");
            } else if (state.playerTwoGoals > state.playerOneGoals) {
                resultTitle.setText("PLAYER 2 VICTORY");
            } else {
                resultTitle.setText("DRAW");
            }

            if (resultTrophy != null) {
                resultTrophy.setVisible(hasWinner);
                resultTrophy.setManaged(hasWinner);
            }
            resultTitle.setFill(hasWinner ? Color.rgb(255, 220, 55) : Color.WHITE);
            resultTitle.setFont(Font.font("Arial Black", FontWeight.EXTRA_BOLD, hasWinner ? 50 : 46));
            resultDetail.setText("PLAYER 1: " + state.playerOneGoals + "/" + MULTIPLAYER_SHOTS_PER_PLAYER
                    + "\nPLAYER 2: " + state.playerTwoGoals + "/" + MULTIPLAYER_SHOTS_PER_PLAYER);
            resultOverlay.setVisible(true);
            resultBox.setVisible(true);
            playTournamentVictoryAnimation(resultBox, resultTitle, hasWinner);
            return;
        }

        // Rotasi giliran menggunakan LinkedList: elemen pertama dipindah ke belakang.
        rotateMultiplayerTurnOrder(state);
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

        // Saat bola memasuki area gawang, pindahkan ke belakang keeper agar
        // sprite bola tidak terlihat menembus badan karakter.
        if (!state.ballBehindKeeper
                && isPointInsidePointBox(root, getCenterX(ball), getCenterY(ball))) {
            moveBallBehindKeeper(ball, keeper);
            state.ballBehindKeeper = true;
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
        ImageView resultTrophy = null;
        Object trophyNode = resultBox.getProperties().get("resultTrophy");
        if (trophyNode instanceof ImageView) {
            resultTrophy = (ImageView) trophyNode;
        }

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
            recordTournamentRoundOutcome(state, true);
            state.champion = finalRound;
            state.eliminated = false;
            updateTournamentBracketLabels(bracketLabels, state);

            // Piala hanya muncul ketika pemain benar-benar memenangkan FINAL.
            if (resultTrophy != null) {
                resultTrophy.setVisible(finalRound);
                resultTrophy.setManaged(finalRound);
            }

            if (finalRound) {
                resultTitle.setFill(Color.rgb(255, 220, 55));
                resultTitle.setFont(Font.font("Arial Black", FontWeight.EXTRA_BOLD, 58));
                resultTitle.setText("VICTORY");
                resultDetail.setText("");
                resultDetail.setVisible(false);
                resultDetail.setManaged(false);
            } else {
                // Quarter Final dan Semi Final cukup menampilkan data hasil ronde.
                resultTitle.setFill(Color.WHITE);
                resultTitle.setFont(Font.font("Arial Black", FontWeight.EXTRA_BOLD, 42));
                resultTitle.setText("RESULT - " + getTournamentRoundName(state));
                int missedShots = Math.max(0, state.shotsTaken - state.roundGoals);
                resultDetail.setText(
                        "TARGET: " + target + " GOALS"
                                + "\nGOALS: " + state.roundGoals
                                + "\nMISS: " + missedShots
                );
                resultDetail.setVisible(true);
                resultDetail.setManaged(true);
            }

            primaryButton.setText("CONTINUE");
            ball.setCursor(Cursor.DEFAULT);
            resultOverlay.setVisible(true);
            resultBox.setVisible(true);
            playTournamentVictoryAnimation(resultBox, resultTitle, finalRound);

            if (finalRound) {
                startMatchButton.setText("RESTART");
            }
            return;
        }

        if (impossibleToQualify || shotsFinished) {
            state.roundFinished = true;
            recordTournamentRoundOutcome(state, false);
            state.eliminated = true;
            state.champion = false;
            updateTournamentBracketLabels(bracketLabels, state);

            // Saat kalah, tampilkan tulisan besar agar langsung terlihat, plus data ronde.
            if (resultTrophy != null) {
                resultTrophy.setVisible(false);
                resultTrophy.setManaged(false);
            }
            resultTitle.setFill(Color.rgb(245, 35, 35));
            resultTitle.setFont(Font.font("Arial Black", FontWeight.EXTRA_BOLD, 78));
            resultTitle.setText("DEFEAT");
            int missedShots = Math.max(0, state.shotsTaken - state.roundGoals);
            resultDetail.setText(
                    "TARGET: " + target + " GOALS"
                            + "\nGOALS: " + state.roundGoals
                            + "\nMISS: " + missedShots
            );
            resultDetail.setVisible(true);
            resultDetail.setManaged(true);
            primaryButton.setText("CONTINUE");
            ball.setCursor(Cursor.DEFAULT);
            resultOverlay.setVisible(true);
            resultBox.setVisible(true);
            return;
        }

        resetRound.run();
    }

    protected void updateTournamentTexts(Text roundText, Text targetText, Text shotsText, TournamentState state) {
        int target = getTournamentTarget(state);
        int shotsLeft = TOURNAMENT_SHOTS_PER_ROUND - state.shotsTaken;
        roundText.setText(getTournamentRoundName(state));
        targetText.setText("TARGET: " + state.roundGoals + "/" + target);
        shotsText.setText("LIVES: " + shotsLeft);
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

    protected void pushTutorialHistory(TutorialState state, Text titleText, Text hintText) {
        state.undoHistory.push(new TutorialSnapshot(
                state.phase.name(),
                state.tutorialComplete,
                titleText.getText(),
                hintText.getText()
        ));
        state.redoHistory.clear();
    }

    protected TutorialSnapshot undoTutorialHistory(TutorialState state, Text titleText, Text hintText) {
        if (state.undoHistory.isEmpty()) {
            return null;
        }
        state.redoHistory.push(new TutorialSnapshot(
                state.phase.name(),
                state.tutorialComplete,
                titleText.getText(),
                hintText.getText()
        ));
        return state.undoHistory.pop();
    }

    protected TutorialSnapshot redoTutorialHistory(TutorialState state, Text titleText, Text hintText) {
        if (state.redoHistory.isEmpty()) {
            return null;
        }
        state.undoHistory.push(new TutorialSnapshot(
                state.phase.name(),
                state.tutorialComplete,
                titleText.getText(),
                hintText.getText()
        ));
        return state.redoHistory.pop();
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
                        // Simpan state lama ke Stack agar Ctrl+Z dapat kembali ke tahap penendang.
                        pushTutorialHistory(state, titleText, hintText);
                        state.phase = TutorialPhase.KEEPER_AIM;
                        titleText.setText("TUTORIAL - KEEPER");
                        resetRound.run();
                        hintText.setText("GREAT! Now play as the KEEPER. Click the yellow circle inside the goal to choose the catch position.");
                    } else {
                        resetRound.run();
                        hintText.setText("NO GOAL YET. Hold the ball inside the yellow circle and aim at the yellow circle inside the goal.");
                    }
                } else if (state.phase == TutorialPhase.KEEPER_EXECUTING) {
                    if (result == ROUND_RESULT_SAVED) {
                        showTutorialCompletePopup.run();
                    } else {
                        state.phase = TutorialPhase.KEEPER_AIM;
                        resetRound.run();
                        hintText.setText("NOT SAVED YET. Click directly inside the yellow circle in the goal.");
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
        state.ballBehindKeeper = false;
        moveBallInFrontOfKeeper(ball, keeper);
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
        livesText.setText("LIVES: " + Math.max(state.lives, 0));
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
                    String entryId = parts[0].trim();
                    String playerName = parts[parts.length - 2].trim();
                    int score = Integer.parseInt(parts[parts.length - 1].trim());
                    if (entryId.isEmpty()) {
                        entryId = "legacy-" + entries.size();
                    }
                    if (playerName.isEmpty()) {
                        playerName = "PLAYER";
                    }
                    entries.add(new EndlessScoreEntry(entryId, playerName, score));
                } catch (NumberFormatException ignored) {
                    // Abaikan baris skor yang rusak agar scoreboard tetap dapat dibuka.
                }
            }
        } catch (IOException ignored) {
            return new ArrayList<>();
        }

        entries.sort((left, right) -> Integer.compare(right.score, left.score));
        if (entries.size() > MAX_ENDLESS_SCOREBOARD_ENTRIES) {
            return new ArrayList<>(entries.subList(0, MAX_ENDLESS_SCOREBOARD_ENTRIES));
        }
        return entries;
    }

    protected List<PlayerRecord> loadPlayerRecords() {
        List<EndlessScoreEntry> entries = loadEndlessScoreboardEntries();
        List<PlayerRecord> players = new ArrayList<>();
        for (EndlessScoreEntry entry : entries) {
            int level = Math.max(1, entry.score / 5 + 1);
            String status;
            if (entry.score >= 20) {
                status = "ELITE";
            } else if (entry.score >= 10) {
                status = "PRO";
            } else {
                status = "ROOKIE";
            }
            players.add(new PlayerRecord(
                    entry.entryId,
                    entry.playerName,
                    entry.score,
                    level,
                    status
            ));
        }
        return players;
    }

    protected HashMap<String, PlayerRecord> indexPlayersById(List<PlayerRecord> players) {
        HashMap<String, PlayerRecord> playerById = new HashMap<>();
        for (PlayerRecord player : players) {
            playerById.put(player.id(), player);
        }
        return playerById;
    }

    protected HashMap<String, Integer> indexPlayerRanksById(List<PlayerRecord> players) {
        HashMap<String, Integer> rankById = new HashMap<>();
        for (int i = 0; i < players.size(); i++) {
            rankById.put(players.get(i).id(), i);
        }
        return rankById;
    }

    protected String formatEndlessScoreboardPage(List<EndlessScoreEntry> entries, int page, int pageSize) {
        if (entries.isEmpty()) {
            return "No scores saved yet.";
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

    protected String saveTopScore(String playerName, int score) throws IOException {
        Path scorePath = Path.of(TOP_SCORE_PATH).toAbsolutePath().normalize();
        String cleanName = playerName.replace("\t", " ").replace("\r", " ").replace("\n", " ");
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        String entryId = timestamp + "-" + Long.toUnsignedString(System.nanoTime());
        String line = entryId + "\t" + cleanName + "\t" + score + System.lineSeparator();
        Files.writeString(
                scorePath,
                line,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
        );

        // Simpan hanya 30 skor tertinggi agar scoreboard maksimal berisi 30 pemain.
        List<EndlessScoreEntry> topEntries = loadEndlessScoreboardEntries();
        StringBuilder savedScores = new StringBuilder();
        for (EndlessScoreEntry entry : topEntries) {
            savedScores.append(entry.entryId)
                    .append('\t')
                    .append(entry.playerName.replace("\t", " ").replace("\r", " ").replace("\n", " "))
                    .append('\t')
                    .append(entry.score)
                    .append(System.lineSeparator());
        }
        Files.writeString(
                scorePath,
                savedScores.toString(),
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );
        return entryId;
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
        double readChance = Math.min(KEEPER_MAX_READ_CHANCE, KEEPER_INITIAL_READ_CHANCE + state.score * KEEPER_READ_GROWTH_PER_POINT);
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

    protected double getKeeperSensorRotationDegrees(int direction, int logicalFrameNumber) {
        if (direction == 0) {
            return 0;
        }

        // JavaFX memakai rotasi positif searah jarum jam.
        // Dive kanan harus miring naik ke kanan (nilai negatif),
        // sedangkan dive kiri adalah kebalikannya.
        if (logicalFrameNumber == 3) {
            return -direction * KEEPER_DIVE_SENSOR_FRAME_3_ROTATION;
        }
        if (logicalFrameNumber == 4) {
            return -direction * KEEPER_DIVE_SENSOR_FRAME_4_ROTATION;
        }
        return 0;
    }

    protected KeeperSensorBox getKeeperSensorBoxAt(double keeperCenterX, double keeperCenterY, int direction, int logicalFrameNumber) {
        double sensorWidth = getKeeperSensorWidth(direction, logicalFrameNumber);
        double sensorHeight = getKeeperSensorHeight(direction, logicalFrameNumber);
        double sensorCenterX = keeperCenterX + getKeeperSensorOffsetX(direction);
        double sensorCenterY = keeperCenterY + getKeeperSensorOffsetY(direction);
        double rotationDegrees = getKeeperSensorRotationDegrees(direction, logicalFrameNumber);

        return new KeeperSensorBox(
                sensorCenterX - sensorWidth / 2,
                sensorCenterY - sensorHeight / 2,
                sensorWidth,
                sensorHeight,
                rotationDegrees
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

        double sensorCenterX = sensorBox.x + sensorBox.width / 2;
        double sensorCenterY = sensorBox.y + sensorBox.height / 2;
        double dx = pointX - sensorCenterX;
        double dy = pointY - sensorCenterY;

        // Putar titik ke arah berlawanan dari rotasi sensor, lalu cek
        // terhadap kotak lokal. Dengan begitu collision ikut miring
        // sama seperti sprite keeper, bukan memakai AABB tegak lurus.
        double radians = Math.toRadians(sensorBox.rotationDegrees);
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);
        double localX = dx * cos + dy * sin;
        double localY = -dx * sin + dy * cos;

        return Math.abs(localX) <= sensorBox.width / 2
                && Math.abs(localY) <= sensorBox.height / 2;
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
        keeperBoxOverlay.setRotate(sensorBox.rotationDegrees);
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

    protected KeeperPathNode resolveKeeperPathNode(StackPane root, double x) {
        double minX = getKeeperMovementMinX(root);
        double maxX = getKeeperMovementMaxX(root);
        double ratio = clamp((x - minX) / Math.max(1, maxX - minX), 0, 1);
        if (ratio < 0.18) {
            return KeeperPathNode.FAR_LEFT;
        }
        if (ratio < 0.40) {
            return KeeperPathNode.LEFT;
        }
        if (ratio < 0.60) {
            return KeeperPathNode.CENTER;
        }
        if (ratio < 0.82) {
            return KeeperPathNode.RIGHT;
        }
        return KeeperPathNode.FAR_RIGHT;
    }

    protected double getKeeperPathNodeX(StackPane root, KeeperPathNode node) {
        double minX = getKeeperMovementMinX(root);
        double maxX = getKeeperMovementMaxX(root);
        double ratio = switch (node) {
            case FAR_LEFT -> 0.0;
            case LEFT -> 0.25;
            case CENTER -> 0.5;
            case RIGHT -> 0.75;
            case FAR_RIGHT -> 1.0;
        };
        return lerp(minX, maxX, ratio);
    }

    protected double interpolateKeeperGraphPathX(
            StackPane root,
            EndlessState state,
            double progress
    ) {
        if (state.keeperGraphPath == null || state.keeperGraphPath.size() < 2) {
            return lerp(state.keeperMoveStartX, state.keeperTargetX, progress);
        }

        int segmentCount = state.keeperGraphPath.size() - 1;
        double scaledProgress = clamp(progress, 0, 1) * segmentCount;
        int segmentIndex = Math.min(segmentCount - 1, (int) Math.floor(scaledProgress));
        double localProgress = scaledProgress - segmentIndex;

        double startX = segmentIndex == 0
                ? state.keeperMoveStartX
                : getKeeperPathNodeX(root, state.keeperGraphPath.get(segmentIndex));
        double endX = segmentIndex == segmentCount - 1
                ? state.keeperTargetX
                : getKeeperPathNodeX(root, state.keeperGraphPath.get(segmentIndex + 1));
        return lerp(startX, endX, easeInOutSine(localProgress));
    }

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

        // Graph + BFS menentukan urutan zona yang dilewati keeper bot.
        KeeperPathNode startNode = resolveKeeperPathNode(root, startX);
        KeeperPathNode targetNode = resolveKeeperPathNode(root, state.keeperTargetX);
        state.keeperGraphPath = new ArrayList<>(KEEPER_PATH_GRAPH.shortestPath(startNode, targetNode));

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
            double moveProgress = easeOutQuad(progress);
            x = interpolateKeeperGraphPathX(root, state, moveProgress);
            y = lerp(state.keeperMoveStartY, state.keeperTargetY, moveProgress);
        } else {
            double moveProgress = easeInOutSine(progress);
            double baseY = lerp(state.keeperMoveStartY, state.keeperTargetY, moveProgress);
            x = interpolateKeeperGraphPathX(root, state, moveProgress);
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
        protected final double rotationDegrees;

        protected KeeperSensorBox(double x, double y, double width, double height, double rotationDegrees) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.rotationDegrees = rotationDegrees;
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
        public final Stack<TutorialSnapshot> undoHistory = new Stack<>();
        public final Stack<TutorialSnapshot> redoHistory = new Stack<>();
    }

    public static class MultiplayerState extends EndlessState {
        public int playerOneGoals;
        public int playerTwoGoals;
        public int playerOneShots;
        public int playerTwoShots;
        public int shooterPlayer = 1;
        public int keeperPlayer = 2;
        public final LinkedList<MultiplayerTurn> turnOrder = new LinkedList<>();
        public int[] playerOneShotResults = new int[MULTIPLAYER_SHOTS_PER_PLAYER];
        public int[] playerTwoShotResults = new int[MULTIPLAYER_SHOTS_PER_PLAYER];
        public MultiplayerPhase phase = MultiplayerPhase.KICKER_AIM;
        public double playerTagFadeTimerSeconds;
    }

    public static class EndlessScoreEntry {
        public final String entryId;
        public final String playerName;
        public final int score;

        protected EndlessScoreEntry(String entryId, String playerName, int score) {
            this.entryId = entryId;
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
        public boolean ballBehindKeeper;
        public List<KeeperPathNode> keeperGraphPath = new ArrayList<>();
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
        public String[] fourTeamSemifinalWinners;
        public String[] eightTeamQuarterWinners;
        public String[] eightTeamSemifinalWinners;
        public String tournamentChampionName;
        public boolean setupDone;
        public boolean roundFinished;
        public boolean eliminated;
        public boolean champion;
        public final Queue<BotMatchEvent> botMatchQueue = new LinkedList<>();
        public TournamentBracketTree bracketTree;
    }

}
