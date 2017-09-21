package com.github.mathiewz.slick.svg.inkscape;

import org.w3c.dom.Element;

import com.github.mathiewz.slick.geom.Shape;
import com.github.mathiewz.slick.geom.Transform;
import com.github.mathiewz.slick.svg.Diagram;
import com.github.mathiewz.slick.svg.Figure;
import com.github.mathiewz.slick.svg.Loader;
import com.github.mathiewz.slick.svg.NonGeometricData;
import com.github.mathiewz.slick.svg.ParsingException;

/**
 * Processor for the "use", a tag that allows references to other elements
 * and cloning.
 *
 * @author kevin
 */
public class UseProcessor implements ElementProcessor {
    
    /**
     * @see com.github.mathiewz.slick.svg.inkscape.ElementProcessor#handles(org.w3c.dom.Element)
     */
    @Override
    public boolean handles(Element element) {
        return element.getNodeName().equals("use");
    }
    
    /**
     * @see com.github.mathiewz.slick.svg.inkscape.ElementProcessor#process(com.github.mathiewz.slick.svg.Loader, org.w3c.dom.Element, com.github.mathiewz.slick.svg.Diagram, com.github.mathiewz.slick.geom.Transform)
     */
    @Override
    public void process(Loader loader, Element element, Diagram diagram, Transform transform) {
        
        String ref = element.getAttributeNS("http://www.w3.org/1999/xlink", "href");
        String href = Util.getAsReference(ref);
        
        Figure referenced = diagram.getFigureByID(href);
        if (referenced == null) {
            throw new ParsingException(element, "Unable to locate referenced element: " + href);
        }
        
        Transform local = Util.getTransform(element);
        Transform trans = local.concatenate(referenced.getTransform());
        
        NonGeometricData data = Util.getNonGeometricData(element);
        Shape shape = referenced.getShape().transform(trans);
        data.addAttribute(NonGeometricData.FILL, referenced.getData().getAttribute(NonGeometricData.FILL));
        data.addAttribute(NonGeometricData.STROKE, referenced.getData().getAttribute(NonGeometricData.STROKE));
        data.addAttribute(NonGeometricData.OPACITY, referenced.getData().getAttribute(NonGeometricData.OPACITY));
        data.addAttribute(NonGeometricData.STROKE_WIDTH, referenced.getData().getAttribute(NonGeometricData.STROKE_WIDTH));
        
        Figure figure = new Figure(referenced.getType(), shape, data, trans);
        diagram.addFigure(figure);
    }
    
}
