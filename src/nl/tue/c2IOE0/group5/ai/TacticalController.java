package nl.tue.c2IOE0.group5.ai;

import nl.tue.c2IOE0.group5.engine.Engine;
import nl.tue.c2IOE0.group5.engine.controller.Controller;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.util.ModelSerializer;

import java.io.File;
import java.io.IOException;

/**
 * TowerDefence
 * Created by s154796 on 5-10-2017.
 */
public class TacticalController implements Controller {
    private Engine engine;
    private ComputationGraph network;

    private TacticalController(){

    }

    /**
     * Imports the neural network from a given .zip file
     * @param file
     * @return Controller
     * @throws IOException
     */
    public static TacticalController FromFile(File file) throws IOException {
        TacticalController controller = new TacticalController();
        controller.network = ModelSerializer.restoreComputationGraph(file);
        return controller;
    }

    public void saveNetwork(File file) throws IOException {
        ModelSerializer.writeModel(network, file, true);
    }

    /**
     * Initialize the controller. This method will only be called once at startup. The {@link Engine} parameter can be
     * used to initialize a link to all required resources.
     *
     * @param engine The game engine.
     */
    @Override
    public void init(Engine engine) {
        this.engine = engine;
    }

    /**
     * Update the controller. Handle controller-specific timed tasks here. This method will be called every game tick.
     * Any resources necessary should already be available from {@link #init(Engine)}.
     */
    @Override
    public void update() {

    }
}
