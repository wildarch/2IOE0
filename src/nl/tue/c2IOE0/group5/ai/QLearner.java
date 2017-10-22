package nl.tue.c2IOE0.group5.ai;

import com.sun.prism.es2.ES2Graphics;
import nl.tue.c2IOE0.group5.providers.GridProvider;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Tom Peters
 *
 * A class to calculate the best policy for every reward grid
 * A state in the grid can be calculated as followed:
 * s = x + gridSize * y;
 * x = s % gridSize
 * y = s / gridSize (integer devision)
 */

public class QLearner {

    private Integer[][] rewards;
    private int[] policy;
    private List<Integer[]> paths;
    private int noIterations;
    private List<Integer> outerStates;
    private boolean converged = false;

    private Double[][] Q;

    private static final Integer[] basicPath = {
            1,2,3,4,5,6,7,8,9,10,11,12,
            25,24,23,22,21,20,19,18,17,16,15,14,13,
            26,27,28,29,30,31,32,33,34,35,36,37,38,
            51,50,49,48,47,46,45,44,43,42,41,40,39,
            52,53,54,55,56,57,58,59,60,61,62,63,64,
            77,76,75,74,73,72,71,7,69,68,67,66,65,
            78,79,80,81,82,83,70,71,72,85,86,87,88,89,90,
            103,102,101,100,99,98,97,96,95,94,93,92,91,
            104,105,106,107,108,109,110,111,112,113,114,115,116,
            129,128,127,126,125,124,123,122,121,120,119,118,117,
            130,131,132,133,134,135,136,137,138,139,140,141,142,
            155,154,153,152,151,150,149,148,147,146,145,144,143,
            156,157,158,159,160,161,162,163,164,165,166,167,168
    };

    /**
     * @param gridSize obvious
     */
    public QLearner(int gridSize, int noIterations) {
        makeRewardMatrix();
        paths = new ArrayList<>();
        this.noIterations = noIterations;

        outerStates = new ArrayList<>();
        for (int i = 0; i < gridSize; i++) { //add bottom states
            outerStates.add(i);
        }
        for (int i = gridSize - 1 + gridSize; i < gridSize * gridSize; i += gridSize) { //add right states
            outerStates.add(i);
        }
        for (int i = gridSize; i < gridSize * gridSize - 1; i += gridSize) { //add left states
            outerStates.add(i);
        }
        for (int i = gridSize * gridSize - gridSize + 1; i < gridSize * gridSize - 2; i++) {
            outerStates.add(i);
        }
    }

    public boolean isConverged() {return this.converged;}

    public void initializeQ() {
        // Initialize Q as only 0
        Q = new Double[rewards.length][rewards[0].length];
        for (int i = 0; i < Q.length; i++) {
            for (int j = 0; j < Q[0].length; j++) {
                if (rewards[i][j] == null) {
                    Q[i][j] = null;
                } else {
                    Q[i][j] = 0d;
                }
            }
        }
        converged = false;
    }

    /**
     * @param gamma the learning coefficient
     * @return Whether or not the Q Learner has converged and is done learning for this specific rewards matrix
     */
    public boolean execute(Double gamma) {
        boolean convergedLocal = false;
        // Do Q-learning
        for (int iteration = 0; iteration < noIterations; iteration++) {
            for (Integer[] path : paths) {
                boolean pathConverged = execute(rewards, path, gamma);
                if (pathConverged) {
                    convergedLocal = true;
                }
            }
        }
        policy();
        converged = convergedLocal;
        return converged;
    }

    public void setNoIterations(int noIterations) {
        this.noIterations = noIterations;
    }

    /**
     * Initialize the rewards matrix
     */
    private void makeRewardMatrix() {
        this.rewards = new Integer[GridProvider.SIZE*GridProvider.SIZE][GridProvider.SIZE*GridProvider.SIZE];
        for (int y = 0; y < GridProvider.SIZE; y++) {
            for (int x = 0; x < GridProvider.SIZE; x++) { //first y and the x to make sure the state increases
                int state = getState(x, y);
                if (x == GridProvider.SIZE / 2 && y == GridProvider.SIZE / 2) { //the middle cell
                    this.rewards[state][state] = 0; //can only go to itself
                    continue; //continue with the next cell
                }
                for (int neighbour : getStatesAdjacent(state)) {
                    this.rewards[state][neighbour] = 0;
                }
            }
        }
        converged = false;
    }


    public void updateRewardsMatrix(int state, int reward) {
        List<Integer> neighbours = getStatesAdjacent(state);
        for (int neighbour : neighbours) {
            this.rewards[neighbour][state] = reward;
        }
        converged = false;
    }

    public static int getState(int x, int y) {
        return x + GridProvider.SIZE * y;
    }

    public static int getState(Vector2i p) {
        return getState(p.x(), p.y());
    }

    public static Vector2i getPoint(int state) {
        return new Vector2i(state % GridProvider.SIZE, state / GridProvider.SIZE);
    }

    public List<Integer> getStatesAdjacent(int state) {
        List<Integer> neighbours = new ArrayList<>();
        if (state >= GridProvider.SIZE) { //there is a bottom
            neighbours.add(state - GridProvider.SIZE);
        }
        if (state < GridProvider.SIZE * GridProvider.SIZE - GridProvider.SIZE) { //there is a top
            neighbours.add(state + GridProvider.SIZE);
        }
        if (state % GridProvider.SIZE != 0) { //there is a left
            neighbours.add(state - 1);
        }
        if (state % GridProvider.SIZE != GridProvider.SIZE - 1) { //there is a right
            neighbours.add(state + 1);
        }
        return neighbours;
    }

    /**
     * do Q-learning for one path. Does the Q learning algorithm for one path.
     * helper method for the first execute method
     * @return Whether or not the Q Learner has converged for this path
     */
    private boolean execute(Integer[][] rewards, Integer[] path, Double gamma) {
        boolean convergedLocal = true;
        if (path.length == 0) {
            System.err.println("Empty path in qlearner");
            return false;
        }
        int s = path[0];
        for (Integer a : path) {
            if (rewards[s][a] != null) {
                Integer maxAction = getMaximumAction(a);
                if (maxAction == null) {
                    System.err.println("From " + s + " to " + a + " is not a valid action in some qlearner path.");
                    break;
                }
                double previousQ = Q[s][a];
                Q[s][a] = rewards[s][a] + gamma * Q[a][maxAction];
                double newQ = Q[s][a];
                s = a;
                if (previousQ != newQ) {
                    convergedLocal = false;
                }
            }
        }
        return convergedLocal;
    }

    /**
     * Gets action in a certain state for which the Q value is the highest
     */
    private Integer getMaximumAction(int state) {
        Integer max = null;
        for (int a = 0; a < Q[state].length; a++) {
            Double currentQValue = Q[state][a];
            if (currentQValue != null) {
                if (max == null) {
                    max = a;
                } else if (currentQValue > Q[state][max]) {
                    max = a;
                }
            }
        }
        return max;
    }

    public void generateRandomPath(int length) {
        Random r = new Random();
        generateRandomPath(length, r.nextInt(GridProvider.SIZE * GridProvider.SIZE - 1));
    }

    public void generateRandomPath(int length, int startState) {
        Integer[] path = new Integer[length];
        Random r = new Random();
        path[0] = startState;
        int state = startState;
        int nextState = startState;
        for (int i = 1; i < length; i++) {
            List<Integer> neighbours = getStatesAdjacent(nextState);
            int random;
            do {
                random = r.nextInt(neighbours.size());
            } while (neighbours.get(random) == state);
            state = nextState;
            nextState = neighbours.get(random);
            path[i] = state;
        }
        paths.add(path);
    }

    public void addPath(Integer[] path) {
        paths.add(path);
    }

    public void addBasicPath() {
        addPath(basicPath);
    }

    public void deletePaths() {
        paths = new ArrayList<>();
    }

    /**
     * computes the policy, according to a certain Q, for a certain state
     */
    private void policy() {
        policy = new int[Q.length];
        for (int s = 0; s < Q.length; s++) {
            Integer maxAction = getMaximumAction(s);
            policy[s] = maxAction;
        }
    }

    /**
     * Return the optimal path for a specific staten
     */
    public List<Integer> getOptimalPath(int state) {
        List<Integer> optimalPath = new ArrayList<>();
        optimalPath.add(state);
        int nextState = policy[state];
        while (nextState != state && !optimalPath.contains(nextState)) {
            state = nextState;
            optimalPath.add(state);
            nextState = policy[state];
        }
        return optimalPath;
    }

    public List<Integer> getOptimalPath(Vector2i state) {
        return getOptimalPath(getState(state));
    }

    /**
     * Return the spawn location with the best Q learner options
     */
    public Vector2i getOptimalSpawnState() {
        return getOptimalNSpawnStates(1)[0];
    }

    /**
     * Get the n best spawnstates
     * @param n the number of spawnstates to get
     * @return the best n spawnstates
     */
    public Vector2i[] getOptimalNSpawnStates(int n) {
        int[] maxQ = new int[n];
        int[] maxStates = new int[n];
        for (int i = 0; i < n; i++) {
            maxQ[i] = 0;
            maxStates[i] = 0;
        }

        for (int i : outerStates) {
            for (int j = 0; j < n; j++) {
                if (getMaximumAction(i) >= maxQ[j]) {
                    maxQ[j] = getMaximumAction(i);
                    maxStates[j] = i;
                    break; // it is already in the array, so continue with the next element
                }
            }
        }
        Vector2i[] results = new Vector2i[n];
        for (int i = 0; i < n; i++) {
            results[i] = new Vector2i(getPoint(maxStates[i]));
        }
        return results;
    }

}

