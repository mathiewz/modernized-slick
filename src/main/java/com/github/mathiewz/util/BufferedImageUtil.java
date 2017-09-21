package com.github.mathiewz.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.lwjgl.opengl.EXTTextureMirrorClamp;
import org.lwjgl.opengl.GL11;

import com.github.mathiewz.opengl.ImageIOImageData;
import com.github.mathiewz.opengl.InternalTextureLoader;
import com.github.mathiewz.opengl.Texture;
import com.github.mathiewz.opengl.TextureImpl;
import com.github.mathiewz.opengl.renderer.Renderer;

/**
 * This is a utility class that allows you to convert a BufferedImage into a
 * texture.
 *
 * @author James Chambers (Jimmy)
 * @author Jeremy Adams (elias_naur)
 * @author Kevin Glass (kevglass)
 */

public class BufferedImageUtil {

    /**
     * Load a texture
     *
     * @param resourceName
     *            The location of the resource to load
     * @param resourceImage
     *            The BufferedImage we are converting
     * @return The loaded texture
     * @throws IOException
     *             Indicates a failure to access the resource
     */
    public static Texture getTexture(String resourceName, BufferedImage resourceImage) throws IOException {
        return getTexture(resourceName, resourceImage, GL11.GL_LINEAR);
    }

    /**
     * Load a texture
     *
     * @param resourceName
     *            The location of the resource to load
     * @param resourceImage
     *            The BufferedImage we are converting
     * @return The loaded texture
     * @throws IOException
     *             Indicates a failure to access the resource
     */
    public static Texture getTexture(String resourceName, BufferedImage resourceImage, int filter) throws IOException {
        return getTexture(resourceName, resourceImage, GL11.GL_TEXTURE_2D, GL11.GL_RGBA8, filter, filter);
    }

    /**
     * Load a texture into OpenGL from a BufferedImage
     *
     * @param resourceName
     *            The location of the resource to load
     * @param bufferedImage
     *            The BufferedImage we are converting
     * @param target
     *            The GL target to load the texture against
     * @param dstPixelFormat
     *            The pixel format of the screen
     * @param minFilter
     *            The minimising filter
     * @param magFilter
     *            The magnification filter
     * @return The loaded texture
     * @throws IOException
     *             Indicates a failure to access the resource
     */
    public static Texture getTexture(String resourceName, BufferedImage bufferedImage, int target, int dstPixelFormat, int minFilter, int magFilter) throws IOException {
        ImageIOImageData data = new ImageIOImageData();
        int srcPixelFormat = 0;

        // create the texture ID for this texture
        int textureID = InternalTextureLoader.createTextureID();
        TextureImpl texture = new TextureImpl(resourceName, target, textureID);

        // Enable texturing
        Renderer.get().glEnable(GL11.GL_TEXTURE_2D);

        // bind this texture
        Renderer.get().glBindTexture(target, textureID);

        texture.setWidth(bufferedImage.getWidth());
        texture.setHeight(bufferedImage.getHeight());

        if (bufferedImage.getColorModel().hasAlpha()) {
            srcPixelFormat = GL11.GL_RGBA;
        } else {
            srcPixelFormat = GL11.GL_RGB;
        }

        // convert that image into a byte buffer of texture data
        ByteBuffer textureBuffer = data.imageToByteBuffer(bufferedImage, false, false, null);
        texture.setTextureHeight(data.getTexHeight());
        texture.setTextureWidth(data.getTexWidth());
        texture.setAlpha(data.getDepth() == 32);

        if (target == GL11.GL_TEXTURE_2D) {
            Renderer.get().glTexParameteri(target, GL11.GL_TEXTURE_MIN_FILTER, minFilter);
            Renderer.get().glTexParameteri(target, GL11.GL_TEXTURE_MAG_FILTER, magFilter);

            if (Renderer.get().canTextureMirrorClamp()) {
                Renderer.get().glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, EXTTextureMirrorClamp.GL_MIRROR_CLAMP_TO_EDGE_EXT);
                Renderer.get().glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, EXTTextureMirrorClamp.GL_MIRROR_CLAMP_TO_EDGE_EXT);
            } else {
                Renderer.get().glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
                Renderer.get().glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
            }
        }

        Renderer.get().glTexImage2D(target, 0, dstPixelFormat, texture.getTextureWidth(), texture.getTextureHeight(), 0, srcPixelFormat, GL11.GL_UNSIGNED_BYTE, textureBuffer);

        return texture;
    }
}
