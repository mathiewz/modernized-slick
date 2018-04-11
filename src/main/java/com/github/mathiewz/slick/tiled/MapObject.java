package com.github.mathiewz.slick.tiled;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.Properties;

/**
 * An object from a object-group on the map
 *
 * @author kulpae
 */
public class MapObject {
    /** The name of this object - read from the XML */
    private String name;
    /** The type of this object - read from the XML */
    private String type;
    /** The x-coordinate of this object */
    private int x;
    /** The y-coordinate of this object */
    private int y;
    /** The width of this object */
    private int width;
    /** The height of this object */
    private int height;
    /** The image source */
    private String image;

    /** the properties of this group */
    private Properties props;

    /**
     * Create a new object based on the XML definition
     *
     * @param element
     *            The XML element describing the layer
     */
    public MapObject(Element element) {
        name = element.getAttribute("name");
        type = element.getAttribute("type");
        x = Integer.parseInt(element.getAttribute("x"));
        y = Integer.parseInt(element.getAttribute("y"));
        width = Integer.parseInt(element.getAttribute("width"));
        height = Integer.parseInt(element.getAttribute("height"));

        Element imageElement = (Element) element.getElementsByTagName("image").item(0);
        if (imageElement != null) {
            image = imageElement.getAttribute("source");
        }

        // now read the layer properties
        Element propsElement = (Element) element.getElementsByTagName("properties").item(0);
        if (propsElement != null) {
            NodeList properties = propsElement.getElementsByTagName("property");
            if (properties != null) {
                props = new Properties();
                for (int p = 0; p < properties.getLength(); p++) {
                    Element propElement = (Element) properties.item(p);

                    String nameAttr = propElement.getAttribute("name");
                    String value = propElement.getAttribute("value");
                    props.setProperty(nameAttr, value);
                }
            }
        }
    }

    public String getProperty(String key){
        return props.getProperty(key);
    }

    public String getProperty(String key, String def){
        String ret = getProperty(key);
        return ret == null ? def : ret;
    }

    public String getImage() {
        return image;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}