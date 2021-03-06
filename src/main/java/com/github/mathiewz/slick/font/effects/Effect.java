
package com.github.mathiewz.slick.font.effects;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.github.mathiewz.slick.UnicodeFont;
import com.github.mathiewz.slick.font.Glyph;

/**
 * A graphical effect that is applied to glyphs in a {@link UnicodeFont}.
 *
 * @author Nathan Sweet 
 */
public interface Effect {
    /**
     * Called to draw the effect.
     *
     * @param image
     *            The image to draw into
     * @param g
     *            The graphics context to use for applying the effect
     * @param unicodeFont
     *            The font being rendered
     * @param glyph
     *            The particular glyph being rendered
     */
    public void draw(BufferedImage image, Graphics2D g, UnicodeFont unicodeFont, Glyph glyph);
}
