package nl.tue.c2IOE0.group5.enemies;

import nl.tue.c2IOE0.group5.engine.Timer;
import nl.tue.c2IOE0.group5.engine.objects.GameObject;
import nl.tue.c2IOE0.group5.engine.objects.PositionInterpolator;
import nl.tue.c2IOE0.group5.providers.Cell;
import nl.tue.c2IOE0.group5.providers.GridProvider;
import nl.tue.c2IOE0.group5.towers.AbstractTower;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public abstract class Enemy extends GameObject {
    private boolean dead = false;
    protected GridProvider gridProvider;
    private final int maxHealth;
    private int health;
    private List<Vector2i> targetPositions;
    protected PositionInterpolator interpolator;
    protected Timer loopTimer;
    protected boolean attacking = false;
    private final float SPEED;
    private final long ATTACKSPEED;

    public Enemy(Timer loopTimer, GridProvider gridProvider,
                 Vector2i initialPosition, List<Vector2i> targetPositions, int maxHealth, float speed, long attackSpeed) {
        this.gridProvider = gridProvider;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.targetPositions = new ArrayList<>(targetPositions);
        this.loopTimer = loopTimer;
        setPosition(gridProvider.getCell(initialPosition).getPosition().add(0, 2f, 0f));
        this.SPEED = speed;
        this.interpolator = new PositionInterpolator(this, SPEED);
        this.ATTACKSPEED = attackSpeed;
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
                System.out.println("Target reached!");
                return;
            }
        }
        Cell targetCell = gridProvider.getCell(targetPositions.get(0));
        AbstractTower tower = targetCell.getTower();
        Vector3f targetPosition = targetCell.getPosition().add(0, 0.5f, 0);
        if (tower == null || (targetReached && attacking)) {
            // Road is clear, move ahead
            attacking = false;
            interpolator.setTarget(targetPosition, loopTimer.getLoopTime());
        }
        else if(tower != null) {
            // Destroy the tower first
            attacking = true;
            doDamage(tower);
        }
    }

    private long timeToDoDamage;
    private void doDamage(AbstractTower tower) {
        if (timeToDoDamage < loopTimer.getLoopTime()) {
            tower.takeDamage(1);
            timeToDoDamage = loopTimer.getLoopTime() + ATTACKSPEED;
        }
    }

    public void getDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            health = 0;
            this.die();
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
