package nl.tue.c2IOE0.group5.ai;

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

    Integer[][] rewards;
    int[] policy;
    List<Integer[]> paths;
    int noIterations;
    List<Integer> outerStates;

    Double[][] Q;

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

    /**
     * @param gamma the learning coefficient
     * @return The optimal policy for the input
     */
    public void execute(Double gamma) {
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

        // Do Q-learning
        for (int iteration = 0; iteration < noIterations; iteration++) {
            for (Integer[] path : paths) {
                execute(rewards, path, gamma);
            }
        }

        policy();
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
    }


    public void updateRewardsMatrix(int state, int reward) {
        List<Integer> neighbours = getStatesAdjacent(state);
        for (int neighbour : neighbours) {
            this.rewards[neighbour][state] = reward;
        }
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
     * do Q-learning for one path. Does the Q learning alogrithm for one path.
     * helper method for the first execute method
     */
    private void execute(Integer[][] rewards, Integer[] path, Double gamma) {
        if (path.length == 0) {
            System.err.println("Empty path in qlearner");
            return;
        }
        int s = path[0];
        for (Integer a : path) {
            if (rewards[s][a] != null) {
                Integer maxAction = getMaximumAction(a);
                if (maxAction == null) {
                    System.err.println("From " + s + " to " + a + " is not a valid action in some qlearner path.");
                    break;
                }
                Q[s][a] = rewards[s][a] + gamma * Q[a][maxAction];
                s = a;
            }
        }
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
        int maxQ = 0;
        int maxState = 0;
        for (int i : outerStates) {
            if (getMaximumAction(i) >= maxQ) {
                maxQ = getMaximumAction(i);
                maxState = i;
            }
        }
        return new Vector2i(getPoint(maxState));
    }

    /**
     * Get the n best spawnstates
     * @param n
     * @return
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

