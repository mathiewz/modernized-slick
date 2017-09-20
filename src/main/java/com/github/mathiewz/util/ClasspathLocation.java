package com.github.mathiewz.util;

import java.io.InputStream;
import java.net.URL;

/**
 * A resource location that searches the classpath
 *
 * @author kevin
 */
public class ClasspathLocation implements ResourceLocation {
    /**
     * @see com.github.mathiewz.util.ResourceLocation#getResource(java.lang.String)
     */
    @Override
    public URL getResource(String ref) {
        String cpRef = ref.replace('\\', '/');
        return ResourceLoader.class.getClassLoader().getResource(cpRef);
    }

    /**
     * @see com.github.mathiewz.util.ResourceLocation#getResourceAsStream(java.lang.String)
     */
    @Override
    public InputStream getResourceAsStream(String ref) {
        String cpRef = ref.replace('\\', '/');
        return ResourceLoader.class.getClassLoader().getResourceAsStream(cpRef);
    }

}
