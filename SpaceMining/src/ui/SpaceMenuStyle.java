package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * Boutons menu
 */
public final class SpaceMenuStyle {

    private static final String FONT = "'Segoe UI', 'Tahoma', sans-serif";
    private static final double BTN_WIDTH = 340;
    private static final double BTN_HEIGHT = 56;
    private static final String SHAPE = "M 18,0 L 320,0 L 340,14 L 340,42 L 322,56 L 18,56 L 0,42 L 0,14 Z";

    private SpaceMenuStyle() {}

    public static Button menuButton(String label) {
        Button btn = new Button(label);
        applyMenuButton(btn);
        return btn;
    }

    public static void applyMenuButton(Button btn) {
        btn.setMinWidth(BTN_WIDTH);
        btn.setPrefWidth(BTN_WIDTH);
        btn.setMaxWidth(BTN_WIDTH);
        btn.setMinHeight(BTN_HEIGHT);
        btn.setPrefHeight(BTN_HEIGHT);
        btn.setFocusTraversable(false);
        btn.setStyle(baseStyle());

        DropShadow glow = new DropShadow(18, Color.web("#ff9a3c88"));
        glow.setSpread(0.15);
        btn.setEffect(glow);

        btn.setOnMouseEntered(e -> {
            btn.setStyle(hoverStyle());
            glow.setColor(Color.web("#ffc078cc"));
            glow.setRadius(26);
        });
        btn.setOnMouseExited(e -> {
            btn.setStyle(baseStyle());
            glow.setColor(Color.web("#ff9a3c88"));
            glow.setRadius(18);
        });
        btn.setOnMousePressed(e -> btn.setStyle(pressedStyle()));
        btn.setOnMouseReleased(e -> btn.setStyle(btn.isHover() ? hoverStyle() : baseStyle()));
    }

    public static VBox menuPanel(Button... buttons) {
        VBox box = new VBox(16);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(12));
        box.setFillWidth(true);
        box.setStyle("-fx-background-color: transparent;");
        box.getChildren().addAll(buttons);
        return box;
    }

    private static String baseStyle() {
        return "-fx-background-color: linear-gradient(to bottom, #2a241c 0%, #15110d 55%, #0c0a08 100%);"
            + "-fx-text-fill: #ffe2c0;"
            + "-fx-font-family: " + FONT + ";"
            + "-fx-font-size: 17px;"
            + "-fx-font-weight: bold;"
            + "-fx-shape: \"" + SHAPE + "\";"
            + "-fx-border-color: #c47a32;"
            + "-fx-border-width: 1.8;"
            + "-fx-background-insets: 0;"
            + "-fx-border-insets: 0;"
            + "-fx-padding: 0 28 0 28;"
            + "-fx-cursor: hand;"
            + "-fx-alignment: center;";
    }

    private static String hoverStyle() {
        return "-fx-background-color: linear-gradient(to bottom, #4a3420 0%, #2a1c12 50%, #17100a 100%);"
            + "-fx-text-fill: #fff4e6;"
            + "-fx-font-family: " + FONT + ";"
            + "-fx-font-size: 17px;"
            + "-fx-font-weight: bold;"
            + "-fx-shape: \"" + SHAPE + "\";"
            + "-fx-border-color: #ffb45a;"
            + "-fx-border-width: 2.2;"
            + "-fx-background-insets: 0;"
            + "-fx-border-insets: 0;"
            + "-fx-padding: 0 28 0 28;"
            + "-fx-cursor: hand;"
            + "-fx-alignment: center;";
    }

    private static String pressedStyle() {
        return "-fx-background-color: linear-gradient(to bottom, #1a120c, #0a0806);"
            + "-fx-text-fill: #ffc98a;"
            + "-fx-font-family: " + FONT + ";"
            + "-fx-font-size: 17px;"
            + "-fx-font-weight: bold;"
            + "-fx-shape: \"" + SHAPE + "\";"
            + "-fx-border-color: #a85f20;"
            + "-fx-border-width: 2;"
            + "-fx-background-insets: 0;"
            + "-fx-border-insets: 0;"
            + "-fx-padding: 0 28 0 28;"
            + "-fx-cursor: hand;"
            + "-fx-alignment: center;";
    }
}