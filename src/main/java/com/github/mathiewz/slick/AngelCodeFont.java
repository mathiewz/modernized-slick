package com.github.mathiewz.slick;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicInteger;

import org.lwjgl.opengl.GL11;

import com.github.mathiewz.slick.geom.Point;
import com.github.mathiewz.slick.opengl.renderer.Renderer;
import com.github.mathiewz.slick.opengl.renderer.SGL;
import com.github.mathiewz.slick.util.ResourceLoader;

/**
 * A font implementation that will parse BMFont format font files. The font files can be output
 * by Hiero, which is included with Slick, and also the AngelCode font tool available at:
 *
 * <a
 * href="http://www.angelcode.com/products/bmfont/">http://www.angelcode.com/products/bmfont/</a>
 *
 * This implementation copes with both the font display and kerning information
 * allowing nicer looking paragraphs of text. Note that this utility only
 * supports the text BMFont format definition file.
 *
 * @author kevin
 * @author Nathan Sweet
 */
public class AngelCodeFont implements Font {
    /** The renderer to use for all GL operations */
    private static final SGL GL = Renderer.get();

    /**
     * The line cache size, this is how many lines we can render before starting
     * to regenerate lists
     */
    private static final int DISPLAY_LIST_CACHE_SIZE = 200;

    /** The highest character that AngelCodeFont will support. */
    private static final int MAX_CHAR = 255;

    /** True if this font should use display list caching */
    private boolean displayListCaching = true;

    /** The image containing the bitmap font */
    private final Image fontImage;
    /** The characters building up the font */
    private CharDef[] chars;
    /** The height of a line */
    private int lineHeight;
    /** The first display list ID */
    private int baseDisplayListID = -1;
    /** The eldest display list ID */
    private int eldestDisplayListID;
    /** The eldest display list */
    private DisplayList eldestDisplayList;

    /** The display list cache for rendered lines */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private final LinkedHashMap<String, DisplayList> displayLists = new LinkedHashMap(DISPLAY_LIST_CACHE_SIZE, 1, true) {
        @Override
        protected boolean removeEldestEntry(Entry eldest) {
            eldestDisplayList = (DisplayList) eldest.getValue();
            eldestDisplayListID = eldestDisplayList.id;
            
            return false;
        }
    };

    /**
     * Create a new font based on a font definition from AngelCode's tool and
     * the font image generated from the tool.
     *
     * @param fntFile
     *            The location of the font defnition file
     * @param image
     *            The image to use for the font
     */
    public AngelCodeFont(String fntFile, Image image) {
        fontImage = image;

        parseFnt(ResourceLoader.getResourceAsStream(fntFile));
    }

    /**
     * Create a new font based on a font definition from AngelCode's tool and
     * the font image generated from the tool.
     *
     * @param fntFile
     *            The location of the font defnition file
     * @param imgFile
     *            The location of the font image
     */
    public AngelCodeFont(String fntFile, String imgFile) {
        fontImage = new Image(imgFile);

        parseFnt(ResourceLoader.getResourceAsStream(fntFile));
    }

    /**
     * Create a new font based on a font definition from AngelCode's tool and
     * the font image generated from the tool.
     *
     * @param fntFile
     *            The location of the font defnition file
     * @param image
     *            The image to use for the font
     * @param caching
     *            True if this font should use display list caching
     */
    public AngelCodeFont(String fntFile, Image image, boolean caching) {
        fontImage = image;
        displayListCaching = caching;
        parseFnt(ResourceLoader.getResourceAsStream(fntFile));
    }

    /**
     * Create a new font based on a font definition from AngelCode's tool and
     * the font image generated from the tool.
     *
     * @param fntFile
     *            The location of the font defnition file
     * @param imgFile
     *            The location of the font image
     * @param caching
     *            True if this font should use display list caching
     */
    public AngelCodeFont(String fntFile, String imgFile, boolean caching) {
        fontImage = new Image(imgFile);
        displayListCaching = caching;
        parseFnt(ResourceLoader.getResourceAsStream(fntFile));
    }

    /**
     * Create a new font based on a font definition from AngelCode's tool and
     * the font image generated from the tool.
     *
     * @param name
     *            The name to assign to the font image in the image store
     * @param fntFile
     *            The stream of the font defnition file
     * @param imgFile
     *            The stream of the font image
     */
    public AngelCodeFont(String name, InputStream fntFile, InputStream imgFile) {
        fontImage = new Image(imgFile, name, false);

        parseFnt(fntFile);
    }

    /**
     * Create a new font based on a font definition from AngelCode's tool and
     * the font image generated from the tool.
     *
     * @param name
     *            The name to assign to the font image in the image store
     * @param fntFile
     *            The stream of the font defnition file
     * @param imgFile
     *            The stream of the font image
     * @param caching
     *            True if this font should use display list caching
     */
    public AngelCodeFont(String name, InputStream fntFile, InputStream imgFile, boolean caching) {
        fontImage = new Image(imgFile, name, false);

        displayListCaching = caching;
        parseFnt(fntFile);
    }

    /**
     * Parse the font definition file
     *
     * @param fntFile
     *            The stream from which the font file can be read
     */
    private void parseFnt(InputStream fntFile) {
        displayCaching();
        try {
            // now parse the font file
            BufferedReader in = new BufferedReader(new InputStreamReader(fntFile));
            Map<Short, List<Short>> kerning = new HashMap<>(64);
            List<CharDef> charDefs = new ArrayList<>(MAX_CHAR);
            int maxChar = 0;
            while (true) {
                String line = in.readLine();
                if (line == null) {
                    break;
                } else {
                    if (line.startsWith("char") && !line.startsWith("chars c")) {
                        CharDef def = parseChar(line);
                        if (def != null) {
                            maxChar = Math.max(maxChar, def.id);
                            charDefs.add(def);
                        }
                    } else if (line.startsWith("kerning") && !line.startsWith("kernings c")) {
                        kerningTreatment(kerning, line);
                    }
                }
            }
            
            chars = new CharDef[maxChar + 1];
            charDefs.forEach(def -> chars[def.id] = def);
            kerningToChardef(kerning);
        } catch (IOException e) {
            throw new SlickException("Failed to parse font file: " + fntFile, e);
        }
    }
    
    private void kerningToChardef(Map<Short, List<Short>> kerning) {
        for (Entry<Short, List<Short>> entry : kerning.entrySet()) {
            short first = entry.getKey().shortValue();
            List<Short> valueList = entry.getValue();
            short[] valueArray = new short[valueList.size()];
            int i = 0;
            for (Iterator<Short> valueIter = valueList.iterator(); valueIter.hasNext(); i++) {
                valueArray[i] = valueIter.next().shortValue();
            }
            chars[first].kerning = valueArray;
        }
    }

    private void displayCaching() {
        if (displayListCaching) {
            baseDisplayListID = GL.glGenLists(DISPLAY_LIST_CACHE_SIZE);
            if (baseDisplayListID == 0) {
                displayListCaching = false;
            }
        }
    }
    
    private void kerningTreatment(Map<Short, List<Short>> kerning, String line) {
        StringTokenizer tokens = new StringTokenizer(line, " =");
        tokens.nextToken(); // kerning
        tokens.nextToken(); // first
        short first = Short.parseShort(tokens.nextToken()); // first value
        tokens.nextToken(); // second
        int second = Integer.parseInt(tokens.nextToken()); // second value
        tokens.nextToken(); // offset
        int offset = Integer.parseInt(tokens.nextToken()); // offset value
        List<Short> values = kerning.get(first);
        if (values == null) {
            values = new ArrayList<>();
            kerning.put(first, values);
        }
        // Pack the character and kerning offset into a short.
        values.add((short) (offset << 8 | second));
    }

    /**
     * Parse a single character line from the definition
     *
     * @param line
     *            The line to be parsed
     * @return The character definition from the line
     */
    private CharDef parseChar(String line) {
        CharDef def = new CharDef();
        StringTokenizer tokens = new StringTokenizer(line, " =");

        tokens.nextToken(); // char
        tokens.nextToken(); // id
        def.id = Short.parseShort(tokens.nextToken()); // id value
        if (def.id < 0) {
            return null;
        }
        if (def.id > MAX_CHAR) {
            throw new SlickException("Invalid character '" + def.id + "': AngelCodeFont does not support characters above " + MAX_CHAR);
        }

        tokens.nextToken(); // x
        def.x = Short.parseShort(tokens.nextToken()); // x value
        tokens.nextToken(); // y
        def.y = Short.parseShort(tokens.nextToken()); // y value
        tokens.nextToken(); // width
        def.width = Short.parseShort(tokens.nextToken()); // width value
        tokens.nextToken(); // height
        def.height = Short.parseShort(tokens.nextToken()); // height value
        tokens.nextToken(); // x offset
        def.xoffset = Short.parseShort(tokens.nextToken()); // xoffset value
        tokens.nextToken(); // y offset
        def.yoffset = Short.parseShort(tokens.nextToken()); // yoffset value
        tokens.nextToken(); // xadvance
        def.xadvance = Short.parseShort(tokens.nextToken()); // xadvance

        def.init();

        if (def.id != ' ') {
            lineHeight = Math.max(def.height + def.yoffset, lineHeight);
        }

        return def;
    }

    /**
     * @see com.github.mathiewz.slick.Font#drawString(float, float, java.lang.String)
     */
    @Override
    public void drawString(float x, float y, String text) {
        drawString(x, y, text, Color.white);
    }

    /**
     * @see com.github.mathiewz.slick.Font#drawString(float, float, java.lang.String,
     *      com.github.mathiewz.slick.Color)
     */
    @Override
    public void drawString(float x, float y, String text, Color col) {
        drawString(x, y, text, col, 0, text.length() - 1);
    }

    /**
     * @see Font#drawString(float, float, String, Color, int, int)
     */
    @Override
    public void drawString(float x, float y, String text, Color col, int startIndex, int endIndex) {
        fontImage.bind();
        col.bind();

        GL.glTranslatef(x, y, 0);
        if (displayListCaching && startIndex == 0 && endIndex == text.length() - 1) {
            DisplayList displayList = displayLists.get(text);
            if (displayList != null) {
                GL.glCallList(displayList.id);
            } else {
                // Compile a new display list.
                displayList = new DisplayList();
                displayList.text = text;
                int displayListCount = displayLists.size();
                if (displayListCount < DISPLAY_LIST_CACHE_SIZE) {
                    displayList.id = baseDisplayListID + displayListCount;
                } else {
                    displayList.id = eldestDisplayListID;
                    displayLists.remove(eldestDisplayList.text);
                }

                displayLists.put(text, displayList);

                GL.glNewList(displayList.id, GL11.GL_COMPILE_AND_EXECUTE);
                render(text, startIndex, endIndex);
                GL.glEndList();
            }
        } else {
            render(text, startIndex, endIndex);
        }
        GL.glTranslatef(-x, -y, 0);
    }

    /**
     * Render based on immediate rendering
     *
     * @param text
     *            The text to be rendered
     * @param start
     *            The index of the first character in the string to render
     * @param end
     *            The index of the last character in the string to render
     */
    private void render(String text, int start, int end) {
        GL.glBegin(GL11.GL_QUADS);
        Point p = new Point(0, 0);
        CharDef lastCharDef = null;
        char[] data = text.toCharArray();
        for (int i = 0; i < data.length; i++) {
            lastCharDef = drawAndGetCharAt(start, end, p, lastCharDef, data, i);
        }
        GL.glEnd();
    }
    
    private CharDef drawAndGetCharAt(int start, int end, Point p, CharDef lastCharDef, char[] data, int i) {
        int id = data[i];
        if (id == '\n') {
            p.setX(0);
            p.addY(getLineHeight());
        } else if (id >= chars.length || chars[id] == null) {
            return lastCharDef;
        } else {
            if (lastCharDef != null) {
                p.addX(lastCharDef.getKerning(id));
            }
            CharDef charDef = chars[id];
            if (i >= start && i <= end) {
                charDef.draw(p.getX(), p.getY());
            }
            
            p.addX(charDef.xadvance);
            return charDef;
        }
        return lastCharDef;
    }

    /**
     * Returns the distance from the y drawing location to the top most pixel of the specified text.
     *
     * @param text
     *            The text that is to be tested
     * @return The yoffset from the y draw location at which text will start
     */
    public int getYOffset(String text) {
        DisplayList displayList = null;
        if (displayListCaching) {
            displayList = displayLists.get(text);
            if (displayList != null && displayList.yOffset != null) {
                return displayList.yOffset.intValue();
            }
        }

        int stopIndex = text.indexOf('\n');
        if (stopIndex == -1) {
            stopIndex = text.length();
        }

        int minYOffset = 10000;
        for (int i = 0; i < stopIndex; i++) {
            int id = text.charAt(i);
            CharDef charDef = chars[id];
            if (charDef == null) {
                continue;
            }
            minYOffset = Math.min(charDef.yoffset, minYOffset);
        }

        if (displayList != null) {
            displayList.yOffset = (short) minYOffset;
        }

        return minYOffset;
    }

    /**
     * @see com.github.mathiewz.slick.Font#getHeight(java.lang.String)
     */
    @Override
    public int getHeight(String text) {
        DisplayList displayList = null;
        if (displayListCaching) {
            displayList = displayLists.get(text);
            if (displayList != null && displayList.height != null) {
                return displayList.height.intValue();
            }
        }

        AtomicInteger lines = new AtomicInteger();
        int maxHeight = 0;
        for (int id : text.toCharArray()) {
            if (id == '\n') {
                lines.incrementAndGet();
                maxHeight = 0;
                continue;
            }
            if (id != ' ' && chars[id] != null) {
                maxHeight = Math.max(chars[id].height + chars[id].yoffset, maxHeight);
            }
        }

        maxHeight += lines.get() * getLineHeight();

        if (displayList != null) {
            displayList.height = (short) maxHeight;
        }

        return maxHeight;
    }

    /**
     * @see com.github.mathiewz.slick.Font#getWidth(java.lang.String)
     */
    @Override
    public int getWidth(String text) {
        DisplayList displayList = null;
        if (displayListCaching) {
            displayList = displayLists.get(text);
            if (displayList != null && displayList.width != null) {
                return displayList.width.intValue();
            }
        }

        int maxWidth = findWidth(text, false);

        if (displayList != null) {
            displayList.width = (short) maxWidth;
        }

        return maxWidth;
    }

    /**
     * @param text
     *            the string to find the width of
     * @param logical
     *            whether to add the space the letters should occupy on the end
     * @return width of string.
     */
    private int findWidth(String text, boolean logical) {
        int maxWidth = 0;
        int width = 0;
        CharDef lastCharDef = null;
        int n = text.length();
        for (int i = 0; i < n; i++) {
            int id = text.charAt(i);
            if (id == '\n') {
                width = 0;
                continue;
            }
            if (id < chars.length && chars[id] != null) {
                CharDef charDef = chars[id];
                if (lastCharDef != null) {
                    width += lastCharDef.getKerning(id);
                }
                lastCharDef = charDef;
                width += i < n - 1 || logical ? charDef.xadvance : charDef.width;
                maxWidth = Math.max(maxWidth, width);
            }

        }
        return maxWidth;
    }

    /**
     *
     * @see com.github.mathiewz.slick.Font#getLogicalWidth(String)
     */
    @Override
    public int getLogicalWidth(String text) {
        DisplayList displayList = null;
        if (displayListCaching) {
            displayList = displayLists.get(text);
            if (displayList != null && displayList.logicalWidth != null) {
                return displayList.logicalWidth.intValue();
            }
        }

        int maxWidth = findWidth(text, true);

        if (displayList != null) {
            displayList.logicalWidth = (short) maxWidth;
        }

        return maxWidth;
    }

    /**
     * The definition of a single character as defined in the AngelCode file
     * format
     *
     * @author kevin
     */
    private class CharDef {
        /** The id of the character */
        private short id;
        /** The x location on the sprite sheet */
        private short x;
        /** The y location on the sprite sheet */
        private short y;
        /** The width of the character image */
        private short width;
        /** The height of the character image */
        private short height;
        /** The amount the x position should be offset when drawing the image */
        private short xoffset;
        /** The amount the y position should be offset when drawing the image */
        private short yoffset;

        /** The amount to move the current position after drawing the character */
        private short xadvance;
        /** The image containing the character */
        private Image image;
        /** The kerning info for this character */
        private short[] kerning;

        /**
         * Initialise the image by cutting the right section from the map
         * produced by the AngelCode tool.
         */
        public void init() {
            image = fontImage.getSubImage(x, y, width, height);
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "[CharDef id=" + id + " x=" + x + " y=" + y + "]";
        }

        /**
         * Draw this character embedded in a image draw
         *
         * @param x
         *            The x position at which to draw the text
         * @param y
         *            The y position at which to draw the text
         */
        public void draw(float x, float y) {
            image.drawEmbedded(x + xoffset, y + yoffset, width, height);
        }

        /**
         * Get the kerning offset between this character and the specified character.
         *
         * @param otherCodePoint
         *            The other code point
         * @return the kerning offset
         */
        public int getKerning(int otherCodePoint) {
            if (kerning == null) {
                return 0;
            }
            int low = 0;
            int high = kerning.length - 1;
            while (low <= high) {
                int midIndex = low + high >>> 1;
                int value = kerning[midIndex];
                int foundCodePoint = value & 0xff;
                if (foundCodePoint < otherCodePoint) {
                    low = midIndex + 1;
                } else if (foundCodePoint > otherCodePoint) {
                    high = midIndex - 1;
                } else {
                    return value >> 8;
                }
            }
            return 0;
        }
    }

    /**
     * @see com.github.mathiewz.slick.Font#getLineHeight()
     */
    @Override
    public int getLineHeight() {
        return lineHeight;
    }

    /**
     * A descriptor for a single display list
     *
     * @author Nathan Sweet
     */
    private static class DisplayList {
        /** The if of the distance list */
        int id;
        /** The offset of the line rendered */
        Short yOffset;
        /** The width of the line rendered */
        Short width;
        /** The logical width of the line rendered */
        Short logicalWidth;
        /** The height of the line rendered */
        Short height;
        /** The text that the display list holds */
        String text;
    }
}
