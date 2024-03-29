package nl.tue.c2IOE0.group5.controllers;

import nl.tue.c2IOE0.group5.ai.QLearner;
import nl.tue.c2IOE0.group5.ai.data.InputConverter;
import nl.tue.c2IOE0.group5.enemies.EnemyType;
import nl.tue.c2IOE0.group5.engine.Engine;
import nl.tue.c2IOE0.group5.engine.Timer;
import nl.tue.c2IOE0.group5.engine.controller.Controller;
import nl.tue.c2IOE0.group5.providers.Cell;
import nl.tue.c2IOE0.group5.providers.EnemyProvider;
import nl.tue.c2IOE0.group5.providers.GridProvider;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.util.ModelSerializer;
import org.joml.Vector2i;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;

public class AiController implements Controller {

    private static int NR_WAVES = 10;
    private static int NR_SUB_WAVES = 5;
    private static long WAVE_TIME = 5000; // 5 seconds
    private int BIG_WAVE_SIZE = 2;
    private int SMALL_WAVE_SIZE = 1;
    private static int BUFFER_SAMPLE_SIZE = 10;

    private int wave = 0;
    private EnemyProvider enemyProvider;
    private Timer loopTimer;
    private long nextWaveTime = 0;

    private QLearner qLearner;
    private List<Integer> optimalPath; //the current optimal path for the active cell
    private GridProvider gridProvider;
    BooleanSupplier isPaused;

    private ComputationGraph network;
    private final InputStream networkFile;
    private final Random random = new Random();

    private boolean gameStarted = false;

    public AiController(InputStream networkFile) {
        this.networkFile = networkFile;
    }

    @Override
    public void init(Engine engine) {
        enemyProvider = engine.getProvider(EnemyProvider.class);
        loopTimer = engine.getRenderLoopTimer();
        gridProvider = engine.getProvider(GridProvider.class);

        new Thread(() -> {
            try {
                if(networkFile != null){
                    network = ModelSerializer.restoreComputationGraph(networkFile);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).run();

        trainQLearner();
        isPaused = engine::isPaused;
    }

    /**
     * Resets the AI. Trains the Q Learner again and set all values of the AI back to the starting values
     */
    public void resetAI(){
        trainQLearner();
        wave = 0;
        NR_SUB_WAVES = 5;
        WAVE_TIME = 5000; // 5 seconds
        BIG_WAVE_SIZE = 2;
        SMALL_WAVE_SIZE = 1;
        BUFFER_SAMPLE_SIZE = 10;
        wave = 0;

        gameStarted = false;
    }

    /**
     * Called when starting the game
     */
    public void startGame(){
        nextWaveTime = loopTimer.getTime() + WAVE_TIME * 2;
        gameStarted = true;
    }

    @Override
    public void update() {
        if(isPaused.getAsBoolean()) return;

        if(!gameStarted){
            startGame();
        }

        boolean bigWave = wave % NR_SUB_WAVES == 0 && enemyProvider.countEnemies() == 0 && loopTimer.getTime() > nextWaveTime;
        boolean smallWave = wave % NR_SUB_WAVES != 0 && loopTimer.getTime() > nextWaveTime;
        if (bigWave || smallWave) {
            wave(bigWave);

            if(bigWave){
                SMALL_WAVE_SIZE++;
                BIG_WAVE_SIZE = SMALL_WAVE_SIZE * 2;
            }

            wave++;
            nextWaveTime = loopTimer.getTime() + WAVE_TIME;
        }
    }

    /**
     * @return The number of big waves that have spawned.
     */
    public int getBigWaves() {
        return wave / NR_SUB_WAVES;
    }

    /**
     * @return The instance of the Q Learner
     */
    public QLearner getQLearner() {
        return qLearner;
    }

    /**
     * Generate a new buffer to spawn according to the rules trained by the neural net
     */
    public EnemyType[] generateBuffer(boolean big){
        InputConverter converter;

        EnemyType[] sampleBuffer = new EnemyType[big ? BIG_WAVE_SIZE : SMALL_WAVE_SIZE];

        EnemyType[] selectedBuffer = null;
        double bufferScore = Double.NEGATIVE_INFINITY;
        double sampleScore;
        for (int i = 0; i < BUFFER_SAMPLE_SIZE; i++){
            for (int j = 0; j < sampleBuffer.length; j++){
                EnemyType t = EnemyType.values()[random.nextInt(EnemyType.getSize())];
                sampleBuffer[j] = t;
            }

            if(network != null){
                converter = InputConverter.fromGameState(gridProvider, 1, sampleBuffer);
                converter.convert();
                INDArray aiInput = converter.getInput();
                INDArray aiOutput = network.outputSingle(false, aiInput);
                sampleScore = aiOutput.getDouble(0, 0);
            } else {
                sampleScore = 0;
            }

            if(sampleScore > bufferScore){
                selectedBuffer = Arrays.copyOf(sampleBuffer, sampleBuffer.length);
                bufferScore = sampleScore;
            }
        }

        if(selectedBuffer == null) throw new NullPointerException("selectedBuffer == null");

        return selectedBuffer;
    }

    /**
     * Spawn a new wave
     * @param big true if it should be a big wave, false if it should be a small wave.
     */
    private void wave(final boolean big) {
        new Thread(() -> {
            EnemyType[] selectedBuffer = generateBuffer(big);

            // Do a wave!
            String size = big ? "Big  " : "Small";
            System.out.println(size + " wave at " + loopTimer.getTime());

            for (EnemyType enemy : selectedBuffer) {
                Random r = new Random();
                int random = r.nextInt(5);
                Cell startCell = gridProvider.getCell(qLearner.getOptimalNSpawnStates(5)[random]);
                Vector2i start = startCell.getGridPosition();
                List<Integer> path = qLearner.getOptimalPath(startCell.getGridPosition());
                enemyProvider.putEnemy(
                    enemy,
                    start,
                    path.stream().map(p -> qLearner.getPoint(p, gridProvider.SIZE)).collect(Collectors.toList()),
                    qLearner
                );
            }
        }).run();
        qLearner.updateRewardsMatrix(QLearner.getState(gridProvider.SIZE / 2, gridProvider.SIZE / 2, gridProvider.SIZE), 1000); //to make sure they still go to the middle
    }

    /**
     * Initialize a new Q Learner and train it.
     */
    private void trainQLearner() {
        int noIterations = 1000;
        double gamma = 0.2d;

        qLearner = new QLearner(gridProvider.SIZE, noIterations, gamma);
        qLearner.initializeQ();

        for (int i = 0; i < 200; i++) {
            qLearner.generateRandomPath(100);
        }
        qLearner.addBasicPath();
        //to prevent going to 0,0
        qLearner.generateRandomPath(100, 0);
        qLearner.execute();
    }
}
