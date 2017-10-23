package nl.tue.c2IOE0.group5.enemies;

import nl.tue.c2IOE0.group5.ai.QLearner;
import nl.tue.c2IOE0.group5.engine.Timer;
import nl.tue.c2IOE0.group5.engine.objects.GameObject;
import nl.tue.c2IOE0.group5.engine.objects.PositionInterpolator;
import nl.tue.c2IOE0.group5.engine.rendering.InstancedMesh;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.providers.Cell;
import nl.tue.c2IOE0.group5.providers.GridProvider;
import nl.tue.c2IOE0.group5.towers.AbstractTower;
import nl.tue.c2IOE0.group5.towers.WallTower;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public abstract class Enemy extends GameObject {

    private final int maxHealth;
    private final float speed;
    private final long attackSpeed;
    private int health;

    private boolean dead = false;
    protected GridProvider gridProvider;
    private List<Vector2i> targetPositions;
    protected PositionInterpolator interpolator;
    protected Renderer renderer;
    protected InstancedMesh iMeshBody;
    protected Timer loopTimer;
    protected Timer renderTimer;
    protected boolean attacking = false;

    private Vector3f offset;
    private QLearner qLearner;
    private Vector3f ambientLight = new Vector3f(0, 0, 1f);
    private int damage;

    public Enemy(Timer loopTimer, Timer renderTimer, GridProvider gridProvider,
                 Vector2i initialPosition, List<Vector2i> targetPositions, int maxHealth, int damage, float speed, long attackSpeed, QLearner qlearner) {
        this.qLearner = qlearner;
        this.gridProvider = gridProvider;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.targetPositions = new ArrayList<>(targetPositions);
        this.loopTimer = loopTimer;
        this.renderTimer = renderTimer;
        setPosition(gridProvider.getCell(initialPosition).getPosition()); //they will emerge from the floor again
        this.speed = speed;
        this.interpolator = new PositionInterpolator(this, this.speed);
        this.attackSpeed = attackSpeed;
        this.offset = new Vector3f(0);
        this.damage = damage;
    }

    public abstract EnemyType getType();

    @Override
    public void update() {
        if(targetPositions.isEmpty()) {
            return;
        }
        boolean targetReached = interpolator.update(loopTimer.getLoopTime());
        if(targetReached) {
            targetPositions.remove(0);
            if(targetPositions.isEmpty()) {
                return;
            }
            setOffset();
        }
        Cell targetCell = gridProvider.getCell(targetPositions.get(0));
        AbstractTower tower = targetCell.getTower();
        Vector3f targetPosition = targetCell.getPosition().add(0, 0.5f, 0).add(offset.toImmutable());
        if (tower == null || (targetReached && attacking)) {
            // Road is clear, move ahead
            attacking = false;
            //System.out.println("Set target position!");
            interpolator.setTarget(targetPosition, loopTimer.getLoopTime());
        }
        else {
            // Destroy the tower first
            attacking = true;
            doDamage(tower);
        }
        setRotation(interpolator.getDirection());
    }

    private void setOffset() {
       offset.set(
                jitter(),
                0,
                jitter()
        );
    }

    private float jitter() {
        return (float) (Math.random()-0.5f) * Cell.CELL_SIZE * 0.5f;
    }

    /**
     * A standard cube. To be overridden by the subclasses
     * @param renderer An instance of the renderer that will draw this object.
     */
    @Override
    public void renderInit(Renderer renderer) {
        setScale(0.25f);

        iMeshBody = renderer.linkMesh("/cube.obj", () -> {
            setModelView(renderer);
            renderer.ambientLight(ambientLight);
            if(!attacking) interpolator.draw(renderTimer.getElapsedTime());
        });
        this.renderer = renderer;
    }

    private long timeToDoDamage;
    private void doDamage(AbstractTower tower) {
        double factor = 1;
        if (this instanceof WalkerEnemy && tower instanceof WallTower) factor = 2; //walkers do double the damage to walls
        if (timeToDoDamage < loopTimer.getLoopTime()) {
            tower.takeDamage(damage*factor);
            timeToDoDamage = loopTimer.getLoopTime() + attackSpeed;
        }
    }

    public void getDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            health = 0;
            this.die();
        }
        qLearner.updateRewardsMatrix(qLearner.getState(this.getCurrentCell().getGridPosition()), -damage); //reward on this tile becomes minus the damage
        qLearner.execute();

        if (health < 0.2 * maxHealth) {
            float redness = ((0.2f * maxHealth) - health) * 5;
            this.ambientLight = new Vector3f(1f, 0f, 0f).mul(redness);
        }
    }

    public void die() {
        if(dead) return;
        dead = true;
        onDie();
    }

    protected abstract void onDie();

    public boolean isDead() {
        return dead;
    }

    public Cell getCurrentCell() {
        Vector3f position = this.getPosition();
        int x = (int)position.x();
        int y = (int)position.z();
        return gridProvider.getCell(x, y);
    }
}
