package com.github.mathiewz.slick.gui;

import com.github.mathiewz.slick.Graphics;

/**
 * Renamed to provide backwards compatibility
 *
 * @author kevin
 * @deprecated
 */
@Deprecated
public abstract class BasicComponent extends AbstractComponent {
    /** The x position of the component */
    protected int x;
    /** The y position of the component */
    protected int y;
    /** The width of the component */
    protected int width;
    /** The height of the component */
    protected int height;
    
    /**
     * Create a new component
     *
     * @param container
     *            The container displaying this component
     */
    public BasicComponent(GUIContext container) {
        super(container);
    }
    
    /**
     * @see com.github.mathiewz.slick.gui.AbstractComponent#getHeight()
     */
    @Override
    public int getHeight() {
        return height;
    }
    
    /**
     * @see com.github.mathiewz.slick.gui.AbstractComponent#getWidth()
     */
    @Override
    public int getWidth() {
        return width;
    }
    
    /**
     * @see com.github.mathiewz.slick.gui.AbstractComponent#getX()
     */
    @Override
    public int getX() {
        return x;
    }
    
    /**
     * @see com.github.mathiewz.slick.gui.AbstractComponent#getY()
     */
    @Override
    public int getY() {
        return y;
    }
    
    /**
     * Allow the sub-component to render
     *
     * @param container
     *            The container holding the GUI
     * @param g
     *            The graphics context into which we should render
     */
    public abstract void renderImpl(GUIContext container, Graphics g);
    
    /**
     * @see com.github.mathiewz.slick.gui.AbstractComponent#render(com.github.mathiewz.slick.gui.GUIContext, com.github.mathiewz.slick.Graphics)
     */
    @Override
    public void render(GUIContext container, Graphics g) {
        renderImpl(container, g);
    }
    
    /**
     * @see com.github.mathiewz.slick.gui.AbstractComponent#setLocation(int, int)
     */
    @Override
    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
}
