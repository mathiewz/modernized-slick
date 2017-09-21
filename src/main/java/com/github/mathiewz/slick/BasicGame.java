package com.github.mathiewz.slick;

/**
 * A basic implementation of a game to take out the boring bits
 *
 * @author kevin
 */
public abstract class BasicGame implements Game, InputListener {
    /** The maximum number of controllers supported by the basic game */
    private static final int MAX_CONTROLLERS = 20;
    /** The maximum number of controller buttons supported by the basic game */
    private static final int MAX_CONTROLLER_BUTTONS = 100;
    /** The title of the game */
    private final String title;
    /** The state of the left control */
    protected boolean[] controllerLeft = new boolean[MAX_CONTROLLERS];
    /** The state of the right control */
    protected boolean[] controllerRight = new boolean[MAX_CONTROLLERS];
    /** The state of the up control */
    protected boolean[] controllerUp = new boolean[MAX_CONTROLLERS];
    /** The state of the down control */
    protected boolean[] controllerDown = new boolean[MAX_CONTROLLERS];
    /** The state of the button controlls */
    protected boolean[][] controllerButton = new boolean[MAX_CONTROLLERS][MAX_CONTROLLER_BUTTONS];

    /**
     * Create a new basic game
     *
     * @param title
     *            The title for the game
     */
    public BasicGame(String title) {
        this.title = title;
    }

    /**
     * @see com.github.mathiewz.slick.InputListener#setInput(com.github.mathiewz.slick.Input)
     */
    @Override
    public void setInput(Input input) {
    }

    /**
     * @see com.github.mathiewz.slick.Game#closeRequested()
     */
    @Override
    public boolean closeRequested() {
        return true;
    }

    /**
     * @see com.github.mathiewz.slick.Game#getTitle()
     */
    @Override
    public String getTitle() {
        return title;
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
     * @see com.github.mathiewz.slick.InputListener#mouseDragged(int, int, int, int)
     */
    @Override
    public void mouseDragged(int oldx, int oldy, int newx, int newy) {
    }

    /**
     * @see com.github.mathiewz.slick.InputListener#mouseClicked(int, int, int, int)
     */
    @Override
    public void mouseClicked(int button, int x, int y, int clickCount) {
    }

    /**
     * @see com.github.mathiewz.slick.InputListener#mousePressed(int, int, int)
     */
    @Override
    public void mousePressed(int button, int x, int y) {

    }

    /**
     * @see com.github.mathiewz.slick.InputListener#controllerButtonPressed(int, int)
     */
    @Override
    public void controllerButtonPressed(int controller, int button) {
        controllerButton[controller][button] = true;
    }

    /**
     * @see com.github.mathiewz.slick.InputListener#controllerButtonReleased(int, int)
     */
    @Override
    public void controllerButtonReleased(int controller, int button) {
        controllerButton[controller][button] = false;
    }

    /**
     * @see com.github.mathiewz.slick.InputListener#controllerDownPressed(int)
     */
    @Override
    public void controllerDownPressed(int controller) {
        controllerDown[controller] = true;
    }

    /**
     * @see com.github.mathiewz.slick.InputListener#controllerDownReleased(int)
     */
    @Override
    public void controllerDownReleased(int controller) {
        controllerDown[controller] = false;
    }

    /**
     * @see com.github.mathiewz.slick.InputListener#controllerLeftPressed(int)
     */
    @Override
    public void controllerLeftPressed(int controller) {
        controllerLeft[controller] = true;
    }

    /**
     * @see com.github.mathiewz.slick.InputListener#controllerLeftReleased(int)
     */
    @Override
    public void controllerLeftReleased(int controller) {
        controllerLeft[controller] = false;
    }

    /**
     * @see com.github.mathiewz.slick.InputListener#controllerRightPressed(int)
     */
    @Override
    public void controllerRightPressed(int controller) {
        controllerRight[controller] = true;
    }

    /**
     * @see com.github.mathiewz.slick.InputListener#controllerRightReleased(int)
     */
    @Override
    public void controllerRightReleased(int controller) {
        controllerRight[controller] = false;
    }

    /**
     * @see com.github.mathiewz.slick.InputListener#controllerUpPressed(int)
     */
    @Override
    public void controllerUpPressed(int controller) {
        controllerUp[controller] = true;
    }

    /**
     * @see com.github.mathiewz.slick.InputListener#controllerUpReleased(int)
     */
    @Override
    public void controllerUpReleased(int controller) {
        controllerUp[controller] = false;
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
     * @see com.github.mathiewz.slick.InputListener#isAcceptingInput()
     */
    @Override
    public boolean isAcceptingInput() {
        return true;
    }

    /**
     * @see com.github.mathiewz.slick.InputListener#inputEnded()
     */
    @Override
    public void inputEnded() {

    }

    /**
     * @see com.github.mathiewz.slick.ControlledInputReciever#inputStarted()
     */
    @Override
    public void inputStarted() {

    }
}
