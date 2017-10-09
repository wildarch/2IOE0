package nl.tue.c2IOE0.group5.AI;

import nl.tue.c2IOE0.group5.AI.Data.NetworkBuilder;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.util.ModelSerializer;

import java.io.File;
import java.io.IOException;

/**
 * TowerDefence
 * Created by s154796 on 9-10-2017.
 */
public class TacticalTrainer {
    private ComputationGraph network;

    private TacticalTrainer(){

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

        return trainer;
    }

    public static TacticalTrainer FromGeneratedModel(){
        TacticalTrainer trainer = new TacticalTrainer();
        ComputationGraphConfiguration conf = NetworkBuilder.buildNetwork(3, 4, 5);

        trainer.network = new ComputationGraph(conf);
        trainer.network.init();

        return trainer;
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
