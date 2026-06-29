import javax.sound.sampled.*;
import java.io.*;
import java.util.HashMap;

public class SoundManager {
    private static SoundManager instance;
    private HashMap<String, Clip> sounds;
    private Clip backgroundMusic;
    private float bgmVolume = 0.7f;
    private float sfxVolume = 1.0f;
    private boolean isMuted = false;

    private SoundManager() {
        sounds = new HashMap<>();
        loadAllSounds();
    }

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    private void loadAllSounds() {
        // Load semua sound effects
        loadSound("intro_music", "resources/audio/intro_music.wav");
        loadSound("game_bgm", "resources/audio/game_bgm.wav");
        loadSound("kick", "resources/audio/kick_sound.wav");
        loadSound("goal", "resources/audio/goal_sound.wav");
        loadSound("save", "resources/audio/save_sound.wav");
        loadSound("miss", "resources/audio/miss_sound.wav");
        loadSound("gameover", "resources/audio/gameover_sound.wav");
        loadSound("button_click", "resources/audio/button_click.wav");
    }

    private void loadSound(String name, String path) {
        try {
            File soundFile = new File(path);
            if (soundFile.exists()) {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);
                sounds.put(name, clip);
                System.out.println("Loaded: " + name);
            } else {
                System.out.println("File not found: " + path);
                // Buat dummy clip untuk development
                sounds.put(name, null);
            }
        } catch (Exception e) {
            System.out.println("Gagal load audio " + name + ": " + e.getMessage());
            sounds.put(name, null);
        }
    }

    public void playBGM(String musicName) {
        if (isMuted) return;

        stopBGM();
        Clip bgm = sounds.get(musicName);
        if (bgm != null) {
            try {
                backgroundMusic = bgm;
                setVolume(bgm, bgmVolume);
                bgm.setFramePosition(0);
                bgm.loop(Clip.LOOP_CONTINUOUSLY);
                bgm.start();
            } catch (Exception e) {
                System.out.println("Error playing BGM: " + e.getMessage());
            }
        }
    }

    public void playSFX(String sfxName) {
        if (isMuted) return;

        Clip sfx = sounds.get(sfxName);
        if (sfx != null) {
            try {
                sfx.setFramePosition(0);
                setVolume(sfx, sfxVolume);
                sfx.start();
            } catch (Exception e) {
                // Reload clip jika error
                System.out.println("Error playing SFX: " + e.getMessage());
            }
        }
    }

    public void stopBGM() {
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
        }
    }

    public void stopAll() {
        stopBGM();
        for (Clip clip : sounds.values()) {
            if (clip != null && clip.isRunning()) {
                clip.stop();
            }
        }
    }

    private void setVolume(Clip clip, float volume) {
        if (clip != null) {
            try {
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float range = gainControl.getMaximum() - gainControl.getMinimum();
                float gain = (range * volume) + gainControl.getMinimum();
                gainControl.setValue(gain);
            } catch (Exception e) {
                // Ignore volume control errors
            }
        }
    }

    public void toggleMute() {
        isMuted = !isMuted;
        if (isMuted) {
            stopAll();
        }
    }

    public boolean isMuted() {
        return isMuted;
    }

    public void setBGMVolume(float volume) {
        this.bgmVolume = Math.max(0.0f, Math.min(1.0f, volume));
        if (backgroundMusic != null) {
            setVolume(backgroundMusic, bgmVolume);
        }
    }

    public void setSFXVolume(float volume) {
        this.sfxVolume = Math.max(0.0f, Math.min(1.0f, volume));
    }
}