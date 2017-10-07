package nl.tue.c2IOE0.group5;

import javafx.util.Pair;
import nl.tue.c2IOE0.group5.providers.Cell;

import java.util.ArrayList;
import java.util.List;

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

    int gridSize;
    Integer[][] rewards;
    int[] policy;

    /**
     * @param gridSize obvious
     */
    public QLearner(int gridSize) {
        this.gridSize = gridSize;
        makeRewardMatrix();
    }

    /**
     * @param paths an array containing the paths to be followed
     * @param gamma the learning coefficient
     * @param noIterations the amount of times all the paths have to be traveled
     * @param state The state to calculate the optimal route for
     * @return The optimal policy for the input
     */
    public void execute(Integer[][] paths, Double gamma, Integer noIterations) {
        // Initialize Q as only 0
        final Double[][] Q = new Double[rewards.length][rewards[0].length];
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
                execute(Q, rewards, path, gamma);
            }
        }

        policy(Q);
    }

    /**
     * Initialize the rewards matrix
     */
    private void makeRewardMatrix() {
        this.rewards = new Integer[gridSize*gridSize][gridSize*gridSize];
        for (int y = 0; y < gridSize; y++) {
            for (int x = 0; x < gridSize; x++) { //first y and the x to make sure the state increases
                int state = getState(x, y);
                if (x == gridSize / 2 && y == gridSize / 2) { //the middle cell
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

    public int getState(int x, int y) {
        return x + gridSize * y;
    }

    public int getState(Cell.Point p) {
        return getState(p.getX(), p.getY());
    }

    public int getX(int state) {
        return state % gridSize;
    }

    public int getY(int state) {
        return state / gridSize;
    }

    public List<Integer> getStatesAdjacent(int state) {
        List<Integer> neighbours = new ArrayList<>();
        if (state >= gridSize) { //there is a bottom
            neighbours.add(state - gridSize);
        }
        if (state < gridSize * gridSize - gridSize) { //there is a top
            neighbours.add(state + gridSize);
        }
        if (state % gridSize != 0) { //there is a left
            neighbours.add(state - 1);
        }
        if (state % gridSize != gridSize - 1) { //there is a right
            neighbours.add(state + 1);
        }
        return neighbours;
    }

    /**
     * do Q-learning for one path. Does the Q learning alogrithm for one path.
     * helper method for the first execute method
     */
    private void execute(Double[][] Q, Integer[][] rewards, Integer[] path, Double gamma) {
        if (path.length == 0) {
            System.err.println("Empty path in qlearner");
            return;
        }
        int s = path[0];
        for (Integer a : path) {
            if (rewards[s][a] != null) {
                Integer maxAction = getMaximumAction(Q, a);
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
    private Integer getMaximumAction(Double[][] Q, int state) {
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

    /**
     * computes the policy, according to a certain Q, for a certain state
     */
    private void policy(Double[][] Q) {
        policy = new int[Q.length];
        for (int s = 0; s < Q.length; s++) {
            Integer maxAction = getMaximumAction(Q, s);
            policy[s] = maxAction;
        }
    }

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
}

