
package com.github.mathiewz.font.effects;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import com.github.mathiewz.UnicodeFont;
import com.github.mathiewz.font.Glyph;

/**
 * Makes glyphs a solid color.
 *
 * @author Nathan Sweet <misc@n4te.com>
 */
public class ColorEffect implements ConfigurableEffect {
    /** The colour that will be applied across the text */
    private Color color = Color.white;

    /**
     * Default constructor for injection
     */
    public ColorEffect() {
    }

    /**
     * Create a new effect to colour the text
     *
     * @param color
     *            The colour to apply across the text
     */
    public ColorEffect(Color color) {
        this.color = color;
    }

    /**
     * @see com.github.mathiewz.font.effects.Effect#draw(java.awt.image.BufferedImage, java.awt.Graphics2D, com.github.mathiewz.UnicodeFont, com.github.mathiewz.font.Glyph)
     */
    @Override
    public void draw(BufferedImage image, Graphics2D g, UnicodeFont unicodeFont, Glyph glyph) {
        g.setColor(color);
        g.fill(glyph.getShape());
    }

    /**
     * Get the colour being applied by this effect
     *
     * @return The colour being applied by this effect
     */
    public Color getColor() {
        return color;
    }

    /**
     * Set the colour being applied by this effect
     *
     * @param color
     *            The colour being applied by this effect
     */
    public void setColor(Color color) {
        if (color == null) {
            throw new IllegalArgumentException("color cannot be null.");
        }
        this.color = color;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Color";
    }

    /**
     * @see com.github.mathiewz.font.effects.ConfigurableEffect#getValues()
     */
    @Override
    public List<Value> getValues() {
        List<Value> values = new ArrayList<>();
        values.add(EffectUtil.colorValue("Color", color));
        return values;
    }

    /**
     * @see com.github.mathiewz.font.effects.ConfigurableEffect#setValues(java.util.List)
     */
    @Override
    public void setValues(List<Value> values) {
        for (Value value2 : values) {
            Value value = value2;
            if (value.getName().equals("Color")) {
                setColor((Color) value.getObject());
            }
        }
    }
}
