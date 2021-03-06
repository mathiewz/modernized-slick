package com.github.mathiewz.slick;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.github.mathiewz.slick.opengl.Texture;

/**
 * A sheet of sprites that can be drawn individually
 *
 * @author Kevin Glass
 */
public class SpriteSheet extends Image {
    /** The width of a single element in pixels */
    private final int tw;
    /** The height of a single element in pixels */
    private final int th;
    /** The margin of the image */
    private int margin = 0;
    /** Subimages */
    private Image[][] subImages;
    /** The spacing between tiles */
    private int spacing;
    /** The target image for this sheet */
    private final Image target;
    
    /**
     * Create a new sprite sheet based on a image location
     *
     * @param ref
     *            The URL to the image to use
     * @param tw
     *            The width of the tiles on the sheet
     * @param th
     *            The height of the tiles on the sheet
     * @throws IOException
     *             Indicates the URL could not be opened
     */
    public SpriteSheet(URL ref, int tw, int th) throws IOException {
        this(new Image(ref.openStream(), ref.toString(), false), tw, th);
    }
    
    /**
     * Create a new sprite sheet based on a image location
     *
     * @param image
     *            The image to based the sheet of
     * @param tw
     *            The width of the tiles on the sheet
     * @param th
     *            The height of the tiles on the sheet
     */
    public SpriteSheet(Image image, int tw, int th) {
        super(image);
        
        target = image;
        this.tw = tw;
        this.th = th;
        
        // call init manually since constructing from an image will have previously initialised
        // from incorrect values
        initImpl();
    }
    
    /**
     * Create a new sprite sheet based on a image location
     *
     * @param image
     *            The image to based the sheet of
     * @param tw
     *            The width of the tiles on the sheet
     * @param th
     *            The height of the tiles on the sheet
     * @param spacing
     *            The spacing between tiles
     * @param margin
     *            The magrin around the tiles
     */
    public SpriteSheet(Image image, int tw, int th, int spacing, int margin) {
        super(image);
        
        target = image;
        this.tw = tw;
        this.th = th;
        this.spacing = spacing;
        this.margin = margin;
        
        // call init manually since constructing from an image will have previously initialised
        // from incorrect values
        initImpl();
    }
    
    /**
     * Create a new sprite sheet based on a image location
     *
     * @param image
     *            The image to based the sheet of
     * @param tw
     *            The width of the tiles on the sheet
     * @param th
     *            The height of the tiles on the sheet
     * @param spacing
     *            The spacing between tiles
     */
    public SpriteSheet(Image image, int tw, int th, int spacing) {
        this(image, tw, th, spacing, 0);
    }
    
    /**
     * Create a new sprite sheet based on a image location
     *
     * @param ref
     *            The location of the sprite sheet to load
     * @param tw
     *            The width of the tiles on the sheet
     * @param th
     *            The height of the tiles on the sheet
     * @param spacing
     *            The spacing between tiles
     */
    public SpriteSheet(String ref, int tw, int th, int spacing) {
        this(ref, tw, th, null, spacing);
    }
    
    /**
     * Create a new sprite sheet based on a image location
     *
     * @param ref
     *            The location of the sprite sheet to load
     * @param tw
     *            The width of the tiles on the sheet
     * @param th
     *            The height of the tiles on the sheet
     */
    public SpriteSheet(String ref, int tw, int th) {
        this(ref, tw, th, null);
    }
    
    /**
     * Create a new sprite sheet based on a image location
     *
     * @param ref
     *            The location of the sprite sheet to load
     * @param tw
     *            The width of the tiles on the sheet
     * @param th
     *            The height of the tiles on the sheet
     * @param col
     *            The colour to treat as transparent
     */
    public SpriteSheet(String ref, int tw, int th, Color col) {
        this(ref, tw, th, col, 0);
    }
    
    /**
     * Create a new sprite sheet based on a image location
     *
     * @param ref
     *            The location of the sprite sheet to load
     * @param tw
     *            The width of the tiles on the sheet
     * @param th
     *            The height of the tiles on the sheet
     * @param col
     *            The colour to treat as transparent
     * @param spacing
     *            The spacing between tiles
     */
    public SpriteSheet(String ref, int tw, int th, Color col, int spacing) {
        super(ref, false, FILTER_NEAREST, col);
        
        target = this;
        this.tw = tw;
        this.th = th;
        this.spacing = spacing;
    }
    
    /**
     * Create a new sprite sheet based on a image location
     *
     * @param name
     *            The name to give to the image in the image cache
     * @param ref
     *            The stream from which we can load the image
     * @param tw
     *            The width of the tiles on the sheet
     * @param th
     *            The height of the tiles on the sheet
     */
    public SpriteSheet(String name, InputStream ref, int tw, int th) {
        super(ref, name, false);
        
        target = this;
        this.tw = tw;
        this.th = th;
    }
    
    /**
     * @see com.github.mathiewz.slick.Image#initImpl()
     */
    @Override
    protected void initImpl() {
        if (subImages != null) {
            return;
        }
        
        int tilesAcross = (getWidth() - margin * 2 - tw) / (tw + spacing) + 1;
        int tilesDown = (getHeight() - margin * 2 - th) / (th + spacing) + 1;
        if ((getHeight() - th) % (th + spacing) != 0) {
            tilesDown++;
        }
        
        subImages = new Image[tilesAcross][tilesDown];
        for (int x = 0; x < tilesAcross; x++) {
            for (int y = 0; y < tilesDown; y++) {
                subImages[x][y] = getSprite(x, y);
            }
        }
    }
    
    /**
     * Get the sub image cached in this sprite sheet
     *
     * @param x
     *            The x position in tiles of the image to get
     * @param y
     *            The y position in tiles of the image to get
     * @return The subimage at that location on the sheet
     */
    public Image getSubImage(int x, int y) {
        init();
        
        if (x < 0 || x >= subImages.length) {
            throw new SlickException("SubImage out of sheet bounds: " + x + "," + y);
        }
        if (y < 0 || y >= subImages[0].length) {
            throw new SlickException("SubImage out of sheet bounds: " + x + "," + y);
        }
        
        return subImages[x][y];
    }
    
    /**
     * Get a sprite at a particular cell on the sprite sheet
     *
     * @param x
     *            The x position of the cell on the sprite sheet
     * @param y
     *            The y position of the cell on the sprite sheet
     * @return The single image from the sprite sheet
     */
    public Image getSprite(int x, int y) {
        target.init();
        initImpl();
        
        if (x < 0 || x >= subImages.length || y < 0 || y >= subImages[0].length) {
            throw new SlickException("SubImage out of sheet bounds: " + x + "," + y);
        }
        
        return target.getSubImage(x * (tw + spacing) + margin, y * (th + spacing) + margin, tw, th);
    }
    
    /**
     * Get the number of sprites across the sheet
     *
     * @return The number of sprites across the sheet
     */
    public int getHorizontalCount() {
        target.init();
        initImpl();
        
        return subImages.length;
    }
    
    /**
     * Get the number of sprites down the sheet
     *
     * @return The number of sprite down the sheet
     */
    public int getVerticalCount() {
        target.init();
        initImpl();
        
        return subImages[0].length;
    }
    
    /**
     * Render a sprite when this sprite sheet is in use.
     *
     * @see #startUse()
     * @see #endUse()
     *
     * @param x
     *            The x position to render the sprite at
     * @param y
     *            The y position to render the sprite at
     * @param sx
     *            The x location of the cell to render
     * @param sy
     *            The y location of the cell to render
     */
    public void renderInUse(int x, int y, int sx, int sy) {
        subImages[sx][sy].drawEmbedded(x, y, tw, th);
    }
    
    /**
     * @see com.github.mathiewz.slick.Image#endUse()
     */
    @Override
    public void endUse() {
        if (target == this) {
            super.endUse();
            return;
        }
        target.endUse();
    }
    
    /**
     * @see com.github.mathiewz.slick.Image#startUse()
     */
    @Override
    public void startUse() {
        if (target == this) {
            super.startUse();
            return;
        }
        target.startUse();
    }
    
    /**
     * @see com.github.mathiewz.slick.Image#setTexture(com.github.mathiewz.slick.opengl.Texture)
     */
    @Override
    public void setTexture(Texture texture) {
        if (target == this) {
            super.setTexture(texture);
            return;
        }
        target.setTexture(texture);
    }
}
