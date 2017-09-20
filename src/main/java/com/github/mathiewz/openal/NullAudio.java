package com.github.mathiewz.openal;

/**
 * A null implementation used to provide an object reference when sound
 * has failed.
 *
 * @author kevin
 */
public class NullAudio implements Audio {
    /**
     * @see com.github.mathiewz.openal.Audio#getBufferID()
     */
    @Override
    public int getBufferID() {
        return 0;
    }

    /**
     * @see com.github.mathiewz.openal.Audio#getPosition()
     */
    @Override
    public float getPosition() {
        return 0;
    }

    /**
     * @see com.github.mathiewz.openal.Audio#isPlaying()
     */
    @Override
    public boolean isPlaying() {
        return false;
    }

    /**
     * @see com.github.mathiewz.openal.Audio#playAsMusic(float, float, boolean)
     */
    @Override
    public int playAsMusic(float pitch, float gain, boolean loop) {
        return 0;
    }

    /**
     * @see com.github.mathiewz.openal.Audio#playAsSoundEffect(float, float, boolean)
     */
    @Override
    public int playAsSoundEffect(float pitch, float gain, boolean loop) {
        return 0;
    }

    /**
     * @see com.github.mathiewz.openal.Audio#playAsSoundEffect(float, float, boolean, float, float, float)
     */
    @Override
    public int playAsSoundEffect(float pitch, float gain, boolean loop, float x, float y, float z) {
        return 0;
    }

    /**
     * @see com.github.mathiewz.openal.Audio#setPosition(float)
     */
    @Override
    public boolean setPosition(float position) {
        return false;
    }

    /**
     * @see com.github.mathiewz.openal.Audio#stop()
     */
    @Override
    public void stop() {
    }

}
