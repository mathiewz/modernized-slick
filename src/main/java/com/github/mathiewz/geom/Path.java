package com.github.mathiewz.geom;

import java.util.ArrayList;

/**
 * A shape built from lines and curves. Hole support is present but
 * restricted.
 *
 * @author kevin
 */
public class Path extends Shape {
    /** The local list of points */
    private ArrayList<Float[]> localPoints = new ArrayList<>();
    /** The current x coordinate */
    private Float cx;
    /** The current y coordiante */
    private Float cy;
    /** True if the path has been closed */
    private boolean closed;
    /** The list of holes placed */
    private final ArrayList<ArrayList<Float[]>> holes = new ArrayList<>();
    /** The current hole being built */
    private ArrayList<Float[]> hole;
    
    /**
     * Create a new path
     *
     * @param sx
     *            The start x coordinate of the path
     * @param sy
     *            The start y coordiante of the path
     */
    public Path(Float sx, Float sy) {
        localPoints.add(new Float[] { sx, sy });
        cx = sx;
        cy = sy;
        pointsDirty = true;
    }
    
    /**
     * Start building a hole in the previously defined contour
     */
    public void startHole() {
        hole = new ArrayList<>();
        holes.add(hole);
    }
    
    /**
     * Add a line to the contour or hole which ends at the specified
     * location.
     *
     * @param x
     *            The x coordinate to draw the line to
     * @param y
     *            The y coordiante to draw the line to
     */
    public void lineTo(Float x, Float y) {
        if (hole != null) {
            hole.add(new Float[] { x, y });
        } else {
            localPoints.add(new Float[] { x, y });
        }
        cx = x;
        cy = y;
        pointsDirty = true;
    }
    
    /**
     * Close the path to form a polygon
     */
    public void close() {
        closed = true;
    }
    
    /**
     * Add a curve to the specified location (using the default segments 10)
     *
     * @param x
     *            The destination x coordinate
     * @param y
     *            The destination y coordiante
     * @param cx1
     *            The x coordiante of the first control point
     * @param cy1
     *            The y coordiante of the first control point
     * @param cx2
     *            The x coordinate of the second control point
     * @param cy2
     *            The y coordinate of the second control point
     */
    public void curveTo(Float x, Float y, Float cx1, Float cy1, Float cx2, Float cy2) {
        curveTo(x, y, cx1, cy1, cx2, cy2, 10);
    }
    
    /**
     * Add a curve to the specified location (specifing the number of segments)
     *
     * @param x
     *            The destination x coordinate
     * @param y
     *            The destination y coordiante
     * @param cx1
     *            The x coordiante of the first control point
     * @param cy1
     *            The y coordiante of the first control point
     * @param cx2
     *            The x coordinate of the second control point
     * @param cy2
     *            The y coordinate of the second control point
     * @param segments
     *            The number of segments to use for the new curve
     */
    public void curveTo(Float x, Float y, Float cx1, Float cy1, Float cx2, Float cy2, int segments) {
        // special case for zero movement
        if (cx == x && cy == y) {
            return;
        }
        
        Curve curve = new Curve(new Vector2f(cx, cy), new Vector2f(cx1, cy1), new Vector2f(cx2, cy2), new Vector2f(x, y));
        Float step = 1.0f / segments;
        
        for (int i = 1; i < segments + 1; i++) {
            Float t = i * step;
            Vector2f p = curve.pointAt(t);
            if (hole != null) {
                hole.add(new Float[] { p.getX(), p.getY() });
            } else {
                localPoints.add(new Float[] { p.getX(), p.getY() });
            }
            cx = p.getX();
            cy = p.getY();
        }
        pointsDirty = true;
    }
    
    /**
     * @see com.github.mathiewz.geom.Shape#createPoints()
     */
    @Override
    protected void createPoints() {
        points = new Float[localPoints.size() * 2];
        for (int i = 0; i < localPoints.size(); i++) {
            Float[] p = localPoints.get(i);
            points[i * 2] = p[0];
            points[i * 2 + 1] = p[1];
        }
    }
    
    /**
     * @see com.github.mathiewz.geom.Shape#transform(com.github.mathiewz.geom.Transform)
     */
    @Override
    public Shape transform(Transform transform) {
        Path p = new Path(cx, cy);
        p.localPoints = transform(localPoints, transform);
        for (int i = 0; i < holes.size(); i++) {
            p.holes.add(transform(holes.get(i), transform));
        }
        p.closed = closed;
        
        return p;
    }
    
    /**
     * Transform a list of points
     *
     * @param pts
     *            The pts to transform
     * @param t
     *            The transform to apply
     * @return The transformed points
     */
    private ArrayList<Float[]> transform(ArrayList<Float[]> pts, Transform t) {
        Float[] in = new Float[pts.size() * 2];
        Float[] out = new Float[pts.size() * 2];
        
        for (int i = 0; i < pts.size(); i++) {
            in[i * 2] = pts.get(i)[0];
            in[i * 2 + 1] = pts.get(i)[1];
        }
        t.transform(in, 0, out, 0, pts.size());
        
        ArrayList<Float[]> outList = new ArrayList<>();
        for (int i = 0; i < pts.size(); i++) {
            outList.add(new Float[] { out[i * 2], out[i * 2 + 1] });
        }
        
        return outList;
    }
    
    /**
     * True if this is a closed shape
     *
     * @return True if this is a closed shape
     */
    @Override
    public boolean closed() {
        return closed;
    }
}
