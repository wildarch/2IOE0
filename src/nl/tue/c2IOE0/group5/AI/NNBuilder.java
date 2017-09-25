package nl.tue.c2IOE0.group5.AI;

import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.util.ArrayList;
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
        INDArray input = Nd4j.zeros(inputs.size(), columns);
        INDArray labels = Nd4j.zeros(inputs.size(), 2);


        for(int r = 0; r < inputs.size(); r++) {
            for(int c = 0; c < columns; c++) {
                input.putScalar(r, c, inputs.get(r).get(c));
            }
            float bufSize = inputs.get(r).get(columns-2);
            labels.putScalar(r, 0, bufSize < 0.5? 1 : 0);
            labels.putScalar(r, 1, bufSize >= 0.5? 1 : 0);
        }

        DataSet ds = new DataSet(input, labels);

        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
            .iterations(1000)
            .learningRate(0.1)
            .seed(123)
            .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
            .miniBatch(true)
            .list()
            .pretrain(false)
            .backprop(true)
            .layer(0, new DenseLayer.Builder().nIn(columns).nOut(20).build())
            .layer(1, new DenseLayer.Builder().nIn(20).nOut(10).build())
            .layer(2, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD).nIn(10).nOut(2).activation(Activation.SOFTMAX).build())
            .build();
        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();

        //net.setListeners(new ScoreIterationListener(10));

        net.setListeners(new StatsListener(statsStorage));

        net.fit(ds);

        // create output for every training sample
        INDArray output = net.output(ds.getFeatureMatrix());
        System.out.println(output);

        // let Evaluation prints stats how often the right output had the
        // highest value
        Evaluation eval = new Evaluation(2);
        eval.eval(ds.getLabels(), output);
        System.out.println(eval.stats());

    }
}
