package nl.tue.c2IOE0.group5.providers;

import nl.tue.c2IOE0.group5.engine.Engine;
import nl.tue.c2IOE0.group5.engine.Simulator;
import nl.tue.c2IOE0.group5.engine.objects.BackgroundScenery;
import nl.tue.c2IOE0.group5.engine.objects.Camera;
import nl.tue.c2IOE0.group5.engine.objects.Skybox;
import nl.tue.c2IOE0.group5.engine.provider.Provider;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.engine.rendering.Window;

/**
 * @author Jorren Hendriks.
 */
public class BackgroundProvider implements Provider<Engine> {

    private Skybox skybox;
    private Camera camera;
    private BackgroundScenery backgroundScenery;

    @Override
    public void init(Engine engine) {
        this.camera = engine.getCamera();
        this.skybox = new Skybox("/skybox/skybox.obj", "/skybox/skyboxspace.png");
        this.skybox.setScale(1000f);
        this.skybox.init(engine.getRenderer());
        this.backgroundScenery = new BackgroundScenery(engine.getProvider(GridProvider.class).SIZE);
        this.backgroundScenery.init(engine.getRenderer());
    }

    @Override
    public void update() {

    }

    @Override
    public void draw(Window window, Renderer renderer) {
        skybox.setPosition(camera);
    }
}
