package com.github.mathiewz.geom;

/**
 * A single point shape
 *
 * @author Kova
 */
public class Point extends Shape {
    /**
     * Create a new point
     *
     * @param x
     *            The x coordinate of the point
     * @param y
     *            The y coordinate of the point
     */
    public Point(float x, float y) {
        this.x = x;
        this.y = y;
        checkPoints();
    }
    
    /**
     * @see com.github.mathiewz.geom.Shape#transform(com.github.mathiewz.geom.Transform)
     */
    @Override
    public Shape transform(Transform transform) {
        Float[] result = new Float[points.length];
        transform.transform(points, 0, result, 0, points.length / 2);
        
        return new Point(points[0], points[1]);
    }
    
    /**
     * @see com.github.mathiewz.geom.Shape#createPoints()
     */
    @Override
    protected void createPoints() {
        points = new Float[2];
        points[0] = x;
        points[1] = y;
        
        maxX = x;
        maxY = y;
        minX = x;
        minY = y;
        
        findCenter();
        calculateRadius();
    }
    
    /**
     * @see com.github.mathiewz.geom.Shape#findCenter()
     */
    @Override
    protected void findCenter() {
        center = new float[2];
        center[0] = points[0];
        center[1] = points[1];
    }
    
    /**
     * @see com.github.mathiewz.geom.Shape#calculateRadius()
     */
    @Override
    protected void calculateRadius() {
        boundingCircleRadius = 0;
    }
    
}