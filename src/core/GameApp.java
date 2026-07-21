package core;

import modes.coop.CoopMode;
import modes.endless.EndlessMode;
import modes.tournament.TournamentMode;
import modes.tutorial.TutorialMode;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.AudioClip;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * BAGIAN APLIKASI UTAMA.
 *
 * Tugas class ini dibuat sederhana:
 * 1. Menjalankan intro.
 * 2. Menampilkan menu utama.
 * 3. Mengatur audio utama.
 * 4. Memindahkan pemain ke mode yang dipilih.
 *
 * Logika permainan yang dipakai bersama ada di GameEngine.
 */
public class GameApp extends Application {
    protected static final String INTRO_VIDEO_PATH = "resources/video/Intro-Awal.mp4";
    protected static final String INTRO_SOUND_PATH = "resources/audio/Sound-Intro.mp3";
    protected static final String INTRO_MUSIC_PATH = "resources/audio/music-intro.MP3";
    protected static final String MENU_AUDIO_PATH = "resources/audio/Sound-Menu.mp3";
    protected static final String MENU_CLICK_AUDIO_PATH = "resources/audio/menu-click.wav";
    protected static final String AUDIO_DEFAULT_PATH = "resources/audio/default.MP3";
    protected static final String AUDIO_GOAL_PATH = "resources/audio/goal.MP3";
    protected static final String AUDIO_MISSED_PATH = "resources/audio/missed.MP3";
    protected static final String MENU_BACKGROUND_PATH = "resources/images/Tampilan-BG-Menu.png";
    protected static final String MENU_BOX_OFF_PATH = "resources/images/No-selected.png";
    protected static final String MENU_BOX_ON_PATH = "resources/images/Selected.png";
    protected static final String MENU_LABEL_TOURNAMENT_PATH = "resources/images/Tournament.png";
    protected static final String MENU_LABEL_MULTIPLAYER_PATH = "resources/images/Co-op.png";
    protected static final String MENU_LABEL_ENDLESS_PATH = "resources/images/Endless.png";
    protected static final String MENU_LABEL_TUTORIAL_PATH = "resources/images/Tutorial.png";
    protected static final String MENU_LABEL_EXIT_PATH = "resources/images/Exit.png";
    protected static final String TOURNAMENT_BACKGROUND_PATH = "resources/images/Tampilan-BG-Turnament.png";
    protected static final String TOURNAMENT_BRACKET_PATH = "resources/images/Bagan-Tournament.png";
    protected static final String TOURNAMENT_4_BRACKET_PATH = "resources/images/Bagan-Tournament-4-Tim.png";
    protected static final String GOAL_BACKGROUND_PATH = "resources/images/GAWANG.png";
    protected static final String BALL_IMAGE_PATH = "resources/images/BOLA.png";
    protected static final String KEEPER_IMAGE_PATH = "resources/images/karakter/keper/berdiri/1.png";
    protected static final String KEEPER_IDLE_IMAGE_PATH = "resources/images/karakter/keper/berdiri/1.png";
    protected static final String KEEPER_STANDING_CATCH_IMAGE_PATH = "resources/images/karakter/keper/berdiri tangkap/2.png";
    protected static final String KEEPER_RIGHT_FOLDER = "resources/images/karakter/keper/lompat kanan";
    protected static final String KEEPER_RIGHT_CATCH_FOLDER = "resources/images/karakter/keper/lompatkanantangkap";
    protected static final String KEEPER_LEFT_FOLDER = "resources/images/karakter/keper/lompat kiri";
    protected static final String KEEPER_LEFT_CATCH_FOLDER = "resources/images/karakter/keper/lompattangkapkiri";
    protected static final String KEEPER_UP_FOLDER = "resources/images/karakter/keper/loncat";
    protected static final String KEEPER_UP_CATCH_FOLDER = "resources/images/karakter/keper/loncat tangkap";
    protected static final String TOP_SCORE_PATH = "top_scores.txt";
    protected static final String START_FONT_PATH = "resources/fonts/MinecraftBoldItalic-1y1e.otf";
    protected static final String MENU_FONT_PATH = "resources/fonts/MinecraftRegular-Bmg3.otf";
    protected static final double MENU_OPTION_WIDTH = 286.5;
    protected static final double MENU_OPTION_HEIGHT = 59.5;
    protected static final double BALL_SIZE = 92;
    protected static final double KEEPER_SIZE = 260;
    protected static final double GAME_DEFAULT_AUDIO_VOLUME = 0.42;
    protected static final double GAME_DEFAULT_DUCK_VOLUME = 0.14;
    protected static final double GAME_EFFECT_AUDIO_VOLUME = 0.82;
    protected static final double GAME_AUDIO_CROSSFADE_SECONDS = 0.32;
    protected static final double INTRO_SOUND_VOLUME = 0.88;
    protected static final double INTRO_MUSIC_VOLUME = 0.42;
    protected static final double MENU_AUDIO_VOLUME = 0.38;
    protected static final double MENU_CLICK_AUDIO_VOLUME = 0.45;

    // Sensor keeper mengikuti pose sprite hijau.
    // Berdiri = sensor vertikal, lompat kiri/kanan = sensor horizontal.
    protected static final double KEEPER_STAND_SENSOR_WIDTH_RATIO = 0.40;
    protected static final double KEEPER_STAND_SENSOR_HEIGHT_RATIO = 0.86;
    protected static final double KEEPER_STAND_SENSOR_Y_OFFSET_RATIO = 0.03;
    protected static final double KEEPER_DIVE_SENSOR_WIDTH_RATIO = 1.44;
    protected static final double KEEPER_DIVE_SENSOR_HEIGHT_RATIO = 0.46;
    protected static final double KEEPER_DIVE_SENSOR_X_OFFSET_RATIO = 0.20;
    protected static final double KEEPER_DIVE_SENSOR_Y_OFFSET_RATIO = 0.10;
    protected static final double KEEPER_EDGE_PADDING_RATIO = 0.015;
    protected static final double MAX_PULL_DISTANCE = 230;
    protected static final double MIN_SHOT_DISTANCE = 100;
    protected static final double MAX_SHOT_DISTANCE = 650;
    protected static final double MIN_BALL_SPEED = 360;
    protected static final double MAX_BALL_SPEED = 900;
    protected static final double UPWARD_SHOT_BONUS = 1.00;
    protected static final double SHOT_POWER_CURVE = 1.35;
    protected static final double SHOT_DISTANCE_GOAL_MULTIPLIER = 1.45;
    protected static final double KEEPER_MAX_READ_CHANCE = 0.82;
    protected static final double KEEPER_READ_GROWTH_PER_POINT = 0.08;
    protected static final double KEEPER_FRAME_SECONDS = 0.19;
    protected static final double KEEPER_LANDING_FRAME_HOLD_SECONDS = 1.0;
    protected static final double ROUND_RESULT_DELAY_SECONDS = 2.0;
    protected static final double KEEPER_DIVE_TRIGGER_RATIO = 0.045;
    protected static final double KEEPER_FINAL_HOLD_SECONDS = 2.0;
    protected static final double KEEPER_MOVE_SPEED = 760;
    // Gerak lompat samping dibuat melengkung, bukan garis lurus segitiga.
    protected static final double KEEPER_SIDE_DIVE_ARC_SECONDS = 0.56;
    protected static final double KEEPER_SIDE_DIVE_ARC_HEIGHT_RATIO = 0.105;
    protected static final double KEEPER_FALL_ARC_HEIGHT_RATIO = 0.085;
    // Durasi keeper turun dari titik loncat ke tanah. Gerak ini bukan animasi balik ke posisi awal.
    protected static final double KEEPER_FALL_TO_GROUND_SECONDS = 0.45;
    protected static final double KEEPER_CATCH_FALL_SIDE_RATIO = 0.50;
    // Efek retro: posisi bola dan keeper di-update per langkah frame rendah agar terasa agak patah-patah.
    protected static final boolean RETRO_MOTION_ENABLED = false;
    protected static final double RETRO_MOTION_FPS = 13.0;
    protected static final double RETRO_MOTION_STEP_SECONDS = 1.0 / RETRO_MOTION_FPS;
    protected static final double RETRO_PIXEL_SNAP = 4.0;
    protected static final double BALL_RETRO_ROTATION_DEGREES_PER_SECOND = 720.0;
    protected static final double BALL_ROTATION_SNAP_DEGREES = 22.5;
    protected static final double BALL_MIN_PERSPECTIVE_SCALE = 0.62;
    protected static final double BALL_PERSPECTIVE_CURVE = 1.18;
    protected static final double GOAL_TEXT_DURATION_SECONDS = 1.55;
    protected static final double GOAL_TEXT_POP_SECONDS = 0.22;
    protected static final double GOAL_TEXT_FADE_OUT_SECONDS = 0.45;
    protected static final double KEEPER_START_CENTER_Y_RATIO = 0.56;
    protected static final double KEEPER_GROUND_TARGET_Y_RATIO = 0.56;
    protected static final double KEEPER_FALL_FORWARD_OFFSET_RATIO = 0.030;
    protected static final double KEEPER_FRAME_5_DOWN_OFFSET = 42;
    protected static final double KEEPER_TOP_REACH_PADDING_RATIO = 0.012;
    protected static final double KEEPER_BOTTOM_REACH_PADDING_RATIO = 0.018;
    protected static final double SHOT_MEMORY_WEIGHT = 0.35;
    protected static final double GOAL_LEFT_RATIO = 0.24;
    protected static final double GOAL_RIGHT_RATIO = 0.76;
    protected static final double GOAL_TOP_RATIO = 0.28;
    protected static final double GOAL_BOTTOM_RATIO = 0.65;
    protected static final double GOAL_SCORE_LINE_RATIO = 0.30;
    protected static final int ROUND_RESULT_NONE = 0;
    protected static final int ROUND_RESULT_SAVED = 1;
    protected static final int ROUND_RESULT_GOAL = 2;
    protected static final int ROUND_RESULT_MISS = 3;
    protected static final boolean SHOW_DEBUG_BOXES = false;
    protected static final int MAX_PLAYER_LIVES = 3;
    protected static final int TOURNAMENT_SHOTS_PER_ROUND = 5;
    protected static final int MULTIPLAYER_SHOTS_PER_PLAYER = 5;
    protected static final int MULTIPLAYER_SCORE_EMPTY = 0;
    protected static final int MULTIPLAYER_SCORE_GOAL = 1;
    protected static final int MULTIPLAYER_SCORE_FAIL = 2;
    protected static final double PLAYER_TAG_DISPLAY_SECONDS = 3.2;
    protected static final double PLAYER_TAG_FADE_SECONDS = 0.55;
    protected static final String[] TOURNAMENT_ROUNDS = {"QUARTER FINAL", "SEMI FINAL", "FINAL"};
    protected static final int[] TOURNAMENT_TARGETS = {2, 3, 4};
    protected static final String[] TOURNAMENT_OPPONENTS = {
            "JAVA UNITED",
            "PIXEL CITY",
            "BYTE ROVERS",
            "GARUDA FC",
            "SDAT CLUB",
            "CODE STRIKERS",
            "IDEA ATHLETIC"
    };
    protected static MediaPlayer introPlayer;
    protected static MediaPlayer introSoundPlayer;
    protected static MediaPlayer introMusicPlayer;
    protected static MediaPlayer menuAudioPlayer;
    protected static AudioClip menuClickAudioClip;
    protected static MediaPlayer defaultGameAudioPlayer;
    protected static MediaPlayer effectGameAudioPlayer;
    protected static Timeline defaultAudioFadeTimeline;
    protected static Timeline effectAudioFadeTimeline;
    protected static boolean gameplayAudioRequested = false;
    protected static boolean introPlaybackStarted = false;
    protected static boolean introAudioStarted = false;
    protected static Timeline introStartupRetryTimeline;
    protected static final Random random = new Random();

    // ==================== 1. INTRO DAN START APLIKASI ====================

    @Override
    public void start(Stage stage) {
        // Reset status agar intro selalu mulai dari awal setiap aplikasi dijalankan.
        introPlaybackStarted = false;
        introAudioStarted = false;

        StackPane root = new StackPane();
        root.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));

        MediaView mediaView = new MediaView();
        mediaView.setPreserveRatio(false);
        mediaView.fitWidthProperty().bind(root.widthProperty());
        mediaView.fitHeightProperty().bind(root.heightProperty());
        root.getChildren().add(mediaView);

        // Lapisan gelap tipis agar tombol START tetap terbaca.
        Rectangle overlay = new Rectangle();
        overlay.widthProperty().bind(root.widthProperty());
        overlay.heightProperty().bind(root.heightProperty());
        overlay.setFill(Color.rgb(0, 0, 0, 0.20));
        overlay.setMouseTransparent(true);

        StackPane startButton = createStartButton();
        startButton.setOnMouseClicked(event -> showMenu(stage));
        StackPane.setAlignment(startButton, Pos.BOTTOM_CENTER);
        StackPane.setMargin(startButton, new Insets(0, 0, 72, 0));

        root.getChildren().addAll(overlay, startButton);

        Scene scene = new Scene(root, 1280, 720);
        stage.setTitle("Penalty Challenge");
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setResizable(false);
        stage.setScene(scene);
        applyPermanentFullscreen(stage);
        stage.show();
        Platform.runLater(() -> applyPermanentFullscreen(stage));

        // Media dibuat setelah window tampil. Pada Windows cara ini lebih stabil
        // daripada memaksa play sebelum Stage benar-benar aktif.
        Platform.runLater(() -> prepareAndPlayIntro(mediaView));
    }

    protected void prepareAndPlayIntro(MediaView mediaView) {
        stopIntroVideoOnly();
        Path videoPath = resolveResource(INTRO_VIDEO_PATH);

        if (!Files.exists(videoPath)) {
            // Jangan membuat aplikasi crash. Menu tetap dapat dibuka lewat START.
            System.err.println("Intro video tidak ditemukan: " + videoPath);
            startIntroAudio();
            return;
        }

        try {
            Media media = new Media(videoPath.toUri().toString());
            MediaPlayer player = new MediaPlayer(media);
            introPlayer = player;
            mediaView.setMediaPlayer(player);

            // Audio intro memakai file audio terpisah agar level volumenya konsisten.
            player.setMute(true);
            player.setCycleCount(1);
            player.setAutoPlay(true);

            player.setOnReady(() -> {
                startIntroAudio();
                ensureIntroPlaying(player);
            });
            player.setOnPlaying(() -> {
                introPlaybackStarted = true;
                stopIntroStartupRetry();
            });
            player.setOnStalled(() -> ensureIntroPlaying(player));
            player.setOnEndOfMedia(() -> {
                introPlaybackStarted = true;
                stopIntroStartupRetry();
                player.pause();
            });
            player.setOnError(() -> {
                stopIntroStartupRetry();
                System.err.println("Intro video gagal diputar: " + player.getError());
            });
            media.setOnError(() -> {
                stopIntroStartupRetry();
                System.err.println("Format intro video gagal dibaca: " + media.getError());
            });

            // Coba play langsung, lalu retry ringan hanya sampai benar-benar PLAYING.
            ensureIntroPlaying(player);
            introStartupRetryTimeline = new Timeline(
                    new KeyFrame(Duration.millis(350), event -> ensureIntroPlaying(player))
            );
            introStartupRetryTimeline.setCycleCount(30);
            introStartupRetryTimeline.play();
        } catch (RuntimeException exception) {
            stopIntroStartupRetry();
            System.err.println("Intro gagal dimuat: " + exception.getMessage());
            startIntroAudio();
        }
    }

    protected void ensureIntroPlaying(MediaPlayer player) {
        if (player == null || player != introPlayer || introPlaybackStarted) {
            return;
        }

        MediaPlayer.Status status = player.getStatus();
        if (status == MediaPlayer.Status.DISPOSED || status == MediaPlayer.Status.HALTED) {
            stopIntroStartupRetry();
            return;
        }

        try {
            player.play();
        } catch (RuntimeException exception) {
            // READY callback atau retry berikutnya akan mencoba lagi.
        }
    }

    protected void stopIntroStartupRetry() {
        if (introStartupRetryTimeline != null) {
            introStartupRetryTimeline.stop();
            introStartupRetryTimeline = null;
        }
    }

    protected void stopIntroVideoOnly() {
        stopIntroStartupRetry();
        if (introPlayer != null) {
            try {
                introPlayer.stop();
            } catch (RuntimeException ignored) {
                // Media mungkin sudah disposed; aman untuk diabaikan saat cleanup.
            }
            disposeMediaPlayer(introPlayer);
            introPlayer = null;
        }
        introPlaybackStarted = false;
    }

    @Override
    public void stop() {
        stopGameplayAudio();
        stopMenuAudio();
        stopIntroAudio();
    }

    // ==================== 2. RESOURCE DAN AUDIO ====================

protected Path resolveResource(String relativePath) {
        Path cleanPath = Path.of(relativePath.replace("/", java.io.File.separator)).normalize();
        Path workingDirectory = Path.of(System.getProperty("user.dir", ".")).toAbsolutePath().normalize();
        List<Path> candidates = new ArrayList<>();

        // Cari dari working directory dan beberapa parent agar tetap stabil
        // ketika dijalankan dari IntelliJ, folder out/production, atau project root.
        Path current = workingDirectory;
        for (int i = 0; i < 8 && current != null; i++) {
            candidates.add(current.resolve(cleanPath).normalize());
            candidates.add(current.resolve("Projek-SDAT-Penalty-Challenge").resolve(cleanPath).normalize());
            current = current.getParent();
        }

        candidates.add(Path.of("").toAbsolutePath().resolve(cleanPath).normalize());
        candidates.add(Path.of("src").toAbsolutePath().resolve(cleanPath).normalize());

        for (Path candidate : candidates) {
            if (Files.exists(candidate)) {
                return candidate;
            }
        }

        return workingDirectory.resolve(cleanPath).normalize();
    }

    protected void startIntroAudio() {
        if (introAudioStarted) {
            return;
        }
        if (introSoundPlayer == null) {
            introSoundPlayer = createAudioPlayer(INTRO_SOUND_PATH, INTRO_SOUND_VOLUME, false);
        }
        if (introMusicPlayer == null) {
            introMusicPlayer = createAudioPlayer(INTRO_MUSIC_PATH, INTRO_MUSIC_VOLUME, true);
        }

        playFromStart(introSoundPlayer);
        playFromStart(introMusicPlayer);
        introAudioStarted = true;
    }

    protected void stopIntroAudio() {
        introAudioStarted = false;
        disposeMediaPlayer(introSoundPlayer);
        introSoundPlayer = null;
        disposeMediaPlayer(introMusicPlayer);
        introMusicPlayer = null;
        stopIntroVideoOnly();
    }

    protected void startMenuAudio() {
        stopIntroAudio();
        if (menuAudioPlayer == null) {
            menuAudioPlayer = createAudioPlayer(MENU_AUDIO_PATH, MENU_AUDIO_VOLUME, true);
        }
        if (menuAudioPlayer != null) {
            try {
                if (menuAudioPlayer.getStatus() == MediaPlayer.Status.DISPOSED) {
                    menuAudioPlayer = createAudioPlayer(MENU_AUDIO_PATH, MENU_AUDIO_VOLUME, true);
                }
                menuAudioPlayer.setVolume(MENU_AUDIO_VOLUME);
                menuAudioPlayer.play();
            } catch (Exception exception) {
                System.err.println("Gagal play menu audio: " + exception.getMessage());
            }
        }
    }

    protected void stopMenuAudio() {
        disposeMediaPlayer(menuAudioPlayer);
        menuAudioPlayer = null;
    }

    protected void playMenuClickSound() {
        try {
            if (menuClickAudioClip == null) {
                Path path = resolveResource(MENU_CLICK_AUDIO_PATH);
                if (!Files.exists(path)) {
                    return;
                }
                menuClickAudioClip = new AudioClip(path.toUri().toString());
                menuClickAudioClip.setVolume(MENU_CLICK_AUDIO_VOLUME);
            }
            menuClickAudioClip.play(MENU_CLICK_AUDIO_VOLUME);
        } catch (Exception exception) {
            System.err.println("Gagal play menu click audio: " + exception.getMessage());
        }
    }

    protected void startGameplayDefaultAudio() {
        stopMenuAudio();
        gameplayAudioRequested = true;
        stopEffectGameAudio(true);

        if (defaultGameAudioPlayer == null) {
            defaultGameAudioPlayer = createAudioPlayer(AUDIO_DEFAULT_PATH, GAME_DEFAULT_AUDIO_VOLUME, true);
        }

        if (defaultGameAudioPlayer != null) {
            try {
                defaultGameAudioPlayer.setVolume(0.0);
                defaultGameAudioPlayer.play();
                fadePlayerVolume(defaultGameAudioPlayer, GAME_DEFAULT_AUDIO_VOLUME, GAME_AUDIO_CROSSFADE_SECONDS, true);
            } catch (Exception exception) {
                System.err.println("Gagal play default audio: " + exception.getMessage());
            }
        }
    }

    protected void resumeDefaultGameAudioIfNeeded() {
        if (!gameplayAudioRequested) {
            return;
        }
        if (defaultGameAudioPlayer == null) {
            defaultGameAudioPlayer = createAudioPlayer(AUDIO_DEFAULT_PATH, 0.0, true);
        }
        if (defaultGameAudioPlayer != null) {
            try {
                defaultGameAudioPlayer.play();
                fadePlayerVolume(defaultGameAudioPlayer, GAME_DEFAULT_AUDIO_VOLUME, GAME_AUDIO_CROSSFADE_SECONDS, true);
            } catch (Exception exception) {
                System.err.println("Gagal resume default audio: " + exception.getMessage());
            }
        }
    }

    protected void playRoundResultAudio(int roundResult) {
        if (roundResult == ROUND_RESULT_GOAL) {
            playEffectGameAudio(AUDIO_GOAL_PATH);
        } else if (roundResult == ROUND_RESULT_MISS || roundResult == ROUND_RESULT_SAVED) {
            playEffectGameAudio(AUDIO_MISSED_PATH);
        }
    }

    protected void playEffectGameAudio(String audioPath) {
        if (!gameplayAudioRequested) {
            return;
        }

        if (defaultGameAudioPlayer == null) {
            defaultGameAudioPlayer = createAudioPlayer(AUDIO_DEFAULT_PATH, GAME_DEFAULT_AUDIO_VOLUME, true);
        }
        if (defaultGameAudioPlayer != null) {
            try {
                defaultGameAudioPlayer.play();
                fadePlayerVolume(defaultGameAudioPlayer, GAME_DEFAULT_DUCK_VOLUME, GAME_AUDIO_CROSSFADE_SECONDS, true);
            } catch (Exception exception) {
                System.err.println("Gagal duck default audio: " + exception.getMessage());
            }
        }

        stopEffectGameAudio(true);

        MediaPlayer player = createAudioPlayer(audioPath, 0.0, false);
        if (player == null) {
            resumeDefaultGameAudioIfNeeded();
            return;
        }

        effectGameAudioPlayer = player;
        player.setOnEndOfMedia(() -> {
            stopEffectGameAudio(true);
            resumeDefaultGameAudioIfNeeded();
        });
        player.setOnError(() -> {
            System.err.println("Gagal memutar effect audio: " + player.getError());
            stopEffectGameAudio(true);
            resumeDefaultGameAudioIfNeeded();
        });

        try {
            player.seek(Duration.ZERO);
            player.play();
            fadePlayerVolume(player, GAME_EFFECT_AUDIO_VOLUME, GAME_AUDIO_CROSSFADE_SECONDS * 0.60, false);
        } catch (Exception exception) {
            System.err.println("Gagal play effect audio: " + exception.getMessage());
            stopEffectGameAudio(true);
            resumeDefaultGameAudioIfNeeded();
        }
    }

    protected MediaPlayer createAudioPlayer(String audioPath, double volume, boolean loop) {
        Path path = resolveResource(audioPath);
        if (!Files.exists(path)) {
            System.err.println("Audio tidak ditemukan: " + path);
            return null;
        }

        try {
            Media media = new Media(path.toUri().toString());
            MediaPlayer player = new MediaPlayer(media);
            player.setVolume(volume);
            player.setCycleCount(loop ? MediaPlayer.INDEFINITE : 1);
            media.setOnError(() -> System.err.println("Format audio tidak bisa dibaca: " + media.getError()));
            player.setOnError(() -> System.err.println("MediaPlayer audio error: " + player.getError()));
            return player;
        } catch (Exception exception) {
            System.err.println("Gagal load audio " + audioPath + ": " + exception.getMessage());
            return null;
        }
    }

    protected void playFromStart(MediaPlayer player) {
        if (player == null) {
            return;
        }
        try {
            player.seek(Duration.ZERO);
            player.play();
        } catch (Exception exception) {
            System.err.println("Gagal play audio: " + exception.getMessage());
        }
    }

    protected void fadePlayerVolume(MediaPlayer player, double targetVolume, double seconds, boolean defaultPlayer) {
        if (player == null) {
            return;
        }
        Timeline oldTimeline = defaultPlayer ? defaultAudioFadeTimeline : effectAudioFadeTimeline;
        if (oldTimeline != null) {
            oldTimeline.stop();
        }
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(player.volumeProperty(), player.getVolume())),
                new KeyFrame(Duration.seconds(seconds), new KeyValue(player.volumeProperty(), targetVolume))
        );
        timeline.play();
        if (defaultPlayer) {
            defaultAudioFadeTimeline = timeline;
        } else {
            effectAudioFadeTimeline = timeline;
        }
    }

    protected void stopEffectGameAudio(boolean dispose) {
        if (effectAudioFadeTimeline != null) {
            effectAudioFadeTimeline.stop();
            effectAudioFadeTimeline = null;
        }
        if (effectGameAudioPlayer == null) {
            return;
        }
        try {
            effectGameAudioPlayer.stop();
        } catch (Exception exception) {
            System.err.println("Gagal stop effect audio: " + exception.getMessage());
        }
        if (dispose) {
            disposeMediaPlayer(effectGameAudioPlayer);
            effectGameAudioPlayer = null;
        }
    }

    protected void stopGameplayAudio() {
        gameplayAudioRequested = false;
        if (defaultAudioFadeTimeline != null) {
            defaultAudioFadeTimeline.stop();
            defaultAudioFadeTimeline = null;
        }
        stopEffectGameAudio(true);
        disposeMediaPlayer(defaultGameAudioPlayer);
        defaultGameAudioPlayer = null;
    }

    protected void disposeMediaPlayer(MediaPlayer player) {
        if (player == null) {
            return;
        }
        try {
            player.stop();
            player.dispose();
        } catch (Exception exception) {
            System.err.println("Gagal dispose media player: " + exception.getMessage());
        }
    }

    protected StackPane createStartButton() {
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

    protected Font loadFont(String fontPath, double size, Font fallbackFont) {
        Path path = resolveResource(fontPath);
        if (!Files.exists(path)) {
            return fallbackFont;
        }

        Font font = Font.loadFont(path.toUri().toString(), size);
        return font != null ? font : fallbackFont;
    }

    protected void applyPermanentFullscreen(Stage stage) {
        if (stage == null) {
            return;
        }

        Screen targetScreen = Screen.getPrimary();
        double x = stage.getX();
        double y = stage.getY();
        double width = stage.getWidth();
        double height = stage.getHeight();

        if (Double.isFinite(x) && Double.isFinite(y)
                && Double.isFinite(width) && Double.isFinite(height)
                && width > 1 && height > 1) {
            targetScreen = Screen.getScreensForRectangle(x, y, width, height)
                    .stream()
                    .findFirst()
                    .orElse(Screen.getPrimary());
        }

        Rectangle2D bounds = targetScreen.getBounds();
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());
    }

    protected void setSceneSmooth(Stage stage, Scene nextScene, Runnable afterSceneSwitch) {
        if (stage == null || nextScene == null) {
            return;
        }

        // Perpindahan scene dibuat langsung tanpa fade/opacity agar tidak menimbulkan
        // efek kedip atau layar hitam di antara mode.
        javafx.scene.Parent nextRoot = nextScene.getRoot();
        nextRoot.setOpacity(1.0);
        nextRoot.setMouseTransparent(false);

        stage.setScene(nextScene);

        // Jangan pernah masuk ulang ke mode fullscreen eksklusif. Cukup pertahankan
        // window borderless pada ukuran layar agar klik START atau perpindahan mode
        // tidak membuat aplikasi restore/minimize.
        applyPermanentFullscreen(stage);
        Platform.runLater(() -> applyPermanentFullscreen(stage));

        if (afterSceneSwitch != null) {
            afterSceneSwitch.run();
        }
    }

    // ==================== 4. MENU UTAMA ====================

    protected void showMenu(Stage stage) {
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

        String[] modes = {"TOURNAMENT", "MULTIPLAYER", "ENDLESS", "TUTORIAL", "EXIT"};

        for (int i = 0; i < modes.length; i++) {
            StackPane option = createMenuOption(modes[i]);
            if ("ENDLESS".equals(modes[i])) {
                option.setOnMouseClicked(event -> showEndlessMode(stage));
            } else if ("MULTIPLAYER".equals(modes[i]) || "CO-OP".equals(modes[i])) {
                option.setOnMouseClicked(event -> showMultiplayerMode(stage));
            } else if ("TOURNAMENT".equals(modes[i])) {
                option.setOnMouseClicked(event -> showTournamentMode(stage));
            } else if ("TUTORIAL".equals(modes[i])) {
                option.setOnMouseClicked(event -> showTutorialMode(stage));
            } else if ("EXIT".equals(modes[i])) {
                option.setOnMouseClicked(event -> Platform.exit());
            }
            menuBox.getChildren().add(option);
        }

        root.getChildren().addAll(background, overlay, menuBox);

        Scene scene = new Scene(root, 1280, 720);
        scene.setCursor(Cursor.DEFAULT);
        setSceneSmooth(stage, scene, () -> {
            stopGameplayAudio();
            startMenuAudio();
        });
    }

    

    protected StackPane createMenuOption(String modeName) {
        ImageView offFrame = createImageView(MENU_BOX_OFF_PATH);
        ImageView onFrame = createImageView(MENU_BOX_ON_PATH);
        offFrame.setFitWidth(MENU_OPTION_WIDTH);
        onFrame.setFitWidth(MENU_OPTION_WIDTH);
        offFrame.setFitHeight(MENU_OPTION_HEIGHT);
        onFrame.setFitHeight(MENU_OPTION_HEIGHT);
        offFrame.setPreserveRatio(false);
        onFrame.setPreserveRatio(false);
        offFrame.setSmooth(false);
        onFrame.setSmooth(false);

        ImageView labelImage = createMenuLabelImage(modeName);
        if (labelImage != null) {
            labelImage.setMouseTransparent(true);
        }

        Text fallbackLabel = new Text(getMenuDisplayName(modeName));
        fallbackLabel.setFill(Color.WHITE);
        fallbackLabel.setFont(loadFont(MENU_FONT_PATH, 16, Font.font("Arial", FontWeight.BOLD, 16)));
        fallbackLabel.setMouseTransparent(true);
        fallbackLabel.setVisible(labelImage == null);

        StackPane option = labelImage == null
                ? new StackPane(offFrame, onFrame, fallbackLabel)
                : new StackPane(offFrame, onFrame, labelImage);
        option.setMinSize(MENU_OPTION_WIDTH, MENU_OPTION_HEIGHT);
        option.setMaxSize(MENU_OPTION_WIDTH, MENU_OPTION_HEIGHT);
        option.setCursor(Cursor.HAND);
        onFrame.setVisible(false);

        option.setOnMouseEntered(event -> {
            onFrame.setVisible(true);
            offFrame.setVisible(false);
            option.setScaleX(1.03);
            option.setScaleY(1.03);
        });
        option.setOnMouseExited(event -> {
            onFrame.setVisible(false);
            offFrame.setVisible(true);
            option.setScaleX(1.0);
            option.setScaleY(1.0);
        });
        option.setOnMousePressed(event -> {
            playMenuClickSound();
            option.setScaleX(0.98);
            option.setScaleY(0.98);
        });
        option.setOnMouseReleased(event -> {
            option.setScaleX(1.03);
            option.setScaleY(1.03);
        });

        return option;
    }

    protected ImageView createMenuLabelImage(String modeName) {
        String path = getMenuLabelPath(modeName);
        if (path == null || !Files.exists(resolveResource(path))) {
            return null;
        }

        ImageView imageView = createImageView(path);
        imageView.setFitWidth(getMenuLabelWidth(modeName));
        imageView.setPreserveRatio(true);
        imageView.setSmooth(false);
        return imageView;
    }

    protected String getMenuLabelPath(String modeName) {
        switch (modeName) {
            case "TOURNAMENT":
                return MENU_LABEL_TOURNAMENT_PATH;
            case "MULTIPLAYER":
            case "CO-OP":
                return MENU_LABEL_MULTIPLAYER_PATH;
            case "ENDLESS":
                return MENU_LABEL_ENDLESS_PATH;
            case "TUTORIAL":
                return MENU_LABEL_TUTORIAL_PATH;
            case "EXIT":
                return MENU_LABEL_EXIT_PATH;
            default:
                return null;
        }
    }

    protected double getMenuLabelWidth(String modeName) {
        switch (modeName) {
            case "TOURNAMENT":
                return 239.0;
            case "MULTIPLAYER":
            case "CO-OP":
                return 190.5;
            case "ENDLESS":
                return 214.5;
            case "TUTORIAL":
                return 217.0;
            case "EXIT":
                return 177.0;
            default:
                return 180.0;
        }
    }

    protected String getMenuDisplayName(String modeName) {
        return "MULTIPLAYER".equals(modeName) ? "CO-OP" : modeName;
    }

    protected ImageView createImageView(String imagePath) {
        Path path = resolveResource(imagePath);
        Image image = Files.exists(path)
                ? new Image(path.toUri().toString(), false)
                : createFallbackImage();
        return new ImageView(image);
    }

protected Image createFallbackImage() {
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

    // -------------------------------------------------------------------------
    // MODE ROUTER
    // Setiap mode berada di file terpisah di src/modes/<nama-mode>/.
    // Main hanya meneruskan perpindahan scene ke modul yang sesuai.
    // -------------------------------------------------------------------------
    protected void showTutorialMode(Stage stage) {
        new TutorialMode().showTutorialMode(stage);
    }

    protected void showEndlessMode(Stage stage) {
        new EndlessMode().showEndlessMode(stage);
    }

    protected void showMultiplayerMode(Stage stage) {
        new CoopMode().showMultiplayerMode(stage);
    }

    protected void showTournamentMode(Stage stage) {
        new TournamentMode().showTournamentMode(stage);
    }
}
