package com.github.mathiewz.slick.state.transition;

import com.github.mathiewz.slick.Color;
import com.github.mathiewz.slick.GameContainer;
import com.github.mathiewz.slick.Graphics;
import com.github.mathiewz.slick.opengl.renderer.Renderer;
import com.github.mathiewz.slick.opengl.renderer.SGL;
import com.github.mathiewz.slick.state.GameState;
import com.github.mathiewz.slick.state.StateBasedGame;

/**
 * Vertical split transition that causes the previous state to split vertically
 * revealing the new state underneath.
 *
 * This state is an enter transition.
 *
 * @author kevin
 */
public class VerticalSplitTransition extends Transition {
    /** The renderer to use for all GL operations */
    protected static SGL GL = Renderer.get();

    /** The previous game state */
    private GameState prev;
    /** The current offset */
    private float offset;
    /** True if the transition is finished */
    private boolean finish;


    /**
     * Create a new transition
     */
    public VerticalSplitTransition() {

    }

    /**
     * Create a new transition
     *
     * @param background
     *            The background colour to draw under the previous state
     */
    public VerticalSplitTransition(Color background) {
        this.background = background;
    }

    /**
     * @see com.github.mathiewz.slick.state.transition.Transition#init(com.github.mathiewz.slick.state.GameState, com.github.mathiewz.slick.state.GameState)
     */
    @Override
    public void init(GameState firstState, GameState secondState) {
        prev = secondState;
    }

    /**
     * @see com.github.mathiewz.slick.state.transition.Transition#isComplete()
     */
    @Override
    public boolean isComplete() {
        return finish;
    }

    /**
     * @see com.github.mathiewz.slick.state.transition.Transition#postRender(com.github.mathiewz.slick.state.StateBasedGame, com.github.mathiewz.slick.GameContainer, com.github.mathiewz.slick.Graphics)
     */
    @Override
    public void postRender(StateBasedGame game, GameContainer container, Graphics g) {
        g.translate(0, -offset);
        g.setClip(0, (int) -offset, container.getWidth(), container.getHeight() / 2);
        fillBackground(container, g);
        GL.glPushMatrix();
        prev.render(container, game, g);
        GL.glPopMatrix();
        g.clearClip();
        g.resetTransform();

        g.translate(0, offset);
        g.setClip(0, (int) (container.getHeight() / 2 + offset), container.getWidth(), container.getHeight() / 2);
        fillBackground(container, g);
        GL.glPushMatrix();
        prev.render(container, game, g);
        GL.glPopMatrix();
        g.clearClip();
        g.translate(0, -offset);
    }



    /**
     * @see com.github.mathiewz.slick.state.transition.Transition#preRender(com.github.mathiewz.slick.state.StateBasedGame, com.github.mathiewz.slick.GameContainer, com.github.mathiewz.slick.Graphics)
     */
    @Override
    public void preRender(StateBasedGame game, GameContainer container, Graphics g) {
    }

    /**
     * @see com.github.mathiewz.slick.state.transition.Transition#update(com.github.mathiewz.slick.state.StateBasedGame, com.github.mathiewz.slick.GameContainer, int)
     */
    @Override
    public void update(StateBasedGame game, GameContainer container, int delta) {
        offset += delta * 1f;
        if (offset > container.getHeight() / 2) {
            finish = true;
        }
    }

}
