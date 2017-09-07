package org.newdawn.slick.svg.inkscape;

import org.newdawn.slick.geom.Ellipse;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.svg.Diagram;
import org.newdawn.slick.svg.Figure;
import org.newdawn.slick.svg.Loader;
import org.newdawn.slick.svg.NonGeometricData;
import org.w3c.dom.Element;

/**
 * Processor for <ellipse> and <path> nodes marked as arcs
 *
 * @author kevin
 */
public class EllipseProcessor implements ElementProcessor {

    /**
     * @see org.newdawn.slick.svg.inkscape.ElementProcessor#process(org.newdawn.slick.svg.Loader, org.w3c.dom.Element, org.newdawn.slick.svg.Diagram, org.newdawn.slick.geom.Transform)
     */
    @Override
    public void process(Loader loader, Element element, Diagram diagram, Transform t) {
        Transform transform = Util.getTransform(element);
        transform = new Transform(t, transform);

        Float x = Util.getFloatAttribute(element, "cx");
        Float y = Util.getFloatAttribute(element, "cy");
        Float rx = Util.getFloatAttribute(element, "rx");
        Float ry = Util.getFloatAttribute(element, "ry");

        Ellipse ellipse = new Ellipse(x, y, rx, ry);
        Shape shape = ellipse.transform(transform);

        NonGeometricData data = Util.getNonGeometricData(element);
        data.addAttribute("cx", x.toString());
        data.addAttribute("cy", y.toString());
        data.addAttribute("rx", rx.toString());
        data.addAttribute("ry", ry.toString());

        diagram.addFigure(new Figure(Figure.ELLIPSE, shape, data, transform));
    }

    /**
     * @see org.newdawn.slick.svg.inkscape.ElementProcessor#handles(org.w3c.dom.Element)
     */
    @Override
    public boolean handles(Element element) {
        if (element.getNodeName().equals("ellipse")) {
            return true;
        }
        if (element.getNodeName().equals("path")) {
            if ("arc".equals(element.getAttributeNS(Util.SODIPODI, "type"))) {
                return true;
            }
        }

        return false;
    }

}
