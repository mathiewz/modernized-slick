package com.github.mathiewz.slick.state.transition;

import com.github.mathiewz.slick.GameContainer;
import com.github.mathiewz.slick.Graphics;
import com.github.mathiewz.slick.state.GameState;
import com.github.mathiewz.slick.state.StateBasedGame;

import java.util.ArrayList;

/**
 * A transition thats built of a set of other transitions which are chained
 * together to build the overall effect.
 *
 * @author kevin
 */
public class CombinedTransition extends Transition {
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
     * @see com.github.mathiewz.slick.state.transition.Transition#isComplete()
     */
    @Override
    public boolean isComplete() {
        return transitions.stream().allMatch(Transition::isComplete);
    }

    /**
     * @see com.github.mathiewz.slick.state.transition.Transition#postRender(com.github.mathiewz.slick.state.StateBasedGame, com.github.mathiewz.slick.GameContainer, com.github.mathiewz.slick.Graphics)
     */
    @Override
    public void postRender(StateBasedGame game, GameContainer container, Graphics g) {
        for (int i = transitions.size() - 1; i >= 0; i--) {
            transitions.get(i).postRender(game, container, g);
        }
    }

    /**
     * @see com.github.mathiewz.slick.state.transition.Transition#preRender(com.github.mathiewz.slick.state.StateBasedGame, com.github.mathiewz.slick.GameContainer, com.github.mathiewz.slick.Graphics)
     */
    @Override
    public void preRender(StateBasedGame game, GameContainer container, Graphics g) {
        transitions.forEach(transition -> transition.postRender(game, container, g));
    }

    /**
     * @see com.github.mathiewz.slick.state.transition.Transition#update(com.github.mathiewz.slick.state.StateBasedGame, com.github.mathiewz.slick.GameContainer, int)
     */
    @Override
    public void update(StateBasedGame game, GameContainer container, int delta) {
        transitions.stream()
                .filter(t -> !t.isComplete())
                .forEach(t -> t.update(game, container, delta));
    }

    @Override
    public void init(GameState firstState, GameState secondState) {
        for (int i = transitions.size() - 1; i >= 0; i--) {
            transitions.get(i).init(firstState, secondState);
        }
    }
}
