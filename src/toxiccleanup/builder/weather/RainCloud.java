package toxiccleanup.builder.weather;

import toxiccleanup.engine.EngineState;
import toxiccleanup.engine.art.sprites.SpriteGroup;
import toxiccleanup.engine.game.Positionable;
import toxiccleanup.engine.timing.RepeatingTimer;
import toxiccleanup.engine.timing.TickTimer;
import toxiccleanup.builder.GameState;
import toxiccleanup.builder.SpriteGallery;


/**
 * <p> A {@link RainCloud} is a weather phenomena that will move to the left, over time. </p>
 * <p> It obscures {@link toxiccleanup.builder.machines.SolarPanel}s that
 * it is sharing a tile with. </p>
 * <p> Plays an animation loop endlessly using sprites from {@link SpriteGallery#raincloud}. </p>
 * <p> When it reaches the leftmost edge of the screen, it will mark itself for removal. </p>
 *
 * <p> Rendered using {@link SpriteGallery#raincloud}. </p>
 *
 * @provided
 */
public class RainCloud extends CloudAnimateBase  {
    private static final SpriteGroup art = SpriteGallery.raincloud;

    public static final int SPAWN_TIME = Cloud.SPAWN_TIME;

    /**
     * Constructs a RainCloud at the given position.
     *
     * @param position the initial position of this rain cloud.
     * @requires position != null
     * @ensures getX() == position.getX() && getY() == position.getY()
     */
    public RainCloud(Positionable position) {
        super(position);
    }

    /**
     * Returns the SpriteGroup used to render this rain cloud.
     *
     * @ensures \result != null && \result == SpriteGallery.raincloud
     * @return the rain cloud sprite art.
     */
    @Override
    protected SpriteGroup getArt() {
        return art;
    }
}
