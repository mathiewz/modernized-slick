package com.github.mathiewz.slick.opengl;

import java.security.AccessController;
import java.security.PrivilegedAction;

import com.github.mathiewz.slick.util.Log;

/**
 * A static utility to create the appropriate image data for a particular reference.
 *
 * @author kevin
 */
public class ImageDataFactory {
    /**
     * True if we're going to use the native PNG loader - cached so it doesn't have
     * the security check repeatedly
     */
    private static boolean usePngLoader = true;
    /** True if the PNG loader property has been checked */
    private static boolean pngLoaderPropertyChecked = false;

    /** The name of the PNG loader configuration property */
    private static final String PNG_LOADER = "com.github.mathiewz.slick.pngloader";

    /**
     * Check PNG loader property. If set the native PNG loader will
     * not be used.
     */
    private static void checkProperty() {
        if (!pngLoaderPropertyChecked) {
            pngLoaderPropertyChecked = true;
            PrivilegedAction<Void> action = () -> {
                String val = System.getProperty(PNG_LOADER);
                if ("false".equalsIgnoreCase(val)) {
                    usePngLoader = false;
                }

                Log.info("Use Java PNG Loader = " + usePngLoader);
                return null;
            };
            try {
                AccessController.doPrivileged(action);
            } catch (Exception e) {
                Log.error(e);
            }
        }
    }

    /**
     * Create an image data that is appropriate for the reference supplied
     *
     * @param ref
     *            The reference to the image to retrieve
     * @return The image data that can be used to retrieve the data for that resource
     */
    public static LoadableImageData getImageDataFor(String ref) {
        checkProperty();

        String refLowered = ref.toLowerCase();

        if (refLowered.endsWith(".tga")) {
            return new TGAImageData();
        }
        if (refLowered.endsWith(".png")) {
            CompositeImageData data = new CompositeImageData();
            if (usePngLoader) {
                data.add(new PNGImageData());
            }
            data.add(new ImageIOImageData());
            return data;
        }

        return new ImageIOImageData();
    }
}
