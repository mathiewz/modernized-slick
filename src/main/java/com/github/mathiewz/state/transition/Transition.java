package com.github.mathiewz.state.transition;

import com.github.mathiewz.GameContainer;
import com.github.mathiewz.Graphics;
import com.github.mathiewz.state.GameState;
import com.github.mathiewz.state.StateBasedGame;

/**
 * A transition between two game states
 *
 * @author kevin
 */
public interface Transition {
    /**
     * Update the transition. Cause what ever happens in the transition to happen
     *
     * @param game
     *            The game this transition is being rendered as part of
     * @param container
     *            The container holding the game
     * @param delta
     *            The amount of time passed since last update
     */
    public void update(StateBasedGame game, GameContainer container, int delta);
    
    /**
     * Render the transition before the existing state rendering
     *
     * @param game
     *            The game this transition is being rendered as part of
     * @param container
     *            The container holding the game
     * @param g
     *            The graphics context to use when rendering the transiton
     */
    public void preRender(StateBasedGame game, GameContainer container, Graphics g);
    
    /**
     * Render the transition over the existing state rendering
     *
     * @param game
     *            The game this transition is being rendered as part of
     * @param container
     *            The container holding the game
     * @param g
     *            The graphics context to use when rendering the transiton
     */
    public void postRender(StateBasedGame game, GameContainer container, Graphics g);
    
    /**
     * Check if this transtion has been completed
     *
     * @return True if the transition has been completed
     */
    public boolean isComplete();
    
    /**
     * Initialise the transition
     *
     * @param firstState
     *            The first state we're rendering (this will be rendered by the framework)
     * @param secondState
     *            The second stat we're transitioning to or from (this one won't be rendered)
     */
    public void init(GameState firstState, GameState secondState);
}
