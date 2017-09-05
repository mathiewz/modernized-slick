package org.newdawn.slick.svg;

import org.newdawn.slick.geom.Line;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.TexCoordGenerator;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.geom.Vector2f;

/**
 * A filler for shapes that applys SVG linear gradients
 *
 * @author kevin
 */
public class LinearGradientFill implements TexCoordGenerator {
    /** The start position of the gradient line */
    private final Vector2f start;
    /** The ends position of the gradient line */
    private final Vector2f end;
    /** The line of the gradient */
    private final Line line;

    /**
     * Create a new fill for gradients
     *
     * @param shape
     *            The shape being filled
     * @param trans
     *            The transform given for the shape
     * @param gradient
     *            The gradient to apply
     */
    public LinearGradientFill(Shape shape, Transform trans, Gradient gradient) {
        Float x = gradient.getX1();
        Float y = gradient.getY1();
        Float mx = gradient.getX2();
        Float my = gradient.getY2();

        Float h = my - y;
        Float w = mx - x;

        Float[] s = new Float[] { x, y + h / 2 };
        gradient.getTransform().transform(s, 0, s, 0, 1);
        trans.transform(s, 0, s, 0, 1);
        Float[] e = new Float[] { x + w, y + h / 2 };
        gradient.getTransform().transform(e, 0, e, 0, 1);
        trans.transform(e, 0, e, 0, 1);

        start = new Vector2f(s[0], s[1]);
        end = new Vector2f(e[0], e[1]);

        line = new Line(start, end);
    }

    /**
     * @see org.newdawn.slick.geom.TexCoordGenerator#getCoordFor(float, float)
     */
    @Override
    public Vector2f getCoordFor(float x, float y) {
        Vector2f result = new Vector2f();
        line.getClosestPoint(new Vector2f(x, y), result);
        float u = result.distance(start);
        u /= line.length();

        return new Vector2f(u, 0);
    }

}
