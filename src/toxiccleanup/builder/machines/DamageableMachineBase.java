package toxiccleanup.builder.machines;

import toxiccleanup.builder.Damage;
import toxiccleanup.builder.GameState;
import toxiccleanup.builder.entities.GameEntity;
import toxiccleanup.engine.EngineState;
import toxiccleanup.engine.game.Positionable;

/**
 * An abstract base class for machines that can be damaged by weather phenomena.
 *
 * <p>This class provides shared logic for querying the weather system for incoming
 * {@link Damage} and delegating state management to a {@link DamageHandler}. Subclasses
 * inherit {@link #checkAndApplyWeatherDamage(EngineState, GameState)} to reduce
 * duplication of this pattern across all damageable machine types.
 *
 * <p>Class invariant: {@link #damageHandler} is always non-null after construction.
 */
public class DamageableMachineBase extends GameEntity implements Damageable{
    /** Manages the damaged/undamaged state for this machine. */
    protected final DamageHandler damageHandler;

    /**
     * Constructs a DamageableMachine at the given position with a fresh
     * {@link DamageHandler} in the undamaged state.
     *
     * @param position the initial position of this machine.
     * @requires position != null
     * @ensures !isDamaged() && getX() == position.getX()
     *  && getY() == position.getY()
     */
    public DamageableMachineBase (Positionable position) {
        super(position);
        this.damageHandler = new DamageHandler();
    }

    /**
     * check the weather system for any  at this machine's current tile.
     * if it gets damaged, it is forwarded to the internal DamageHandler.
     * subclass should call this at the start of their implementation.
     *
     * @param state the current engine state, used to obtain tile dimensions.
     * @param game  the current game state, used to query the weather system.
     * @requires state != null && game != null
     * @ensures game.getWeather().getDamage(state.getDimensions(), getPosition()) != null
     *          ==> isDamaged()
     */
    protected void checkAndApplyWeatherDamage(EngineState state, GameState game) {
        final Damage dmg = game.getWeather().getDamage(state.getDimensions(), this.getPosition());
        if (dmg != null) {
            this.damageHandler.setDamage(dmg);
        }
    }

    /**
     * Returns whether this machine is currently in its damaged state.
     *
     * @ensures \result == true <==> setDamage() has been called
     *          since the last call to repairDamage()
     * @return {@code true} if this machine has been damaged and not yet repaired.
     */
    @Override
    public boolean isDamaged() {
        return damageHandler.isDamaged();
    }

    /**
     * Applies the given {@link Damage} to this machine, transitioning it to the damaged state.
     *
     * @param dmg the damage to apply.
     * @requires dmg != null
     * @ensures isDamaged()
     */
    @Override
    public void setDamage(Damage dmg) {
        damageHandler.setDamage(dmg);
    }

    /**
     * Repairs this machine, returning it to the undamaged state.
     *
     * @ensures !isDamaged()
     */
    @Override
    public void repairDamage() {
        damageHandler.repairDamage();
    }
}
