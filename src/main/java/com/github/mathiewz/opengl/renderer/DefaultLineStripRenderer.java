package com.github.mathiewz.opengl.renderer;

/**
 * The default version of the renderer relies of GL calls to do everything.
 * Unfortunately this is driver dependent and often implemented inconsistantly
 *
 * @author kevin
 */
public class DefaultLineStripRenderer implements LineStripRenderer {
    /** The access to OpenGL */
    private final SGL GL = Renderer.get();

    /**
     * @see com.github.mathiewz.opengl.renderer.LineStripRenderer#end()
     */
    @Override
    public void end() {
        GL.glEnd();
    }

    /**
     * @see com.github.mathiewz.opengl.renderer.LineStripRenderer#setAntiAlias(boolean)
     */
    @Override
    public void setAntiAlias(boolean antialias) {
        if (antialias) {
            GL.glEnable(SGL.GL_LINE_SMOOTH);
        } else {
            GL.glDisable(SGL.GL_LINE_SMOOTH);
        }
    }

    /**
     * @see com.github.mathiewz.opengl.renderer.LineStripRenderer#setWidth(float)
     */
    @Override
    public void setWidth(float width) {
        GL.glLineWidth(width);
    }

    /**
     * @see com.github.mathiewz.opengl.renderer.LineStripRenderer#start()
     */
    @Override
    public void start() {
        GL.glBegin(SGL.GL_LINE_STRIP);
    }

    /**
     * @see com.github.mathiewz.opengl.renderer.LineStripRenderer#vertex(float, float)
     */
    @Override
    public void vertex(float x, float y) {
        GL.glVertex2f(x, y);
    }

    /**
     * @see com.github.mathiewz.opengl.renderer.LineStripRenderer#color(float, float, float, float)
     */
    @Override
    public void color(float r, float g, float b, float a) {
        GL.glColor4f(r, g, b, a);
    }

    /**
     * @see com.github.mathiewz.opengl.renderer.LineStripRenderer#setLineCaps(boolean)
     */
    @Override
    public void setLineCaps(boolean caps) {
    }

    /**
     * @see com.github.mathiewz.opengl.renderer.LineStripRenderer#applyGLLineFixes()
     */
    @Override
    public boolean applyGLLineFixes() {
        return true;
    }

}
