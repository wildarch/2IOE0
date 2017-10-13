package nl.tue.c2IOE0.group5.providers;

import nl.tue.c2IOE0.group5.engine.Engine;
import nl.tue.c2IOE0.group5.engine.provider.ObjectProvider;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.engine.rendering.Window;
import nl.tue.c2IOE0.group5.towers.Bullet;

public class BulletProvider extends ObjectProvider<Bullet> {

    public void addBullet(Bullet b) {
        this.objects.add(b);
    }

    @Override
    public void init(Engine engine) {

    }

    @Override
    public void update() {
        objects.removeIf((b -> b.isDone()));
        super.update();
    }

    @Override
    public void draw(Window window, Renderer renderer) {
        super.draw(window, renderer);
    }
}
