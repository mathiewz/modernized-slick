package com.github.mathiewz.state.transition;

import java.util.ArrayList;

import com.github.mathiewz.GameContainer;
import com.github.mathiewz.Graphics;
import com.github.mathiewz.state.GameState;
import com.github.mathiewz.state.StateBasedGame;

/**
 * A transition thats built of a set of other transitions which are chained
 * together to build the overall effect.
 *
 * @author kevin
 */
public class CombinedTransition implements Transition {
    /** The list of transitions to be combined */
    private final ArrayList<Transition> transitions = new ArrayList<>();

    /**
     * Create an empty transition
     */
    public CombinedTransition() {
    }

    /**
     * Add a transition to the list that will be combined to form
     * the final transition
     *
     * @param t
     *            The transition to add
     */
    public void addTransition(Transition t) {
        transitions.add(t);
    }

    /**
     * @see com.github.mathiewz.state.transition.Transition#isComplete()
     */
    @Override
    public boolean isComplete() {
        for (int i = 0; i < transitions.size(); i++) {
            if (!transitions.get(i).isComplete()) {
                return false;
            }
        }

        return true;
    }

    /**
     * @see com.github.mathiewz.state.transition.Transition#postRender(com.github.mathiewz.state.StateBasedGame, com.github.mathiewz.GameContainer, com.github.mathiewz.Graphics)
     */
    @Override
    public void postRender(StateBasedGame game, GameContainer container, Graphics g) {
        for (int i = transitions.size() - 1; i >= 0; i--) {
            transitions.get(i).postRender(game, container, g);
        }
    }

    /**
     * @see com.github.mathiewz.state.transition.Transition#preRender(com.github.mathiewz.state.StateBasedGame, com.github.mathiewz.GameContainer, com.github.mathiewz.Graphics)
     */
    @Override
    public void preRender(StateBasedGame game, GameContainer container, Graphics g) {
        for (int i = 0; i < transitions.size(); i++) {
            transitions.get(i).postRender(game, container, g);
        }
    }

    /**
     * @see com.github.mathiewz.state.transition.Transition#update(com.github.mathiewz.state.StateBasedGame, com.github.mathiewz.GameContainer, int)
     */
    @Override
    public void update(StateBasedGame game, GameContainer container, int delta) {
        for (int i = 0; i < transitions.size(); i++) {
            Transition t = transitions.get(i);

            if (!t.isComplete()) {
                t.update(game, container, delta);
            }
        }
    }

    @Override
    public void init(GameState firstState, GameState secondState) {
        for (int i = transitions.size() - 1; i >= 0; i--) {
            transitions.get(i).init(firstState, secondState);
        }
    }
}
