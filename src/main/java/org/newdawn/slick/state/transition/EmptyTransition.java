package org.newdawn.slick.state.transition;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.GameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 * A transition that has no effect and instantly finishes. Used as a utility for the people
 * not using transitions
 *
 * @author kevin
 */
public class EmptyTransition implements Transition {

    /**
     * @see org.newdawn.slick.state.transition.Transition#isComplete()
     */
    @Override
    public boolean isComplete() {
        return true;
    }

    /**
     * @see org.newdawn.slick.state.transition.Transition#postRender(org.newdawn.slick.state.StateBasedGame, org.newdawn.slick.GameContainer, org.newdawn.slick.Graphics)
     */
    @Override
    public void postRender(StateBasedGame game, GameContainer container, Graphics g) {
        // no op
    }

    /**
     * @see org.newdawn.slick.state.transition.Transition#preRender(org.newdawn.slick.state.StateBasedGame, org.newdawn.slick.GameContainer, org.newdawn.slick.Graphics)
     */
    @Override
    public void preRender(StateBasedGame game, GameContainer container, Graphics g) {
        // no op
    }

    /**
     * @see org.newdawn.slick.state.transition.Transition#update(org.newdawn.slick.state.StateBasedGame, org.newdawn.slick.GameContainer, int)
     */
    @Override
    public void update(StateBasedGame game, GameContainer container, int delta) {
        // no op
    }

    @Override
    public void init(GameState firstState, GameState secondState) {
        // TODO Auto-generated method stub

    }
}
