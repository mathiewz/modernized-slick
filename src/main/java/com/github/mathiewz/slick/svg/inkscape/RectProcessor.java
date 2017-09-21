package com.github.mathiewz.slick.svg.inkscape;

import org.w3c.dom.Element;

import com.github.mathiewz.slick.geom.Rectangle;
import com.github.mathiewz.slick.geom.Shape;
import com.github.mathiewz.slick.geom.Transform;
import com.github.mathiewz.slick.svg.Diagram;
import com.github.mathiewz.slick.svg.Figure;
import com.github.mathiewz.slick.svg.Loader;
import com.github.mathiewz.slick.svg.NonGeometricData;

/**
 * A processor for the rect element.
 *
 * @author kevin
 */
public class RectProcessor implements ElementProcessor {
    
    /**
     * @see com.github.mathiewz.slick.svg.inkscape.ElementProcessor#process(com.github.mathiewz.slick.svg.Loader, org.w3c.dom.Element, com.github.mathiewz.slick.svg.Diagram, com.github.mathiewz.slick.geom.Transform)
     */
    @Override
    public void process(Loader loader, Element element, Diagram diagram, Transform t) {
        Transform transform = Util.getTransform(element);
        transform = new Transform(t, transform);
        
        float width = Float.parseFloat(element.getAttribute("width"));
        float height = Float.parseFloat(element.getAttribute("height"));
        float x = Float.parseFloat(element.getAttribute("x"));
        float y = Float.parseFloat(element.getAttribute("y"));
        
        Rectangle rect = new Rectangle(x, y, width + 1, height + 1);
        Shape shape = rect.transform(transform);
        
        NonGeometricData data = Util.getNonGeometricData(element);
        data.addAttribute("width", String.valueOf(width));
        data.addAttribute("height", String.valueOf(height));
        data.addAttribute("x", String.valueOf(x));
        data.addAttribute("y", String.valueOf(y));
        
        diagram.addFigure(new Figure(Figure.RECTANGLE, shape, data, transform));
    }
    
    /**
     * @see com.github.mathiewz.slick.svg.inkscape.ElementProcessor#handles(org.w3c.dom.Element)
     */
    @Override
    public boolean handles(Element element) {
        return element.getNodeName().equals("rect");
    }
}
