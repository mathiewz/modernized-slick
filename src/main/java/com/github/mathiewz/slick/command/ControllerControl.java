package com.github.mathiewz.slick.command;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * A control describing input provided from a controller. This allows controls to be
 * mapped to game pad inputs.
 *
 * @author joverton
 */
abstract class ControllerControl implements Control {
    /** Indicates a button was pressed */
    protected static final int BUTTON_EVENT = 0;

    /** The type of event we're looking for */
    private final int event;
    /** The index of the button we're waiting for */
    private final int button;
    /** The index of the controller we're waiting on */
    private final int controllerNumber;

    /**
     * Create a new controller control
     *
     * @param controllerNumber
     *            The index of the controller to react to
     * @param event
     *            The event to react to
     * @param button
     *            The button index to react to on a BUTTON event
     */
    protected ControllerControl(int controllerNumber, int event, int button) {
        this.event = event;
        this.button = button;
        this.controllerNumber = controllerNumber;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof ControllerControl)) {
            return false;
        }
        ControllerControl c = (ControllerControl) o;
        return new EqualsBuilder()
                .append(controllerNumber, c.controllerNumber)
                .append(event, c.event)
                .append(button, c.button)
                .isEquals();
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(controllerNumber)
                .append(event)
                .append(button)
                .toHashCode();
    }
}
