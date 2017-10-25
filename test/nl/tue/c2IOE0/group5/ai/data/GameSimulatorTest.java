package nl.tue.c2IOE0.group5.ai.data;

import nl.tue.c2IOE0.group5.ai.GameSimulator;
import nl.tue.c2IOE0.group5.controllers.PlayerController;
import nl.tue.c2IOE0.group5.enemies.Enemy;
import nl.tue.c2IOE0.group5.enemies.EnemyType;
import nl.tue.c2IOE0.group5.engine.Engine;
import nl.tue.c2IOE0.group5.engine.Simulator;
import nl.tue.c2IOE0.group5.providers.AnimationProvider;
import nl.tue.c2IOE0.group5.providers.BackgroundProvider;
import nl.tue.c2IOE0.group5.providers.EnemyProvider;
import nl.tue.c2IOE0.group5.providers.TowerProvider;
import nl.tue.c2IOE0.group5.towers.TowerType;
import org.joml.Vector2i;
import org.junit.Test;

import static org.junit.Assert.*;

public class GameSimulatorTest {
    @Test
    public void getDestructionScore() throws Exception {
        long start = System.currentTimeMillis();
        Engine simulator = new Engine();
        simulator.addProvider(new AnimationProvider());
        simulator.addProvider(new BackgroundProvider());
        GameSimulator sim = new GameSimulator(simulator, 13, 9);
        sim.init();
        simulator.getCamera().setPosition(sim.getGridProvider().SIZE/2, sim.getGridProvider().SIZE, sim.getGridProvider().SIZE/2);
        simulator.getCamera().setRotation(90, 0, 0);
        sim.placeTower(TowerType.CANNON, 6, 7);

        for(int i = 0; i < 10; i++) {
            sim.spawnEnemy(EnemyType.DRILL);
        }

        sim.run();

        float score = sim.getDestructionScore();
        System.out.println("Score: " + score);
        assertTrue(score > 0 && score < 1);
    }

}