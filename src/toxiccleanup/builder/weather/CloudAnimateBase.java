package toxiccleanup.builder.weather;

import toxiccleanup.engine.EngineState;
import toxiccleanup.engine.art.sprites.SpriteGroup;
import toxiccleanup.engine.game.Positionable;
import toxiccleanup.engine.timing.RepeatingTimer;
import toxiccleanup.engine.timing.TickTimer;
import toxiccleanup.builder.GameState;

/**
 * An abstract extension of {@link Cloud} that adds a configurable looping sprite animation.
 *
 * <p>Subclasses provide their own {@link SpriteGroup} via {@link #getArt()}, and this class
 * manages the animation timer, frame counter, and sprite updates automatically on each tick.
 * The animation loops back to frame 1 after reaching the final frame.
 *
 * <p>Class invariant: {@code 1 <= currentArtFrame <= maxFrames} always holds.
 */
public abstract class CloudAnimateBase extends Cloud {

    /** Interval (in ticks) between animation frame advances. */
    private static final int ANIM_INTERVAL = 12;

    /** Timer controlling how frequently the animation frame advances. */
    private final TickTimer animTimer = new RepeatingTimer(ANIM_INTERVAL);

    /** Current animation frame index, starting at 1. */
    private int currentArtFrame = 1;

    /** Total number of animation frames available from {@link #getArt()}. */
    private final int maxFrames;

    /**
     * Constructs an CloudAnimateBase at the given position using the art returned
     * by getArt.
     *
     * @param position the initial position of this cloud.
     * @requires position != null
     * @ensures currentArtFrame == 1 && getSprite() == getArt().getSprite("1")
     */
    public CloudAnimateBase(Positionable position) {
        super(position);
        this.maxFrames = getArt().getSprites().size();
        setSprite(getArt().getSprite(currentArtFrame + ""));
    }

    /**
     * Returns the SpriteGroup used for this cloud's animation.
     * Subclasses must provide their specific art here.
     *
     * @ensures \result != null
     * @return the sprite group for this cloud type.
     */
    protected abstract SpriteGroup getArt();

    /**
     * Advances the animation timer on each tick. When the timer fires, the animation frame
     * advances by 1, looping from the last frame back to frame 1.
     *
     * @param state the current engine state.
     * @param game  the current game state.
     * @requires state != null && game != null
     * @ensures animTimer.isFinished() ==> currentArtFrame == (\old(currentArtFrame) % maxFrames) + 1
     */
    @Override
    public void tick(EngineState state, GameState game) {
        super.tick(state, game);
        animTimer.tick();

        if (animTimer.isFinished()) {
            currentArtFrame += 1;
            if (currentArtFrame > maxFrames) {
                currentArtFrame = 1;
            }
        }
        setSprite(getArt().getSprite(currentArtFrame + ""));
    }
}