package toxiccleanup.builder.weather;

import toxiccleanup.engine.EngineState;
import toxiccleanup.engine.art.sprites.SpriteGroup;
import toxiccleanup.engine.game.Positionable;
import toxiccleanup.engine.renderer.Dimensions;
import toxiccleanup.engine.timing.RepeatingTimer;
import toxiccleanup.engine.timing.TickTimer;
import toxiccleanup.builder.Damage;
import toxiccleanup.builder.GameState;
import toxiccleanup.builder.SpriteGallery;

/**
 * <p> A {@link AcidCloud} is a weather phenomena that will move to the left, over time. </p>
 * <p> It damages any machine that it shares a tile with, changing them to their damaged state </p>
 * <p> Plays an animation loop endlessly using sprites from {@link SpriteGallery#acidcloud}. </p>
 * <p> When it reaches the leftmost edge of the screen, it will mark itself for removal. </p>
 *
 * <p> Rendered using {@link SpriteGallery#acidcloud}. </p>
 *
 * @provided
 */
public class AcidCloud extends CloudAnimateBase implements Damaging {
    public static final int SPAWN_TIME = Cloud.SPAWN_TIME;

    private static final SpriteGroup art = SpriteGallery.acidcloud;
    /**
     * @param position
     */
    public AcidCloud(Positionable position) {
        super(position);
    }

    /**
     * Returns the {@link SpriteGroup} used to render this acid cloud.
     *
     * @ensures \result != null && \result == SpriteGallery.acidcloud
     * @return the acid cloud sprite art.
     */
    @Override
    protected SpriteGroup getArt() {
        return art;
    }

    /**
     * Returns a Damage instance at this cloud's current position.
     *
     * @param dimensions screen and tile dimensions.
     * @param position theposition of the entity querying for damage.
     * @requires dimensions != null && postion != null
     * @ensures \result != null
     *          && \result.getX() == getX()
     *          && \result.getY() == getY()
     * @return a new {@link Damage} at this cloud's current position.
     */
    @Override
    public Damage getDamage(Dimensions dimensions, Positionable position) {
        return new Damage(this.getPosition());
    }

    @Override
    public Damage getDamage() {
        return new Damage(this.getPosition());
    }
}