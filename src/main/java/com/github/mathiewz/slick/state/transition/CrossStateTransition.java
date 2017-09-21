package com.github.mathiewz.slick.state.transition;

import com.github.mathiewz.slick.GameContainer;
import com.github.mathiewz.slick.Graphics;
import com.github.mathiewz.slick.state.GameState;
import com.github.mathiewz.slick.state.StateBasedGame;

/**
 * A transition that will combine two states into one effect. The first state is
 * the one we're transitioning from. The second state is specified in the constructor.
 *
 * By default one state will simply be rendered over the other. Subclass this transition
 * overriding the preRenderFirstState and preRenderSecondState to setup the rendering
 * for each state (alpha or what ever). Note that it's also possible to use the
 * postRenderSecondState method to clean up your OpenGL setup.
 *
 * So these methods are called like so:
 *
 * preRenderFirstState()
 * = the first state is rendered
 * preRenderSecondState()
 * = the second state is rendered
 * postRenderSecondState()
 *
 * @author kevin
 */
public abstract class CrossStateTransition implements Transition {
    /** The second state to cross with */
    private final GameState secondState;
    
    /**
     * Create a cross state transitions
     *
     * @param secondState
     *            The secondary state with combining with the
     *            source state.
     */
    public CrossStateTransition(GameState secondState) {
        this.secondState = secondState;
    }
    
    /**
     * @see com.github.mathiewz.slick.state.transition.Transition#postRender(com.github.mathiewz.slick.state.StateBasedGame, com.github.mathiewz.slick.GameContainer, com.github.mathiewz.slick.Graphics)
     */
    @Override
    public void postRender(StateBasedGame game, GameContainer container, Graphics g) {
        preRenderSecondState(game, container, g);
        secondState.render(container, game, g);
        postRenderSecondState(game, container, g);
    }
    
    /**
     * @see com.github.mathiewz.slick.state.transition.Transition#preRender(com.github.mathiewz.slick.state.StateBasedGame, com.github.mathiewz.slick.GameContainer, com.github.mathiewz.slick.Graphics)
     */
    @Override
    public void preRender(StateBasedGame game, GameContainer container, Graphics g) {
        preRenderFirstState(game, container, g);
    }
    
    /**
     * @see com.github.mathiewz.slick.state.transition.Transition#update(com.github.mathiewz.slick.state.StateBasedGame, com.github.mathiewz.slick.GameContainer, int)
     */
    @Override
    public void update(StateBasedGame game, GameContainer container, int delta) {
    }
    
    /**
     * Notification that the transition is about to render the first state is the cross
     * transition.
     *
     * @param game
     *            The game being rendered
     * @param container
     *            The container holding the game
     * @param g
     *            The graphic context used to render
     */
    public void preRenderFirstState(StateBasedGame game, GameContainer container, Graphics g) {
    }
    
    /**
     * Notification that the transition is about to render the second state is the cross
     * transition.
     *
     * @param game
     *            The game being rendered
     * @param container
     *            The container holding the game
     * @param g
     *            The graphic context used to render
     */
    public void preRenderSecondState(StateBasedGame game, GameContainer container, Graphics g) {
    }
    
    /**
     * Notification that the transition is has just rendered the second state is the cross
     * transition.
     *
     * @param game
     *            The game being rendered
     * @param container
     *            The container holding the game
     * @param g
     *            The graphic context used to render
     */
    public void postRenderSecondState(StateBasedGame game, GameContainer container, Graphics g) {
    }
    
}
