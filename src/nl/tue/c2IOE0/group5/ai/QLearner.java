package nl.tue.c2IOE0.group5.ai;

import nl.tue.c2IOE0.group5.providers.GridProvider;
import org.joml.Vector2i;
import org.lwjgl.opengl.GLXARBRobustnessApplicationIsolation;

import java.util.*;

/**
 * @author Tom Peters
 *
 * A class to calculate the best policy for every reward grid
 * A state in the grid can be calculated as followed:
 * s = x + gridSize * y;
 * x = s % gridSize
 * y = s / gridSize (integer devision)
 */

public class QLearner extends Thread {

    private Integer[][] rewards;
    private int[] policy;
    private List<Integer[]> paths;
    private int noIterations;
    private List<Integer> outerStates;
    private volatile boolean converged = false;
    private double gamma;

    private final int gridSize;

    private Double[][] Q;

    // A basic path covering every state
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
     * @param gridSize obvious.
     * @param noIterations the amount of iterations per path
     * @param gamma the learning quotient
     */
    public QLearner(int gridSize, int noIterations, double gamma) {
        this.gamma = gamma;
        this.noIterations = noIterations;
        this.gridSize = gridSize;

        makeRewardMatrix();
        paths = new ArrayList<>();

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

    public double getGamma() {return this.gamma;}

    public void setGamma(double gamma) {this.gamma = gamma;}

    /**
     * Check if the Q learner has converged
     * @return whether or  not the Q learner has converged
     */
    public boolean isConverged() {return this.converged;}

    /**
     * Initialize the Q matrix based on the reward matrix
     */
    public void initializeQ() {
        if (rewards != null);

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
     * Do Q learning in a different thread
     */
    @Override
    public void run() {
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
        nrofThreads--;
    }

    private static final int MAX_THREADS = 5;
    private volatile int nrofThreads = 0;
    private Thread first;
    /**
     * Start a new Thread to learn, upto a maximum amount of threads
     */
    public void execute() {
        if (first == null) {
            first = new Thread(this);
            first.start();
            try {
                first.join();       //wait for the first thread to finish
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return;
        } else if (nrofThreads < MAX_THREADS) {
            Thread t = new Thread(this);
            t.start();
            nrofThreads++;
        }
    }

    /**
     * A setter method for the number of iterations per path
     * @param noIterations
     */
    public void setNoIterations(int noIterations) {
        this.noIterations = noIterations;
    }

    /**
     * Initialize the rewards matrix in a grid like fashion
     */
    private void makeRewardMatrix() {
        this.rewards = new Integer[gridSize*gridSize][gridSize*gridSize];

        for (int y = 0; y < gridSize; y++) {
            for (int x = 0; x < gridSize; x++) { //first y and then x to make sure the state increases
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
        converged = false;
        setRewardsMatrix(getState(gridSize / 2, gridSize / 2, gridSize), 100000); //set the middle to the max
    }

    /**
     * Set the reward for entering a specific state
     * @param state the state to enter
     * @param reward the reward for entering that state
     */
    public void setRewardsMatrix(int state, int reward) {
        List<Integer> neighbours = getStatesAdjacent(state);
        for (int neighbour : neighbours) {
            this.rewards[neighbour][state] = reward;
        }
        converged = false;
    }

    /**
     * Increase the reward for entering a specific state
     * @param state the state to enter
     * @param rewardAdd the reward to add to the reward already gained by entering this state
     */
    public void updateRewardsMatrix(int state, int rewardAdd) {
        List<Integer> neighbours = getStatesAdjacent(state);
        for (int neighbour : neighbours) {
            if (rewards[neighbour][state] != null) {
                this.rewards[neighbour][state] += rewardAdd;
                if (this.rewards[neighbour][state] <= Integer.MIN_VALUE + 100000000) { //if damage is becoming too low, set it back to prevent overflow
                    this.rewards[neighbour][state] += 100000000;
                }
            }
        }
        converged = false;
    }

    /**
     * Convert a grid position to a Q learner state
     * @param x
     * @param y
     * @param gridSize
     * @return the Q learner state
     */
    public static int getState(int x, int y, int gridSize) {
        return x + gridSize * y;
    }

    /**
     * Convert a grid position to a Q learner state
     * @param x
     * @param y
     * @return the Q learner state
     */
    public int getState(int x, int y){
        return getState(x, y, gridSize);
    }

    /**
     * Convert a grid position to a Q learner state
     * @param p
     * @param gridSize
     * @return the Q learner state
     */
    public static int getState(Vector2i p, int gridSize) {
        return getState(p.x(), p.y(), gridSize);
    }

    /**
     * Convert a grid position to a Q learner state
     * @param p
     * @return the Q learner state
     */
    public int getState(Vector2i p){
        return getState(p, gridSize);
    }

    /**
     * Convert a Q learner state to a grid position
     * @param state
     * @param gridSize
     * @return the grid position
     */
    public static Vector2i getPoint(int state, int gridSize) {
        return new Vector2i(state % gridSize, state / gridSize);
    }

    /**
     * Convert a Q learner state tot a grid position
     * @param state
     * @return the grid position
     */
    public Vector2i getPoint(int state){
        return getPoint(state, gridSize);
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

    /**
     * Generate a random path through the grid
     * @param length the length of the random path
     */
    public void generateRandomPath(int length) {
        Random r = new Random();
        generateRandomPath(length, r.nextInt(gridSize * gridSize - 1));
    }

    /**
     * Generate a random path through the grid starting at a specific state
     * @param length the length of the path
     * @param startState the state to start in
     */
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

    /**
     * Add a path to the paths to try
     * @param path the path to add
     */
    public void addPath(Integer[] path) {
        paths.add(path);
    }

    /**
     * Add the basic path defined up top to the paths to try
     */
    public void addBasicPath() {
        addPath(basicPath);
    }

    /**
     * Delete all the paths from the paths to try.
     */
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
        if (policy == null) {
            throw new NullPointerException("QLearner has not yet learned anything. Please call execute first.");
        }
        List<Integer> optimalPath = new ArrayList<>();
        optimalPath.add(state);
        int nextState = policy[state];
        while (nextState != state && !optimalPath.contains(nextState)) {
            state = nextState;
            optimalPath.add(state);
            nextState = policy[state];
        }
        if (optimalPath.get(optimalPath.size() - 1) != getState(gridSize / 2, gridSize / 2)) {  //they start to cuddle in a corner
            int middlestate = getState(gridSize /2 , gridSize / 2);
            updateRewardsMatrix(middlestate, rewards[middlestate-1][middlestate]/10);      //increase the reward for the middle square by 10%
            return getBasicPath(state);                                                            //and return a basic path
        }
        return optimalPath;
    }

    /**
     * Return a basic path for a specific state. Just go horizontal until the middleline, then go vertical to the center
     * @param state
     * @return a basic path
     */
    private List<Integer> getBasicPath(int state) {
        int x = getPoint(state).x();
        int y = getPoint(state).y();
        List<Integer> path = new ArrayList<>();
        path.add(state);
        if (x < gridSize / 2) { //do the horizontal stuff
            while (x < gridSize / 2) {
                x++;
                path.add(getState(x, y));
            }
        } else if (x > gridSize / 2) {
            while (x > gridSize / 2) {
                x--;
                path.add(getState(x, y));
            }
        }

        if (y < gridSize / 2) {
            while (y < gridSize / 2) {
                y++;
                path.add(getState(x, y));
            }
        } else if (y > gridSize / 2) {
            while (y > gridSize / 2) {
                y--;
                path.add(getState(x, y));
            }
        }
        return path;
    }

    /**
     * Get the optimal path for a specific grid position
     * @param p the grid position to get the optimal path from
     * @return A list containing the optimal path
     */
    public List<Integer> getOptimalPath(Vector2i p) {
        return getOptimalPath(getState(p, gridSize));
    }

    /**
     * Return the spawn location with the best Q value
     */
    public Vector2i getOptimalSpawnState() {
        return getOptimalNSpawnStates(1)[0];
    }

    /**
     * Get the n best spawnstates, if there are more than needed with the same Q value, return random ones
     * @param n the number of spawnstates to get
     * @return the best n spawnstates
     */
    public Vector2i[] getOptimalNSpawnStates(int n) {
        double[] maxQ = new double[n];
        int[] maxStates = new int[n];
        for (int i = 0; i < n; i++) {
            maxQ[i] = 0;
            maxStates[i] = 0;
        }

        for (int outerState : outerStates) {
            for (int j = 0; j < n; j++) {
                if (Q[outerState][getMaximumAction(outerState)] >= maxQ[j]) {
                    maxQ[j] = Q[outerState][getMaximumAction(outerState)];
                    maxStates[j] = outerState;
                    break; // it is already in the array, so continue with the next element
                }
            }
        }
        Vector2i[] results = new Vector2i[n];
        for (int i = 0; i < n; i++) {
            results[i] = new Vector2i(getPoint(maxStates[i], gridSize));
        }
        return results;
    }

    /**
     * Gets the reward for a specific cell on x, y
     */
    public int getReward(Vector2i pos) {
        int state = getState(pos.x, pos.y);
        List<Integer> neighbours = getStatesAdjacent(state);
        Integer reward = rewards[neighbours.get(0)][state];

        return reward == null ? 0 : reward;
    }

}

