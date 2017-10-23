package nl.tue.c2IOE0.group5.ai.data;

import nl.tue.c2IOE0.group5.ai.QLearner;
import nl.tue.c2IOE0.group5.enemies.EnemyType;
import nl.tue.c2IOE0.group5.engine.Simulator;
import nl.tue.c2IOE0.group5.engine.provider.Provider;
import nl.tue.c2IOE0.group5.providers.*;
import nl.tue.c2IOE0.group5.towers.AbstractTower;
import nl.tue.c2IOE0.group5.towers.MainTower;
import nl.tue.c2IOE0.group5.towers.TowerType;
import org.joml.Vector2i;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * TowerDefence
 * Created by s154796 on 19-10-2017.
 */
public class DataSimulator {
    private final Thread[] threads;
    private final boolean[] activeThreads;
    private final INDArray inputs;
    private final INDArray outputs;
    private final int playSize, totalSize, borderSize, nrTowers, nrDeployTypes, bufferSize, numThreads;
    private final Random random = new Random();

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

                            assert grid != null;
                            assert buffer != null;

                            double result = simulate(grid, buffer, trust);

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
        Simulator simulator = new Simulator(new Predicate<Simulator>() {
            @Override
            public boolean test(Simulator simulator) {
                return true;
            }
        });

        EnemyProvider ep = new EnemyProvider();
        TowerProvider tp = new TowerProvider();
        GridProvider gp = new GridProvider(totalSize, playSize);
        BulletProvider bp = new BulletProvider();

        simulator.addProviders(new Provider[]{
            ep,
            tp,
            gp,
            bp
        });

        try {
            simulator.init();
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert simulator.isInitialized();

        QLearner routingLearner = new QLearner(totalSize, 1000, 0.1);

        //TODO: train q-learner here

        for (EnemyType type : buffer) {
            int index = random.nextInt(7);
            Cell startCell = gp.getCell(routingLearner.getOptimalNSpawnStates(7)[index]);
            Vector2i start = startCell.getGridPosition();
            List<Integer> path = routingLearner.getOptimalPath(startCell.getGridPosition());
            List<Vector2i> targets = path.stream().map(p -> QLearner.getPoint(p, totalSize)).collect(Collectors.toList());
            ep.putEnemy(type, start, targets, routingLearner);
        }

        tp.putMainTower();

        for (int x = 0; x < playSize; x++){
            for (int y = 0; y < playSize; y++){
                if (grid[x][y] != null){
                    AbstractTower tower;
                    switch (grid[x][y]){
                        default:
                            tower = new MainTower(tp);
                            break;
                    }

                    gp.placePlayFieldTower(x, y, tower);
                }
            }
        }

        try {
            simulator.run();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public void run(){
        for (int i = 0; i < threads.length; i++){
            activeThreads[i] = true;
            threads[i].start();
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
