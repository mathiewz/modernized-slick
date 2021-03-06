package com.github.mathiewz.slick.command;

/**
 * A control relating to a command indicate that it should be fired when a specific key is pressed
 * or released.
 *
 * @author joverton
 */
public class KeyControl implements Control {
    /** The key code that needs to be pressed */
    private final int keycode;
    
    /**
     * Create a new control that caused an command to be fired on a key pressed/released
     *
     * @param keycode
     *            The code of the key that causes the command
     */
    public KeyControl(int keycode) {
        this.keycode = keycode;
    }
    
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        return o instanceof KeyControl ? ((KeyControl) o).keycode == keycode : false;
    }
    
    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return keycode;
    }
}
