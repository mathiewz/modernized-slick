package com.github.mathiewz.slick.command;

/**
 * A control indicating that a particular direction must be pressed or released
 * on a controller to cause the command to fire
 *
 * @author kevin
 */
public class ControllerDirectionControl extends ControllerControl {

    public enum Direction {
        LEFT,
        RIGHT,
        UP,
        DOWN;
    }
    
    /**
     * Create a new input that indicates a direcitonal control must be pressed
     *
     * @param controllerIndex
     *            The index of the controller to listen to
     * @param dir
     *            The direction to wait for
     */
    public ControllerDirectionControl(int controllerIndex, Direction dir) {
        super(controllerIndex, dir.ordinal() + 1, 0);
    }
    
}
