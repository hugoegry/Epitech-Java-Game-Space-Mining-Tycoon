import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class MediaSmoke extends Application {
    static String path;

    @Override
    public void start(Stage stage) {
        try {
            File f = new File(path);
            System.out.println("FILE=" + f.getAbsolutePath() + " exists=" + f.exists() + " size=" + f.length());
            Media media = new Media(f.toURI().toString());
            MediaPlayer player = new MediaPlayer(media);
            MediaView view = new MediaView(player);
            view.setFitWidth(640);
            view.setFitHeight(360);
            StackPane root = new StackPane(view);
            stage.setScene(new Scene(root, 640, 360));
            stage.show();

            player.setOnError(() -> {
                System.err.println("ERROR=" + player.getError());
                if (player.getError() != null) player.getError().printStackTrace();
                Platform.exit();
                System.exit(2);
            });
            player.setOnReady(() -> {
                System.out.println("READY dur=" + player.getTotalDuration());
                player.play();
            });
            player.statusProperty().addListener((a,b,c) -> System.out.println("STATUS " + b + " -> " + c));

            Thread t = new Thread(() -> {
                try {
                    Thread.sleep(2500);
                    CountDownLatch l = new CountDownLatch(1);
                    double[] times = new double[1];
                    Platform.runLater(() -> {
                        times[0] = player.getCurrentTime().toSeconds();
                        System.out.println("TIME=" + times[0] + " STATUS=" + player.getStatus());
                        l.countDown();
                    });
                    l.await(5, TimeUnit.SECONDS);
                    int code = times[0] > 0.5 ? 0 : 1;
                    Platform.runLater(() -> { stage.close(); Platform.exit(); System.exit(code); });
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(3);
                }
            });
            t.setDaemon(true);
            t.start();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(4);
        }
    }

    public static void main(String[] args) {
        path = args.length > 0 ? args[0] : System.getProperty("java.io.tmpdir") + "/smt-videos/menu_noaudio.mp4";
        launch(args);
    }
}
