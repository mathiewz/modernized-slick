package com.github.mathiewz.slick.svg;

import com.github.mathiewz.slick.geom.Shape;
import com.github.mathiewz.slick.geom.TexCoordGenerator;
import com.github.mathiewz.slick.geom.Transform;
import com.github.mathiewz.slick.geom.Vector2f;

/**
 * A filler to apply a SVG radial gradient across a shape
 *
 * @author kevin
 */
public class RadialGradientFill implements TexCoordGenerator {
    /** The centre of the gradient */
    private final Vector2f centre;
    /** The radius before the gradient is complete */
    private float radius;

    /**
     * Create a new fill for a radial gradient
     *
     * @param shape
     *            The shape being filled
     * @param trans
     *            The transform given for the shape in the SVG
     * @param gradient
     *            The gradient to apply across the shape
     */
    public RadialGradientFill(Shape shape, Transform trans, Gradient gradient) {
        radius = gradient.getR();
        float x = gradient.getX1();
        float y = gradient.getY1();

        Float[] c = new Float[] { x, y };
        gradient.getTransform().transform(c, 0, c, 0, 1);
        trans.transform(c, 0, c, 0, 1);
        Float[] rt = new Float[] { x, y - radius };
        gradient.getTransform().transform(rt, 0, rt, 0, 1);
        trans.transform(rt, 0, rt, 0, 1);

        centre = new Vector2f(c[0], c[1]);
        Vector2f dis = new Vector2f(rt[0], rt[1]);
        radius = dis.distance(centre);
    }

    /**
     * @see com.github.mathiewz.slick.geom.TexCoordGenerator#getCoordFor(float, float)
     */
    @Override
    public Vector2f getCoordFor(float x, float y) {
        float u = centre.distance(new Vector2f(x, y));
        u /= radius;

        if (u > 0.99f) {
            u = 0.99f;
        }

        return new Vector2f(u, 0);
    }

}
