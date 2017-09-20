package com.github.mathiewz.util;

import com.github.mathiewz.AppGameContainer;
import com.github.mathiewz.Game;

/**
 * Utility class to wrap up starting a game in a single line
 *
 * @author kevin
 */
public class Bootstrap {

    private Bootstrap() {
        // To avoid instanciation
    }

    /**
     * Start the game as an application
     *
     * @param game
     *            The game to be started
     * @param width
     *            The width of the window
     * @param height
     *            The height of the window
     * @param fullscreen
     *            True if the window should be fullscreen
     */
    public static void runAsApplication(Game game, int width, int height, boolean fullscreen) {
        try {
            AppGameContainer container = new AppGameContainer(game, width, height, fullscreen);
            container.start();
        } catch (Exception e) {
            Log.error(e);
        }
    }
}
