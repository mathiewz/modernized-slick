package com.github.mathiewz.slick.command;

/**
 * A control indicating that a mouse button must be pressed or released to cause an command
 *
 * @author joverton
 */
public class MouseButtonControl implements Control {
    /** The button to be pressed */
    private final int button;
    
    /**
     * Create a new control that indicates a mouse button to be pressed or released
     *
     * @param button
     *            The button that should be pressed to cause the command
     */
    public MouseButtonControl(int button) {
        this.button = button;
    }
    
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        return o instanceof MouseButtonControl ? ((MouseButtonControl) o).button == button : false;
    }
    
    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return button;
    }
}
