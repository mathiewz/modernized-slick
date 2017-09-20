package com.github.mathiewz.opengl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;

/**
 * The PNG imge data source that is pure java reading PNGs
 *
 * @author Matthias Mann (original code)
 */
public class PNGImageData implements LoadableImageData {
    /** The width of the data loaded */
    private int width;
    /** The height of the data loaded */
    private int height;
    /** The texture height */
    private int texHeight;
    /** The texture width */
    private int texWidth;
    /** The bit depth of the image */
    private int bitDepth;
    /** The scratch buffer storing the image data */
    private ByteBuffer scratch;

    /**
     * @see com.github.mathiewz.opengl.ImageData#getDepth()
     */
    @Override
    public int getDepth() {
        return bitDepth;
    }

    /**
     * @see com.github.mathiewz.opengl.ImageData#getImageBufferData()
     */
    @Override
    public ByteBuffer getImageBufferData() {
        return scratch;
    }

    /**
     * @see com.github.mathiewz.opengl.ImageData#getTexHeight()
     */
    @Override
    public int getTexHeight() {
        return texHeight;
    }

    /**
     * @see com.github.mathiewz.opengl.ImageData#getTexWidth()
     */
    @Override
    public int getTexWidth() {
        return texWidth;
    }

    /**
     * @see com.github.mathiewz.opengl.LoadableImageData#loadImage(java.io.InputStream)
     */
    @Override
    public ByteBuffer loadImage(InputStream fis) throws IOException {
        return loadImage(fis, false, null);
    }

    /**
     * @see com.github.mathiewz.opengl.LoadableImageData#loadImage(java.io.InputStream, boolean, int[])
     */
    @Override
    public ByteBuffer loadImage(InputStream fis, boolean flipped, int[] transparent) throws IOException {
        return loadImage(fis, flipped, false, transparent);
    }

    /**
     * @see com.github.mathiewz.opengl.LoadableImageData#loadImage(java.io.InputStream, boolean, boolean, int[])
     */
    @Override
    public ByteBuffer loadImage(InputStream fis, boolean flipped, boolean forceAlpha, int[] transparent) throws IOException {
        if (transparent != null) {
            forceAlpha = true;
            throw new IOException("Transparent color not support in custom PNG Decoder");
        }

        PNGDecoder decoder = new PNGDecoder(fis);

        if (!decoder.isRGB()) {
            throw new IOException("Only RGB formatted images are supported by the PNGLoader");
        }

        width = decoder.getWidth();
        height = decoder.getHeight();
        texWidth = get2Fold(width);
        texHeight = get2Fold(height);

        int perPixel = decoder.hasAlpha() ? 4 : 3;
        bitDepth = decoder.hasAlpha() ? 32 : 24;

        // Get a pointer to the image memory
        scratch = BufferUtils.createByteBuffer(texWidth * texHeight * perPixel);
        decoder.decode(scratch, texWidth * perPixel, perPixel == 4 ? PNGDecoder.RGBA : PNGDecoder.RGB);

        if (height < texHeight - 1) {
            int topOffset = (texHeight - 1) * texWidth * perPixel;
            int bottomOffset = (height - 1) * texWidth * perPixel;
            for (int x = 0; x < texWidth; x++) {
                for (int i = 0; i < perPixel; i++) {
                    scratch.put(topOffset + x + i, scratch.get(x + i));
                    scratch.put(bottomOffset + texWidth * perPixel + x + i, scratch.get(bottomOffset + x + i));
                }
            }
        }
        if (width < texWidth - 1) {
            for (int y = 0; y < texHeight; y++) {
                for (int i = 0; i < perPixel; i++) {
                    scratch.put((y + 1) * texWidth * perPixel - perPixel + i, scratch.get(y * texWidth * perPixel + i));
                    scratch.put(y * texWidth * perPixel + width * perPixel + i, scratch.get(y * texWidth * perPixel + (width - 1) * perPixel + i));
                }
            }
        }

        if (!decoder.hasAlpha() && forceAlpha) {
            ByteBuffer temp = BufferUtils.createByteBuffer(texWidth * texHeight * 4);
            for (int x = 0; x < texWidth; x++) {
                for (int y = 0; y < texHeight; y++) {
                    int srcOffset = y * 3 + x * texHeight * 3;
                    int dstOffset = y * 4 + x * texHeight * 4;

                    temp.put(dstOffset, scratch.get(srcOffset));
                    temp.put(dstOffset + 1, scratch.get(srcOffset + 1));
                    temp.put(dstOffset + 2, scratch.get(srcOffset + 2));
                    if (x < getHeight() && y < getWidth()) {
                        temp.put(dstOffset + 3, (byte) 255);
                    } else {
                        temp.put(dstOffset + 3, (byte) 0);
                    }
                }
            }

            bitDepth = 32;
            scratch = temp;
        }
        scratch.position(0);

        return scratch;
    }

    /**
     * Get the closest greater power of 2 to the fold number
     *
     * @param fold
     *            The target number
     * @return The power of 2
     */
    private int get2Fold(int fold) {
        int ret = 2;
        while (ret < fold) {
            ret *= 2;
        }
        return ret;
    }

    /**
     * @see com.github.mathiewz.opengl.LoadableImageData#configureEdging(boolean)
     */
    @Override
    public void configureEdging(boolean edging) {
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }
}
