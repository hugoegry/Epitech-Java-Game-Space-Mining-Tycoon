package utils;

import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public final class AppBrand {
    public static final String APP_NAME = "SPACE MINING TYCOON";
    private static final String[] ICON_PATHS = {
        "/assets/icon/app_icon.gif",
        "/assets/icon/app_icon.png",
        "/assets/icon/app_icon.jpg"
    };

    private static List<Image> cachedIcons;
    private AppBrand() {}
    public static void apply(Stage stage) {
        if (stage == null) return;
        stage.setTitle(APP_NAME);
        List<Image> icons = loadIcons();
        if (!icons.isEmpty()) {
            stage.getIcons().setAll(icons);
        }
    }

    private static List<Image> loadIcons() {
        if (cachedIcons != null) return cachedIcons;
        List<Image> list = new ArrayList<>();
        for (String path : ICON_PATHS) {
            try (InputStream in = AppBrand.class.getResourceAsStream(path)) {
                if (in == null) continue;
                Image img = new Image(in);
                if (!img.isError()) {
                    list.add(img);
                    System.out.println("[AppBrand] icon OK : " + path);
                }
            } catch (Exception e) {
                System.err.println("[AppBrand] icon fail : " + path + " -> " + e.getMessage());
            }
        }
        cachedIcons = list;
        return list;
    }
}