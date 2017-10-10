package nl.tue.c2IOE0.group5.providers;

import nl.tue.c2IOE0.group5.engine.Engine;
import nl.tue.c2IOE0.group5.engine.provider.ObjectProvider;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.engine.rendering.Window;
import nl.tue.c2IOE0.group5.towers.AbstractTower;
import nl.tue.c2IOE0.group5.towers.MainTower;

public class TowerProvider extends ObjectProvider<AbstractTower> {

    GridProvider gridProvider;
    private MainTower mainTower;

    @Override
    public void init(Engine engine) {
        gridProvider = engine.getProvider(GridProvider.class);
        putMainTower();
    }

    private void putMainTower() {
        int x = GridProvider.SIZE / 2;
        Cell baseCell = gridProvider.getCell(x, x);
        mainTower = new MainTower();
        baseCell.placeTower(mainTower);
        objects.add(mainTower);
    }

    public MainTower getMainTower() {
        return mainTower;
    }

    @Override
    public void update() {
        objects.removeIf((t -> t.isDead()));
        super.update();
    }

    @Override
    public void draw(Window window, Renderer renderer) {
        super.draw(window, renderer);
    }
}
