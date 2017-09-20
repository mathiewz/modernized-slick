package com.github.mathiewz.openal;

import java.io.IOException;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;

import com.github.mathiewz.util.Log;

/**
 * A sound implementation wrapped round a player which reads (and potentially) rereads
 * a stream. This supplies streaming audio
 *
 * @author kevin
 * @author Nathan Sweet 
 * @author Rockstar playAsMusic cleanup
 */
public class StreamSound extends AudioImpl {
    /** The player we're going to ask to stream data */
    private final OpenALStreamPlayer player;

    /**
     * Create a new sound wrapped round a stream
     *
     * @param player
     *            The stream player we'll use to access the stream
     */
    public StreamSound(OpenALStreamPlayer player) {
        this.player = player;
    }

    /**
     * @see com.github.mathiewz.openal.AudioImpl#isPlaying()
     */
    @Override
    public boolean isPlaying() {
        return SoundStore.get().isPlaying(player);
    }

    /**
     * @see com.github.mathiewz.openal.AudioImpl#playAsMusic(float, float, boolean)
     */
    @Override
    public int playAsMusic(float pitch, float gain, boolean loop) {
        try {
            cleanUpSource();

            player.setup(pitch);
            player.play(loop);
            SoundStore.get().setStream(player);
        } catch (IOException e) {
            Log.error("Failed to read OGG source: " + player.getSource());
        }

        return SoundStore.get().getSource(0);
    }

    /**
     * Clean up the buffers applied to the sound source
     */
    private void cleanUpSource() {
        SoundStore store = SoundStore.get();

        AL10.alSourceStop(store.getSource(0));
        IntBuffer buffer = BufferUtils.createIntBuffer(1);
        int queued = AL10.alGetSourcei(store.getSource(0), AL10.AL_BUFFERS_QUEUED);

        while (queued > 0) {
            AL10.alSourceUnqueueBuffers(store.getSource(0), buffer);
            queued--;
        }

        AL10.alSourcei(store.getSource(0), AL10.AL_BUFFER, 0);
    }

    /**
     * @see com.github.mathiewz.openal.AudioImpl#playAsSoundEffect(float, float, boolean, float, float, float)
     */
    @Override
    public int playAsSoundEffect(float pitch, float gain, boolean loop, float x, float y, float z) {
        return playAsMusic(pitch, gain, loop);
    }

    /**
     * @see com.github.mathiewz.openal.AudioImpl#playAsSoundEffect(float, float, boolean)
     */
    @Override
    public int playAsSoundEffect(float pitch, float gain, boolean loop) {
        return playAsMusic(pitch, gain, loop);
    }

    /**
     * @see com.github.mathiewz.openal.AudioImpl#stop()
     */
    @Override
    public void stop() {
        SoundStore.get().setStream(null);
    }

    /**
     * @see com.github.mathiewz.openal.AudioImpl#setPosition(float)
     */
    @Override
    public boolean setPosition(float position) {
        return player.setPosition(position);
    }

    /**
     * @see com.github.mathiewz.openal.AudioImpl#getPosition()
     */
    @Override
    public float getPosition() {
        return player.getPosition();
    }
}
