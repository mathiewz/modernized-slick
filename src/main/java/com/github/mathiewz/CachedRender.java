package com.github.mathiewz;

import org.lwjgl.opengl.GL11;

import com.github.mathiewz.opengl.SlickCallable;
import com.github.mathiewz.opengl.renderer.Renderer;
import com.github.mathiewz.opengl.renderer.SGL;

/**
 * A set of rendering that is cached onto the graphics card and hopefully
 * is quicker to render. Note that there are some things that can't be done
 * in lists and that all dependent operations must be container. For instance,
 * any colour configuration can not be assumed from outside the cache.
 *
 * Note: The destroy method needs to be used to tidy up. This is pretty important
 * in this case since there are limited number of underlying resources.
 *
 * @author kevin
 */
public class CachedRender {
    /** The renderer to use for all GL operations */
    protected static SGL GL = Renderer.get();

    /** The operations to cache */
    private final Runnable runnable;
    /** The display list cached to */
    private int list = -1;

    /**
     * Create a new cached render that will build the specified
     * operations on to a video card resource
     *
     * @param runnable
     *            The operations to cache
     */
    public CachedRender(Runnable runnable) {
        this.runnable = runnable;
        build();
    }

    /**
     * Build the display list
     */
    private void build() {
        if (list == -1) {
            list = GL.glGenLists(1);

            SlickCallable.enterSafeBlock();
            GL.glNewList(list, GL11.GL_COMPILE);
            runnable.run();
            GL.glEndList();
            SlickCallable.leaveSafeBlock();
        } else {
            throw new SlickException("Attempt to build the display list more than once in CachedRender");
        }
    }

    /**
     * Render the cached operations. Note that this doesn't call the operations, but
     * rather calls the cached version
     */
    public void render() {
        if (list == -1) {
            throw new SlickException("Attempt to render cached operations that have been destroyed");
        }

        SlickCallable.enterSafeBlock();
        GL.glCallList(list);
        SlickCallable.leaveSafeBlock();
    }

    /**
     * Destroy this cached render
     */
    public void destroy() {
        GL.glDeleteLists(list, 1);
        list = -1;
    }
}
