package nl.tue.c2IOE0.group5.AI;

import nl.tue.c2IOE0.group5.AI.Data.NetworkBuilder;
import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.MultiDataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.iterator.MultiDataSetIterator;

import java.io.File;
import java.io.IOException;

/**
 * TowerDefence
 * Created by s154796 on 9-10-2017.
 */
public class TacticalTrainer {
    private ComputationGraph network;
    private StatsStorage statsStorage;

    private TacticalTrainer(){

    }

    private void initialize(){
        network.init();

        //Initialize the user interface backend
        UIServer uiServer = UIServer.getInstance();

        //Configure where the network information (gradients, score vs. time etc) is to be stored. Here: store in memory.
        statsStorage = new InMemoryStatsStorage();         //Alternative: new FileStatsStorage(File), for saving and loading later

        //Attach the StatsStorage instance to the UI: this allows the contents of the StatsStorage to be visualized
        uiServer.attach(statsStorage);

        network.setListeners(new ScoreIterationListener(10), new StatsListener(statsStorage));
    }

    /**
     * Imports the neural network from a given .zip file
     * @param file
     * @return Controller
     * @throws IOException
     */
    public static TacticalTrainer FromFile(File file) throws IOException {
        TacticalTrainer trainer = new TacticalTrainer();
        trainer.network = ModelSerializer.restoreComputationGraph(file);
        trainer.initialize();
        return trainer;
    }

    public static TacticalTrainer FromGeneratedModel(int gridSize, int nrTowers, int nrDeployTypes, int iterations){
        TacticalTrainer trainer = new TacticalTrainer();
        ComputationGraphConfiguration conf = NetworkBuilder.buildNetwork(gridSize, nrTowers, nrDeployTypes, iterations);

        trainer.network = new ComputationGraph(conf);
        trainer.initialize();
        return trainer;
    }

    public void fit(MultiDataSetIterator data, int nEpochs){
        for( int i=0; i<nEpochs; i++ ){
            data.reset();
            network.fit(data);
        }
    }

    public void fit(DataSetIterator data, int nEpochs){
        for( int i=0; i<nEpochs; i++ ){
            data.reset();
            network.fit(data);
        }
    }

    public void fit(DataSet data, int nEpochs){
        for( int i=0; i<nEpochs; i++ ){
            //data.reset();
            network.fit(data);
        }
    }

    public INDArray[] eval(INDArray[] inputs){
        return network.output(inputs);
    }

    public INDArray[] eval(INDArray inputs){
        return network.output(inputs);
    }

    public void fit(MultiDataSet data, int nEpochs){
        for( int i=0; i<nEpochs; i++ ){
            network.fit(data);
        }
    }

    /**
     * Save the model to a file specified
     * @param file the .zip file to write to
     * @throws IOException
     */
    public void saveNetwork(File file) throws IOException {
        ModelSerializer.writeModel(network, file, true);
    }
}
