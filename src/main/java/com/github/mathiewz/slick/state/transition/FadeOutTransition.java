package com.github.mathiewz.slick.state.transition;

import com.github.mathiewz.slick.Color;
import com.github.mathiewz.slick.GameContainer;
import com.github.mathiewz.slick.Graphics;
import com.github.mathiewz.slick.state.GameState;
import com.github.mathiewz.slick.state.StateBasedGame;

/**
 * A transition to fade out to a given colour
 *
 * @author kevin
 */
public class FadeOutTransition implements Transition {
    /** The color to fade to */
    private final Color color;
    /** The time it takes the fade to happen */
    private final int fadeTime;

    /**
     * Create a new fade out transition
     */
    public FadeOutTransition() {
        this(Color.black, 500);
    }

    /**
     * Create a new fade out transition
     *
     * @param color
     *            The color we're going to fade out to
     */
    public FadeOutTransition(Color color) {
        this(color, 500);
    }

    /**
     * Create a new fade out transition
     *
     * @param color
     *            The color we're going to fade out to
     * @param fadeTime
     *            The time it takes the fade to occur
     */
    public FadeOutTransition(Color color, int fadeTime) {
        this.color = new Color(color);
        this.color.a = 0;
        this.fadeTime = fadeTime;
    }

    /**
     * @see com.github.mathiewz.slick.state.transition.Transition#isComplete()
     */
    @Override
    public boolean isComplete() {
        return color.a >= 1;
    }

    /**
     * @see com.github.mathiewz.slick.state.transition.Transition#postRender(com.github.mathiewz.slick.state.StateBasedGame, com.github.mathiewz.slick.GameContainer, com.github.mathiewz.slick.Graphics)
     */
    @Override
    public void postRender(StateBasedGame game, GameContainer container, Graphics g) {
        Color old = g.getColor();
        g.setColor(color);
        g.fillRect(0, 0, container.getWidth() * 2, container.getHeight() * 2);
        g.setColor(old);
    }

    /**
     * @see com.github.mathiewz.slick.state.transition.Transition#update(com.github.mathiewz.slick.state.StateBasedGame, com.github.mathiewz.slick.GameContainer, int)
     */
    @Override
    public void update(StateBasedGame game, GameContainer container, int delta) {
        color.a += delta * (1.0f / fadeTime);
        if (color.a > 1) {
            color.a = 1;
        }
    }

    /**
     * @see com.github.mathiewz.slick.state.transition.Transition#preRender(com.github.mathiewz.slick.state.StateBasedGame, com.github.mathiewz.slick.GameContainer, com.github.mathiewz.slick.Graphics)
     */
    @Override
    public void preRender(StateBasedGame game, GameContainer container, Graphics g) {
    }

    @Override
    public void init(GameState firstState, GameState secondState) {
        // TODO Auto-generated method stub

    }

}
