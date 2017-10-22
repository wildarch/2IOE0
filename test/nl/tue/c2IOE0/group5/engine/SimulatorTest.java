package nl.tue.c2IOE0.group5.engine;

import nl.tue.c2IOE0.group5.engine.provider.Provider;
import nl.tue.c2IOE0.group5.providers.BulletProvider;
import nl.tue.c2IOE0.group5.providers.EnemyProvider;
import nl.tue.c2IOE0.group5.providers.GridProvider;
import nl.tue.c2IOE0.group5.providers.TowerProvider;
import org.junit.Test;

import java.util.function.Predicate;

public class SimulatorTest {
    @Test
    public void towerKillsEnemy() {
        Predicate<Simulator> stopCondition = sim -> {
            EnemyProvider enemyProvider = sim.getProvider(EnemyProvider.class);
            return enemyProvider.countEnemies() == 0;
        };
        Simulator sim = new Simulator(stopCondition);
        sim.addProviders(new Provider[] {
                new EnemyProvider(),
                new GridProvider(),
                new TowerProvider(),
                new BulletProvider(),
        });
        try {
            sim.run();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        long time = sim.getTimer().getLoopTime();
        System.out.println("Terminated after " + time + "ms");
    }

}