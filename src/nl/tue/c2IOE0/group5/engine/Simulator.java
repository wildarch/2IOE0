package nl.tue.c2IOE0.group5.engine;

import nl.tue.c2IOE0.group5.engine.provider.Provider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Subclass of Engine used to simulate outcomes of certain game states.
 * Does no rendering, attaches no controllers and stops when a certain condition is met
 *
 * @author Daan de Graaf
 */
public class Simulator implements Cleanable {

    public final static int TARGET_TPS = 20;
    public final static int TARGET_FPS = 144;
    public final static int TPS_INTERVAL = 1000 / TARGET_TPS;
    public final static int FPS_INTERVAL = 1000 / TARGET_FPS;

    protected boolean running = false;
    protected boolean paused = false;
    protected List<Provider> providers;

    private boolean initialized = false;

    protected Timer timer;
    private long time;
    private Predicate<Simulator> stopCondition;

    public Simulator(Predicate<Simulator> stopCondition) {
        this.stopCondition = stopCondition;
        timer = new Timer();
        providers = new ArrayList<>();
    }

    public boolean isInitialized(){
        return initialized;
    }

    /**
     * Run the Simulator, should be invoked after initializing and attaching {@link Provider}s.
     */
    public void run() throws IOException {
        try {
            running = true;
            init();
            loop();
        } catch (Exception e){
            e.printStackTrace();
        }finally {
            cleanup();
        }
    }

    /**
     * The game loop
     */
    private void loop() {
        while(running && !stopCondition.test(this)) {
            step();
        }
    }

    protected void step() {
        time += TPS_INTERVAL;
        timer.updateLoopTime(time);
        providers.forEach(Provider::update);
    }

    /**
     * Initialize necessary objects
     */
    public void init() throws IOException {
        if (initialized) {
            return;
        }
        timer.init();
        time = timer.getLoopTime();
        providers.forEach(provider -> provider.init(this));
        initialized = true;
    }

    public void forceInit() throws IOException {
        initialized = false;
        init();
    }

    @Override
    public void cleanup() {

    }

    /**
     * Add a {@link Provider} to the Engine to keep track of. Providers should provide their own rendering and
     * update logic. Furthermore, a Provider can be retrieved from the Engine by invoking {@link #getProvider(Class)}
     * using it's class as identifier (each provider class can only run once on the engine).
     *
     * @param provider The Provider to attach to the engine
     */
    public void addProvider(Provider provider) {
        if (running)
            throw new IllegalStateException("Can't add providers after start!");

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
     * @throws ClassCastException       if the provider is of a different class type as requested.
     */
    public <T extends Provider> T getProvider(Class<T> type) {
        for (Provider provider : providers) {
            if (type.isInstance(provider)) {
                return type.cast(provider);
            }
        }
        throw new IllegalArgumentException("Requested Provider does not exist");
    }

    /**
     * Reports whether the Engine is currently running
     *
     * @return Whether the engine is running
     */
    public boolean isRunning() {
        return running;
    }

    public Timer getGameloopTimer() {
        return timer;
    }

}
