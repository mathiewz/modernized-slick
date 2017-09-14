package org.newdawn.slick.openal;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.lwjgl.openal.AL10;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.Log;

/**
 *
 * Utitlity class for loading wavefiles.
 *
 * @author Brian Matzon <brian@matzon.dk>
 * @version $Revision: 2286 $
 */
public class AiffData {
    /** actual AIFF data */
    public final ByteBuffer data;
    
    /** format type of data */
    public final int format;
    
    /** sample rate of data */
    public final int samplerate;
    
    /**
     * Creates a new AiffData
     *
     * @param data
     *            actual Aiffdata
     * @param format
     *            format of Aiff data
     * @param samplerate
     *            sample rate of data
     */
    private AiffData(ByteBuffer data, int format, int samplerate) {
        this.data = data;
        this.format = format;
        this.samplerate = samplerate;
    }
    
    /**
     * Disposes the Aiffdata
     */
    public void dispose() {
        data.clear();
    }
    
    /**
     * Creates a AiffData container from the specified url
     *
     * @param path
     *            URL to file
     * @return AiffData containing data, or null if a failure occured
     */
    public static AiffData create(URL path) {
        try {
            return create(AudioSystem.getAudioInputStream(new BufferedInputStream(path.openStream())));
        } catch (Exception e) {
            org.lwjgl.LWJGLUtil.log("Unable to create from: " + path);
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Creates a AiffData container from the specified in the classpath
     *
     * @param path
     *            path to file (relative, and in classpath)
     * @return AiffData containing data, or null if a failure occured
     */
    public static AiffData create(String path) {
        return create(AiffData.class.getClassLoader().getResource(path));
    }
    
    /**
     * Creates a AiffData container from the specified inputstream
     *
     * @param is
     *            InputStream to read from
     * @return AiffData containing data, or null if a failure occured
     */
    public static AiffData create(InputStream is) {
        try {
            return create(AudioSystem.getAudioInputStream(is));
        } catch (Exception e) {
            org.lwjgl.LWJGLUtil.log("Unable to create from inputstream");
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Creates a AiffData container from the specified bytes
     *
     * @param buffer
     *            array of bytes containing the complete Aiff file
     * @return AiffData containing data, or null if a failure occured
     */
    public static AiffData create(byte[] buffer) {
        try {
            return create(AudioSystem.getAudioInputStream(new BufferedInputStream(new ByteArrayInputStream(buffer))));
        } catch (Exception e) {
            Log.error(e);
            return null;
        }
    }
    
    /**
     * Creates a AiffData container from the specified ByetBuffer.
     * If the buffer is backed by an array, it will be used directly,
     * else the contents of the buffer will be copied using get(byte[]).
     *
     * @param buffer
     *            ByteBuffer containing sound file
     * @return AiffData containing data, or null if a failure occured
     */
    public static AiffData create(ByteBuffer buffer) {
        try {
            byte[] bytes = null;
            
            if (buffer.hasArray()) {
                bytes = buffer.array();
            } else {
                bytes = new byte[buffer.capacity()];
                buffer.get(bytes);
            }
            return create(bytes);
        } catch (Exception e) {
            Log.error(e);
            return null;
        }
    }
    
    /**
     * Creates a AiffData container from the specified stream
     *
     * @param ais
     *            AudioInputStream to read from
     * @return AiffData containing data, or null if a failure occured
     */
    public static AiffData create(AudioInputStream ais) {
        // get format of data
        AudioFormat audioformat = ais.getFormat();
        
        // get channels
        int channels = 0;
        if (audioformat.getChannels() == 1) {
            if (audioformat.getSampleSizeInBits() == 8) {
                channels = AL10.AL_FORMAT_MONO8;
            } else if (audioformat.getSampleSizeInBits() == 16) {
                channels = AL10.AL_FORMAT_MONO16;
            } else {
                throw new SlickException("Illegal sample size");
            }
        } else if (audioformat.getChannels() == 2) {
            if (audioformat.getSampleSizeInBits() == 8) {
                channels = AL10.AL_FORMAT_STEREO8;
            } else if (audioformat.getSampleSizeInBits() == 16) {
                channels = AL10.AL_FORMAT_STEREO16;
            } else {
                throw new SlickException("Illegal sample size");
            }
        } else {
            throw new SlickException("Only mono or stereo is supported");
        }
        
        // read data into buffer
        byte[] buf = new byte[audioformat.getChannels() * (int) ais.getFrameLength() * audioformat.getSampleSizeInBits() / 8];
        int read = 0;
        int total = 0;
        try {
            while ((read = ais.read(buf, total, buf.length - total)) != -1 && total < buf.length) {
                total += read;
            }
        } catch (IOException ioe) {
            return null;
        }
        
        // insert data into bytebuffer
        ByteBuffer buffer = convertAudioBytes(audioformat, buf, audioformat.getSampleSizeInBits() == 16);
        
        // create our result
        AiffData aiffdata = new AiffData(buffer, channels, (int) audioformat.getSampleRate());
        
        // close stream
        try {
            ais.close();
        } catch (IOException ioe) {
        }
        
        return aiffdata;
    }
    
    /**
     * Convert the audio bytes into the stream
     *
     * @param format
     *            The audio format being decoded
     * @param audioBytes
     *            The audio byts
     * @param twoBytesData
     *            True if we using double byte data
     * @return The byte bufer of data
     */
    private static ByteBuffer convertAudioBytes(AudioFormat format, byte[] audioBytes, boolean twoBytesData) {
        ByteBuffer dest = ByteBuffer.allocateDirect(audioBytes.length);
        dest.order(ByteOrder.nativeOrder());
        ByteBuffer src = ByteBuffer.wrap(audioBytes);
        src.order(ByteOrder.BIG_ENDIAN);
        if (twoBytesData) {
            ShortBuffer descShort = dest.asShortBuffer();
            ShortBuffer srcShort = src.asShortBuffer();
            while (srcShort.hasRemaining()) {
                descShort.put(srcShort.get());
            }
        } else {
            while (src.hasRemaining()) {
                byte b = src.get();
                if (format.getEncoding() == Encoding.PCM_SIGNED) {
                    b = (byte) (b + 127);
                }
                dest.put(b);
            }
        }
        dest.rewind();
        return dest;
    }
}
