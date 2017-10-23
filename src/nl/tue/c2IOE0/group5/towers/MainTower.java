package nl.tue.c2IOE0.group5.towers;

import nl.tue.c2IOE0.group5.engine.rendering.InstancedMesh;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.providers.TowerProvider;

public class MainTower extends AbstractTower {

    private static final int RANGE = 2;
    private static final int MAX_LEVEL = 1;
    private static final int MAX_HEALTH = 1000;
    private static final int PRICE = 0;

    private Renderer renderer;
    private InstancedMesh iMesh;

    public MainTower(TowerProvider towerProvider) {
        super(RANGE, MAX_LEVEL, MAX_HEALTH, 1000 , 3f, 20, 2.5f, 1f, towerProvider);
    }

    @Override
    public void renderInit(Renderer renderer) {
        setScale(1f);

        iMesh = renderer.linkMesh("/models/towers/mainbase/mainbase.obj", () -> setModelView(renderer));

        this.renderer = renderer;
    }

    @Override
    protected void onDie() {
        //renderer.unlinkMesh(mesh, render, shadowRender);
        renderer.unlinkMesh(iMesh);
        //When the main tower dies, game should end
    }

    @Override
    public int getMaxHealth() {
        return MAX_HEALTH;
    }

    @Override
    public TowerType getType() {
        return TowerType.CASTLE;
    }

    @Override
    public int getPrice() {
        return PRICE;
    }

}
