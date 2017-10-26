package nl.tue.c2IOE0.group5.ai.data;

import nl.tue.c2IOE0.group5.ai.GameSimulator;
import nl.tue.c2IOE0.group5.enemies.EnemyType;
import nl.tue.c2IOE0.group5.providers.EnemyProvider;
import nl.tue.c2IOE0.group5.providers.TowerProvider;
import nl.tue.c2IOE0.group5.towers.TowerType;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class GameSimulatorTest {
    @Test
    public void getDestructionScore() throws IOException {
        long start = System.currentTimeMillis();
        GameSimulator sim = new GameSimulator(true, s -> s.getProvider(TowerProvider.class).getMainTower().isDead(),13, 9);
        sim.init();
        sim.placeTower(TowerType.CANNON, 6, 7);

        for(int i = 0; i < 100; i++) {
            sim.spawnEnemy(EnemyType.DRILL);
        }

        sim.run();

        double score = sim.getDestructionScore();
        System.out.println("Score: " + score);
        assertTrue(score > 0 && score <= 1);
    }

    @Test
    public void halfDestroy() throws IOException {
        GameSimulator sim = new GameSimulator(true, s -> {
            System.err.println(s.getProvider(EnemyProvider.class).getEnemies().stream().filter(e -> !e.isDead()).count());
            return s.getProvider(TowerProvider.class).getMainTower().isDead();
        },13, 9);
        sim.init();
        sim.placeTower(TowerType.CANNON, 6, 7);

        for(int i = 0; i < 10; i++) {
            sim.spawnEnemy(EnemyType.DRILL);
        }

        sim.run();

        double score = sim.getDestructionScore();
        System.out.println("Score: " + score);
        assertTrue(score > 0 && score <= 1);
    }


    @Test
    public void towers(){
        //AiController
    }
}