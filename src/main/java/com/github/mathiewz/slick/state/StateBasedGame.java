package com.github.mathiewz.slick.state;

import java.util.HashMap;
import java.util.Iterator;

import com.github.mathiewz.slick.Game;
import com.github.mathiewz.slick.GameContainer;
import com.github.mathiewz.slick.Graphics;
import com.github.mathiewz.slick.Input;
import com.github.mathiewz.slick.InputListener;
import com.github.mathiewz.slick.SlickException;
import com.github.mathiewz.slick.state.transition.EmptyTransition;
import com.github.mathiewz.slick.state.transition.Transition;

/**
 * A state based game isolated different stages of the game (menu, ingame, hiscores, etc) into
 * different states so they can be easily managed and maintained.
 *
 * @author kevin
 */
public abstract class StateBasedGame implements Game, InputListener {
    /** The list of states making up this game */
    private final HashMap<Integer, GameState> states = new HashMap<>();
    /** The current state */
    private GameState currentState;
    /** The next state we're moving into */
    private GameState nextState;
    /** The container holding this game */
    private GameContainer container;
    /** The title of the game */
    private String title;
    
    /** The transition being used to enter the state */
    private Transition enterTransition;
    /** The transition being used to leave the state */
    private Transition leaveTransition;
    
    /**
     * Create a new state based game
     *
     * @param name
     *            The name of the game
     */
    public StateBasedGame(String name) {
        title = name;
        
        currentState = new BasicGameState() {
            @Override
            public int getID() {
                return -1;
            }
            
            @Override
            public void init(GameContainer container, StateBasedGame game) {
            }
            
            @Override
            public void update(GameContainer container, StateBasedGame game, int delta) {
            }
            
            @Override
            public void render(GameContainer container, StateBasedGame game, Graphics g) {
            }
        };
    }
    
    /**
     * @see com.github.mathiewz.slick.ControlledInputReciever#inputStarted()
     */
    @Override
    public void inputStarted() {
        
    }
    
    /**
     * Get the number of states that have been added to this game
     *
     * @return The number of states that have been added to this game
     */
    public int getStateCount() {
        return states.keySet().size();
    }
    
    /**
     * Get the ID of the state the game is currently in
     *
     * @return The ID of the state the game is currently in
     */
    public int getCurrentStateID() {
        return currentState.getID();
    }
    
    /**
     * Get the state the game is currently in
     *
     * @return The state the game is currently in
     */
    public GameState getCurrentState() {
        return currentState;
    }
    
    /**
     * @see com.github.mathiewz.slick.InputListener#setInput(com.github.mathiewz.slick.Input)
     */
    @Override
    public void setInput(Input input) {
    }
    
    /**
     * Add a state to the game. The state will be updated and maintained
     * by the game
     *
     * @param state
     *            The state to be added
     */
    public void addState(GameState state) {
        states.put(new Integer(state.getID()), state);
        
        if (currentState.getID() == -1) {
            currentState = state;
        }
    }
    
    /**
     * Get a state based on it's identifier
     *
     * @param id
     *            The ID of the state to retrieve
     * @return The state requested or null if no state with the specified ID exists
     */
    public GameState getState(int id) {
        return states.get(new Integer(id));
    }
    
    /**
     * Enter a particular game state with no transition
     *
     * @param id
     *            The ID of the state to enter
     */
    public void enterState(int id) {
        enterState(id, new EmptyTransition(), new EmptyTransition());
    }
    
    /**
     * Enter a particular game state with the transitions provided
     *
     * @param id
     *            The ID of the state to enter
     * @param leave
     *            The transition to use when leaving the current state
     * @param enter
     *            The transition to use when entering the new state
     */
    public void enterState(int id, Transition leave, Transition enter) {
        if (leave == null) {
            leave = new EmptyTransition();
        }
        if (enter == null) {
            enter = new EmptyTransition();
        }
        leaveTransition = leave;
        enterTransition = enter;
        
        nextState = getState(id);
        if (nextState == null) {
            throw new SlickException("No game state registered with the ID: " + id);
        }
        
        leaveTransition.init(currentState, nextState);
    }
    
    /**
     * @see com.github.mathiewz.slick.BasicGame#init(com.github.mathiewz.slick.GameContainer)
     */
    @Override
    public final void init(GameContainer container) {
        this.container = container;
        initStatesList(container);
        
        Iterator<GameState> gameStates = states.values().iterator();
        
        while (gameStates.hasNext()) {
            GameState state = gameStates.next();
            
            state.init(container, this);
        }
        
        if (currentState != null) {
            currentState.enter(container, this);
        }
    }
    
    /**
     * Initialise the list of states making up this game
     *
     * @param container
     *            The container holding the game
     */
    public abstract void initStatesList(GameContainer container);
    
    /**
     * @see com.github.mathiewz.slick.Game#render(com.github.mathiewz.slick.GameContainer, com.github.mathiewz.slick.Graphics)
     */
    @Override
    public final void render(GameContainer container, Graphics g) {
        preRenderState(container, g);
        
        if (leaveTransition != null) {
            leaveTransition.preRender(this, container, g);
        } else if (enterTransition != null) {
            enterTransition.preRender(this, container, g);
        }
        
        currentState.render(container, this, g);
        
        if (leaveTransition != null) {
            leaveTransition.postRender(this, container, g);
        } else if (enterTransition != null) {
            enterTransition.postRender(this, container, g);
        }
        
        postRenderState(container, g);
    }
    
    /**
     * User hook for rendering at the before the current state
     * and/or transition have been rendered
     *
     * @param container
     *            The container in which the game is hosted
     * @param g
     *            The graphics context on which to draw
     */
    protected void preRenderState(GameContainer container, Graphics g) {
        // NO-OP
    }
    
    /**
     * User hook for rendering at the game level after the current state
     * and/or transition have been rendered
     *
     * @param container
     *            The container in which the game is hosted
     * @param g
     *            The graphics context on which to draw
     */
    protected void postRenderState(GameContainer container, Graphics g) {
        // NO-OP
    }
    
    /**
     * @see com.github.mathiewz.slick.BasicGame#update(com.github.mathiewz.slick.GameContainer, int)
     */
    @Override
    public final void update(GameContainer container, int delta) {
        preUpdateState(container, delta);
        
        if (leaveTransition != null) {
            leaveTransition.update(this, container, delta);
            if (leaveTransition.isComplete()) {
                currentState.leave(container, this);
                GameState prevState = currentState;
                currentState = nextState;
                nextState = null;
                leaveTransition = null;
                currentState.enter(container, this);
                if (enterTransition != null) {
                    enterTransition.init(currentState, prevState);
                }
            } else {
                return;
            }
        }
        
        if (enterTransition != null) {
            enterTransition.update(this, container, delta);
            if (enterTransition.isComplete()) {
                enterTransition = null;
            } else {
                return;
            }
        }
        
        currentState.update(container, this, delta);
        
        postUpdateState(container, delta);
    }
    
    /**
     * User hook for updating at the game before the current state
     * and/or transition have been updated
     *
     * @param container
     *            The container in which the game is hosted
     * @param delta
     *            The amount of time in milliseconds since last update
     */
    protected void preUpdateState(GameContainer container, int delta) {
        // NO-OP
    }
    
    /**
     * User hook for rendering at the game level after the current state
     * and/or transition have been updated
     *
     * @param container
     *            The container in which the game is hosted
     * @param delta
     *            The amount of time in milliseconds since last update
     */
    protected void postUpdateState(GameContainer container, int delta) {
        // NO-OP
    }
    
    /**
     * Check if the game is transitioning between states
     *
     * @return True if we're transitioning between states
     */
    private boolean transitioning() {
        return leaveTransition != null || enterTransition != null;
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
     * Get the container holding this game
     *
     * @return The game container holding this game
     */
    public GameContainer getContainer() {
        return container;
    }
    
    /**
     * @see com.github.mathiewz.slick.InputListener#controllerButtonPressed(int, int)
     */
    @Override
    public void controllerButtonPressed(int controller, int button) {
        if (transitioning()) {
            return;
        }
        
        currentState.controllerButtonPressed(controller, button);
    }
    
    /**
     * @see com.github.mathiewz.slick.InputListener#controllerButtonReleased(int, int)
     */
    @Override
    public void controllerButtonReleased(int controller, int button) {
        if (transitioning()) {
            return;
        }
        
        currentState.controllerButtonReleased(controller, button);
    }
    
    /**
     * @see com.github.mathiewz.slick.InputListener#controllerDownPressed(int)
     */
    @Override
    public void controllerDownPressed(int controller) {
        if (transitioning()) {
            return;
        }
        
        currentState.controllerDownPressed(controller);
    }
    
    /**
     * @see com.github.mathiewz.slick.InputListener#controllerDownReleased(int)
     */
    @Override
    public void controllerDownReleased(int controller) {
        if (transitioning()) {
            return;
        }
        
        currentState.controllerDownReleased(controller);
    }
    
    /**
     * @see com.github.mathiewz.slick.InputListener#controllerLeftPressed(int)
     */
    @Override
    public void controllerLeftPressed(int controller) {
        if (transitioning()) {
            return;
        }
        
        currentState.controllerLeftPressed(controller);
    }
    
    /**
     * @see com.github.mathiewz.slick.InputListener#controllerLeftReleased(int)
     */
    @Override
    public void controllerLeftReleased(int controller) {
        if (transitioning()) {
            return;
        }
        
        currentState.controllerLeftReleased(controller);
    }
    
    /**
     * @see com.github.mathiewz.slick.InputListener#controllerRightPressed(int)
     */
    @Override
    public void controllerRightPressed(int controller) {
        if (transitioning()) {
            return;
        }
        
        currentState.controllerRightPressed(controller);
    }
    
    /**
     * @see com.github.mathiewz.slick.InputListener#controllerRightReleased(int)
     */
    @Override
    public void controllerRightReleased(int controller) {
        if (transitioning()) {
            return;
        }
        
        currentState.controllerRightReleased(controller);
    }
    
    /**
     * @see com.github.mathiewz.slick.InputListener#controllerUpPressed(int)
     */
    @Override
    public void controllerUpPressed(int controller) {
        if (transitioning()) {
            return;
        }
        
        currentState.controllerUpPressed(controller);
    }
    
    /**
     * @see com.github.mathiewz.slick.InputListener#controllerUpReleased(int)
     */
    @Override
    public void controllerUpReleased(int controller) {
        if (transitioning()) {
            return;
        }
        
        currentState.controllerUpReleased(controller);
    }
    
    /**
     * @see com.github.mathiewz.slick.InputListener#keyPressed(int, char)
     */
    @Override
    public void keyPressed(int key, char c) {
        if (transitioning()) {
            return;
        }
        
        currentState.keyPressed(key, c);
    }
    
    /**
     * @see com.github.mathiewz.slick.InputListener#keyReleased(int, char)
     */
    @Override
    public void keyReleased(int key, char c) {
        if (transitioning()) {
            return;
        }
        
        currentState.keyReleased(key, c);
    }
    
    /**
     * @see com.github.mathiewz.slick.InputListener#mouseMoved(int, int, int, int)
     */
    @Override
    public void mouseMoved(int oldx, int oldy, int newx, int newy) {
        if (transitioning()) {
            return;
        }
        
        currentState.mouseMoved(oldx, oldy, newx, newy);
    }
    
    /**
     * @see com.github.mathiewz.slick.InputListener#mouseDragged(int, int, int, int)
     */
    @Override
    public void mouseDragged(int oldx, int oldy, int newx, int newy) {
        if (transitioning()) {
            return;
        }
        
        currentState.mouseDragged(oldx, oldy, newx, newy);
    }
    
    /**
     * @see com.github.mathiewz.slick.InputListener#mouseClicked(int, int, int, int)
     */
    @Override
    public void mouseClicked(int button, int x, int y, int clickCount) {
        if (transitioning()) {
            return;
        }
        
        currentState.mouseClicked(button, x, y, clickCount);
    }
    
    /**
     * @see com.github.mathiewz.slick.InputListener#mousePressed(int, int, int)
     */
    @Override
    public void mousePressed(int button, int x, int y) {
        if (transitioning()) {
            return;
        }
        
        currentState.mousePressed(button, x, y);
    }
    
    /**
     * @see com.github.mathiewz.slick.InputListener#mouseReleased(int, int, int)
     */
    @Override
    public void mouseReleased(int button, int x, int y) {
        if (transitioning()) {
            return;
        }
        
        currentState.mouseReleased(button, x, y);
    }
    
    /**
     * @see com.github.mathiewz.slick.InputListener#isAcceptingInput()
     */
    @Override
    public boolean isAcceptingInput() {
        if (transitioning()) {
            return false;
        }
        
        return currentState.isAcceptingInput();
    }
    
    /**
     * @see com.github.mathiewz.slick.InputListener#inputEnded()
     */
    @Override
    public void inputEnded() {
    }
    
    /**
     * @see com.github.mathiewz.slick.InputListener#mouseWheelMoved(int)
     */
    @Override
    public void mouseWheelMoved(int newValue) {
        if (transitioning()) {
            return;
        }
        
        currentState.mouseWheelMoved(newValue);
    }
    
}
