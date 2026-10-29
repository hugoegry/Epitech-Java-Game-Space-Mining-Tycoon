package tools;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import ui.VideoLayer;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Diag: verifie que les videos avancent (pas figees).
 * Exit 0 = OK, 1 = KO.
 */
public class VideoDiag extends Application {

    @Override
    public void start(Stage stage) {
        StackPane root = new StackPane();
        stage.setScene(new Scene(root, 800, 450));
        stage.setTitle("VideoDiag");
        stage.show();

        Thread t = new Thread(() -> {
            boolean allOk = true;
            try {
                for (String name : new String[]{"video_menu_comp.mp4", "Mercatora_comp.mp4"}) {
                    CountResult r = measure(root, name);
                    System.out.println("[DIAG] " + name + " t1=" + r.t1 + " t2=" + r.t2 + " ok=" + r.ok);
                    if (!r.ok) allOk = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                allOk = false;
            }
            int code = allOk ? 0 : 1;
            Platform.runLater(() -> {
                stage.close();
                Platform.exit();
                System.exit(code);
            });
        }, "video-diag");
        t.setDaemon(true);
        t.start();
    }

    private static CountResult measure(StackPane root, String name) throws Exception {
        AtomicReference<VideoLayer> ref = new AtomicReference<>();
        CountDownLatch created = new CountDownLatch(1);
        Platform.runLater(() -> {
            root.getChildren().clear();
            VideoLayer layer = new VideoLayer(root, name, VideoLayer.Type.BACKGROUND, true);
            layer.play();
            ref.set(layer);
            created.countDown();
        });
        if (!created.await(10, TimeUnit.SECONDS)) return new CountResult(-1, -1, false);

        Thread.sleep(1200);

        AtomicReference<Double> t1 = new AtomicReference<>(-1.0);
        CountDownLatch s1 = new CountDownLatch(1);
        Platform.runLater(() -> {
            VideoLayer layer = ref.get();
            if (layer != null && layer.getPlayer().getCurrentTime() != null) {
                t1.set(layer.getPlayer().getCurrentTime().toSeconds());
            }
            s1.countDown();
        });
        s1.await(5, TimeUnit.SECONDS);

        Thread.sleep(1800);

        AtomicReference<Double> t2 = new AtomicReference<>(-1.0);
        CountDownLatch s2 = new CountDownLatch(1);
        Platform.runLater(() -> {
            VideoLayer layer = ref.get();
            if (layer != null && layer.getPlayer().getCurrentTime() != null) {
                t2.set(layer.getPlayer().getCurrentTime().toSeconds());
                layer.dispose();
            }
            s2.countDown();
        });
        s2.await(5, TimeUnit.SECONDS);

        boolean ok = t1.get() >= 0 && t2.get() > t1.get() + 0.3;
        return new CountResult(t1.get(), t2.get(), ok);
    }

    private record CountResult(double t1, double t2, boolean ok) {}

    public static void main(String[] args) {
        launch(args);
    }
}
