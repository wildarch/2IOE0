package nl.tue.c2IOE0.group5.enemies;

import nl.tue.c2IOE0.group5.engine.Timer;
import nl.tue.c2IOE0.group5.engine.rendering.*;

import java.io.IOException;

public class TestEnemy extends Enemy {
    private float t = 0;
    private Timer timer;


    public TestEnemy(Timer timer) {
        try {
            Mesh mesh = OBJLoader.loadMesh("/bunny.obj");
            mesh.setTexture(new Texture("/tower.png"));
            super.setMesh(mesh);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.timer = timer;
        setScale(20f);
    }

    @Override
    public void draw(Window window, Renderer renderer) {
        super.draw(window, renderer);
        float step = timer.getElapsedTime() / 10000f * 2f * (float) Math.PI;
        t = (t + step) % (float) (2*Math.PI);
        setPosition((float) (Math.cos(t)), -0.5f, (float) (Math.sin(t)));
    }
}
