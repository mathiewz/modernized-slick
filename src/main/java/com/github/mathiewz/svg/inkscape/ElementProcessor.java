package com.github.mathiewz.svg.inkscape;

import org.w3c.dom.Element;

import com.github.mathiewz.geom.Transform;
import com.github.mathiewz.svg.Diagram;
import com.github.mathiewz.svg.Loader;

/**
 * The description of a module which processes a single XML element from a SVG (inkscape)
 * document.
 *
 * @author kevin
 */
public interface ElementProcessor {
    /**
     * Process a document extracting all the elements that the processor is
     * interested in and producing appropriate diagram components for the
     * element.
     *
     * @param loader
     *            The loader/context of the parsing
     * @param element
     *            The element to be processed
     * @param diagram
     *            The diagram to be built
     * @param transform
     *            The transform to apply to all elements at this level
     */
    public void process(Loader loader, Element element, Diagram diagram, Transform transform);

    /**
     * Check if this processor handles the element specified
     *
     * @param element
     *            The element to check
     * @return True if this processor can handle the given element
     */
    public boolean handles(Element element);
}
