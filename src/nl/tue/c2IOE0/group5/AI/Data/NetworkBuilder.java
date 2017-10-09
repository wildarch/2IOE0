package nl.tue.c2IOE0.group5.AI.Data;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;

/**
 * TowerDefence
 * Created by s154796 on 9-10-2017.
 */
public class NetworkBuilder {
    public static ComputationGraphConfiguration buildNetwork(int gridSize, int nrTowers, int nrDeployTypes){
        return new NeuralNetConfiguration.Builder()
            .iterations(2000)
            .learningRate(0.1)
            .seed(123)
            .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
            .weightInit(WeightInit.XAVIER)
            .updater(new Nesterovs(0.9))
            .graphBuilder()
            .pretrain(false)
            .backprop(true)
            .addInputs("feedforward-input", "grid-input")
            .setInputTypes(InputType.feedForward(nrDeployTypes + 1), InputType.convolutionalFlat(gridSize, gridSize, nrTowers))
            .addLayer("layerc0", new ConvolutionLayer.Builder().nIn(7).nOut(10).build(), "grid")
            .addLayer("layer0", new DenseLayer.Builder().nIn(10).nOut(20).build(), "input")
            .addLayer("layer1", new DenseLayer.Builder().nIn(10).nOut(20).build(), "input")
            .addLayer("layer2", new OutputLayer.Builder(LossFunctions.LossFunction.MSE).nIn(50).nOut(1).activation(Activation.IDENTITY).build(), "layer0", "layer1", "layerc0")
            .setOutputs("layer2")
            .build();
    }
}
