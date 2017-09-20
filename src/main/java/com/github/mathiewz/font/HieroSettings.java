
package com.github.mathiewz.font;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import com.github.mathiewz.SlickException;
import com.github.mathiewz.UnicodeFont;
import com.github.mathiewz.font.effects.ConfigurableEffect;
import com.github.mathiewz.font.effects.ConfigurableEffect.Value;
import com.github.mathiewz.util.ResourceLoader;

/**
 * Holds the settings needed to configure a UnicodeFont.
 *
 * @author Nathan Sweet 
 */
public class HieroSettings {
    /** The size of the font to be generated */
    private int fontSize = 12;
    /** True if the font is rendered bold */
    private boolean bold = false;
    /** True fi the font if rendered italic */
    private boolean italic = false;
    /** The padding applied in pixels to the top of the glyph rendered area */
    private int paddingTop;
    /** The padding applied in pixels to the left of the glyph rendered area */
    private int paddingLeft;
    /** The padding applied in pixels to the bottom of the glyph rendered area */
    private int paddingBottom;
    /** The padding applied in pixels to the right of the glyph rendered area */
    private int paddingRight;
    /** The padding applied in pixels to horizontal advance for each glyph */
    private int paddingAdvanceX;
    /** The padding applied in pixels to vertical advance for each glyph */
    private int paddingAdvanceY;
    /** The width of the glyph page generated */
    private int glyphPageWidth = 512;
    /** The height of the glyph page generated */
    private int glyphPageHeight = 512;
    /** The list of effects applied */
    private final List<ConfigurableEffect> effects = new ArrayList<>();
    
    /**
     * Default constructor for injection
     */
    public HieroSettings() {
    }
    
    /**
     * Create a new set of configuration from a file
     *
     * @param hieroFileRef
     *            The file system or classpath location of the Hiero settings file.
     */
    public HieroSettings(String hieroFileRef) {
        this(ResourceLoader.getResourceAsStream(hieroFileRef));
    }
    
    /**
     * Create a new set of configuration from a file
     *
     * @param in
     *            The stream from which to read the settings from
     */
    public HieroSettings(InputStream in) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                line = line.trim();
                if (line.length() == 0) {
                    continue;
                }
                String[] pieces = line.split("=", 2);
                String name = pieces[0].trim();
                String value = pieces[1];
                if (name.equals("font.size")) {
                    fontSize = Integer.parseInt(value);
                } else if (name.equals("font.bold")) {
                    bold = Boolean.valueOf(value).booleanValue();
                } else if (name.equals("font.italic")) {
                    italic = Boolean.valueOf(value).booleanValue();
                } else if (name.equals("pad.top")) {
                    paddingTop = Integer.parseInt(value);
                } else if (name.equals("pad.right")) {
                    paddingRight = Integer.parseInt(value);
                } else if (name.equals("pad.bottom")) {
                    paddingBottom = Integer.parseInt(value);
                } else if (name.equals("pad.left")) {
                    paddingLeft = Integer.parseInt(value);
                } else if (name.equals("pad.advance.x")) {
                    paddingAdvanceX = Integer.parseInt(value);
                } else if (name.equals("pad.advance.y")) {
                    paddingAdvanceY = Integer.parseInt(value);
                } else if (name.equals("glyph.page.width")) {
                    glyphPageWidth = Integer.parseInt(value);
                } else if (name.equals("glyph.page.height")) {
                    glyphPageHeight = Integer.parseInt(value);
                } else if (name.equals("effect.class")) {
                    try {
                        effects.add((ConfigurableEffect) Class.forName(value).newInstance());
                    } catch (Exception ex) {
                        throw new SlickException("Unable to create effect instance: " + value, ex);
                    }
                } else if (name.startsWith("effect.")) {
                    // Set an effect value on the last added effect.
                    name = name.substring(7);
                    ConfigurableEffect effect = effects.get(effects.size() - 1);
                    List<Value> values = effect.getValues();
                    for (Value value2 : values) {
                        Value effectValue = value2;
                        if (effectValue.getName().equals(name)) {
                            effectValue.setString(value);
                            break;
                        }
                    }
                    effect.setValues(values);
                }
            }
            reader.close();
        } catch (Exception ex) {
            throw new SlickException("Unable to load Hiero font file", ex);
        }
    }
    
    /**
     * @see UnicodeFont#getPaddingTop()
     *
     * @return The padding for the top of the glyph area in pixels
     */
    public int getPaddingTop() {
        return paddingTop;
    }
    
    /**
     * @see UnicodeFont#setPaddingTop(int)
     *
     * @param paddingTop
     *            The padding for the top of the glyph area in pixels
     */
    public void setPaddingTop(int paddingTop) {
        this.paddingTop = paddingTop;
    }
    
    /**
     * @see UnicodeFont#getPaddingLeft()
     *
     * @return The padding for the left of the glyph area in pixels
     */
    public int getPaddingLeft() {
        return paddingLeft;
    }
    
    /**
     * @see UnicodeFont#setPaddingLeft(int)
     *
     * @param paddingLeft
     *            The padding for the left of the glyph area in pixels
     */
    public void setPaddingLeft(int paddingLeft) {
        this.paddingLeft = paddingLeft;
    }
    
    /**
     * @see UnicodeFont#getPaddingBottom()
     *
     * @return The padding for the bottom of the glyph area in pixels
     */
    public int getPaddingBottom() {
        return paddingBottom;
    }
    
    /**
     * @see UnicodeFont#setPaddingBottom(int)
     *
     * @param paddingBottom
     *            The padding for the bottom of the glyph area in pixels
     */
    public void setPaddingBottom(int paddingBottom) {
        this.paddingBottom = paddingBottom;
    }
    
    /**
     * @see UnicodeFont#getPaddingRight()
     *
     * @return The padding for the right of the glyph area in pixels
     */
    public int getPaddingRight() {
        return paddingRight;
    }
    
    /**
     * @see UnicodeFont#setPaddingRight(int)
     *
     * @param paddingRight
     *            The padding for the right of the glyph area in pixels
     */
    public void setPaddingRight(int paddingRight) {
        this.paddingRight = paddingRight;
    }
    
    /**
     * @see UnicodeFont#getPaddingAdvanceX()
     *
     * @return The padding for the horizontal advance of each glyph
     */
    public int getPaddingAdvanceX() {
        return paddingAdvanceX;
    }
    
    /**
     * @see UnicodeFont#setPaddingAdvanceX(int)
     *
     * @param paddingAdvanceX
     *            The padding for the horizontal advance of each glyph
     */
    public void setPaddingAdvanceX(int paddingAdvanceX) {
        this.paddingAdvanceX = paddingAdvanceX;
    }
    
    /**
     * @see UnicodeFont#getPaddingAdvanceY()
     *
     * @return The padding for the vertical advance of each glyph
     */
    public int getPaddingAdvanceY() {
        return paddingAdvanceY;
    }
    
    /**
     * @see UnicodeFont#setPaddingAdvanceY(int)
     *
     * @param paddingAdvanceY
     *            The padding for the vertical advance of each glyph
     */
    public void setPaddingAdvanceY(int paddingAdvanceY) {
        this.paddingAdvanceY = paddingAdvanceY;
    }
    
    /**
     * @see UnicodeFont#getGlyphPageWidth()
     *
     * @return The width of the generate glyph pages
     */
    public int getGlyphPageWidth() {
        return glyphPageWidth;
    }
    
    /**
     * @see UnicodeFont#setGlyphPageWidth(int)
     *
     * @param glyphPageWidth
     *            The width of the generate glyph pages
     */
    public void setGlyphPageWidth(int glyphPageWidth) {
        this.glyphPageWidth = glyphPageWidth;
    }
    
    /**
     * @see UnicodeFont#getGlyphPageHeight()
     *
     * @return The height of the generate glyph pages
     */
    public int getGlyphPageHeight() {
        return glyphPageHeight;
    }
    
    /**
     * @see UnicodeFont#setGlyphPageHeight(int)
     *
     * @param glyphPageHeight
     *            The height of the generate glyph pages
     */
    public void setGlyphPageHeight(int glyphPageHeight) {
        this.glyphPageHeight = glyphPageHeight;
    }
    
    /**
     * @see UnicodeFont#UnicodeFont(String, int, boolean, boolean)
     * @see UnicodeFont#UnicodeFont(java.awt.Font, int, boolean, boolean)
     *
     * @return The point size of the font generated
     */
    public int getFontSize() {
        return fontSize;
    }
    
    /**
     * @see UnicodeFont#UnicodeFont(String, int, boolean, boolean)
     * @see UnicodeFont#UnicodeFont(java.awt.Font, int, boolean, boolean)
     *
     * @param fontSize
     *            The point size of the font generated
     */
    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }
    
    /**
     * @see UnicodeFont#UnicodeFont(String, int, boolean, boolean)
     * @see UnicodeFont#UnicodeFont(java.awt.Font, int, boolean, boolean)
     *
     * @return True if the font was generated in bold typeface
     */
    public boolean isBold() {
        return bold;
    }
    
    /**
     * @see UnicodeFont#UnicodeFont(String, int, boolean, boolean)
     * @see UnicodeFont#UnicodeFont(java.awt.Font, int, boolean, boolean)
     *
     * @param bold
     *            True if the font was generated in bold typeface
     */
    public void setBold(boolean bold) {
        this.bold = bold;
    }
    
    /**
     * @see UnicodeFont#UnicodeFont(String, int, boolean, boolean)
     * @see UnicodeFont#UnicodeFont(java.awt.Font, int, boolean, boolean)
     *
     * @return True if the font was generated in italic typeface
     */
    public boolean isItalic() {
        return italic;
    }
    
    /**
     * @see UnicodeFont#UnicodeFont(String, int, boolean, boolean)
     * @see UnicodeFont#UnicodeFont(java.awt.Font, int, boolean, boolean)
     *
     * @param italic
     *            True if the font was generated in italic typeface
     */
    public void setItalic(boolean italic) {
        this.italic = italic;
    }
    
    /**
     * @see UnicodeFont#getEffects()
     *
     * @return The list of effects applied to the text
     */
    public List<ConfigurableEffect> getEffects() {
        return effects;
    }
    
    /**
     * Saves the settings to a file.
     *
     * @param file
     *            The file we're saving to
     * @throws IOException
     *             if the file could not be saved.
     */
    public void save(File file) throws IOException {
        PrintStream out = new PrintStream(new FileOutputStream(file));
        out.println("font.size=" + fontSize);
        out.println("font.bold=" + bold);
        out.println("font.italic=" + italic);
        out.println();
        out.println("pad.top=" + paddingTop);
        out.println("pad.right=" + paddingRight);
        out.println("pad.bottom=" + paddingBottom);
        out.println("pad.left=" + paddingLeft);
        out.println("pad.advance.x=" + paddingAdvanceX);
        out.println("pad.advance.y=" + paddingAdvanceY);
        out.println();
        out.println("glyph.page.width=" + glyphPageWidth);
        out.println("glyph.page.height=" + glyphPageHeight);
        out.println();
        for (ConfigurableEffect configurableEffect : effects) {
            ConfigurableEffect effect = configurableEffect;
            out.println("effect.class=" + effect.getClass().getName());
            for (Value value2 : effect.getValues()) {
                Value value = value2;
                out.println("effect." + value.getName() + "=" + value.getString());
            }
            out.println();
        }
        out.close();
    }
}
