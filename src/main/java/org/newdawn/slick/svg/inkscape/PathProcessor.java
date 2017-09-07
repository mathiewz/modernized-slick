package org.newdawn.slick.svg.inkscape;

import java.util.Optional;
import java.util.StringTokenizer;

import org.newdawn.slick.geom.Path;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.svg.Diagram;
import org.newdawn.slick.svg.Figure;
import org.newdawn.slick.svg.Loader;
import org.newdawn.slick.svg.NonGeometricData;
import org.newdawn.slick.svg.ParsingException;
import org.w3c.dom.Element;

/**
 * A processor for the <polygon> and <path> elements marked as not an arc.
 *
 * @author kevin
 */
public class PathProcessor implements ElementProcessor {

    /**
     * Process the points in a polygon definition
     *
     * @param element
     *            The XML element being read
     * @param tokens
     *            The tokens representing the path
     * @return The number of points found
     */
    private static Path processPoly(Element element, StringTokenizer tokens) {
        boolean moved = false;
        boolean reasonToBePath = false;
        Optional<Path> optPath = Optional.empty();

        while (tokens.hasMoreTokens()) {
            try {
                String nextToken = tokens.nextToken();
                float x;
                float y;
                switch (nextToken) {
                    case "L":
                        x = Float.parseFloat(tokens.nextToken());
                        y = Float.parseFloat(tokens.nextToken());
                        optPath.ifPresent(path -> path.lineTo(x, y));
                        break;
                    case "z":
                        optPath.ifPresent(Path::close);
                        break;
                    case "M":
                        if (!moved) {
                            moved = true;
                            x = Float.parseFloat(tokens.nextToken());
                            y = Float.parseFloat(tokens.nextToken());
                            optPath = Optional.of(new Path(x, y));
                            continue;
                        }
                        reasonToBePath = true;
                        optPath.ifPresent(Path::startHole);
                        break;
                    case "C":
                        reasonToBePath = true;
                        float cx1 = Float.parseFloat(tokens.nextToken());
                        float cy1 = Float.parseFloat(tokens.nextToken());
                        float cx2 = Float.parseFloat(tokens.nextToken());
                        float cy2 = Float.parseFloat(tokens.nextToken());
                        x = Float.parseFloat(tokens.nextToken());
                        y = Float.parseFloat(tokens.nextToken());
                        optPath.ifPresent(path -> path.curveTo(x, y, cx1, cy1, cx2, cy2));
                        break;
                    default:
                        break;
                }
            } catch (NumberFormatException e) {
                throw new ParsingException(element.getAttribute("id"), "Invalid token in points list", e);
            }
        }

        if (!reasonToBePath) {
            return null;
        }

        return optPath.orElse(null);
    }

    /**
     * @see org.newdawn.slick.svg.inkscape.ElementProcessor#process(org.newdawn.slick.svg.Loader, org.w3c.dom.Element, org.newdawn.slick.svg.Diagram, org.newdawn.slick.geom.Transform)
     */
    @Override
    public void process(Loader loader, Element element, Diagram diagram, Transform t) {
        Transform transform = Util.getTransform(element);
        transform = new Transform(t, transform);

        String points = element.getAttribute("points");
        if (element.getNodeName().equals("path")) {
            points = element.getAttribute("d");
        }

        StringTokenizer tokens = new StringTokenizer(points, ", ");
        Path path = processPoly(element, tokens);
        NonGeometricData data = Util.getNonGeometricData(element);
        if (path != null) {
            Shape shape = path.transform(transform);

            diagram.addFigure(new Figure(Figure.PATH, shape, data, transform));
        }
    }

    /**
     * @see org.newdawn.slick.svg.inkscape.ElementProcessor#handles(org.w3c.dom.Element)
     */
    @Override
    public boolean handles(Element element) {
        return element.getNodeName().equals("path") && !"arc".equals(element.getAttributeNS(Util.SODIPODI, "type"));
    }

}
