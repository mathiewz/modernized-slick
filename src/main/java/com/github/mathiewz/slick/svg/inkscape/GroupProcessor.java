package com.github.mathiewz.slick.svg.inkscape;

import org.w3c.dom.Element;

import com.github.mathiewz.slick.geom.Transform;
import com.github.mathiewz.slick.svg.Diagram;
import com.github.mathiewz.slick.svg.Loader;

/**
 * TODO: Document this class
 *
 * @author kevin
 */
public class GroupProcessor implements ElementProcessor {
    
    /**
     * @see com.github.mathiewz.slick.svg.inkscape.ElementProcessor#handles(org.w3c.dom.Element)
     */
    @Override
    public boolean handles(Element element) {
        if (element.getNodeName().equals("g")) {
            return true;
        }
        return false;
    }
    
    /**
     * O
     *
     * @see com.github.mathiewz.slick.svg.inkscape.ElementProcessor#process(com.github.mathiewz.slick.svg.Loader, org.w3c.dom.Element, com.github.mathiewz.slick.svg.Diagram, com.github.mathiewz.slick.geom.Transform)
     */
    @Override
    public void process(Loader loader, Element element, Diagram diagram, Transform t) {
        Transform transform = Util.getTransform(element);
        transform = new Transform(t, transform);
        
        loader.loadChildren(element, transform);
    }
    
}
