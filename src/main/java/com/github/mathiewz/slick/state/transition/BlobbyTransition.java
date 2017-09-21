package com.github.mathiewz.slick.state.transition;

import java.util.ArrayList;

import com.github.mathiewz.slick.Color;
import com.github.mathiewz.slick.GameContainer;
import com.github.mathiewz.slick.Graphics;
import com.github.mathiewz.slick.opengl.renderer.Renderer;
import com.github.mathiewz.slick.opengl.renderer.SGL;
import com.github.mathiewz.slick.state.GameState;
import com.github.mathiewz.slick.state.StateBasedGame;
import com.github.mathiewz.slick.util.MaskUtil;

/**
 * A transition that causes the previous state to rotate and scale down into
 * the new state.
 *
 * This is an enter transition
 *
 * @author kevin
 */
public class BlobbyTransition implements Transition {
    /** The renderer to use for all GL operations */
    protected static SGL GL = Renderer.get();

    /** The previous state */
    private GameState prev;
    /** True if the state has finished */
    private boolean finish;
    /** The background applied under the previous state if any */
    private Color background;
    /** ArrayList blobs */
    private final ArrayList<Blob> blobs = new ArrayList<>();
    /** The time it will run for */
    private int timer = 1000;
    /** The number of blobs to create */
    private final int blobCount = 10;

    /**
     * Create a new transition
     */
    public BlobbyTransition() {

    }

    /**
     * Create a new transition
     *
     * @param background
     *            The background colour to draw under the previous state
     */
    public BlobbyTransition(Color background) {
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
        MaskUtil.resetMask();
    }

    /**
     * @see com.github.mathiewz.slick.state.transition.Transition#preRender(com.github.mathiewz.slick.state.StateBasedGame, com.github.mathiewz.slick.GameContainer, com.github.mathiewz.slick.Graphics)
     */
    @Override
    public void preRender(StateBasedGame game, GameContainer container, Graphics g) {
        prev.render(container, game, g);

        MaskUtil.defineMask();
        for (int i = 0; i < blobs.size(); i++) {
            blobs.get(i).render(g);
        }
        MaskUtil.finishDefineMask();

        MaskUtil.drawOnMask();
        if (background != null) {
            Color c = g.getColor();
            g.setColor(background);
            g.fillRect(0, 0, container.getWidth(), container.getHeight());
            g.setColor(c);
        }
    }

    /**
     * @see com.github.mathiewz.slick.state.transition.Transition#update(com.github.mathiewz.slick.state.StateBasedGame, com.github.mathiewz.slick.GameContainer, int)
     */
    @Override
    public void update(StateBasedGame game, GameContainer container, int delta) {
        if (blobs.size() == 0) {
            for (int i = 0; i < blobCount; i++) {
                blobs.add(new Blob(container));
            }
        }

        for (int i = 0; i < blobs.size(); i++) {
            blobs.get(i).update(delta);
        }

        timer -= delta;
        if (timer < 0) {
            finish = true;
        }
    }

    /**
     * A blob to show the new state
     *
     * @author kevin
     */
    private class Blob {
        /** The x coordinate of the centre of this blob */
        private final float x;
        /** The y coordinate of the centre of this blob */
        private final float y;
        /** The speed at which this blob grows */
        private final float growSpeed;
        /** The radius of this blob */
        private float rad;

        /**
         * Create a new blob
         *
         * @param container
         *            The container for dimensions
         */
        public Blob(GameContainer container) {
            x = (float) (Math.random() * container.getWidth());
            y = (float) (Math.random() * container.getWidth());
            growSpeed = (float) (1f + Math.random() * 1f);
        }

        /**
         * Update the blob
         *
         * @param delta
         *            The change in time in milliseconds
         */
        public void update(int delta) {
            rad += growSpeed * delta * 0.4f;
        }

        /**
         * Render the blob - i.e. the mask
         *
         * @param g
         *            The grphics context on which the mask should be drawn
         */
        public void render(Graphics g) {
            g.fillOval(x - rad, y - rad, rad * 2, rad * 2);
        }
    }
}
