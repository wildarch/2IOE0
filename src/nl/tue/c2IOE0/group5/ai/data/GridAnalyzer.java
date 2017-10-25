package nl.tue.c2IOE0.group5.ai.data;

import nl.tue.c2IOE0.group5.providers.Cell;
import nl.tue.c2IOE0.group5.providers.GridProvider;
import nl.tue.c2IOE0.group5.towers.AbstractTower;

import java.util.stream.Stream;

public class GridAnalyzer {
    private GridProvider gridProvider;

    private int startScore = 0;

    public GridAnalyzer(GridProvider gridProvider) {
        this.gridProvider = gridProvider;
    }

    public void start() {
        startScore = getTotalTowerScore();
    }

    /**
     * A measure of destruction since `start()`
     * @return A score between 0 and 1
     */
    public float getDestructionScore() {
        int endScore = getTotalTowerScore();
        return 1 - (endScore / startScore);
    }

    /**
     * Calculates the score of all towers on the board.
     * Towers with higher price are considered more valuable
     */
    private int getTotalTowerScore() {
        return getTowers()
                .mapToInt(AbstractTower::getPrice)
                .sum();
    }

    private Stream<AbstractTower> getTowers() {
        return gridProvider.stream()
                .filter(c -> c.getTower() != null)
                .map(Cell::getTower);
    }
}