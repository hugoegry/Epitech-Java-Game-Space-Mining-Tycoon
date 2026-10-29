package utils;

import javafx.application.Platform;
import javafx.stage.Stage;
import ui.VideoLayer;

public final class AppLifecycle {

    private AppLifecycle() {}
    public static void bindStage(Stage stage) {
        AppBrand.apply(stage);
        stage.setOnCloseRequest(e -> {
            e.consume();
            exit();
        });
    }

    public static void exit() {
        try { Audio.stopAll(); } catch (Exception ignored) {}
        try { VideoLayer.shutdownAll(); } catch (Exception ignored) {}
        try {
            Platform.exit();
        } catch (Exception ignored) {}
        System.exit(0);
    }
}