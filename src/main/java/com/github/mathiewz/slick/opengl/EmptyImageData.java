package com.github.mathiewz.slick.opengl;

import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;

/**
 * An image data implementation which represents an empty texture
 *
 * @author kevin
 */
public class EmptyImageData implements ImageData {
    /** The width of the data */
    private final int width;
    /** The height of the data */
    private final int height;

    /**
     * Create an empty image data source
     *
     * @param width
     *            The width of the source
     * @param height
     *            The height of the source
     */
    public EmptyImageData(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * @see com.github.mathiewz.slick.opengl.ImageData#getDepth()
     */
    @Override
    public int getDepth() {
        return 32;
    }

    /**
     * @see com.github.mathiewz.slick.opengl.ImageData#getHeight()
     */
    @Override
    public int getHeight() {
        return height;
    }

    /**
     * @see com.github.mathiewz.slick.opengl.ImageData#getImageBufferData()
     */
    @Override
    public ByteBuffer getImageBufferData() {
        return BufferUtils.createByteBuffer(getTexWidth() * getTexHeight() * 4);
    }

    /**
     * @see com.github.mathiewz.slick.opengl.ImageData#getTexHeight()
     */
    @Override
    public int getTexHeight() {
        return InternalTextureLoader.get2Fold(height);
    }

    /**
     * @see com.github.mathiewz.slick.opengl.ImageData#getTexWidth()
     */
    @Override
    public int getTexWidth() {
        return InternalTextureLoader.get2Fold(width);
    }

    /**
     * @see com.github.mathiewz.slick.opengl.ImageData#getWidth()
     */
    @Override
    public int getWidth() {
        return width;
    }

}
