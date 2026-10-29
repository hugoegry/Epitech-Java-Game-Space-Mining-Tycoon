    package world.planet;

    import entities.Ressource;
    import game.GameState;
    import javafx.scene.Scene;
    import javafx.scene.input.KeyCode;
    import javafx.scene.layout.Pane;
    import ui.SellMenu;
    import ui.UpgradeMenu;
    import utils.InteractiveZone;

    import java.util.ArrayList;
    import java.util.List;

    import ui.HUD;
    import utils.Audio;
    import javafx.scene.canvas.Canvas;
    import javafx.scene.canvas.GraphicsContext;
    import javafx.animation.AnimationTimer;

    import static game.GameState.currentState;
    import utils.InteractiveZoneView;

    /**
     * Représente une planète explorable dans l’univers du jeu.
     * <p>
     * Une instance de {@code Planet} regroupe l’ensemble des informations de gameplay
     * (niveau requis, difficulté, ressource locale), les assets visuels et audio,
     * les zones interactives ainsi que les différents contrôleurs nécessaires
     * à la construction et au fonctionnement de la scène.
     *
     * <p>
     * Concrètement, cette classe prend en charge :
     * <ul>
     *     <li>le chargement de l’arrière-plan, des éléments de décor et de l’ambiance sonore ;</li>
     *     <li>la construction de la scène JavaFX via {@link PlanetView} ;</li>
     *     <li>l’initialisation des interactions utilisateur et du rover ;</li>
     *     <li>la gestion des événements d’entrée et de sortie de la planète ;</li>
     *     <li>les informations descriptives liées à la ressource et au type de lune.</li>
     * </ul>
     *
     * Une fois la planète chargée, la scène JavaFX peut être récupérée via {@link #getPlanetScene()}.
     */
    public class Planet {

        private static final double VIRTUAL_WIDTH = 1920;
        private static final double VIRTUAL_HEIGHT = 1080;

        private static List<Planet> planets = new ArrayList<>();

        // Données de gameplay
        private String name;
        private int requiredLevel;
        private int difficulty;
        private Ressource planetResource;
        private MoonType moonType;
        private int timeTravelling;

        // Données visuelles et audio
        private String backgroundPath;
        private List<?> decorList;
        private String audioPath;
        private List<InteractiveZone> zones;

        // Composants
        private PlanetView view;
        private Audio audio;
        private PlanetAssetsLoader loader;
        private InteractiveController interactions;
        private RoverController rover;

        // Events
        private Runnable onEnterPlanet;
        private Runnable onExitPlanet;

        // Scene exposée
        private Scene planetScene;

        private AnimationTimer loop;
        private Canvas hudCanvas;
        private GraphicsContext gc;
        private HUD hud;

        public enum MoonType { MECHANICAL, MARKET, FARM }


        /**
         * Crée une planète contenant uniquement les données de gameplay.
         *
         * @param name           nom de la planète
         * @param requiredLevel  niveau minimum requis pour y accéder
         * @param difficulty     difficulté de la planète
         * @param moonType       type de biome/lune associé
         */
        public Planet(String name, int requiredLevel, int difficulty, MoonType moonType) {
            this.name = name;
            this.requiredLevel = requiredLevel;
            this.difficulty = difficulty;
            this.moonType = moonType;
            if (this.difficulty == 1) {
                this.timeTravelling = 2;
            } else if (this.difficulty == 2) {
                this.timeTravelling = 5;
            } else if (this.difficulty == 3) {
                this.timeTravelling = 10;
            } else {
                this.timeTravelling = 0;
            }

            addPlanet(this);
        }

        /**
         * Crée une planète contenant uniquement les données de gameplay.
         *
         * @param name           nom de la planète
         * @param requiredLevel  niveau minimum requis pour y accéder
         * @param difficulty     difficulté de la planète
         * @param moonType       type de biome/lune associé
         * @param ressource       ressource disponible sur la planète
         */
        public Planet(String name, int requiredLevel, int difficulty, MoonType moonType, Ressource ressource) {
            this(name, requiredLevel, difficulty, moonType);
            this.planetResource = ressource;
        }

        /**
         * Crée une planète en définissant les données de gameplay
         * ainsi que les assets visuels et audio.
         *
         * @param name            nom de la planète
         * @param requiredLevel   niveau requis du joueur
         * @param difficulty      niveau de difficulté
         * @param moonType        type de biome/lune
         * @param ressource       ressource disponible sur la planète
         * @param backgroundPath  chemin vers l’image d’arrière-plan
         * @param decorList       liste des éléments de décor
         * @param audioPath       chemin vers l’ambiance sonore
         */
        public Planet(String name, int requiredLevel, int difficulty, MoonType moonType, Ressource  ressource, String backgroundPath, List<?> decorList, String audioPath) {
            this(name, requiredLevel, difficulty, moonType, ressource);
            this.backgroundPath = backgroundPath;
            this.decorList = decorList;
            this.audioPath = audioPath;
        }

        /**
         * Crée une planète en définissant les données de gameplay
         * ainsi que les assets visuels et audio.
         *
         * @param name            nom de la planète
         * @param requiredLevel   niveau requis du joueur
         * @param difficulty      niveau de difficulté
         * @param moonType        type de biome/lune
         * @param backgroundPath  chemin vers l’image d’arrière-plan
         * @param decorList       liste des éléments de décor
         * @param audioPath       chemin vers l’ambiance sonore
         */
        public Planet(String name, int requiredLevel, int difficulty, MoonType moonType, String backgroundPath, List<?> decorList, String audioPath) {
            this(name, requiredLevel, difficulty, moonType);
            this.backgroundPath = backgroundPath;
            this.decorList = decorList;
            this.audioPath = audioPath;
        }


        /**
         * Crée une planète avec assets visuels, audio et zones interactives.
         *
         * @param name            nom de la planète
         * @param requiredLevel   niveau minimum requis
         * @param difficulty      difficulté
         * @param moonType        type de biome/lune
         * @param ressource       ressource disponible sur la planète
         * @param backgroundPath  chemin vers l’arrière-plan
         * @param decorList       liste des éléments de décor
         * @param audioPath       chemin vers l’audio
         * @param zones           zones interactives présentes sur la planète
         */
        public Planet(String name, int requiredLevel, int difficulty, MoonType moonType, Ressource ressource, String backgroundPath, List<?> decorList, String audioPath, List<InteractiveZone> zones) {
            this(name, requiredLevel, difficulty, moonType, ressource, backgroundPath, decorList, audioPath);
            this.zones = zones;
        }

        /**
         * Charge l’ensemble des assets définis par les constructeurs :
         * arrière-plan, décor, audio et zones interactives.
         * <p>
         * Construit également la scène JavaFX et initialise les contrôleurs.
         */
        public void loadAssets() {
            this.loadAssets(this.backgroundPath, this.decorList, this.audioPath, this.zones);
        }

        /**
         * Charge les assets sans zones interactives.
         *
         * @param backgroundPath chemin de l’arrière-plan
         * @param decorList      liste des décors
         * @param audioPath      chemin du fichier audio
         */
        public void loadAssets(String backgroundPath, List<?> decorList, String audioPath) {
            this.loadAssets(backgroundPath, decorList, audioPath, null);
        }

        /**
         * Charge tous les assets et construit la scène interne de la planète.
         *
         * @param backgroundPath chemin de l’arrière-plan
         * @param decorList      liste des éléments de décor
         * @param audioPath      chemin de l’ambiance sonore
         * @param zones          zones interactives à intégrer à la scène
         */
        public void loadAssets(String backgroundPath, List<?> decorList, String audioPath, List<InteractiveZone> zones) {
            loader = new PlanetAssetsLoader();
            audio  = new Audio();
            view   = new PlanetView();
            Pane rootPane = loader.load(backgroundPath, decorList, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);

            view.initialize(rootPane);
            this.planetScene = view.buildScene();

            // === HUD GESTION ===
            System.out.println("Initializing HUD canvas...");

            // Initialisation du canvas HUD
            hudCanvas = new Canvas(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
            gc = hudCanvas.getGraphicsContext2D();

            // Configuration du canvas
            hudCanvas.setMouseTransparent(true);
            hudCanvas.setVisible(true);
            hudCanvas.setOpacity(1.0);

            // IMPORTANT : Ajouter le canvas à la scène
            rootPane.getChildren().add(hudCanvas);
            hudCanvas.toFront();

            System.out.println("Canvas added to scene, children count: " + rootPane.getChildren().size());

            // Le HUD sera initialisé dans enterPlanetView
            hud = null;

            // Setup resizing
            setupHUDCanvasResizing();

            audio.load(audioPath);
            interactions = new InteractiveController(rootPane, zones);
            if (this.moonType == MoonType.FARM) rover = new RoverController(rootPane, interactions);
            new RoverPathEditor(rootPane, view);
            attachSceneEvents();
        }

        // === Scene events ===

        /**
         * Attache les différents listeners clavier/souris à la scène de la planète.
         * <ul>
         *     <li>ESCAPE → quitter la planète</li>
         *     <li>Routage des événements clavier vers le rover (si présent)</li>
         *     <li>Clic souris → gestion des zones interactives</li>
         *     <li>Changements de taille → mise à l’échelle dynamique</li>
         * </ul>
         */
        private void attachSceneEvents() {
            planetScene.setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.ESCAPE) exitPlanetView();
                if (rover != null) rover.onKeyPressed(e.getCode());
            });

            planetScene.setOnKeyReleased(e -> {
                if (rover != null) rover.onKeyReleased(e.getCode());
            });

            planetScene.setOnMouseClicked(e -> {
                interactions.handleClick(view.toVirtual(e.getSceneX(), e.getSceneY()));
            });

            planetScene.widthProperty().addListener((obs, o, n) -> view.adjustScale());
            planetScene.heightProperty().addListener((obs, o, n) -> view.adjustScale());
        }

        // === Entrée / sortie planète ===

        /**
         * Appelé lorsque le joueur entre dans la scène de la planète.
         * <p>
         * Démarre l’audio, le rover et déclenche le callback associé.
         */
        public void enterPlanetView() { this.enterPlanetView(GameState.getCurrentState()); }

        public void enterPlanetView(GameState state) {
            if (audio != null) audio.play();
            if (rover != null) rover.start();
            if (onEnterPlanet != null) onEnterPlanet.run();

            if (hud == null) hud = new HUD(); // init HUD avec le bon GameState \\

            HUD.setHud(hud);

            if (hudCanvas != null && view.getRootPane().getChildren().contains(hudCanvas)) hudCanvas.toFront(); // met le canva au premier plan \\

            if (this.getMoonType() == MoonType.MARKET) {
                UpgradeMenu upgradeMenu = new UpgradeMenu(state);
                SellMenu sellMenu = new SellMenu(state.ship, state, upgradeMenu);
                view.getRootPane().getChildren().add(sellMenu);
                upgradeMenu.setTranslateX(960);
                view.getRootPane().getChildren().add(upgradeMenu);
            }

            if (loop != null) { loop.stop(); loop = null; }
            loop = new AnimationTimer() {
                private long last = 0;

                @Override
                public void handle(long now) {
                    if (last > 0) {
                        double dt = (now - last) / 1e9;
                        update(dt);
                    }
                    last = now;
                }
            };
            loop.start();
        }

        /**
         * Appelé lorsque le joueur quitte la planète.
         * <p>
         * Arrête l’audio, le rover, et exécute le callback de sortie.
         */
        public void exitPlanetView() {
            if (audio != null) audio.stop();
            if (rover != null) rover.stop();
            if (onExitPlanet != null) onExitPlanet.run();
            if (loop != null) {
                loop.stop();
                loop = null;
            }
        }

        // === Getters setters ===

        /** Définit le callback exécuté lors de l’entrée dans la planète. */
        public void setOnEnterPlanet(Runnable r) { this.onEnterPlanet = r; }

        /** Définit le callback exécuté lors de la sortie de la planète. */
        public void setOnExitPlanet(Runnable r) { this.onExitPlanet = r; }


        /** @return la scène JavaFX construite pour cette planète */
        public Scene getPlanetScene() { return planetScene; }

        /** @return le nom de la planète */
        public String getName() { return name; }

        /** @return la ressource disponible sur la planète */
        public Ressource getResource() { return this.planetResource; }

        /** @return le niveau requis pour accéder à cette planète */
        public int getRequiredLevel() { return this.requiredLevel; }

        /** @return la difficulté de la planète */
        public float getDifficulty() { return this.difficulty; }

        /** @return le type de biome/lune associé */
        public MoonType getMoonType() { return this.moonType; }

        public int getTravelTime() { return this.timeTravelling; }

        // getter setter statics \\
        public static List<Planet> getPlanets() { return planets; }
        public static void addPlanet(Planet planet) { planets.add(planet); }

        public static void clearPlanets() { planets.clear(); }

        // fall Back
        public int getResearchNeeded() { return this.getRequiredLevel(); }
        public Ressource getPrimary() { return this.getResource(); }

        private void update(double dt) {
            if (hud == null || gc == null) {
                System.out.println("HUD or GC is null!");
                return;
            }

            hudCanvas.toFront(); // Mettre le canvas au premier plan \\

            if (hud != null && gc != null) hud.render(gc); // update HUD \\

            // Nettoyage du canvas
            gc.clearRect(0, 0, hudCanvas.getWidth(), hudCanvas.getHeight());
            gc.save();

            if (view != null) {
                double scaleX = hudCanvas.getWidth() / VIRTUAL_WIDTH;
                double scaleY = hudCanvas.getHeight() / VIRTUAL_HEIGHT;
                double scale = Math.min(scaleX, scaleY);
                double offsetX = (hudCanvas.getWidth() - VIRTUAL_WIDTH * scale) / 2;
                double offsetY = (hudCanvas.getHeight() - VIRTUAL_HEIGHT * scale) / 2;

                gc.translate(offsetX, offsetY);
                gc.scale(scale, scale);
            }

            // Rendu du HUD \\
            if(this.getMoonType() == MoonType.FARM) {
                hud.render(gc);
                currentState.currentPlanet.getResource().checkMining(dt, hud);
            }
            gc.restore();
            if (zones != null && !zones.isEmpty() && currentState.currentPlanet.getMoonType() == MoonType.FARM) InteractiveZoneView.render(gc, zones, dt);
        }

        /**
         * Configure le redimensionnement automatique du canvas HUD
         */
        private void setupHUDCanvasResizing() {
            if (hudCanvas != null && planetScene != null) {
                planetScene.widthProperty().addListener((obs, oldVal, newVal) -> {
                    hudCanvas.setWidth(newVal.doubleValue());
                });

                planetScene.heightProperty().addListener((obs, oldVal, newVal) -> {
                    hudCanvas.setHeight(newVal.doubleValue());
                });
            }
        }

    }