package nl.tue.c2IOE0.group5.providers;

import nl.tue.c2IOE0.group5.engine.Engine;
import nl.tue.c2IOE0.group5.engine.controller.input.events.MouseEvent;
import nl.tue.c2IOE0.group5.engine.provider.Provider;
import nl.tue.c2IOE0.group5.engine.rendering.Hud;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.engine.rendering.Window;
import nl.tue.c2IOE0.group5.towers.AbstractTower;
import nl.tue.c2IOE0.group5.userinterface.Buildbar;
import nl.tue.c2IOE0.group5.userinterface.UIButton;
import org.joml.Vector4f;

/**
 * @author Jorren
 */
public class UIProvider implements Provider<Engine> {


    private Window window;
    private Hud hud;
    public TowerProvider towerProvider;

    private Class<? extends AbstractTower> selectedTower;

    private final Vector4f color = new Vector4f(0.3f, 0.3f, 0.8f, 0.8f);
    private final Vector4f textColor = new Vector4f(1f, 1f, 1f, 1f);

    private UIButton buildBar;

    @Override
    public void init(Engine engine) {
        this.hud = engine.getHud();
        this.window = engine.getWindow();
        this.towerProvider = engine.getProvider(TowerProvider.class);

        buildBar = new Buildbar(80, 80, this);

        hud.create(() -> {
            if (engine.isPaused()) return;

            buildBar.draw(hud);
        });

    }

    public boolean onClick(MouseEvent event) {
        boolean passthrough = true;

        if (buildBar.contains(event.getPosition())) {
            buildBar.onClick(event);
            passthrough = false;
        }

        return passthrough;

    }

    private int bottom(int y) {
        return window.getHeight() - y;
    }

    private int right(int x) {
        return window.getWidth() - x;
    }

    @Override
    public void update() {

    }

    @Override
    public void draw(Window window, Renderer renderer) {
        buildBar.setX(window.getWidth()/2 - buildBar.getWidth()/2);
        buildBar.setY(window.getHeight() - buildBar.getHeight() - 20);
    }

    public void select(Class<? extends AbstractTower> tower) {
        this.selectedTower = tower;
    }

    public Class<? extends AbstractTower> getSelected() {
        return this.selectedTower;
    }
}
