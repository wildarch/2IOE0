package nl.tue.c2IOE0.group5.controllers;

import nl.tue.c2IOE0.group5.engine.Engine;
import nl.tue.c2IOE0.group5.engine.Timer;
import nl.tue.c2IOE0.group5.engine.controller.Controller;
import nl.tue.c2IOE0.group5.providers.EnemyProvider;
import org.joml.Vector3f;

public class AiController implements Controller {

    private static int NR_WAVES = 10;
    private static int NR_SUB_WAVES = 5;
    private static long WAVE_TIME = 5000; // 5 seconds

    private int wave = 0;
    private EnemyProvider enemyProvider;
    private Timer loopTimer;
    private long nextWaveTime = 0;

    @Override
    public void init(Engine engine) {
        enemyProvider = engine.getProvider(EnemyProvider.class);
        loopTimer = engine.getGameloopTimer();
    }

    @Override
    public void update() {
        boolean bigWave = wave % NR_SUB_WAVES == 0 && enemyProvider.countEnemies() == 0;
        boolean smallWave = wave % NR_SUB_WAVES != 0 && loopTimer.getLoopTime() > nextWaveTime;
        if (bigWave || smallWave) {
            wave(bigWave);
            wave++;
            nextWaveTime = loopTimer.getLoopTime() + WAVE_TIME;
        }
    }

    private void wave(boolean big) {
        //if(true) return;
        // Do a wave!
        String size = big ? "Big  " : "Small";
        System.out.println(size + " wave at " + loopTimer.getLoopTime());
        for (int i = 0; i < 2; i++) {
            Vector3f pos = new Vector3f((float)(Math.random() * 13), 1f, (float)(Math.random() * 13));
            enemyProvider.putEnemy(pos);
        }
        if (big) {
            for (int i = 0; i < 3; i++) {
                Vector3f pos2 = new Vector3f((float)(Math.random() * 13), 1f, (float)(Math.random() * 13));
                enemyProvider.putEnemy(pos2);
            }
        }
    }
}
