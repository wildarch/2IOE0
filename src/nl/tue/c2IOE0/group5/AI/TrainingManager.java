package nl.tue.c2IOE0.group5.AI;

import nl.tue.c2IOE0.group5.AI.Data.InputGenerator;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Random;

/**
 * TowerDefence
 * Created by s154796 on 12-10-2017.
 */
public class TrainingManager extends JFrame{
    TacticalTrainer trainer = null;
    DataSet data = null;
    DataSetIterator iterator = null;
    boolean trainerActive = false;

    Random random = new Random();

    JPanel btnPanel = new JPanel();
    JPanel statusPanel = new JPanel();
    JTabbedPane optionsPanel = new JTabbedPane();
    JPanel modelOptionsPanel = new JPanel();
    JPanel dataOptionsPanel = new JPanel();

    JButton loadNetworkBtn = new JButton("Load NN");
    JButton saveNetworkBtn = new JButton("Save NN");
    JButton generateNetworkBtn = new JButton("Generate NN");
    JButton trainBtn = new JButton("Train");
    JButton stopTrainBtn = new JButton("Stop Training");
    JButton loadDataBtn = new JButton("Load Data");
    JButton generateDataBtn = new JButton("Generate Data");
    JButton saveDataBtn = new JButton("Save Data");
    JButton resetBtn = new JButton("Reset");
    JButton batchBtn = new JButton("Create Batches");

    SpinnerModel intModel1 = new SpinnerNumberModel(5, 0, 1000000, 1);
    SpinnerModel intModel2 = new SpinnerNumberModel(5, 0, 1000000, 1);
    SpinnerModel intModel3 = new SpinnerNumberModel(5, 0, 1000000, 1);
    SpinnerModel intModel4 = new SpinnerNumberModel(5, 0, 1000000, 1);
    SpinnerModel intModel5 = new SpinnerNumberModel(5, 0, 1000000, 1);
    SpinnerModel intModel6 = new SpinnerNumberModel(5, 0, 1000000, 1);
    SpinnerModel intModel7 = new SpinnerNumberModel(5, 0, 1000000, 1);
    SpinnerModel intModel8 = new SpinnerNumberModel(5, 0, 1000000, 1);
    SpinnerModel intModel9 = new SpinnerNumberModel(5, 0, 1000000, 1);

    JLabel labelDataSetSize = new JLabel("input size: ");
    JLabel labelGridSize = new JLabel("grid size: ");
    JLabel labelNumTowers = new JLabel("num towers: ");
    JLabel labelNumTowerLevels = new JLabel("num tower levels: ");
    JLabel labelDeployTypes = new JLabel("num deploy types: ");
    JLabel labelQtrustSteps = new JLabel("q-steps: ");
    JLabel labelBatchSize = new JLabel("batch size: ");
    JLabel labelIterations = new JLabel("iterations: ");
    JLabel labelEpochs = new JLabel("epochs: ");

    //DATASET INPUTS
    //numin
    JSpinner inputDataSetSize = new JSpinner(intModel1);
    //gridsize
    JSpinner inputGridSize = new JSpinner(intModel2);
    //numtowers
    JSpinner inputNumTowers = new JSpinner(intModel3);
    //towerlevels
    JSpinner inputNumTowerLevels = new JSpinner(intModel4);
    //numdeploy
    JSpinner inputNumDeployTypes = new JSpinner(intModel5);
    //qtruststeps
    JSpinner inputQTrustSteps = new JSpinner(intModel6);
    //batchsize
    JSpinner inputBatchSize = new JSpinner(intModel7);

    //MODEL INPUTS
    JSpinner inputNumIterations = new JSpinner(intModel8);
    JSpinner inputNumEpochs = new JSpinner(intModel9);

    JFileChooser modelSelector = new JFileChooser();
    JFileChooser dataSelector = new JFileChooser();

    JLabel statusLabel = new JLabel();

    public TrainingManager(){
        super("Training Manager");

        SwingUtilities.invokeLater(this::buildUI);
    }

    void buildUI(){
        System.out.println("Building GUI...");

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(800, 600);

        loadNetworkBtn.addActionListener(a -> {
            if(modelSelector.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
                status("Loading model from: " + modelSelector.getSelectedFile().toString());
                new Thread(() -> {
                    try {
                        trainer = TacticalTrainer.FromFile(modelSelector.getSelectedFile());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    setEnabled();
                    status("Done loading!");
                }).start();
            }
        });

        saveNetworkBtn.addActionListener(a -> {
            if(modelSelector.showSaveDialog(this) == JFileChooser.APPROVE_OPTION){
                status("Saving model to: " + modelSelector.getSelectedFile().toString());
                new Thread(() -> {
                    try {
                        trainer.saveNetwork(modelSelector.getSelectedFile());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    setEnabled();
                    status("Done saving!");
                }).start();

            }
        });

        generateNetworkBtn.addActionListener(a -> {
            int nrIterations = (int)inputNumIterations.getValue();
            int gridSize = (int)inputGridSize.getValue();
            int nrTowers = (int)inputNumTowers.getValue();
            int nrDeployTypes = (int)inputNumDeployTypes.getValue();

            new Thread(() -> {
                status("Generating model...");
                trainer = TacticalTrainer.FromGeneratedModel(gridSize, nrTowers, nrDeployTypes, nrIterations);
                setEnabled();
                status("Done generating!");
            }).start();
        });

        loadDataBtn.addActionListener(a -> {
            if(dataSelector.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
                status("Loading data from: " + dataSelector.getSelectedFile().toString());
                new Thread(() -> {
                    try {
                        data = InputGenerator.fromFile(dataSelector.getSelectedFile());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    setEnabled();
                    status("Done loading!");
                }).start();
            }
        });

        saveDataBtn.addActionListener(a -> {
            if(dataSelector.showSaveDialog(this) == JFileChooser.APPROVE_OPTION){
                status("Saving dataset to: " + dataSelector.getSelectedFile().toString());
                new Thread(() -> {
                    try {
                        InputGenerator.export(dataSelector.getSelectedFile(), data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    setEnabled();
                    status("Done saving!");
                }).start();
            }
        });

        generateDataBtn.addActionListener(a -> {
            int numInputs = (int)inputDataSetSize.getValue();
            int gridSize = (int)inputGridSize.getValue();
            int nrTowers = (int)inputNumTowers.getValue();
            int nrDeployTypes = (int)inputNumDeployTypes.getValue();
            int nrTowerLevels = (int)inputNumTowerLevels.getValue();
            int qtruststeps = (int)inputQTrustSteps.getValue();

            new Thread(() -> {
                status("generating input data...");
                data = InputGenerator.getTrainingData(numInputs, gridSize, nrTowers, nrTowerLevels, nrDeployTypes, qtruststeps, input -> {
                    INDArray output = Nd4j.zeros(numInputs, 1);

                    for (int i = 0; i < numInputs; i++){
                        double res = 0;
                        for (int j = gridSize * gridSize * nrTowers; j < gridSize * gridSize * nrTowers + nrDeployTypes; j++){
                            if (input.getDouble(i, j) > 1.0 / nrDeployTypes){
                                res += 1.0 / nrDeployTypes;
                            }
                        }

                        output.putScalar(i, 0, res * input.getDouble(i, gridSize * gridSize * nrTowers + nrDeployTypes));
                        //output.putScalar(i, 0, 0);

                        status("generating input data... " + Math.round(i * 100.0 / (double)numInputs) + "%");
                    }

                    return output;
                });
                setEnabled();
                status("done generating input data!");
            }).start();
        });

        resetBtn.addActionListener(a -> {
            trainerActive = false;
            trainer = null;
            data = null;
            iterator = null;
            setEnabled();
            status("State reset");
        });

        batchBtn.addActionListener(a -> {
            int bSize = (int) inputBatchSize.getValue();
            status("Creating batches with size " + bSize);
            iterator = InputGenerator.getTrainingData(data, bSize, random);
            status("Done");
        });

        trainBtn.addActionListener(a -> {
            trainerActive = true;
            new Thread(() -> {
                status("Starting trainer...");
                int c = 1;
                int numEpochs = (int) inputNumEpochs.getValue();
                while (trainerActive){
                    status("Starting " + (iterator != null ? "iterator" : "dataset") + " round " + c + " with " + numEpochs + " epochs...");

                    if(iterator != null){
                        trainer.fit(iterator, numEpochs);
                    } else {
                        trainer.fit(data, numEpochs);
                    }

                    status("Round " + c + " with " + numEpochs + " epochs completed!");
                    c++;
                }
                status("Trainer aborted");
            }).start();
            setEnabled();
        });

        stopTrainBtn.addActionListener(a -> {
            trainerActive = false;
            setEnabled();
        });

        setEnabled();

        SpringLayout dataLayout = new SpringLayout();
        SpringLayout modelLayout = new SpringLayout();

        dataOptionsPanel.setLayout(dataLayout);
        modelOptionsPanel.setLayout(modelLayout);

        dataOptionsPanel.add(inputDataSetSize);
        dataOptionsPanel.add(inputGridSize);
        dataOptionsPanel.add(inputNumTowers);
        dataOptionsPanel.add(inputNumTowerLevels);
        dataOptionsPanel.add(inputNumDeployTypes);
        dataOptionsPanel.add(inputQTrustSteps);
        dataOptionsPanel.add(inputBatchSize);

        dataOptionsPanel.add(labelDataSetSize);
        dataOptionsPanel.add(labelGridSize);
        dataOptionsPanel.add(labelNumTowers);
        dataOptionsPanel.add(labelNumTowerLevels);
        dataOptionsPanel.add(labelDeployTypes);
        dataOptionsPanel.add(labelQtrustSteps);
        dataOptionsPanel.add(labelBatchSize);

        dataLayout.putConstraint(SpringLayout.WEST, labelDataSetSize, 5, SpringLayout.WEST, dataOptionsPanel);
        dataLayout.putConstraint(SpringLayout.WEST, labelGridSize, 5, SpringLayout.WEST, dataOptionsPanel);
        dataLayout.putConstraint(SpringLayout.WEST, labelNumTowers, 5, SpringLayout.WEST, dataOptionsPanel);
        dataLayout.putConstraint(SpringLayout.WEST, labelNumTowerLevels, 5, SpringLayout.WEST, dataOptionsPanel);
        dataLayout.putConstraint(SpringLayout.WEST, labelDeployTypes, 5, SpringLayout.WEST, dataOptionsPanel);
        dataLayout.putConstraint(SpringLayout.WEST, labelQtrustSteps, 5, SpringLayout.WEST, dataOptionsPanel);
        dataLayout.putConstraint(SpringLayout.WEST, labelBatchSize, 5, SpringLayout.WEST, dataOptionsPanel);

        dataLayout.putConstraint(SpringLayout.NORTH, labelDataSetSize, 5, SpringLayout.NORTH, dataOptionsPanel);
        dataLayout.putConstraint(SpringLayout.NORTH, labelGridSize, 5, SpringLayout.SOUTH, labelDataSetSize);
        dataLayout.putConstraint(SpringLayout.NORTH, labelNumTowers, 5, SpringLayout.SOUTH, labelGridSize);
        dataLayout.putConstraint(SpringLayout.NORTH, labelNumTowerLevels, 5, SpringLayout.SOUTH, labelNumTowers);
        dataLayout.putConstraint(SpringLayout.NORTH, labelDeployTypes, 5, SpringLayout.SOUTH, labelNumTowerLevels);
        dataLayout.putConstraint(SpringLayout.NORTH, labelQtrustSteps, 5, SpringLayout.SOUTH, labelDeployTypes);
        dataLayout.putConstraint(SpringLayout.NORTH, labelBatchSize, 5, SpringLayout.SOUTH, labelQtrustSteps);

        dataLayout.putConstraint(SpringLayout.WEST, inputDataSetSize, 5, SpringLayout.EAST, labelDataSetSize);
        dataLayout.putConstraint(SpringLayout.WEST, inputGridSize, 5, SpringLayout.EAST, labelGridSize);
        dataLayout.putConstraint(SpringLayout.WEST, inputNumTowers, 5, SpringLayout.EAST, labelNumTowers);
        dataLayout.putConstraint(SpringLayout.WEST, inputNumTowerLevels, 5, SpringLayout.EAST, labelNumTowerLevels);
        dataLayout.putConstraint(SpringLayout.WEST, inputNumDeployTypes, 5, SpringLayout.EAST, labelDeployTypes);
        dataLayout.putConstraint(SpringLayout.WEST, inputQTrustSteps, 5, SpringLayout.EAST, labelQtrustSteps);
        dataLayout.putConstraint(SpringLayout.WEST, inputBatchSize, 5, SpringLayout.EAST, labelBatchSize);

        dataLayout.putConstraint(SpringLayout.NORTH, inputDataSetSize, 0, SpringLayout.NORTH, labelDataSetSize);
        dataLayout.putConstraint(SpringLayout.NORTH, inputGridSize, 0, SpringLayout.NORTH, labelGridSize);
        dataLayout.putConstraint(SpringLayout.NORTH, inputNumTowers, 0, SpringLayout.NORTH, labelNumTowers);
        dataLayout.putConstraint(SpringLayout.NORTH, inputNumTowerLevels, 0, SpringLayout.NORTH, labelNumTowerLevels);
        dataLayout.putConstraint(SpringLayout.NORTH, inputNumDeployTypes, 0, SpringLayout.NORTH, labelDeployTypes);
        dataLayout.putConstraint(SpringLayout.NORTH, inputQTrustSteps, 0, SpringLayout.NORTH, labelQtrustSteps);
        dataLayout.putConstraint(SpringLayout.NORTH, inputBatchSize, 0, SpringLayout.NORTH, labelBatchSize);

        modelOptionsPanel.add(inputNumIterations);
        modelOptionsPanel.add(inputNumEpochs);

        modelOptionsPanel.add(labelIterations);
        modelOptionsPanel.add(labelEpochs);

        modelLayout.putConstraint(SpringLayout.WEST, labelIterations, 5, SpringLayout.WEST, modelOptionsPanel);
        modelLayout.putConstraint(SpringLayout.WEST, labelEpochs, 5, SpringLayout.WEST, modelOptionsPanel);

        modelLayout.putConstraint(SpringLayout.NORTH, labelIterations, 5, SpringLayout.NORTH, modelOptionsPanel);
        modelLayout.putConstraint(SpringLayout.NORTH, labelEpochs, 5, SpringLayout.SOUTH, labelIterations);

        modelLayout.putConstraint(SpringLayout.WEST, inputNumIterations, 5, SpringLayout.EAST, labelIterations);
        modelLayout.putConstraint(SpringLayout.WEST, inputNumEpochs, 5, SpringLayout.EAST, labelEpochs);

        modelLayout.putConstraint(SpringLayout.NORTH, inputNumIterations, 0, SpringLayout.NORTH, labelIterations);
        modelLayout.putConstraint(SpringLayout.NORTH, inputNumEpochs, 0, SpringLayout.NORTH, labelEpochs);


        btnPanel.add(loadNetworkBtn);
        btnPanel.add(saveNetworkBtn);
        btnPanel.add(generateNetworkBtn);
        btnPanel.add(trainBtn);
        btnPanel.add(stopTrainBtn);

        btnPanel.add(loadDataBtn);
        btnPanel.add(saveDataBtn);
        btnPanel.add(generateDataBtn);
        btnPanel.add(batchBtn);

        btnPanel.add(resetBtn);

        statusPanel.add(statusLabel);

        optionsPanel.addTab("Model", modelOptionsPanel);
        optionsPanel.addTab("Data", dataOptionsPanel);

        add(btnPanel, BorderLayout.SOUTH);
        add(statusPanel, BorderLayout.NORTH);
        add(optionsPanel, BorderLayout.CENTER);

        pack();
    }

    void status(String str){
        SwingUtilities.invokeLater(() -> statusLabel.setText(str));
    }

    void setEnabled(){
        saveDataBtn.setEnabled(data != null);
        saveNetworkBtn.setEnabled(trainer != null);
        trainBtn.setEnabled((data != null || iterator != null) && trainer != null && !trainerActive);
        stopTrainBtn.setEnabled(trainerActive);
        batchBtn.setEnabled(data != null);
    }


    public void showUI(){
        SwingUtilities.invokeLater(() -> this.setVisible(true));
    }

    public static void main(String[] args){
        System.out.println("initializing...");
        TrainingManager tm = new TrainingManager();
        System.out.println("init done");
        tm.showUI();
    }
}
