package com.github.mathiewz.util;

import com.github.mathiewz.Input;
import com.github.mathiewz.InputListener;

/**
 * An implement implementation of the InputListener interface
 *
 * @author kevin
 */
public class InputAdapter implements InputListener {
    /** A flag to indicate if we're accepting input here */
    private boolean acceptingInput = true;

    /**
     * @see com.github.mathiewz.InputListener#controllerButtonPressed(int, int)
     */
    @Override
    public void controllerButtonPressed(int controller, int button) {
    }

    /**
     * @see com.github.mathiewz.InputListener#controllerButtonReleased(int, int)
     */
    @Override
    public void controllerButtonReleased(int controller, int button) {
    }

    /**
     * @see com.github.mathiewz.InputListener#controllerDownPressed(int)
     */
    @Override
    public void controllerDownPressed(int controller) {
    }

    /**
     * @see com.github.mathiewz.InputListener#controllerDownReleased(int)
     */
    @Override
    public void controllerDownReleased(int controller) {
    }

    /**
     * @see com.github.mathiewz.InputListener#controllerLeftPressed(int)
     */
    @Override
    public void controllerLeftPressed(int controller) {
    }

    /**
     * @see com.github.mathiewz.InputListener#controllerLeftReleased(int)
     */
    @Override
    public void controllerLeftReleased(int controller) {
    }

    /**
     * @see com.github.mathiewz.InputListener#controllerRightPressed(int)
     */
    @Override
    public void controllerRightPressed(int controller) {
    }

    /**
     * @see com.github.mathiewz.InputListener#controllerRightReleased(int)
     */
    @Override
    public void controllerRightReleased(int controller) {
    }

    /**
     * @see com.github.mathiewz.InputListener#controllerUpPressed(int)
     */
    @Override
    public void controllerUpPressed(int controller) {
    }

    /**
     * @see com.github.mathiewz.InputListener#controllerUpReleased(int)
     */
    @Override
    public void controllerUpReleased(int controller) {
    }

    /**
     * @see com.github.mathiewz.InputListener#inputEnded()
     */
    @Override
    public void inputEnded() {
    }

    /**
     * @see com.github.mathiewz.InputListener#isAcceptingInput()
     */
    @Override
    public boolean isAcceptingInput() {
        return acceptingInput;
    }

    /**
     * Indicate if we should be accepting input of any sort
     *
     * @param acceptingInput
     *            True if we should accept input
     */
    public void setAcceptingInput(boolean acceptingInput) {
        this.acceptingInput = acceptingInput;
    }

    /**
     * @see com.github.mathiewz.InputListener#keyPressed(int, char)
     */
    @Override
    public void keyPressed(int key, char c) {
    }

    /**
     * @see com.github.mathiewz.InputListener#keyReleased(int, char)
     */
    @Override
    public void keyReleased(int key, char c) {
    }

    /**
     * @see com.github.mathiewz.InputListener#mouseMoved(int, int, int, int)
     */
    @Override
    public void mouseMoved(int oldx, int oldy, int newx, int newy) {
    }

    /**
     * @see com.github.mathiewz.InputListener#mousePressed(int, int, int)
     */
    @Override
    public void mousePressed(int button, int x, int y) {
    }

    /**
     * @see com.github.mathiewz.InputListener#mouseReleased(int, int, int)
     */
    @Override
    public void mouseReleased(int button, int x, int y) {
    }

    /**
     * @see com.github.mathiewz.InputListener#mouseWheelMoved(int)
     */
    @Override
    public void mouseWheelMoved(int change) {
    }

    /**
     * @see com.github.mathiewz.InputListener#setInput(com.github.mathiewz.Input)
     */
    @Override
    public void setInput(Input input) {
    }

    /**
     * @see com.github.mathiewz.InputListener#mouseClicked(int, int, int, int)
     */
    @Override
    public void mouseClicked(int button, int x, int y, int clickCount) {
    }

    @Override
    public void mouseDragged(int oldx, int oldy, int newx, int newy) {
    }

    /**
     * @see com.github.mathiewz.ControlledInputReciever#inputStarted()
     */
    @Override
    public void inputStarted() {

    }
}
