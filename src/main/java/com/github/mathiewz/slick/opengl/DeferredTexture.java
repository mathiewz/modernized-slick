package com.github.mathiewz.slick.opengl;

import java.io.IOException;
import java.io.InputStream;

import com.github.mathiewz.slick.SlickException;
import com.github.mathiewz.slick.loading.DeferredResource;
import com.github.mathiewz.slick.loading.LoadingList;

/**
 * A texture proxy that can be used to load a texture at a later date while still
 * allowing elements to reference it
 *
 * @author kevin
 */
public class DeferredTexture extends TextureImpl implements DeferredResource {
    /** The stream to read the texture from */
    private final InputStream in;
    /** The name of the resource to load */
    private final String resourceName;
    /** True if the image should be flipped */
    private final boolean flipped;
    /** The filter to apply to the texture */
    private final int filter;
    /** The texture we're proxying for */
    private TextureImpl target;
    /** The color to be transparent */
    private final int[] trans;
    
    /**
     * Create a new deferred texture
     *
     * @param in
     *            The input stream from which to read the texture
     * @param resourceName
     *            The name to give the resource
     * @param flipped
     *            True if the image should be flipped
     * @param filter
     *            The filter to apply
     * @param trans
     *            The colour to defined as transparent
     */
    public DeferredTexture(InputStream in, String resourceName, boolean flipped, int filter, int[] trans) {
        this.in = in;
        this.resourceName = resourceName;
        this.flipped = flipped;
        this.filter = filter;
        this.trans = trans;
        
        LoadingList.get().add(this);
    }
    
    /**
     * @see com.github.mathiewz.slick.loading.DeferredResource#load()
     */
    @Override
    public void load() throws IOException {
        boolean before = InternalTextureLoader.get().isDeferredLoading();
        InternalTextureLoader.get().setDeferredLoading(false);
        target = InternalTextureLoader.get().getTexture(in, resourceName, flipped, filter, trans);
        InternalTextureLoader.get().setDeferredLoading(before);
    }
    
    /**
     * Check if the target has been obtained already
     */
    private void checkTarget() {
        if (target == null) {
            try {
                load();
                LoadingList.get().remove(this);
                return;
            } catch (IOException e) {
                throw new SlickException("Attempt to use deferred texture before loading and resource not found: " + resourceName);
            }
        }
    }
    
    /**
     * @see com.github.mathiewz.slick.opengl.TextureImpl#bind()
     */
    @Override
    public void bind() {
        checkTarget();
        
        target.bind();
    }
    
    /**
     * @see com.github.mathiewz.slick.opengl.TextureImpl#getHeight()
     */
    @Override
    public float getHeight() {
        checkTarget();
        
        return target.getHeight();
    }
    
    /**
     * @see com.github.mathiewz.slick.opengl.TextureImpl#getImageHeight()
     */
    @Override
    public int getImageHeight() {
        checkTarget();
        return target.getImageHeight();
    }
    
    /**
     * @see com.github.mathiewz.slick.opengl.TextureImpl#getImageWidth()
     */
    @Override
    public int getImageWidth() {
        checkTarget();
        return target.getImageWidth();
    }
    
    /**
     * @see com.github.mathiewz.slick.opengl.TextureImpl#getTextureHeight()
     */
    @Override
    public int getTextureHeight() {
        checkTarget();
        return target.getTextureHeight();
    }
    
    /**
     * @see com.github.mathiewz.slick.opengl.TextureImpl#getTextureID()
     */
    @Override
    public int getTextureID() {
        checkTarget();
        return target.getTextureID();
    }
    
    /**
     * @see com.github.mathiewz.slick.opengl.TextureImpl#getTextureRef()
     */
    @Override
    public String getTextureRef() {
        checkTarget();
        return target.getTextureRef();
    }
    
    /**
     * @see com.github.mathiewz.slick.opengl.TextureImpl#getTextureWidth()
     */
    @Override
    public int getTextureWidth() {
        checkTarget();
        return target.getTextureWidth();
    }
    
    /**
     * @see com.github.mathiewz.slick.opengl.TextureImpl#getWidth()
     */
    @Override
    public float getWidth() {
        checkTarget();
        return target.getWidth();
    }
    
    /**
     * @see com.github.mathiewz.slick.opengl.TextureImpl#release()
     */
    @Override
    public void release() {
        checkTarget();
        target.release();
    }
    
    /**
     * @see com.github.mathiewz.slick.opengl.TextureImpl#setAlpha(boolean)
     */
    @Override
    public void setAlpha(boolean alpha) {
        checkTarget();
        target.setAlpha(alpha);
    }
    
    /**
     * @see com.github.mathiewz.slick.opengl.TextureImpl#setHeight(int)
     */
    @Override
    public void setHeight(int height) {
        checkTarget();
        target.setHeight(height);
    }
    
    /**
     * @see com.github.mathiewz.slick.opengl.TextureImpl#setTextureHeight(int)
     */
    @Override
    public void setTextureHeight(int texHeight) {
        checkTarget();
        target.setTextureHeight(texHeight);
    }
    
    /**
     * @see com.github.mathiewz.slick.opengl.TextureImpl#setTextureID(int)
     */
    @Override
    public void setTextureID(int textureID) {
        checkTarget();
        target.setTextureID(textureID);
    }
    
    /**
     * @see com.github.mathiewz.slick.opengl.TextureImpl#setTextureWidth(int)
     */
    @Override
    public void setTextureWidth(int texWidth) {
        checkTarget();
        target.setTextureWidth(texWidth);
    }
    
    /**
     * @see com.github.mathiewz.slick.opengl.TextureImpl#setWidth(int)
     */
    @Override
    public void setWidth(int width) {
        checkTarget();
        target.setWidth(width);
    }
    
    /**
     * @see com.github.mathiewz.slick.opengl.TextureImpl#getTextureData()
     */
    @Override
    public byte[] getTextureData() {
        checkTarget();
        return target.getTextureData();
    }
    
    /**
     * @see com.github.mathiewz.slick.loading.DeferredResource#getDescription()
     */
    @Override
    public String getDescription() {
        return resourceName;
    }
    
    /**
     * @see com.github.mathiewz.slick.opengl.Texture#hasAlpha()
     */
    @Override
    public boolean hasAlpha() {
        checkTarget();
        return target.hasAlpha();
    }
    
    /**
     * @see com.github.mathiewz.slick.opengl.Texture#setTextureFilter(int)
     */
    @Override
    public void setTextureFilter(int textureFilter) {
        checkTarget();
        target.setTextureFilter(textureFilter);
    }
}
