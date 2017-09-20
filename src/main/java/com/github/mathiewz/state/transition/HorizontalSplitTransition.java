package com.github.mathiewz.state.transition;

import com.github.mathiewz.Color;
import com.github.mathiewz.GameContainer;
import com.github.mathiewz.Graphics;
import com.github.mathiewz.opengl.renderer.Renderer;
import com.github.mathiewz.opengl.renderer.SGL;
import com.github.mathiewz.state.GameState;
import com.github.mathiewz.state.StateBasedGame;

/**
 * Horitzonal split transition that causes the previous state to split horizontally
 * revealing the new state underneath.
 *
 * This state is an enter transition.
 *
 * @author kevin
 */
public class HorizontalSplitTransition implements Transition {
    /** The renderer to use for all GL operations */
    protected static SGL GL = Renderer.get();

    /** The previous game state */
    private GameState prev;
    /** The current offset */
    private float offset;
    /** True if the transition is finished */
    private boolean finish;
    /** The background to draw underneath the previous state (null for none) */
    private Color background;

    /**
     * Create a new transition
     */
    public HorizontalSplitTransition() {

    }

    /**
     * Create a new transition
     *
     * @param background
     *            The background colour to draw under the previous state
     */
    public HorizontalSplitTransition(Color background) {
        this.background = background;
    }

    /**
     * @see com.github.mathiewz.state.transition.Transition#init(com.github.mathiewz.state.GameState, com.github.mathiewz.state.GameState)
     */
    @Override
    public void init(GameState firstState, GameState secondState) {
        prev = secondState;
    }

    /**
     * @see com.github.mathiewz.state.transition.Transition#isComplete()
     */
    @Override
    public boolean isComplete() {
        return finish;
    }

    /**
     * @see com.github.mathiewz.state.transition.Transition#postRender(com.github.mathiewz.state.StateBasedGame, com.github.mathiewz.GameContainer, com.github.mathiewz.Graphics)
     */
    @Override
    public void postRender(StateBasedGame game, GameContainer container, Graphics g) {
        g.translate(-offset, 0);
        g.setClip((int) -offset, 0, container.getWidth() / 2, container.getHeight());
        if (background != null) {
            Color c = g.getColor();
            g.setColor(background);
            g.fillRect(0, 0, container.getWidth(), container.getHeight());
            g.setColor(c);
        }
        GL.glPushMatrix();
        prev.render(container, game, g);
        GL.glPopMatrix();
        g.clearClip();

        g.translate(offset * 2, 0);
        g.setClip((int) (container.getWidth() / 2 + offset), 0, container.getWidth() / 2, container.getHeight());
        if (background != null) {
            Color c = g.getColor();
            g.setColor(background);
            g.fillRect(0, 0, container.getWidth(), container.getHeight());
            g.setColor(c);
        }
        GL.glPushMatrix();
        prev.render(container, game, g);
        GL.glPopMatrix();
        g.clearClip();
        g.translate(-offset, 0);
    }

    /**
     * @see com.github.mathiewz.state.transition.Transition#preRender(com.github.mathiewz.state.StateBasedGame, com.github.mathiewz.GameContainer, com.github.mathiewz.Graphics)
     */
    @Override
    public void preRender(StateBasedGame game, GameContainer container, Graphics g) {
    }

    /**
     * @see com.github.mathiewz.state.transition.Transition#update(com.github.mathiewz.state.StateBasedGame, com.github.mathiewz.GameContainer, int)
     */
    @Override
    public void update(StateBasedGame game, GameContainer container, int delta) {
        offset += delta * 1f;
        if (offset > container.getWidth() / 2) {
            finish = true;
        }
    }
}
