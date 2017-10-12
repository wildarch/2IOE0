package nl.tue.c2IOE0.group5.enemies;

import nl.tue.c2IOE0.group5.engine.objects.GameObject;
import nl.tue.c2IOE0.group5.engine.rendering.*;
import nl.tue.c2IOE0.group5.providers.Cell;
import nl.tue.c2IOE0.group5.providers.GridProvider;
import org.joml.Vector3f;

public abstract class Enemy extends GameObject {
    private Mesh mesh;
    private boolean dead = false;
    protected GridProvider gridProvider;
    private final int maxHealth;
    private int health;

    public Enemy(GridProvider gridProvider, int maxHealth) {
        this.gridProvider = gridProvider;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
    }

    @Override
    public void draw(Window window, Renderer renderer) {
        super.draw(window, renderer);
        if (mesh != null)
            mesh.draw(renderer);
    }

    public void getDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            health = 0;
            this.die();
        }
    }

    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }

    public Mesh getMesh() {
        return mesh;
    }

    public void die() {
        dead = true;
    }

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
