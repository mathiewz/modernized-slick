package com.github.mathiewz.opengl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.HashMap;

import org.lwjgl.BufferUtils;

import com.github.mathiewz.SlickException;
import com.github.mathiewz.opengl.renderer.Renderer;
import com.github.mathiewz.opengl.renderer.SGL;
import com.github.mathiewz.util.ResourceLoader;

/**
 * A texture loaded based on many old versions that will load image data from a file
 * and produce OpenGL textures.
 *
 * @see ImageData
 *
 * @author kevin
 */
public class InternalTextureLoader {
    /** The renderer to use for all GL operations */
    private static final SGL GL = Renderer.get();
    /** The standard texture loaded used everywhere */
    private static final InternalTextureLoader loader = new InternalTextureLoader();
    /** The table of textures that have been loaded in this loader */
    private final HashMap<String, TextureImpl> texturesLinear = new HashMap<>();
    /** The table of textures that have been loaded in this loader */
    private final HashMap<String, TextureImpl> texturesNearest = new HashMap<>();
    /** The destination pixel format */
    private int dstPixelFormat = SGL.GL_RGBA8;
    /** True if we're using deferred loading */
    private boolean deferred;
    
    /**
     * Create a new texture loader based on the game panel
     */
    private InternalTextureLoader() {
    }
    
    /**
     * Get the single instance of this texture loader
     *
     * @return The single instance of the texture loader
     */
    public static InternalTextureLoader get() {
        return loader;
    }
    
    /**
     * True if we should only record the request to load in the intention
     * of loading the texture later
     *
     * @param deferred
     *            True if the we should load a token
     */
    public void setDeferredLoading(boolean deferred) {
        this.deferred = deferred;
    }
    
    /**
     * Check if we're using deferred loading
     *
     * @return True if we're loading deferred textures
     */
    public boolean isDeferredLoading() {
        return deferred;
    }
    
    /**
     * Remove a particular named image from the cache
     *
     * @param name
     *            The name of the image to be cleared
     */
    public void clear(String name) {
        texturesLinear.remove(name);
        texturesNearest.remove(name);
    }
    
    /**
     * Clear out the cached textures
     */
    public void clear() {
        texturesLinear.clear();
        texturesNearest.clear();
    }
    
    /**
     * Tell the loader to produce 16 bit textures
     */
    public void set16BitMode() {
        dstPixelFormat = SGL.GL_RGBA16;
    }
    
    /**
     * Create a new texture ID
     *
     * @return A new texture ID
     */
    public static int createTextureID() {
        IntBuffer tmp = createIntBuffer(1);
        GL.glGenTextures(tmp);
        return tmp.get(0);
    }
    
    /**
     * Get a texture from a specific file
     *
     * @param source
     *            The file to load the texture from
     * @param flipped
     *            True if we should flip the texture on the y axis while loading
     * @param filter
     *            The filter to use
     * @return The texture loaded
     * @throws IOException
     *             Indicates a failure to load the image
     */
    public Texture getTexture(File source, boolean flipped, int filter) throws IOException {
        String resourceName = source.getAbsolutePath();
        InputStream in = new FileInputStream(source);
        
        return getTexture(in, resourceName, flipped, filter, null);
    }
    
    /**
     * Get a texture from a specific file
     *
     * @param source
     *            The file to load the texture from
     * @param flipped
     *            True if we should flip the texture on the y axis while loading
     * @param filter
     *            The filter to use
     * @param transparent
     *            The colour to interpret as transparent or null if none
     * @return The texture loaded
     * @throws IOException
     *             Indicates a failure to load the image
     */
    public Texture getTexture(File source, boolean flipped, int filter, int[] transparent) throws IOException {
        String resourceName = source.getAbsolutePath();
        InputStream in = new FileInputStream(source);
        
        return getTexture(in, resourceName, flipped, filter, transparent);
    }
    
    /**
     * Get a texture from a resource location
     *
     * @param resourceName
     *            The location to load the texture from
     * @param flipped
     *            True if we should flip the texture on the y axis while loading
     * @param filter
     *            The filter to use when scaling the texture
     * @return The texture loaded
     * @throws IOException
     *             Indicates a failure to load the image
     */
    public Texture getTexture(String resourceName, boolean flipped, int filter) throws IOException {
        InputStream in = ResourceLoader.getResourceAsStream(resourceName);
        
        return getTexture(in, resourceName, flipped, filter, null);
    }
    
    /**
     * Get a texture from a resource location
     *
     * @param resourceName
     *            The location to load the texture from
     * @param flipped
     *            True if we should flip the texture on the y axis while loading
     * @param filter
     *            The filter to use when scaling the texture
     * @param transparent
     *            The colour to interpret as transparent or null if none
     * @return The texture loaded
     * @throws IOException
     *             Indicates a failure to load the image
     */
    public Texture getTexture(String resourceName, boolean flipped, int filter, int[] transparent) throws IOException {
        InputStream in = ResourceLoader.getResourceAsStream(resourceName);
        
        return getTexture(in, resourceName, flipped, filter, transparent);
    }
    
    /**
     * Get a texture from a image file
     *
     * @param in
     *            The stream from which we can load the image
     * @param resourceName
     *            The name to give this image in the internal cache
     * @param flipped
     *            True if we should flip the image on the y-axis while loading
     * @param filter
     *            The filter to use when scaling the texture
     * @return The texture loaded
     * @throws IOException
     *             Indicates a failure to load the image
     */
    public Texture getTexture(InputStream in, String resourceName, boolean flipped, int filter) throws IOException {
        return getTexture(in, resourceName, flipped, filter, null);
    }
    
    /**
     * Get a texture from a image file
     *
     * @param in
     *            The stream from which we can load the image
     * @param resourceName
     *            The name to give this image in the internal cache
     * @param flipped
     *            True if we should flip the image on the y-axis while loading
     * @param filter
     *            The filter to use when scaling the texture
     * @param transparent
     *            The colour to interpret as transparent or null if none
     * @return The texture loaded
     * @throws IOException
     *             Indicates a failure to load the image
     */
    public TextureImpl getTexture(InputStream in, String resourceName, boolean flipped, int filter, int[] transparent) throws IOException {
        if (deferred) {
            return new DeferredTexture(in, resourceName, flipped, filter, transparent);
        }
        
        HashMap<String, TextureImpl> hash = filter == SGL.GL_NEAREST ? texturesNearest : texturesLinear;
        
        String resName = resourceName;
        if (transparent != null) {
            resName += ":" + transparent[0] + ":" + transparent[1] + ":" + transparent[2];
        }
        resName += ":" + flipped;
        
        TextureImpl tex = hash.get(resName);
        if (tex != null) {
            return tex;
        } else {
            hash.remove(resName);
        }
        
        // horrible test until I can find something more suitable
        try {
            GL.glGetError();
        } catch (NullPointerException e) {
            throw new SlickException("Image based resources must be loaded as part of init() or the game loop. They cannot be loaded before initialisation.", e);
        }
        
        TextureImpl texture = getTexture(in, resourceName, SGL.GL_TEXTURE_2D, filter, filter, flipped, transparent);
        
        texture.setCacheName(resName);
        hash.put(resName, texture);
        
        return texture;
    }
    
    /**
     * Get a texture from a image file
     *
     * @param in
     *            The stream from which we can load the image
     * @param resourceName
     *            The name to give this image in the internal cache
     * @param flipped
     *            True if we should flip the image on the y-axis while loading
     * @param target
     *            The texture target we're loading this texture into
     * @param minFilter
     *            The scaling down filter
     * @param magFilter
     *            The scaling up filter
     * @param transparent
     *            The colour to interpret as transparent or null if none
     * @return The texture loaded
     * @throws IOException
     *             Indicates a failure to load the image
     */
    private TextureImpl getTexture(InputStream in, String resourceName, int target, int magFilter, int minFilter, boolean flipped, int[] transparent) throws IOException {
        // create the texture ID for this texture
        ByteBuffer textureBuffer;
        
        LoadableImageData imageData = ImageDataFactory.getImageDataFor(resourceName);
        textureBuffer = imageData.loadImage(new BufferedInputStream(in), flipped, transparent);
        
        int textureID = createTextureID();
        TextureImpl texture = new TextureImpl(resourceName, target, textureID);
        // bind this texture
        GL.glBindTexture(target, textureID);
        
        int width;
        int height;
        int texWidth;
        int texHeight;
        
        boolean hasAlpha;
        
        width = imageData.getWidth();
        height = imageData.getHeight();
        hasAlpha = imageData.getDepth() == 32;
        
        texture.setTextureWidth(imageData.getTexWidth());
        texture.setTextureHeight(imageData.getTexHeight());
        
        texWidth = texture.getTextureWidth();
        texHeight = texture.getTextureHeight();
        
        IntBuffer temp = BufferUtils.createIntBuffer(16);
        GL.glGetInteger(SGL.GL_MAX_TEXTURE_SIZE, temp);
        int max = temp.get(0);
        if (texWidth > max || texHeight > max) {
            throw new IOException("Attempt to allocate a texture to big for the current hardware");
        }
        
        int srcPixelFormat = hasAlpha ? SGL.GL_RGBA : SGL.GL_RGB;
        
        texture.setWidth(width);
        texture.setHeight(height);
        texture.setAlpha(hasAlpha);
        texture.setTextureData(srcPixelFormat, minFilter, magFilter, textureBuffer);
        
        GL.glTexParameteri(target, SGL.GL_TEXTURE_MIN_FILTER, minFilter);
        GL.glTexParameteri(target, SGL.GL_TEXTURE_MAG_FILTER, magFilter);
        
        // produce a texture from the byte buffer
        GL.glTexImage2D(target, 0, dstPixelFormat, get2Fold(width), get2Fold(height), 0, srcPixelFormat, SGL.GL_UNSIGNED_BYTE, textureBuffer);
        
        return texture;
    }
    
    /**
     * Create an empty texture
     *
     * @param width
     *            The width of the new texture
     * @param height
     *            The height of the new texture
     * @return The created empty texture
     * @throws IOException
     *             Indicates a failure to create the texture on the graphics hardware
     */
    public Texture createTexture(final int width, final int height) throws IOException {
        return createTexture(width, height, SGL.GL_NEAREST);
    }
    
    /**
     * Create an empty texture
     *
     * @param width
     *            The width of the new texture
     * @param height
     *            The height of the new texture
     * @return The created empty texture
     * @throws IOException
     *             Indicates a failure to create the texture on the graphics hardware
     */
    public Texture createTexture(final int width, final int height, final int filter) throws IOException {
        ImageData ds = new EmptyImageData(width, height);
        
        return getTexture(ds, filter);
    }
    
    /**
     * Get a texture from a image file
     *
     * @param dataSource
     *            The image data to generate the texture from
     * @param filter
     *            The filter to use when scaling the texture
     * @return The texture created
     * @throws IOException
     *             Indicates the texture is too big for the hardware
     */
    public Texture getTexture(ImageData dataSource, int filter) throws IOException {
        int target = SGL.GL_TEXTURE_2D;
        
        ByteBuffer textureBuffer;
        textureBuffer = dataSource.getImageBufferData();
        
        // create the texture ID for this texture
        int textureID = createTextureID();
        TextureImpl texture = new TextureImpl("generated:" + dataSource, target, textureID);
        
        int minFilter = filter;
        int magFilter = filter;
        
        // bind this texture
        GL.glBindTexture(target, textureID);
        
        int width;
        int height;
        int texWidth;
        int texHeight;
        
        boolean hasAlpha;
        
        width = dataSource.getWidth();
        height = dataSource.getHeight();
        hasAlpha = dataSource.getDepth() == 32;
        
        texture.setTextureWidth(dataSource.getTexWidth());
        texture.setTextureHeight(dataSource.getTexHeight());
        
        texWidth = texture.getTextureWidth();
        texHeight = texture.getTextureHeight();
        
        int srcPixelFormat = hasAlpha ? SGL.GL_RGBA : SGL.GL_RGB;
        
        texture.setWidth(width);
        texture.setHeight(height);
        texture.setAlpha(hasAlpha);
        
        IntBuffer temp = BufferUtils.createIntBuffer(16);
        GL.glGetInteger(SGL.GL_MAX_TEXTURE_SIZE, temp);
        int max = temp.get(0);
        if (texWidth > max || texHeight > max) {
            throw new IOException("Attempt to allocate a texture to big for the current hardware");
        }
        
        texture.setTextureData(srcPixelFormat, minFilter, magFilter, textureBuffer);
        
        GL.glTexParameteri(target, SGL.GL_TEXTURE_MIN_FILTER, minFilter);
        GL.glTexParameteri(target, SGL.GL_TEXTURE_MAG_FILTER, magFilter);
        
        // produce a texture from the byte buffer
        GL.glTexImage2D(target, 0, dstPixelFormat, get2Fold(width), get2Fold(height), 0, srcPixelFormat, SGL.GL_UNSIGNED_BYTE, textureBuffer);
        
        return texture;
    }
    
    /**
     * Get the closest greater power of 2 to the fold number
     *
     * @param fold
     *            The target number
     * @return The power of 2
     */
    public static int get2Fold(int fold) {
        int ret = 2;
        while (ret < fold) {
            ret *= 2;
        }
        return ret;
    }
    
    /**
     * Creates an integer buffer to hold specified ints
     * - strictly a utility method
     *
     * @param size
     *            how many int to contain
     * @return created IntBuffer
     */
    public static IntBuffer createIntBuffer(int size) {
        ByteBuffer temp = ByteBuffer.allocateDirect(4 * size);
        temp.order(ByteOrder.nativeOrder());
        
        return temp.asIntBuffer();
    }
    
    /**
     * Reload all the textures loaded in this loader
     */
    public void reload() {
        texturesLinear.values().forEach(TextureImpl::reload);
        texturesNearest.values().forEach(TextureImpl::reload);
    }
    
    /**
     * Reload a given texture blob
     *
     * @param texture
     *            The texture being reloaded
     * @param srcPixelFormat
     *            The source pixel format
     * @param minFilter
     *            The minification filter
     * @param magFilter
     *            The magnification filter
     * @param textureBuffer
     *            The pixel data
     * @return The ID of the newly created texture
     */
    public int reload(TextureImpl texture, int srcPixelFormat, int minFilter, int magFilter, ByteBuffer textureBuffer) {
        int target = SGL.GL_TEXTURE_2D;
        int textureID = createTextureID();
        GL.glBindTexture(target, textureID);
        
        GL.glTexParameteri(target, SGL.GL_TEXTURE_MIN_FILTER, minFilter);
        GL.glTexParameteri(target, SGL.GL_TEXTURE_MAG_FILTER, magFilter);
        
        // produce a texture from the byte buffer
        GL.glTexImage2D(target, 0, dstPixelFormat, texture.getTextureWidth(), texture.getTextureHeight(), 0, srcPixelFormat, SGL.GL_UNSIGNED_BYTE, textureBuffer);
        
        return textureID;
    }
}