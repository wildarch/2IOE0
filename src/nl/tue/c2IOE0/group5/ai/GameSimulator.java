package nl.tue.c2IOE0.group5.ai;

import nl.tue.c2IOE0.group5.ai.data.GridAnalyzer;
import nl.tue.c2IOE0.group5.enemies.EnemyType;
import nl.tue.c2IOE0.group5.engine.Engine;
import nl.tue.c2IOE0.group5.engine.Simulator;
import nl.tue.c2IOE0.group5.engine.provider.Provider;
import nl.tue.c2IOE0.group5.providers.*;
import nl.tue.c2IOE0.group5.towers.TowerType;
import org.joml.Vector2i;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class GameSimulator {
    private static final double QLEARNER_GAMMA = 0.1;
    private static final int Q_LEARNER_ITERATIONS = 100;

    private Simulator simulator;
    private QLearner qLearner;
    private EnemyProvider enemyProvider = new EnemyProvider();
    private TowerProvider towerProvider = new TowerProvider();
    private GridProvider gridProvider;
    private GridAnalyzer gridAnalyzer;
    private BulletProvider bulletProvider = new BulletProvider();
    private TowerConnectionProvider towerConnectionProvider =
            new TowerConnectionProvider();
    private Random random = new Random();

    private int gridSize;
    private int playSize;

    public GameSimulator(Simulator simulator, int gridSize, int playSize) {
        setup(simulator,gridSize, playSize);
    }

    public GameSimulator(Engine engine, int gridSize, int playSize) {
        setup(engine, gridSize, playSize);
    }

    public GameSimulator(boolean render, Predicate<Simulator> stopCondition, int gridSize, int playSize) {
        if(render) {
            setup(new Engine(stopCondition), gridSize, playSize);
        }
        else {
            setup(new Simulator(stopCondition), gridSize, playSize);
        }
    }

    private void setup(Engine engine, int gridSize, int playSize) {
        setup((Simulator) engine, gridSize, playSize);
        engine.addProvider(new AnimationProvider());
        engine.addProvider(new BackgroundProvider());
    }

    private void setup(Simulator simulator, int gridSize, int playSize) {
        this.simulator = simulator;
        this.gridProvider = new GridProvider(gridSize, playSize);
        this.gridAnalyzer = new GridAnalyzer(gridProvider);
        this.simulator.addProviders(new Provider[]{
                enemyProvider,
                towerProvider,
                gridProvider,
                bulletProvider,
                towerConnectionProvider
        });
        this.gridSize = gridSize;
        this.playSize = playSize;
    }

    public void init() throws IOException {
        simulator.init();
        qLearner = new QLearner(gridSize, Q_LEARNER_ITERATIONS, QLEARNER_GAMMA);
        //TODO should this really run before towers have been spawned??
        trainQLearner();
        towerProvider.putMainTower();
        if (simulator instanceof Engine) {
            Engine engine = (Engine) simulator;
            engine.getCamera().setPosition(getGridProvider().SIZE/2, getGridProvider().SIZE, getGridProvider().SIZE/2);
            engine.getCamera().setRotation(90, 0, 0);
        }
    }

    public void spawnEnemy(EnemyType type) {
        int index = random.nextInt(7);
        Cell startCell = gridProvider.getCell(qLearner.getOptimalNSpawnStates(7)[index]);
        Vector2i start = startCell.getGridPosition();
        List<Integer> path = qLearner.getOptimalPath(startCell.getGridPosition());
        List<Vector2i> targets = path.stream()
                .map(p -> QLearner.getPoint(p, gridSize))
                .collect(Collectors.toList());
        enemyProvider.putEnemy(type, start, targets, qLearner);
    }

    public void placeTower(TowerType type, int x, int y) {
        if(!towerProvider.buildTower(x, y, type.getTowerClass())) {
            throw new IllegalArgumentException("Cell (" + x + ", " + y + ") is not available to build on");
        }
    }

    public void run() throws IOException {
        gridAnalyzer.start();
        simulator.run();
    }

    public double getDestructionScore() {
        return gridAnalyzer.getDestructionScore();
    }

    //TODO should reflect game state, so should probably be in shared code between AI and Game
    private void trainQLearner() {
        qLearner.initializeQ();
        qLearner.setRewardsMatrix(QLearner.getState(gridSize / 2, gridSize / 2, gridSize), 1000);

        for (int i = 0; i < 200; i++) {
            qLearner.generateRandomPath(10);
        }
        qLearner.addBasicPath();
        //to prevent going to 0,0
        qLearner.generateRandomPath(100, 0);
        qLearner.execute();
    }

    public EnemyProvider getEnemyProvider() {
        return enemyProvider;
    }

    public TowerProvider getTowerProvider() {
        return towerProvider;
    }

    public GridProvider getGridProvider() {
        return gridProvider;
    }

    public BulletProvider getBulletProvider() {
        return bulletProvider;
    }

    public TowerConnectionProvider getTowerConnectionProvider() {
        return towerConnectionProvider;
    }

}
