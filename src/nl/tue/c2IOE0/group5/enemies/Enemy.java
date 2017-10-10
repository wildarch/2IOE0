package nl.tue.c2IOE0.group5.enemies;

import nl.tue.c2IOE0.group5.engine.objects.GameObject;
import nl.tue.c2IOE0.group5.engine.rendering.*;

public abstract class Enemy extends GameObject {
    private Mesh mesh;
    private boolean dead = false;

    @Override
    public void draw(Window window, Renderer renderer) {
        super.draw(window, renderer);
        if (mesh != null)
            mesh.draw(renderer);
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
}
