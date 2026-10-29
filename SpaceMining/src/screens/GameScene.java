package screens;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Screen;
import javafx.stage.Stage;
import java.util.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.scene.shape.Rectangle;

import entities.*;
import game.GameState;
import ships.Ship_Discount;
import ships.Ship_Speed;
import ships.Ship_Stock;
import ui.*;
import world.ZoneView;
import world.planet.Planet;
import world.Wall;
import world.Zone;
import utils.InteractiveZone;
import utils.InteractiveZoneView;
import world.asset.DecorData;
import utils.Audio;
import utils.AppLifecycle;
import static game.GameState.currentState;
import static game.GameState.DEBUG;

/**
 * Classe représentant la scène principale du jeu.
 * <p>
 * {@code GameScene} gère :
 * <ul>
 *     <li>l'affichage et le rendu du vaisseau et du joueur dans le vaisseau ;</li>
 *     <li>les interactions avec les zones (Hublot, Door, Terminal) et les planètes ;</li>
 *     <li>le déplacement du joueur à l'intérieur du vaisseau et le suivi des entrées clavier/souris ;</li>
 *     <li>la boucle principale de mise à jour et de rendu via {@link AnimationTimer} ;</li>
 *     <li>l'affichage du HUD et des notifications ;</li>
 *     <li>la gestion des voyages entre planètes et des scènes associées.</li>
 * </ul>
 * <p>
 * La classe supporte la lecture de vidéos de fond et de transition, le chargement de ressources,
 * et intègre l'audio ambiant. Elle est liée à {@link GameState} pour stocker les informations de jeu.
 */
public class GameScene {

    // Constantes Resolution ---
    private static final double VIRTUAL_WIDTH = 1920;
    private static final double VIRTUAL_HEIGHT = 1080;

    // --- Composants principaux ---
    private final Scene scene;
    private final Canvas canvas;
    private final GraphicsContext gc;

    public final GameState state = currentState;
    private final Player player;
    private final Stage primaryStage;

    // --- Entrées et états ---
    private final Set<KeyCode> keysPressed = new HashSet<>();
    private Pane uiLayer;
    private SellMenu sellMenu;
    private TravelMenu travelMenu;
    private VideoLayer travelVideo;
    private VideoLayer backgroundVideo;
    private boolean traveling = false;
    private Planet targetPlanet;
    private float travelProgress = 0.0F;
    private double travelDuration = 2.0;

    // --- Ressources ---
    private Image shipTexture;
    private Image shipContourTexture;
    private Image playerUp;
    private Image playerDown;
    private Image playerLeft;
    private Image playerRight;

    // Inputs
    private boolean mouseLeftPressed = false;
    private boolean mouseRightPressed = false;
    private double mouseX, mouseY;
    private enum Direction { UP, DOWN, LEFT, RIGHT }
    private Direction playerDirection = Direction.DOWN;

    private AnimationTimer loop;
    private Point2D savedShipPosition = null;
    private AnimationTimer planetLoop;

    private boolean escHandled = false;
    private boolean f2Handled = false;
    private HUD hud = new HUD();

    private Audio audio = new Audio();
    //endregion

    //region Constructeur

    /**
     * Crée une nouvelle instance de {@code GameScene} et initialise la scène,
     * les composants graphiques, les entrées, le joueur et le vaisseau.
     *
     * @param stage     la fenêtre principale du jeu
     * @param shipType  type de vaisseau choisi par le joueur ("stock", "discount" ou autre)
     */
    public GameScene(Stage stage, String shipType) {
        state.clearState();
        this.primaryStage = stage;
        AppLifecycle.bindStage(stage);

        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        canvas = new Canvas(screenBounds.getWidth(), screenBounds.getHeight());
        gc = canvas.getGraphicsContext2D();

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: black;");
        // Video derriere le canvas, meme logique que le menu (MediaPlayer dedie, pas pause preload)
        this.backgroundVideo = new VideoLayer(root, "Mercatora_comp.mp4", VideoLayer.Type.BACKGROUND, true);
        this.backgroundVideo.play();

        canvas.setMouseTransparent(false);
        root.getChildren().add(this.canvas);

        scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);

        player = new Player(960, 540);
        player.setInputs(keysPressed);


        loadShip(shipType);
        loadAssets();
        createZones();
        currentState.currentPlanet = this.targetPlanet;
        createRessources();
        createPlanets();
        createTools();
        createRessources();
        setupUI(root);
        setupInput();
        setupResizeListener();
        startGameLoop();
        audio.load("/assets/music/space-ambient-atmosphere-438253.mp3");
        if (audio != null) audio.play();
    }

    /**
     * Crée une nouvelle instance de {@code GameScene} à partir d'une sauvegarde.
     *
     * @param stage     la fenêtre principale
     * @param shipType  type de vaisseau
     * @param Save      vrai si la partie doit être chargée depuis la sauvegarde
     */
    public GameScene(Stage stage, String shipType, Boolean Save) {
        this(stage,shipType);
        if (Save) tools.SaveManager.loadGame();
    }

    /**
     * Charge le vaisseau choisi par le joueur.
     *
     * @param type type de vaisseau ("stock", "discount", "speed")
     */
    private void loadShip(String type) {
        switch (type.toLowerCase()) {
            case "stock" -> state.ship = new Ship_Stock();
            case "discount" -> state.ship = new Ship_Discount();
            default -> state.ship = new Ship_Speed();
        }
    }

    /**
     * Charge les textures et sprites utilisés dans la scène.
     */
    private void loadAssets() {
        switch (state.interiorStyle) {
            case CLASSIC -> shipTexture = new Image("assets/ship/spaceship.jpg");
            case INDUSTRIAL -> shipTexture = new Image("assets/ship/spaceship1.jpg");
            case FUTURISTIC -> shipTexture = new Image("assets/ship/spaceship2.jpg");
        }
        shipContourTexture = new Image("assets/contourSpaceship.png");
        playerUp = new Image("assets/astronautTop.png");
        playerDown = new Image("assets/astronaut.png");
        playerLeft = new Image("assets/astronautLeft.png");
        playerRight = new Image("assets/astronautRight.png");
    }

    /**
     * Crée les zones interactives dans le vaisseau.
     */
    private void createZones() {
        new Zone("Hublot", 870.0, 490.0, 180.0, 70.0, Zone.ZoneType.HUBLOT); // haut millieu
        new Zone("Terminal", 1279.0, 620.0, 70.0, 111.0, Zone.ZoneType.PILOT);//droite millieu
        new Zone("Door", 850.0, 808.0, 192.0, 70.0, Zone.ZoneType.DOOR); // bas millieu
    }

    /**
     * Initialise les ressources exploitables dans le jeu.
     */
    private void createRessources() {
        new Ressource("Gold", 50, 100, 0.5f, "Drill");
        new Ressource("Diamond", 200, 500, 1,"Laser");
        new Ressource("Orichalcum", 500, 1000, 2,"Plasma");
        new Ressource("Market", 0, 0.0f, 0,"None");
    }

    /**
     * Crée les planètes et leurs décorations.
     */
    private void createPlanets() {
        List<Object> decorPlanet1 = List.of(
                new DecorData("/assets/space.png", 400.0, 185.0, 1100, 700, false),
                new DecorData("/assets/images/rock/canyon_rocks/canyon_rock3.png", 1100.0, 525.0, 40, 80, true),
                new DecorData("/assets/images/planet/Solid/Icy/Icy_05-512x512.png", 425.0, 550.0, 1050, 800, false)
        );

        List<Object> decorPlanet2 = List.of(
                new DecorData("/assets/space.png", 400.0, 185.0, 1100, 700, false),
                new DecorData("/assets/images/rock/snowy_rocks1/snowy_rock3.png", 1100.0, 525.0, 40, 80, true),
                new DecorData("/assets/images/planet/Solid/Snowy/Snowy_05-512x512.png", 425.0, 550.0, 1050, 800, false)
        );

        List<Object> decorPlanet3 = List.of(
                new DecorData("/assets/space.png", 400.0, 185.0, 1100, 700, false),
                new DecorData("/assets/images/rock/stalagmites/stalagmite3.png", 1100.0, 525.0, 55, 95, true),
                new DecorData("/assets/images/planet/Solid/Magma/Magma_04-512x512.png", 425.0, 550.0, 1050, 800, false)
        );

        List<InteractiveZone> interactiveZones = List.of(
                new InteractiveZone(
                        new Rectangle(1050, 520, 150, 150),
                        () -> currentState.currentPlanet.getResource().startMining(hud)
                )
        );

        currentState.currentPlanet = new Planet("Mercatora", 1, 0, Planet.MoonType.MARKET, Ressource.getMineralByName("Market"), "/assets/hubloView.png", null, "/assets/music/GameOST.mp3", interactiveZones);
        new Planet("Aurea", 1, 3, Planet.MoonType.FARM, Ressource.getMineralByName("Gold"), "/assets/hubloView.png", decorPlanet1, "/assets/music/GameOST.mp3", interactiveZones);
        new Planet("Crysalon", 2, 2, Planet.MoonType.FARM, Ressource.getMineralByName("Diamond"), "/assets/hubloView.png", decorPlanet2, "/assets/music/GameOST.mp3", interactiveZones);
        new Planet("Orichara", 3, 1, Planet.MoonType.FARM, Ressource.getMineralByName("Orichalcum"), "/assets/hubloView.png", decorPlanet3, "/assets/music/GameOST.mp3", interactiveZones);
        System.out.println(currentState.currentPlanet.getName());
    }

    /**
     * Crée les outils disponibles pour le joueur.
     */
    private void createTools() {
        currentState.ship.addTool(new Drill());
        new Laser();
        new Plasma();
    }
    //endregion

    //region Setup

    /**
     * Configure les entrées clavier et souris.
     */
    private void setupInput() {
        scene.setOnKeyPressed(e -> keysPressed.add(e.getCode()));
        scene.setOnKeyReleased(e -> keysPressed.remove(e.getCode()));

        scene.setOnMouseMoved(e -> {
            mouseX = e.getX();
            mouseY = e.getY();
        });
        scene.setOnMouseClicked(e -> {
            if (!DEBUG) return;
            double x = e.getX();
            double y = e.getY();
            System.out.println("Clic à : X = " + x + " | Y = " + y);
        });

        scene.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.PRIMARY) mouseLeftPressed = true;
            if (e.getButton() == MouseButton.SECONDARY) mouseRightPressed = true;
        });

        scene.setOnMouseReleased(e -> {
            if (e.getButton() == MouseButton.PRIMARY) mouseLeftPressed = false;
            if (e.getButton() == MouseButton.SECONDARY) mouseRightPressed = false;
        });
    }

    /**
     * Configure le redimensionnement du canvas principal en fonction de la taille de la fenêtre.
     */
    private void setupResizeListener() {
        scene.widthProperty().addListener((obs, oldW, newW) -> canvas.setWidth(newW.doubleValue()));
        scene.heightProperty().addListener((obs, oldH, newH) -> canvas.setHeight(newH.doubleValue()));
    }

    /**
     * Configure la couche d'interface utilisateur (UI) avec menus de vente et de voyage.
     *
     * @param root le conteneur racine de la scène
     */
    private void setupUI(StackPane root) {
        this.uiLayer = new Pane();
        this.uiLayer.setPickOnBounds(false);
        this.sellMenu = new SellMenu(this.state.ship, state, new UpgradeMenu(state));
        this.sellMenu.setLayoutX(760);
        this.sellMenu.setLayoutY(300);
        this.sellMenu.setVisible(false);
        this.uiLayer.getChildren().add(this.sellMenu);

        this.travelMenu = new TravelMenu(this);
        this.travelMenu.setLayoutX(760);
        this.travelMenu.setLayoutY(200);
        this.travelMenu.setVisible(false);
        this.uiLayer.getChildren().add(this.travelMenu);

        root.getChildren().add(this.uiLayer);
    }
    //endregion

    /**
     * Met à jour la vidéo de fond lorsque le joueur change de planète.
     */
    private void updateBackgroundForPlanet() {
        String bgVideo = currentState.currentPlanet.getName() + "_comp.mp4";
        if (this.backgroundVideo != null) {
            VideoLayer oldVideo = this.backgroundVideo;
            oldVideo.fadeOut(() -> {
                oldVideo.removeFromParent();
                oldVideo.dispose();
            });
        }
        this.backgroundVideo = new VideoLayer((StackPane) this.scene.getRoot(), bgVideo, VideoLayer.Type.BACKGROUND, true);
        this.backgroundVideo.play();
    }

    //region  Boucle Principale

    /**
     * Mise à jour du gameplay, déplacements, interactions, voyages et vérifications des touches.
     *
     * @param dt temps écoulé depuis la dernière mise à jour en secondes
     */
    private void update(double dt) {
        if (keysPressed.contains(KeyCode.F2) && !f2Handled) {
            f2Handled = true;
            tools.SaveManager.saveGame();
            Platform.runLater(() -> showMessage("Partie sauvegardée !"));
        } else if (!keysPressed.contains(KeyCode.F2)) {
            f2Handled = false;
        }

        if (keysPressed.contains(KeyCode.ESCAPE) && !escHandled) {
            tools.SaveManager.saveGame();
            escHandled = true;
            goToMainMenu();
            return;
        } else if (!keysPressed.contains(KeyCode.ESCAPE)) escHandled = false;

        if (!traveling) {
            if (keysPressed.contains(KeyCode.Z)) playerDirection = Direction.UP;
            if (keysPressed.contains(KeyCode.S)) playerDirection = Direction.DOWN;
            if (keysPressed.contains(KeyCode.Q)) playerDirection = Direction.LEFT;
            if (keysPressed.contains(KeyCode.D)) playerDirection = Direction.RIGHT;
            player.move(dt);
        }

        Zone.checkPlayerHoverZone(player.getPosition().getX(), player.getPosition().getY()); // Verif hover zone \\

        if (keysPressed.contains(KeyCode.E) && ZoneView.getHoveredZone() != null) {
            switch (ZoneView.getHoveredZone().getType()) {
                case PILOT -> MenuTravel();
                case HUBLOT -> openHublo();
                case DOOR -> visitPlanet(currentState.currentPlanet);
            }
            keysPressed.remove(KeyCode.E);
        }

        if (this.traveling) {
            this.travelDuration = Math.max(0.5, this.targetPlanet.getTravelTime() * (1.0 + (1.0 - currentState.ship.getSpeed())));
            this.travelProgress += (float) (dt / this.travelDuration);
            if (this.travelProgress >= 1.0F) {
                this.traveling = false;
                this.travelProgress = 1.0F;
                if (this.travelVideo != null) {
                    this.travelVideo.stop();
                    this.travelVideo.removeFromParent();
                    this.travelVideo = null;
                }
                this.updateBackgroundForPlanet();
            }
        }
    }

    /**
     * Démarre la boucle principale du jeu (boucle d'animation).
     */
    private void startGameLoop() {
        loop = new AnimationTimer() {
            private long last = 0;

            @Override
            public void handle(long now) {
                if (last > 0) {
                    double dt = (now - last) / 1e9;
                    update(dt);
                    render(dt);
                }
                last = now;
            }
        };
        loop.start();
    }
    //endregion


    //region Travel

    /**
     * Affiche le menu de voyage vers d'autres planètes.
     */
    private void MenuTravel() {
        this.travelMenu.refresh(this);
        this.travelMenu.show();
    }

    /**
     * Lance le voyage vers la planète cible.
     *
     * @param planet planète destination
     */
    public void startTravelTo(Planet planet) {
        state.currentPlanet = planet;
        this.traveling = true;
        this.targetPlanet = planet;
        this.travelProgress = 0.0F;
        this.travelDuration = (double) planet.getTravelTime();
        this.travelVideo = new VideoLayer((StackPane) this.scene.getRoot(), "travel_comp.mp4", VideoLayer.Type.FOREGROUND, false);
        this.travelVideo.play();
    }

    /**
     * Permet au joueur de se rendre planète (marcket).
     * @param planet planète à visiter
     */
    private void visitPlanet(Planet planet) {
        if (currentState.currentPlanet == null) {
            System.out.println("Aucune planète actuelle définie. [LOG DEBUG HUBLOT OPEN]");
            return;
        }
        if (currentState.currentPlanet.getMoonType() != Planet.MoonType.MARKET) {
            System.out.println("La planet doit etre de type MARKET pour pouvoir accedé au hublo valeur actuel (" + currentState.currentPlanet.getMoonType() + "). [LOG DEBUG MARKET OPEN]");
            showMessage("Planetary access locked. Landings are only permitted from Mercatora.");
            return;
        }
        savedShipPosition = new Point2D(player.getPosition().getX(), player.getPosition().getY()); // Sauvegarde la position du vaisseau avant de quitter \\

        if (loop != null) loop.stop(); // Stop la boucle du vaisseau \\
        if (planet.getPlanetScene() == null) {
            List<Object> decor = List.of(
                    new DecorData("/assets/Planet1.png", 400, 250, 280, 280),
                    new DecorData("/assets/Planet2.png", 950, 500, 250, 250)
            );
            planet.loadAssets("/assets/spaceMarcket.jpg", decor, "/assets/music/GameOST.mp3");
        }

        // Def les actions d entre et de sortie \\
        planet.setOnEnterPlanet(() -> {
            primaryStage.setScene(planet.getPlanetScene());
            primaryStage.setFullScreen(true);
            primaryStage.setFullScreenExitHint("");
            primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
            if (audio != null) audio.stop();
        });

        planet.setOnExitPlanet(() -> {
            primaryStage.setScene(GameScene.this.scene);
            primaryStage.setFullScreen(true);
            primaryStage.setFullScreenExitHint("");
            primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
            if (savedShipPosition != null) player.setPosition(savedShipPosition.getX(),savedShipPosition.getY());
            if (audio != null) audio.play();
            if (loop != null) loop.start();
        });
        planet.enterPlanetView(state);
    }

    /**
     * Ouvre le hublot pour observer la planète si le type est correct.
     */
    private void openHublo() {
        if (currentState.currentPlanet == null) {
            System.out.println("Aucune planète actuelle définie. [LOG DEBUG HUBLOT OPEN]");
            return;
        }
        if (currentState.currentPlanet.getMoonType() != Planet.MoonType.FARM) {
            System.out.println("La planet doit etre de type FARM pour pouvoir accedé au hublo valeur actuel (" + currentState.currentPlanet.getMoonType() + "). [LOG DEBUG HUBLOT OPEN]");
            showMessage("Access denied: observation deck can only be reached from mining planets such as Aurea or Crysalon ...");
            return;
        }
        savedShipPosition = new Point2D(player.getPosition().getX(), player.getPosition().getY());
        currentState.currentPlanet.loadAssets();
        currentState.currentPlanet.setOnEnterPlanet(() -> {
            primaryStage.setScene(currentState.currentPlanet.getPlanetScene());
            primaryStage.setFullScreen(true);
            primaryStage.setFullScreenExitHint("");
            primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
            if (audio != null) audio.stop();
        });

        currentState.currentPlanet.setOnExitPlanet(() -> {
            primaryStage.setScene(GameScene.this.getScene());
            primaryStage.setFullScreen(true);
            primaryStage.setFullScreenExitHint("");
            primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
            if (audio != null) audio.play();
            if (savedShipPosition != null) player.setPosition(savedShipPosition.getX(), savedShipPosition.getY());
            if (loop != null) loop.start();
        });

        if (loop != null) loop.stop();
        currentState.currentPlanet.enterPlanetView();
    }
    //endregion

    //region --- Graphique ---

    /**
     * Affiche un message de notification dans le HUD.
     * @param text texte du message
     */
    private void showMessage(String text) {
        hud.addNotification(text);
    }

    /**
     * Rendu graphique de la scène : vaisseau, joueur, HUD, murs et zones.
     */
    private void render(double dt) {
        double scaleX = canvas.getWidth() / VIRTUAL_WIDTH;
        double scaleY = canvas.getHeight() / VIRTUAL_HEIGHT;
        double scale = Math.min(scaleX, scaleY);
        double offsetX = (canvas.getWidth() - VIRTUAL_WIDTH * scale) / 2;
        double offsetY = (canvas.getHeight() - VIRTUAL_HEIGHT * scale) / 2;

        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.save();
        gc.translate(offsetX, offsetY);
        gc.scale(scale, scale);
        gc.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
        gc.drawImage(shipTexture, 300, 400, 1300, 500);
        Image playerTexture = switch (playerDirection) {
            case UP -> playerUp;
            case DOWN -> playerDown;
            case LEFT -> playerLeft;
            case RIGHT -> playerRight;
        };

        gc.drawImage(playerTexture, player.getPosition().getX() - 32, player.getPosition().getY() - 32, 90, 90);
        gc.drawImage(shipContourTexture, -145, -50, 2150, 1400);
        hud.render(gc); // render HUD \\
        Wall.displayWall(gc);
        ZoneView.render(gc, player, dt);
        gc.restore();
    }

    /** Stoppe loops / audio / video avant de quitter la scene (evite pulse xN). */
    private void disposeScene() {
        if (loop != null) { loop.stop(); loop = null; }
        if (planetLoop != null) { planetLoop.stop(); planetLoop = null; }
        if (audio != null) audio.stop();
        if (backgroundVideo != null) { backgroundVideo.dispose(); backgroundVideo = null; }
        if (travelVideo != null) { travelVideo.dispose(); travelVideo = null; }
        Zone.clearZones();
        ZoneView.setHoveredZone();
        ZoneView.resetpulseTimer();
        InteractiveZoneView.resetPulseTimer();
    }

    /**
     * Retourne à l'écran principal du menu.
     */
    private void goToMainMenu() {
        disposeScene();
        MenuShell.open(primaryStage);
    }

    /**
     * Retourne la scène JavaFX associée à ce GameScene.
     *
     * @return la scène principale
     */
    public Scene getScene() { return scene; }
    //endregion
}