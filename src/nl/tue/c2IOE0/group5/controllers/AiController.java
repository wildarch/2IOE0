package nl.tue.c2IOE0.group5.controllers;

import nl.tue.c2IOE0.group5.engine.Engine;
import nl.tue.c2IOE0.group5.engine.Timer;
import nl.tue.c2IOE0.group5.engine.controller.Controller;
import nl.tue.c2IOE0.group5.engine.controller.input.events.Event;
import nl.tue.c2IOE0.group5.engine.controller.input.events.Listener;
import nl.tue.c2IOE0.group5.engine.controller.input.events.MouseEvent;
import nl.tue.c2IOE0.group5.providers.EnemyProvider;
import nl.tue.c2IOE0.group5.providers.GridProvider;
import org.joml.Vector3f;

import java.util.List;

public class AiController implements Controller, Listener {

    private static int NR_WAVES = 10;
    private static int NR_SUB_WAVES = 5;
    private static long WAVE_TIME = 5000; // 5 seconds

    private int wave = 0;
    private EnemyProvider enemyProvider;
    private Timer loopTimer;
    private long nextWaveTime = 0;

    private QLearner qlearner;
    private List<Integer> optimalPath; //the current optimal path for the active cell
    private GridProvider gridProvider;

    @Override
    public void init(Engine engine) {
        enemyProvider = engine.getProvider(EnemyProvider.class);
        loopTimer = engine.getGameloopTimer();
        gridProvider = engine.getProvider(GridProvider.class);
        doQLearnerStuffForTesting();
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
        for (int i = 0; i < 5; i++) {
            Vector3f pos = new Vector3f((float)(Math.random() * 13), 1f, (float)(Math.random() * 13));
            enemyProvider.putEnemy(pos);
        }
        if (big) {
            for (int i = 0; i < 10; i++) {
                Vector3f pos2 = new Vector3f((float)(Math.random() * 13), 1f, (float)(Math.random() * 13));
                enemyProvider.putEnemy(pos2);
            }
        }
    }

    public void doQLearnerStuffForTesting() {
        int noIterations = 1000;
        qlearner = new QLearner(GridProvider.SIZE, noIterations);
        qlearner.updateRewardsMatrix(qlearner.getState(GridProvider.SIZE/2, GridProvider.SIZE/2), 1000);
        qlearner.updateRewardsMatrix(qlearner.getState(3, 3), 500);
        qlearner.updateRewardsMatrix(qlearner.getState(2, 3), -5);
        qlearner.updateRewardsMatrix(qlearner.getState(4, 3), -5);
        qlearner.updateRewardsMatrix(qlearner.getState(3, 2), -5);
        qlearner.updateRewardsMatrix(qlearner.getState(3, 4), -5);
        for (int i = 0; i < 10; i++) {
            qlearner.generateRandomPath(100);
        }
        qlearner.generateRandomPath(100, 0);
        double gamma = 0.1d;
        qlearner.execute(gamma);
    }

    @Override
    public void onKeyPressed(Event event) {

    }

    @Override
    public void onKeyReleased(Event event) {

    }

    @Override
    public void onKeyHold(Event event) {

    }

    @Override
    public void onMouseButtonPressed(MouseEvent event) {
        optimalPath = qlearner.getOptimalPath(qlearner.getState(gridProvider.getActiveCell()));
        gridProvider.drawPath(optimalPath);
    }

    @Override
    public void onMouseButtonReleased(MouseEvent event) {

    }

    @Override
    public void onMouseMove(MouseEvent event) {

    }

    @Override
    public void onMouseScroll(MouseEvent event) {

    }
}
