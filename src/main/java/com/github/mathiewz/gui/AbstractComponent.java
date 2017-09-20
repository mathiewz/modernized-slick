package com.github.mathiewz.gui;

import java.util.HashSet;
import java.util.Set;

import com.github.mathiewz.Graphics;
import com.github.mathiewz.Input;
import com.github.mathiewz.geom.Rectangle;
import com.github.mathiewz.util.InputAdapter;

/**
 * The utility class to handle all the input related gubbins for basic GUI
 * components
 *
 * @author kevin
 */
public abstract class AbstractComponent extends InputAdapter {
    /** The component that currently has focus */
    private static AbstractComponent currentFocus = null;
    
    /** The game container */
    protected GUIContext container;
    
    /** Listeners for the component to notify */
    protected Set<ComponentListener> listeners;
    
    /** True if this component currently has focus */
    private boolean focus = false;
    
    /** The input we're responding to */
    protected Input input;
    
    /**
     * Create a new component
     *
     * @param container
     *            The container displaying this component
     */
    public AbstractComponent(GUIContext container) {
        this.container = container;
        
        listeners = new HashSet<>();
        
        input = container.getInput();
        input.addPrimaryListener(this);
        
        setLocation(0, 0);
    }
    
    /**
     * Add a component listener to be informed when the component sees fit.
     *
     * It will ignore listeners already added.
     *
     * @param listener
     *            listener
     */
    public void addListener(ComponentListener listener) {
        listeners.add(listener);
    }
    
    /**
     * Remove a component listener.
     *
     * It will ignore if the listener wasn't added.
     *
     * @param listener
     *            listener
     */
    public void removeListener(ComponentListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * Notify all the listeners.
     */
    protected void notifyListeners() {
        listeners.forEach(l -> l.componentActivated(this));
    }
    
    /**
     * Render this component to the screen
     *
     * @param container
     *            The container displaying this component
     * @param g
     *            The graphics context used to render to the display
     */
    public abstract void render(GUIContext container, Graphics g);
    
    /**
     * Moves the component.
     *
     * @param x
     *            X coordinate
     * @param y
     *            Y coordinate
     */
    public abstract void setLocation(int x, int y);
    
    /**
     * Returns the position in the X coordinate
     *
     * @return x
     */
    public abstract int getX();
    
    /**
     * Returns the position in the Y coordinate
     *
     * @return y
     */
    public abstract int getY();
    
    /**
     * Get the width of the component
     *
     * @return The width of the component
     */
    public abstract int getWidth();
    
    /**
     * Get the height of the component
     *
     * @return The height of the component
     */
    public abstract int getHeight();
    
    /**
     * Indicate whether this component should be focused or not
     *
     * @param focus
     *            if the component should be focused
     */
    public void setFocus(boolean focus) {
        if (focus) {
            if (currentFocus != null) {
                currentFocus.setFocus(false);
            }
            currentFocus = this;
        } else {
            if (currentFocus == this) {
                currentFocus = null;
            }
        }
        this.focus = focus;
    }
    
    /**
     * Check if this component currently has focus
     *
     * @return if this field currently has focus
     */
    public boolean hasFocus() {
        return focus;
    }
    
    /**
     * Consume the event currently being processed
     */
    protected void consumeEvent() {
        input.consumeEvent();
    }
    
    /**
     * Gives the focus to this component with a click of the mouse.
     *
     * @see com.github.mathiewz.gui.AbstractComponent#mouseReleased(int, int, int)
     */
    @Override
    public void mouseReleased(int button, int x, int y) {
        setFocus(Rectangle.contains(x, y, getX(), getY(), getWidth(), getHeight()));
    }
}