package nl.tue.c2IOE0.group5.AI;

import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * TowerDefence
 * Created by s154796 on 25-9-2017.
 */
public class NNBuilder {
    private static final int NR_TOWERS = 1;
    private static final int NR_TOWER_LEVELS = 5;
    private static final int NR_DEPLOY_TYPES = 5;
    private static final int NR_TIME_STEPS = 10;
    private static final int UNIT_BUF_SIZE = 5;
    private static final int Q_LEARNER_TRUST_STEPS = 10;

    public static void main(String[] args){

        //Initialize the user interface backend
        UIServer uiServer = UIServer.getInstance();

        //Configure where the network information (gradients, score vs. time etc) is to be stored. Here: store in memory.
        StatsStorage statsStorage = new InMemoryStatsStorage();         //Alternative: new FileStatsStorage(File), for saving and loading later

        //Attach the StatsStorage instance to the UI: this allows the contents of the StatsStorage to be visualized
        uiServer.attach(statsStorage);

        //Then add the StatsListener to collect this information from the network, as it trains


        InputGenerator gen = new InputGenerator(
            NR_TOWERS,
            NR_TOWER_LEVELS,
            NR_DEPLOY_TYPES,
            NR_TIME_STEPS,
            UNIT_BUF_SIZE,
            Q_LEARNER_TRUST_STEPS
        );
        List<List<Float>> inputs = new ArrayList<>(gen.getInputs());
        System.out.println("Number of inputs: " + inputs.size());

        int columns = inputs.iterator().next().size();
        INDArray input = Nd4j.create(inputs.size(), columns);
        INDArray labels = Nd4j.create(inputs.size(), 2);

        for(int r = 0; r < inputs.size(); r++) {
            for(int c = 0; c < columns; c++) {
                input.putScalar(r, c, inputs.get(r).get(c));
            }
            float bufSize = inputs.get(r).get(columns-2);
            labels.putScalar(r, 0, bufSize < 0.5? 1 : 0);
            labels.putScalar(r, 1, bufSize >= 0.5? 1 : 0);
        }

        DataSet ds = new DataSet(input, labels);

        DataSet ds2 = new DataSet();

        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder()
            .weightInit(WeightInit.XAVIER)
            .iterations(500)
            .learningRate(0.15)
            .seed(123)
            .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
            .miniBatch(true)
            .graphBuilder()
            .pretrain(false)
            .backprop(true)
            .addInputs("input", "grid")
            .setInputTypes(InputType.inferInputType(input), InputType.convolutional(9, 9, 7))
            .addLayer("layerc0", new ConvolutionLayer.Builder(9, 9).nIn(7).nOut(10).build(), "grid")
            .addLayer("layer0", new DenseLayer.Builder().nIn(columns).nOut(20).build(), "input")
            .addLayer("layer1", new DenseLayer.Builder().nIn(columns).nOut(20).build(), "input")
            .addLayer("layer2", new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD).nIn(50).nOut(2).activation(Activation.SOFTMAX).build(), "layer0", "layer1", "layerc0")
            .setOutputs("layer2")
            .build();

        ComputationGraph net = new ComputationGraph(conf);

        net.init();

        //net.setListeners(new ScoreIterationListener(10));

        net.setListeners(new StatsListener(statsStorage));

        net.fit(ds);


        // create output for every training sample
        INDArray[] output = net.output(ds.getFeatureMatrix());
        System.out.println(Arrays.toString(output));

        // let Evaluation prints stats how often the right output had the
        // highest value
        Evaluation eval = new Evaluation(2);
        //eval.eval(ds.getLabels(), ds);
        System.out.println(eval.stats());

    }
}
