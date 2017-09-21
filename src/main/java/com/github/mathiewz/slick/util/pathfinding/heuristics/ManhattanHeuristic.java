package com.github.mathiewz.slick.util.pathfinding.heuristics;

import com.github.mathiewz.slick.util.pathfinding.AStarHeuristic;
import com.github.mathiewz.slick.util.pathfinding.Mover;
import com.github.mathiewz.slick.util.pathfinding.TileBasedMap;

/**
 * A heuristic that drives the search based on the Manhattan distance
 * between the current location and the target
 *
 * @author Kevin Glass
 */
public class ManhattanHeuristic implements AStarHeuristic {
    /** The minimum movement cost from any one square to the next */
    private final int minimumCost;

    /**
     * Create a new heuristic
     *
     * @param minimumCost
     *            The minimum movement cost from any one square to the next
     */
    public ManhattanHeuristic(int minimumCost) {
        this.minimumCost = minimumCost;
    }

    /**
     * @see AStarHeuristic#getCost(TileBasedMap, Mover, int, int, int, int)
     */
    @Override
    public float getCost(TileBasedMap map, Mover mover, int x, int y, int tx, int ty) {
        return (float) minimumCost * (Math.abs(x - tx) + Math.abs(y - ty));
    }

}
