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

public class VideoRestartDiag extends Application {

    @Override
    public void start(Stage stage) {
        StackPane root = new StackPane();
        stage.setScene(new Scene(root, 640, 360));
        stage.show();

        Thread t = new Thread(() -> {
            boolean ok = true;
            String[] shipsCycle = {"Mercatora_comp.mp4", "Aurea_comp.mp4", "video_menu_comp.mp4"};
            try {
                for (int i = 0; i < 6; i++) {
                    String name = shipsCycle[i % shipsCycle.length];
                    CountResult r = measureOnce(root, name);
                    System.out.println("[RESTART-DIAG] #" + i + " " + name + " t1=" + r.t1 + " t2=" + r.t2 + " ok=" + r.ok);
                    if (!r.ok) ok = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                ok = false;
            }
            int code = ok ? 0 : 1;
            Platform.runLater(() -> {
                VideoLayer.shutdownAll();
                stage.close();
                Platform.exit();
                System.exit(code);
            });
        }, "restart-diag");
        t.setDaemon(true);
        t.start();
    }

    private static CountResult measureOnce(StackPane root, String name) throws Exception {
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

        Thread.sleep(900);

        AtomicReference<Double> t1 = new AtomicReference<>(-1.0);
        CountDownLatch s1 = new CountDownLatch(1);
        Platform.runLater(() -> {
            if (ref.get() != null && ref.get().getPlayer().getCurrentTime() != null) t1.set(ref.get().getPlayer().getCurrentTime().toSeconds());
            s1.countDown();
        });
        s1.await(5, TimeUnit.SECONDS);

        Thread.sleep(1200);

        AtomicReference<Double> t2 = new AtomicReference<>(-1.0);
        CountDownLatch s2 = new CountDownLatch(1);
        Platform.runLater(() -> {
            if (ref.get() != null && ref.get().getPlayer().getCurrentTime() != null)
                t2.set(ref.get().getPlayer().getCurrentTime().toSeconds());
            if (ref.get() != null) ref.get().dispose();
            s2.countDown();
        });
        s2.await(5, TimeUnit.SECONDS);

        return new CountResult(t1.get(), t2.get(), t1.get() >= 0 && t2.get() > t1.get() + 0.2);
    }

    private record CountResult(double t1, double t2, boolean ok) {}
    public static void main(String[] args) { launch(args); }
}