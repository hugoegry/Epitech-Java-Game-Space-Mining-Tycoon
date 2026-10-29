import javafx.application.Application;
import javafx.stage.Stage;
import screens.MenuShell;
import utils.AppBrand;
import utils.AppLifecycle;

/**
 * Point d entree
 */
public class Main extends Application {

    @Override
    public void start(Stage stage) {
        AppBrand.apply(stage);
        AppLifecycle.bindStage(stage);
        MenuShell.open(stage);
    }

    @Override
    public void stop() {
        try { utils.Audio.stopAll(); } catch (Exception ignored) {}
        try { ui.VideoLayer.shutdownAll(); } catch (Exception ignored) {}
    }

    public static void main(String[] args) {
        launch(args);
    }
}