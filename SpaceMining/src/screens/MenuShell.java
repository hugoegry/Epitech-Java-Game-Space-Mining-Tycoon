package screens;

import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import ui.SpaceMenuStyle;
import ui.VideoLayer;
import utils.AppLifecycle;

public final class MenuShell {

    private final Stage stage;
    private final Scene scene;
    private final StackPane root;
    private final StackPane uiLayer;
    private final VideoLayer menuVideo;

    private MenuShell(Stage stage) {
        this.stage = stage;
        AppLifecycle.bindStage(stage);
        root = new StackPane();
        uiLayer = new StackPane();
        menuVideo = new VideoLayer(root, "video_menu_comp.mp4", VideoLayer.Type.BACKGROUND, true);
        menuVideo.play();
        root.getChildren().add(uiLayer);
        Rectangle2D bounds = Screen.getPrimary().getBounds();
        scene = new Scene(root, bounds.getWidth(), bounds.getHeight());
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.show();
    }

    public static void open(Stage stage) {
        new MenuShell(stage).showMainMenu();
    }

    public void showMainMenu() {
        Button newGameBtn = SpaceMenuStyle.menuButton("NOUVELLE PARTIE");
        Button loadBtn = SpaceMenuStyle.menuButton("CHARGER PARTIE");
        Button settingsBtn = SpaceMenuStyle.menuButton("OPTIONS");
        Button quitBtn = SpaceMenuStyle.menuButton("QUITTER");

        newGameBtn.setOnAction(e -> showShipSelect());
        loadBtn.setOnAction(e -> leaveToGame("load", true));
        settingsBtn.setOnAction(e -> showSettings());
        quitBtn.setOnAction(e -> utils.AppLifecycle.exit());

        VBox menuBox = SpaceMenuStyle.menuPanel(newGameBtn, loadBtn, settingsBtn, quitBtn);
        setOverlay(menuBox);
    }

    public void showShipSelect() {
        Label title = new Label("SELECTION VAISSEAU");
        title.setStyle(
            "-fx-text-fill: #ffb45a;"
            + "-fx-font-family: 'Segoe UI', Tahoma, sans-serif;"
            + "-fx-font-size: 20px;"
            + "-fx-font-weight: bold;"
            + "-fx-effect: dropshadow(gaussian, rgba(255,154,60,0.45), 12, 0.2, 0, 0);"
        );

        Button speedBtn = SpaceMenuStyle.menuButton("VAISSEAU RAPIDE");
        Button stockBtn = SpaceMenuStyle.menuButton("TRANSPORTEUR");
        Button discountBtn = SpaceMenuStyle.menuButton("DISCOUNT");
        Button backBtn = SpaceMenuStyle.menuButton("RETOUR");

        speedBtn.setOnAction(e -> leaveToGame("speed", false));
        stockBtn.setOnAction(e -> leaveToGame("stock", false));
        discountBtn.setOnAction(e -> leaveToGame("discount", false));
        backBtn.setOnAction(e -> showMainMenu());

        VBox menuBox = SpaceMenuStyle.menuPanel(speedBtn, stockBtn, discountBtn, backBtn);
        menuBox.getChildren().add(0, title);
        setOverlay(menuBox);

        VideoLayer.preloadVideo("Mercatora_comp.mp4");
        VideoLayer.preloadVideo("Aurea_comp.mp4");
        VideoLayer.preloadVideo("Crysalon_comp.mp4");
        VideoLayer.preloadVideo("Orichara_comp.mp4");
        VideoLayer.preloadVideo("travel_comp.mp4");
    }

    public void showSettings() {
        setOverlay(SettingsScene.buildOverlay(this::showMainMenu));
    }

    private void leaveToGame(String shipType, boolean loadGame) {
        menuVideo.dispose();
        GameScene game = new GameScene(stage, shipType, loadGame);
        stage.setScene(game.getScene());
        stage.setFullScreenExitHint("");
        stage.setFullScreenExitKeyCombination(null);
        stage.setFullScreen(true);
    }

    private void setOverlay(javafx.scene.Node node) {
        uiLayer.getChildren().setAll(node);
        StackPane.setAlignment(node, Pos.CENTER);
    }

    public Scene getScene() { return scene; }
}