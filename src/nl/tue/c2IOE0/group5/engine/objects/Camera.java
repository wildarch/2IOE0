package nl.tue.c2IOE0.group5.engine.objects;

import nl.tue.c2IOE0.group5.engine.Engine;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.engine.rendering.Window;
import nl.tue.c2IOE0.group5.providers.GridProvider;

/**
 * @author Yoeri Poels, Jorren Hendriks
 *
 * Class to represent a camera in 3d space.
 */
public class Camera extends Positionable {

    private Engine engine;
    private GridProvider gridProvider;
    private Window window;
    private Renderer renderer;

    public Camera(Engine engine) {
        super();
        this.engine = engine;
        this.gridProvider = engine.getProvider(GridProvider);
        this.window = engine.getWindow();
        this.renderer = engine.getRenderer();
    }

    public void recalculateActiveCell() {
        gridProvider.recalculateActiveCell(window.getMousePosition(), this, renderer, window);
    }

    @Override
    public void setPosition(float x, float y, float z) {
        super.setPosition(x, y, z);
        recalculateActiveCell();
    }

    @Override
    public void move(float offsetX, float offsetY, float offsetZ) {
        super.move(offsetX, offsetY, offsetZ);
        recalculateActiveCell();
    }

    @Override
    public void moveRelative(float offsetX, float offsetY, float offsetZ) {
        super.moveRelative(offsetX, offsetY, offsetZ);
        recalculateActiveCell();
    }

    @Override
    public void setRotation(float x, float y, float z) {
        super.setRotation(x, y, z);
        recalculateActiveCell();
    }

    @Override
    public void rotate(float offsetX, float offsetY, float offsetZ) {
        super.rotate(offsetX, offsetY, offsetZ);
        recalculateActiveCell();
    }

}