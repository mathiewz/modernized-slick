package com.github.mathiewz.geom;

import com.github.mathiewz.Image;
import com.github.mathiewz.ShapeFill;
import com.github.mathiewz.opengl.Texture;
import com.github.mathiewz.opengl.TextureImpl;
import com.github.mathiewz.opengl.renderer.LineStripRenderer;
import com.github.mathiewz.opengl.renderer.Renderer;
import com.github.mathiewz.opengl.renderer.SGL;

/**
 * @author Mark Bernard
 *
 *         Use this class to render shpaes directly to OpenGL. Allows you to bypass the Graphics class.
 */
public final class ShapeRenderer {
    /** The renderer to use for all GL operations */
    private static final SGL GL = Renderer.get();
    /** The renderer to use line strips */
    private static final LineStripRenderer LSR = Renderer.getLineStripRenderer();
    
    private ShapeRenderer() {
        // to avoid instantiation
    }
    
    /**
     * Draw the outline of the given shape. Only the vertices are set.
     * The colour has to be set independently of this method.
     *
     * @param shape
     *            The shape to draw.
     */
    public static final void draw(Shape shape) {
        Texture t = TextureImpl.getLastBind();
        TextureImpl.bindNone();
        
        Float[] points = shape.getPoints();
        
        LSR.start();
        for (int i = 0; i < points.length; i += 2) {
            LSR.vertex(points[i], points[i + 1]);
        }
        
        if (shape.closed()) {
            LSR.vertex(points[0], points[1]);
        }
        
        LSR.end();
        
        if (t == null) {
            TextureImpl.bindNone();
        } else {
            t.bind();
        }
    }
    
    /**
     * Draw the outline of the given shape. Only the vertices are set.
     * The colour has to be set independently of this method.
     *
     * @param shape
     *            The shape to draw.
     * @param fill
     *            The fill to apply
     */
    public static final void draw(Shape shape, ShapeFill fill) {
        Float[] points = shape.getPoints();
        
        Texture t = TextureImpl.getLastBind();
        TextureImpl.bindNone();
        
        GL.glBegin(SGL.GL_LINE_STRIP);
        for (int i = 0; i < points.length; i += 2) {
            fill.colorAt(shape, points[i], points[i + 1]).bind();
            Vector2f offset = fill.getOffsetAt(shape, points[i], points[i + 1]);
            GL.glVertex2f(points[i] + offset.getX(), points[i + 1] + offset.getY());
        }
        
        if (shape.closed()) {
            fill.colorAt(shape, points[0], points[1]).bind();
            Vector2f offset = fill.getOffsetAt(shape, points[0], points[1]);
            GL.glVertex2f(points[0] + offset.getX(), points[1] + offset.getY());
        }
        GL.glEnd();
        
        if (t == null) {
            TextureImpl.bindNone();
        } else {
            t.bind();
        }
    }
    
    /**
     * Check there are enough points to fill
     *
     * @param shape
     *            THe shape we're drawing
     * @return True if the fill is valid
     */
    public static boolean validFill(Shape shape) {
        if (shape.getTriangles() == null) {
            return false;
        }
        return shape.getTriangles().getTriangleCount() != 0;
    }
    
    /**
     * Draw the the given shape filled in. Only the vertices are set.
     * The colour has to be set independently of this method.
     *
     * @param shape
     *            The shape to fill.
     */
    public static final void fill(Shape shape) {
        if (!validFill(shape)) {
            return;
        }
        
        Texture t = TextureImpl.getLastBind();
        TextureImpl.bindNone();
        
        fill(shape, (shape1, x, y) -> null);
        
        if (t == null) {
            TextureImpl.bindNone();
        } else {
            t.bind();
        }
    }
    
    /**
     * Draw the the given shape filled in. Only the vertices are set.
     * The colour has to be set independently of this method.
     *
     * @param shape
     *            The shape to fill.
     * @param callback
     *            The callback that will be invoked for each shape point
     */
    private static final void fill(Shape shape, PointCallback callback) {
        Triangulator tris = shape.getTriangles();
        
        GL.glBegin(SGL.GL_TRIANGLES);
        for (int i = 0; i < tris.getTriangleCount(); i++) {
            for (int p = 0; p < 3; p++) {
                float[] pt = tris.getTrianglePoint(i, p);
                float[] np = callback.preRenderPoint(shape, pt[0], pt[1]);
                
                if (np == null) {
                    GL.glVertex2f(pt[0], pt[1]);
                } else {
                    GL.glVertex2f(np[0], np[1]);
                }
            }
        }
        GL.glEnd();
    }
    
    /**
     * Draw the the given shape filled in with a texture. Only the vertices are set.
     * The colour has to be set independently of this method.
     *
     * @param shape
     *            The shape to texture.
     * @param image
     *            The image to tile across the shape
     */
    public static final void texture(Shape shape, Image image) {
        texture(shape, image, 0.01f, 0.01f);
    }
    
    /**
     * Draw the the given shape filled in with a texture. Only the vertices are set.
     * The colour has to be set independently of this method. This method is required to
     * fit the texture once across the shape.
     *
     * @param shape
     *            The shape to texture.
     * @param image
     *            The image to tile across the shape
     */
    public static final void textureFit(Shape shape, Image image) {
        textureFit(shape, image, 1f, 1f);
    }
    
    /**
     * Draw the the given shape filled in with a texture. Only the vertices are set.
     * The colour has to be set independently of this method.
     *
     * @param shape
     *            The shape to texture.
     * @param image
     *            The image to tile across the shape
     * @param scaleX
     *            The scale to apply on the x axis for texturing
     * @param scaleY
     *            The scale to apply on the y axis for texturing
     */
    public static final void texture(Shape shape, final Image image, final float scaleX, final float scaleY) {
        if (!validFill(shape)) {
            return;
        }
        
        final Texture t = TextureImpl.getLastBind();
        image.getTexture().bind();
        
        fill(shape, (shape1, x, y) -> {
            float tx = x * scaleX;
            float ty = y * scaleY;
            
            tx = image.getTextureOffsetX() + image.getTextureWidth() * tx;
            ty = image.getTextureOffsetY() + image.getTextureHeight() * ty;
            
            GL.glTexCoord2f(tx, ty);
            return null;
        });
        
        shape.getPoints();
        
        if (t == null) {
            TextureImpl.bindNone();
        } else {
            t.bind();
        }
    }
    
    /**
     * Draw the the given shape filled in with a texture. Only the vertices are set.
     * The colour has to be set independently of this method. This method is required to
     * fit the texture scaleX times across the shape and scaleY times down the shape.
     *
     * @param shape
     *            The shape to texture.
     * @param image
     *            The image to tile across the shape
     * @param scaleX
     *            The scale to apply on the x axis for texturing
     * @param scaleY
     *            The scale to apply on the y axis for texturing
     */
    public static final void textureFit(Shape shape, final Image image, final float scaleX, final float scaleY) {
        if (!validFill(shape)) {
            return;
        }
        
        shape.getPoints();
        
        Texture t = TextureImpl.getLastBind();
        image.getTexture().bind();
        
        shape.getX();
        shape.getY();
        shape.getMaxX();
        shape.getMaxY();
        
        fill(shape, (shape1, x, y) -> {
            x -= shape1.getMinX();
            y -= shape1.getMinY();
            
            x /= shape1.getMaxX() - shape1.getMinX();
            y /= shape1.getMaxY() - shape1.getMinY();
            
            float tx = x * scaleX;
            float ty = y * scaleY;
            
            tx = image.getTextureOffsetX() + image.getTextureWidth() * tx;
            ty = image.getTextureOffsetY() + image.getTextureHeight() * ty;
            
            GL.glTexCoord2f(tx, ty);
            return null;
        });
        
        if (t == null) {
            TextureImpl.bindNone();
        } else {
            t.bind();
        }
    }
    
    /**
     * Draw the the given shape filled in. Only the vertices are set.
     * The colour has to be set independently of this method.
     *
     * @param shape
     *            The shape to fill.
     * @param fill
     *            The fill to apply
     */
    public static final void fill(final Shape shape, final ShapeFill fill) {
        if (!validFill(shape)) {
            return;
        }
        
        Texture t = TextureImpl.getLastBind();
        TextureImpl.bindNone();
        
        fill(shape, (shape1, x, y) -> {
            fill.colorAt(shape1, x, y).bind();
            Vector2f offset = fill.getOffsetAt(shape1, x, y);
            
            return new float[] { offset.getX() + x, offset.getY() + y };
        });
        
        if (t == null) {
            TextureImpl.bindNone();
        } else {
            t.bind();
        }
    }
    
    /**
     * Draw the the given shape filled in with a texture. Only the vertices are set.
     * The colour has to be set independently of this method.
     *
     * @param shape
     *            The shape to texture.
     * @param image
     *            The image to tile across the shape
     * @param scaleX
     *            The scale to apply on the x axis for texturing
     * @param scaleY
     *            The scale to apply on the y axis for texturing
     * @param fill
     *            The fill to apply
     */
    public static final void texture(final Shape shape, final Image image, final float scaleX, final float scaleY, final ShapeFill fill) {
        if (!validFill(shape)) {
            return;
        }
        
        Texture t = TextureImpl.getLastBind();
        image.getTexture().bind();
        
        final float[] center = shape.getCenter();
        fill(shape, (shape1, x, y) -> {
            fill.colorAt(shape1, x - center[0], y - center[1]).bind();
            Vector2f offset = fill.getOffsetAt(shape1, x, y);
            
            x += offset.getX();
            y += offset.getY();
            
            float tx = x * scaleX;
            float ty = y * scaleY;
            
            tx = image.getTextureOffsetX() + image.getTextureWidth() * tx;
            ty = image.getTextureOffsetY() + image.getTextureHeight() * ty;
            
            GL.glTexCoord2f(tx, ty);
            
            return new float[] { offset.getX() + x, offset.getY() + y };
        });
        
        if (t == null) {
            TextureImpl.bindNone();
        } else {
            t.bind();
        }
    }
    
    /**
     * Draw the the given shape filled in with a texture. Only the vertices are set.
     * The colour has to be set independently of this method.
     *
     * @param shape
     *            The shape to texture.
     * @param image
     *            The image to tile across the shape
     * @param gen
     *            The texture coordinate generator to create coordiantes for the shape
     */
    public static final void texture(final Shape shape, Image image, final TexCoordGenerator gen) {
        Texture t = TextureImpl.getLastBind();
        
        image.getTexture().bind();
        
        fill(shape, (shape1, x, y) -> {
            Vector2f tex = gen.getCoordFor(x, y);
            GL.glTexCoord2f(tex.getX(), tex.getY());
            
            return new float[] { x, y };
        });
        
        if (t == null) {
            TextureImpl.bindNone();
        } else {
            t.bind();
        }
    }
    
    /**
     * Description of some feature that will be applied to each point render
     *
     * @author kevin
     */
    private static interface PointCallback {
        /**
         * Apply feature before the call to glVertex
         *
         * @param shape
         *            The shape the point belongs to
         * @param x
         *            The x poisiton the vertex will be at
         * @param y
         *            The y position the vertex will be at
         * @return The new coordinates of null
         */
        float[] preRenderPoint(Shape shape, float x, float y);
    }
}
