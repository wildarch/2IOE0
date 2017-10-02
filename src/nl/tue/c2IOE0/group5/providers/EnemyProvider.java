package nl.tue.c2IOE0.group5.providers;

import nl.tue.c2IOE0.group5.enemies.Enemy;
import nl.tue.c2IOE0.group5.enemies.TestEnemy;
import nl.tue.c2IOE0.group5.engine.Engine;
import nl.tue.c2IOE0.group5.engine.provider.ObjectProvider;
import nl.tue.c2IOE0.group5.engine.provider.Provider;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.engine.rendering.Window;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EnemyProvider extends ObjectProvider<Enemy> {

    @Override
    public void init(Engine engine) {
        objects.add(new TestEnemy(engine.getGameloopTimer()));
    }
}
