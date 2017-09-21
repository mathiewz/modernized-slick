package com.github.mathiewz.slick.particles.effects;

import com.github.mathiewz.slick.Image;
import com.github.mathiewz.slick.particles.Particle;
import com.github.mathiewz.slick.particles.ParticleEmitter;
import com.github.mathiewz.slick.particles.ParticleSystem;

/**
 * A stock effect for fire usin the particle system
 *
 * @author kevin
 */
public class FireEmitter implements ParticleEmitter {
    /** The x coordinate of the center of the fire effect */
    private int x;
    /** The y coordinate of the center of the fire effect */
    private int y;

    /** The particle emission rate */
    private final int interval = 50;
    /** Time til the next particle */
    private int timer;
    /** The size of the initial particles */
    private float size = 40;

    /**
     * Create a default fire effect at 0,0
     */
    public FireEmitter() {
    }

    /**
     * Create a default fire effect at x,y
     *
     * @param x
     *            The x coordinate of the fire effect
     * @param y
     *            The y coordinate of the fire effect
     */
    public FireEmitter(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Create a default fire effect at x,y
     *
     * @param x
     *            The x coordinate of the fire effect
     * @param y
     *            The y coordinate of the fire effect
     * @param size
     *            The size of the particle being pumped out
     */
    public FireEmitter(int x, int y, float size) {
        this.x = x;
        this.y = y;
        this.size = size;
    }

    /**
     * @see com.github.mathiewz.slick.particles.ParticleEmitter#update(com.github.mathiewz.slick.particles.ParticleSystem, int)
     */
    @Override
    public void update(ParticleSystem system, int delta) {
        timer -= delta;
        if (timer <= 0) {
            timer = interval;
            Particle p = system.getNewParticle(this, 1000);
            p.setColor(1, 1, 1, 0.5f);
            p.setPosition(x, y);
            p.setSize(size);
            float vx = (float) (-0.02f + Math.random() * 0.04f);
            float vy = (float) -(Math.random() * 0.15f);
            p.setVelocity(vx, vy, 1.1f);
        }
    }

    /**
     * @see com.github.mathiewz.slick.particles.ParticleEmitter#updateParticle(com.github.mathiewz.slick.particles.Particle, int)
     */
    @Override
    public void updateParticle(Particle particle, int delta) {
        if (particle.getLife() > 600) {
            particle.adjustSize(0.07f * delta);
        } else {
            particle.adjustSize(-0.04f * delta * (size / 40.0f));
        }
        float c = 0.002f * delta;
        particle.adjustColor(0, -c / 2, -c * 2, -c / 4);
    }

    /**
     * @see com.github.mathiewz.slick.particles.ParticleEmitter#isEnabled()
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * @see com.github.mathiewz.slick.particles.ParticleEmitter#setEnabled(boolean)
     */
    @Override
    public void setEnabled(boolean enabled) {
    }

    /**
     * @see com.github.mathiewz.slick.particles.ParticleEmitter#completed()
     */
    @Override
    public boolean completed() {
        return false;
    }

    /**
     * @see com.github.mathiewz.slick.particles.ParticleEmitter#useAdditive()
     */
    @Override
    public boolean useAdditive() {
        return false;
    }

    /**
     * @see com.github.mathiewz.slick.particles.ParticleEmitter#getImage()
     */
    @Override
    public Image getImage() {
        return null;
    }

    /**
     * @see com.github.mathiewz.slick.particles.ParticleEmitter#usePoints(com.github.mathiewz.slick.particles.ParticleSystem)
     */
    @Override
    public boolean usePoints(ParticleSystem system) {
        return false;
    }

    /**
     * @see com.github.mathiewz.slick.particles.ParticleEmitter#isOriented()
     */
    @Override
    public boolean isOriented() {
        return false;
    }

    /**
     * @see com.github.mathiewz.slick.particles.ParticleEmitter#wrapUp()
     */
    @Override
    public void wrapUp() {
    }

    /**
     * @see com.github.mathiewz.slick.particles.ParticleEmitter#resetState()
     */
    @Override
    public void resetState() {
    }
}
