package utils;

import javafx.scene.media.AudioClip;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Gère la lecture de l’ambiance sonore associée à une planète.
 *
 * <p>
 * Cette classe encapsule le chargement et le contrôle d’un fichier audio
 * utilisé comme musique ou ambiance de fond. Elle utilise un {@link AudioClip},
 * ce qui permet une lecture légère, simple et en boucle continue.
 * </p>
 *
 * <p>Note : Cette classe ne gère pas le volume, les transitions,
 * ni les effets audio. Elle se concentre uniquement sur le chargement et la lecture.</p>
 */
public class Audio {
    private static final Set<Audio> LIVE = ConcurrentHashMap.newKeySet();
    private AudioClip clip;

    public Audio() {
        LIVE.add(this);
    }

    public void load(String path) {
        if (path == null) return;
        stop();
        var url = getClass().getResource(path);
        if (url == null) throw new RuntimeException("Audio introuvable : " + path);
        clip = new AudioClip(url.toExternalForm());
        clip.setCycleCount(AudioClip.INDEFINITE);
    }

    public void play() {
        if (clip != null) clip.play();
    }

    public void stop() {
        if (clip != null) {
            try { clip.stop(); } catch (Exception ignored) {}
        }
    }

    public static void stopAll() {
        for (Audio a : LIVE) {
            try { a.stop(); } catch (Exception ignored) {}
        }
        LIVE.clear();
    }
}