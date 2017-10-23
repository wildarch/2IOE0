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
import nl.tue.c2IOE0.group5.providers.MusicProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jorren Hendriks.
 */
public class Engine extends Simulator {

    private Window window;
    private Renderer renderer;
    private Hud hud;
    private InputHandler inputHandler;
    private Camera camera;

    protected List<Controller> controllers;
    private Timer renderTimer;

    public Engine() {
        super(sim -> ((Engine) sim).getWindow().shouldClose());
        renderTimer = new Timer();
        window = new Window("Tower Defence", 1600, 900, false, new Window.Options());
        renderer = new Renderer();
        hud = new Hud();
        inputHandler = new InputHandler();
        camera = new Camera(this);
        controllers = new ArrayList<>();
    }


    /**
     * Initialize necessary objects
     */
    @Override
    public void init() throws ShaderException, IOException {
        renderTimer.init();
        window.init();
        renderer.init(window);
        renderer.setActiveCamera(camera);
        hud.init(window);
        inputHandler.init(window);
        super.init();
        controllers.forEach(controller -> controller.init(this));
    }

    /**
     * Cleanup used objects
     */
    @Override
    protected void cleanup() {
        super.cleanup();
        window.cleanup();
        renderer.cleanup();
        hud.cleanup();
        try {
            getProvider(MusicProvider.class).cleanup();
        } catch (IllegalArgumentException e) {
            // Don't need to clean what you don't use
        }
    }


    /**
     * The main game loop. Graphic rendering is done at the speed of {@value TARGET_FPS} frames per second unless vSync
     * is enabled. In the case vSync is enabled the monitor frame rate will be used. Updates to the game state however
     * is done at a slower pace (since it doesn't require that many updates). These updates will happen at
     * {@value TARGET_TPS} ticks per second. Input handling is done at the same speed as the rendering since a slow
     * tick-speed might cause missed events.
     */
    @Override
    protected void step() {
        timer.updateLoopTime();
        long elapsedTime = timer.getElapsedTime();

        while (elapsedTime >= TPS_INTERVAL) {
            System.out.println(elapsedTime);
            // update all controllers and providers
            controllers.forEach(Controller::update);
            providers.forEach(Provider::update);
            // tick has been processed, remove 1 interval from tick timer
            elapsedTime -= TPS_INTERVAL;
        }

        // draw
        if (window.update()) {
            renderTimer.updateLoopTime();
            // fire non-native events
            inputHandler.fire();

            // set main camera
            renderer.setActiveCamera(camera);
            // update projection matrix
            window.updateProjectionMatrix();

            // render everything
            renderer.render();
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
                } catch (InterruptedException ignored) {

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
     * Add a {@link Controller} (User or ai) to the {@link Engine} to keep track of. In case the {@link Controller} is also an
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

    public Camera getCamera() {
        return camera;
    }

    public Hud getHud() {
        return hud;
    }

    public boolean isPaused() {
        return this.paused;
    }

    public void pause(boolean value) {
        this.paused = value;
    }

    public Timer getRenderLoopTimer(){
        return renderTimer;
    }

    public Renderer getRenderer() {
        return this.renderer;
    }
}
