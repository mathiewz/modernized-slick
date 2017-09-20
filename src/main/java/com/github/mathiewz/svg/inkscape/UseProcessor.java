package com.github.mathiewz.svg.inkscape;

import org.w3c.dom.Element;

import com.github.mathiewz.geom.Shape;
import com.github.mathiewz.geom.Transform;
import com.github.mathiewz.svg.Diagram;
import com.github.mathiewz.svg.Figure;
import com.github.mathiewz.svg.Loader;
import com.github.mathiewz.svg.NonGeometricData;
import com.github.mathiewz.svg.ParsingException;

/**
 * Processor for the "use", a tag that allows references to other elements
 * and cloning.
 *
 * @author kevin
 */
public class UseProcessor implements ElementProcessor {
    
    /**
     * @see com.github.mathiewz.svg.inkscape.ElementProcessor#handles(org.w3c.dom.Element)
     */
    @Override
    public boolean handles(Element element) {
        return element.getNodeName().equals("use");
    }
    
    /**
     * @see com.github.mathiewz.svg.inkscape.ElementProcessor#process(com.github.mathiewz.svg.Loader, org.w3c.dom.Element, com.github.mathiewz.svg.Diagram, com.github.mathiewz.geom.Transform)
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
