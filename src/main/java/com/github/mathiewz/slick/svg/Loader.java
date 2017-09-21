package com.github.mathiewz.slick.svg;

import org.w3c.dom.Element;

import com.github.mathiewz.slick.geom.Transform;

/**
 * Description of a simple XML loader
 *
 * @author kevin
 */
public interface Loader {
    /**
     * Load the children of a given element
     *
     * @param element
     *            The element whose children should be loaded
     * @param t
     *            The transform to apply to all the children
     */
    public void loadChildren(Element element, Transform t);
}
