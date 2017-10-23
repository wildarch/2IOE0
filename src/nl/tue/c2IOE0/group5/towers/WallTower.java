package nl.tue.c2IOE0.group5.towers;

import nl.tue.c2IOE0.group5.engine.rendering.InstancedMesh;
import nl.tue.c2IOE0.group5.engine.rendering.Mesh;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.engine.rendering.shader.Material;
import nl.tue.c2IOE0.group5.providers.TowerProvider;

/**
 * @Author Tom Peters
 */
public class WallTower extends AbstractTower {


    private static final int RANGE = 0;
    private static final int MAX_LEVEL = 1;
    private static final int MAX_HEALTH = 100;
    private static final int PRICE = 30;

    public static MetaData metadata = new MetaData();
    static {
        metadata.name = "Wall";
        metadata.icon = "/hud/walltower.png";
        metadata.price = PRICE;
    }

    private Renderer renderer;
    private InstancedMesh iMesh;

    public WallTower(TowerProvider towerProvider) {
        super(RANGE, MAX_LEVEL, MAX_HEALTH, -1, 0, 0, 2f, 1f, towerProvider);
    }

    @Override
    public void renderInit(Renderer renderer) {
        setScale(0.9f);
        Mesh mesh = renderer.linkMesh("/models/towers/walltower/walltower.obj");
        iMesh = renderer.linkMesh("/models/towers/walltower/walltower.obj", () -> setModelView(renderer));
        this.renderer = renderer;
    }

    @Override
    protected void onDie() {
        //renderer.unlinkMesh(mesh, render, shadowRender);
        renderer.unlinkMesh(iMesh);
    }

    @Override
    public TowerType getType() {
        return TowerType.WALL;
    }
}