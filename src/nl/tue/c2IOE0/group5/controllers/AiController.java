package nl.tue.c2IOE0.group5.controllers;

import nl.tue.c2IOE0.group5.engine.Engine;
import nl.tue.c2IOE0.group5.engine.Timer;
import nl.tue.c2IOE0.group5.engine.controller.Controller;
import nl.tue.c2IOE0.group5.engine.controller.input.events.Event;
import nl.tue.c2IOE0.group5.engine.controller.input.events.Listener;
import nl.tue.c2IOE0.group5.engine.controller.input.events.MouseEvent;
import nl.tue.c2IOE0.group5.providers.Cell;
import nl.tue.c2IOE0.group5.providers.EnemyProvider;
import nl.tue.c2IOE0.group5.providers.GridProvider;
import org.joml.Vector2i;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;

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
        trainQLearner();
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
        // Do a wave!
        String size = big ? "Big  " : "Small";
        System.out.println(size + " wave at " + loopTimer.getLoopTime());
        Random r = new Random();
        for (int i = 0; i < 5; i++) {
            int random = r.nextInt(5);
            Cell startCell = gridProvider.getCell(qlearner.getOptimalNSpawnStates(5)[random]);
            Vector2i start = startCell.getGridPosition();
            List<Integer> path = qlearner.getOptimalPath(startCell.getGridPosition());
            enemyProvider.putEnemy(
                    start,
                    path.stream().map(QLearner::getPoint).collect(Collectors.toList())
            );
        }
        if (big) {
            for (int i = 0; i < 10; i++) {
                int random = r.nextInt(7);
                Cell startCell = gridProvider.getCell(qlearner.getOptimalNSpawnStates(7)[random]);
                Vector2i start = startCell.getGridPosition();
                List<Integer> path = qlearner.getOptimalPath(startCell.getGridPosition());
                enemyProvider.putEnemy(
                        start,
                        path.stream().map(QLearner::getPoint).collect(Collectors.toList())
                );
            }
        }
    }

    public void trainQLearner() {
        int noIterations = 1000;
        qlearner = new QLearner(GridProvider.SIZE, noIterations);
        qlearner.updateRewardsMatrix(qlearner.getState(GridProvider.SIZE/2, GridProvider.SIZE/2), 1000);
        for (int i = 3; i <= 9; i++) {
            qlearner.updateRewardsMatrix(qlearner.getState(3, i), -5);
            qlearner.updateRewardsMatrix(qlearner.getState(9, i), -5);
        }
        for (int i = 0; i < 20; i++) {
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
        if (event.getSubject() == GLFW_MOUSE_BUTTON_1) {
            optimalPath = qlearner.getOptimalPath(qlearner.getState(gridProvider.getActiveCell()));
            gridProvider.drawPath(optimalPath);
        }
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
