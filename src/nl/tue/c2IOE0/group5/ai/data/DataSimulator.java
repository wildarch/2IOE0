package nl.tue.c2IOE0.group5.ai.data;

import nl.tue.c2IOE0.group5.enemies.EnemyType;
import nl.tue.c2IOE0.group5.engine.Simulator;
import nl.tue.c2IOE0.group5.engine.provider.Provider;
import nl.tue.c2IOE0.group5.providers.BulletProvider;
import nl.tue.c2IOE0.group5.providers.EnemyProvider;
import nl.tue.c2IOE0.group5.providers.GridProvider;
import nl.tue.c2IOE0.group5.providers.TowerProvider;
import nl.tue.c2IOE0.group5.towers.AbstractTower;
import nl.tue.c2IOE0.group5.towers.MainTower;
import nl.tue.c2IOE0.group5.towers.TowerType;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.Predicate;

/**
 * TowerDefence
 * Created by s154796 on 19-10-2017.
 */
public class DataSimulator {
    private final Thread[] threads;
    private final boolean[] activeThreads;
    private final INDArray inputs;
    private final INDArray outputs;
    private final int gridSize, nrTowers, nrDeployTypes, bufferSize, numThreads;

    public DataSimulator(int numThreads, final INDArray inputs, int gridSize, int nrTowers,
                         int nrDeployTypes, int bufferSize){
        threads = new Thread[numThreads];
        activeThreads = new boolean[numThreads];

        this.inputs = inputs;

        //create label output matrix with one column
        this.outputs = Nd4j.create(inputs.rows(), 1);

        this.gridSize = gridSize;
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
                                gridSize, nrTowers, nrDeployTypes, bufferSize);

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
        GridProvider gp = new GridProvider();
        BulletProvider bp = new BulletProvider();

//        for (int i = 0; i < buffer.length; i++){
//            Enemy enemy;
//            switch (buffer[i]){
//                default:
//
//                    break;
//            }
//
//        }

        for (int x = 0; x < gridSize; x++){
            for (int y = 0; y < gridSize; y++){
                if (grid[x][y] != null){
                    AbstractTower tower;
                    switch (grid[x][y]){
                        default:
                            tower = new MainTower(ep, bp, simulator.getGameloopTimer());
                            break;
                    }
                    //gp.placeTower(x, y, tower);
                }
            }
        }

        simulator.addProviders(new Provider[]{
            ep,
            tp,
            gp,
            bp
        });

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
