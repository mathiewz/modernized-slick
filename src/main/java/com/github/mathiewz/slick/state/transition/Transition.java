package com.github.mathiewz.slick.state.transition;

import com.github.mathiewz.slick.Color;
import com.github.mathiewz.slick.GameContainer;
import com.github.mathiewz.slick.Graphics;
import com.github.mathiewz.slick.state.GameState;
import com.github.mathiewz.slick.state.StateBasedGame;

/**
 * A transition between two game states
 *
 * @author kevin
 */
public abstract class Transition {

    /** The background to draw underneath the previous state (null for none) */
    protected Color background;

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
    public abstract void update(StateBasedGame game, GameContainer container, int delta);
    
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
    public abstract void preRender(StateBasedGame game, GameContainer container, Graphics g);
    
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
    public abstract void postRender(StateBasedGame game, GameContainer container, Graphics g);
    
    /**
     * Check if this transtion has been completed
     *
     * @return True if the transition has been completed
     */
    public abstract boolean isComplete();
    
    /**
     * Initialise the transition
     *
     * @param firstState
     *            The first state we're rendering (this will be rendered by the framework)
     * @param secondState
     *            The second stat we're transitioning to or from (this one won't be rendered)
     */
    public abstract void init(GameState firstState, GameState secondState);

    protected void fillBackground(GameContainer container, Graphics g) {
        if (background != null) {
            Color c = g.getColor();
            g.setColor(background);
            g.fillRect(0, 0, container.getWidth(), container.getHeight());
            g.setColor(c);
        }
    }
}
