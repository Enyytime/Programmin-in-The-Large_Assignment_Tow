package toxiccleanup.builder.weather;

import toxiccleanup.engine.game.Positionable;
import toxiccleanup.builder.Damage;

/**
 * Represents a unit of lightning-specific damage. Unlike standard {@link Damage},
 * {@link LightningDamage} has the type {@value TYPE}, which is used to make
 * lightning rods immune to lightning strikes.
 *
 * <p>Class invariant: {@code getType().equals(TYPE)} is always true.
 */
public class LightningDamage extends Damage {
    private int x = 0;
    private int y = 0;
    public static final String TYPE = "lightning";

    /**
     * Constructs a new {@link LightningDamage} at the given position.
     *
     * @param position the position at which this lightning damage occurs.
     *
     * @requires position != null
     * @ensures getType().equals(TYPE)
     *          && getX() == position.getX()
     *          && getY() == position.getY()
     */
    public LightningDamage(Positionable position) {
        super(position);
        this.setType(TYPE);
    }
}
