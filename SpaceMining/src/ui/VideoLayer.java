package ui;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class VideoLayer {
    public enum Type { BACKGROUND, FOREGROUND }
    private static final Set<VideoLayer> LIVE = ConcurrentHashMap.newKeySet();
    private static final Path SAFE_VIDEO_DIR = Path.of(System.getProperty("java.io.tmpdir"), "smt-videos");
    private static final ExecutorService preloadExecutor = Executors.newFixedThreadPool(2, new ThreadFactory() {
        private final AtomicInteger n = new AtomicInteger();
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, "video-preload-" + n.incrementAndGet());
            t.setDaemon(true);
            return t;
        }
    });

    private final String videoName;
    private final MediaPlayer player;
    private final MediaView view;
    private final StackPane parent;
    private final Type type;
    private final boolean loop;
    private final Path localFile;
    private volatile boolean disposed = false;

    public VideoLayer(StackPane parent, String videoName, Type type, boolean loop) {
        this.parent = parent;
        this.videoName = videoName;
        this.type = type;
        this.loop = loop;

        this.localFile = copyUniqueSafeFile(videoName);
        Media media = new Media(localFile.toUri().toString());
        System.out.println("[VideoLayer] load " + videoName + " -> " + localFile.toUri());

        this.player = new MediaPlayer(media);
        this.player.setMute(type == Type.BACKGROUND);
        this.player.setCycleCount(1);

        this.view = new MediaView(player);
        view.setPreserveRatio(false);
        view.setSmooth(true);
        view.setFitWidth(parent.getWidth() > 1 ? parent.getWidth() : 1920);
        view.setFitHeight(parent.getHeight() > 1 ? parent.getHeight() : 1080);
        view.setMouseTransparent(true);
        parent.widthProperty().addListener((o, a, w) -> { if (w.doubleValue() > 1) view.setFitWidth(w.doubleValue()); });
        parent.heightProperty().addListener((o, a, h) -> { if (h.doubleValue() > 1) view.setFitHeight(h.doubleValue()); });

        player.setOnError(() -> {
            System.err.println("[VideoLayer] ERREUR " + videoName + " : " + player.getError());
            if (player.getError() != null) player.getError().printStackTrace();
        });

        player.setOnEndOfMedia(() -> {
            if (disposed || !loop) return;
            Platform.runLater(() -> {
                try {
                    player.seek(Duration.ZERO);
                    player.play();
                } catch (Exception e) {
                    System.err.println("[VideoLayer] loop fail " + videoName + " : " + e.getMessage());
                }
            });
        });

        player.setOnReady(() -> {
            System.out.println("[VideoLayer] READY " + videoName + " dur=" + player.getTotalDuration());
            attach();
            player.play();
        });

        LIVE.add(this);
        attach();
    }

    private static String normalizeName(String name) { return name.toLowerCase().trim(); }

    public static void preloadVideo(String videoName) {
        preloadExecutor.submit(() -> {
            try {
                ensureSourceCopy(videoName);
                System.out.println("[VideoLayer] Media precharge : " + videoName);
            } catch (Exception e) {
                System.err.println("[VideoLayer] Erreur preload : " + videoName);
                e.printStackTrace();
            }
        });
    }

    public static CompletableFuture<Path> preloadVideoAsync(String videoName) {
        return CompletableFuture.supplyAsync(() -> {
            try { return ensureSourceCopy(videoName); }
            catch (Exception e) { throw new RuntimeException(e); }
        }, preloadExecutor);
    }

    private static Path copyUniqueSafeFile(String videoName) {
        try {
            Path src = ensureSourceCopy(videoName);
            Files.createDirectories(SAFE_VIDEO_DIR);
            String safe = videoName.replaceAll("[^a-zA-Z0-9._-]", "_");
            Path target = SAFE_VIDEO_DIR.resolve(UUID.randomUUID() + "_" + safe);
            Files.copy(src, target, StandardCopyOption.REPLACE_EXISTING);
            target.toFile().deleteOnExit();
            return target;
        } catch (Exception e) {
            throw new RuntimeException("Erreur copie video : " + videoName, e);
        }
    }

    private static Path ensureSourceCopy(String videoName) throws Exception {
        Files.createDirectories(SAFE_VIDEO_DIR);
        Path stable = SAFE_VIDEO_DIR.resolve("src_" + videoName.replaceAll("[^a-zA-Z0-9._-]", "_"));
        if (Files.isRegularFile(stable) && Files.size(stable) > 0) return stable;

        var resource = VideoLayer.class.getResource("/assets/videos/" + videoName);
        if (resource != null) {
            if ("file".equalsIgnoreCase(resource.getProtocol())) {
                Path src = Path.of(resource.toURI());
                if (!src.toAbsolutePath().toString().contains(" ")) return src;
                Files.copy(src, stable, StandardCopyOption.REPLACE_EXISTING);
                return stable;
            }
            try (InputStream in = resource.openStream()) {
                Files.copy(in, stable, StandardCopyOption.REPLACE_EXISTING);
            }
            return stable;
        }

        Path[] candidates = {
            Path.of("SpaceMining/src/assets/videos/" + videoName),
            Path.of("build/resources/main/assets/videos/" + videoName),
            Path.of("assets/videos/" + videoName)
        };
        for (Path c : candidates) {
            if (Files.isRegularFile(c)) {
                Files.copy(c.toAbsolutePath(), stable, StandardCopyOption.REPLACE_EXISTING);
                return stable;
            }
        }
        throw new RuntimeException("Fichier video introuvable : " + videoName);
    }

    private void attach() {
        Runnable r = () -> {
            if (disposed) return;
            if (!parent.getChildren().contains(view)) {
                if (type == Type.BACKGROUND) parent.getChildren().add(0, view);
                else parent.getChildren().add(view);
            }
        };
        if (Platform.isFxApplicationThread()) r.run();
        else Platform.runLater(r);
    }

    public void fadeIn() {
        Platform.runLater(() -> {
            FadeTransition ft = new FadeTransition(Duration.millis(400), view);
            ft.setFromValue(view.getOpacity());
            ft.setToValue(1);
            ft.play();
        });
    }

    public void fadeOut(Runnable onFinished) {
        Platform.runLater(() -> {
            FadeTransition ft = new FadeTransition(Duration.millis(400), view);
            ft.setFromValue(view.getOpacity());
            ft.setToValue(0);
            ft.setOnFinished(e -> { if (onFinished != null) onFinished.run(); });
            ft.play();
        });
    }

    public void play() {
        Platform.runLater(() -> {
            if (disposed) return;
            attach();
            MediaPlayer.Status st = player.getStatus();
            if (st == MediaPlayer.Status.READY || st == MediaPlayer.Status.PAUSED || st == MediaPlayer.Status.STOPPED || st == MediaPlayer.Status.PLAYING) {
                try { player.play(); } catch (Exception e) {
                    System.err.println("[VideoLayer] play fail " + videoName + " : " + e.getMessage());
                }
            }
        });
    }

    public void stop() {
        try { player.stop(); } catch (Exception ignored) {}
    }

    public void dispose() {
        if (disposed) return;
        disposed = true;
        LIVE.remove(this);
        removeFromParent();
        try { player.stop(); } catch (Exception ignored) {}
        try { player.dispose(); } catch (Exception ignored) {}
        try { if (localFile != null) Files.deleteIfExists(localFile); } catch (Exception ignored) {}
    }

    public void removeFromParent() {
        if (view != null && parent != null) {
            Runnable r = () -> parent.getChildren().remove(view);
            if (Platform.isFxApplicationThread()) r.run();
            else Platform.runLater(r);
        }
    }

    public static void shutdownAll() {
        for (VideoLayer layer : LIVE.toArray(new VideoLayer[0])) {
            try { layer.dispose(); } catch (Exception ignored) {}
        }
        LIVE.clear();
        preloadExecutor.shutdownNow();
    }

    public MediaPlayer getPlayer() { return player; }
    public MediaView getView() { return view; }
}