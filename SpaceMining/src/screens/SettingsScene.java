package screens;

import game.GameState;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import ui.SpaceMenuStyle;

/**
 * Overlay parametres (pose dans MenuShell, video menu reste active).
 */
public class SettingsScene {
    private SettingsScene() {}
    public static VBox buildOverlay(Runnable onBack) {
        VBox root = new VBox(30);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(50));
        root.setMaxWidth(900);
        root.setStyle(
            "-fx-background-color: rgba(8, 14, 24, 0.82);"
            + "-fx-background-radius: 8;"
            + "-fx-border-color: #c47a32;"
            + "-fx-border-width: 1.5;"
            + "-fx-border-radius: 8;"
        );

        GameState state = GameState.getCurrentState();

        Label title = new Label("PARAMETRES");
        title.setStyle("-fx-font-size: 36px; -fx-text-fill: #ffe2c0; -fx-font-weight: bold;");

        VBox keysBox = new VBox(15);
        keysBox.setAlignment(Pos.CENTER);
        keysBox.setPadding(new Insets(20));
        keysBox.setStyle("-fx-background-color: rgba(22,36,71,0.75); -fx-background-radius: 10; -fx-border-color: #1f4287; -fx-border-width: 2;");

        Label keysTitle = new Label("Touches de controle");
        keysTitle.setStyle("-fx-font-size: 22px; -fx-text-fill: #ffffff; -fx-font-weight: bold;");
        keysBox.getChildren().add(keysTitle);

        String[] actions = {"UP", "DOWN", "LEFT", "RIGHT", "INTERACT", "MENU"};
        for (String action : actions) {
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER);

            Label actionLabel = new Label(action + " : ");
            actionLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 18px;");

            KeyCode code = state.keyBindings.get(action);
            String keyName = (code != null) ? code.getName() : "Non assigne";
            Button changeBtn = new Button(keyName);
            changeBtn.setStyle("-fx-background-color: #1f4287; -fx-text-fill: #ffffff; -fx-font-size: 16px;");
            changeBtn.setOnMouseEntered(e -> changeBtn.setStyle("-fx-background-color: #3a6ea5; -fx-text-fill: #ffffff; -fx-font-size: 16px;"));
            changeBtn.setOnMouseExited(e -> changeBtn.setStyle("-fx-background-color: #1f4287; -fx-text-fill: #ffffff; -fx-font-size: 16px;"));

            changeBtn.setOnAction(e -> {
                changeBtn.setText("Appuyez sur une touche...");
                root.setOnKeyPressed(keyEvent -> {
                    state.keyBindings.put(action, keyEvent.getCode());
                    changeBtn.setText(keyEvent.getCode().getName());
                    root.setOnKeyPressed(null);
                });
                root.requestFocus();
            });

            row.getChildren().addAll(actionLabel, changeBtn);
            keysBox.getChildren().add(row);
        }

        VBox shipBox = new VBox(15);
        shipBox.setAlignment(Pos.CENTER);
        shipBox.setPadding(new Insets(20));
        shipBox.setStyle("-fx-background-color: rgba(22,36,71,0.75); -fx-background-radius: 10; -fx-border-color: #1f4287; -fx-border-width: 2;");

        Label shipTitle = new Label("Style du vaisseau");
        shipTitle.setStyle("-fx-font-size: 22px; -fx-text-fill: #ffffff; -fx-font-weight: bold;");
        shipBox.getChildren().add(shipTitle);

        HBox shipsPreview = new HBox(30);
        shipsPreview.setAlignment(Pos.CENTER);

        String[] shipTypes = {"CLASSIC", "INDUSTRIAL", "FUTURISTIC"};
        String[] shipImages = {
            "assets/ship/spaceship.jpg",
            "assets/ship/spaceship1.jpg",
            "assets/ship/spaceship2.jpg"
        };

        StackPane[] shipPanes = new StackPane[3];
        for (int i = 0; i < shipTypes.length; i++) {
            int index = i;
            ImageView shipView = new ImageView(new Image(shipImages[i]));
            shipView.setFitWidth(200);
            shipView.setFitHeight(120);

            StackPane shipPane = new StackPane(shipView);
            shipPane.setStyle("-fx-border-color: transparent; -fx-border-width: 4; -fx-border-radius: 5;");
            if (state.interiorStyle.name().equals(shipTypes[i])) {
                shipPane.setStyle("-fx-border-color: #00ffff; -fx-border-width: 4; -fx-border-radius: 5;");
            }
            shipPane.setOnMouseClicked(e -> {
                state.interiorStyle = GameState.ShipInteriorStyle.valueOf(shipTypes[index]);
                for (StackPane p : shipPanes) {
                    p.setStyle("-fx-border-color: transparent; -fx-border-width: 4; -fx-border-radius: 5;");
                }
                shipPane.setStyle("-fx-border-color: #00ffff; -fx-border-width: 4; -fx-border-radius: 5;");
            });
            shipPanes[i] = shipPane;
            shipsPreview.getChildren().add(shipPane);
        }
        shipBox.getChildren().add(shipsPreview);

        VBox debugBox = buildDebugBox();

        Button backBtn = SpaceMenuStyle.menuButton("RETOUR");
        backBtn.setOnAction(e -> { if (onBack != null) onBack.run(); });

        root.getChildren().addAll(title, keysBox, shipBox, debugBox, backBtn);
        root.setFocusTraversable(true);
        return root;
    }

    private static VBox buildDebugBox() {
        VBox debugBox = new VBox(12);
        debugBox.setAlignment(Pos.CENTER);
        debugBox.setPadding(new Insets(20));
        debugBox.setStyle("-fx-background-color: rgba(22,36,71,0.75); -fx-background-radius: 10; -fx-border-color: #c47a32; -fx-border-width: 2;");

        Label debugTitle = new Label("Mode debug");
        debugTitle.setStyle("-fx-font-size: 22px; -fx-text-fill: #ffe2c0; -fx-font-weight: bold;");

        Label hint = new Label("Argent infini + toutes les planetes debloquees");
        hint.setStyle("-fx-font-size: 13px; -fx-text-fill: #b8c5d6;");

        Label stateLabel = new Label(GameState.DEBUG ? "ON" : "OFF");
        stateLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + (GameState.DEBUG ? "#7CFF9A;" : "#ffb45a;"));

        StackPane toggle = createThemeToggle(GameState.DEBUG);
        HBox row = new HBox(18, toggle, stateLabel);
        row.setAlignment(Pos.CENTER);

        toggle.setOnMouseClicked(e -> {
            if (!GameState.DEBUG) {
                if (!askDebugCode()) return;
                GameState.getCurrentState().enableDebugMode();
            } else {
                GameState.getCurrentState().disableDebugMode();
            }
            boolean on = GameState.DEBUG;
            refreshToggleVisual(toggle, on);
            stateLabel.setText(on ? "ON" : "OFF");
            stateLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + (on ? "#7CFF9A;" : "#ffb45a;"));
        });

        debugBox.getChildren().addAll(debugTitle, hint, row);
        return debugBox;
    }

    private static boolean askDebugCode() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("SPACE MINING TYCOON");
        dialog.setHeaderText("Activation mode debug");
        dialog.setContentText("Code secret :");
        dialog.getEditor().setPromptText("****");
        var opt = dialog.showAndWait();
        if (opt.isEmpty()) return false;
        String code = opt.get().trim();
        if (!GameState.DEBUG_CODE.equals(code)) {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Debug");
            alert.setHeaderText(null);
            alert.setContentText("Code incorrect.");
            alert.showAndWait();
            return false;
        }
        return true;
    }

    private static StackPane createThemeToggle(boolean on) {
        StackPane pane = new StackPane();
        pane.setPrefSize(72, 34);
        pane.setMaxSize(72, 34);
        pane.setCursor(javafx.scene.Cursor.HAND);

        Rectangle track = new Rectangle(72, 34);
        track.setArcWidth(34);
        track.setArcHeight(34);
        track.setStroke(Color.web("#c47a32"));
        track.setStrokeWidth(2);
        track.setId("track");

        Circle knob = new Circle(12);
        knob.setFill(Color.web("#ffe2c0"));
        knob.setId("knob");
        StackPane.setAlignment(knob, on ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        StackPane.setMargin(knob, new Insets(0, 6, 0, 6));

        pane.getChildren().addAll(track, knob);
        refreshToggleVisual(pane, on);
        return pane;
    }

    private static void refreshToggleVisual(StackPane pane, boolean on) {
        Rectangle track = (Rectangle) pane.lookup("#track");
        Circle knob = (Circle) pane.lookup("#knob");
        if (track != null) {
            track.setFill(Color.web(on ? "#4a3420" : "#15110d"));
        }
        if (knob != null) {
            StackPane.setAlignment(knob, on ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
            knob.setFill(Color.web(on ? "#ffb45a" : "#ffe2c0"));
        }
    }
}