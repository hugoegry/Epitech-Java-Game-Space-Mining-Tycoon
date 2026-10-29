package screens;

import javafx.stage.Stage;

public class ShipSelectionScene {

    public static void startGame(Stage stage, String type, boolean loadGame) {
        GameScene game = new GameScene(stage, type, loadGame);
        stage.setScene(game.getScene());
        stage.setFullScreenExitHint("");
        stage.setFullScreenExitKeyCombination(null);
        stage.setFullScreen(true);
    }
}