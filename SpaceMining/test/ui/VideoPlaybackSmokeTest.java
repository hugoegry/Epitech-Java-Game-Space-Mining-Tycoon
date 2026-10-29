package ui;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifie que la video menu avance bien dans le temps (pas figee sur la 1ere frame).
 */
public class VideoPlaybackSmokeTest {

    private static final AtomicBoolean fxStarted = new AtomicBoolean(false);

    private static void ensureFx() throws Exception {
        if (fxStarted.get()) return;
        CountDownLatch latch = new CountDownLatch(1);
        try {
            Platform.startup(latch::countDown);
        } catch (IllegalStateException already) {
            latch.countDown();
        }
        assertTrue(latch.await(10, TimeUnit.SECONDS));
        fxStarted.set(true);
    }

    @Test
    @Timeout(40)
    void menuVideoCurrentTimeAdvances() throws Exception {
        ensureFx();

        CountDownLatch ready = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(1);
        AtomicReference<Double> t1 = new AtomicReference<>(-1.0);
        AtomicReference<Double> t2 = new AtomicReference<>(-1.0);
        AtomicReference<String> err = new AtomicReference<>(null);

        Platform.runLater(() -> {
            try {
                Stage stage = new Stage();
                StackPane root = new StackPane();
                VideoLayer layer = new VideoLayer(root, "video_menu_comp.mp4", VideoLayer.Type.BACKGROUND, true);
                stage.setScene(new Scene(root, 640, 360));
                stage.show();
                layer.play();

                layer.getPlayer().setOnReady(() -> {
                    ready.countDown();
                    layer.getPlayer().play();
                });
                if (layer.getPlayer().getStatus() != null) ready.countDown();

                // apres ready, sample a t=0 puis t~1.5s
                new Thread(() -> {
                    try {
                        ready.await(15, TimeUnit.SECONDS);
                        Thread.sleep(300);
                        CountDownLatch s1 = new CountDownLatch(1);
                        Platform.runLater(() -> {
                            t1.set(layer.getPlayer().getCurrentTime().toSeconds());
                            s1.countDown();
                        });
                        s1.await(5, TimeUnit.SECONDS);
                        Thread.sleep(1500);
                        CountDownLatch s2 = new CountDownLatch(1);
                        Platform.runLater(() -> {
                            t2.set(layer.getPlayer().getCurrentTime().toSeconds());
                            layer.dispose();
                            stage.close();
                            s2.countDown();
                            done.countDown();
                        });
                        s2.await(5, TimeUnit.SECONDS);
                    } catch (Exception e) {
                        err.set(e.toString());
                        done.countDown();
                    }
                }).start();
            } catch (Exception e) {
                err.set(e.toString());
                ready.countDown();
                done.countDown();
            }
        });

        assertTrue(done.await(35, TimeUnit.SECONDS), "timeout smoke video");
        if (err.get() != null) throw new AssertionError(err.get());

        // Si aucun display (CI headless), on skip
        Assumptions.assumeTrue(t1.get() >= 0 && t2.get() >= 0, "pas de timing video (env headless?)");
        System.out.println("[SMOKE] t1=" + t1.get() + " t2=" + t2.get());
        assertTrue(t2.get() > t1.get() + 0.3, "video fige: t1=" + t1.get() + " t2=" + t2.get());
    }
}
