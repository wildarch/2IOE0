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


    private static final int RANGE = 5;
    private static final int MAX_LEVEL = 1;
    private static final int MAX_HEALTH = 30;
    private static final int ATTACK_TIME = 1;
    private static final float BULLET_SPEED = 3f;
    private static final int BULLET_DAMAGE = 1;
    private static final int PRICE = 50;

    public static MetaData metadata = new MetaData();
    static {
        metadata.name = "Rocket";
        metadata.icon = "/hud/rockettower.png";
        metadata.price = PRICE;
    }

    private Vector3f rocketRotation = new Vector3f(0f, 0f, 0f);

    private Renderer renderer;
    private InstancedMesh iBaseMesh;
    private InstancedMesh iRocketMesh;

    public RocketTower(TowerProvider towerProvider) {
        super(RANGE, MAX_LEVEL, MAX_HEALTH, ATTACK_TIME, BULLET_SPEED, BULLET_DAMAGE, 1f, 0.32f, towerProvider);
    }

    @Override
    protected void attack(Enemy e) {
        super.attack(e);
        //calculate rotation of the rocket, based on the angle between the position of the tower / the enemy
        rocketRotation.y = (float) Math.toDegrees(Math.atan2((e.getPosition().z - getPosition().z), (e.getPosition().x - getPosition().x)));
    }

    @Override
    public TowerType getType() {
        return TowerType.ROCKET;
    }

    @Override
    public int getPrice() {
        return PRICE;
    }

    @Override
    public void renderInit(Renderer renderer) {
        setScale(1f);

        Mesh baseMesh = renderer.linkMesh("/models/towers/rockettower/base.obj");
        Mesh rocketMesh = renderer.linkMesh("/models/towers/rockettower/rocket.obj");
        iBaseMesh = renderer.linkMesh(baseMesh, () -> {
            setModelView(renderer, getPositionOffset());
            renderer.boink(getBounceDegree(), baseMesh, rocketMesh);
        });
        iRocketMesh = renderer.linkMesh(rocketMesh, () -> {
            setModelView(renderer, new Vector3f(0f, 0.148f, 0f).add(getPositionOffset().toImmutable()));
            renderer.boink(getBounceDegree(), baseMesh, rocketMesh);
        });

        this.renderer = renderer;
    }

    @Override
    protected void onDie() {
        //renderer.unlinkMesh(mesh, render, shadowRender);
        renderer.unlinkMesh(iBaseMesh);
        renderer.unlinkMesh(iRocketMesh);
    }

    @Override
    public int getMaxHealth() {
        return MAX_HEALTH;
    }
}