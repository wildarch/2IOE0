package nl.tue.c2IOE0.group5.engine;

import nl.tue.c2IOE0.group5.engine.controller.Controller;
import nl.tue.c2IOE0.group5.engine.controller.input.InputHandler;
import nl.tue.c2IOE0.group5.engine.controller.input.events.Listener;
import nl.tue.c2IOE0.group5.engine.objects.Camera;
import nl.tue.c2IOE0.group5.engine.provider.Provider;
import nl.tue.c2IOE0.group5.engine.rendering.Hud;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.engine.rendering.ShaderException;
import nl.tue.c2IOE0.group5.engine.rendering.Window;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jorren Hendriks.
 */
public class Engine {

    private final static int TARGET_TPS = 20;
    private final static int TARGET_FPS = 75;

    private final static int TPS_INTERVAL = 1000 / TARGET_TPS;
    private final static int FPS_INTERVAL = 1000 / TARGET_FPS;

    private boolean running = false;

    private Window window;
    private Renderer renderer;
    private Hud hud;
    private InputHandler inputHandler;
    private Timer timer;
    private Camera camera;

    private List<Provider> providers;
    private List<Controller> controllers;

    public Engine() {
        window = new Window("Tower Defence", 1600, 900, false, new Window.Options());
        renderer = new Renderer();
        hud = new Hud();
        inputHandler = new InputHandler();
        timer = new Timer();
        camera = new Camera(this);

        providers = new ArrayList<>();
        controllers = new ArrayList<>();
    }

    /**
     * Run the Engine, should be invoked after initializing and attaching {@link Controller}s and {@link Provider}s.
     */
    public void run() throws ShaderException, IOException, Exception {
        try {
            running = true;
            init();
            loop();
        } finally {
            cleanup();
        }
    }

    /**
     * Initialize necessary objects
     */
    private void init() throws ShaderException, IOException, Exception {
        timer.init();
        window.init();
        renderer.init(window);
        renderer.setActiveCamera(camera);
        hud.init(window);

        inputHandler.init(window);
        providers.forEach(provider -> provider.init(this));
        controllers.forEach(controller -> controller.init(this));
    }

    /**
     * Cleanup used objects
     */
    private void cleanup() {
        window.cleanup();
        renderer.cleanup();
        hud.cleanup();
    }

    /**
     * The main game loop. Graphic rendering is done at the speed of {@value TARGET_FPS} frames per second unless vSync
     * is enabled. In the case vSync is enabled the monitor frame rate will be used. Updates to the game state however
     * is done at a slower pace (since it doesn't require that many updates). These updates will happen at
     * {@value TARGET_TPS} ticks per second. Input handling is done at the same speed as the rendering since a slow
     * tick-speed might cause missed events.
     */
    private void loop() {
        long elapsedTime;
        long tickTimer = 0;

        while (running && !window.shouldClose()) {
            timer.updateLoopTime();
            elapsedTime = timer.getElapsedTime();
            tickTimer += elapsedTime;

            while (tickTimer >= TPS_INTERVAL) {


                timer.updateTickTime();

                // update all controllers and providers
                controllers.forEach(Controller::update);
                providers.forEach(Provider::update);

                // tick has been processed, remove 1 interval from tick timer
                tickTimer -= TPS_INTERVAL;
            }

            // draw
            if (window.update()) {
                // fire non-native events
                inputHandler.fire();

                // set main camera
                renderer.setActiveCamera(camera);
                // update projection matrix
                window.updateProjectionMatrix();

                // render everything
                providers.forEach(provider -> provider.draw(window, renderer));
                renderer.render();

                // draw the hud
                hud.draw(window, renderer);
            }

            // sync up frame rate as desired
            if (!window.vSyncEnabled()) {
                // manually sync up frame rate with the timer if vSync is disabled
                long endTime = timer.getPreviousTime() + FPS_INTERVAL;
                while (timer.getSystemTime() < endTime) {
                    try { // use sleep(1) for more accurate intervals
                        Thread.sleep(1);
                    } catch (InterruptedException ignored) {}
                }
            }

        }
    }

    /**
     * Get the {@link Window} of the currently running instance.
     *
     * @return The currently active Window.
     */
    public Window getWindow() {
        return window;
    }

    /**
     * Add a {@link Controller} (User or AI) to the {@link Engine} to keep track of. In case the {@link Controller} is also an
     * instanceof {@link Listener} it is possible to handle several events happening on the {@link Window}.
     *
     * @param controller The controller to add to the engine
     */
    public void addController(Controller controller) {
        if (isRunning()) return;

        if (controller instanceof Listener) {
            inputHandler.addListener((Listener) controller);
        }

        controllers.add(controller);
    }

    /**
     * Add multiple Controllers. See {@link #addController(Controller)}.
     *
     * @param controllers The Controllers to attach to the engine
     */
    public void addControllers(Controller[] controllers) {
        for (Controller controller : controllers) {
            addController(controller);
        }
    }

    /**
     * Add a {@link Provider} to the Engine to keep track of. Providers should provide their own rendering and
     * update logic. Furthermore, a Provider can be retrieved from the Engine by invoking {@link #getProvider(Class)}
     * using it's class as identifier (each provider class can only run once on the engine).
     *
     * @param provider The Provider to attach to the engine
     */
    public void addProvider(Provider provider) {
        if (isRunning()) return;

        providers.add(provider);
    }

    /**
     * Add multiple Providers. See {@link #addProvider(Provider)}.
     *
     * @param providers The Providers to attach to the engine
     */
    public void addProviders(Provider[] providers) {
        for (Provider provider : providers) {
            addProvider(provider);
        }
    }

    /**
     * Get an active {@link Provider} by class type. If at a certain point you don't have a reference to a certain
     * Provider you can get said Provider using this method.
     *
     * @param type The class type of the provider
     * @return An instance of the attached provider
     * @throws IllegalArgumentException if the provider is not currently an active provider
     * @throws ClassCastException if the provider is of a different class type as requested.
     */
    public <T extends Provider> T getProvider(Class<T> type) {
        for (Provider provider : providers) {
            if (type.isInstance(provider)) {
                return type.cast(provider);
            }
        }
        throw new IllegalArgumentException("Requested Provider does not exist");
    }

    public Camera getCamera() {
        return camera;
    }

    public Hud getHud() {
        return hud;
    }

    /**
     * Reports whether the Engine is currently running
     *
     * @return Whether the engine is running
     */
    public boolean isRunning() {
        return running;
    }

    public Timer getGameloopTimer(){
        return timer;
    }

    public Renderer getRenderer() {
        return this.renderer;
    }

}
