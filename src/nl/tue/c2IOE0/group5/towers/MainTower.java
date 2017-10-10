package nl.tue.c2IOE0.group5.towers;

import nl.tue.c2IOE0.group5.engine.rendering.Mesh;
import nl.tue.c2IOE0.group5.engine.rendering.OBJLoader;
import nl.tue.c2IOE0.group5.engine.rendering.shader.Material;

import java.io.IOException;

public class MainTower extends AbstractTower {

    private static final int RANGE = 1;
    private static final int MAX_LEVEL = 1;
    private static final int DAMAGE_PER_ATTACK = 10;
    private static final int ATTACKS_PER_SECOND = 2;
    private static final int MAX_HEALTH = 100;

    public MainTower() {
        super(RANGE, MAX_LEVEL, DAMAGE_PER_ATTACK, ATTACKS_PER_SECOND, MAX_HEALTH);
        setMesh();
    }

    public void setMesh() {
        try {
            Mesh m = OBJLoader.loadMesh("/tower.obj");
            m.setMaterial(new Material("/tower.png"));
            setMesh(m);
            setScale(40f);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load Tower model");
        }
    }
}
