package nl.tue.c2IOE0.group5.ai.data;

import nl.tue.c2IOE0.group5.ai.GameSimulator;
import nl.tue.c2IOE0.group5.enemies.EnemyType;
import nl.tue.c2IOE0.group5.providers.EnemyProvider;
import nl.tue.c2IOE0.group5.providers.TowerProvider;
import nl.tue.c2IOE0.group5.towers.TowerType;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.IOException;
import java.util.Arrays;

/**
 * TowerDefence
 * Created by s154796 on 19-10-2017.
 */
public class DataSimulator {
    private final Thread[] threads;
    private final boolean[] activeThreads;
    private final INDArray inputs;
    private final INDArray outputs;
    private final int playSize;
    private final int totalSize;
    private final int nrTowers;
    private final int nrDeployTypes;
    private final int bufferSize;
    private final int numThreads;
    private final int borderSize;

    public DataSimulator(int numThreads, final INDArray inputs, int playSize, int borderSize, int nrTowers,
                         int nrDeployTypes, int bufferSize){
        threads = new Thread[numThreads];
        activeThreads = new boolean[numThreads];

        this.inputs = inputs;

        //create label output matrix with one column
        this.outputs = Nd4j.create(inputs.rows(), 1);

        this.playSize = playSize;
        this.totalSize = borderSize * 2 + playSize;
        this.borderSize = borderSize;
        this.nrTowers = nrTowers;
        this.nrDeployTypes = nrDeployTypes;
        this.bufferSize = bufferSize;
        this.numThreads = numThreads;
    }

    public void initialize(){
        defineThreads();
    }

    private void defineThreads(){
        final int numInputs = inputs.rows();
        final int numInputsPerThread = (int) Math.ceil(numInputs / (double)numThreads);

        for (int i = 0; i < numThreads; i++){
            final int rowStart = i * numInputsPerThread;
            final int rowEnd = (i + 1) * numInputsPerThread;
            final int threadIndex = i;

            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    synchronized (this){
                        System.out.println("Thread " + threadIndex + " started.");
                        System.out.println(
                            "Thread " + threadIndex + " will simulate rows " + rowStart + " till " + rowEnd);
                        for (int r = rowStart; r < numInputs &&
                            r < rowEnd && activeThreads[threadIndex]; r++){

                            final INDArray row = inputs.getRow(r);
                            final InputConverter converter = InputConverter.fromNNInput(row,
                                playSize, nrTowers, nrDeployTypes, bufferSize);

                            TowerType[][] grid = converter.getGrid();
                            EnemyType[] buffer = converter.getBuffer();
                            double trust = converter.getqTrust();

                            double result;

                            try {
                                result = simulate(grid, buffer, trust);
                            } catch (Exception e){
                                e.printStackTrace();
                                result = 0;
                            }

                            System.out.println("Thread: " + threadIndex + "; Iteration: " + r +
                                "; Result: " + result + "; Done: " + Math.round(((r - rowStart) / (double)(rowEnd - rowStart)) * 1000.0) / 10 + "%;");

                            outputs.putScalar(r, 0, result);
                        }
                        System.out.println("Thread " + threadIndex + " stopped.");
                        activeThreads[threadIndex] = false;
                        notify();
                    }
                }
            });
        }
    }

    private double simulate(final TowerType[][] grid, final EnemyType[] buffer, final double trust){
        //Simulator sim = new Simulator(s -> true);
        long start = System.currentTimeMillis();
        GameSimulator simulator = new GameSimulator(false, sim -> {
            if(System.currentTimeMillis() - start > 5000){
                System.out.println(Arrays.toString(sim.getProvider(EnemyProvider.class).getEnemies().stream().filter(e -> !e.isDead()).toArray()));
                System.out.println(Arrays.deepToString(grid));
                System.out.println(Arrays.toString(buffer));
                //sim.getProvider(TowerProvider.class).gridProvider.
                throw new IllegalStateException("stopcondition blocking" );
            }

            return sim.getProvider(EnemyProvider.class)
                .getEnemies().stream().filter(e -> !e.isDead()).count() == 0 || sim.getProvider(TowerProvider.class).getMainTower().isDead();
        }, totalSize, playSize);

        try {
            simulator.init();
        } catch (IOException e) {
            throw new RuntimeException("Failed to simulate because of exception: " + e.getMessage(), e);
        }

        for (EnemyType type : buffer) {
            simulator.spawnEnemy(type);
        }

        for (int x = 0; x < playSize; x++){
            for (int y = 0; y < playSize; y++){
                TowerType type = grid[x][y];
                if (type != null && type != TowerType.CASTLE) {
                    simulator.placeTower(type, x + borderSize, y + borderSize);
                }
            }
        }
        try {
            simulator.run();
        } catch (IOException e) {
            throw new RuntimeException("Failed to simulate because of exception: " + e.getMessage(), e);
        }

        return simulator.getDestructionScore();
    }

    public void run(){
        for (int i = 0; i < threads.length; i++){
            activeThreads[i] = true;
            threads[i].start();
        }
    }

    public void stop(){
        for (int i = 0; i < threads.length; i++){
            activeThreads[i] = false;
        }
    }

    public void waitTillDone(){
        for (int i = 0; i < threads.length; i++) {
            Thread t = threads[i];
            if(!t.isAlive()) continue;
            synchronized (t){
                try {
                    t.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(activeThreads[i]) throw new RuntimeException("thread " + i + " did not abort correctly");
            }
        }
    }

    public boolean running(){
        return Arrays.stream(threads).anyMatch(Thread::isAlive);
    }

    public INDArray getOutputs() {
        if(running()) throw new RuntimeException("cannot get outputs if simulator is still running");
        return outputs;
    }
}
