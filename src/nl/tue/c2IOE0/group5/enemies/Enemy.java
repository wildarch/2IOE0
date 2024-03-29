package nl.tue.c2IOE0.group5.enemies;

import nl.tue.c2IOE0.group5.ai.QLearner;
import nl.tue.c2IOE0.group5.controllers.PlayerController;
import nl.tue.c2IOE0.group5.engine.Engine;
import nl.tue.c2IOE0.group5.engine.Timer;
import nl.tue.c2IOE0.group5.engine.objects.GameObject;
import nl.tue.c2IOE0.group5.engine.objects.PositionInterpolator;
import nl.tue.c2IOE0.group5.engine.rendering.Drawable;
import nl.tue.c2IOE0.group5.engine.rendering.InstancedMesh;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.engine.rendering.Window;
import nl.tue.c2IOE0.group5.providers.Cell;
import nl.tue.c2IOE0.group5.providers.EnemyProvider;
import nl.tue.c2IOE0.group5.providers.GridProvider;
import nl.tue.c2IOE0.group5.towers.AbstractTower;
import nl.tue.c2IOE0.group5.towers.WallTower;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for all enemies.
 * Handles movement, rendering and interaction with the environment.
 * @author Daan de Graaf
 */
public abstract class Enemy extends GameObject implements Drawable {

    private final int onDieReward;
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
    protected float rotationOffset = 0f;

    private Vector3f offset;
    private QLearner qLearner;
    private Vector3f ambientLight = new Vector3f(0, 0, 1f);
    private int damage;
    protected Vector3f drawOffset = new Vector3f();
    private PlayerController playerController;

    public Enemy(Timer loopTimer, Timer renderTimer, GridProvider gridProvider,
                 Vector2i initialPosition, List<Vector2i> targetPositions,
                 int maxHealth, int damage, float speed, long attackSpeed, QLearner qlearner,
                 PlayerController playerController, int onDieReward) {
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
        this.playerController = playerController;
        this.onDieReward = onDieReward;
    }

    public abstract EnemyType getType();

    /**
     * Move the enemy and attack if necessary
     */
    @Override
    public void update() {
        if (!targetPositions.isEmpty()) {
            //long targetTime = interpolator.update(loopTimer.getElapsedTime());
            boolean targetReached = interpolator.targetReached();
            if (targetReached) {
                targetPositions.remove(0);
                if (targetPositions.isEmpty()) {
                    return;
                }
                setOffset();
            }
            Cell targetCell = gridProvider.getCell(targetPositions.get(0));
            AbstractTower tower = targetCell.getTower();
            Vector3f targetPosition = targetCell.getPosition().add(0, 0.5f, 0).add(offset.toImmutable());
            interpolator.setTarget(targetPosition);
            if (tower == null) {
                // Road is clear, move ahead
                attacking = false;
                //System.out.println("Set target position!");
                interpolator.update(loopTimer.getElapsedTime());
            } else {
                // Destroy the tower first
                attacking = true;
                doDamage(tower);
            }
        }
        setRotation(interpolator.getDirection(), rotationOffset);
    }

    /**
     * Set the offset, so units do not go to the centre of a tile
     */
    private void setOffset() {
       offset.set(
                jitter(),
                0,
                jitter()
        );
    }

    /**
     * Calculate a random jitter
     * @return a random float to use as offset
     */
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

        iMeshBody = renderer.linkMesh("/testobjects/cube.obj", () -> {
            setModelView(renderer);
            renderer.ambientLight(ambientLight);
        });
        this.renderer = renderer;
    }

    @Override
    public void draw(Window window, Renderer renderer) {
        if(!attacking) drawOffset = interpolator.getOffset(renderTimer.getTime() - loopTimer.getTime());
    }

    private long timeToDoDamage;

    /**
     * Do damage to a tower
     * @param tower the tower to damage
     */
    private void doDamage(AbstractTower tower) {
        double factor = 1;

        if (this instanceof WalkerEnemy && tower instanceof WallTower) factor = 100; //walkers do double the damage to walls
        if (this instanceof BasicEnemy && tower instanceof WallTower) factor = 0.5;

        if (timeToDoDamage < loopTimer.getTime()) {
            tower.takeDamage(damage*factor);
            timeToDoDamage = loopTimer.getTime() + attackSpeed;
        }
    }

    /**
     * Get damage from a tower
     * @param damage the amount of damage to get
     */
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

    /**
     * Let this unit die.
     */
    public void die() {
        if(dead) return;
        dead = true;
        if (playerController != null) {
            playerController.addBudget(getDieReward());
        }
        onDie();

        gridProvider.addKill(this.getType());
        System.out.println("Enemy died: " + this.getType().toString());
    }

    /**
     * Get the reward the player gets when this units dies.
     * @return
     */
    private int getDieReward() {
        return onDieReward;
    }

    /**
     * To be called when the units dies. For example to unlink meshes.
     */
    protected abstract void onDie();

    /**
     * @return Whether or not this unit is dead.
     */
    public boolean isDead() {
        return dead;
    }

    /**
     * @return the cell the unit is currently on.
     */
    public Cell getCurrentCell() {
        Vector3f position = this.getPosition();
        int x = (int)(position.x() + 0.5f);
        int y = (int)(position.z() + 0.5f);
        return gridProvider.getCell(x, y);
    }
}
