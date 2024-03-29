package nl.tue.c2IOE0.group5.providers;

import nl.tue.c2IOE0.group5.engine.Engine;
import nl.tue.c2IOE0.group5.engine.provider.ObjectProvider;
import nl.tue.c2IOE0.group5.engine.rendering.Mesh;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.engine.rendering.Window;
import nl.tue.c2IOE0.group5.engine.rendering.shader.Material;
import nl.tue.c2IOE0.group5.towers.Bullet;

public class BulletProvider extends ObjectProvider<Bullet> {

    public void addBullet(Bullet b) {
        this.objects.add(b);
    }

    private Engine engine;

    @Override
    public void renderInit(Engine engine) {
        this.engine = engine;
        Mesh bullet = engine.getRenderer().linkMesh("/models/items/bullet4.obj");
        bullet.setMaterial(new Material("/general/square.png"));
    }

    @Override
    public void update() {
        if (engine != null && engine.isPaused()) return;
        objects.removeIf(Bullet::isDone);
        super.update();
    }

    @Override
    public void draw(Window window, Renderer renderer) {
        objects.forEach(bullet -> bullet.draw(window, renderer));
    }
}
