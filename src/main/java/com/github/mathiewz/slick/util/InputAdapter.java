package com.github.mathiewz.slick.util;

import com.github.mathiewz.slick.Input;
import com.github.mathiewz.slick.InputListener;

/**
 * An implement implementation of the InputListener interface
 *
 * @author kevin
 */
public class InputAdapter implements InputListener {
    /** A flag to indicate if we're accepting input here */
    private boolean acceptingInput = true;

    /**
     * @see com.github.mathiewz.slick.InputListener#controllerButtonPressed(int, int)
     */
    @Override
    public void controllerButtonPressed(int controller, int button) {
    }

    /**
     * @see com.github.mathiewz.slick.InputListener#controllerButtonReleased(int, int)
     */
    @Override
    public void controllerButtonReleased(int controller, int button) {
    }

    /**
     * @see com.github.mathiewz.slick.InputListener#controllerDownPressed(int)
     */
    @Override
    public void controllerDownPressed(int controller) {
    }

    /**
     * @see com.github.mathiewz.slick.InputListener#controllerDownReleased(int)
     */
    @Override
    public void controllerDownReleased(int controller) {
    }

    /**
     * @see com.github.mathiewz.slick.InputListener#controllerLeftPressed(int)
     */
    @Override
    public void controllerLeftPressed(int controller) {
    }

    /**
     * @see com.github.mathiewz.slick.InputListener#controllerLeftReleased(int)
     */
    @Override
    public void controllerLeftReleased(int controller) {
    }

    /**
     * @see com.github.mathiewz.slick.InputListener#controllerRightPressed(int)
     */
    @Override
    public void controllerRightPressed(int controller) {
    }

    /**
     * @see com.github.mathiewz.slick.InputListener#controllerRightReleased(int)
     */
    @Override
    public void controllerRightReleased(int controller) {
    }

    /**
     * @see com.github.mathiewz.slick.InputListener#controllerUpPressed(int)
     */
    @Override
    public void controllerUpPressed(int controller) {
    }

    /**
     * @see com.github.mathiewz.slick.InputListener#controllerUpReleased(int)
     */
    @Override
    public void controllerUpReleased(int controller) {
    }

    /**
     * @see com.github.mathiewz.slick.InputListener#inputEnded()
     */
    @Override
    public void inputEnded() {
    }

    /**
     * @see com.github.mathiewz.slick.InputListener#isAcceptingInput()
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
     * @see com.github.mathiewz.slick.InputListener#keyPressed(int, char)
     */
    @Override
    public void keyPressed(int key, char c) {
    }

    /**
     * @see com.github.mathiewz.slick.InputListener#keyReleased(int, char)
     */
    @Override
    public void keyReleased(int key, char c) {
    }

    /**
     * @see com.github.mathiewz.slick.InputListener#mouseMoved(int, int, int, int)
     */
    @Override
    public void mouseMoved(int oldx, int oldy, int newx, int newy) {
    }

    /**
     * @see com.github.mathiewz.slick.InputListener#mousePressed(int, int, int)
     */
    @Override
    public void mousePressed(int button, int x, int y) {
    }

    /**
     * @see com.github.mathiewz.slick.InputListener#mouseReleased(int, int, int)
     */
    @Override
    public void mouseReleased(int button, int x, int y) {
    }

    /**
     * @see com.github.mathiewz.slick.InputListener#mouseWheelMoved(int)
     */
    @Override
    public void mouseWheelMoved(int change) {
    }

    /**
     * @see com.github.mathiewz.slick.InputListener#setInput(com.github.mathiewz.slick.Input)
     */
    @Override
    public void setInput(Input input) {
    }

    /**
     * @see com.github.mathiewz.slick.InputListener#mouseClicked(int, int, int, int)
     */
    @Override
    public void mouseClicked(int button, int x, int y, int clickCount) {
    }

    @Override
    public void mouseDragged(int oldx, int oldy, int newx, int newy) {
    }

    /**
     * @see com.github.mathiewz.slick.ControlledInputReciever#inputStarted()
     */
    @Override
    public void inputStarted() {

    }
}
