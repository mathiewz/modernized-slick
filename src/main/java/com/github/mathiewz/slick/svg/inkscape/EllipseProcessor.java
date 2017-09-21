package com.github.mathiewz.slick.svg.inkscape;

import org.w3c.dom.Element;

import com.github.mathiewz.slick.geom.Ellipse;
import com.github.mathiewz.slick.geom.Shape;
import com.github.mathiewz.slick.geom.Transform;
import com.github.mathiewz.slick.svg.Diagram;
import com.github.mathiewz.slick.svg.Figure;
import com.github.mathiewz.slick.svg.Loader;
import com.github.mathiewz.slick.svg.NonGeometricData;

/**
 * Processor for ellipse and path nodes marked as arcs
 *
 * @author kevin
 */
public class EllipseProcessor implements ElementProcessor {
    
    /**
     * @see com.github.mathiewz.slick.svg.inkscape.ElementProcessor#process(com.github.mathiewz.slick.svg.Loader, org.w3c.dom.Element, com.github.mathiewz.slick.svg.Diagram, com.github.mathiewz.slick.geom.Transform)
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
     * @see com.github.mathiewz.slick.svg.inkscape.ElementProcessor#handles(org.w3c.dom.Element)
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
