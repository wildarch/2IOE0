package nl.tue.c2IOE0.group5.towers;

import nl.tue.c2IOE0.group5.enemies.Enemy;
import nl.tue.c2IOE0.group5.engine.rendering.InstancedMesh;
import nl.tue.c2IOE0.group5.engine.rendering.Mesh;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.engine.rendering.shader.Material;
import nl.tue.c2IOE0.group5.providers.TowerProvider;
import org.joml.Vector3f;

/**
 * @Author Yoeri Poels
 */
public class RocketTower extends AbstractTower {

    public static MetaData metadata = new MetaData();
    static {
        metadata.name = "Rocket";
        metadata.icon = "/hud/rockettower.png";
    }

    private static final int RANGE = 5;
    private static final int MAX_LEVEL = 1;
    private static final int MAX_HEALTH = 20;

    private Vector3f rocketRotation = new Vector3f(0f, 0f, 0f);

    private Renderer renderer;
    private InstancedMesh iBaseMesh;
    private InstancedMesh iRocketMesh;

    public RocketTower(TowerProvider towerProvider) {
        super(RANGE, MAX_LEVEL, MAX_HEALTH, 500, 3f, 1000, 1f, 0.2f, towerProvider);
    }

    @Override
    protected void attack(Enemy e) {
        super.attack(e);
        //calculate rotation of the rocket, based on the angle between the position of the tower / the enemy
        rocketRotation.y = (float) Math.toDegrees(Math.atan2((e.getPosition().z - getPosition().z), (e.getPosition().x - getPosition().x)));
    }

    @Override
    public void renderInit(Renderer renderer) {
        setScale(1f);
        Mesh base = renderer.linkMesh("/models/towers/rockettower/base.obj");
        Mesh rocket = renderer.linkMesh("/models/towers/rockettower/rocket.obj");
        base.setMaterial(new Material("/models/towers/rockettower/base.png"));
        rocket.setMaterial(new Material("/models/towers/rockettower/rocket.png"));

        iBaseMesh = renderer.linkMesh(base, () -> setModelView(renderer));
        iRocketMesh = renderer.linkMesh(rocket, () -> setModelView(renderer, new Vector3f(0f, 0.148f, 0f), rocketRotation));


        this.renderer = renderer;
    }

    @Override
    protected void onDie() {
        //renderer.unlinkMesh(mesh, render, shadowRender);
        renderer.unlinkMesh(iBaseMesh);
        renderer.unlinkMesh(iRocketMesh);
    }
}