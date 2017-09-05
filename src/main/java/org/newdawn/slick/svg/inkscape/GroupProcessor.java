package org.newdawn.slick.svg.inkscape;

import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.svg.Diagram;
import org.newdawn.slick.svg.Loader;
import org.w3c.dom.Element;

/**
 * TODO: Document this class
 *
 * @author kevin
 */
public class GroupProcessor implements ElementProcessor {
    
    /**
     * @see org.newdawn.slick.svg.inkscape.ElementProcessor#handles(org.w3c.dom.Element)
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
     * @see org.newdawn.slick.svg.inkscape.ElementProcessor#process(org.newdawn.slick.svg.Loader, org.w3c.dom.Element, org.newdawn.slick.svg.Diagram, org.newdawn.slick.geom.Transform)
     */
    @Override
    public void process(Loader loader, Element element, Diagram diagram, Transform t) {
        Transform transform = Util.getTransform(element);
        transform = new Transform(t, transform);
        
        loader.loadChildren(element, transform);
    }
    
}
