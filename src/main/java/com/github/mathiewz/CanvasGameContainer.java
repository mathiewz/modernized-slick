package com.github.mathiewz;

import java.awt.Canvas;

import javax.swing.SwingUtilities;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;

import com.github.mathiewz.util.Log;

/**
 * A game container that displays the game on an AWT Canvas.
 *
 * @author kevin
 */
public class CanvasGameContainer extends Canvas {
    /** The actual container implementation */
    protected Container container;
    /** The game being held in this container */
    protected Game game;

    /**
     * Create a new panel
     *
     * @param game
     *            The game being held
     */
    public CanvasGameContainer(Game game) {
        this(game, false);
    }

    /**
     * Create a new panel
     *
     * @param game
     *            The game being held
     * @param shared
     *            True if shared GL context should be enabled. This allows multiple panels
     *            to share textures and other GL resources.
     */
    public CanvasGameContainer(Game game, boolean shared) {
        super();

        this.game = game;
        setIgnoreRepaint(true);
        requestFocus();
        setSize(500, 500);

        container = new Container(game, shared);
        container.setForceExit(false);
    }

    /**
     * Start the game container rendering
     *
     */
    public void start() {
        SwingUtilities.invokeLater(() -> {
            try {
                Input.disableControllers();

                try {
                    Display.setParent(CanvasGameContainer.this);
                } catch (LWJGLException e1) {
                    throw new SlickException("Failed to setParent of canvas", e1);
                }

                container.setup();
                scheduleUpdate();
            } catch (SlickException e2) {
                e2.printStackTrace();
                System.exit(0);
            }
        });
    }

    /**
     * Schedule an update on the EDT
     */
    private void scheduleUpdate() {
        if (!isVisible()) {
            return;
        }

        SwingUtilities.invokeLater(() -> {
            try {
                container.gameLoop();
            } catch (SlickException e) {
                e.printStackTrace();
            }
            container.checkDimensions();
            scheduleUpdate();
        });
    }

    /**
     * A game container to provide the canvas context
     *
     * @author kevin
     */
    private class Container extends AppGameContainer {
        /**
         * Create a new container wrapped round the game
         *
         * @param game
         *            The game to be held in this container
         * @param shared
         *            True if shared GL context should be enabled. This allows multiple panels
         *            to share textures and other GL resources.
         */
        public Container(Game game, boolean shared) {
            super(game, CanvasGameContainer.this.getWidth(), CanvasGameContainer.this.getHeight(), false);

            width = CanvasGameContainer.this.getWidth();
            height = CanvasGameContainer.this.getHeight();

            if (shared) {
                enableSharedContext();
            }
        }

        /**
         * Updated the FPS counter
         */
        @Override
        protected void updateFPS() {
            super.updateFPS();
        }

        /**
         * @see com.github.mathiewz.GameContainer#running()
         */
        @Override
        protected boolean running() {
            return super.running() && isDisplayable();
        }

        /**
         * @see com.github.mathiewz.GameContainer#getHeight()
         */
        @Override
        public int getHeight() {
            return CanvasGameContainer.this.getHeight();
        }

        /**
         * @see com.github.mathiewz.GameContainer#getWidth()
         */
        @Override
        public int getWidth() {
            return CanvasGameContainer.this.getWidth();
        }

        /**
         * Check the dimensions of the canvas match the display
         */
        public void checkDimensions() {
            if (width != CanvasGameContainer.this.getWidth() || height != CanvasGameContainer.this.getHeight()) {

                try {
                    setDisplayMode(CanvasGameContainer.this.getWidth(), CanvasGameContainer.this.getHeight(), false);
                } catch (SlickException e) {
                    Log.error(e);
                }
            }
        }
    }
}
