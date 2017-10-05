package nl.tue.c2IOE0.group5.AI.Data;

import com.google.common.collect.Sets;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Daan de Graaf on 13/09/17.
 */
public class InputGenerator {

    private final int nrTowers;
    private final int nrTowerLevels;
    private final int nrDeployTypes;
    private final int nrTimeSteps;
    private final int unitBufSize;
    private final int qLearnerTrustSteps;
    private Set<List<Float>> inputs;
    private List<String> labels;

    public InputGenerator(int nrTowers, int nrTowerLevels, int nrDeployTypes, int nrTimeSteps, int unitBufSize, int qLearnerTrustSteps) {
        this.nrTowers = nrTowers;
        this.nrTowerLevels = nrTowerLevels;
        this.nrDeployTypes = nrDeployTypes;
        this.nrTimeSteps = nrTimeSteps;
        this.unitBufSize = unitBufSize;
        this.qLearnerTrustSteps = qLearnerTrustSteps;
    }

    public Set<List<Float>> getInputs() {
        if (inputs != null) {
            return inputs;
        }
        List<Set<Float>> properties = new ArrayList<>();
        labels = new ArrayList<>();
        for (int i = 0; i < nrTowers; i++) {
            properties.add(getTowerValues(nrTowerLevels));
            labels.add("Tower " + i + " level");
        }

        for (int i = 0; i < nrDeployTypes; i++) {
            properties.add(getDeployValues(unitBufSize));
            labels.add("Deploy type " + i);
        }

        properties.add(getQLearnerTrustValues(qLearnerTrustSteps));
        labels.add("QLearner trust");

        // Only allow inputs where at most one unit is placed
        inputs = Sets.cartesianProduct(properties).stream().filter(l -> {
            int sum = 0;
            for (int i = nrTowers; i < nrTowers + nrDeployTypes; i++) {
                sum += l.get(i);
            }
            return sum == 1;
        }).collect(Collectors.toSet());

        return inputs;
    }

    public void writeCSV(FileOutputStream fos) throws IOException {
        String header = labels.stream().collect(Collectors.joining(",")) + "\n";
        fos.write(header.getBytes());
        for (List<Float> row : getInputs()) {
            String line = row.stream().map(Object::toString).collect(Collectors.joining(",")) + "\n";
            fos.write(line.getBytes());
        }

    }

    private static Set<Float> getQLearnerTrustValues(int steps) {
        return discretize(steps);
    }

    private static Set<Float> getTowerValues(int levels) {
        return discretize(levels);
    }

    private static Set<Float> discretize(int steps) {
        Set<Float> res = new HashSet<>(steps + 1);
        for (float i = 0; i <= steps; i++) {
            res.add(i / steps);
        }
        return res;
    }

    private static Set<Float> getDeployValues(int unitBufSize) {
        return discretize(unitBufSize);
    }
}
