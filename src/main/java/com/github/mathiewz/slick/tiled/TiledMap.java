package com.github.mathiewz.slick.tiled;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.github.mathiewz.slick.Image;
import com.github.mathiewz.slick.SlickException;
import com.github.mathiewz.slick.util.Log;
import com.github.mathiewz.slick.util.ResourceLoader;

/**
 * This class is intended to parse TilED maps. TilED is a generic tool for tile
 * map editing and can be found at:
 *
 * http://mapeditor.org/
 *
 * @author kevin
 * @author Tiago Costa
 * @author Loads of others!
 */
public class TiledMap {
    /** Indicates if we're running on a headless system */
    private static boolean headless;

    /** The width of the map */
    protected int width;
    /** The height of the map */
    protected int height;
    /** The width of the tiles used on the map */
    protected int tileWidth;
    /** The height of the tiles used on the map */
    protected int tileHeight;

    /** The location prefix where we can find tileset images */
    protected String tilesLocation;

    /** the properties of the map */
    protected Properties props;

    /** The list of tilesets defined in the map */
    protected ArrayList<TileSet> tileSets = new ArrayList<>();
    /** The list of layers defined in the map */
    protected ArrayList<Layer> layers = new ArrayList<>();
    /** The list of object-groups defined in the map */
    protected ArrayList<ObjectGroup> objectGroups = new ArrayList<>();
    
    /** The orientation of this map */
    protected OrientationEnum orientation;

    /** True if we want to load tilesets - including their image data */
    private boolean loadTileSets = true;

    private enum OrientationEnum {
        ORTHOGONAL,
        ISOMETRIC;
    }

    /**
     * Create a new tile map based on a given TMX file
     *
     * @param ref
     *            The location of the tile map to load
     */
    public TiledMap(String ref) {
        this(ref, true);
    }

    /**
     * Create a new tile map based on a given TMX file
     *
     * @param ref
     *            The location of the tile map to load
     * @param loadTileSets
     *            True if we want to load tilesets - including their image data
     */
    public TiledMap(String refArg, boolean loadTileSets) {
        this.loadTileSets = loadTileSets;
        String ref = refArg.replace('\\', '/');
        load(ResourceLoader.getResourceAsStream(ref), ref.substring(0, ref.lastIndexOf('/')));
    }

    /**
     * Create a new tile map based on a given TMX file
     *
     * @param ref
     *            The location of the tile map to load
     * @param tileSetsLocation
     *            The location where we can find the tileset images and other
     *            resources
     */
    public TiledMap(String ref, String tileSetsLocation) {
        this(ResourceLoader.getResourceAsStream(ref), tileSetsLocation);
    }

    /**
     * Load a tile map from an arbitary input stream
     *
     * @param in
     *            The input stream to load from
     */
    public TiledMap(InputStream in) {
        this(in, StringUtils.EMPTY);
    }

    /**
     * Load a tile map from an arbitary input stream
     *
     * @param in
     *            The input stream to load from
     * @param tileSetsLocation
     *            The location at which we can find tileset images
     */
    public TiledMap(InputStream in, String tileSetsLocation) {
        load(in, tileSetsLocation);
    }

    /**
     * Get the location of the tile images specified
     *
     * @return The location of the tile images specified as a resource reference
     *         prefix
     */
    public String getTilesLocation() {
        return tilesLocation;
    }

    /**
     * Get the index of the layer with given name
     *
     * @param name
     *            The name of the tile to search for
     * @return The index of the layer or -1 if there is no layer with given name
     */
    public int getLayerIndex(String name) {
        for (int i = 0; i < layers.size(); i++) {
            Layer layer = layers.get(i);
            if (layer.getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Gets the Image used to draw the tile at the given x and y coordinates.
     *
     * @param x
     *            The x coordinate of the tile whose image should be retrieved
     * @param y
     *            The y coordinate of the tile whose image should be retrieved
     * @param layerIndex
     *            The index of the layer on which the tile whose image should be
     *            retrieve exists
     * @return The image used to draw the specified tile or null if there is no
     *         image for the specified tile.
     */
    public Image getTileImage(int x, int y, int layerIndex) {
        Layer layer = layers.get(layerIndex);
        int tileSetIndex = layer.data[x][y][0];
        if (tileSetIndex >= 0 && tileSetIndex < tileSets.size()) {
            TileSet tileSet = tileSets.get(tileSetIndex);
            int tile = layer.data[x][y][1];
            int sheetX = tileSet.getTileX(tile);
            int sheetY = tileSet.getTileY(tile);
            return tileSet.tiles.getSprite(sheetX, sheetY);
        }
        return null;
    }

    /**
     * Get the width of the map
     *
     * @return The width of the map (in tiles)
     */
    public int getWidth() {
        return width;
    }

    /**
     * Get the height of the map
     *
     * @return The height of the map (in tiles)
     */
    public int getHeight() {
        return height;
    }

    /**
     * Get the height of a single tile
     *
     * @return The height of a single tile (in pixels)
     */
    public int getTileHeight() {
        return tileHeight;
    }

    /**
     * Get the width of a single tile
     *
     * @return The height of a single tile (in pixels)
     */
    public int getTileWidth() {
        return tileWidth;
    }

    /**
     * Get the global ID of a tile at specified location in the map
     *
     * @param x
     *            The x location of the tile
     * @param y
     *            The y location of the tile
     * @param layerIndex
     *            The index of the layer to retireve the tile from
     * @return The global ID of the tile
     */
    public int getTileId(int x, int y, int layerIndex) {
        return layers.get(layerIndex).getTileID(x, y);
    }

    /**
     * Set the global ID of a tile at specified location in the map
     *
     * @param x
     *            The x location of the tile
     * @param y
     *            The y location of the tile
     * @param layerIndex
     *            The index of the layer to set the new tileid
     * @param tileid
     *            The tileid to be set
     */
    public void setTileId(int x, int y, int layerIndex, int tileid) {
        layers.get(layerIndex).setTileID(x, y, tileid);
    }

    /**
     * Get a property given to the map. Note that this method will not perform
     * well and should not be used as part of the default code path in the game
     * loop.
     *
     * @param propertyName
     *            The name of the property of the map to retrieve
     * @param def
     *            The default value to return
     * @return The value assigned to the property on the map (or the default
     *         value if none is supplied)
     */
    public String getMapProperty(String propertyName, String def) {
        return props == null ? def : props.getProperty(propertyName, def);
    }

    /**
     * Get a property given to a particular layer. Note that this method will
     * not perform well and should not be used as part of the default code path
     * in the game loop.
     *
     * @param layerIndex
     *            The index of the layer to retrieve
     * @param propertyName
     *            The name of the property of this layer to retrieve
     * @param def
     *            The default value to return
     * @return The value assigned to the property on the layer (or the default
     *         value if none is supplied)
     */
    public String getLayerProperty(int layerIndex, String propertyName, String def) {
        Layer layer = layers.get(layerIndex);
        return layer == null || layer.props == null ? def : layer.props.getProperty(propertyName, def);
    }

    /**
     * Get a propety given to a particular tile. Note that this method will not
     * perform well and should not be used as part of the default code path in
     * the game loop.
     *
     * @param tileID
     *            The global ID of the tile to retrieve
     * @param propertyName
     *            The name of the property to retireve
     * @param def
     *            The default value to return
     * @return The value assigned to the property on the tile (or the default
     *         value if none is supplied)
     */
    public String getTileProperty(int tileID, String propertyName, String def) {
        if (tileID == 0) {
            return def;
        }

        TileSet set = findTileSet(tileID);

        Properties properties = set.getProperties(tileID);
        return properties == null ? def : properties.getProperty(propertyName, def);
    }

    /**
     * Render the whole tile map at a given location
     *
     * @param x
     *            The x location to render at
     * @param y
     *            The y location to render at
     */
    public void render(int x, int y) {
        render(x, y, 0, 0, width, height, false);
    }

    /**
     * Render a single layer from the map
     *
     * @param x
     *            The x location to render at
     * @param y
     *            The y location to render at
     * @param layer
     *            The layer to render
     */
    public void render(int x, int y, int layer) {
        render(x, y, 0, 0, width, height, layer, false);
    }

    /**
     * Render a section of the tile map
     *
     * @param x
     *            The x location to render at
     * @param y
     *            The y location to render at
     * @param sx
     *            The x tile location to start rendering
     * @param sy
     *            The y tile location to start rendering
     * @param width
     *            The width of the section to render (in tiles)
     * @param height
     *            The height of the secton to render (in tiles)
     */
    public void render(int x, int y, int sx, int sy, int width, int height) {
        render(x, y, sx, sy, width, height, false);
    }

    /**
     * Render a section of the tile map
     *
     * @param x
     *            The x location to render at
     * @param y
     *            The y location to render at
     * @param sx
     *            The x tile location to start rendering
     * @param sy
     *            The y tile location to start rendering
     * @param width
     *            The width of the section to render (in tiles)
     * @param height
     *            The height of the secton to render (in tiles)
     * @param l
     *            The index of the layer to render
     * @param lineByLine
     *            True if we should render line by line, i.e. giving us a chance
     *            to render something else between lines (@see
     *            {@link #renderedLine(int, int, int)}
     */
    public void render(int x, int y, int sx, int sy, int width, int height, int l, boolean lineByLine) {
        Layer layer = layers.get(l);
        if (orientation == OrientationEnum.ORTHOGONAL) {
            for (int ty = 0; ty < height; ty++) {
                layer.render(x, y, sx, sy, width, ty, lineByLine, tileWidth, tileHeight);
            }
        } else {
            renderIsometricMap(x, y, width, height, layer, lineByLine);
        }
    }

    /**
     * Render a section of the tile map
     *
     * @param x
     *            The x location to render at
     * @param y
     *            The y location to render at
     * @param sx
     *            The x tile location to start rendering
     * @param sy
     *            The y tile location to start rendering
     * @param width
     *            The width of the section to render (in tiles)
     * @param height
     *            The height of the secton to render (in tiles)
     * @param lineByLine
     *            True if we should render line by line, i.e. giving us a chance
     *            to render something else between lines (@see
     *            {@link #renderedLine(int, int, int)}
     */
    public void render(int x, int y, int sx, int sy, int width, int height, boolean lineByLine) {
        if (orientation == OrientationEnum.ORTHOGONAL) {
            for (int ty = 0; ty < height; ty++) {
                for (int i = 0; i < layers.size(); i++) {
                    Layer layer = layers.get(i);
                    layer.render(x, y, sx, sy, width, ty, lineByLine, tileWidth, tileHeight);
                }
            }
        } else {
            renderIsometricMap(x, y, width, height, null, lineByLine);
        }
    }

    /**
     * Render of isometric map renders.
     *
     * @param x
     *            The x location to render at
     * @param y
     *            The y location to render at
     * @param sx
     *            The x tile location to start rendering
     * @param sy
     *            The y tile location to start rendering
     * @param width
     *            The width of the section to render (in tiles)
     * @param height
     *            The height of the section to render (in tiles)
     * @param layer
     *            if this is null all layers are rendered, if not only the
     *            selected layer is renderered
     * @param lineByLine
     *            True if we should render line by line, i.e. giving us a chance
     *            to render something else between lines (@see
     *            {@link #renderedLine(int, int, int)}
     *
     */
    protected void renderIsometricMap(int x, int y, int width, int height, Layer layer, boolean lineByLine) {
        ArrayList<Layer> drawLayers = layers;
        if (layer != null) {
            drawLayers = new ArrayList<>();
            drawLayers.add(layer);
        }

        int maxCount = width * height;
        int allCount = 0;

        int initialLineX = x;
        int initialLineY = y;

        int startLineTileX = 0;
        int startLineTileY = 0;
        while (allCount < maxCount) {

            int currentTileX = startLineTileX;
            int currentTileY = startLineTileY;
            int currentLineX = initialLineX;

            int min = getMin(width, height, startLineTileY, currentTileX);

            for (int burner = 0; burner <= min; currentTileX++, currentTileY--, burner++) {
                for (Layer currentLayer : drawLayers) {
                    currentLayer.render(currentLineX, initialLineY, currentTileX, currentTileY, 1, 0, lineByLine, tileWidth, tileHeight);
                }
                currentLineX += tileWidth;
                allCount++;
            }

            int modifier = startLineTileY < height - 1 ? -1 : 1;
            startLineTileY += 1;
            initialLineX += modifier * tileWidth / 2;
            initialLineY += tileHeight / 2;
        }
    }

    private int getMin(int width, int height, int startLineTileY, int currentTileX) {
        int value = height > width ? width : height;
        if (startLineTileY < value - 1) {
            return startLineTileY;
        } else if (width - currentTileX < height) {
            return width - currentTileX - 1;
        }
        return value - 1;
    }

    /**
     * Retrieve a count of the number of layers available
     *
     * @return The number of layers available in this map
     */
    public int getLayerCount() {
        return layers.size();
    }

    /**
     * Save parser for strings to ints
     *
     * @param value
     *            The string to parse
     * @return The integer to parse or zero if the string isn't an int
     */
    private int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Load a TilED map
     *
     * @param in
     *            The input stream from which to load the map
     * @param tileSetsLocation
     *            The location from which we can retrieve tileset images
     */
    private void load(InputStream in, String tileSetsLocation) {
        tilesLocation = tileSetsLocation;

        try {
            Element docElement = getDocElement(in);

            orientation = docElement.getAttribute("orientation").equals("orthogonal") ? OrientationEnum.ORTHOGONAL : OrientationEnum.ISOMETRIC;

            width = parseInt(docElement.getAttribute("width"));
            height = parseInt(docElement.getAttribute("height"));
            tileWidth = parseInt(docElement.getAttribute("tilewidth"));
            tileHeight = parseInt(docElement.getAttribute("tileheight"));

            loadProperties(docElement);
            loadTileSet(docElement);
            loadLayer(docElement);
            loadObjectGroups(docElement);
        } catch (Exception e) {
            Log.error(e);
            throw new SlickException("Failed to parse tilemap", e);
        }
    }

    private void loadObjectGroups(Element docElement) {
        NodeList objectGroupNodes = docElement.getElementsByTagName("objectgroup");
        for (int i = 0; i < objectGroupNodes.getLength(); i++) {
            Element current = (Element) objectGroupNodes.item(i);
            ObjectGroup objectGroup = new ObjectGroup(current);
            objectGroups.add(objectGroup);
        }
    }

    private Element getDocElement(InputStream in) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        builder.setEntityResolver((publicId, systemId) -> new InputSource(new ByteArrayInputStream(new byte[0])));
        Document doc = builder.parse(in);
        return doc.getDocumentElement();
    }
    
    private void loadLayer(Element docElement) {
        NodeList layerNodes = docElement.getElementsByTagName("layer");
        for (int i = 0; i < layerNodes.getLength(); i++) {
            Layer layer = new Layer(this, (Element) layerNodes.item(i));
            layer.index = i;
            layers.add(layer);
        }
    }
    
    private void loadProperties(Element docElement) {
        Element propsElement = (Element) docElement.getElementsByTagName("properties").item(0);
        if (propsElement == null) {
            return;
        }
        NodeList properties = propsElement.getElementsByTagName("property");
        if (properties == null) {
            return;
        }
        props = new Properties();
        for (int p = 0; p < properties.getLength(); p++) {
            Element propElement = (Element) properties.item(p);

            String name = propElement.getAttribute("name");
            String value = propElement.getAttribute("value");
            props.setProperty(name, value);
        }
    }
    
    private void loadTileSet(Element docElement) {
        if (!loadTileSets) {
            return;
        }
        TileSet lastSet = null;

        NodeList setNodes = docElement.getElementsByTagName("tileset");
        for (int i = 0; i < setNodes.getLength(); i++) {
            Element current = (Element) setNodes.item(i);

            TileSet tileSet = new TileSet(this, current, !headless);
            tileSet.index = i;

            if (lastSet != null) {
                lastSet.setLimit(tileSet.firstGID - 1);
            }
            lastSet = tileSet;
            tileSets.add(tileSet);
        }
    }

    /**
     * Retrieve the number of tilesets available in this map
     *
     * @return The number of tilesets available in this map
     */
    public int getTileSetCount() {
        return tileSets.size();
    }

    /**
     * Get a tileset at a particular index in the list of sets for this map
     *
     * @param index
     *            The index of the tileset.
     * @return The TileSet requested
     */
    public TileSet getTileSet(int index) {
        return tileSets.get(index);
    }

    /**
     * Find a tile for a given global tile id
     *
     * @param gid
     *            The global tile id we're looking for
     * @return The tileset in which that tile lives or null if the gid is not
     *         defined
     */
    public TileSet findTileSet(int gid) {
        return tileSets.stream()
                .filter(set -> set.contains(gid))
                .findFirst().orElse(null);
    }

    /**
     * Overrideable to allow other sprites to be rendered between lines of the
     * map
     *
     * @param visualY
     *            The visual Y coordinate, i.e. 0 to height
     * @param mapY
     *            The map Y coordinate, i.e. y to y+height
     * @param layer
     *            The layer being rendered
     */
    protected void renderedLine(int visualY, int mapY, int layer) {
        // Nothing to do here
    }

    /**
     * Returns the number of object-groups defined in the map.
     *
     * @return Number of object-groups on the map
     */
    public int getObjectGroupCount() {
        return objectGroups.size();
    }

    /**
     * Returns the number of objects of a specific object-group.
     *
     * @param groupID
     *            The index of this object-group
     * @return Number of the objects in the object-group or -1, when error
     *         occurred.
     */
    public int getObjectCount(int groupID) {
        return groupID >= 0 && groupID < objectGroups.size() ? objectGroups.get(groupID).objects.size() : -1;
    }

    /**
     * Return the name of a specific object from a specific group.
     *
     * @param groupID
     *            Index of a group
     * @param objectID
     *            Index of an object
     * @return The name of an object or null, when error occurred
     */
    public String getObjectName(int groupID, int objectID) {
        return getObjectAttribute(groupID, objectID, object -> object.name, null);
    }

    /**
     * Return the type of an specific object from a specific group.
     *
     * @param groupID
     *            Index of a group
     * @param objectID
     *            Index of an object
     * @return The type of an object or null, when error occurred
     */
    public String getObjectType(int groupID, int objectID) {
        return getObjectAttribute(groupID, objectID, object -> object.type, null);
    }

    /**
     * Returns the x-coordinate of a specific object from a specific group.
     *
     * @param groupID
     *            Index of a group
     * @param objectID
     *            Index of an object
     * @return The x-coordinate of an object, or -1, when error occurred
     */
    public int getObjectX(int groupID, int objectID) {
        return getObjectAttribute(groupID, objectID, object -> object.x, -1);
    }

    /**
     * Returns the y-coordinate of a specific object from a specific group.
     *
     * @param groupID
     *            Index of a group
     * @param objectID
     *            Index of an object
     * @return The y-coordinate of an object, or -1, when error occurred
     */
    public int getObjectY(int groupID, int objectID) {
        return getObjectAttribute(groupID, objectID, object -> object.y, -1);
    }

    /**
     * Returns the width of a specific object from a specific group.
     *
     * @param groupID
     *            Index of a group
     * @param objectID
     *            Index of an object
     * @return The width of an object, or -1, when error occurred
     */
    public int getObjectWidth(int groupID, int objectID) {
        return getObjectAttribute(groupID, objectID, object -> object.width, -1);
    }

    /**
     * Returns the height of a specific object from a specific group.
     *
     * @param groupID
     *            Index of a group
     * @param objectID
     *            Index of an object
     * @return The height of an object, or -1, when error occurred
     */
    public int getObjectHeight(int groupID, int objectID) {
        return getObjectAttribute(groupID, objectID, object -> object.height, -1);
    }

    private <T> T getObjectAttribute(int groupID, int objectID, Function<GroupObject, T> getter, T def) {
        if (groupID >= 0 && groupID < objectGroups.size()) {
            ObjectGroup grp = objectGroups.get(groupID);
            if (objectID >= 0 && objectID < grp.objects.size()) {
                GroupObject object = grp.objects.get(objectID);
                return getter.apply(object);
            }
        }
        return def;
    }

    /**
     * Retrieve the image source property for a given object
     *
     * @param groupID
     *            Index of a group
     * @param objectID
     *            Index of an object
     * @return The image source reference or null if one isn't defined
     */
    public String getObjectImage(int groupID, int objectID) {
        return getObjectAttribute(groupID, objectID, object -> object.image, null);
    }

    /**
     * Looks for a property with the given name and returns it's value. If no
     * property is found, def is returned.
     *
     * @param groupID
     *            Index of a group
     * @param objectID
     *            Index of an object
     * @param propertyName
     *            Name of a property
     * @param def
     *            default value to return, if no property is found
     * @return The value of the property with the given name or def, if there is
     *         no property with that name.
     */
    public String getObjectProperty(int groupID, int objectID, String propertyName, String def) {
        return getObjectAttribute(groupID, objectID, object -> object.props.getProperty(propertyName, def), def);
    }

    /**
     * A group of objects on the map (objects layer)
     *
     * @author kulpae
     */
    protected class ObjectGroup {
        /** The Objects of this group */
        private List<GroupObject> objects;
        /** the properties of this group */
        private Properties props;

        /**
         * Create a new group based on the XML definition
         *
         * @param element
         *            The XML element describing the layer
         */
        public ObjectGroup(Element element) {
            objects = new ArrayList<>();

            // now read the layer properties
            Element propsElement = (Element) element.getElementsByTagName("properties").item(0);
            if (propsElement != null) {
                NodeList properties = propsElement.getElementsByTagName("property");
                if (properties != null) {
                    props = new Properties();
                    for (int p = 0; p < properties.getLength(); p++) {
                        Element propElement = (Element) properties.item(p);

                        String name = propElement.getAttribute("name");
                        String value = propElement.getAttribute("value");
                        props.setProperty(name, value);
                    }
                }
            }

            NodeList objectNodes = element.getElementsByTagName("object");
            for (int i = 0; i < objectNodes.getLength(); i++) {
                Element objElement = (Element) objectNodes.item(i);
                GroupObject object = new GroupObject(objElement);
                objects.add(object);
            }
        }
    }

    /**
     * An object from a object-group on the map
     *
     * @author kulpae
     */
    protected class GroupObject {
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
         * Create a new group based on the XML definition
         *
         * @param element
         *            The XML element describing the layer
         */
        public GroupObject(Element element) {
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
    }

}
